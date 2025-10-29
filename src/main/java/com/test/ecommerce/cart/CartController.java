package com.test.ecommerce.cart;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
@Validated
public class CartController {

    private final CartService cartService;

    public record CreateCartRequest(@NotNull Long customerId) {}
    public record CartResponse(Long cartId, Long customerId, Instant createdAt, Instant updatedAt) {}

    private static CartResponse toResponse(Cart c) {
        Long customerId = (c.getCustomer() != null ? c.getCustomer().getId() : null);
        return new CartResponse(c.getId(), customerId, c.getCreatedAt(), c.getUpdatedAt());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CartResponse create(@Valid @RequestBody CreateCartRequest req) {
        return toResponse(cartService.createCart(req.customerId()));
    }

    @GetMapping("/{cartId}")
    public CartResponse getOne(@PathVariable Long cartId) {
        return cartService.getById(cartId).map(CartController::toResponse)
                .orElseThrow(() -> new CartNotFoundException(cartId));
    }

    @GetMapping("/by-customer/{customerId}")
    public List<CartResponse> listByCustomer(@PathVariable Long customerId) {
        return cartService.getByCustomer(customerId).stream().map(CartController::toResponse).toList();
    }

    @PatchMapping("/{cartId}/touch")
    public CartResponse touch(@PathVariable Long cartId) {
        return toResponse(cartService.touch(cartId));
    }

    @DeleteMapping("/{cartId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long cartId) {
        cartService.delete(cartId);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(CartNotFoundException.class)
    public String handleNotFound(CartNotFoundException ex) { return ex.getMessage(); }

    static class CartNotFoundException extends RuntimeException {
        CartNotFoundException(Long id) { super("Cart not found: " + id); }
    }
}
