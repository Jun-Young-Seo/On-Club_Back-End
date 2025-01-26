package com.springboot.club_house_api_server.user.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Table(name="user")
@Builder
@Getter
@Setter
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
public class UserEntity {
    @Id
    @GeneratedValue
    @Column(name = "user_id")
    private long userId;

    @Column(name ="user_name")
    private String userName;

    @Column(name = "user_tel")
    private String userTel;

    @Column (name= "created_at")
    private LocalDateTime createdAt;
}
