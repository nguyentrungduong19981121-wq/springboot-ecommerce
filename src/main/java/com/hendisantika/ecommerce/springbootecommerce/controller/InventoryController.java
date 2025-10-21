package com.hendisantika.ecommerce.springbootecommerce.controller;

import com.hendisantika.ecommerce.springbootecommerce.dto.InventoryDto;
import com.hendisantika.ecommerce.springbootecommerce.dto.UpdateInventoryRequest;
import com.hendisantika.ecommerce.springbootecommerce.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Project : springboot-ecommerce
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 2025-10-20
 * Time: 19:00
 */
@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    /**
     * GET /api/inventory
     * Get all products with their current stock levels
     * 
     * @return List of inventory
     */
    @GetMapping
    public ResponseEntity<List<InventoryDto>> getAllInventory() {
        List<InventoryDto> inventory = inventoryService.getAllInventory();
        return ResponseEntity.ok(inventory);
    }

    /**
     * PUT /api/inventory/{productId}
     * Update inventory for a product
     * 
     * @param productId Product ID
     * @param request Update inventory request
     * @return Updated inventory
     */
    @PutMapping("/{productId}")
    public ResponseEntity<InventoryDto> updateInventory(
            @PathVariable Long productId,
            @Valid @RequestBody UpdateInventoryRequest request) {
        
        InventoryDto updatedInventory = inventoryService.updateInventory(productId, request);
        return ResponseEntity.ok(updatedInventory);
    }

    /**
     * GET /api/inventory/{productId}
     * Get inventory details for a specific product
     * 
     * @param productId Product ID
     * @return Inventory details
     */
    @GetMapping("/{productId}")
    public ResponseEntity<InventoryDto> getInventoryByProductId(@PathVariable Long productId) {
        InventoryDto inventory = inventoryService.getInventoryByProductId(productId);
        return ResponseEntity.ok(inventory);
    }
}
