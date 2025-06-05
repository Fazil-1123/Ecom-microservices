package com.ecommerce.product.mappers;

import com.ecommerce.product.domains.Product;
import com.ecommerce.product.dtos.ProductDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    Product toEntity(ProductDto productDto);

    ProductDto toDto(Product product);
}
