package com.hendisantika.ecommerce.springbootecommerce.repository;

import com.hendisantika.ecommerce.springbootecommerce.model.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PageRepository extends JpaRepository<Page, Long> {
    Optional<Page> findBySlug(String slug);
    Optional<Page> findBySlugAndActiveTrue(String slug);
}
