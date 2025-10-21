package com.hendisantika.ecommerce.springbootecommerce.service;

import com.hendisantika.ecommerce.springbootecommerce.dto.ApplyCouponRequest;
import com.hendisantika.ecommerce.springbootecommerce.dto.CheckoutRequest;
import com.hendisantika.ecommerce.springbootecommerce.dto.CheckoutResponse;
import com.hendisantika.ecommerce.springbootecommerce.dto.PaymentRequest;
import com.hendisantika.ecommerce.springbootecommerce.exception.ResourceNotFoundException;
import com.hendisantika.ecommerce.springbootecommerce.model.CheckoutItem;
import com.hendisantika.ecommerce.springbootecommerce.model.CheckoutSession;
import com.hendisantika.ecommerce.springbootecommerce.model.Coupon;
import com.hendisantika.ecommerce.springbootecommerce.model.Customer;
import com.hendisantika.ecommerce.springbootecommerce.model.DiscountType;
import com.hendisantika.ecommerce.springbootecommerce.model.Order;
import com.hendisantika.ecommerce.springbootecommerce.model.OrderProduct;
import com.hendisantika.ecommerce.springbootecommerce.model.OrderStatus;
import com.hendisantika.ecommerce.springbootecommerce.model.PaymentStatus;
import com.hendisantika.ecommerce.springbootecommerce.model.Product;
import com.hendisantika.ecommerce.springbootecommerce.repository.CheckoutSessionRepository;
import com.hendisantika.ecommerce.springbootecommerce.repository.CouponRepository;
import com.hendisantika.ecommerce.springbootecommerce.repository.CustomerRepository;
import com.hendisantika.ecommerce.springbootecommerce.repository.OrderRepository;
import com.hendisantika.ecommerce.springbootecommerce.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Project : springboot-ecommerce
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 2025-10-20
 * Time: 18:45
 */
@Service
@Transactional
public class CheckoutServiceImpl implements CheckoutService {

    private static final Logger logger = LoggerFactory.getLogger(CheckoutServiceImpl.class);

    private final CheckoutSessionRepository checkoutSessionRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final CouponRepository couponRepository;
    private final OrderRepository orderRepository;
    private final EmailService emailService;

    public CheckoutServiceImpl(CheckoutSessionRepository checkoutSessionRepository,
                              CustomerRepository customerRepository,
                              ProductRepository productRepository,
                              CouponRepository couponRepository,
                              OrderRepository orderRepository,
                              EmailService emailService) {
        this.checkoutSessionRepository = checkoutSessionRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.couponRepository = couponRepository;
        this.orderRepository = orderRepository;
        this.emailService = emailService;
    }

    @Override
    public CheckoutResponse createCheckoutSession(CheckoutRequest request, String email) {
        // Get customer
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        // Create checkout session
        CheckoutSession session = new CheckoutSession();
        session.setCustomer(customer);

        List<CheckoutItem> items = new ArrayList<>();
        double subtotal = 0.0;

        // Process each item
        for (CheckoutRequest.CheckoutItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + itemRequest.getProductId()));

            // Check stock availability
            if (product.getStock() == null || product.getStock() < itemRequest.getQuantity()) {
                throw new IllegalArgumentException("Insufficient stock for product: " + product.getName() +
                        ". Available: " + (product.getStock() != null ? product.getStock() : 0) +
                        ", Requested: " + itemRequest.getQuantity());
            }

