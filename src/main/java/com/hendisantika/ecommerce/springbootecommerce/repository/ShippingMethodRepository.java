package com.hendisantika.ecommerce.springbootecommerce.repository;

import com.hendisantika.ecommerce.springbootecommerce.model.ShippingMethod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShippingMethodRepository extends JpaRepository<ShippingMethod, Long> {
    List<ShippingMethod> findByActiveTrue();
}
