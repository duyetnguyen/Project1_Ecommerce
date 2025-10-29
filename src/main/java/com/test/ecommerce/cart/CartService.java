package com.test.ecommerce.cart;

import com.test.ecommerce.customer.Customer;
import com.test.ecommerce.customer.CustomerRepository;
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
    private final CustomerRepository customerRepository;

    @Transactional
    public Cart createCart(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + customerId));

        Cart cart = new Cart();
        cart.setCustomer(customer);
        // createdAt/updatedAt set by @PrePersist
        return cartRepository.save(cart);
    }

    @Transactional(readOnly = true)
    public Optional<Cart> getById(Long cartId) {
        return cartRepository.findById(cartId);
    }

    @Transactional(readOnly = true)
    public List<Cart> getByCustomer(Long customerId) {
        return cartRepository.findAllByCustomerIdOrderByUpdatedAtDesc(customerId);
    }

    @Transactional
    public Cart touch(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found: " + cartId));
        cart.setUpdatedAt(Instant.now());
        return cartRepository.save(cart);
    }

    @Transactional
    public void delete(Long cartId) {
        if (!cartRepository.existsById(cartId)) return;
        cartRepository.deleteById(cartId);
    }
}
