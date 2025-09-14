package com.ecommerce.order.services;


import com.ecommerce.order.dtos.OrderResponse;
import com.ecommerce.order.dtos.ReserveStockResponse;

public interface OrderService {
    OrderResponse placeOrder(String userId);


}
