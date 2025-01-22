package com.triptalk.triptalk.controller;

import com.triptalk.triptalk.domain.entity.User;
import com.triptalk.triptalk.dto.NicknameDto;
import com.triptalk.triptalk.dto.ProfileDto;
import com.triptalk.triptalk.dto.UserDataDto;
import com.triptalk.triptalk.dto.UserDto;
import com.triptalk.triptalk.exception.NicknameDuplicationException;
import com.triptalk.triptalk.exception.UserNotFoundException;
import com.triptalk.triptalk.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

  private final UserService userService;

  @PostMapping("/logout")
  public ResponseEntity<?> logout(Authentication authentication){
    if (authentication != null && authentication.isAuthenticated()) {
      String username = authentication.getName();
      log.info("{} 사용자가 로그아웃 되었습니다.", username);
    } else {
      log.info("익명 사용자가 로그아웃 되었습니다.");
    }

    SecurityContextHolder.clearContext();
    return ResponseEntity.ok().build();
  }


  @DeleteMapping
  public ResponseEntity<?> deleteUser(@AuthenticationPrincipal UserDetails userDetails){
    try {
      // 인증된 사용자 정보에서 username 추출
      String username = userDetails.getUsername();

      // 회원 탈퇴 처리
      String message = userService.deleteUser(username);

      return ResponseEntity.ok(message);
    } catch (UsernameNotFoundException ex) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
              .body("사용자를 찾을 수 없습니다.");
    } catch (Exception ex) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
              .body("알 수 없는 오류가 발생했습니다.");
    }
  }

  @GetMapping
  public ResponseEntity<?> getAllUser(){
    List<UserDataDto> allUser = userService.getAllUser();
    return ResponseEntity.ok(allUser);
  }

  @PatchMapping("/me/nickname")
  public ResponseEntity<?> updateNickname(@RequestBody NicknameDto nicknameDto, Authentication authentication){
    try{
      Long userId = ((User) authentication.getPrincipal()).getId();
      String message = userService.updateUserNickname(userId, nicknameDto.getNickname());
      return ResponseEntity.ok(message);
    } catch (Exception e) {
      log.error("닉네임 변경 중 예외 발생:", e);
      return ResponseEntity.internalServerError().body("닉네임 변경 중 오류가 발생했습니다.");
    }
  }

  @PatchMapping("/me/nickname")
  public ResponseEntity<?> updateProfileUrl(@RequestBody ProfileDto profileDto, Authentication authentication){
    try{
      Long userId = ((User) authentication.getPrincipal()).getId();
      String message = userService.updateUserProfileUrl(userId, profileDto.getProfileImageUrl());
      return ResponseEntity.ok(message);
    } catch (Exception e) {
      log.error("프로필 사진 변경 중 예외 발생:", e);
      return ResponseEntity.internalServerError().body("프로필 사진 변경 중 오류가 발생했습니다.");
    }
  }
}
