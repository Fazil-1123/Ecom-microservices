package com.ecommerce.order.services;


import com.ecommerce.order.dtos.CartRequest;
import com.ecommerce.order.dtos.CartResponse;

import java.util.List;

public interface CartItemService {
    Boolean addCartItem(String userId, CartRequest cartRequest);

    boolean removeItem(String userId, Long productId);

    List<CartResponse> getCartItems(String userId);
}
