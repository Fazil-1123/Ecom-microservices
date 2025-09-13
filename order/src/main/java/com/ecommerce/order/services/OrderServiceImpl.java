package com.ecommerce.order.services;

import com.ecom.common.exception.ResourceNotFound;
import com.ecommerce.order.clients.ProductServiceClient;
import com.ecommerce.order.external.dtos.ProductDto;
import com.ecommerce.order.mappers.OrderEventMapper;
import com.ecommerce.order.mappers.OrderMapper;
import com.ecommerce.order.models.CartItem;
import com.ecommerce.order.repositories.CartItemRepository;
import com.ecommerce.order.repositories.OrderRepository;
import com.ecommerce.order.models.OrderStatus;
import com.ecommerce.order.dtos.OrderResponse;
import com.ecommerce.order.models.Order;
import com.ecommerce.order.models.OrderItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final ProductServiceClient productServiceClient;
    private final OrderEventMapper orderEventMapper;
    private final StreamBridge streamBridge;

    public OrderServiceImpl(CartItemRepository cartItemRepository,
                            OrderRepository orderRepository, OrderMapper orderMapper, ProductServiceClient productServiceClient, OrderEventMapper orderEventMapper, StreamBridge streamBridge) {
        this.cartItemRepository = cartItemRepository;
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.productServiceClient = productServiceClient;
        this.orderEventMapper = orderEventMapper;
        this.streamBridge = streamBridge;
    }

    @Override
    public OrderResponse placeOrder(String userId) {
        logger.info("Placing order for userId={}", userId);

        BigDecimal totalPrice = BigDecimal.ZERO;

        List<CartItem> cartItems = cartItemRepository.findByUserId(userId).stream().toList();


        if (cartItems.isEmpty()) {
            logger.warn("User with userId={} tried to place order with empty cart", userId);
            throw new ResourceNotFound("User doesn't have any items in cart");
        }

        List<OrderItem> orderItemList = new ArrayList<>();
        Order order = new Order();
        order.setUserId(userId);
        order.setStatus(OrderStatus.ORDERED);

        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(cartItem.getProductId());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getPrice());
            orderItem.setOrder(order);
            orderItemList.add(orderItem);

            totalPrice = totalPrice.add(cartItem.getPrice() != null ? cartItem.getPrice() : BigDecimal.ZERO);
        }

        order.setItems(orderItemList);
        order.setTotalAmount(totalPrice);

        Order placedOrder = orderRepository.save(order);
        logger.info("Sending order event: id={}, items={}", placedOrder.getId(),
                placedOrder.getItems() == null ? 0 : placedOrder.getItems().size());
        //clear cart
        cartItemRepository.deleteAll(cartItems);
        streamBridge.send("placeOrder-out-0",orderMapper.toDto(placedOrder));

        logger.info("Order placed successfully for userId={}, orderId={}, total={}",
                userId, placedOrder.getId(), placedOrder.getTotalAmount());
        for(OrderItem orderItem : order.getItems()){
            ProductDto productToAdd = productServiceClient.getProductById(orderItem.getProductId()).get();
            productToAdd.setStockQuantity(productToAdd.getStockQuantity()-orderItem.getQuantity());
            productServiceClient.updateProductQuantity(productToAdd,orderItem.getProductId());
        }

        return orderMapper.toDto(placedOrder);
    }
}
