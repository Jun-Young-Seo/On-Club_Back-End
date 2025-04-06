package com.springboot.club_house_api_server.guest.service;

import com.springboot.club_house_api_server.club.entity.ClubEntity;
import com.springboot.club_house_api_server.club.repository.ClubRepository;
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
import com.springboot.club_house_api_server.user.entity.UserEntity;
import com.springboot.club_house_api_server.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GuestService {
    private final GuestRepository guestRepository;
    private final UserRepository userRepository;
    private final ClubEventRepository clubEventRepository;
    private final MembershipRepository membershipRepository;
    private final NotificationService notificationService;
    private final NotificationRepository notificationRepository;

    public ResponseEntity<?> attendEventAsGuest(long userId, long eventId){
        Optional<UserEntity> userOpt = userRepository.findById(userId);
        if(userOpt.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("user Id에 해당하는 User가 없습니다.");
        }
        Optional<ClubEventEntity> eventOpt = clubEventRepository.findById(eventId);
        if(eventOpt.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("event Id에 해당하는 Event가 없습니다.");
        }
        ClubEntity club = eventOpt.get().getClub();
        Optional<MembershipEntity> membershipOpt = membershipRepository.getMembershipEntityByUserIdAndClubId(userId, club.getClubId());
        if(membershipOpt.isPresent()){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 클럽에 가입한 유저입니다.");
        }
        Optional<GuestEntity> alreadyAttend = guestRepository.findByUserAndEvent(userOpt.get(), eventOpt.get());
        if(alreadyAttend.isPresent()){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 참석한 유저입니다.");
        }
        //중복 신청 방지 로직
        if(notificationRepository.existsJoinRequest(userId,eventId,NotificationEntity.NotificationType.GUEST_REQUEST)){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 게스트 참여 신청 했습니다.");
        }

        long clubId = club.getClubId();
        List<Long> clubManagerIds = membershipRepository.findUserIdsOfManagersAndLeadersByClubId(clubId);
        NotificationSendDto sendDto = NotificationSendDto.builder()
                .userIdList(clubManagerIds)
                .title("게스트 참석 요청")
                .message("게스트 참석 요청이 도착했습니다.")
                .sender("SYSTEM")
                .targetId(eventId)
                .referenceId(userId)
                .type(NotificationEntity.NotificationType.GUEST_REQUEST)
                .build();

        notificationService.sendNotification(sendDto);
        return ResponseEntity.ok("게스트 참석 요청이 성공적으로 완료되었습니다.");
    }

    @Transactional
    public ResponseEntity<?> approveGuest(long userId, long eventId){
        Optional<UserEntity> userOpt = userRepository.findById(userId);
        if(userOpt.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("user Id에 해당하는 User가 없습니다.");
        }
        Optional<ClubEventEntity> eventOpt = clubEventRepository.findById(eventId);
        if(eventOpt.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("event Id에 해당하는 Event가 없습니다.");
        }
        ClubEntity club = eventOpt.get().getClub();
        Optional<MembershipEntity> membershipOpt = membershipRepository.getMembershipEntityByUserIdAndClubId(userId, club.getClubId());
        if(membershipOpt.isPresent()){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 클럽에 가입한 유저입니다.");
        }
        Optional<GuestEntity> alreadyAttend = guestRepository.findByUserAndEvent(userOpt.get(), eventOpt.get());
        if(alreadyAttend.isPresent()){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 참석한 유저입니다.");
        }
        GuestEntity guestEntity = new GuestEntity();
        guestEntity.setClub(club);
        guestEntity.setUser(userOpt.get());
        guestEntity.setEvent(eventOpt.get());

        guestRepository.save(guestEntity);
        club.setClubAccumulatedGuests(club.getClubAccumulatedGuests() + 1);

        NotificationSendDto sendDto = NotificationSendDto.builder()
                .userIdList(List.of(userId))
                .title("게스트 참석 요청 승인")
                .message("게스트 참석 요청이 승인되었습니다!.")
                .sender(club.getClubName())
                .targetId(eventId)
                .referenceId(userId)
                .type(NotificationEntity.NotificationType.APPROVED)
                .build();
        notificationService.sendNotification(sendDto);
        return ResponseEntity.ok("userId : "+ userId+"의 "+"eventId : " +eventId+"참석 처리 완료");
    }

    public ResponseEntity<?> rejectGuest(long userId, long eventId){
        Optional<UserEntity> userOpt = userRepository.findById(userId);
        if(userOpt.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("user Id에 해당하는 User가 없습니다.");
        }
        Optional<ClubEventEntity> eventOpt = clubEventRepository.findById(eventId);
        if(eventOpt.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("event Id에 해당하는 Event가 없습니다.");
        }
        ClubEntity club = eventOpt.get().getClub();
        Optional<MembershipEntity> membershipOpt = membershipRepository.getMembershipEntityByUserIdAndClubId(userId, club.getClubId());
        if(membershipOpt.isPresent()){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 클럽에 가입한 유저입니다.");
        }
        Optional<GuestEntity> alreadyAttend = guestRepository.findByUserAndEvent(userOpt.get(), eventOpt.get());
        if(alreadyAttend.isPresent()){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 참석한 유저입니다.");
        }
        NotificationSendDto sendDto = NotificationSendDto.builder()
                .userIdList(List.of(userId))
                .title("게스트 참석 요청 거절")
                .message("게스트 참석 요청이 거절되었습니다.")
                .sender(club.getClubName())
                .targetId(eventId)
                .referenceId(userId)
                .type(NotificationEntity.NotificationType.REJECTED)
                .build();

        notificationService.sendNotification(sendDto);

        return ResponseEntity.ok("게스트 참석 거절 요청이 완료되었습니다.");
    }
}
