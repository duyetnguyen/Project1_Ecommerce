package com.test.ecommerce.customer;

import com.test.ecommerce.customer.Customer;
import com.test.ecommerce.customer.CustomerService;
import com.test.ecommerce.customer.CustomerService.CustomerNotFoundException;
import com.test.ecommerce.customer.CustomerService.DuplicateEmailException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    /* ===== Create ===== */
    @PostMapping
    public ResponseEntity<Customer> create(@Valid @RequestBody CreateCustomerRequest req) {
        var cmd = new CustomerService.CreateCustomerCommand(
                req.email(),
                req.passwordHash(),
                req.firstName(),
                req.lastName(),
                req.phone(),
                req.address(),
                req.city(),
                req.state(),
                req.zipCode(),
                req.createdAt() != null ? req.createdAt() : Instant.now(),
                req.lastLogin()
        );
        Customer created = customerService.create(cmd);
        return ResponseEntity.created(URI.create("/api/customers/" + created.getId()))
                .body(created);
    }

    /* ===== Reads ===== */
    @GetMapping("/{id}")
    public Customer getById(@PathVariable Long id) {
        return customerService.getById(id);
    }

    @GetMapping("/by-email/{email}")
    public ResponseEntity<Customer> getByEmail(@PathVariable String email) {
        return customerService.getByEmail(email)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping
    public Page<Customer> list(@RequestParam(required = false) String q, Pageable pageable) {
        return customerService.list(q, pageable);
    }

    /* ===== Update ===== */
    @PutMapping("/{id}")
    public Customer update(@PathVariable Long id, @Valid @RequestBody UpdateCustomerRequest req) {
        var cmd = new CustomerService.UpdateCustomerCommand(
                req.email(),
                req.passwordHash(),
                req.firstName(),
                req.lastName(),
                req.phone(),
                req.address(),
                req.city(),
                req.state(),
                req.zipCode(),
                req.lastLogin()
        );
        return customerService.update(id, cmd);
    }

    @PatchMapping("/{id}/last-login")
    public Customer touchLastLogin(@PathVariable Long id,
                                   @RequestBody @Valid TouchLoginRequest req) {
        LocalDateTime when = req.when() != null ? req.when() : LocalDateTime.now();
        return customerService.touchLastLogin(id, when);
    }

    /* ===== Delete ===== */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        customerService.delete(id);
    }

    /* ===== Request DTOs (simple/local) ===== */
    public record CreateCustomerRequest(
            @Email @NotBlank String email,
            @NotBlank String passwordHash, // already hashed
            @NotBlank String firstName,
            @NotBlank String lastName,
            String phone,
            String address,
            String city,
            String state,
            String zipCode,
            Instant createdAt,
            LocalDateTime lastLogin
    ) {}

    public record UpdateCustomerRequest(
            @Email @NotBlank String email,
            String passwordHash,           // optional
            @NotBlank String firstName,
            @NotBlank String lastName,
            String phone,
            String address,
            String city,
            String state,
            String zipCode,
            LocalDateTime lastLogin
    ) {}

    public record TouchLoginRequest(LocalDateTime when) {}

    /* ===== Exception mapping ===== */
    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<Void> notFound() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<Map<String, String>> conflict(DuplicateEmailException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", "conflict", "message", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> validation(MethodArgumentNotValidException ex) {
        Map<String, String> fields = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(fe -> fields.put(fe.getField(), fe.getDefaultMessage()));
        return ResponseEntity.badRequest()
                .body(Map.of("error", "validation_failed", "fields", fields));
    }
}