package com.triptalk.triptalk.service;

import com.triptalk.triptalk.dto.responseDto.UserResponseDto;
import com.triptalk.triptalk.dto.requestDto.UserRequestDto;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
  UserRequestDto saveUser(UserRequestDto userDto);
  UserResponseDto getUser(Long id);
  String deleteUser(String token);
  List<UserResponseDto> getAllUserList();
  String modifyUserNickname(Long targetUserId, String targetNickname);
  String modifyUserProfileUrl(Long targetUserId, String targetProfileUrl);
}