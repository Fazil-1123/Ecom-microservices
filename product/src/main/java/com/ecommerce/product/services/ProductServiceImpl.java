package com.ecommerce.product.services;

import com.ecom.common.exception.ResourceNotFound;
import com.ecommerce.product.domains.Product;
import com.ecommerce.product.dtos.ProductDto;
import com.ecommerce.product.dtos.ReserveStockRequest;
import com.ecommerce.product.dtos.ReserveStockResponse;
import com.ecommerce.product.mappers.ProductMapper;
import com.ecommerce.product.repositories.ProductRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductServiceImpl(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    @Override
    public ProductDto addProduct(ProductDto productDto) {
        logger.info("Adding new product: {}", productDto.getName());
        Product product = productMapper.toEntity(productDto);
        Product savedProduct = productRepository.save(product);
        logger.info("Product added with ID: {}", savedProduct.getId());
        return productMapper.toDto(savedProduct);
    }

    @Override
    public ProductDto updateProduct(Long id, ProductDto productDto) {
        logger.info("Updating product ID: {}", id);
        return productRepository.findByIdAndActiveTrue(id).map(product -> {
            product.setName(productDto.getName());
            product.setDescription(productDto.getDescription());
            product.setPrice(productDto.getPrice());
            product.setCategory(productDto.getCategory());
            product.setImageUrl(productDto.getImageUrl());
            Product updatedProduct = productRepository.save(product);
            logger.info("Product updated: ID={}", updatedProduct.getId());
            return productMapper.toDto(updatedProduct);
        }).orElseThrow(() -> {
            logger.warn("Product not found for update: ID={}", id);
            return new ResourceNotFound("Resource not found with id: " + id);
        });
    }

    @Override
    public String deleteProduct(Long id) {
        logger.info("Deleting (deactivating) product ID: {}", id);
        productRepository.findByIdAndActiveTrue(id).map(product -> {
            product.setActive(false);
            Product saved = productRepository.save(product);
            logger.info("Product deactivated: ID={}", saved.getId());
            return saved;
        }).orElseThrow(() -> {
            logger.warn("Product not found for deletion: ID={}", id);
            return new ResourceNotFound("Resource not found with id: " + id);
        });
        return "Product deleted successfully";
    }

    @Override
    public List<ProductDto> getAllProducts() {
        logger.info("Fetching all active products");
        return productRepository.findByActiveTrue().stream()
                .map(productMapper::toDto).toList();
    }

    @Override
    public ProductDto findById(Long id) {
        logger.info("Fetching product by ID: {}", id);
        return productRepository.findByIdAndActiveTrue(id).map(productMapper::toDto).orElseThrow(
                () -> new ResourceNotFound("Product not found with id: %d".formatted(id))
        );

    }

    @Override
    public List<ProductDto> findByKeyword(String keyword) {
        logger.info("Searching products by keyword: {}", keyword);
        return productRepository.findByNameContainingIgnoreCaseAndActiveTrue(keyword).stream()
                .map(productMapper::toDto).toList();
    }

    @Override
    @Transactional
    public ReserveStockResponse reserve(Long productId, ReserveStockRequest req) {
        // 1) 404 if product missing / inactive
        Product current = productRepository.findByIdAndActiveTrue(productId)
                .orElseThrow(() -> new ResourceNotFound("Product not found with id: " + productId));

        // 2) single atomic decrement (optimistic)
        int rows = productRepository.decrementStockIfVersionMatch(productId, req.qty(), req.version());

        if (rows == 0) {
            // Distinguish version conflict vs insufficient stock for correct HTTP code
            if (current.getStockQuantity() == null || current.getStockQuantity() < req.qty()) {
                // 422 Unprocessable Entity
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "INSUFFICIENT_STOCK");
            }
            // 409 Conflict
            throw new ResponseStatusException(HttpStatus.CONFLICT, "VERSION_CONFLICT");
        }

        // 3) Return fresh values
        Product fresh = productRepository.findByIdAndActiveTrue(productId)
                .orElseThrow(() -> new ResourceNotFound("Product not found with id: " + productId));

        return new ReserveStockResponse(fresh.getId(), fresh.getStockQuantity(), fresh.getVersion());
    }
}
