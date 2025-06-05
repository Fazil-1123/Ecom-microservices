package com.ecommerce.order.mappers;


import com.ecommerce.order.dtos.OrderItemResponse;
import com.ecommerce.order.dtos.OrderResponse;
import com.ecommerce.order.models.Order;
import com.ecommerce.order.models.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface OrderMapper {

    OrderResponse toDto(Order order);

    @Mapping(target = "productId", source = "productId")
    @Mapping(target = "price", source = "price")
    OrderItemResponse toDto(OrderItem item);
}
