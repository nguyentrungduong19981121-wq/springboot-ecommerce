package com.hendisantika.ecommerce.springbootecommerce.service;

import com.hendisantika.ecommerce.springbootecommerce.dto.CategoryDto;
import com.hendisantika.ecommerce.springbootecommerce.dto.CreateCategoryRequest;
import com.hendisantika.ecommerce.springbootecommerce.dto.UpdateCategoryRequest;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Project : springboot-ecommerce
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 2025-10-20
 * Time: 11:15
 */
public interface CategoryService {
    
    /**
     * Get all categories (flat list)
     */
    List<CategoryDto> getAllCategories();
    
    /**
     * Get hierarchical category tree (root categories with nested children)
     */
    List<CategoryDto> getCategoryTree();
    
    /**
     * Get single category by ID
     */
    CategoryDto getCategoryById(Long id);
    
    /**
     * Get single category by slug
     */
    CategoryDto getCategoryBySlug(String slug);
    
    /**
     * Create new category
     */
    CategoryDto createCategory(CreateCategoryRequest request);
    
    /**
     * Update existing category
     */
    CategoryDto updateCategory(Long id, UpdateCategoryRequest request);
    
    /**
     * Delete category (cascade delete children)
     */
    void deleteCategory(Long id);
    
    /**
     * Get children of a category
     */
    List<CategoryDto> getChildren(Long parentId);
}
