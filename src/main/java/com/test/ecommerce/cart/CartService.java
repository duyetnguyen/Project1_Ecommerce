package com.test.ecommerce.cart;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;

    @Transactional
    public Cart createCart(Long customerId) {
        var now = Instant.now();
        Cart cart = new Cart();
        cart.setCustomer_id(customerId);
        cart.setCreated_at(now);
        cart.setUpdated_at(now);
        return cartRepository.save(cart);
    }

    @Transactional(readOnly = true)
    public Optional<Cart> getById(Long cartId) {
        return cartRepository.findById(cartId);
    }

    @Transactional(readOnly = true)
    public List<Cart> getByCustomer(Long customerId) {
        return cartRepository.findAllByCustomerIdOrderByUpdatedDesc(customerId);
    }

    @Transactional
    public Cart touch(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found: " + cartId));
        cart.setUpdated_at(Instant.now());
        return cartRepository.save(cart);
    }

    @Transactional
    public void delete(Long cartId) {
        if (!cartRepository.existsById(cartId)) return; // idempotent
        cartRepository.deleteById(cartId);
    }
}