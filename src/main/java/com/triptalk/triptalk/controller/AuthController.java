package com.triptalk.triptalk.controller;

import com.triptalk.triptalk.dto.AuthRequestDto;
import com.triptalk.triptalk.dto.UserDto;
import com.triptalk.triptalk.service.JwtService;
import com.triptalk.triptalk.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

  private final UserService userService;
  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;

  @PostMapping("/join")
  public ResponseEntity<?> join(@RequestBody @Valid UserDto userDto) {
    try {
      UserDto createdUser = userService.createUser(userDto);
      return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    } catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
  }

  @PostMapping("login")
  public ResponseEntity<?> login(@RequestBody @Valid AuthRequestDto authRequestDto){
    try {
      Authentication authentication = authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(authRequestDto.getUsername(), authRequestDto.getPassword()));

      if (authentication.isAuthenticated()) {
        String token = jwtService.generateToken(authRequestDto.getUsername());
        return ResponseEntity.ok(token);
      } else {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증 실패");
      }
    } catch (UsernameNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    } catch (AuthenticationException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("무효한 권한");
    }
  }
}
