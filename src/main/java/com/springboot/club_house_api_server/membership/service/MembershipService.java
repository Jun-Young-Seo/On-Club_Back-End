package com.springboot.club_house_api_server.membership.service;

import com.springboot.club_house_api_server.club.entity.ClubEntity;
import com.springboot.club_house_api_server.club.repository.ClubRepository;
import com.springboot.club_house_api_server.membership.dto.MembershipResponseDto;
import com.springboot.club_house_api_server.membership.entity.MembershipEntity;
import com.springboot.club_house_api_server.membership.repository.MembershipRepository;
import com.springboot.club_house_api_server.user.entity.UserEntity;
import com.springboot.club_house_api_server.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
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

    @Transactional
    //클럽에 가입 처리하는 경우
    public ResponseEntity<?> joinToClub(long userId, long clubId, MembershipEntity.RoleType role) {
        if(!isValidRole(role.name())){
            return ResponseEntity.badRequest().body("role의 이름이 틀렸습니다.");
        }
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
        Optional<MembershipEntity> existingMembership = membershipRepository.checkAlreadyJoined(userId, clubId);
        if (existingMembership.isPresent()) {
            return ResponseEntity.badRequest().body("이미 이 클럽에 가입한 사용자입니다.");
        }
        // 동호회에 회원가입이 되면 멤버십 테이블이 새 항목으로 저장
        MembershipEntity membership = new MembershipEntity();
        membership.setUser(userOpt.get());
        membership.setClub(clubOpt.get());
        membership.setRole(role);
        membership.setAttendanceRate(0.0);
        membershipRepository.save(membership);

        String clubName = clubOpt.get().getClubName();
        String userTel = userOpt.get().getUserTel();

        //가입하면 클럽 회원 수 증가 시키기
        clubOpt.get().setClubHowManyMembers(clubOpt.get().getClubHowManyMembers() + 1);
        return ResponseEntity.ok(clubName+"에 "+ userTel+" 님이 성공적으로 가입되었습니다.");
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
                    .attendanceRate(membership.getAttendanceRate())
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
}
