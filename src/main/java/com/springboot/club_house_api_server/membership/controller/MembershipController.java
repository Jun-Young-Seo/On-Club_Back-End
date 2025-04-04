package com.springboot.club_house_api_server.membership.controller;

import com.springboot.club_house_api_server.membership.dto.MembershipJoinDto;
import com.springboot.club_house_api_server.membership.dto.MembershipModifyDto;
import com.springboot.club_house_api_server.membership.entity.MembershipEntity;
import com.springboot.club_house_api_server.membership.repository.MembershipRepository;
import com.springboot.club_house_api_server.membership.service.MembershipService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/membership")
public class MembershipController {
    private final MembershipService membershipService;


    @PostMapping("/join/request")
    public ResponseEntity<?> joinRequestToClub(@RequestBody MembershipJoinDto membershipJoinDto){
        return membershipService.joinRequestToClub(membershipJoinDto.getUserId(), membershipJoinDto.getClubId());
    }

    @PostMapping("/join/approve")
    public ResponseEntity<?> joinApproveToClub(@RequestBody MembershipJoinDto membershipJoinDto){
        return membershipService.approveJoinRequest(membershipJoinDto.getUserId(), membershipJoinDto.getClubId());
    }

    @PostMapping("/join/reject")
    public ResponseEntity<?> joinRejectToClub(@RequestBody MembershipJoinDto membershipJoinDto){
        return membershipService.rejectJoinRequest(membershipJoinDto.getUserId(), membershipJoinDto.getClubId());
    }

    @GetMapping("/all-members")
    public ResponseEntity<?> getAllMembers(@RequestParam long clubId){
        return membershipService.getAllMembershipsByClubId(clubId);
    }

    @DeleteMapping("/withdraw")
    public ResponseEntity<?> withdrawClub(@RequestParam long membershipId){
        return membershipService.withdrawClub(membershipId);
    }

    @PatchMapping("/modify")
    public ResponseEntity<?> modifyMembershipRole(@RequestBody MembershipModifyDto membershipModifyDto){
        return membershipService.modifyMembershipRole(membershipModifyDto);
    }

    @GetMapping("/my-role")
    public ResponseEntity<?> getMyRole(@RequestParam long userId, @RequestParam long clubId){
        return membershipService.getMyRole(userId, clubId);
    }
}
