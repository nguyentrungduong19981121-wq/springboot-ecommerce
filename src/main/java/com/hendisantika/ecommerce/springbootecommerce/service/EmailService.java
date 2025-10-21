package com.hendisantika.ecommerce.springbootecommerce.service;

import com.hendisantika.ecommerce.springbootecommerce.model.Order;

/**
 * Created by IntelliJ IDEA.
 * Project : springboot-ecommerce
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 2025-10-20
 * Time: 18:30
 */
public interface EmailService {
    
    /**
     * Send order confirmation email to customer
     */
    void sendOrderConfirmation(Order order);
    
    /**
     * Send payment confirmation email
     */
    void sendPaymentConfirmation(Order order);
}
