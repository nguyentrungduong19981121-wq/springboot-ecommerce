package com.hendisantika.ecommerce.springbootecommerce.repository;

import com.hendisantika.ecommerce.springbootecommerce.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByOrderId(Long orderId);
    Optional<Payment> findByTransactionId(String transactionId);
}
