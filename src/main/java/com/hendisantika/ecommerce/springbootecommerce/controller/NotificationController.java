package com.hendisantika.ecommerce.springbootecommerce.controller;

import com.hendisantika.ecommerce.springbootecommerce.model.Notification;
import com.hendisantika.ecommerce.springbootecommerce.model.NotificationStatus;
import com.hendisantika.ecommerce.springbootecommerce.model.NotificationType;
import com.hendisantika.ecommerce.springbootecommerce.repository.NotificationRepository;
import com.hendisantika.ecommerce.springbootecommerce.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);

    private final NotificationRepository notificationRepository;
    private final EmailService emailService;

    public NotificationController(NotificationRepository notificationRepository, EmailService emailService) {
        this.notificationRepository = notificationRepository;
        this.emailService = emailService;
    }

    @PostMapping("/email")
    public ResponseEntity<Map<String, Object>> sendEmail(@RequestBody Map<String, String> request) {
        String recipientEmail = request.get("recipientEmail");
        String subject = request.get("subject");
        String content = request.get("content");

        Notification notification = new Notification();
        notification.setRecipientEmail(recipientEmail);
        notification.setSubject(subject);
        notification.setContent(content);
        notification.setType(NotificationType.EMAIL);

        try {
            // Mock email sending (logs to console)
            logger.info("==========================================");
            logger.info("📧 SENDING EMAIL NOTIFICATION");
            logger.info("==========================================");
            logger.info("To: {}", recipientEmail);
            logger.info("Subject: {}", subject);
            logger.info("Content: {}", content);
            logger.info("==========================================");

            notification.setStatus(NotificationStatus.SENT);
            notification.setSentAt(LocalDateTime.now());
        } catch (Exception e) {
            notification.setStatus(NotificationStatus.FAILED);
        }

        Notification savedNotification = notificationRepository.save(notification);

        Map<String, Object> response = new HashMap<>();
        response.put("notificationId", savedNotification.getId());
        response.put("status", savedNotification.getStatus());
        response.put("sentAt", savedNotification.getSentAt());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/push")
    public ResponseEntity<Map<String, Object>> sendPush(@RequestBody Map<String, String> request) {
        String recipientEmail = request.get("recipientEmail");
        String subject = request.get("subject");
        String content = request.get("content");

        Notification notification = new Notification();
        notification.setRecipientEmail(recipientEmail);
        notification.setSubject(subject);
        notification.setContent(content);
        notification.setType(NotificationType.PUSH);

        try {
            // Mock push notification (logs to console)
            logger.info("==========================================");
            logger.info("🔔 SENDING PUSH NOTIFICATION");
            logger.info("==========================================");
            logger.info("To: {}", recipientEmail);
            logger.info("Title: {}", subject);
            logger.info("Message: {}", content);
            logger.info("==========================================");

            notification.setStatus(NotificationStatus.SENT);
            notification.setSentAt(LocalDateTime.now());
        } catch (Exception e) {
            notification.setStatus(NotificationStatus.FAILED);
        }

        Notification savedNotification = notificationRepository.save(notification);

        Map<String, Object> response = new HashMap<>();
        response.put("notificationId", savedNotification.getId());
        response.put("status", savedNotification.getStatus());
        response.put("sentAt", savedNotification.getSentAt());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/history/{email}")
    public ResponseEntity<Map<String, Object>> getNotificationHistory(@PathVariable String email) {
        var notifications = notificationRepository.findByRecipientEmail(email);
        
        Map<String, Object> response = new HashMap<>();
        response.put("email", email);
        response.put("notifications", notifications);
        response.put("count", notifications.size());
        
        return ResponseEntity.ok(response);
    }
}
