package com.triptalk.triptalk.service;

import com.triptalk.triptalk.dto.UserDataDto;
import com.triptalk.triptalk.dto.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
  UserDto createUser(UserDto userDto);
  UserDto getUserByUserId(Long id);
  String deleteUser(String token);
  List<UserDataDto> getAllUser();
  String updateUserNickname(Long targetUserId, String targetNickname);
  String updateUserProfileUrl(Long targetUserId, String targetProfileUrl);
}