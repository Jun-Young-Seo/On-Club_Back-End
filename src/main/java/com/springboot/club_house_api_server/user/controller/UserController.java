package com.springboot.club_house_api_server.user.controller;

import com.springboot.club_house_api_server.user.dto.JoinRequestDto;
import com.springboot.club_house_api_server.user.dto.LoginRequestDto;
import com.springboot.club_house_api_server.user.dto.LoginResponseDto;
import com.springboot.club_house_api_server.user.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/user")
public class UserController {
    private final UserService userService;
    //회원가입 엔드포인트
    @PostMapping("/join")
    public ResponseEntity<String> join(@RequestBody JoinRequestDto request){
        userService.join(request);
        return ResponseEntity.ok("회원가입 성공");
    }
    //로그인 엔드포인트
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto request, HttpServletResponse response){
        LoginResponseDto r= userService.login(request);
        setCookie(response, "accessToken", r.getAccessToken(), 15); // 15분
        setCookie(response, "refreshToken", r.getRefreshToken(), 60); // 1시간

        return ResponseEntity.ok(r);
    }
    //Access Token 재발급 엔드포인트
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDto> refresh(@RequestHeader("Authorization") String refreshToken){
        if(refreshToken!=null || !refreshToken.startsWith("Bearer ")){
            refreshToken = refreshToken.substring(7);
        }
        LoginResponseDto response = userService.refreshToken(refreshToken);
        return ResponseEntity.ok(response);
    }

    //For Test Exception Filter
    @GetMapping("/test")
    public ResponseEntity<String> test(){
        return new ResponseEntity<>("Correct Token", HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String refreshToken){
        String logoutMsg = userService.logout(refreshToken);
        return ResponseEntity.ok(logoutMsg);
    }

    //Http-Only Cookie 설정 메소드
    //2025-03-21 추가
    private void setCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }

}
