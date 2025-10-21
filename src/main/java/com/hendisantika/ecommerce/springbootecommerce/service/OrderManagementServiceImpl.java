package com.hendisantika.ecommerce.springbootecommerce.service;

import com.hendisantika.ecommerce.springbootecommerce.dto.CreateOrderRequest;
import com.hendisantika.ecommerce.springbootecommerce.dto.OrderDto;
import com.hendisantika.ecommerce.springbootecommerce.dto.OrderItemDto;
import com.hendisantika.ecommerce.springbootecommerce.dto.UpdateOrderStatusRequest;
import com.hendisantika.ecommerce.springbootecommerce.exception.ResourceNotFoundException;
import com.hendisantika.ecommerce.springbootecommerce.model.Customer;
import com.hendisantika.ecommerce.springbootecommerce.model.Order;
import com.hendisantika.ecommerce.springbootecommerce.model.OrderProduct;
import com.hendisantika.ecommerce.springbootecommerce.model.OrderStatus;
import com.hendisantika.ecommerce.springbootecommerce.model.Product;
import com.hendisantika.ecommerce.springbootecommerce.repository.CustomerRepository;
import com.hendisantika.ecommerce.springbootecommerce.repository.OrderRepository;
import com.hendisantika.ecommerce.springbootecommerce.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by IntelliJ IDEA.
 * Project : springboot-ecommerce
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 2025-10-20
 * Time: 16:25
 */
@Service
@Transactional
public class OrderManagementServiceImpl implements OrderManagementService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    public OrderManagementServiceImpl(OrderRepository orderRepository,
                                     CustomerRepository customerRepository,
                                     ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
    }

    @Override
    public List<OrderDto> getAllOrders(String email, boolean isAdmin) {
        List<Order> orders;

        if (isAdmin) {
            // Admin can see all orders
            orders = orderRepository.findAllByOrderByCreatedAtDesc();
        } else {
            // Customer can only see their own orders
            Customer customer = customerRepository.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
            orders = orderRepository.findByCustomerId(customer.getId());
        }

        return orders.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public OrderDto getOrderById(Long id, String email, boolean isAdmin) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        // Check authorization: admin can view all, customer can only view their own
        if (!isAdmin) {
            Customer customer = customerRepository.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
            
            if (!order.getCustomer().getId().equals(customer.getId())) {
                throw new SecurityException("You are not authorized to view this order");
            }
        }

        return convertToDto(order);
    }

    @Override
    public OrderDto createOrder(CreateOrderRequest request, String email) {
        // Get customer
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        // Create order
        Order order = new Order();
        order.setCustomer(customer);
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);

        // Save order first to get ID
        Order savedOrder = orderRepository.save(order);

        // Process order items and reduce stock
        for (CreateOrderRequest.OrderItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + itemRequest.getProductId()));

            // Check stock availability
            if (product.getStock() == null || product.getStock() < itemRequest.getQuantity()) {
                throw new IllegalArgumentException("Insufficient stock for product: " + product.getName() + 
                        ". Available: " + (product.getStock() != null ? product.getStock() : 0) + 
                        ", Requested: " + itemRequest.getQuantity());
            }

            // Reduce stock
            product.setStock(product.getStock() - itemRequest.getQuantity());
            productRepository.save(product);

            // Create order product
            OrderProduct orderProduct = new OrderProduct(savedOrder, product, itemRequest.getQuantity());
            savedOrder.getOrderProducts().add(orderProduct);
        }

        // Save order with products
        Order finalOrder = orderRepository.save(savedOrder);
        return convertToDto(finalOrder);
    }

    @Override
    public OrderDto updateOrderStatus(Long id, UpdateOrderStatusRequest request, String email, boolean isAdmin) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        // Authorization check
        if (!isAdmin) {
            Customer customer = customerRepository.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
            
            if (!order.getCustomer().getId().equals(customer.getId())) {
                throw new SecurityException("You are not authorized to update this order");
            }
            
            // Customer can only cancel their own pending/paid orders
            if (request.getStatus() != OrderStatus.CANCELLED) {
                throw new SecurityException("Customers can only cancel orders");
            }
            
            if (order.getStatus() == OrderStatus.SHIPPED || order.getStatus() == OrderStatus.DELIVERED) {
                throw new IllegalStateException("Cannot cancel order that is already shipped or delivered");
            }
        }

        // Update status
        order.setStatus(request.getStatus());
        Order updatedOrder = orderRepository.save(order);
        
        return convertToDto(updatedOrder);
    }

    @Override
    public List<OrderDto> getOrdersByStatus(OrderStatus status, String email, boolean isAdmin) {
        List<Order> orders;

        if (isAdmin) {
            // Admin can filter all orders by status
            orders = orderRepository.findByStatus(status);
        } else {
            // Customer can filter their own orders by status
            Customer customer = customerRepository.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
            orders = orderRepository.findByCustomerIdAndStatus(customer.getId(), status);
        }

        return orders.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private OrderDto convertToDto(Order order) {
        List<OrderItemDto> items = order.getOrderProducts().stream()
                .map(op -> new OrderItemDto(
                        op.getProduct().getId(),
                        op.getProduct().getName(),
                        op.getQuantity(),
                        op.getProduct().getPrice(),
                        op.getTotalPrice()
                ))
                .collect(Collectors.toList());

        return new OrderDto(
                order.getId(),
                order.getCustomer().getId(),
                order.getCustomer().getName(),
                order.getCustomer().getEmail(),
                order.getCreatedAt(),
                order.getStatus(),
                items,
                order.getTotalPrice()
        );
    }
}
