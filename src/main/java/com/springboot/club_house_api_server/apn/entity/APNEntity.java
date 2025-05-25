package com.springboot.club_house_api_server.apn.entity;

import com.springboot.club_house_api_server.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
//, uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "device_token"}) 적용 ㅇ안됨
//Maybe ORM 이슈. 워크벤치에서 직접 걸어주면 되지만 매 배포마다 그러긴 좀
//그냥 서비스 레이어에서 일단 중복 걸러내기로
@Table(name="apn")
public class APNEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="apn_id")
    private long apnId;

    @ManyToOne
    @JoinColumn(name="user_id", nullable = false)
    private UserEntity user;

    @Column(name="device_token",nullable = true, columnDefinition = "TEXT")
    private String deviceToken;

    //ios, android 등 추후 확장용 필드
    @Column(name="platform")
    private String platform;

    @Column(name="last_used_at")
    private LocalDateTime lastUsedAt;

}
