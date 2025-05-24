package com.springboot.club_house_api_server.apn.entity;

import com.springboot.club_house_api_server.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@Table(name="apn", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "device_token"}))
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
