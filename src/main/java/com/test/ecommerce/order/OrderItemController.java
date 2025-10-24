package com.test.ecommerce.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/order-items")
@RequiredArgsConstructor
@Validated
public class OrderItemController {

    private final OrderItemService orderItemService;

    // === DTOs ===
    public record AddLineRequest(
            @NotNull Long orderId,
            @NotNull Long productId,
            @Positive int quantity,
            BigDecimal unitPrice // required for new line; optional for upsert
    ) {}
    public record SetQtyRequest(
            @NotNull Long orderId, @NotNull Long productId, @Positive int quantity
    ) {}
    public record SetPriceRequest(
            @NotNull Long orderId, @NotNull Long productId, @NotNull BigDecimal unitPrice
    ) {}
    public record LineResponse(
            Long id, Long orderId, Long productId, int quantity, BigDecimal unitPrice, BigDecimal lineTotal
    ) {}

    private static LineResponse toResponse(OrderItem oi) {
        return new LineResponse(
                oi.getId(),
                oi.getOrder().getId(),
                oi.getProduct().getId(),
                oi.getQuantity(),
                oi.getUnitPrice(),
                oi.getLineTotal()
        );
    }

    @GetMapping("/{orderId}")
    public List<LineResponse> list(@PathVariable Long orderId) {
        return orderItemService.listLines(orderId).stream().map(OrderItemController::toResponse).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LineResponse add(@Valid @RequestBody AddLineRequest req) {
        var oi = orderItemService.addLine(req.orderId(), req.productId(), req.quantity(), req.unitPrice());
        return toResponse(oi);
    }

    @PutMapping("/quantity")
    public LineResponse setQuantity(@Valid @RequestBody SetQtyRequest req) {
        var oi = orderItemService.setQuantity(req.orderId(), req.productId(), req.quantity());
        return toResponse(oi);
    }

    @PutMapping("/price")
    public LineResponse setPrice(@Valid @RequestBody SetPriceRequest req) {
        var oi = orderItemService.setUnitPrice(req.orderId(), req.productId(), req.unitPrice());
        return toResponse(oi);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@RequestParam Long orderId, @RequestParam Long productId) {
        orderItemService.removeLine(orderId, productId);
    }

    @DeleteMapping("/clear/{orderId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clear(@PathVariable Long orderId) {
        orderItemService.clear(orderId);
    }
}