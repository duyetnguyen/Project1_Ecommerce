package com.test.ecommerce.cart;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.test.ecommerce.product.ProductRepository;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartItemService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository; // assume you have this

    @Transactional(readOnly = true)
    public List<CartItem> listItems(Long cartId) {
        return cartItemRepository.findAllByCartIdFetchProduct(cartId);
    }

    @Transactional
    public CartItem addItem(Long cartId, Long productId, int qty) {
        if (qty < 1) throw new IllegalArgumentException("Quantity must be >= 1");

        // Fast path: try find existing and increment
        var existing = cartItemRepository.findByCartIdAndProductId(cartId, productId);
        if (existing.isPresent()) {
            var ci = existing.get();
            ci.setQuantity(ci.getQuantity() + qty);
            bumpCartUpdatedAt(cartId);
            return ci;
        }

        // Create new row. If another thread created it concurrently, we retry.
        try {
            var cart = cartRepository.findById(cartId)
                    .orElseThrow(() -> new IllegalArgumentException("Cart not found: " + cartId));
            var product = productRepository.findById(productId)
                    .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));

            var ci = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(qty)
                    .build();
            var saved = cartItemRepository.save(ci);
            bumpCartUpdatedAt(cartId);
            return saved;
        } catch (DataIntegrityViolationException ex) {
            // Unique constraint hit -> someone inserted in parallel. Retry by incrementing.
            var ci = cartItemRepository.findByCartIdAndProductId(cartId, productId)
                    .orElseThrow(() -> ex);
            ci.setQuantity(ci.getQuantity() + qty);
            bumpCartUpdatedAt(cartId);
            return ci;
        }
    }

    @Transactional
    public CartItem setQuantity(Long cartId, Long productId, int qty) {
        if (qty < 1) throw new IllegalArgumentException("Quantity must be >= 1");
        var ci = cartItemRepository.findByCartIdAndProductId(cartId, productId)
                .orElseThrow(() -> new IllegalArgumentException("Cart item not found"));
        ci.setQuantity(qty);
        bumpCartUpdatedAt(cartId);
        return ci;
    }

    @Transactional
    public void removeItem(Long cartId, Long productId) {
        cartItemRepository.deleteByCartIdAndProductId(cartId, productId);
        bumpCartUpdatedAt(cartId);
    }

    @Transactional
    public void clear(Long cartId) {
        var items = cartItemRepository.findAllByCartIdFetchProduct(cartId);
        cartItemRepository.deleteAll(items);
        bumpCartUpdatedAt(cartId);
    }

    private void bumpCartUpdatedAt(Long cartId) {
        cartRepository.findById(cartId).ifPresent(c -> {
            c.setUpdatedAt(java.time.Instant.now());
            // save not necessary if within persistence context; but safe:
            cartRepository.save(c);
        });
    }
}