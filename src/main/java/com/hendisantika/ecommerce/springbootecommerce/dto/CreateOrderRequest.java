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
 * Time: 16:10
 */
public class CreateOrderRequest {

    @NotEmpty(message = "Order must have at least one item")
    private List<OrderItemRequest> items;

    public CreateOrderRequest() {
    }

    public CreateOrderRequest(List<OrderItemRequest> items) {
        this.items = items;
    }

    public List<OrderItemRequest> getItems() {
        return items;
    }

    public void setItems(List<OrderItemRequest> items) {
        this.items = items;
    }

    public static class OrderItemRequest {
        @NotNull(message = "Product ID is required")
        private Long productId;

        @NotNull(message = "Quantity is required")
        private Integer quantity;

        public OrderItemRequest() {
        }

        public OrderItemRequest(Long productId, Integer quantity) {
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
