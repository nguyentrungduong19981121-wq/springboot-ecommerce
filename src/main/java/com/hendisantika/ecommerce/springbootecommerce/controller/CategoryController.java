package com.hendisantika.ecommerce.springbootecommerce.controller;

import com.hendisantika.ecommerce.springbootecommerce.dto.CategoryDto;
import com.hendisantika.ecommerce.springbootecommerce.dto.CreateCategoryRequest;
import com.hendisantika.ecommerce.springbootecommerce.dto.UpdateCategoryRequest;
import com.hendisantika.ecommerce.springbootecommerce.service.CategoryService;
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
 * Date: 2025-10-20
 * Time: 11:30
 */
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * GET /api/categories
     * Returns all categories in flat list or hierarchical tree
     * 
     * @param tree If true, returns hierarchical tree structure, otherwise flat list
     * @return List of categories
     */
    @GetMapping
    public ResponseEntity<List<CategoryDto>> getCategories(
            @RequestParam(required = false, defaultValue = "false") boolean tree) {
        
        List<CategoryDto> categories = tree ? 
                categoryService.getCategoryTree() : 
                categoryService.getAllCategories();
        
        return ResponseEntity.ok(categories);
    }

    /**
     * GET /api/categories/{id}
     * Get a specific category by ID with its children
     * 
     * @param id Category ID
     * @return Category details with nested children
     */
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> getCategory(@PathVariable Long id) {
        CategoryDto category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(category);
    }

    /**
     * GET /api/categories/slug/{slug}
     * Get a specific category by slug with its children
     * 
     * @param slug Category slug
     * @return Category details with nested children
     */
    @GetMapping("/slug/{slug}")
    public ResponseEntity<CategoryDto> getCategoryBySlug(@PathVariable String slug) {
        CategoryDto category = categoryService.getCategoryBySlug(slug);
        return ResponseEntity.ok(category);
    }

    /**
     * GET /api/categories/{id}/children
     * Get direct children of a category
     * 
     * @param id Parent category ID
     * @return List of child categories
     */
    @GetMapping("/{id}/children")
    public ResponseEntity<List<CategoryDto>> getCategoryChildren(@PathVariable Long id) {
        List<CategoryDto> children = categoryService.getChildren(id);
        return ResponseEntity.ok(children);
    }

    /**
     * POST /api/categories
     * Create a new category
     * 
     * @param request Category creation request
     * @return Created category
     */
    @PostMapping
    public ResponseEntity<CategoryDto> createCategory(@Valid @RequestBody CreateCategoryRequest request) {
        CategoryDto createdCategory = categoryService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCategory);
    }

    /**
     * PUT /api/categories/{id}
     * Update an existing category
     * 
     * @param id Category ID
     * @param request Category update request
     * @return Updated category
     */
    @PutMapping("/{id}")
    public ResponseEntity<CategoryDto> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCategoryRequest request) {
        CategoryDto updatedCategory = categoryService.updateCategory(id, request);
        return ResponseEntity.ok(updatedCategory);
    }

    /**
     * DELETE /api/categories/{id}
     * Delete a category (cascade delete all children)
     * 
     * @param id Category ID
     * @return No content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
