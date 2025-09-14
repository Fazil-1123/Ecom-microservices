package com.ecommerce.product.dtos;

public record ReserveStockResponse(Long productId, int remainingStock, int newVersion) {
}
