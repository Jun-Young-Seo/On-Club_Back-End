package com.springboot.club_house_api_server.jwt.filter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtExceptionFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try{
            filterChain.doFilter(request, response);
        }//만료 토큰인 경우
        catch(ExpiredJwtException e){

            handleException(response,"Expired","토큰이 만료되었습니다.");
        }//Expired의 상위 예외 클래스. 여기 포함되면 변조되었을 수 있음
        catch (JwtException e){
            handleException(response,"Invalid","토큰이 잘못되었습니다.");
        }//그 외 모든 예외
        catch(Exception e){
            handleException(response,"ServerErr","서버 내부 에러");
            e.printStackTrace();
        }
    }
    private void handleException(HttpServletResponse response, String error, String msg) {
        if (!response.isCommitted()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            try {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
