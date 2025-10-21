package com.hendisantika.ecommerce.springbootecommerce.service;

import com.hendisantika.ecommerce.springbootecommerce.dto.CreateOrderRequest;
import com.hendisantika.ecommerce.springbootecommerce.dto.OrderDto;
import com.hendisantika.ecommerce.springbootecommerce.dto.UpdateOrderStatusRequest;
import com.hendisantika.ecommerce.springbootecommerce.model.OrderStatus;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Project : springboot-ecommerce
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 2025-10-20
 * Time: 16:20
 */
public interface OrderManagementService {
    
    /**
     * Get all orders (admin) or customer's orders
     */
    List<OrderDto> getAllOrders(String email, boolean isAdmin);
    
    /**
     * Get single order by ID
     */
    OrderDto getOrderById(Long id, String email, boolean isAdmin);
    
    /**
     * Create new order for current customer
     */
    OrderDto createOrder(CreateOrderRequest request, String email);
    
    /**
     * Update order status (admin or customer can cancel)
     */
    OrderDto updateOrderStatus(Long id, UpdateOrderStatusRequest request, String email, boolean isAdmin);
    
    /**
     * Get orders filtered by status
     */
    List<OrderDto> getOrdersByStatus(OrderStatus status, String email, boolean isAdmin);
}
