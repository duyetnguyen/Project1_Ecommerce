package com.test.ecommerce.customer;

import java.time.Instant;
import java.time.LocalDateTime; // if you prefer timestamp w/o timezone

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "password_hash") // <— don’t log hashes
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "customers",
       uniqueConstraints = @UniqueConstraint(name = "uk_customers_email", columnNames = "email"))
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Email @NotBlank
    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @NotBlank
    @Column(name = "password_hash", nullable = false, length = 100)
    private String password_hash;

    @Column(name = "last_login")
    private LocalDateTime last_login;  // or Instant if you prefer UTC

    @Column(name = "create_at", nullable = false, updatable = false)
    private Instant create_at;

    @NotBlank
    @Column(name = "first_name", nullable = false)
    private String first_name;

    @NotBlank
    @Column(name = "last_name", nullable = false)
    private String last_name;

    @Column(name = "phone")
    private String phone;

    @Column(name = "address")
    private String address;

    @Column(name = "city")
    private String city;

    @Column(name = "state")
    private String state;

    @Column(name = "zip_code")
    private String zip_code;
}
