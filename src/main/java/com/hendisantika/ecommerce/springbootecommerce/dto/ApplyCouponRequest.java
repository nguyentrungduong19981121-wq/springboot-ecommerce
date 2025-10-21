package com.hendisantika.ecommerce.springbootecommerce.dto;

import jakarta.validation.constraints.NotNull;

/**
 * Created by IntelliJ IDEA.
 * Project : springboot-ecommerce
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 2025-10-20
 * Time: 18:10
 */
public class ApplyCouponRequest {

    @NotNull(message = "Session ID is required")
    private Long sessionId;

    @NotNull(message = "Coupon code is required")
    private String couponCode;

    public ApplyCouponRequest() {
    }

    public ApplyCouponRequest(Long sessionId, String couponCode) {
        this.sessionId = sessionId;
        this.couponCode = couponCode;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }
}
