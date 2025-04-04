package com.springboot.club_house_api_server.notification.dto;

import com.springboot.club_house_api_server.notification.entity.NotificationEntity;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponseDto {
    private Long notificationId;
    private String title;
    private String message;
    private NotificationEntity.NotificationType type;
    private boolean isRead;
    private LocalDateTime createdAt;
    private String sender;
    private Long referenceId;
    private Long targetId;
}
