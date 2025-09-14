package com.ecommerce.order.dtos;

public record ReserveStockResponse(Long productId, int remainingStock, int newVersion) {
}
