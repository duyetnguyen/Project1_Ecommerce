package com.test.ecommerce.category;

import com.test.ecommerce.category.Category;
import com.test.ecommerce.category.CategoryService;
import com.test.ecommerce.category.CategoryService.CategoryInUseException;
import com.test.ecommerce.category.CategoryService.CategoryNotFoundException;
import com.test.ecommerce.category.CategoryService.DuplicateCategoryNameException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final CategoryRepository categoryRepository;

    @PostMapping
    public ResponseEntity<Category> create(@Valid @RequestBody CategoryService.CreateCategoryRequest req) {
        Category c = categoryService.create(req);
        return ResponseEntity.created(URI.create("/api/categories/" + c.getId())).body(c);
    }

    @GetMapping("/{id}")
    public Category get(@PathVariable Long id) {
        return categoryService.getById(id);
    }

    @GetMapping
    public Page<Category> list(Pageable pageable) {
        
       
        return categoryRepository.findAll(pageable);
        
    }

    @PutMapping("/{id}")
    public Category update(@PathVariable Long id,
                           @Valid @RequestBody CategoryService.UpdateCategoryRequest req) {
        return categoryService.update(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        categoryService.delete(id);
    }

    /* --- simple exception mapping --- */
    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<Void> notFound() { return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); }

    @ExceptionHandler(DuplicateCategoryNameException.class)
    public ResponseEntity<Map<String,String>> conflict(DuplicateCategoryNameException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error","conflict","message",ex.getMessage()));
    }

    @ExceptionHandler(CategoryInUseException.class)
    public ResponseEntity<Map<String,String>> inUse(CategoryInUseException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error","category_in_use","message",ex.getMessage()));
    }
}
