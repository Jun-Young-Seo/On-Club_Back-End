package com.springboot.club_house_api_server.club.service;

import com.springboot.club_house_api_server.club.entity.ClubEntity;
import com.springboot.club_house_api_server.club.repository.ClubRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClubService {
    private final ClubRepository clubRepository;

    //모든 클럽 조회 메서드
    public ResponseEntity<?> getAllClubs() {
        List<ClubEntity> clubs = clubRepository.findAll();
        if (clubs.isEmpty()) {
            return ResponseEntity.noContent().build();  // HTTP 204 No Content
        }
        return ResponseEntity.ok(clubs);
    }
    public ResponseEntity<?> getClubByName(String clubName) {
        Optional<ClubEntity> club = clubRepository.findByClubName(clubName);
        if (club.isPresent()) {
            return ResponseEntity.ok(club.get());
        }
        return ResponseEntity.noContent().build(); //HTTP 204 No Content
    }

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

}
