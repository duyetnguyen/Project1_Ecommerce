package com.test.ecommerce.customer;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Validated
@Transactional(readOnly = true)
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    /* ===========================
       Create
       =========================== */
    @Transactional
    public Customer create(@Valid CreateCustomerCommand cmd) {
        if (customerRepository.existsByEmailIgnoreCase(cmd.email())) {
            throw new DuplicateEmailException(cmd.email());
        }

        Customer c = Customer.builder()
                .email(cmd.email())
                .password_hash(cmd.passwordHash())      // assume already hashed
                .first_name(cmd.firstName())
                .last_name(cmd.lastName())
                .phone(cmd.phone())
                .address(cmd.address())
                .city(cmd.city())
                .state(cmd.state())
                .zip_code(cmd.zipCode())
                .create_at(cmd.createdAt() != null ? cmd.createdAt() : Instant.now())
                .last_login(cmd.lastLogin())
                .build();

        return customerRepository.save(c);
    }

    /* ===========================
       Reads
       =========================== */
    public Customer getById(@NotNull Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));
    }

    public Optional<Customer> getByEmail(@Email @NotBlank String email) {
        return customerRepository.findByEmailIgnoreCase(email);
    }

    public Page<Customer> list(@Nullable String emailContains, Pageable pageable) {
        // simplest: list all; you can add a query method like findByEmailContainingIgnoreCase later
        if (emailContains == null || emailContains.isBlank()) {
            return customerRepository.findAll(pageable);
        }
        // If you want this filter, add it to repo: Page<Customer> findByEmailContainingIgnoreCase(String e, Pageable p)
        throw new UnsupportedOperationException("Add findByEmailContainingIgnoreCase to the repository to enable filtering");
    }

    /* ===========================
       Updates
       =========================== */
    @Transactional
    public Customer update(@NotNull Long id, @Valid UpdateCustomerCommand cmd) {
        Customer c = getById(id);

        // If email changes, enforce uniqueness
        if (!c.getEmail().equalsIgnoreCase(cmd.email())
                && customerRepository.existsByEmailIgnoreCase(cmd.email())) {
            throw new DuplicateEmailException(cmd.email());
        }

        c.setEmail(cmd.email());
        c.setFirst_name(cmd.firstName());
        c.setLast_name(cmd.lastName());
        c.setPhone(cmd.phone());
        c.setAddress(cmd.address());
        c.setCity(cmd.city());
        c.setState(cmd.state());
        c.setZip_code(cmd.zipCode());

        // Only set password if provided (avoid nuking it accidentally)
        if (cmd.passwordHash() != null && !cmd.passwordHash().isBlank()) {
            c.setPassword_hash(cmd.passwordHash());
        }

        // last_login is managed separately; skip here unless provided
        if (cmd.lastLogin() != null) {
            c.setLast_login(cmd.lastLogin());
        }

        return customerRepository.save(c);
    }

    @Transactional
    public Customer touchLastLogin(@NotNull Long id, @NotNull LocalDateTime when) {
        Customer c = getById(id);
        c.setLast_login(when);
        return customerRepository.save(c);
    }

    /* ===========================
       Delete
       =========================== */
    @Transactional
    public void delete(@NotNull Long id) {
        try {
            customerRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new CustomerNotFoundException(id);
        }
    }

    /* ===========================
       Commands
       =========================== */
    public record CreateCustomerCommand(
            @Email @NotBlank String email,
            @NotBlank String passwordHash, // already hashed
            @NotBlank String firstName,
            @NotBlank String lastName,
            String phone,
            String address,
            String city,
            String state,
            String zipCode,
            Instant createdAt,             // optional; default now
            LocalDateTime lastLogin        // optional
    ) {}

    public record UpdateCustomerCommand(
            @Email @NotBlank String email,
            String passwordHash,           // optional; set only if provided
            @NotBlank String firstName,
            @NotBlank String lastName,
            String phone,
            String address,
            String city,
            String state,
            String zipCode,
            LocalDateTime lastLogin        // optional
    ) {}

    /* ===========================
       Exceptions
       =========================== */
    public static class CustomerNotFoundException extends RuntimeException {
        public CustomerNotFoundException(Long id) { super("Customer not found: id=" + id); }
    }
    public static class DuplicateEmailException extends RuntimeException {
        public DuplicateEmailException(String email) { super("Email already in use: " + email); }
    }
}