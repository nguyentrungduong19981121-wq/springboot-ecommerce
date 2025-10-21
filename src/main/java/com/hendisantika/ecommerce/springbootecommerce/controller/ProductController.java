package com.hendisantika.ecommerce.springbootecommerce.controller;

import com.hendisantika.ecommerce.springbootecommerce.dto.CreateProductRequest;
import com.hendisantika.ecommerce.springbootecommerce.dto.ProductDto;
import com.hendisantika.ecommerce.springbootecommerce.dto.UpdateProductRequest;
import com.hendisantika.ecommerce.springbootecommerce.model.Product;
import com.hendisantika.ecommerce.springbootecommerce.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Project : springboot-ecommerce
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 2019-04-26
 * Time: 06:21
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * GET /api/products (legacy - for backward compatibility)
     * Returns all products in original format
     */
    @GetMapping(value = {"/legacy"})
    public ResponseEntity<Iterable<Product>> getProductsLegacy() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    /**
     * GET /api/products
     * Returns all products with optional search, filter, and sort
     * 
     * @param name Search by product name (optional)
     * @param categoryId Filter by category ID (optional)
     * @param sortBy Sort by price: "price_asc" or "price_desc" (optional)
     * @return List of products
     */
    @GetMapping
    public ResponseEntity<List<ProductDto>> getProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String sortBy) {
        
        // If any filter/search parameter is provided, use search
        if (name != null || categoryId != null || sortBy != null) {
            List<ProductDto> products = productService.searchProducts(name, categoryId, sortBy);
            return ResponseEntity.ok(products);
        }
        
        // Otherwise return all products
        List<ProductDto> products = productService.getAllProductsDto();
        return ResponseEntity.ok(products);
    }

    /**
     * GET /api/products/{id}
     * Get a specific product by ID
     * 
     * @param id Product ID
     * @return Product details
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable Long id) {
        ProductDto product = productService.getProductDto(id);
        return ResponseEntity.ok(product);
    }

    /**
     * POST /api/products
     * Create a new product
     * 
     * @param request Product creation request
     * @return Created product
     */
    @PostMapping
    public ResponseEntity<ProductDto> createProduct(@Valid @RequestBody CreateProductRequest request) {
        ProductDto createdProduct = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    /**
     * PUT /api/products/{id}
     * Update an existing product
     * 
     * @param id Product ID
     * @param request Product update request
     * @return Updated product
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductRequest request) {
        ProductDto updatedProduct = productService.updateProduct(id, request);
        return ResponseEntity.ok(updatedProduct);
    }

    /**
     * DELETE /api/products/{id}
     * Delete a product
     * 
     * @param id Product ID
     * @return No content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
