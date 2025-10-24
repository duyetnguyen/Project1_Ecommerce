package com.test.ecommerce.cart;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    @Query("select c from Cart c where c.customer_id = :customerId")
    List<Cart> findByCustomerId(@Param("customerId") Long customerId);

    @Query("""
           select c from Cart c
           where c.customer_id = :customerId
           order by c.updated_at desc
           """)
    List<Cart> findAllByCustomerIdOrderByUpdatedDesc(@Param("customerId") Long customerId);

    @Query("""
           select c from Cart c
           where c.customer_id = :customerId
           order by c.updated_at desc
           """)
    Optional<Cart> findTopByCustomerIdOrderByUpdatedDesc(@Param("customerId") Long customerId);

    @Query("select count(c) > 0 from Cart c where c.customer_id = :customerId and c.created_at >= :since")
    boolean existsForCustomerSince(@Param("customerId") Long customerId, @Param("since") Instant since);
}