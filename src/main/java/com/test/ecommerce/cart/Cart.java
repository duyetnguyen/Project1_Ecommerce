package com.test.ecommerce.cart;

import jakarta.persistence.*; // for JPA annotations
import jakarta.validation.constraints.*; // for validation annotations
import lombok.*;


@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "carts")


public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cart_id; // primary key ID

    @NotNull
    @Column(name = "customer_id", nullable = false)
    private Long customer_id; // foreign key to Customer

    @NotNull
    @Column(name = "created_at", nullable = false, updatable = false)
    private java.time.Instant created_at; // timestamp when the cart was created

    @NotNull
    @Column(name = "updated_at", nullable = false)
    private java.time.Instant updated_at; // timestamp when the cart was last updated
}
