package com.hendisantika.ecommerce.springbootecommerce.repository;

import com.hendisantika.ecommerce.springbootecommerce.model.Order;
import com.hendisantika.ecommerce.springbootecommerce.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Project : springboot-ecommerce
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 2019-04-26
 * Time: 06:12
 */
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    @Query("SELECT COUNT(o) FROM Order o")
    Long countAllOrders();
    
    @Query("SELECT o FROM Order o WHERE FUNCTION('DATE', o.createdAt) >= :startDate AND FUNCTION('DATE', o.createdAt) <= :endDate")
    List<Order> findOrdersBetweenDates(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    // Find orders by customer
    List<Order> findByCustomerId(Long customerId);
    
    // Find orders by status
    List<Order> findByStatus(OrderStatus status);
    
    // Find orders by customer and status
    List<Order> findByCustomerIdAndStatus(Long customerId, OrderStatus status);
    
    // Find all orders ordered by creation date descending
    List<Order> findAllByOrderByCreatedAtDesc();
}