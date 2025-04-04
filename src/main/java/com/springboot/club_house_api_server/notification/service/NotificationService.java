package com.springboot.club_house_api_server.notification.service;


import com.springboot.club_house_api_server.notification.dto.NotificationResponseDto;
import com.springboot.club_house_api_server.notification.dto.NotificationSendDto;
import com.springboot.club_house_api_server.notification.entity.NotificationEntity;
import com.springboot.club_house_api_server.notification.repository.NotificationRepository;
import com.springboot.club_house_api_server.user.entity.UserEntity;
import com.springboot.club_house_api_server.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    //알림전송
    @Transactional
    public ResponseEntity<?> sendNotification(NotificationSendDto dto) {
        List<Long> failedUserIds = new ArrayList<>();

        List<Long> userList = dto.getUserIdList();
        for (Long userId : dto.getUserIdList()) {
            Optional<UserEntity> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                failedUserIds.add(userId);
                continue;
            }
            NotificationEntity notification = NotificationEntity.builder()
                    .title(dto.getTitle())
                    .message(dto.getMessage())
                    .sender(dto.getSender())
                    .type(dto.getType())
                    .user(userOpt.get())
                    .referenceId(dto.getReferenceId())
                    .targetId(dto.getTargetId())
                    .build();

            notificationRepository.save(notification);
        }

        if(failedUserIds.isEmpty()) {
            return ResponseEntity.ok("메세지 전송 완료");
        }
        else{
            return ResponseEntity.ok("메세지 전송은 완료했지만 실패가 있습니다 : "+failedUserIds);
        }
    }

    public ResponseEntity<?> getAllNotifications(Long userId) {
        List<NotificationEntity> notifications = notificationRepository.findAllByUserId(userId);
        if(notifications.isEmpty()) {
            return ResponseEntity.ok().body("도착한 알림이 없습니다.");
        }
        List<NotificationResponseDto> response = new ArrayList<>();
        for(NotificationEntity notification : notifications) {
            NotificationResponseDto responseDto = NotificationResponseDto.builder()
                    .notificationId(notification.getNotificationId())
                    .title(notification.getTitle())
                    .message(notification.getMessage())
                    .sender(notification.getSender())
                    .type(notification.getType())
                    .createdAt(notification.getCreatedAt())
                    .isRead(notification.getIsRead())
                    .referenceId(notification.getReferenceId())
                    .targetId(notification.getTargetId())
                    .build();
            response.add(responseDto);
        }
        return ResponseEntity.ok(response);
    }

    @Transactional
    public ResponseEntity<?> setNotificationsRead(long notificationId){
        Optional<NotificationEntity> notificationOpt = notificationRepository.findById(notificationId);
        if(notificationOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("noti Id에 해당하는 알림이 없습니다.");
        }

        NotificationEntity notification = notificationOpt.get();
        notification.setIsRead(true);
        notificationRepository.save(notification);

        return ResponseEntity.ok("읽음 처리 완료");
    }

    @Transactional
    public ResponseEntity<?> deleteNotification(long notificationId){
        Optional<NotificationEntity> notificationOpt = notificationRepository.findById(notificationId);
        if(notificationOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("noti Id에 해당하는 알림이 없습니다.");
        }
        notificationRepository.delete(notificationOpt.get());

        return ResponseEntity.ok("삭제 완료");
    }
}
