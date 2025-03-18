package com.springboot.club_house_api_server.club.entity;

import com.springboot.club_house_api_server.budget.entity.TransactionEntity;
import com.springboot.club_house_api_server.club.account.entity.ClubAccountEntity;
import com.springboot.club_house_api_server.event.entity.ClubEventEntity;
import com.springboot.club_house_api_server.membership.entity.MembershipEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity

@Table(name = "club")
public class ClubEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="club_id")
    private long clubId;

    @Column(name="club_name")
    private String clubName;

    @Column(name="club_description")
    private String clubDescription;

    @Column(name="club_logo")
    private String clubLogoURL;

    @Column(name = "club_background")
    private String clubBackgroundURL;

    @Column(name="club_created_at")
    @CreationTimestamp
    private LocalDateTime clubCreatedAt;

    // 양방향 매핑 - 클럽에서 거래 내역 조회 가능
    @OneToMany(mappedBy = "club", fetch = FetchType.LAZY)
    private List<TransactionEntity> transactions;

    // 양방향 매핑 - 클럽에서 계좌 정보 조회 가능
    @OneToMany(mappedBy = "club", fetch = FetchType.LAZY)
    private List<ClubAccountEntity> accounts;

    // 양방향 매핑 - 클럽에서 멤버십 정보(유저 가입 정보) 조회 가능
    @OneToMany(mappedBy = "club",fetch = FetchType.LAZY)
    private List<MembershipEntity> memberships;

    // 양방향 매핑 - 클럽에서 이벤트 정보 조회 가능
    @OneToMany(mappedBy = "club",fetch = FetchType.LAZY)
    private List<ClubEventEntity> events;

    public ClubEntity(String clubName, String clubDescription) {
        this.clubName = clubName;
        this.clubDescription = clubDescription;
    }
}
