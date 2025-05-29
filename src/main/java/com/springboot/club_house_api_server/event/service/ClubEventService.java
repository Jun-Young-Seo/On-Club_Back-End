package com.springboot.club_house_api_server.event.service;

import com.springboot.club_house_api_server.apn.entity.APNEntity;
import com.springboot.club_house_api_server.apn.repository.APNRepository;
import com.springboot.club_house_api_server.apn.service.APNsService;
import com.springboot.club_house_api_server.club.entity.ClubEntity;
import com.springboot.club_house_api_server.club.repository.ClubRepository;
import com.springboot.club_house_api_server.event.dto.ClubEventDto;
import com.springboot.club_house_api_server.event.dto.MyPageClubEventDto;
import com.springboot.club_house_api_server.event.entity.ClubEventEntity;
import com.springboot.club_house_api_server.event.repository.ClubEventRepository;
import com.springboot.club_house_api_server.guest.entity.GuestEntity;
import com.springboot.club_house_api_server.guest.repository.GuestRepository;
import com.springboot.club_house_api_server.membership.entity.MembershipEntity;
import com.springboot.club_house_api_server.membership.repository.MembershipRepository;
import com.springboot.club_house_api_server.notification.dto.NotificationSendDto;
import com.springboot.club_house_api_server.notification.entity.NotificationEntity;
import com.springboot.club_house_api_server.notification.repository.NotificationRepository;
import com.springboot.club_house_api_server.notification.service.NotificationService;
import com.springboot.club_house_api_server.participant.repository.ParticipantRepository;
import com.springboot.club_house_api_server.user.entity.UserEntity;
import com.springboot.club_house_api_server.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class ClubEventService {
    private final ClubEventRepository clubEventRepository;
    private final ClubRepository clubRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final MembershipRepository membershipRepository;
    private final NotificationService notificationService;
    private final ParticipantRepository participantRepository;
    private final GuestRepository guestRepository;
    private final APNRepository apnRepository;
    private final APNsService apnService;

    @Transactional
    //이벤트 추가하기
    public ResponseEntity<?> addEvent(long clubId, LocalDateTime startTime, LocalDateTime endTime, String description) throws ExecutionException, InterruptedException {
        Optional<ClubEntity> clubOpt = clubRepository.findById(clubId);
//        System.out.println("clubId : "+clubId);
        if(clubOpt.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("클럽 ID와 일치하는 클럽이 없습니다.");
        }
        ClubEntity club = clubOpt.get();
        String clubName = club.getClubName();
        ClubEventEntity clubEvent = new ClubEventEntity(club,startTime,endTime,description);

        List<MembershipEntity> clubMembers = membershipRepository.findAllMembershipsByClubId(clubId);
        if(clubMembers.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("클럽에 가입된 유저가 없습니다.");
        }
        List<Long> memberIds = membershipRepository.findUserIdsOfAllRolesByClubId(clubId);
        NotificationSendDto sendDto = NotificationSendDto.builder()
                .type(NotificationEntity.NotificationType.NOTICE)
                .message("신규 클럽 일정이 생성되었습니다. :" +description)
                .title("클럽 신규 일정")
                .sender(clubName)
                .userIdList(memberIds)
                .referenceId(null)
                .targetId(clubId)
                .build();
        notificationService.sendNotification(sendDto);
        clubEventRepository.save(clubEvent);

        for(Long mId : memberIds){
            Optional<APNEntity> apnOpt = apnRepository.findById(mId);
            if(apnOpt.isPresent()){
                APNEntity apn = apnOpt.get();
                apnService.sendPush(apn.getDeviceToken());
            }

        }
        return ResponseEntity.status(HttpStatus.OK).body("이벤트가 정상적으로 추가되었습니다.");
    }


    //클럽 ID로 모든 이벤트 받아오기
    public ResponseEntity<?> getAllEvents(long clubId){
        Optional<ClubEntity> clubOpt = clubRepository.findById(clubId);
        if(clubOpt.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("클럽 ID와 일치하는 클럽이 없습니다.");
        }
        List<ClubEventEntity> events = clubEventRepository.findAllEventsByClubId(clubId);
        List<ClubEventDto> response = new ArrayList<>();
        for(ClubEventEntity event : events){
            ClubEventDto dto = new ClubEventDto(event.getEventId(), event.getClub().getClubId(), event.getEventStartTime(), event.getEventEndTime(), event.getEventDescription());
            response.add(dto);
        }
        return ResponseEntity.ok(response);
    }

    //클럽 ID와 시작, 끝 시간으로 특정 시간대의 모든 이벤트 받아오기
    public ResponseEntity<?> getEventsByClubAndDate(long clubId, LocalDateTime startTime, LocalDateTime endTime) {
        Optional<ClubEntity> clubOpt = clubRepository.findById(clubId);
        if (clubOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("클럽 ID와 일치하는 클럽이 없습니다.");
        }

        List<ClubEventEntity> events = clubEventRepository.findAllEventsByClubIdAndTimeRange(clubId, startTime, endTime);
        List<ClubEventDto> response = new ArrayList<>();
        for(ClubEventEntity event : events){
            ClubEventDto dto = new ClubEventDto(event.getEventId(),event.getClub().getClubId(), event.getEventStartTime(), event.getEventEndTime(), event.getEventDescription());
            response.add(dto);
        }

        return ResponseEntity.ok(response);
    }


    //클럽 ID와 시작, 끝 시간, 키워드로 특정 시간대의 특정 키워드가 포함된 모든 이벤트 받아오기
    public ResponseEntity<?> getEventsByClubAndDateAndKeyword(long clubId, LocalDateTime startTime, LocalDateTime endTime, String keyword) {
        Optional<ClubEntity> clubOpt = clubRepository.findById(clubId);
        if (clubOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("클럽 ID와 일치하는 클럽이 없습니다.");
        }

        List<ClubEventEntity> events = clubEventRepository.findAllEventsByClubIdAndDateAndKeyword(clubId, startTime, endTime,keyword);
        List<ClubEventDto> response = new ArrayList<>();
        for(ClubEventEntity event : events){
            ClubEventDto dto = new ClubEventDto(event.getEventId(),event.getClub().getClubId(), event.getEventStartTime(), event.getEventEndTime(), event.getEventDescription());
            response.add(dto);
        }

        return ResponseEntity.ok(response);
    }

    //사용자가 소속된 모든 클럽의 이벤트 조회
    public ResponseEntity<?> getAllEventsWhereUserJoined( long userId){
        Optional<UserEntity> userOpt = userRepository.findById(userId);
        if(userOpt.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(userId+"에 해당하는 user가 없습니다.");
        }
        List<ClubEventEntity> events = clubEventRepository.findAllEventsByUserId(userId);
        List<ClubEventDto> response = new ArrayList<>();
        for(ClubEventEntity event : events){
            ClubEventDto dto = new ClubEventDto(event.getEventId(),event.getClub().getClubId(), event.getEventStartTime(), event.getEventEndTime(), event.getEventDescription());
            response.add(dto);
        }
        return ResponseEntity.ok(response);
    }
    //사용자가 소속된 모든 클럽의 이벤트 조회 + 시간으로 조회 쿼리
    public ResponseEntity<?> getEventsByUserAndDateRange(long userId, LocalDateTime startDate, LocalDateTime endDate) {
        // 유저 확인
        Optional<UserEntity> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(userId + "에 해당하는 user가 없습니다.");
        }

        // 이벤트 조회
        List<ClubEventEntity> events = clubEventRepository.findEventsByUserIdWithinDateRange(userId, startDate, endDate);
        if (events.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("해당 기간 동안 사용자가 참여한 이벤트가 없습니다.");
        }

        // DTO 변환
        List<ClubEventDto> response = new ArrayList<>();
        for (ClubEventEntity event : events) {
            ClubEventDto dto = new ClubEventDto(
                    event.getEventId(),
                    event.getClub().getClubId(),
                    event.getEventStartTime(),
                    event.getEventEndTime(),
                    event.getEventDescription()
            );
            response.add(dto);
        }

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> getParticipantsAndGuestsByUserId(long userId){
        Optional<UserEntity> userOpt = userRepository.findById(userId);
        if(userOpt.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("user Id에 해당하는 user가 없습니다.");
        }
        List<MyPageClubEventDto> response = new ArrayList<>();
        List<ClubEventEntity> participantList = participantRepository.findAllEventIdsByUserId(userId);
        for(ClubEventEntity c : participantList){
            MyPageClubEventDto m = MyPageClubEventDto.builder()
                    .clubName(c.getClub().getClubName())
                    .eventDescription(c.getEventDescription())
                    .eventStartTime(c.getEventStartTime())
                    .eventEndTime(c.getEventEndTime())
                    .type("membership")
                    .build();
            response.add(m);
        }

        List<GuestEntity> guest = guestRepository.findAllGuestEntitiesByUserId(userId);
        for(GuestEntity g : guest){
            MyPageClubEventDto m = MyPageClubEventDto.builder()
                    .clubName(g.getClub().getClubName())
                    .eventDescription(g.getEvent().getEventDescription())
                    .eventStartTime(g.getEvent().getEventStartTime())
                    .eventEndTime(g.getEvent().getEventEndTime())
                    .type("guest")
                    .build();
            response.add(m);
        }
        return ResponseEntity.ok(response);
    }
}
