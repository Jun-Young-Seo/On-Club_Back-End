package com.springboot.club_house_api_server.summary.repository;

import com.springboot.club_house_api_server.summary.entity.AudioSummaryRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AudioSummaryRecordRepository extends JpaRepository<AudioSummaryRecord, Long> {
    // 필요한 추가 쿼리 메서드가 있으면 여기에 작성
}
