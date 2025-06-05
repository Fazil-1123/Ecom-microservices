package com.ecommerce.order.services;


import com.ecommerce.order.dtos.CartRequest;
import com.ecommerce.order.dtos.CartResponse;

import java.util.List;

public interface CartItemService {
    Boolean addCartItem(Long userId, CartRequest cartRequest);

    boolean removeItem(Long userId, Long productId);

    List<CartResponse> getCartItems(Long userId);
}
