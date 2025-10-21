package com.hendisantika.ecommerce.springbootecommerce.controller;

import com.hendisantika.ecommerce.springbootecommerce.dto.CreateOrderRequest;
import com.hendisantika.ecommerce.springbootecommerce.dto.OrderDto;
import com.hendisantika.ecommerce.springbootecommerce.dto.UpdateOrderStatusRequest;
import com.hendisantika.ecommerce.springbootecommerce.model.OrderStatus;
import com.hendisantika.ecommerce.springbootecommerce.service.OrderManagementService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Project : springboot-ecommerce
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 2025-10-20
 * Time: 16:30
 */
@RestController
@RequestMapping("/api/orders")
public class OrderManagementController {

    private final OrderManagementService orderManagementService;

    public OrderManagementController(OrderManagementService orderManagementService) {
        this.orderManagementService = orderManagementService;
    }

    /**
     * GET /api/orders
     * Get all orders (admin sees all, customer sees own orders)
     * 
     * @param status Optional filter by order status
     * @return List of orders
     */
    @GetMapping
    public ResponseEntity<List<OrderDto>> getOrders(
            @RequestParam(required = false) OrderStatus status) {
        
        String email = getCurrentUserEmail();
        boolean isAdmin = isAdmin();

        List<OrderDto> orders;
        if (status != null) {
            orders = orderManagementService.getOrdersByStatus(status, email, isAdmin);
        } else {
            orders = orderManagementService.getAllOrders(email, isAdmin);
        }

        return ResponseEntity.ok(orders);
    }

    /**
     * GET /api/orders/{id}
     * Get specific order details
     * 
     * @param id Order ID
     * @return Order details
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getOrder(@PathVariable Long id) {
        String email = getCurrentUserEmail();
        boolean isAdmin = isAdmin();
        
        OrderDto order = orderManagementService.getOrderById(id, email, isAdmin);
        return ResponseEntity.ok(order);
    }

    /**
     * POST /api/orders
     * Create new order for authenticated customer
     * 
     * @param request Order creation request with items
     * @return Created order
     */
    @PostMapping
    public ResponseEntity<OrderDto> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        String email = getCurrentUserEmail();
        OrderDto createdOrder = orderManagementService.createOrder(request, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
    }

    /**
     * PUT /api/orders/{id}/status
     * Update order status
     * - Admin can change to any status
     * - Customer can only cancel (PENDING/PAID -> CANCELLED)
     * 
     * @param id Order ID
     * @param request Status update request
     * @return Updated order
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<OrderDto> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        
        String email = getCurrentUserEmail();
        boolean isAdmin = isAdmin();
        
        OrderDto updatedOrder = orderManagementService.updateOrderStatus(id, request, email, isAdmin);
        return ResponseEntity.ok(updatedOrder);
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

    /**
     * Check if current user is admin
     */
    private boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && 
               authentication.getAuthorities().stream()
                       .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}
