package com.test.ecommerce.cart;

import jakarta.persistence.*; // for JPA annotations
import jakarta.validation.constraints.*; // for validation annotations
import lombok.*;
import java.time.Instant;
import org.hibernate.annotations.CreationTimestamp;
import com.test.ecommerce.product.Product;
import com.test.ecommerce.cart.Cart;
@Getter
@Setter
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(
  name = "cart_items",
  uniqueConstraints = @UniqueConstraint(name = "uniq_cart_product", columnNames = {"cart_id","product_id"}),
  indexes = {
    @Index(name = "ix_ci_cart", columnList = "cart_id"),
    @Index(name = "ix_ci_product", columnList = "product_id")
  }
)
public class Item_in_Cart {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "cart_id", nullable = false)       // FK -> shopping_carts.id
  private Cart cart;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "product_id", nullable = false)    // FK -> products.id
  private Product product;

  @NotNull
  @Column(nullable = false)
  private Integer quantity;

  @CreationTimestamp
  @Column(name = "added_at", nullable = false, updatable = false)
  private Instant addedAt;

}