package com.hendisantika.ecommerce.springbootecommerce.repository;

import com.hendisantika.ecommerce.springbootecommerce.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * Created by IntelliJ IDEA.
 * Project : springboot-ecommerce
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 2025-10-20
 * Time: 10:05
 */
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    // Find by slug
    Optional<Category> findBySlug(String slug);
    
    // Check if slug exists
    boolean existsBySlug(String slug);
    
    // Find root categories (top level, no parent)
    @Query("SELECT c FROM Category c WHERE c.parent IS NULL")
    List<Category> findRootCategories();
    
    // Find children by parent ID
    List<Category> findByParentId(Long parentId);
    
    // Find categories by parent
    List<Category> findByParent(Category parent);
}
