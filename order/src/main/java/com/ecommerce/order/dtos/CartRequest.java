package com.ecommerce.order.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CartRequest {

    @NotBlank(message = "Product ID is required")
    private Long productId;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;
}
