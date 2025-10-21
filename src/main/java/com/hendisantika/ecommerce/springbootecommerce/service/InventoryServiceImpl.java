package com.hendisantika.ecommerce.springbootecommerce.service;

import com.hendisantika.ecommerce.springbootecommerce.dto.InventoryDto;
import com.hendisantika.ecommerce.springbootecommerce.dto.UpdateInventoryRequest;
import com.hendisantika.ecommerce.springbootecommerce.exception.ResourceNotFoundException;
import com.hendisantika.ecommerce.springbootecommerce.model.Inventory;
import com.hendisantika.ecommerce.springbootecommerce.model.InventoryTransactionType;
import com.hendisantika.ecommerce.springbootecommerce.model.Product;
import com.hendisantika.ecommerce.springbootecommerce.repository.InventoryRepository;
import com.hendisantika.ecommerce.springbootecommerce.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by IntelliJ IDEA.
 * Project : springboot-ecommerce
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 2025-10-20
 * Time: 18:25
 */
@Service
@Transactional
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;

    public InventoryServiceImpl(InventoryRepository inventoryRepository, ProductRepository productRepository) {
        this.inventoryRepository = inventoryRepository;
        this.productRepository = productRepository;
    }

    @Override
    public List<InventoryDto> getAllInventory() {
        List<Product> products = productRepository.findAll();
        return products.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public InventoryDto updateInventory(Long productId, UpdateInventoryRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        Integer currentStock = product.getStock() != null ? product.getStock() : 0;
        Integer newStock;

        // Calculate new stock based on transaction type
        switch (request.getTransactionType()) {
            case IMPORT:
            case RETURN:
                newStock = currentStock + request.getQuantity();
                break;
            case EXPORT:
            case SALE:
                newStock = currentStock - request.getQuantity();
                if (newStock < 0) {
                    throw new IllegalArgumentException("Insufficient stock. Current: " + currentStock + 
                            ", Requested: " + request.getQuantity());
                }
                break;
            case ADJUSTMENT:
                newStock = request.getQuantity(); // Direct set
                break;
            default:
                throw new IllegalArgumentException("Invalid transaction type");
        }

        // Update product stock
        product.setStock(newStock);
        productRepository.save(product);

        // Create inventory transaction record
        Inventory inventory = new Inventory(
                product,
                request.getQuantity(),
                request.getTransactionType(),
                request.getReason()
        );
        inventoryRepository.save(inventory);

        return convertToDto(product);
    }

    @Override
    public InventoryDto getInventoryByProductId(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
        return convertToDto(product);
    }

    private InventoryDto convertToDto(Product product) {
        return new InventoryDto(
                product.getId(),
                product.getName(),
                product.getSlug(),
                product.getStock() != null ? product.getStock() : 0,
                product.getPrice()
        );
    }
}
