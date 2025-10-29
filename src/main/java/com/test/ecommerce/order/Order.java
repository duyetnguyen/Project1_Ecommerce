package com.test.ecommerce.order;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.validation.constraints.*; // for validation annotations
import lombok.*;
import com.test.ecommerce.customer.Customer; // assuming Customer is in this package
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "customer_id")  
@EqualsAndHashCode(onlyExplicitlyIncluded = true)   // only include fields marked
@Entity
@Table(name = "orders",  indexes = {
    @Index(name = "ix_orders_customer", columnList = "customer_id"),
    @Index(name = "ix_orders_order_number", columnList = "order_number")
  },
  uniqueConstraints = {
    @UniqueConstraint(name = "uk_orders_order_number", columnNames = "order_number")
  }
)
@Data   
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @NotBlank
    @Column(name = "orderNumber", nullable = false)
    private String orderNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_Id", nullable = false)
    private Customer customerid;   // many-to-one relationship with Customer

    @NotNull
    @Column(name = "Order_Date", nullable = false, updatable = false)   
    private Instant orderdate;

    @NotNull
    @DecimalMin(value = "0.00", inclusive = false)
    @Digits(integer = 8, fraction = 2)
    @Column(name = "Sub_Total", nullable = false,precision = 10, scale = 2)
    private BigDecimal subtotal;

    @NotNull
    @DecimalMin(value = "0.00", inclusive = false)
    @Digits(integer = 8, fraction = 2)
    @Column(name = "Tax", nullable = false, precision = 10, scale = 2)
    private BigDecimal tax;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    @Digits(integer = 8, fraction = 2)
    @Column(name = "Shipping", nullable = false, precision = 10, scale = 2)
    private BigDecimal shipping;


    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    @Digits(integer = 8, fraction = 2)
    @Column(name = "Total", nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @NotBlank
    @Column(name = "Status", nullable = false, length = 50)
    private String status; // e.g., "Pending", "Shipped", "Delivered"

    @Column(name = "paid", nullable = false)
    private Boolean paid; // payment status

    @Column(name = "shipped_Date")
    private LocalDateTime shippeddate; // date when the order was shipped

    @Column(name = "payment_ref",  length = 50 )
    private String payment_ref; // e.g., "Credit Card", "PayPal"

    @Column(name = "payment_Date" )
    private LocalDateTime paymentdate; // date when the payment was made

}
