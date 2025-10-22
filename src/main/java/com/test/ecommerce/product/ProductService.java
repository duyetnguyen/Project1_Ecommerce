package com.test.ecommerce.product;

import java.math.BigDecimal;
import java.util.Optional;


import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.test.ecommerce.category.Category;
import com.test.ecommerce.category.CategoryRepository;

import io.micrometer.common.lang.Nullable;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Service
@Validated
@Transactional
public class ProductService {

    private final ProductRepository productRepository;   // Dependency Injection of ProductRepository
    private final CategoryRepository categoryRepository;                     // Missing initialization
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }
    
    /*
     * Create Product method
     * 
     * 
     * 
     */
    @Transactional
    public Product create(@Valid CreateProductCommand cmd) {

        //SKU is unique
        if(productRepository.existsBySku(cmd.sku())){
            throw new DuplicateSkuException(cmd.sku());
        }

        Category category = findCategoryOrThrow(cmd.categoryId());

        Product product = Product.builder()
                    .name(cmd.name())
                    .sku(cmd.sku())
                    .description(cmd.description())
                    .price(cmd.price())
                    .stock(cmd.stock())
                    .category(category)
                    .build();

        return productRepository.save(product);
    }

    /*
     * Read product method
     */
    public Product getById(@NotNull long id){
        return productRepository.findById(id).orElseThrow(()-> new ProductNotFoundException(id));

    }

    public Optional<Product> getBySku(@NotNull String sku){
        return productRepository.findBySku(sku);
    }

    public Page<Product> list(@Nullable String nameContains, Pageable pageable){
        if (nameContains == null || nameContains.isBlank()){
            return productRepository.findAll(pageable);

        }
        return productRepository.findByNameContainingIgnoreCase(nameContains.trim(),pageable)
        
    }
    @Transactional
    public Product update(@NotNull long id, @Valid UpdateProductCommand cmd){

        Product existing = productRepository.findByIdForUpdate(id)
            .orElseThrow(()-> new ProductNotFoundException(id));
        if (!existing.getSku().equals(cmd.sku()) && productRepository.existsBySku(cmd.sku())){
             throw new DuplicateSkuException(cmd.sku());
        }

        Category category = findCategoryOrThrow(cmd.categoryId());

        existing.setName(cmd.name());
        existing.setSku(cmd.sku());
        existing.setDescription(cmd.description());
        existing.setPrice(cmd.price());
        existing.setStock(cmd.stock());
        existing.setCategory(category);

        return productRepository.save(existing);
    }
     @Transactional
    public Product adjustStock(@NotNull Long id, int delta) {
        Product p = productRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        int newStock = p.getStock() + delta;
        if (newStock < 0) {
            throw new IllegalArgumentException(
                    "Insufficient stock for product %d (sku=%s). Requested delta=%d, available=%d"
                            .formatted(id, p.getSku(), delta, p.getStock()));
        }
        p.setStock(newStock);
        return productRepository.save(p);
    }

    @Transactional
    public Product setStock(@NotNull Long id, @Min(0) int newStock) {
        Product p = productRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        p.setStock(newStock);
        return productRepository.save(p);
    }

    /* ===========================
       Deletion
       =========================== */

    @Transactional
    public void delete(@NotNull Long id) {
        try {
            productRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new ProductNotFoundException(id);
        }
    }

    /* ===========================
       Helpers
       =========================== */

    private com.test.ecommerce.category.Category findCategoryOrThrow(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));
    }

    /* ===========================
       Command DTOs
       =========================== */

    // Keep commands small and validation-centric; controllers map JSON -> these records.
    public record CreateProductCommand(
            @NotNull String name,
            @NotNull String sku,
            @Nullable String description,
            @NotNull BigDecimal price,
            @NotNull @Min(0) Integer stock,
            @NotNull Long categoryId
    ) {}

    public record UpdateProductCommand(
            @NotNull String name,
            @NotNull String sku,
            @Nullable String description,
            @NotNull BigDecimal price,
            @NotNull @Min(0) Integer stock,
            @NotNull Long categoryId
    ) {}

    /* ===========================
       Exceptions
       =========================== */

    public static class ProductNotFoundException extends RuntimeException {
        public ProductNotFoundException(Long id) {
            super("Product not found: id=" + id);
        }
    }

    public static class DuplicateSkuException extends RuntimeException {
        public DuplicateSkuException(String sku) {
            super("SKU already exists: " + sku);
        }
    }

    public static class CategoryNotFoundException extends RuntimeException {
        public CategoryNotFoundException(Long id) {
            super("Category not found: id=" + id);
        }
    }
}

