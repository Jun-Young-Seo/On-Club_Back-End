package com.springboot.club_house_api_server.user.controller;

import com.springboot.club_house_api_server.user.dto.JoinRequestDto;
import com.springboot.club_house_api_server.user.dto.LoginRequestDtoForIOS;
import com.springboot.club_house_api_server.user.dto.LoginRequestDtoForWeb;
import com.springboot.club_house_api_server.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;
    //회원가입 엔드포인트
    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody JoinRequestDto request){
        return userService.join(request);
    }

    //기존 회원인데 운영진 권한으로 멤버십 가입시키는 경우
//    @PostMapping("/join-direct")
//    public ResponseEntity<?> joinDirect(@RequestParam String userTel, @RequestParam long clubId){
//        return userService.joinMembershipAlreadyUser(userTel, clubId);
//    }

    //로그인 엔드포인트 - 웹용
    @PostMapping("/login")
    public ResponseEntity<?> loginForWeb(@RequestBody LoginRequestDtoForWeb request, HttpServletResponse response){
        return userService.loginForWeb(request, response);
    }

    //로그인 - ios용 ( 토큰 발급 - 저장, 갱신 )
    @PostMapping("/login/ios")
    public ResponseEntity<?> loginForIOS(@RequestBody LoginRequestDtoForIOS request){
        return userService.loginForIOS(request);
    }

    //Access Token 재발급 엔드포인트
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(HttpServletRequest request, HttpServletResponse response){
        return userService.refreshToken(request, response);

    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authHeader) {
        String bearerPrefix = "Bearer ";
        String refreshToken = authHeader.substring(bearerPrefix.length()).trim();

        String logoutMsg = userService.logout(authHeader);
        return ResponseEntity.ok(logoutMsg);
    }

    @GetMapping("/info")
    public ResponseEntity<?> getUserInfo(@RequestParam long userId){
        return userService.getUserInfo(userId);
    }

    @GetMapping("/info/mypage")
    public ResponseEntity<?> getUserInfoForMyPage(@RequestParam long userId, @RequestParam int year, @RequestParam int month){
        return userService.getUserInfoForMyPage(userId, year, month);
    }

}
