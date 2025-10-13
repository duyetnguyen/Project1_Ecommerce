package com.test.ecommerce.product;

import java.math.BigDecimal; // for price representation
import jakarta.persistence.*; // for JPA annotations
import jakarta.validation.constraints.*; // for validation annotations
import lombok.*;
import com.test.ecommerce.category.Category; // assuming Category is in this package

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "category")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)   // only include fields marked with @EqualsAndHashCode.Include
@Entity@Table(name = "products")
@Data
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id; // primary key ID

    @NotBlank
    @Column(name = "name", nullable = false, length = 100)
    private String name; // product name

    @NotBlank
    @Column(name = "sku", nullable = false, unique = true, length = 50)
    private String sku; // stock keeping unit, unique identifier

    @Column(name = "description", length = 500)
    private String description; // product description

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    @Column(name = "price", nullable = false)
    private BigDecimal price; // product price

    @NotNull
    @Min(0)
    @Column(name = "stock", nullable = false)
    private Integer stock; // available stock quantity

    @ManyToOne(fetch = FetchType.LAZY, optional = false )
    @JoinColumn(name = "category_id", nullable = false)
    private Category category; // many-to-one relationship with Category

}
