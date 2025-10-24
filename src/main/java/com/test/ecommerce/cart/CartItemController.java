package com.test.ecommerce.cart;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart-items")
@RequiredArgsConstructor
@Validated
public class CartItemController {

    private final CartItemService cartItemService;

    // DTOs
    public record AddItemRequest(@NotNull Long cartId, @NotNull Long productId, @Min(1) int quantity) {}
    public record SetQtyRequest(@NotNull Long cartId, @NotNull Long productId, @Min(1) int quantity) {}
    public record ItemResponse(Long id, Long cartId, Long productId, int quantity) {}

    private static ItemResponse toResponse(CartItem ci) {
        return new ItemResponse(
                ci.getId(),
                ci.getCart().getCart_id(),
                ci.getProduct().getId(),
                ci.getQuantity()
        );
    }

    @GetMapping("/{cartId}")
    public List<ItemResponse> list(@PathVariable Long cartId) {
        return cartItemService.listItems(cartId).stream().map(CartItemController::toResponse).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemResponse add(@Valid @RequestBody AddItemRequest req) {
        return toResponse(cartItemService.addItem(req.cartId(), req.productId(), req.quantity()));
    }

    @PutMapping("/quantity")
    public ItemResponse setQuantity(@Valid @RequestBody SetQtyRequest req) {
        return toResponse(cartItemService.setQuantity(req.cartId(), req.productId(), req.quantity()));
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@RequestParam Long cartId, @RequestParam Long productId) {
        cartItemService.removeItem(cartId, productId);
    }

    @DeleteMapping("/clear/{cartId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clear(@PathVariable Long cartId) {
        cartItemService.clear(cartId);
    }
}