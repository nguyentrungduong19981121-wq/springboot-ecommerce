package com.hendisantika.ecommerce.springbootecommerce.repository;

import com.hendisantika.ecommerce.springbootecommerce.model.CheckoutSession;
import com.hendisantika.ecommerce.springbootecommerce.model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Project : springboot-ecommerce
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 2025-10-20
 * Time: 17:45
 */
public interface CheckoutSessionRepository extends JpaRepository<CheckoutSession, Long> {
    
    List<CheckoutSession> findByCustomerId(Long customerId);
    
    List<CheckoutSession> findByPaymentStatus(PaymentStatus paymentStatus);
}
