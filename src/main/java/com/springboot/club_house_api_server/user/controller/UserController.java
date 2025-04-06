package com.springboot.club_house_api_server.user.controller;

import com.springboot.club_house_api_server.user.dto.JoinRequestDto;
import com.springboot.club_house_api_server.user.dto.LoginRequestDto;
import com.springboot.club_house_api_server.user.dto.LoginResponseDto;
import com.springboot.club_house_api_server.user.service.UserService;
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

    //로그인 엔드포인트
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto request){
        return userService.login(request);
    }
    //Access Token 재발급 엔드포인트
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDto> refresh(@RequestHeader("Authorization") String refreshToken){
//        System.out.println(refreshToken);
        if(refreshToken!=null && refreshToken.startsWith("Bearer ")){
            refreshToken = refreshToken.substring(7);
        }
        LoginResponseDto response = userService.refreshToken(refreshToken);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String refreshToken){
        String logoutMsg = userService.logout(refreshToken);
        return ResponseEntity.ok(logoutMsg);
    }

    @GetMapping("/info")
    public ResponseEntity<?> getUserInfo(@RequestParam long userId){
        return userService.getUserInfo(userId);
    }
    //Http-Only Cookie 설정 메소드
    //2025-03-21 추가
    //SSL 설정 이슈로 보류 ㅠㅠㅠ
//    private void setCookie(HttpServletResponse response, String name, String value, int maxAge) {
//        ResponseCookie cookie = ResponseCookie.from(name, value)
//                .httpOnly(true)
//                .secure(false) // ✅ HTTPS가 아니므로 false (배포 시 true로 변경)
//                .path("/")
//                .domain("43.201.191.12")
//                .sameSite("Lax") // ✅ Cross-Origin 요청에서 필요
//                .maxAge(maxAge)
//                .build();
//
//        response.addHeader("Set-Cookie", cookie.toString());
//    }

}
