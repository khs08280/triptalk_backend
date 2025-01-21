package com.triptalk.triptalk.service;

import com.triptalk.triptalk.domain.entity.User;
import com.triptalk.triptalk.dto.UserDto;
import com.triptalk.triptalk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Transactional
  @Override
  public UserDto createUser(UserDto userDto) {

    userRepository.findByUsername(userDto.getUsername()).ifPresent(u -> {
      throw new IllegalArgumentException(("해당 아이디가 이미 존재합니다."));
    });

    userRepository.findByEmail(userDto.getEmail()).ifPresent(u -> {
      throw new IllegalArgumentException(("해당 이메일이 이미 존재합니다."));
    });

    User user = User.builder()
            .username(userDto.getUsername())
            .nickname(userDto.getNickname())
            .email(userDto.getEmail())
            .password(passwordEncoder.encode(userDto.getPassword()))
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

    User savedUser = userRepository.save(user);
    return UserDto.fromEntity(savedUser);
  }

  @Override
  public UserDto getUserByUserId(Long id) {
    User user = userRepository.findById(id)
            .orElseThrow(() -> new UsernameNotFoundException("일치하는 유저를 찾지 못했습니다."));
    return UserDto.fromEntity(user);
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return userRepository.findByUsername(username)
            .orElseThrow(()-> new UsernameNotFoundException("일치하는 유저를 찾지 못했습니다."));
  }
}
