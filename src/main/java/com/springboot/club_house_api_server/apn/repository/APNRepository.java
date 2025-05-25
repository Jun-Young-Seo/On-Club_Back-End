package com.springboot.club_house_api_server.apn.repository;

import com.springboot.club_house_api_server.apn.entity.APNEntity;
import com.springboot.club_house_api_server.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface APNRepository extends JpaRepository<APNEntity, Long> {
    @Query("SELECT a FROM APNEntity a WHERE a.user.userId = :userId AND a.deviceToken = :deviceToken")
    Optional<APNEntity> findByUserIdAndDeviceToken(@Param("userId") Long userId,
                                                   @Param("deviceToken") String deviceToken);

}
