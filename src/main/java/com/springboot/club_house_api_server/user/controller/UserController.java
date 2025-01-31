package com.springboot.club_house_api_server.user.controller;

import com.springboot.club_house_api_server.user.dto.JoinRequestDto;
import com.springboot.club_house_api_server.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/user")
public class UserController {
    private final UserService userService;
    @PostMapping("/join")
    public ResponseEntity<String> join(@RequestBody JoinRequestDto request){
        userService.join(request);
        return ResponseEntity.ok("회원가입 성공");
    }
}
