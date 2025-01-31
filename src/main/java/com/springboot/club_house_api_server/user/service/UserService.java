package com.springboot.club_house_api_server.user.service;

import com.springboot.club_house_api_server.jwt.generator.JwtTokenGenerator;
import com.springboot.club_house_api_server.user.dto.JoinRequestDto;
import com.springboot.club_house_api_server.user.dto.LoginRequestDto;
import com.springboot.club_house_api_server.user.dto.LoginResponseDto;
import com.springboot.club_house_api_server.user.entity.UserEntity;
import com.springboot.club_house_api_server.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenGenerator jwtTokenGenerator;

    //회원가입
    public void join(JoinRequestDto joinRequestDto){
        Optional<UserEntity> userExist = userRepository.findByUserTel(joinRequestDto.getUserTel());
        if(userExist.isPresent()){
            throw new IllegalArgumentException("이미 가입된 전화번호입니다.");
        }
        String hashPwd = passwordEncoder.encode(joinRequestDto.getPassword());

        UserEntity userEntity = UserEntity.builder()
                .userName(joinRequestDto.getUserName())
                .password(hashPwd)
                .userTel(joinRequestDto.getUserTel())
                .build();
        userRepository.save(userEntity);
    }
    //Login
    public LoginResponseDto login(LoginRequestDto loginRequestDto){
        Optional<UserEntity> user = userRepository.findByUserTel(loginRequestDto.getUserTel());
        if(!user.isPresent()){
            throw new IllegalArgumentException("존재하지 않는 전화번호입니다.");
        }
        if(!passwordEncoder.matches(loginRequestDto.getPassword(), user.get().getPassword())){
            throw new IllegalArgumentException("비밀번호가 틀렸습니다.");
        }
        long accessTokenValidity = 1000 * 60 * 15; // 15분 - accessToken
        long refreshTokenValidity = 1000 * 60 * 60; // 1시간 - RefreshToken

        String accessToken = jwtTokenGenerator.createToken(loginRequestDto.getUserTel(), "ROLE_USER",accessTokenValidity);
        String refreshToken = jwtTokenGenerator.createToken(loginRequestDto.getUserTel(), "ROLE_USER",refreshTokenValidity);

        return new LoginResponseDto(accessToken, refreshToken);
    }

    //refresh Token
    public LoginResponseDto refreshToken(String refreshToken){
        if(refreshToken == null || !jwtTokenGenerator.validateToken(refreshToken)){
            throw new IllegalArgumentException("Invalid Refresh Token");
        }

        String userTel = jwtTokenGenerator.getUserTel(refreshToken);
        long accessTokenValidity = 1000 * 60 * 15;
        String newAccessToken = jwtTokenGenerator.createToken(userTel, "ROLE_USER", accessTokenValidity);

        return new LoginResponseDto(newAccessToken, refreshToken);
    }
}
