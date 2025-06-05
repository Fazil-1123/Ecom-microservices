package com.ecommerce.order.services;

import com.ecom.common.exception.ResourceNotFound;
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
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    public OrderServiceImpl(CartItemRepository cartItemRepository,
                            OrderRepository orderRepository, OrderMapper orderMapper) {
        this.cartItemRepository = cartItemRepository;
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
    }

    @Override
    public OrderResponse placeOrder(String userId) {
        logger.info("Placing order for userId={}", userId);

        BigDecimal totalPrice = BigDecimal.ZERO;

        List<CartItem> cartItems = cartItemRepository.findByUserId(Long.valueOf(userId)).stream().toList();

        if (cartItems.isEmpty()) {
            logger.warn("User with userId={} tried to place order with empty cart", userId);
            throw new ResourceNotFound("User doesn't have any items in cart");
        }

        List<OrderItem> orderItemList = new ArrayList<>();
        Order order = new Order();
        order.setUserId(Long.valueOf(userId));
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
        cartItemRepository.deleteAll(cartItems);

        logger.info("Order placed successfully for userId={}, orderId={}, total={}",
                userId, placedOrder.getId(), placedOrder.getTotalAmount());

        return orderMapper.toDto(placedOrder);
    }
}
