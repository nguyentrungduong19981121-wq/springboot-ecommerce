package com.hendisantika.ecommerce.springbootecommerce.service;

import com.hendisantika.ecommerce.springbootecommerce.dto.InventoryDto;
import com.hendisantika.ecommerce.springbootecommerce.dto.UpdateInventoryRequest;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Project : springboot-ecommerce
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 2025-10-20
 * Time: 18:20
 */
public interface InventoryService {
    
    /**
     * Get all products with their current stock levels
     */
    List<InventoryDto> getAllInventory();
    
    /**
     * Update inventory for a product
     */
    InventoryDto updateInventory(Long productId, UpdateInventoryRequest request);
    
    /**
     * Get inventory details for a specific product
     */
    InventoryDto getInventoryByProductId(Long productId);
}