            // Create checkout item
            CheckoutItem item = new CheckoutItem(
                    product.getId(),
                    product.getName(),
                    itemRequest.getQuantity(),
                    product.getPrice()
            );
            items.add(item);
            subtotal += item.getTotalPrice();
        }

        session.setItems(items);
        session.setSubtotal(subtotal);
        session.setDiscountAmount(0.0);
        session.setTotalPrice(subtotal);

        // Apply coupon if provided
        if (request.getCouponCode() != null && !request.getCouponCode().isEmpty()) {
            applyCouponToSession(session, request.getCouponCode());
        }

        // Save session
        CheckoutSession savedSession = checkoutSessionRepository.save(session);
        
        logger.info("Created checkout session {} for customer {}", savedSession.getId(), customer.getEmail());

        return convertToResponse(savedSession);
    }

    @Override
    public CheckoutResponse applyCoupon(ApplyCouponRequest request, String email) {
        // Get customer
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        // Get checkout session
        CheckoutSession session = checkoutSessionRepository.findById(request.getSessionId())
                .orElseThrow(() -> new ResourceNotFoundException("Checkout session not found"));

        // Verify session belongs to customer
        if (!session.getCustomer().getId().equals(customer.getId())) {
            throw new SecurityException("You are not authorized to access this checkout session");
        }

        // Check session not expired
        if (session.isExpired()) {
            throw new IllegalStateException("Checkout session has expired");
        }

        // Apply coupon
        applyCouponToSession(session, request.getCouponCode());

        // Save and return
        CheckoutSession updatedSession = checkoutSessionRepository.save(session);
        return convertToResponse(updatedSession);
    }

    @Override
    public CheckoutResponse processPayment(PaymentRequest request, String email) {
        // Get customer
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        // Get checkout session
        CheckoutSession session = checkoutSessionRepository.findById(request.getSessionId())
                .orElseThrow(() -> new ResourceNotFoundException("Checkout session not found"));

        // Verify session belongs to customer
        if (!session.getCustomer().getId().equals(customer.getId())) {
            throw new SecurityException("You are not authorized to access this checkout session");
        }

        // Check session not expired
        if (session.isExpired()) {
            throw new IllegalStateException("Checkout session has expired");
        }

        // Check session not already paid
        if (session.getPaymentStatus() == PaymentStatus.COMPLETED) {
            throw new IllegalStateException("This session has already been paid");
        }

        // Update payment status
        session.setPaymentStatus(PaymentStatus.PROCESSING);
        checkoutSessionRepository.save(session);

        try {
            // Simulate payment processing
            logger.info("Processing payment for session {} using method: {}", 
                    session.getId(), request.getPaymentMethod());
            
            // In production, integrate with real payment gateway here
            // For demo, we simulate successful payment
            Thread.sleep(1000); // Simulate processing time

            // Create order
            Order order = createOrderFromSession(session);

            // Reduce stock for each product
            for (CheckoutItem item : session.getItems()) {
                Product product = productRepository.findById(item.getProductId())
                        .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

                if (product.getStock() < item.getQuantity()) {
                    throw new IllegalArgumentException("Insufficient stock for product: " + product.getName());
                }

                product.setStock(product.getStock() - item.getQuantity());
                productRepository.save(product);
            }

            // Increment coupon usage if applied
            if (session.getAppliedCoupon() != null) {
                Coupon coupon = session.getAppliedCoupon();
                coupon.incrementUsage();
                couponRepository.save(coupon);
            }

            // Update session
            session.setPaymentStatus(PaymentStatus.COMPLETED);
            session.setOrder(order);
            CheckoutSession completedSession = checkoutSessionRepository.save(session);

            // Send confirmation emails
            emailService.sendOrderConfirmation(order);
            emailService.sendPaymentConfirmation(order);

            logger.info("Payment successful! Order #{} created for customer {}", 
                    order.getId(), customer.getEmail());

            return convertToResponse(completedSession);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            session.setPaymentStatus(PaymentStatus.FAILED);
            checkoutSessionRepository.save(session);
            throw new RuntimeException("Payment processing interrupted", e);
        } catch (Exception e) {
            session.setPaymentStatus(PaymentStatus.FAILED);
            checkoutSessionRepository.save(session);
            logger.error("Payment failed for session {}: {}", session.getId(), e.getMessage());
            throw new RuntimeException("Payment failed: " + e.getMessage(), e);
        }
    }

    private void applyCouponToSession(CheckoutSession session, String couponCode) {
        Coupon coupon = couponRepository.findByCodeAndActiveTrue(couponCode)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid or inactive coupon code: " + couponCode));

        // Validate coupon
        if (!coupon.isValid()) {
            throw new IllegalArgumentException("Coupon is not valid or has expired");
        }

        // Check minimum order value
        if (coupon.getMinimumOrderValue() != null && session.getSubtotal() < coupon.getMinimumOrderValue()) {
            throw new IllegalArgumentException("Order subtotal must be at least $" + 
                    coupon.getMinimumOrderValue() + " to use this coupon");
        }

        // Calculate discount
        double discountAmount = 0.0;
        if (coupon.getDiscountType() == DiscountType.PERCENTAGE) {
            discountAmount = session.getSubtotal() * (coupon.getDiscountValue() / 100.0);
        } else if (coupon.getDiscountType() == DiscountType.FIXED_AMOUNT) {
            discountAmount = coupon.getDiscountValue();
        }

        // Ensure discount doesn't exceed subtotal
        discountAmount = Math.min(discountAmount, session.getSubtotal());

        // Apply discount
        session.setAppliedCoupon(coupon);
        session.setDiscountAmount(discountAmount);
        session.setTotalPrice(session.getSubtotal() - discountAmount);

        logger.info("Applied coupon {} to session {}. Discount: ${}", 
                couponCode, session.getId(), discountAmount);
    }

    private Order createOrderFromSession(CheckoutSession session) {
        Order order = new Order();
        order.setCustomer(session.getCustomer());
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus(OrderStatus.PAID);

        // Save order first to get ID
        Order savedOrder = orderRepository.save(order);

        // Create order products
        List<OrderProduct> orderProducts = new ArrayList<>();
        for (CheckoutItem item : session.getItems()) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

            OrderProduct orderProduct = new OrderProduct(savedOrder, product, item.getQuantity());
            orderProducts.add(orderProduct);
        }

        savedOrder.setOrderProducts(orderProducts);
        return orderRepository.save(savedOrder);
    }

    private CheckoutResponse convertToResponse(CheckoutSession session) {
        CheckoutResponse response = new CheckoutResponse();
        response.setSessionId(session.getId());
        response.setOrderId(session.getOrder() != null ? session.getOrder().getId() : null);
        response.setItems(session.getItems());
        response.setSubtotal(session.getSubtotal());
        response.setAppliedCouponCode(session.getAppliedCoupon() != null ? 
                session.getAppliedCoupon().getCode() : null);
        response.setDiscountAmount(session.getDiscountAmount());
        response.setTotalPrice(session.getTotalPrice());
        response.setPaymentStatus(session.getPaymentStatus());
        response.setCreatedAt(session.getCreatedAt());
        response.setExpiresAt(session.getExpiresAt());
        return response;
    }
}
