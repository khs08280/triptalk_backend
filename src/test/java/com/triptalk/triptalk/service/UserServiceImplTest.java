package com.triptalk.triptalk.service;

import com.triptalk.triptalk.domain.entity.User;
import com.triptalk.triptalk.dto.requestDto.UserRequestDto;
import com.triptalk.triptalk.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

  @InjectMocks
  private UserServiceImpl userService;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private UserRepository userRepository;

  @Test
  @DisplayName("회원가입 테스트")
  public void 회원가입테스트(){
    //given
    UserRequestDto request = UserRequestDto.builder()
            .username("테스트")
            .email("test123@tetete.com")
            .nickname("테스트")
            .password("1234")
            .build();

    given(userRepository.existsByEmail("test123@tetete.com")).willReturn(false);

    given(passwordEncoder.encode("1234")).willReturn("encode_1234");

    given(userRepository.save(any(User.class))).willAnswer(i -> i.getArgument(0));

    //when
    UserRequestDto savedUser = userService.saveUser(request);

    //then
    assertThat(savedUser.getEmail()).isEqualTo("test123@tetete.com");
    assertThat(savedUser.getPassword()).isEqualTo("encode_1234");
    assertThat(savedUser.getUsername()).isEqualTo("테스트");

    verify(userRepository).existsByEmail("test123@tetete.com");
    verify(passwordEncoder).encode("encode_1234");
    verify(userRepository).save(any(User.class));



  }
}