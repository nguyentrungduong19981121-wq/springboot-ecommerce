package com.hendisantika.ecommerce.springbootecommerce.dto;

import java.time.LocalDateTime;

/**
 * Created by IntelliJ IDEA.
 * Project : springboot-ecommerce
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 2025-10-20
 * Time: 17:50
 */
public class InventoryDto {

    private Long productId;
    private String productName;
    private String productSlug;
    private Integer currentStock;
    private Double price;

    public InventoryDto() {
    }

    public InventoryDto(Long productId, String productName, String productSlug, Integer currentStock, Double price) {
        this.productId = productId;
        this.productName = productName;
        this.productSlug = productSlug;
        this.currentStock = currentStock;
        this.price = price;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductSlug() {
        return productSlug;
    }

    public void setProductSlug(String productSlug) {
        this.productSlug = productSlug;
    }

    public Integer getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(Integer currentStock) {
        this.currentStock = currentStock;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
