package com.hendisantika.ecommerce.springbootecommerce.controller;

import com.hendisantika.ecommerce.springbootecommerce.model.*;
import com.hendisantika.ecommerce.springbootecommerce.repository.OrderRepository;
import com.hendisantika.ecommerce.springbootecommerce.repository.PaymentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
public class PaymentIntegrationController {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    public PaymentIntegrationController(PaymentRepository paymentRepository, OrderRepository orderRepository) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
    }

    @PostMapping("/paypal")
    public ResponseEntity<Map<String, Object>> processPayPal(@RequestBody Map<String, Object> request) {
        return processPayment(request, PaymentMethod.PAYPAL);
    }

    @PostMapping("/vnpay")
    public ResponseEntity<Map<String, Object>> processVNPay(@RequestBody Map<String, Object> request) {
        return processPayment(request, PaymentMethod.VNPAY);
    }

    @PostMapping("/momo")
    public ResponseEntity<Map<String, Object>> processMomo(@RequestBody Map<String, Object> request) {
        return processPayment(request, PaymentMethod.MOMO);
    }

    @PostMapping("/cod")
    public ResponseEntity<Map<String, Object>> processCOD(@RequestBody Map<String, Object> request) {
        return processPayment(request, PaymentMethod.COD);
    }

    private ResponseEntity<Map<String, Object>> processPayment(Map<String, Object> request, PaymentMethod method) {
        Long orderId = Long.valueOf(request.get("orderId").toString());
        Double amount = Double.valueOf(request.get("amount").toString());

        return orderRepository.findById(orderId)
                .map(order -> {
                    // Create payment record
                    Payment payment = new Payment();
                    payment.setOrder(order);
                    payment.setMethod(method);
                    payment.setAmount(amount);
                    payment.setTransactionId(UUID.randomUUID().toString());
                    
                    // Simulate payment processing
                    try {
                        if (method == PaymentMethod.COD) {
                            payment.setStatus(PaymentTransactionStatus.PENDING);
                        } else {
                            // Simulate success for demo
                            payment.setStatus(PaymentTransactionStatus.SUCCESS);
                            payment.setCompletedAt(LocalDateTime.now());
                            order.setStatus(OrderStatus.PAID);
                            orderRepository.save(order);
                        }
                    } catch (Exception e) {
                        payment.setStatus(PaymentTransactionStatus.FAILED);
                    }

                    Payment savedPayment = paymentRepository.save(payment);

                    Map<String, Object> response = new HashMap<>();
                    response.put("paymentId", savedPayment.getId());
                    response.put("orderId", orderId);
                    response.put("method", method.toString());
                    response.put("status", savedPayment.getStatus().toString());
                    response.put("transactionId", savedPayment.getTransactionId());
                    response.put("amount", amount);

                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<Map<String, Object>> getPaymentsByOrder(@PathVariable Long orderId) {
        var payments = paymentRepository.findByOrderId(orderId);
        Map<String, Object> response = new HashMap<>();
        response.put("orderId", orderId);
        response.put("payments", payments);
        return ResponseEntity.ok(response);
    }
}
