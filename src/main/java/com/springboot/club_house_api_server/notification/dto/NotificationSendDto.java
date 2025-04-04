package com.springboot.club_house_api_server.notification.dto;


import com.springboot.club_house_api_server.notification.entity.NotificationEntity;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationSendDto {
    private List<Long> userIdList; //받을사람
    private String title; //제목
    private String message; //내용
    private String sender; //발송자
    private NotificationEntity.NotificationType type; //알림의 종
    private Long referenceId; //사용할 레퍼런스 ID, ex) 유저가 가입신청시 UserID필드, 이벤트 알림시 eventID 필드 등으로 활용 가능
    private Long targetId; //사용할 타겟 ID, ex) 유저가 가입신청시 ClubId필드. 어느 클럽에 가입할지에 대한 정보
}
