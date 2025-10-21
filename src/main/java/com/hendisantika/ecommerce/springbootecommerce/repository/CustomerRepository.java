package com.hendisantika.ecommerce.springbootecommerce.repository;

import com.hendisantika.ecommerce.springbootecommerce.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Created by IntelliJ IDEA.
 * Project : springboot-ecommerce
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 2025-10-20
 * Time: 06:13
 */
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    
    @Query("SELECT COUNT(c) FROM Customer c WHERE c.registeredDate >= :startDate")
    Long countNewCustomersSince(@Param("startDate") LocalDate startDate);
    
    Optional<Customer> findByEmail(String email);
    
    boolean existsByEmail(String email);
}
