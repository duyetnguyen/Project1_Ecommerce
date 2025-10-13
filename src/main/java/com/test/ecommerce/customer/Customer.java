package com.test.ecommerce.customer;

import java.security.Timestamp;
import java.time.Instant;

import jakarta.persistence.*; // for JPA annotations
import jakarta.validation.constraints.*; // for validation annotations
import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)   // only include fields marked
@Entity
@Table(name = "customers")
public class Customer {
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;    //primary key

    @NotBlank
    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @NotBlank
    @Column (name = "password_hash", nullable = false, length= 100)
    private String password_hash;

    @Column(name = "last_login", nullable = true)
    private Timestamp last_login;

    @Column(name = "Create_at", nullable = false, updatable = false)
    private Instant Create_at; 

    //@Column(name = "Update_at")
    //private Timestamp Update_at;
    
    @Column(name = "first_name", nullable = false)
    private String first_name;

    @Column(name = "last_name", nullable = false)
    private String last_name;

    @Column(name = "Phone")
    private String phone;

    @Column(name = "Address")
    private String address;

    @Column(name = "City")
    private String city;

    @Column(name = "State")
    private String state;

    @Column(name = "Zip_code")
    private String zip_code;

    
}
