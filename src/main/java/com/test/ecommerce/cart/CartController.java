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

    // DTOs
    public record CreateCartRequest(@NotNull Long customerId) {}
    public record CartResponse(Long cartId, Long customerId, Instant createdAt, Instant updatedAt) {}

    private static CartResponse toResponse(Cart c) {
        return new CartResponse(
                c.getCart_id(),
                c.getCustomer_id(),
                c.getCreated_at(),
                c.getUpdated_at()
        );
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CartResponse create(@Valid @RequestBody CreateCartRequest req) {
        return toResponse(cartService.createCart(req.customerId()));
    }

    @GetMapping("/{cartId}")
    public CartResponse getOne(@PathVariable Long cartId) {
        Cart cart = cartService.getById(cartId)
                .orElseThrow(() -> new CartNotFoundException(cartId));
        return toResponse(cart);
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

    // Simple 404 mapper
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(CartNotFoundException.class)
    public String handleNotFound(CartNotFoundException ex) {
        return ex.getMessage();
    }

    static class CartNotFoundException extends RuntimeException {
        CartNotFoundException(Long id) { super("Cart not found: " + id); }
    }
}