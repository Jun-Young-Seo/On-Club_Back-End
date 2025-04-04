package com.springboot.club_house_api_server.user.dto;

import com.springboot.club_house_api_server.user.entity.UserEntity;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfoDto {
    private String userName;
    private String userTel;
    private String region;
    private UserEntity.Gender gender;
    private LocalDate birthDate;
    private int career;
//    private String recommender; //추천인


}
