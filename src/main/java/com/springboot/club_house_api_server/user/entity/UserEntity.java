package com.springboot.club_house_api_server.user.entity;

import com.springboot.club_house_api_server.club.entity.ClubEntity;
import com.springboot.club_house_api_server.guest.entity.GuestEntity;
import com.springboot.club_house_api_server.membership.entity.MembershipEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

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
    //DB상의 ID. Login ID 아님
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private long userId;

    @Column(name ="user_name")
    private String userName;

    @Column(name="password", nullable = false)
    private String password;

    //Tel이 유일하므로 로그인용 ID로 사용 예정
    @Column(name = "user_tel", unique = true, nullable = false)
    private String userTel;

    @Column (name= "created_at", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name ="refresh_token")
    private String refreshToken;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<MembershipEntity> memberships;

    @OneToMany(mappedBy = "user",fetch = FetchType.LAZY)
    private List<GuestEntity> guests;


}
