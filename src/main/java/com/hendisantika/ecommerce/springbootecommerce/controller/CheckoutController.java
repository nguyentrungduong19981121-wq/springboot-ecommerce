package com.hendisantika.ecommerce.springbootecommerce.controller;

import com.hendisantika.ecommerce.springbootecommerce.dto.ApplyCouponRequest;
import com.hendisantika.ecommerce.springbootecommerce.dto.CheckoutRequest;
import com.hendisantika.ecommerce.springbootecommerce.dto.CheckoutResponse;
import com.hendisantika.ecommerce.springbootecommerce.dto.PaymentRequest;
import com.hendisantika.ecommerce.springbootecommerce.service.CheckoutService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by IntelliJ IDEA.
 * Project : springboot-ecommerce
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 2025-10-20
 * Time: 19:05
 */
@RestController
@RequestMapping("/api/checkout")
public class CheckoutController {

    private final CheckoutService checkoutService;

    public CheckoutController(CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }

    /**
     * POST /api/checkout
     * Create checkout session with items
     * 
     * @param request Checkout request with items
     * @return Checkout session details
     */
    @PostMapping
    public ResponseEntity<CheckoutResponse> createCheckoutSession(@Valid @RequestBody CheckoutRequest request) {
        String email = getCurrentUserEmail();
        CheckoutResponse response = checkoutService.createCheckoutSession(request, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * POST /api/checkout/apply-coupon
     * Apply coupon code to checkout session
     * 
     * @param request Apply coupon request
     * @return Updated checkout session with discount
     */
    @PostMapping("/apply-coupon")
    public ResponseEntity<CheckoutResponse> applyCoupon(@Valid @RequestBody ApplyCouponRequest request) {
        String email = getCurrentUserEmail();
        CheckoutResponse response = checkoutService.applyCoupon(request, email);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/checkout/payment
     * Process payment and create order
     * 
     * @param request Payment request
     * @return Completed checkout with order details
     */
    @PostMapping("/payment")
    public ResponseEntity<CheckoutResponse> processPayment(@Valid @RequestBody PaymentRequest request) {
        String email = getCurrentUserEmail();
        CheckoutResponse response = checkoutService.processPayment(request, email);
        return ResponseEntity.ok(response);
    }

    /**
     * Get current authenticated user's email
     */
    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new SecurityException("User not authenticated");
        }
        return authentication.getName();
    }
}
