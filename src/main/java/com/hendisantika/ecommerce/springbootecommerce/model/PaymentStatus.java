package com.hendisantika.ecommerce.springbootecommerce.model;

/**
 * Created by IntelliJ IDEA.
 * Project : springboot-ecommerce
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 2025-10-20
 * Time: 17:30
 */
public enum PaymentStatus {
    PENDING,    // Payment not yet attempted
    PROCESSING, // Payment in progress
    COMPLETED,  // Payment successful
    FAILED,     // Payment failed
    CANCELLED   // Payment cancelled
}
