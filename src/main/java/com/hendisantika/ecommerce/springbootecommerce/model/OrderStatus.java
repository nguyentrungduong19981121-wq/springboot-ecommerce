package com.hendisantika.ecommerce.springbootecommerce.model;

/**
 * Created by IntelliJ IDEA.
 * Project : springboot-ecommerce
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 2019-04-26
 * Time: 06:05
 */
public enum OrderStatus {
    PENDING,    // Order created, awaiting payment
    PAID,       // Payment received
    SHIPPED,    // Order shipped to customer
    DELIVERED,  // Order delivered
    CANCELLED   // Order cancelled
}
