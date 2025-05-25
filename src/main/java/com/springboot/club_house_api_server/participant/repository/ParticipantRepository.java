package com.springboot.club_house_api_server.participant.repository;

import com.springboot.club_house_api_server.event.entity.ClubEventEntity;
import com.springboot.club_house_api_server.participant.dto.MembersReportDto;
import com.springboot.club_house_api_server.participant.entity.ParticipantEntity;
import com.springboot.club_house_api_server.user.entity.UserEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ParticipantRepository extends JpaRepository<ParticipantEntity, Long> {


    @Query("SELECT p FROM ParticipantEntity p WHERE p.event.eventId = :eventId")
    List<ParticipantEntity> findByEventId(@Param("eventId") Long eventId);

    @Query("SELECT p FROM ParticipantEntity p WHERE p.user.userId = :userId AND p.event.eventId = :eventId")
    Optional<ParticipantEntity> findByUserIdAndEventId(@Param("userId") Long userId, @Param("eventId") Long eventId);

    @Query("SELECT p.event FROM ParticipantEntity p WHERE p.user.userId = :userId")
    List<ClubEventEntity> findAllEventIdsByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(p) > 0 FROM ParticipantEntity p WHERE p.user.userId = :userId AND p.event.eventId = :eventId")
    boolean existsByUserIdAndEventId(@Param("userId") long userId, @Param("eventId") long eventId);

    //이 달에 참석하는 횟수 - MyPage용
    @Query("""
        SELECT COUNT(p)
        FROM ParticipantEntity p
        WHERE p.user.userId = :userId 
            AND
        p.event.eventStartTime BETWEEN :startDate AND :endDate 
    """)
    Long countParticipantsByUserIdWithTime(@Param("userId") Long userId,
                                   @Param("startDate") LocalDateTime start,
                                   @Param("endDate") LocalDateTime end);

    //누적 참여수 - MyPage용
    @Query("""
        SELECT COUNT(p)
        FROM ParticipantEntity p
        WHERE p.user.userId = :userId
    """)
    Long countParticipantsByUserId(@Param("userId") Long userId);

    //이 달에 참석한 멤버 얻어오기
    //보고서 작성용
    @Query("""
    SELECT DISTINCT p.user
    FROM ParticipantEntity p
    WHERE p.event.eventStartTime BETWEEN :startDate AND :endDate
    """)
    List<UserEntity> findAttendedMemberUserIds(@Param("startDate") LocalDateTime start,
                                               @Param("endDate") LocalDateTime end);

    @Query("""
    SELECT new com.springboot.club_house_api_server.participant.dto.MembersReportDto(p.user, COUNT(p))
    FROM ParticipantEntity p
    WHERE p.event.eventStartTime BETWEEN :start AND :end
    GROUP BY p.user
    """)
    List<MembersReportDto> findMembershipAttendanceCount(@Param("start") LocalDateTime start,
                                                         @Param("end") LocalDateTime end);

    @Query("""  
    SELECT p.user
    FROM ParticipantEntity p
    GROUP BY p.user
    ORDER BY COUNT(p) DESC
""")
    List<UserEntity> findTopEventParticipant(Pageable pageable);

}
