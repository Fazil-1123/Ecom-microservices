package com.ecommerce.product.repositories;

import com.ecommerce.product.domains.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByNameContainingIgnoreCaseAndActiveTrue(String keyword);

    List<Product> findByActiveTrue();
}
