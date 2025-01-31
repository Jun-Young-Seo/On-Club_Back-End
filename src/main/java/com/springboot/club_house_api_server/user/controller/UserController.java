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
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto request){
        LoginResponseDto r= userService.login(request);
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
}
