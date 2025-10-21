package com.hendisantika.ecommerce.springbootecommerce.model;

/**
 * Created by IntelliJ IDEA.
 * Project : springboot-ecommerce
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 2025-10-20
 * Time: 17:05
 */
public enum InventoryTransactionType {
    IMPORT,     // Adding stock
    EXPORT,     // Removing stock manually
    SALE,       // Stock reduced due to order
    RETURN,     // Stock increased due to order return
    ADJUSTMENT  // Manual adjustment
}
