package com.test.ecommerce.order;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // Use explicit JPQL because the field is named `order_number` (snake_case).
    @Query("select o from Order o where o.order_number = :orderNumber")
    Optional<Order> findByOrderNumber(@Param("orderNumber") String orderNumber);

    @Query("select (count(o) > 0) from Order o where o.order_number = :orderNumber")
    boolean existsByOrderNumber(@Param("orderNumber") String orderNumber);

    Page<Order> findByStatusIgnoreCase(String status, Pageable pageable);

    // Pessimistic lock for safe status/payment/shipping updates
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select o from Order o where o.id = :id")
    Optional<Order> findByIdForUpdate(@Param("id") Long id);
}