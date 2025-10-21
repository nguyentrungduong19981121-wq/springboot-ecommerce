package com.hendisantika.ecommerce.springbootecommerce.service;

import com.hendisantika.ecommerce.springbootecommerce.dto.ApplyCouponRequest;
import com.hendisantika.ecommerce.springbootecommerce.dto.CheckoutRequest;
import com.hendisantika.ecommerce.springbootecommerce.dto.CheckoutResponse;
import com.hendisantika.ecommerce.springbootecommerce.dto.PaymentRequest;

/**
 * Created by IntelliJ IDEA.
 * Project : springboot-ecommerce
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 2025-10-20
 * Time: 18:40
 */
public interface CheckoutService {
    
    /**
     * Create checkout session with items
     */
    CheckoutResponse createCheckoutSession(CheckoutRequest request, String email);
    
    /**
     * Apply coupon code to checkout session
     */
    CheckoutResponse applyCoupon(ApplyCouponRequest request, String email);
    
    /**
     * Process payment and create order
     */
    CheckoutResponse processPayment(PaymentRequest request, String email);
}
