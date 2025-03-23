package com.springboot.club_house_api_server.jwt.customobj;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ClubUserDetails implements UserDetails {
    private final String userId;
    private final Map<Long,String> clubRoles; // [클럽ID : 역할]

    public ClubUserDetails(String userId, Map<Long,String> clubRoles) {
        this.userId = userId;
        this.clubRoles = clubRoles;
    }

    //특정 클럽의 역할 반환
    public String getRoleForClub(Long clubId) {
        return clubRoles.get(clubId);
    }


    //---------여기부터 인터페이스 오버라이딩----------
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for(String role : clubRoles.values()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_"+role));
        }
        return authorities;
    }

    //pwd 없음
    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return userId;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
