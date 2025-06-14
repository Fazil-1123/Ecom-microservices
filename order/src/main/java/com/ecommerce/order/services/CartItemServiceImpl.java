package com.ecommerce.order.services;

import com.ecom.common.exception.ResourceNotFound;
import com.ecommerce.order.clients.ProductServiceClient;
import com.ecommerce.order.clients.UserServiceClient;
import com.ecommerce.order.dtos.CartRequest;
import com.ecommerce.order.dtos.CartResponse;
import com.ecommerce.order.external.dtos.ProductDto;
import com.ecommerce.order.external.dtos.UsersDto;
import com.ecommerce.order.mappers.CartMapper;
import com.ecommerce.order.models.CartItem;
import com.ecommerce.order.repositories.CartItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class CartItemServiceImpl implements CartItemService {

    private static final Logger logger = LoggerFactory.getLogger(CartItemServiceImpl.class);
    private final CartItemRepository cartItemRepository;
    private final CartMapper cartMapper;
    private final ProductServiceClient productServiceClient;
    private final UserServiceClient userServiceClient;

    public CartItemServiceImpl(CartItemRepository cartItemRepository, CartMapper cartMapper, ProductServiceClient productServiceClient, UserServiceClient userServiceClient) {
        this.cartItemRepository = cartItemRepository;
        this.cartMapper = cartMapper;
        this.productServiceClient = productServiceClient;
        this.userServiceClient = userServiceClient;
    }

    @Override
    public Boolean addCartItem(String userId, CartRequest cartRequest) {
        UsersDto cartUser = userServiceClient.getUserById(userId).orElseThrow(()-> new ResourceNotFound("user does not exist"));
        ProductDto productToAdd = productServiceClient.getProductById(cartRequest.getProductId()).orElseThrow(
                ()-> new ResourceNotFound("Product with id: "+cartRequest.getProductId()+" does not exist")
        );
        if (productToAdd.getStockQuantity()<cartRequest.getQuantity()) {
            throw new ResourceNotFound("Product with id: "+cartRequest.getProductId()+" has only "+productToAdd.getStockQuantity()+ " left!!, Please adjust your cart quantity");
        }
        BigDecimal productPrice = productToAdd.getPrice().multiply(BigDecimal.valueOf(cartRequest.getQuantity()));

        Optional<CartItem> cartItem = cartItemRepository.findByUserIdAndProductId(userId, cartRequest.getProductId());
        if (cartItem.isPresent()) {
            CartItem existingItem = cartItem.get();
            existingItem.setQuantity(existingItem.getQuantity() + cartRequest.getQuantity());
            existingItem.setPrice(existingItem.getPrice().add(productPrice));
            cartItemRepository.save(existingItem);

            logger.info("Updated existing cart item for userId={}, productId={}", userId, cartRequest.getProductId());
            return true;
        }

        CartItem newCartItem = new CartItem();
        newCartItem.setProductId(cartRequest.getProductId());
        newCartItem.setUserId(userId);
        newCartItem.setQuantity(cartRequest.getQuantity());
        newCartItem.setPrice(productPrice);
        cartItemRepository.save(newCartItem);

        logger.info("Created new cart item for userId={}, productId={}", userId, cartRequest.getProductId());
        return true;
    }

    @Override
    public boolean removeItem(String userId, Long productId) {
        logger.info("Removing item from cart for userId={}, productId={}", userId, productId);


        Optional<CartItem> cartItem = cartItemRepository.findByUserIdAndProductId(userId, productId);
        if (cartItem.isPresent()) {
            cartItemRepository.delete(cartItem.get());
            logger.info("Removed cart item for userId={}, productId={}", userId, productId);
            return true;
        }

        logger.warn("No cart item found to remove for userId={}, productId={}", userId, productId);
        return false;
    }

    @Override
    public List<CartResponse> getCartItems(String userId) {
        logger.info("Fetching cart items for userId={}", userId);

        List<CartResponse> cartItems = cartItemRepository.findByUserId(userId)
                .stream().map(cartMapper::toDto).toList();

        logger.info("Found {} cart items for userId={}", cartItems.size(), userId);
        return cartItems;
    }
}
