package com.springboot.club_house_api_server.club_data.entity;


import com.springboot.club_house_api_server.club.entity.ClubEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "Club_Data")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClubDataEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DATA_ID")
    private Long id; // 데이터 ID (PK)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CLUB_ID", nullable = false)
    private ClubEntity club; // 동호회 (FK)

    @Column(name = "DATA_URL", nullable = false, columnDefinition = "TEXT")
    private String dataUrl; // S3 버킷 객체 URL

    @Column(name = "DATA_NAME", nullable = false)
    private String dataName; // 데이터 이름

    @Column(name = "DATA_DATE", nullable = false)
    private LocalDateTime dataDate; // 데이터 업로드 일자

    @Column(name = "DATA_WHO", nullable = false)
    private String dataWho; // 데이터 업로드한 사람

    @Column(name="DATA_SCENARIO",nullable = false)
    private String dataScenario;
}
