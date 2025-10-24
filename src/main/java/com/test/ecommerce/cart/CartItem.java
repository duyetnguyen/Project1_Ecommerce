package com.test.ecommerce.cart;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.Instant;
import org.hibernate.annotations.CreationTimestamp;
import com.test.ecommerce.product.Product;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(
  name = "cart_items",
  uniqueConstraints = @UniqueConstraint(name = "uniq_cart_product", columnNames = {"cart_id","product_id"}),
  indexes = {
    @Index(name = "ix_ci_cart", columnList = "cart_id"),
    @Index(name = "ix_ci_product", columnList = "product_id")
  }
)
public class CartItem {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  @EqualsAndHashCode.Include
  private Long id;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "cart_id", nullable = false)
  private Cart cart;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "product_id", nullable = false)
  private Product product;

  @NotNull
  @Min(1)
  @Column(nullable = false)
  private Integer quantity;

  @CreationTimestamp
  @Column(name = "added_at", nullable = false, updatable = false)
  private Instant addedAt;

  @Version
  private Long version;
}