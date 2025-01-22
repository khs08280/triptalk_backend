package com.triptalk.triptalk.service;

import com.triptalk.triptalk.domain.entity.User;
import com.triptalk.triptalk.dto.UserDataDto;
import com.triptalk.triptalk.dto.UserDto;
import com.triptalk.triptalk.exception.NicknameDuplicationException;
import com.triptalk.triptalk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;

  @Override
  public List<UserDataDto> getAllUser() {
    List<User> allUsers = userRepository.findAll();


    return allUsers.stream()
            .map(UserDataDto::fromEntity)
            .collect(Collectors.toList());
  }

  @Override
  public String updateUserProfileUrl(Long targetUserId, String targetProfileUrl) {
    User user = userRepository.findById(targetUserId)
            .orElseThrow(() -> new UsernameNotFoundException("해당 유저를 찾지 못했습니다."));

    user.updateProfileUrl(targetProfileUrl);

    return "프로필 사진이 성공적으로 변경되었습니다.";
  }

  @Override
  @Transactional
  public String updateUserNickname(Long targetUserId, String targetNickname) {
    if (userRepository.existsByNickname(targetNickname)) {
      throw new NicknameDuplicationException("이미 사용 중인 닉네임입니다.");
    }
    log.info(targetNickname);

    User user = userRepository.findById(targetUserId)
            .orElseThrow(() -> new UsernameNotFoundException("해당 유저를 찾지 못했습니다."));

    user.updateNickname(targetNickname);

    User user2 = userRepository.findById(targetUserId)
            .orElseThrow(() -> new UsernameNotFoundException("해당 유저를 찾지 못했습니다."));
    log.info(user2.getNickname());
    return "닉네임이 성공적으로 변경되었습니다.";
  }

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
  public UserDataDto getUserByUserId(Long id) {
    User user = userRepository.findById(id)
            .orElseThrow(() -> new UsernameNotFoundException("일치하는 유저를 찾지 못했습니다."));
    return UserDataDto.fromEntity(user);
  }

  @Override
  public String deleteUser(String username) {
    User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("해당 유저를 찾지 못했습니다."));

    userRepository.delete(user);
    return "회원탈퇴가 성공적으로 처리되었습니다.";
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return userRepository.findByUsername(username)
            .orElseThrow(()-> new UsernameNotFoundException("일치하는 유저를 찾지 못했습니다."));
  }

}
