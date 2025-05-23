package com.springboot.club_house_api_server.report.repository;

import com.springboot.club_house_api_server.club.entity.ClubEntity;
import com.springboot.club_house_api_server.report.entity.ReportEntity;
import jakarta.persistence.Table;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReportRepository extends JpaRepository<ReportEntity, Long> {
    Optional<ReportEntity> findByClubAndYearAndMonth(ClubEntity club, int year, int month);

}
