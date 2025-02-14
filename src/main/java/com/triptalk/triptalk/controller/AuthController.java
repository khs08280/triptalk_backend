package com.triptalk.triptalk.controller;

import com.triptalk.triptalk.domain.entity.User;
import com.triptalk.triptalk.dto.requestDto.LoginRequestDto;
import com.triptalk.triptalk.dto.requestDto.UserRequestDto;
import com.triptalk.triptalk.dto.responseDto.UserResponseDto;
import com.triptalk.triptalk.exception.InvalidTokenException;
import com.triptalk.triptalk.exception.ResourceNotFoundException;
import com.triptalk.triptalk.service.JwtService;
import com.triptalk.triptalk.service.UserService;
import com.triptalk.triptalk.dto.responseDto.ApiResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Slf4j
@RequiredArgsConstructor
public class AuthController {

  private final UserService userService;
  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;

  @PostMapping("/signup")
  public ResponseEntity<ApiResponse<UserResponseDto>> signUp(@RequestBody @Valid UserRequestDto userDto) {
    UserResponseDto createdUser = userService.saveUser(userDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(createdUser));
  }

  @PostMapping("/login")
  public ResponseEntity<ApiResponse<UserResponseDto>> login(@RequestBody @Valid LoginRequestDto authRequestDto) {

    // 1. 유저 인증
    Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                    authRequestDto.getUsername(),
                    authRequestDto.getPassword()
            )
    );
    if (!authentication.isAuthenticated()) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
              .body(ApiResponse.error("인증 실패"));
    }

    // 2. Access & Refresh 토큰 생성
    String username = authRequestDto.getUsername();
    String accessToken = jwtService.generateAccessToken(username);
    String refreshToken = jwtService.generateRefreshToken(username);

    // == (중요) 생성된 Refresh Token을 User 엔티티에 저장 ==
    UserResponseDto user = userService.saveRefreshToken(username, refreshToken);

    // 3. HTTP-Only 쿠키로 내려주기 (Spring 5+ ResponseCookie)
    ResponseCookie accessCookie = ResponseCookie.from("accessToken", accessToken)
            .httpOnly(true)
            .secure(true)
            .sameSite("None")
            .path("/")
            .maxAge(15 * 60) // 15분
            .build();

    ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
            .httpOnly(true)
            .secure(true)
            .sameSite("None")
            .path("/")
            .maxAge(14 * 24 * 60 * 60)
            .build();

    return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
            .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
            .body(ApiResponse.success("로그인 성공", user));
  }


  @PostMapping("/refresh")
  public ResponseEntity<?> refreshToken(HttpServletRequest request) {
    // 1) 쿠키에서 refreshToken 추출
    Cookie[] cookies = request.getCookies();
    if (cookies == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
              .body(ApiResponse.error("쿠키 없음"));
    }

    String refreshTokenFromCookie = null;
    for (Cookie cookie : cookies) {
      if ("refreshToken".equals(cookie.getName())) {
        refreshTokenFromCookie = cookie.getValue();
        break;
      }
    }
    if (refreshTokenFromCookie == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
              .body(ApiResponse.error("Refresh Token 쿠키 없음"));
    }

    try {
      // 2) 서비스 계층에서 비즈니스 로직 처리 (검증, DB조회, 새 Access Token 생성)
      String newAccessToken = jwtService.refreshAccessToken(refreshTokenFromCookie);

      // 3) 새 Access Token 쿠키 생성
      ResponseCookie newAccessCookie = ResponseCookie.from("accessToken", newAccessToken)
              .httpOnly(true)
              .secure(true)
              .sameSite("None")
              .path("/")
              .maxAge(15 * 60) // 15분
              .build();

      // 4) 쿠키 헤더에 담아 응답
      return ResponseEntity.ok()
              .header(HttpHeaders.SET_COOKIE, newAccessCookie.toString())
              .body(ApiResponse.success("Access Token 재발급 성공", null));

    } catch (Exception e) {
      log.error("Refresh Token 재발급 실패: {}", e.getMessage());
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
              .body(ApiResponse.error("Refresh Token이 유효하지 않거나 매칭되지 않습니다."));
    }
  }

  @GetMapping("/check")
  public ResponseEntity<ApiResponse<UserResponseDto>> checkToken(HttpServletRequest request){
    Cookie[] cookies = request.getCookies();
    if (cookies == null) {
      throw new InvalidTokenException("accessToken 쿠키가 없습니다.");
    }

    String accessToken = null;
    for (Cookie cookie : cookies) {
      if ("accessToken".equals(cookie.getName())) {
        accessToken = cookie.getValue();
        break;
      }
    }
    if (accessToken == null) {
      throw new InvalidTokenException("accessToken 쿠키가 없습니다.");
    }

    jwtService.validateToken(accessToken);
    UserResponseDto user = jwtService.tokenToUserDto(accessToken);

    return ResponseEntity.ok(ApiResponse.success(user));
  }
}
