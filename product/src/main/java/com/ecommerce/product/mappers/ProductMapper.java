package com.ecommerce.product.mappers;

import com.ecommerce.product.domains.Product;
import com.ecommerce.product.dtos.ProductDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    Product toEntity(ProductDto productDto);

    @Mapping(target = "version", source = "version")
    ProductDto toDto(Product product);
}
