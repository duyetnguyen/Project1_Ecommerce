package com.test.ecommerce.order;

import com.test.ecommerce.order.Order;
import com.test.ecommerce.order.OrderService;
import com.test.ecommerce.order.OrderService.CustomerNotFoundException;
import com.test.ecommerce.order.OrderService.DuplicateOrderNumberException;
import com.test.ecommerce.order.OrderService.OrderNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /* ===== Create ===== */
    @PostMapping
    public ResponseEntity<Order> create(@Valid @RequestBody CreateOrderRequest req) {
        var cmd = new OrderService.CreateOrderCommand(
                req.orderNumber(),
                req.customerId(),
                req.subTotal(),
                req.tax(),
                req.shipping(),
                req.total(),
                req.orderDate(),
                req.status(),
                req.paid(),
                req.shippedDate(),
                req.paymentMethod(),
                req.paymentDate()
        );
        Order created = orderService.create(cmd);
        return ResponseEntity
                .created(URI.create("/api/orders/" + created.getId()))
                .body(created);
    }

    /* ===== Reads ===== */
    @GetMapping("/{id}")
    public Order get(@PathVariable Long id) {
        return orderService.getById(id);
    }

    @GetMapping("/by-number/{orderNumber}")
    public ResponseEntity<Order> getByNumber(@PathVariable String orderNumber) {
        return orderService.getByOrderNumber(orderNumber)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping
    public Page<Order> list(@RequestParam(required = false) String status, Pageable pageable) {
        return orderService.list(status, pageable);
    }

    /* ===== Updates ===== */
    @PatchMapping("/{id}/status")
    public Order updateStatus(@PathVariable Long id, @RequestBody @Valid UpdateStatusRequest req) {
        return orderService.updateStatus(id, req.status());
    }

    @PatchMapping("/{id}/paid")
    public Order markPaid(@PathVariable Long id, @RequestBody @Valid MarkPaidRequest req) {
        return orderService.markPaid(id, req.paid(), req.paymentDate(), req.paymentMethod());
    }

    @PatchMapping("/{id}/shipped")
    public Order setShipped(@PathVariable Long id, @RequestBody @Valid SetShippedRequest req) {
        LocalDateTime when = req.shippedDate() != null ? req.shippedDate() : LocalDateTime.now();
        return orderService.setShipped(id, when);
    }

    @PutMapping("/{id}/totals")
    public Order recalcTotals(@PathVariable Long id, @RequestBody @Valid RecalcTotalsRequest req) {
        return orderService.recalcTotals(id, req.subTotal(), req.tax(), req.shipping());
    }

    /* ===== Delete ===== */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        orderService.delete(id);
    }

    /* ===== Local Request DTOs (Option: move to a Dtos file later) ===== */
    public record CreateOrderRequest(
            @NotBlank String orderNumber,
            @NotNull Long customerId,
            @NotNull java.math.BigDecimal subTotal,
            @NotNull java.math.BigDecimal tax,
            @NotNull java.math.BigDecimal shipping,
            java.math.BigDecimal total,
            java.time.Instant orderDate,
            @NotBlank String status,
            Boolean paid,
            java.time.LocalDateTime shippedDate,
            String paymentMethod,
            java.time.LocalDateTime paymentDate
    ) {}

    public record UpdateStatusRequest(@NotBlank String status) {}
    public record MarkPaidRequest(@NotNull Boolean paid,
                                  java.time.LocalDateTime paymentDate,
                                  String paymentMethod) {}
    public record SetShippedRequest(java.time.LocalDateTime shippedDate) {}
    public record RecalcTotalsRequest(
            @NotNull java.math.BigDecimal subTotal,
            @NotNull java.math.BigDecimal tax,
            @NotNull java.math.BigDecimal shipping
    ) {}

    /* ===== Simple exception mapping ===== */
    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<Void> notFound() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @ExceptionHandler({DuplicateOrderNumberException.class, CustomerNotFoundException.class})
    public ResponseEntity<Map<String, String>> conflict(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", "conflict", "message", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> validation(MethodArgumentNotValidException ex) {
        Map<String, String> fields = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(fe -> fields.put(fe.getField(), fe.getDefaultMessage()));
        return ResponseEntity.badRequest().body(Map.of("error", "validation_failed", "fields", fields));
    }
}