package com.hendisantika.ecommerce.springbootecommerce.service;

import com.hendisantika.ecommerce.springbootecommerce.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Created by IntelliJ IDEA.
 * Project : springboot-ecommerce
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 2025-10-20
 * Time: 18:35
 * 
 * Mock email service - In production, integrate with real email provider
 */
@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Override
    public void sendOrderConfirmation(Order order) {
        // Mock implementation - logs instead of sending actual email
        logger.info("==========================================");
        logger.info("📧 SENDING ORDER CONFIRMATION EMAIL");
        logger.info("==========================================");
        logger.info("To: {}", order.getCustomer().getEmail());
        logger.info("Subject: Order Confirmation - Order #{}", order.getId());
        logger.info("------------------------------------------");
        logger.info("Dear {},", order.getCustomer().getName());
        logger.info("");
        logger.info("Thank you for your order!");
        logger.info("Order ID: #{}", order.getId());
        logger.info("Order Date: {}", order.getCreatedAt());
        logger.info("Order Status: {}", order.getStatus());
        logger.info("Total Amount: ${}", order.getTotalPrice());
        logger.info("");
        logger.info("Items:");
        order.getOrderProducts().forEach(op -> {
            logger.info("  - {} x {} = ${}", 
                    op.getProduct().getName(), 
                    op.getQuantity(), 
                    op.getTotalPrice());
        });
        logger.info("");
        logger.info("We will process your order shortly.");
        logger.info("==========================================");
    }

    @Override
    public void sendPaymentConfirmation(Order order) {
        // Mock implementation - logs instead of sending actual email
        logger.info("==========================================");
        logger.info("📧 SENDING PAYMENT CONFIRMATION EMAIL");
        logger.info("==========================================");
        logger.info("To: {}", order.getCustomer().getEmail());
        logger.info("Subject: Payment Confirmed - Order #{}", order.getId());
        logger.info("------------------------------------------");
        logger.info("Dear {},", order.getCustomer().getName());
        logger.info("");
        logger.info("Your payment has been successfully processed!");
        logger.info("Order ID: #{}", order.getId());
        logger.info("Payment Amount: ${}", order.getTotalPrice());
        logger.info("Payment Status: COMPLETED");
        logger.info("");
        logger.info("Your order will be shipped soon.");
        logger.info("==========================================");
    }
}
