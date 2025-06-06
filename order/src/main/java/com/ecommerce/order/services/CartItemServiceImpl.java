package com.ecommerce.order.services;

import com.ecommerce.order.dtos.CartRequest;
import com.ecommerce.order.dtos.CartResponse;
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

    public CartItemServiceImpl(CartItemRepository cartItemRepository, CartMapper cartMapper) {
        this.cartItemRepository = cartItemRepository;
        this.cartMapper = cartMapper;
    }

    @Override
    public Boolean addCartItem(Long userId, CartRequest cartRequest) {

        Optional<CartItem> cartItem = cartItemRepository.findByUserIdAndProductId(userId, cartRequest.getProductId());
        if (cartItem.isPresent()) {
            CartItem existingItem = cartItem.get();
            existingItem.setQuantity(existingItem.getQuantity() + cartRequest.getQuantity());
            existingItem.setPrice(BigDecimal.valueOf(1500));
            cartItemRepository.save(existingItem);

            logger.info("Updated existing cart item for userId={}, productId={}", userId, cartRequest.getProductId());
            return true;
        }

        CartItem newCartItem = new CartItem();
        newCartItem.setProductId(cartRequest.getProductId());
        newCartItem.setUserId(Long.valueOf(userId));
        newCartItem.setQuantity(cartRequest.getQuantity());
        newCartItem.setPrice(BigDecimal.valueOf(1000));
        cartItemRepository.save(newCartItem);

        logger.info("Created new cart item for userId={}, productId={}", userId, cartRequest.getProductId());
        return true;
    }

    @Override
    public boolean removeItem(Long userId, Long productId) {
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
    public List<CartResponse> getCartItems(Long userId) {
        logger.info("Fetching cart items for userId={}", userId);

        List<CartResponse> cartItems = cartItemRepository.findByUserId(userId)
                .stream().map(cartMapper::toDto).toList();

        logger.info("Found {} cart items for userId={}", cartItems.size(), userId);
        return cartItems;
    }
}
