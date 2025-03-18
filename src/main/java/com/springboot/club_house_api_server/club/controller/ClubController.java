package com.springboot.club_house_api_server.club.controller;

import com.springboot.club_house_api_server.club.dto.ClubRequestDto;
import com.springboot.club_house_api_server.club.dto.SearchClubResponseDto;
import com.springboot.club_house_api_server.club.entity.ClubEntity;
import com.springboot.club_house_api_server.club.service.ClubService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/club")
public class ClubController {
    private final ClubService clubService;

    @PostMapping("/add")
    public ResponseEntity<?> addClub(@RequestBody ClubRequestDto clubRequestDto){
        return clubService.addClub(
                clubRequestDto.getClubName(),
                clubRequestDto.getClubDescription(),
                clubRequestDto.getClubLogoURL(),
                clubRequestDto.getClubBackgroundURL()
        );
    }

    @GetMapping("/find/all")
    public ResponseEntity<List<SearchClubResponseDto>> findAllClubs(){

       return clubService.getAllClubs();
    }

    @GetMapping("/find/by-name")
    public ResponseEntity<?> findClubByName(@RequestParam String clubName){
        return clubService.getClubByName(clubName);
    }

    @GetMapping("/find/all-members")
    public ResponseEntity<?> findAllMembers(@RequestParam long clubId){
        return clubService.findAllClubMembers(clubId);
    }
    //유저 ID로 가입한 모든 동호회 조회
    @GetMapping("/find/by-user_id")
    public ResponseEntity<?> findClubByUserId(@RequestParam long userId){
        return clubService.findAllClubsByUserId(userId);
    }
}
