package com.ecommerce.order.dtos;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartResponse {

    private Long productId;

    private Integer quantity;

    private BigDecimal price;
}
