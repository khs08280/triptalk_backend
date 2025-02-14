package com.triptalk.triptalk.service;

import com.triptalk.triptalk.domain.entity.User;
import com.triptalk.triptalk.dto.responseDto.UserResponseDto;
import com.triptalk.triptalk.dto.requestDto.UserRequestDto;
import com.triptalk.triptalk.exception.BadRequestException;
import com.triptalk.triptalk.exception.DuplicatedException;
import com.triptalk.triptalk.exception.ResourceNotFoundException;
import com.triptalk.triptalk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;

  @Override
  @Transactional(readOnly = true)
  public List<UserResponseDto> getAllUserList() {
    List<User> allUsers = userRepository.findAll();


    return allUsers.stream()
            .map(UserResponseDto::fromEntity)
            .collect(Collectors.toList());
  }

  public UserResponseDto saveRefreshToken(String username, String refreshToken){
    User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("해당 유저를 찾을 수 없습니다."));
    user.updateRefreshToken(refreshToken);
    userRepository.save(user);

    return UserResponseDto.fromEntity(user);
  }

  @Override
  public String modifyUserProfileUrl(Long targetUserId, String targetProfileUrl) {
    User user = userRepository.findById(targetUserId)
            .orElseThrow(() -> new ResourceNotFoundException("해당 유저를 찾지 못했습니다."));

    user.updateProfileUrl(targetProfileUrl);

    return "프로필 사진이 성공적으로 변경되었습니다.";
  }

  @Override
  public String modifyUserNickname(Long targetUserId, String targetNickname) {
    if (userRepository.existsByNickname(targetNickname)) {
      throw new DuplicatedException("이미 사용 중인 닉네임입니다.");
    }
    User user = userRepository.findById(targetUserId)
            .orElseThrow(() -> new ResourceNotFoundException("해당 유저를 찾지 못했습니다."));

    user.updateNickname(targetNickname);

    log.info("수정된 닉네임 = {}", user.getNickname());
    return "닉네임이 성공적으로 변경되었습니다.";
  }

  @Override
  public UserResponseDto saveUser(UserRequestDto userDto) {

    if(userRepository.existsByUsername(userDto.getUsername())){
      throw new DuplicatedException(("해당 아이디가 이미 존재합니다."));
    }

    if(userRepository.existsByEmail(userDto.getEmail())){
      throw new DuplicatedException(("해당 이메일이 이미 존재합니다."));
    }

    if(!userDto.getPassword().equals(userDto.getConfirmPassword())){
      throw new BadRequestException("비밀번호와 비밀번호 확인이 같지 않습니다.");
    }

    User user = User.builder()
            .username(userDto.getUsername())
            .nickname(userDto.getNickname())
            .email(userDto.getEmail())
            .password(passwordEncoder.encode(userDto.getPassword()))
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

    User savedUser = userRepository.save(user);
    return UserResponseDto.fromEntity(savedUser);
  }

  @Override
  @Transactional(readOnly = true)
  public UserResponseDto getUser(Long id) {
    User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("해당 유저를 찾지 못했습니다."));
    return UserResponseDto.fromEntity(user);
  }

  @Override
  public String deleteUser(String username) {
    User user = userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("해당 유저를 찾지 못했습니다."));

    userRepository.delete(user);
    return "회원탈퇴가 성공적으로 처리되었습니다.";
  }

  @Override
  public UserDetails loadUserByUsername(String username) {
    return userRepository.findByUsername(username)
            .orElseThrow(()-> new ResourceNotFoundException("해당 유저를 찾지 못했습니다."));
  }

  public void logoutUser(Authentication authentication) {
    if (authentication != null && authentication.isAuthenticated()) {
      String username = authentication.getName();
      log.info("{} 사용자가 로그아웃 요청", username);

      // DB에서 refreshToken 제거
      userRepository.findByUsername(username).ifPresent(user -> {
        user.clearRefreshToken();
        userRepository.save(user);
      });
    } else {
      log.info("익명 사용자가 로그아웃 요청");
    }

    // SecurityContextHolder 초기화
    SecurityContextHolder.clearContext();
  }
}
