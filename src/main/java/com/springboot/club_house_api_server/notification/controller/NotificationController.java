package com.springboot.club_house_api_server.notification.controller;

import com.springboot.club_house_api_server.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/notification")
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllNotifications(@RequestParam long userId){
        return notificationService.getAllNotifications(userId);
    }

    @PatchMapping("/read")
    public ResponseEntity<?> setNotificationsRead(@RequestParam long notificationId){
        return notificationService.setNotificationsRead(notificationId);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteNotification(@RequestParam long notificationId){
        return notificationService.deleteNotification(notificationId);
    }
}
