package com.springboot.club_house_api_server.membership.service;

import com.springboot.club_house_api_server.club.entity.ClubEntity;
import com.springboot.club_house_api_server.club.repository.ClubRepository;
import com.springboot.club_house_api_server.membership.entity.MembershipEntity;
import com.springboot.club_house_api_server.membership.repository.MembershipRepository;
import com.springboot.club_house_api_server.user.entity.UserEntity;
import com.springboot.club_house_api_server.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MembershipService {

    private final MembershipRepository membershipRepository;
    private final UserRepository userRepository;
    private final ClubRepository clubRepository;

    //클럽에 가입 처리하는 경우
    public ResponseEntity<?> joinToClub(long userId, long clubId, String role) {
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
        return ResponseEntity.ok(clubName+"에 "+ userTel+" 님이 성공적으로 가입되었습니다.");
    }
}
