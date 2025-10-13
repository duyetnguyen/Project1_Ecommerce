package com.test.ecommerce.category;

import jakarta.persistence.*; // for JPA annotations
import jakarta.validation.constraints.*; // for validation annotations
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // primary key ID

    @NotBlank
    @Column(name = "name", nullable = false, length = 100)
    private String name; // category name

    @Column(name = "description", length = 500)
    private String description; // category description

}

