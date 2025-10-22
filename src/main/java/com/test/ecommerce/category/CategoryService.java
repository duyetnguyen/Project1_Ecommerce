package com.test.ecommerce.category;

import com.test.ecommerce.product.ProductRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public CategoryService(CategoryRepository categoryRepository,
                           ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public Category create(@Valid CreateCategoryRequest req) {
        if (categoryRepository.existsByNameIgnoreCase(req.name())) {
            throw new DuplicateCategoryNameException(req.name());
        }
        Category c = new Category(null, req.name(), req.description());
        return categoryRepository.save(c);
    }

    public Category getById(@NotNull Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
    }

    @Transactional
    public Category update(@NotNull Long id, @Valid UpdateCategoryRequest req) {
        Category c = getById(id);
        if (!c.getName().equalsIgnoreCase(req.name())
                && categoryRepository.existsByNameIgnoreCase(req.name())) {
            throw new DuplicateCategoryNameException(req.name());
        }
        c.setName(req.name());
        c.setDescription(req.description());
        return categoryRepository.save(c);
    }

    @Transactional
    public void delete(@NotNull Long id) {
        // block deletion if products exist in this category
        if (productRepository.existsByCategoryId(id)) {
            throw new CategoryInUseException(id);
        }
        categoryRepository.deleteById(id);
    }

    /* ===== Requests ===== */
    public record CreateCategoryRequest(@NotBlank String name, String description) {}
    public record UpdateCategoryRequest(@NotBlank String name, String description) {}

    /* ===== Exceptions ===== */
    public static final class CategoryNotFoundException extends RuntimeException {
        public CategoryNotFoundException(Long id){ super("Category not found: id=" + id); }
    }
    public static final class DuplicateCategoryNameException extends RuntimeException {
        public DuplicateCategoryNameException(String name){ super("Category name exists: " + name); }
    }
    public static final class CategoryInUseException extends RuntimeException {
        public CategoryInUseException(Long id){ super("Category in use by products: id=" + id); }
    }
}
