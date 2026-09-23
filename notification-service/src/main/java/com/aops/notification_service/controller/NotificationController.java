package com.aops.notification_service.controller;

import com.aops.notification_service.dto.response.NotificationResponse;
import com.aops.notification_service.service.NotificationService;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public List<NotificationResponse> getAllNotifications() {

        return notificationService.getAllNotifications();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public NotificationResponse getNotificationById(
            @PathVariable Long id) {

        return notificationService.getNotificationById(id);
    }
}