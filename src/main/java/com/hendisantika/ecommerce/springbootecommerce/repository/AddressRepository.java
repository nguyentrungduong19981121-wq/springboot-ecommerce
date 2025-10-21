package com.hendisantika.ecommerce.springbootecommerce.repository;

import com.hendisantika.ecommerce.springbootecommerce.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Project : springboot-ecommerce
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 2025-10-20
 * Time: 14:05
 */
public interface AddressRepository extends JpaRepository<Address, Long> {
    
    List<Address> findByCustomerId(Long customerId);
}
