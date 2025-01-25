package com.triptalk.triptalk.controller;

import com.triptalk.triptalk.dto.requestDto.AuthRequestDto;
import com.triptalk.triptalk.dto.requestDto.UserRequestDto;
import com.triptalk.triptalk.service.JwtService;
import com.triptalk.triptalk.service.UserService;
import com.triptalk.triptalk.dto.responseDto.ApiResponse;
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
  public ResponseEntity<ApiResponse<UserRequestDto>> signUp(@RequestBody @Valid UserRequestDto userDto) {
    UserRequestDto createdUser = userService.saveUser(userDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(createdUser));
  }

  @PostMapping("/login")
  public ResponseEntity<ApiResponse<String>> login(@RequestBody @Valid AuthRequestDto authRequestDto) {

    Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(authRequestDto.getUsername(), authRequestDto.getPassword()));

    if (authentication.isAuthenticated()) {
      String token = jwtService.generateToken(authRequestDto.getUsername());
      return ResponseEntity.ok(ApiResponse.success("로그인 성공", token));
    } else {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("인증 실패"));
    }

  }

}
