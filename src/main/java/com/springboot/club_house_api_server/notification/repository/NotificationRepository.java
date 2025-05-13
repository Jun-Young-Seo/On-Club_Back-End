package com.springboot.club_house_api_server.notification.repository;

import com.springboot.club_house_api_server.notification.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {
    //유저에게 도착한 모든 메세지 가져오기
    @Query("SELECT n FROM NotificationEntity n WHERE n.user.userId = :userId ORDER BY n.createdAt DESC")
    List<NotificationEntity> findAllByUserId(@Param("userId") Long userId);

    //중복 가입신청, 게스트 신청 방지 코드
    @Query("SELECT COUNT(n) > 0 FROM NotificationEntity n " +
            "WHERE n.referenceId = :referenceId AND n.type = :type AND n.targetId = :targetId")
    boolean alreadyRequestExisted(
            @Param("referenceId") Long referenceId,
            @Param("targetId") Long targetId,
            @Param("type") NotificationEntity.NotificationType type
    );


}
