package com.hendisantika.ecommerce.springbootecommerce.service;

import com.hendisantika.ecommerce.springbootecommerce.dto.CategoryDto;
import com.hendisantika.ecommerce.springbootecommerce.dto.CreateCategoryRequest;
import com.hendisantika.ecommerce.springbootecommerce.dto.UpdateCategoryRequest;
import com.hendisantika.ecommerce.springbootecommerce.exception.ResourceNotFoundException;
import com.hendisantika.ecommerce.springbootecommerce.model.Category;
import com.hendisantika.ecommerce.springbootecommerce.repository.CategoryRepository;
import com.hendisantika.ecommerce.springbootecommerce.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by IntelliJ IDEA.
 * Project : springboot-ecommerce
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 2025-10-20
 * Time: 11:20
 */
@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    @Override
    public List<CategoryDto> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoryDto> getCategoryTree() {
        List<Category> rootCategories = categoryRepository.findRootCategories();
        return rootCategories.stream()
                .map(this::convertToDtoWithChildren)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        return convertToDtoWithChildren(category);
    }

    @Override
    public CategoryDto getCategoryBySlug(String slug) {
        Category category = categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with slug: " + slug));
        return convertToDtoWithChildren(category);
    }

    @Override
    public CategoryDto createCategory(CreateCategoryRequest request) {
        // Validate unique slug
        if (categoryRepository.existsBySlug(request.getSlug())) {
            throw new IllegalArgumentException("Category with slug '" + request.getSlug() + "' already exists");
        }

        Category category = new Category();
        category.setName(request.getName());
        category.setSlug(request.getSlug());
        category.setDescription(request.getDescription());

        // Set parent if provided
        if (request.getParentId() != null) {
            Category parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent category not found with id: " + request.getParentId()));
            category.setParent(parent);
        }

        Category savedCategory = categoryRepository.save(category);
        return convertToDto(savedCategory);
    }

    @Override
    public CategoryDto updateCategory(Long id, UpdateCategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        // Update name if provided
        if (request.getName() != null) {
            category.setName(request.getName());
        }

        // Update slug if provided and different
        if (request.getSlug() != null && !request.getSlug().equals(category.getSlug())) {
            // Validate unique slug
            if (categoryRepository.existsBySlug(request.getSlug())) {
                throw new IllegalArgumentException("Category with slug '" + request.getSlug() + "' already exists");
            }
            category.setSlug(request.getSlug());
        }

        // Update description if provided
        if (request.getDescription() != null) {
            category.setDescription(request.getDescription());
        }

        // Update parent if provided
        if (request.getParentId() != null) {
            // Prevent setting self as parent
            if (request.getParentId().equals(id)) {
                throw new IllegalArgumentException("Category cannot be its own parent");
            }

            Category newParent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent category not found with id: " + request.getParentId()));

            // Prevent circular reference (new parent cannot be a descendant of this category)
            if (isDescendant(category, newParent)) {
                throw new IllegalArgumentException("Cannot set a descendant category as parent (circular reference)");
            }

            category.setParent(newParent);
        }

        Category updatedCategory = categoryRepository.save(category);
        return convertToDto(updatedCategory);
    }

    @Override
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        
        // Cascade delete is handled by JPA with CascadeType.ALL and orphanRemoval = true
        categoryRepository.delete(category);
    }

    @Override
    public List<CategoryDto> getChildren(Long parentId) {
        List<Category> children = categoryRepository.findByParentId(parentId);
        return children.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Convert Category to DTO without children (flat structure)
     */
    private CategoryDto convertToDto(Category category) {
        CategoryDto dto = new CategoryDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setSlug(category.getSlug());
        dto.setDescription(category.getDescription());

        if (category.getParent() != null) {
            dto.setParentId(category.getParent().getId());
            dto.setParentName(category.getParent().getName());
        }

        // Count products in this category
        Long productCount = productRepository.countByCategoryId(category.getId());
        dto.setProductCount(productCount != null ? productCount.intValue() : 0);

        // Don't include children in flat DTO to avoid deep recursion
        dto.setChildren(null);

        return dto;
    }

    /**
     * Convert Category to DTO with nested children (hierarchical structure)
     */
    private CategoryDto convertToDtoWithChildren(Category category) {
        CategoryDto dto = convertToDto(category);

        // Recursively convert children
        if (category.getChildren() != null && !category.getChildren().isEmpty()) {
            List<CategoryDto> childrenDtos = category.getChildren().stream()
                    .map(this::convertToDtoWithChildren)
                    .collect(Collectors.toList());
            dto.setChildren(childrenDtos);
        } else {
            dto.setChildren(new ArrayList<>());
        }

        return dto;
    }

    /**
     * Check if a category is a descendant of another category
     * Used to prevent circular references
     */
    private boolean isDescendant(Category ancestor, Category potentialDescendant) {
        Category current = potentialDescendant.getParent();
        while (current != null) {
            if (current.getId().equals(ancestor.getId())) {
                return true;
            }
            current = current.getParent();
        }
        return false;
    }
}
