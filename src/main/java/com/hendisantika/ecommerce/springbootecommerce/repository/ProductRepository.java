package com.hendisantika.ecommerce.springbootecommerce.repository;

import com.hendisantika.ecommerce.springbootecommerce.model.Product;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Project : springboot-ecommerce
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 2019-04-26
 * Time: 06:13
 */
public interface ProductRepository extends CrudRepository<Product, Long> {
    
    @Query("SELECT op.pk.product.id, op.pk.product.name, SUM(op.quantity), SUM(op.quantity * op.pk.product.price) " +
           "FROM OrderProduct op " +
           "GROUP BY op.pk.product.id, op.pk.product.name " +
           "ORDER BY SUM(op.quantity) DESC")
    List<Object[]> findBestSellingProducts();
}