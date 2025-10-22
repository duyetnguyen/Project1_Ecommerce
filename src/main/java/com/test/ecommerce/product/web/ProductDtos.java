package com.test.ecommerce.product.web;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.test.ecommerce.product.Product;
import com.test.ecommerce.product.ProductService;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;


public class ProductDtos {

    private ProductDtos() {}

     @JsonPropertyOrder({ "name", "sku", "description", "price", "stock", "categoryId" })
    public record CreateProductRequest(
        @NotBlank String name,
        @NotBlank String sku,
        String description,
        @NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal price,
        @NotNull @Min(0) Integer stock,
        @NotNull Long categoryId
    ){}

    @JsonPropertyOrder({ "name", "sku", "description", "price", "stock", "categoryId"})
    public record UpdateProductRequest(
        @NotBlank @Size(max=100) String name,
        @NotBlank @Size(max = 50) String sku,
        @Size(max = 500) String description,
        @NotNull @DecimalMin (value = "0.0", inclusive = false) BigDecimal price,
        @NotNull @Min(0) Integer stock,
        @NotNull Long categoryId
    ){}

    public record AdjustStockRequest(int delta) {}

    public record SetStockRequest(@NotNull @Min(0) Integer stock){}



    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder({
            "id", "name", "sku", "description", "price", "stock",
            "categoryId", "categoryName"
    })
    public record ProductResponse(
            Long id,
            String name,
            String sku,
            String description,
            BigDecimal price,
            Integer stock,
            Long categoryId,
            String categoryName
    ) {}

    public static ProductService.CreateProductCommand toCreateCommand(CreateProductRequest r) {
        return new ProductService.CreateProductCommand(
                r.name(), r.sku(), r.description(), r.price(), r.stock(), r.categoryId()
        );
    }

    public static ProductService.UpdateProductCommand toUpdateCommand(UpdateProductRequest r) {
        return new ProductService.UpdateProductCommand(
                r.name(), r.sku(), r.description(), r.price(), r.stock(), r.categoryId()
        );
    }

     // entity -> response
    public static ProductResponse fromEntity(Product p) {
        var cat = p.getCategory();
        return new ProductResponse(
                p.getId(),
                p.getName(),
                p.getSku(),
                p.getDescription(),
                p.getPrice(),
                p.getStock(),
                cat != null ? cat.getId() : null,
                cat != null ? cat.getName() : null
        );
    }

}
