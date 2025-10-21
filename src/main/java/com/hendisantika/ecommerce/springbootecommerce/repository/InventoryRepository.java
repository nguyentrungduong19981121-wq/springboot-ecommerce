package com.hendisantika.ecommerce.springbootecommerce.repository;

import com.hendisantika.ecommerce.springbootecommerce.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Project : springboot-ecommerce
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 2025-10-20
 * Time: 17:35
 */
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    
    List<Inventory> findByProductIdOrderByCreatedAtDesc(Long productId);
    
    List<Inventory> findAllByOrderByCreatedAtDesc();
}
