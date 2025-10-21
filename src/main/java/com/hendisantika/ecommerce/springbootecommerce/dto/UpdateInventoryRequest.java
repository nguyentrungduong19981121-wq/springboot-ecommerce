package com.hendisantika.ecommerce.springbootecommerce.dto;

import com.hendisantika.ecommerce.springbootecommerce.model.InventoryTransactionType;
import jakarta.validation.constraints.NotNull;

/**
 * Created by IntelliJ IDEA.
 * Project : springboot-ecommerce
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 2025-10-20
 * Time: 17:55
 */
public class UpdateInventoryRequest {

    @NotNull(message = "Quantity is required")
    private Integer quantity;

    @NotNull(message = "Transaction type is required")
    private InventoryTransactionType transactionType;

    private String reason;

    public UpdateInventoryRequest() {
    }

    public UpdateInventoryRequest(Integer quantity, InventoryTransactionType transactionType, String reason) {
        this.quantity = quantity;
        this.transactionType = transactionType;
        this.reason = reason;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public InventoryTransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(InventoryTransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
