package com.triptalk.triptalk.controller;

import com.triptalk.triptalk.domain.entity.User;
import com.triptalk.triptalk.dto.requestDto.NicknameRequestDto;
import com.triptalk.triptalk.dto.requestDto.ProfileRequestDto;
import com.triptalk.triptalk.dto.responseDto.ApiResponse;
import com.triptalk.triptalk.dto.responseDto.UserResponseDto;
import com.triptalk.triptalk.service.UserService;
import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
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
  public ResponseEntity<ApiResponse<String>> logout(Authentication authentication) {
    // 1) Service 계층에 로그아웃(비즈니스 로직) 위임
    userService.logoutUser(authentication);

    // 2) 쿠키 만료(Access/Refresh 둘 다)
    ResponseCookie accessCookie = ResponseCookie.from("accessToken", "")
            .httpOnly(true)
            .secure(true)
            .path("/")
            .sameSite("None")
            .maxAge(0)
            .build();

    ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", "")
            .httpOnly(true)
            .secure(true)
            .path("/")
            .sameSite("None")
            .maxAge(0)
            .build();

    return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
            .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
            .body(ApiResponse.success("로그아웃 성공", null));
  }


  @DeleteMapping
  public ResponseEntity<ApiResponse<String>> deleteUser(@AuthenticationPrincipal UserDetails userDetails){
    try {
      String username = userDetails.getUsername();
      String message = userService.deleteUser(username);

      return ResponseEntity.ok(ApiResponse.success(message, null));
    } catch (UsernameNotFoundException ex) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("사용자를 찾을 수 없습니다."));
    } catch (Exception ex) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("알 수 없는 오류가 발생했습니다."));
    }
  }

  @GetMapping("/all")
  public ResponseEntity<ApiResponse<List<UserResponseDto>>> getAllUser() {
    List<UserResponseDto> allUser = userService.getAllUserList();
    return ResponseEntity.ok(ApiResponse.success("모든 사용자 조회 성공", allUser));
  }

  @GetMapping
  public ResponseEntity<ApiResponse<List<UserResponseDto>>> getUserListByNickname(
          @PathParam("nickname") String nickname
  ){
    List<UserResponseDto> userList = userService.getUserListByNickname(nickname);

    return ResponseEntity.ok(ApiResponse.success("닉네임 조회 성공", userList));
  }

  @GetMapping("/me")
  public ResponseEntity<ApiResponse<UserResponseDto>> getMyInfo(Authentication authentication) {
    Long userId = ((User) authentication.getPrincipal()).getId();
    UserResponseDto user = userService.getUser(userId);
    return ResponseEntity.ok(ApiResponse.success("내 정보 조회 성공", user));
  }

  @PatchMapping("/me/nickname")
  public ResponseEntity<ApiResponse<String>> updateNickname(@Valid @RequestBody NicknameRequestDto nicknameDto, Authentication authentication){
    try {
      Long userId = ((User) authentication.getPrincipal()).getId();
      String message = userService.modifyUserNickname(userId, nicknameDto.getNickname());
      return ResponseEntity.ok(ApiResponse.success(message, null));
    } catch (Exception e) {
      log.error("닉네임 변경 중 예외 발생:", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("닉네임 변경 중 오류가 발생했습니다."));
    }
  }

  @PatchMapping("/me/profileImageUrl")
  public ResponseEntity<ApiResponse<String>> updateProfileImageUrl(@Valid @RequestBody ProfileRequestDto profileDto, Authentication authentication){
    try {
      Long userId = ((User) authentication.getPrincipal()).getId();
      String message = userService.modifyUserProfileUrl(userId, profileDto.getProfileImageUrl());
      return ResponseEntity.ok(ApiResponse.success(message, null));
    } catch (Exception e) {
      log.error("프로필 사진 변경 중 예외 발생:", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("프로필 사진 변경 중 오류가 발생했습니다."));
    }
  }

}
