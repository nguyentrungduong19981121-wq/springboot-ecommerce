package com.hendisantika.ecommerce.springbootecommerce.repository;

import com.hendisantika.ecommerce.springbootecommerce.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Created by IntelliJ IDEA.
 * Project : springboot-ecommerce
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 2019-04-26
 * Time: 06:13
 */
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    @Query("SELECT op.pk.product.id, op.pk.product.name, SUM(op.quantity), SUM(op.quantity * op.pk.product.price) " +
           "FROM OrderProduct op " +
           "GROUP BY op.pk.product.id, op.pk.product.name " +
           "ORDER BY SUM(op.quantity) DESC")
    List<Object[]> findBestSellingProducts();
    
    // Find by slug (for validation and lookup)
    Optional<Product> findBySlug(String slug);
    
    boolean existsBySlug(String slug);
    
    // Search by name (case-insensitive)
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Product> searchByName(@Param("name") String name);
    
    // Filter by category
    List<Product> findByCategoryId(Long categoryId);
    
    // Count products by category
    Long countByCategoryId(Long categoryId);
    
    // Search by name and filter by category
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')) AND p.category.id = :categoryId")
    List<Product> searchByNameAndCategoryId(@Param("name") String name, @Param("categoryId") Long categoryId);
    
    // Sort by price ascending
    List<Product> findAllByOrderByPriceAsc();
    
    // Sort by price descending
    List<Product> findAllByOrderByPriceDesc();
    
    // Filter by category and sort by price ascending
    List<Product> findByCategoryIdOrderByPriceAsc(Long categoryId);
    
    // Filter by category and sort by price descending
    List<Product> findByCategoryIdOrderByPriceDesc(Long categoryId);
}