package com.hendisantika.ecommerce.springbootecommerce.service;

import com.hendisantika.ecommerce.springbootecommerce.dto.CreateProductRequest;
import com.hendisantika.ecommerce.springbootecommerce.dto.ProductDto;
import com.hendisantika.ecommerce.springbootecommerce.dto.UpdateProductRequest;
import com.hendisantika.ecommerce.springbootecommerce.model.Product;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Project : springboot-ecommerce
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 2019-04-26
 * Time: 06:15
 */
@Validated
public interface ProductService {

    @NotNull
    Iterable<Product> getAllProducts();

    Product getProduct(@Min(value = 1L, message = "Invalid product ID.") long id);

    Product save(Product product);
    
    // New methods for Product Management
    List<ProductDto> getAllProductsDto();
    
    ProductDto getProductDto(@Min(value = 1L, message = "Invalid product ID.") Long id);
    
    ProductDto createProduct(CreateProductRequest request);
    
    ProductDto updateProduct(@Min(value = 1L, message = "Invalid product ID.") Long id, UpdateProductRequest request);
    
    void deleteProduct(@Min(value = 1L, message = "Invalid product ID.") Long id);
    
    List<ProductDto> searchProducts(String name, Long categoryId, String sortBy);
}
