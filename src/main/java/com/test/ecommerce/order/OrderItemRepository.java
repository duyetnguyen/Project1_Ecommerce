package com.test.ecommerce.order;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query("""
      select oi from OrderItem oi
      join fetch oi.product p
      where oi.order.id = :orderId
    """)
    List<OrderItem> findAllByOrderIdFetchProduct(@Param("orderId") Long orderId);

    @Query("select oi from OrderItem oi where oi.order.id = :orderId and oi.product.id = :productId")
    Optional<OrderItem> findByOrderIdAndProductId(@Param("orderId") Long orderId, @Param("productId") Long productId);

    @Modifying
    @Query("delete from OrderItem oi where oi.order.id = :orderId and oi.product.id = :productId")
    void deleteByOrderIdAndProductId(@Param("orderId") Long orderId, @Param("productId") Long productId);
}