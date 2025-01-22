package com.triptalk.triptalk.config;

import com.triptalk.triptalk.service.JwtService;
import com.triptalk.triptalk.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
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
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
          throws ServletException, IOException {
    final String authHeader = request.getHeader("Authorization");
    final String jwtToken;
    final String username;

    String requestPath = request.getServletPath();
    if (requestPath.startsWith("/api/v1/auth")) {
      // 로그인이나 회원가입 등 인증이 필요 없는 경로는 필터를 건너뛴다.
      filterChain.doFilter(request, response);
      return;
    }

    // 1. Authorization 헤더가 없거나 Bearer 토큰 형식이 아닌 경우, 필터를 통과시킴
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }

    // 2. 토큰에서 JWT 값 추출
    jwtToken = authHeader.substring(7);

    // 3. JWT에서 사용자 이름(username) 추출
    username = jwtService.extractUsername(jwtToken);

    // 4. SecurityContext에 인증 정보가 없고, username이 유효한 경우 처리
    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      // UserDetailsService를 통해 사용자 정보 로드
      UserDetails userDetails = userDetailsService.loadUserByUsername(username);

      // JWT 유효성 검증
      if (jwtService.isTokenValid(jwtToken, userDetails)) {
        // 인증 객체 생성 및 SecurityContext에 설정
        var authToken = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
      }
    }

    // 5. 다음 필터로 요청 전달
    filterChain.doFilter(request, response);
  }
}