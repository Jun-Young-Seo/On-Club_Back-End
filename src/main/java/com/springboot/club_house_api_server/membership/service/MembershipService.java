package com.springboot.club_house_api_server.membership.service;

import com.springboot.club_house_api_server.club.entity.ClubEntity;
import com.springboot.club_house_api_server.club.repository.ClubRepository;
import com.springboot.club_house_api_server.event.repository.ClubEventRepository;
import com.springboot.club_house_api_server.membership.dto.MembershipModifyDto;
import com.springboot.club_house_api_server.membership.dto.MembershipResponseDto;
import com.springboot.club_house_api_server.membership.entity.MembershipEntity;
import com.springboot.club_house_api_server.membership.repository.MembershipRepository;
import com.springboot.club_house_api_server.notification.dto.NotificationSendDto;
import com.springboot.club_house_api_server.notification.entity.NotificationEntity;
import com.springboot.club_house_api_server.notification.service.NotificationService;
import com.springboot.club_house_api_server.user.entity.UserEntity;
import com.springboot.club_house_api_server.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MembershipService {

    private final MembershipRepository membershipRepository;
    private final UserRepository userRepository;
    private final ClubRepository clubRepository;
    private final ClubEventRepository clubEventRepository;
    private final NotificationService notificationService;

    @Transactional
    //클럽에 가입 요청하는 경우
    public ResponseEntity<?> joinRequestToClub(long userId, long clubId) {
        //유저 확인
        Optional<UserEntity> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("userId를 확인해주세요.");
        }
        //클럽 확인
        Optional<ClubEntity> clubOpt = clubRepository.findById(clubId);
        if (clubOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("clubId를 확인해주세요.");
        }
        //중복가입 확인
        Optional<MembershipEntity> existingMembership = membershipRepository.getMembershipEntityByUserIdAndClubId(userId, clubId);
        if (existingMembership.isPresent()) {
            return ResponseEntity.badRequest().body("이미 이 클럽에 가입한 사용자입니다.");
        }
        List<Long> clubManagerIds = membershipRepository.findUserIdsOfManagersAndLeadersByClubId(clubId);
        NotificationSendDto sendDto = NotificationSendDto.builder()
                        .type(NotificationEntity.NotificationType.JOIN_REQUEST)
                        .message("신규 회원 가입 신청이 도착했습니다.")
                        .title("신규 회원가입 신청")
                        .sender("SYSTEM")
                        .userIdList(clubManagerIds)
                        .referenceId(userId)
                        .targetId(clubId)
                        .build();
        notificationService.sendNotification(sendDto);

        return ResponseEntity.ok("정상적으로 가입신청 되었습니다.");

    }


    public ResponseEntity<?> approveJoinRequest(long userId, long clubId) {
        //유저 확인
        Optional<UserEntity> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("userId를 확인해주세요.");
        }
        //클럽 확인
        Optional<ClubEntity> clubOpt = clubRepository.findById(clubId);
        if (clubOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("clubId를 확인해주세요.");
        }
        //중복가입 확인
        Optional<MembershipEntity> existingMembership = membershipRepository.getMembershipEntityByUserIdAndClubId(userId, clubId);
        if (existingMembership.isPresent()) {
            return ResponseEntity.badRequest().body("이미 이 클럽에 가입한 사용자입니다.");
        }
        // 동호회에 회원가입이 되면 멤버십 테이블이 새 항목으로 저장
        MembershipEntity membership = new MembershipEntity();
        membership.setUser(userOpt.get());
        membership.setClub(clubOpt.get());
        membership.setRole(MembershipEntity.RoleType.REGULAR);
        membership.setAttendanceCount(0);
        membershipRepository.save(membership);

        String clubName = clubOpt.get().getClubName();
        String userTel = userOpt.get().getUserTel();

        //가입하면 클럽 회원 수 증가 시키기
        clubOpt.get().setClubHowManyMembers(clubOpt.get().getClubHowManyMembers() + 1);

        NotificationSendDto sendDto = NotificationSendDto.builder()
                .type(NotificationEntity.NotificationType.APPROVED)
                .referenceId(userId)
                .targetId(clubId)
                .message(clubName+"에 가입되었습니다!")
                .sender(clubName)
                .title("가입 신청 승인")
                .userIdList(List.of(userOpt.get().getUserId()))
                .build();
        notificationService.sendNotification(sendDto);

        return ResponseEntity.ok(clubName+"에 "+ userTel+" 님이 성공적으로 가입되었습니다.");
    }

    public ResponseEntity<?> rejectJoinRequest(long userId, long clubId) {
        //유저 확인
        Optional<UserEntity> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("userId를 확인해주세요.");
        }
        //클럽 확인
        Optional<ClubEntity> clubOpt = clubRepository.findById(clubId);
        if (clubOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("clubId를 확인해주세요.");
        }
        //중복가입 확인
        Optional<MembershipEntity> existingMembership = membershipRepository.getMembershipEntityByUserIdAndClubId(userId, clubId);
        if (existingMembership.isPresent()) {
            return ResponseEntity.badRequest().body("이미 이 클럽에 가입한 사용자입니다.");
        }
        String clubName = clubOpt.get().getClubName();

        NotificationSendDto sendDto = NotificationSendDto.builder()
                .type(NotificationEntity.NotificationType.REJECTED)
                .referenceId(userId)
                .targetId(clubId)
                .message(clubName+"에서 가입을 거절했습니다.")
                .sender(clubName)
                .title("가입 신청 거절")
                .userIdList(List.of(userOpt.get().getUserId()))
                .build();
        notificationService.sendNotification(sendDto);

        return ResponseEntity.ok("가입 신청 거절 알림이 전송되었습니다.");
    }

    //요청한 role이 enum에 포함되는지에 대한 메서드
    private boolean isValidRole(String role) {
        for(MembershipEntity.RoleType type : MembershipEntity.RoleType.values()) {
            if(type.name().equals(role)) {
                return true;
            }
        }
        return false;
    }

    public ResponseEntity<?> getAllMembershipsByClubId(long clubId){

        int clubEventCount = clubEventRepository.countEventsByClubId(clubId);
        List<MembershipEntity> members = membershipRepository.findAllMembershipsByClubId(clubId);
        List<MembershipResponseDto> response = new ArrayList<>();
        for(MembershipEntity membership : members) {
            Optional<UserEntity> userOpt = userRepository.findById(membership.getUser().getUserId());
            if(userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("userId에 해당하는 유저가 없습니다. membership Id: "+membership.getMembershipId());
            }
            UserEntity user = userOpt.get();
            MembershipResponseDto dto = MembershipResponseDto.builder()
                    .membershipId(membership.getMembershipId())
                    .attendanceCount(membership.getAttendanceCount())
                    .joinedAt(membership.getJoinedAt())
                    .eventCount(clubEventCount)
                    .birthDate(user.getBirthDate())
                    .career(user.getCareer())
                    .region(user.getRegion())
                    .userName(user.getUserName())
                    .userTel(user.getUserTel())
                    .gender(user.getGender())
                    .role(membership.getRole())
                    .build();
            response.add(dto);
        }

        return ResponseEntity.ok(response);
    }

    @Transactional
    public ResponseEntity<?> withdrawClub(long membershipId){
        Optional<MembershipEntity> membershipOpt = membershipRepository.findById(membershipId);
        if(membershipOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("membership ID에 해당하는 가입정보가 없습니다.");
        }
        MembershipEntity membership = membershipOpt.get();

        membershipRepository.delete(membership);

        return ResponseEntity.ok().body("정상적으로 탈퇴가 완료되었습니다.");
    }

    @Transactional
    public ResponseEntity<?> modifyMembershipRole(MembershipModifyDto dto){
        long membershipId = dto.getMembershipId();
        MembershipEntity.RoleType role = dto.getRole();

        Optional<MembershipEntity> membershipOpt = membershipRepository.findById(membershipId);
        if (membershipOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("membership ID에 해당하는 가입정보가 없습니다.");
        }
        MembershipEntity membership = membershipOpt.get();
        membership.setRole(role);
        membershipRepository.save(membership);

        return ResponseEntity.ok().body(membership.getMembershipId() +"의 가입정보가 수정되었습니다.");
    }

    public ResponseEntity<?> getMyRole(long userId, long ClubId){
        Optional<MembershipEntity> membershipOpt = membershipRepository.getMembershipEntityByUserIdAndClubId(userId, ClubId);
        if(membershipOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("userId와 clubID에 해당하는 가입정보가 없습니다.");
        }
        MembershipEntity membership = membershipOpt.get();
        return ResponseEntity.ok().body(membership.getRole());
    }
}
