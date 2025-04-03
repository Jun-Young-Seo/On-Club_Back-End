package com.springboot.club_house_api_server.membership.controller;

import com.springboot.club_house_api_server.membership.dto.MembershipJoinDto;
import com.springboot.club_house_api_server.membership.service.MembershipService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/membership")
public class MembershipController {
    private final MembershipService membershipService;

    @PostMapping("/join")
    public ResponseEntity<?> joinToClub(@RequestBody MembershipJoinDto membershipJoinDto){
        return membershipService.joinToClub(
                membershipJoinDto.getUserId(),
                membershipJoinDto.getClubId(),
                membershipJoinDto.getRole());
    }

    @GetMapping("/all-members")
    public ResponseEntity<?> getAllMembers(@RequestParam long clubId){
        return membershipService.getAllMembershipsByClubId(clubId);
    }
}
