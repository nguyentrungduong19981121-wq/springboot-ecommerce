package com.hendisantika.ecommerce.springbootecommerce.repository;

import com.hendisantika.ecommerce.springbootecommerce.model.Order;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
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
public interface OrderRepository extends CrudRepository<Order, Long> {
    
    @Query("SELECT COUNT(o) FROM Order o")
    Long countAllOrders();
    
    @Query("SELECT o FROM Order o WHERE o.dateCreated >= :startDate AND o.dateCreated <= :endDate")
    List<Order> findOrdersBetweenDates(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}