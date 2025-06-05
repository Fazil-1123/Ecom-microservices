package com.ecommerce.order.mappers;


import com.ecommerce.order.dtos.CartResponse;
import com.ecommerce.order.models.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartMapper {

    CartItem toEntity(CartResponse cartResponse);

    @Mapping(source = "productId", target = "productId")
    CartResponse toDto(CartItem cartItem);
}
