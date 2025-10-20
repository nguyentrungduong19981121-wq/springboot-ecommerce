package com.hendisantika.ecommerce.springbootecommerce.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Created by IntelliJ IDEA.
 * Project : springboot-ecommerce
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 2025-10-20
 * Time: 11:05
 */
public class CreateCategoryRequest {

    @NotBlank(message = "Category name is required.")
    private String name;

    @NotBlank(message = "Category slug is required.")
    private String slug;

    private String description;

    private Long parentId;

    public CreateCategoryRequest() {
    }

    public CreateCategoryRequest(String name, String slug, String description, Long parentId) {
        this.name = name;
        this.slug = slug;
        this.description = description;
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }
}
