package com.hendisantika.ecommerce.springbootecommerce.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Project : springboot-ecommerce
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 2025-10-20
 * Time: 18:00
 */
public class CheckoutRequest {

    @NotEmpty(message = "Items are required")
    private List<CheckoutItemRequest> items;

    private String couponCode;

    public CheckoutRequest() {
    }

    public CheckoutRequest(List<CheckoutItemRequest> items, String couponCode) {
        this.items = items;
        this.couponCode = couponCode;
    }

    public List<CheckoutItemRequest> getItems() {
        return items;
    }

    public void setItems(List<CheckoutItemRequest> items) {
        this.items = items;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public static class CheckoutItemRequest {
        @NotNull(message = "Product ID is required")
        private Long productId;

        @NotNull(message = "Quantity is required")
        private Integer quantity;

        public CheckoutItemRequest() {
        }

        public CheckoutItemRequest(Long productId, Integer quantity) {
            this.productId = productId;
            this.quantity = quantity;
        }

        public Long getProductId() {
            return productId;
        }

        public void setProductId(Long productId) {
            this.productId = productId;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }
    }
}
