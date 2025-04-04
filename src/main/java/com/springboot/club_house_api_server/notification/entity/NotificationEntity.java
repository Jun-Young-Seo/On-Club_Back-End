package com.springboot.club_house_api_server.notification.entity;


import com.springboot.club_house_api_server.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "notification")
public class NotificationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    @JoinColumn(name="user_id",nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity user;

    //여러 서비스에서 재사용을 위해 일반 스트링으로 지정
    //ex) 클럽에서 보내는 경우 클럽 이름을 쓰게 서비스코드를 작성하고
    //시스템에서 보내는 경우 시스템으로 서비스코드 쓰면 되니까?
    @Column(name="sender", nullable = false)
    private String sender;

    @Column(name="title", nullable = false)
    private String title;

    @Column(name="message", nullable = false)
    private String message;

    @Column(name="type", nullable = false)
    private NotificationType type;

    @Column(name="reference_id", nullable = true)
    private Long referenceId;

    @Column(name="target_id", nullable = true)
    private Long targetId;

    //롬복 자체 이슈로 null 가능성이 있어 false 할당 금지
    @Builder.Default
    @Column(name="is_read",nullable = false)
    private Boolean isRead = false;

    @CreationTimestamp
    @Column(name="created_at", nullable = false)
    private LocalDateTime createdAt;

    public enum NotificationType {
        JOIN_REQUEST, //가입신청
        GUEST_REQUEST,
        APPROVED, //신청 승인
        REJECTED, //신청 거부
        KICKED, //클럽 짤림
        EVENT_ATTENDANCE, //이벤트에 참석되었다고 확인 메시지
        COMMENT_REQUEST, //이벤트 끝나고 후기 요청
        NOTICE, //알림 메시지
        SYSTEM // 시스템 메시진데 쓸지말지 모르곘음
    }
}
