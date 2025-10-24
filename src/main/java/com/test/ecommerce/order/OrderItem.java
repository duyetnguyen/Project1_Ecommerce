package com.test.ecommerce.order;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

import com.test.ecommerce.order.Order;              
import com.test.ecommerce.product.Product;         

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@ToString(exclude = {"order", "product"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(
  name = "order_items",
  indexes = {
    @Index(name = "ix_oi_order", columnList = "order_id"),
    @Index(name = "ix_oi_product", columnList = "product_id")
  }
)
public class OrderItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @EqualsAndHashCode.Include
  @Column(name = "id")
  private Long id;

  /** FK -> orders.id (many items belong to one order) */
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(
    name = "order_id",
    nullable = false,
    foreignKey = @ForeignKey(name = "fk_oi_order")
  )
  private Order order;

  /** FK -> products.id (each line references one product) */
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(
    name = "product_id",
    nullable = false,
    foreignKey = @ForeignKey(name = "fk_oi_product")
  )
  private Product product;

  /** Must be > 0 (matches DB CHECK constraint) */
  @NotNull
  @Positive
  @Column(name = "quantity", nullable = false)
  private Integer quantity;

  /** DECIMAL(10,2) NOT NULL */
  @NotNull
  @DecimalMin(value = "0.00")
  @Digits(integer = 8, fraction = 2)
  @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
  private BigDecimal unitPrice;

  /**
   * STORED generated column in DB: line_total = quantity * unit_price
   * Mark read-only so Hibernate doesn’t try to write it.
   */
  @Digits(integer = 8, fraction = 2)
  @Column(name = "line_total", precision = 10, scale = 2, insertable = false, updatable = false)
  private BigDecimal lineTotal;

  /** Convenience helper (not persisted) */
  @Transient
  public BigDecimal computeLineTotal() {
    if (unitPrice == null || quantity == null) return null;
    return unitPrice.multiply(BigDecimal.valueOf(quantity.longValue()));
  }
}
