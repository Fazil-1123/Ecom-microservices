package com.ecommerce.product.services;

import com.ecommerce.product.dtos.ProductDto;

import java.util.List;

public interface ProductService {
    ProductDto addProduct(ProductDto productDto);

    ProductDto updateProduct(Long id, ProductDto productDto);

    String deleteProduct(Long id);

    List<ProductDto> getAllProducts();

    ProductDto findById(Long id);

    List<ProductDto> findByKeyword(String keyword);
}
