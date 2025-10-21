package com.hendisantika.ecommerce.springbootecommerce.repository;

import com.hendisantika.ecommerce.springbootecommerce.model.ProductImage;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Project : springboot-ecommerce
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 2025-10-20
 * Time: 10:15
 */
public interface ProductImageRepository extends CrudRepository<ProductImage, Long> {
    
    List<ProductImage> findByProductId(Long productId);
    
    @Modifying
    @Query("DELETE FROM ProductImage pi WHERE pi.product.id = :productId")
    void deleteByProductId(@Param("productId") Long productId);
}
