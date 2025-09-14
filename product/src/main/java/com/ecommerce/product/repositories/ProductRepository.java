package com.ecommerce.product.repositories;

import com.ecommerce.product.domains.Product;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByNameContainingIgnoreCaseAndActiveTrue(String keyword);

    List<Product> findByActiveTrue();

    Optional<Product> findByIdAndActiveTrue(Long id);

    /**
     * Atomically decrement stock if there is enough AND the version matches.
     * Returns number of rows updated (0 means conflict or insufficient stock).
     *
     * JPQL bulk update generates SQL like:
     * UPDATE product
     *   SET stock_quantity = stock_quantity - ?, version = version + 1
     * WHERE id = ? AND stock_quantity >= ? AND version = ?
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("""
        UPDATE Product p
           SET p.stockQuantity = p.stockQuantity - :qty,
               p.version = p.version + 1
         WHERE p.id = :id
           AND p.stockQuantity >= :qty
           AND p.version = :expectedVersion
    """)
    int decrementStockIfVersionMatch(@Param("id") Long id,
                                     @Param("qty") int qty,
                                     @Param("expectedVersion") int expectedVersion);

    /**
     * (Optional) Release previously reserved stock (e.g., on order cancellation)
     * using optimistic versioning.
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("""
        UPDATE Product p
           SET p.stockQuantity = p.stockQuantity + :qty,
               p.version = p.version + 1
         WHERE p.id = :id
           AND p.version = :expectedVersion
    """)
    int incrementStockIfVersionMatch(@Param("id") Long id,
                                     @Param("qty") int qty,
                                     @Param("expectedVersion") int expectedVersion);
}
