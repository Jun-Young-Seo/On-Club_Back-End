package com.springboot.club_house_api_server.report.entity;

import com.springboot.club_house_api_server.club.entity.ClubEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name="report")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="report_id")
    private long reportId;

    @Column(name="ai_budget_report", columnDefinition = "TEXT")
    private String aiBudgetReport;

    @Column(name="ai_member_report", columnDefinition = "TEXT")
    private String aiMemberReport;

    @Column(name="year")
    private Integer year;

    @Column(name="month")
    private Integer month;

    //Relation
    //Club에서 받아올 일 있으면 클럽엔티티에 추가하기
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id", nullable = false)
    private ClubEntity club;

}
