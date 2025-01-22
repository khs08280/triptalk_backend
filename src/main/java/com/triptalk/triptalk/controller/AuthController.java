package com.triptalk.triptalk.controller;

import com.triptalk.triptalk.dto.AuthRequestDto;
import com.triptalk.triptalk.dto.UserDto;
import com.triptalk.triptalk.service.JwtService;
import com.triptalk.triptalk.service.UserService;
import com.triptalk.triptalk.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
  public ResponseEntity<ApiResponse<UserDto>> signUp(@RequestBody @Valid UserDto userDto) {
    try {
      UserDto createdUser = userService.createUser(userDto);
      return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(createdUser));
    } catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("회원가입 중 오류가 발생했습니다."));
    }
  }

  @PostMapping("/login")
  public ResponseEntity<ApiResponse<String>> login(@RequestBody @Valid AuthRequestDto authRequestDto) {
    try {
      Authentication authentication = authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(authRequestDto.getUsername(), authRequestDto.getPassword()));

      if (authentication.isAuthenticated()) {
        String token = jwtService.generateToken(authRequestDto.getUsername());
        return ResponseEntity.ok(ApiResponse.success("로그인 성공", token));
      } else {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("인증 실패"));
      }

    }catch (UsernameNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("사용자를 찾을 수 없습니다."));
    } catch (AuthenticationException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("잘못된 인증 정보입니다."));
    }
  }

}
