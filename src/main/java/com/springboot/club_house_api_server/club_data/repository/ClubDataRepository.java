package com.springboot.club_house_api_server.club_data.repository;

import com.springboot.club_house_api_server.club_data.entity.ClubDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClubDataRepository extends JpaRepository<ClubDataEntity, Long> {

}
