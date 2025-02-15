package com.springboot.club_house_api_server.user.repository;

import com.springboot.club_house_api_server.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUserTel(String userTel);
}
