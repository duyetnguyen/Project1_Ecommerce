package com.test.ecommerce.product;

import jakarta.persistence.LockModeType;
import jakarta.validation.constraints.NotNull;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // For create/update guards
    boolean existsBySku(String sku);

    // For lookups by SKU
    Optional<Product> findBySku(String sku);

    // For the list(...) filter
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);

    // For safe updates & stock adjustments (SELECT ... FOR UPDATE)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Product p where p.id = :id")
    Optional<Product> findByIdForUpdate(@Param("id") Long id);

    boolean existsByCategory_Id(Long id);
}
