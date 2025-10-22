package com.test.ecommerce.product;

import com.test.ecommerce.product.Product;
import com.test.ecommerce.product.ProductService;
import com.test.ecommerce.product.ProductService.CategoryNotFoundException;
import com.test.ecommerce.product.ProductService.DuplicateSkuException;
import com.test.ecommerce.product.ProductService.ProductNotFoundException;
import com.test.ecommerce.product.web.ProductDtos;
import com.test.ecommerce.product.web.ProductDtos.AdjustStockRequest;
import com.test.ecommerce.product.web.ProductDtos.SetStockRequest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor

public class ProductController {
    private final ProductService productService;


    /*
     * 
     * Create 
     */
    @PostMapping
    public ResponseEntity<ProductDtos.ProductResponse> create(
        @Valid @RequestBody ProductDtos.CreateProductRequest req){
            
            var cmd = new ProductService.CreateProductCommand(
                req.name(), req.sku(), req.description(), req.price(), req.stock(),req.categoryId());

            Product created = productService.create(cmd);
            
            var body = ProductDtos.fromEntity(created);
            var location = URI.create("/api/products" + created.getId());

            return ResponseEntity
                    .created(location)
                    .body(body);
        }
    
    @GetMapping("/{id}")
    public ProductDtos.ProductResponse getById(@PathVariable Long id) {
        return ProductDtos.fromEntity(productService.getById(id));
    }

    @GetMapping("/by-sku/{sku}")
    public ResponseEntity<ProductDtos.ProductResponse> getBySku(@PathVariable String sku) {
        return productService.getBySku(sku)
                .map(ProductDtos::fromEntity)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping
    public Page<ProductDtos.ProductResponse> list(
            @RequestParam(name = "q", required = false) String nameContains,
            Pageable pageable) {

        return productService.list(nameContains, pageable)
                .map(ProductDtos::fromEntity);
    }

    @PutMapping("/{id}")
    public ProductDtos.ProductResponse update(
            @PathVariable Long id,
            @Valid @RequestBody ProductDtos.UpdateProductRequest req) {

        var cmd = new ProductService.UpdateProductCommand(
                req.name(), req.sku(), req.description(), req.price(), req.stock(), req.categoryId());

        return ProductDtos.fromEntity(productService.update(id, cmd));
    }

    @PatchMapping("/{id}/stock/adjust")
    public ProductDtos.ProductResponse adjustStock(
            @PathVariable Long id,
            @RequestBody @Valid AdjustStockRequest req) {

        return ProductDtos.fromEntity(productService.adjustStock(id, req.delta()));
    }

    @PutMapping("/{id}/stock")
    public ProductDtos.ProductResponse setStock(
            @PathVariable Long id,
            @RequestBody @Valid SetStockRequest req) {

        return ProductDtos.fromEntity(productService.setStock(id, req.stock()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        productService.delete(id);
    }


    /* ===========================
       Exception Handling
       =========================== */

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<Void> handleNotFound(ProductNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @ExceptionHandler({DuplicateSkuException.class, DataIntegrityViolationException.class})
    public ResponseEntity<Map<String, Object>> handleConflict(RuntimeException ex) {
        Map<String, Object> body = Map.of(
                "error", "conflict",
                "message", ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleCategoryNotFound(CategoryNotFoundException ex) {
        Map<String, Object> body = Map.of(
                "error", "category_not_found",
                "message", ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(fe.getField(), fe.getDefaultMessage());
        }
        Map<String, Object> body = Map.of(
                "error", "validation_failed",
                "fields", fieldErrors
        );
        return ResponseEntity.badRequest().body(body);
    }

    /* ===========================
       Small request DTOs (local)
       =========================== */

    public record AdjustStockRequest(int delta) {}

    public record SetStockRequest(@NotNull @Min(0) Integer stock) {}

    
}
