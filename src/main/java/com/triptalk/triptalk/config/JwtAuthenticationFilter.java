package com.triptalk.triptalk.config;

import com.triptalk.triptalk.service.JwtService;
import com.triptalk.triptalk.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  private final UserDetailsService userDetailsService;

  @Override
  protected void doFilterInternal(
          HttpServletRequest request,
          HttpServletResponse response,
          FilterChain filterChain
  ) throws ServletException, IOException {

    // 1) /api/v1/auth 로 시작하는 경로는 (로그인, 회원가입 등) 인증 제외
    String requestPath = request.getServletPath();
    if (requestPath.startsWith("/api/v1/auth")) {
      filterChain.doFilter(request, response);
      return;
    }

    // 2) 쿠키에서 accessToken 추출 (이름 "accessToken"이라고 가정)
    String jwtToken = null;
    if (request.getCookies() != null) {
      for (Cookie cookie : request.getCookies()) {
        if ("accessToken".equals(cookie.getName())) {
          jwtToken = cookie.getValue();
          break;
        }
      }
    }

    // 3) 쿠키에 토큰이 없으면 → 그냥 다음 필터로
    if (jwtToken == null || jwtToken.trim().isEmpty()) {
      filterChain.doFilter(request, response);
      return;
    }

    // 4) JWT에서 사용자 이름(username) 추출
    String username;
    try {
      username = jwtService.extractUsername(jwtToken);
    } catch (Exception e) {
      // JWT가 유효하지 않으면 다음 필터
      filterChain.doFilter(request, response);
      return;
    }

    // 5) SecurityContext에 인증 정보가 없고, username이 유효한 경우 처리
    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      // UserDetailsService를 통해 사용자 정보 로드
      UserDetails userDetails = userDetailsService.loadUserByUsername(username);

      // 토큰 유효성 검증
      if (jwtService.isTokenValid(jwtToken, userDetails)) {
        // 인증 객체 생성 및 SecurityContext에 설정
        var authToken = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authToken);
      }
    }

    // 6) 다음 필터로 요청 전달
    filterChain.doFilter(request, response);
  }
}