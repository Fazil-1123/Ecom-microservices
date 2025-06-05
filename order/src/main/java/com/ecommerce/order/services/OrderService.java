package com.ecommerce.order.services;


import com.ecommerce.order.dtos.OrderResponse;

public interface OrderService {
    OrderResponse placeOrder(String userId);

}
