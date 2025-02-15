package com.springboot.club_house_api_server.club.service;

import com.springboot.club_house_api_server.club.dto.SearchClubResponseDto;
import com.springboot.club_house_api_server.club.entity.ClubEntity;
import com.springboot.club_house_api_server.club.repository.ClubRepository;
import com.springboot.club_house_api_server.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClubService {
    private final ClubRepository clubRepository;

    //모든 클럽 조회 메서드
    //이 메서드는 사용자 가입 여부와 관계 없이 모든 클럽 반환 --> FE 렌더링용
    //ex) 모든 동호회 살펴보기
    public ResponseEntity<?> getAllClubs() {
        List<ClubEntity> clubs = clubRepository.findAll();
        if (clubs.isEmpty()) {
            return ResponseEntity.noContent().build();  // HTTP 204 No Content
        }
        return ResponseEntity.ok(clubs);
    }
    //이름으로 클럽 찾기
    public ResponseEntity<?> getClubByName(String clubName) {
        Optional<ClubEntity> club = clubRepository.findByClubName(clubName);
        if (club.isPresent()) {
            return ResponseEntity.ok(club.get());
        }
        return ResponseEntity.noContent().build(); //HTTP 204 No Content
    }
    //동호회 신규 생성 - 중복 이름 생성 불가능
    public ResponseEntity<?> addClub(String clubName, String clubDescription, String clubLogoURL, String clubBackgroundURL) {
        Optional<ClubEntity> clubOpt = clubRepository.findByClubName(clubName);
        if (clubOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 있는 클럽 이름입니다.");
        }

        ClubEntity club = new ClubEntity(clubName, clubDescription);
        if(clubLogoURL!=null && !clubLogoURL.equals("")) {
            club.setClubLogoURL(clubLogoURL);
        }
        if(clubBackgroundURL!=null && !clubBackgroundURL.equals("")) {
            club.setClubBackgroundURL(clubBackgroundURL);
        }

        clubRepository.save(club);
        return ResponseEntity.ok(club);
    }

    //클럽 소속 모든 유저 찾기
    public ResponseEntity<?> findAllClubMembers(Long clubId){
        Optional<ClubEntity> clubOpt = clubRepository.findById(clubId);
        if(!clubOpt.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("club Id에 해당하는 클럽이 없습니다.");
        }
        List<UserEntity> clubUsers = clubRepository.findAllClubMembers(clubId);
        List<List<String>> userInfos = new ArrayList<>();
        for(UserEntity user : clubUsers){
            List<String> userInfo = new ArrayList<>();
            userInfo.add(user.getUserName());
            userInfo.add(user.getUserTel());
            userInfos.add(userInfo);
        }
        return ResponseEntity.ok(userInfos);
    }

//    private long club_id;
//    private String clubName;
//    private String clubDescription;
//    private String clubLogoURL;
//    private String clubBackgroundImageURL;
//    private LocalDateTime clubWhenCreated;

    public ResponseEntity<?> findAllClubsByUserId(long userId){
        List<ClubEntity> allClubs = clubRepository.findClubsByUserId(userId);
        List<SearchClubResponseDto> result = new ArrayList<>();
        for(ClubEntity club : allClubs) {
            SearchClubResponseDto responseDto = new SearchClubResponseDto();
            responseDto.setClub_id(club.getClubId());
            responseDto.setClubName(club.getClubName());
            responseDto.setClubDescription(club.getClubDescription());
            responseDto.setClubLogoURL(club.getClubLogoURL());
            responseDto.setClubBackgroundImageURL(club.getClubBackgroundURL());
            responseDto.setClubWhenCreated(club.getClubCreatedAt());

            result.add(responseDto);
        }
        return ResponseEntity.ok(result);
    }
}
