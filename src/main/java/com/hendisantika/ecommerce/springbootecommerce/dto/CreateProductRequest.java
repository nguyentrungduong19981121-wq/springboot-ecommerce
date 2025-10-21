package com.hendisantika.ecommerce.springbootecommerce.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Project : springboot-ecommerce
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 2025-10-20
 * Time: 10:35
 */
public class CreateProductRequest {

    @NotBlank(message = "Product name is required.")
    private String name;

    @NotBlank(message = "Product slug is required.")
    private String slug;

    private String description;

    @NotNull(message = "Price is required.")
    @Min(value = 0, message = "Price must be non-negative.")
    private Double price;

    @Min(value = 0, message = "Stock must be non-negative.")
    private Integer stock;

    private String pictureUrl;

    private Long categoryId;

    private List<String> imageUrls;

    public CreateProductRequest() {
    }

    public CreateProductRequest(String name, String slug, String description, Double price, 
                                Integer stock, String pictureUrl, Long categoryId, List<String> imageUrls) {
        this.name = name;
        this.slug = slug;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.pictureUrl = pictureUrl;
        this.categoryId = categoryId;
        this.imageUrls = imageUrls;
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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }
}
