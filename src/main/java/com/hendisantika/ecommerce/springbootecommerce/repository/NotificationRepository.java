package com.hendisantika.ecommerce.springbootecommerce.repository;

import com.hendisantika.ecommerce.springbootecommerce.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipientEmail(String email);
}
