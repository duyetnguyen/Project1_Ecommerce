package com.test.ecommerce.cart;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    // if you ever need it:
    @Query("select c from Cart c where c.customer.id = :customerId")
    List<Cart> findByCustomerId(@Param("customerId") Long customerId);

    @Query("""
           select c from Cart c
           where c.customer.id = :customerId
           order by c.updatedAt desc
           """)
    List<Cart> findAllByCustomerIdOrderByUpdatedAtDesc(@Param("customerId") Long customerId);

    @Query("""
           select c from Cart c
           where c.customer.id = :customerId
           order by c.updatedAt desc
           """)
    Optional<Cart> findTopByCustomerIdOrderByUpdatedAtDesc(@Param("customerId") Long customerId);

    @Query("select count(c) > 0 from Cart c where c.customer.id = :customerId and c.createdAt >= :since")
    boolean existsForCustomerSince(@Param("customerId") Long customerId, @Param("since") Instant since);
}