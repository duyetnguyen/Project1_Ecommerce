package com.test.ecommerce.order;

import com.test.ecommerce.customer.Customer;
import com.test.ecommerce.customer.CustomerRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Validated
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;

    public OrderService(OrderRepository orderRepository,
                        CustomerRepository customerRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
    }

    /* ===========================
       Create
       =========================== */
    @Transactional
    public Order create(@Valid CreateOrderCommand cmd) {
        if (orderRepository.existsByOrderNumber(cmd.orderNumber())) {
            throw new DuplicateOrderNumberException(cmd.orderNumber());
        }
        Customer customer = findCustomerOrThrow(cmd.customerId());

        // If not provided, set order_date to now
        Instant when = cmd.orderDate() != null ? cmd.orderDate() : Instant.now();

        // Optionally validate totals; here we recompute to be safe
        BigDecimal computedTotal = cmd.subTotal().add(cmd.tax()).add(cmd.shipping());
        if (cmd.total() != null && cmd.total().compareTo(computedTotal) != 0) {
            throw new IllegalArgumentException("Total does not equal sub_total + tax + shipping");
        }

        Order o = Order.builder()
                .order_number(cmd.orderNumber())
                .customer(customer)
                .order_date(when)
                .sub_total(cmd.subTotal())
                .tax(cmd.tax())
                .shipping(cmd.shipping())
                .total(computedTotal)
                .status(cmd.status())
                .paid(Boolean.FALSE.equals(cmd.paid()) ? false : (cmd.paid() != null && cmd.paid()))
                .shipped_date(cmd.shippedDate())
                .payment_method(cmd.paymentMethod())
                .payment_date(cmd.paymentDate())
                .build();

        return orderRepository.save(o);
    }

    /* ===========================
       Reads
       =========================== */
    public Order getById(@NotNull Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
    }

    public Optional<Order> getByOrderNumber(@NotBlank String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber);
    }

    public Page<Order> list(@Nullable String status, Pageable pageable) {
        if (status == null || status.isBlank()) {
            return orderRepository.findAll(pageable);
        }
        return orderRepository.findByStatusIgnoreCase(status.trim(), pageable);
    }

    /* ===========================
       Updates (status / payment / shipping / totals)
       =========================== */

    @Transactional
    public Order updateStatus(@NotNull Long id, @NotBlank String newStatus) {
        Order o = orderRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        o.setStatus(newStatus);
        return orderRepository.save(o);
    }

    @Transactional
    public Order markPaid(@NotNull Long id, @NotNull Boolean paid,
                          @Nullable LocalDateTime paymentDate,
                          @Nullable String paymentMethod) {
        Order o = orderRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        o.setPaid(paid);
        if (paid) {
            if (paymentDate != null) o.setPayment_date(paymentDate);
            if (paymentMethod != null && !paymentMethod.isBlank()) o.setPayment_method(paymentMethod);
        }
        return orderRepository.save(o);
    }

    @Transactional
    public Order setShipped(@NotNull Long id, @NotNull LocalDateTime shippedAt) {
        Order o = orderRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        o.setShipped_date(shippedAt);
        o.setStatus("Shipped");
        return orderRepository.save(o);
    }

    @Transactional
    public Order recalcTotals(@NotNull Long id, @NotNull BigDecimal subTotal,
                              @NotNull BigDecimal tax, @NotNull BigDecimal shipping) {
        Order o = orderRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        o.setSub_total(subTotal);
        o.setTax(tax);
        o.setShipping(shipping);
        o.setTotal(subTotal.add(tax).add(shipping));
        return orderRepository.save(o);
    }

    /* ===========================
       Delete
       =========================== */
    @Transactional
    public void delete(@NotNull Long id) {
        try {
            orderRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new OrderNotFoundException(id);
        }
    }

    /* ===========================
       Helpers & Commands
       =========================== */
    private Customer findCustomerOrThrow(Long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));
    }

    // Command DTO for creation
    public record CreateOrderCommand(
            @NotBlank String orderNumber,
            @NotNull Long customerId,
            @NotNull BigDecimal subTotal,
            @NotNull BigDecimal tax,
            @NotNull BigDecimal shipping,
            @Nullable BigDecimal total,            // optional; will be validated/recomputed
            @Nullable Instant orderDate,
            @NotBlank String status,
            @Nullable Boolean paid,
            @Nullable LocalDateTime shippedDate,
            @Nullable String paymentMethod,
            @Nullable LocalDateTime paymentDate
    ) {}

    /* Exceptions */
    public static class OrderNotFoundException extends RuntimeException {
        public OrderNotFoundException(Long id) { super("Order not found: id=" + id); }
    }
    public static class DuplicateOrderNumberException extends RuntimeException {
        public DuplicateOrderNumberException(String on) { super("Order number exists: " + on); }
    }
    public static class CustomerNotFoundException extends RuntimeException {
        public CustomerNotFoundException(Long id) { super("Customer not found: id=" + id); }
    }
}