package com.triptalk.triptalk.service;

import com.triptalk.triptalk.dto.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
  UserDto createUser(UserDto userDto);
  UserDto getUserByUserId(Long id);
}