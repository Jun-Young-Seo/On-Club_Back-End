package com.springboot.club_house_api_server.membership.controller;

import com.springboot.club_house_api_server.membership.dto.MembershipJoinDto;
import com.springboot.club_house_api_server.membership.dto.MembershipModifyDto;
import com.springboot.club_house_api_server.membership.entity.MembershipEntity;
import com.springboot.club_house_api_server.membership.repository.MembershipRepository;
import com.springboot.club_house_api_server.membership.service.MembershipService;
import com.springboot.club_house_api_server.user.dto.JoinRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/membership")
public class MembershipController {
    private final MembershipService membershipService;


    @PostMapping("/join/request")
    public ResponseEntity<?> joinRequestToClub(@RequestBody MembershipJoinDto membershipJoinDto) throws ExecutionException, InterruptedException {
        return membershipService.joinRequestToClub(membershipJoinDto.getUserId(), membershipJoinDto.getClubId());
    }

    @PostMapping("/join/approve")
    public ResponseEntity<?> joinApproveToClub(@RequestBody MembershipJoinDto membershipJoinDto) throws ExecutionException, InterruptedException {
        return membershipService.approveJoinRequest(membershipJoinDto.getUserId(), membershipJoinDto.getClubId());
    }

    @PostMapping("/join/reject")
    public ResponseEntity<?> joinRejectToClub(@RequestBody MembershipJoinDto membershipJoinDto){
        return membershipService.rejectJoinRequest(membershipJoinDto.getUserId(), membershipJoinDto.getClubId());
    }

    //서비스 회원이지만 멤버십이 아닌 경우
    @PostMapping("/join/direct-user")
    public ResponseEntity<?> joinDirectMembershipWhenUserExist(@RequestParam String userTel, @RequestParam long clubId){
        return membershipService.joinDirectMembershipWhenUserExist(userTel, clubId);
    }

    //서비스 회원도 아닌 경우
    @PostMapping("/join/direct-not-user")
    public ResponseEntity<?> joinDirectMembershipWhenUserNotExist(@RequestBody JoinRequestDto joinRequestDto, @RequestParam long clubId){
        return membershipService.joinDirectMembershipWhenUserNotExist(joinRequestDto, clubId);
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
