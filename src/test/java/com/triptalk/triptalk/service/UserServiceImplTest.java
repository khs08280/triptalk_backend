package com.triptalk.triptalk.service;

import com.triptalk.triptalk.domain.entity.User;
import com.triptalk.triptalk.dto.requestDto.UserRequestDto;
import com.triptalk.triptalk.dto.responseDto.UserResponseDto;
import com.triptalk.triptalk.exception.DuplicatedException;
import com.triptalk.triptalk.exception.ResourceNotFoundException;
import com.triptalk.triptalk.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
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
            .username("테스트테스트123")
            .email("testtest123@tetete.com")
            .nickname("테스트테스트123")
            .password("1234")
            .build();

    given(userRepository.existsByEmail("testtest123@tetete.com")).willReturn(false);
    given(userRepository.existsByUsername("테스트테스트123")).willReturn(false);

    given(passwordEncoder.encode("1234")).willReturn("encode_1234");

    given(userRepository.save(any(User.class))).willAnswer(i -> i.getArgument(0));

    //when
    UserResponseDto savedUser = userService.saveUser(request);

    //then
    assertThat(savedUser.getEmail()).isEqualTo("testtest123@tetete.com");
    assertThat(savedUser.getUsername()).isEqualTo("테스트테스트123");

    verify(userRepository).existsByEmail("testtest123@tetete.com");
    verify(userRepository).existsByUsername("테스트테스트123");
    verify(userRepository).save(any(User.class));

  }

  @Test
  @DisplayName("유저 회원가입 - username 중복 예외")
  public void 회원가입_username중복예외(){
    UserRequestDto request = UserRequestDto.builder()
            .username("테스트테스트123")
            .email("testtest123@tetete.com")
            .nickname("테스트테스트123")
            .password("1234")
            .build();

    given(userRepository.existsByUsername("테스트테스트123")).willReturn(true);

    assertThatThrownBy(()-> userService.saveUser(request))
            .isInstanceOf(DuplicatedException.class)
            .hasMessage("해당 아이디가 이미 존재합니다.");

    verify(userRepository).existsByUsername("테스트테스트123");
    verify(userRepository, never()).existsByEmail(anyString());
    verify(userRepository, never()).save(any(User.class));

  }

  @Test
  @DisplayName("유저 회원가입 - email 중복 예외")
  public void 회원가입_email중복예외(){
    UserRequestDto request = UserRequestDto.builder()
            .username("테스트테스트123")
            .email("testtest123@tetete.com")
            .nickname("테스트테스트123")
            .password("1234")
            .build();

    given(userRepository.existsByUsername("테스트테스트123")).willReturn(false);
    given(userRepository.existsByEmail("testtest123@tetete.com")).willReturn(true);

    assertThatThrownBy(()-> userService.saveUser(request)).isInstanceOf(DuplicatedException.class).hasMessage("해당 이메일이 이미 존재합니다.");

    verify(userRepository).existsByUsername("테스트테스트123");
    verify(userRepository).existsByEmail("testtest123@tetete.com");
    verify(userRepository, never()).save(any(User.class));

  }

  @Test
  @DisplayName("getAllUserList - 정상적으로 유저 목록을 UserResponseDto로 변환")
  void getAllUserList_success() {
    // given
    List<User> users = new ArrayList<>();
    users.add(User.builder().id(1L).username("user1").nickname("nick1").build());
    users.add(User.builder().id(2L).username("user2").nickname("nick2").build());

    given(userRepository.findAll()).willReturn(users);

    // when
    List<UserResponseDto> result = userService.getAllUserList();

    // then
    assertThat(result).hasSize(2);
    assertThat(result.get(0).getUsername()).isEqualTo("user1");
    assertThat(result.get(0).getNickname()).isEqualTo("nick1");
    assertThat(result.get(1).getUsername()).isEqualTo("user2");
    assertThat(result.get(1).getNickname()).isEqualTo("nick2");

    verify(userRepository).findAll();
  }

  @Test
  @DisplayName("modifyUserProfileUrl - 유저가 존재할 때 프로필 변경 성공")
  void modifyUserProfileUrl_success() {
    // given
    Long userId = 100L;
    String newProfileUrl = "http://example.com/new_profile.png";

    User user = User.builder().id(userId).profileImageUrl("old_url").build();
    given(userRepository.findById(userId)).willReturn(Optional.of(user));

    // when
    String result = userService.modifyUserProfileUrl(userId, newProfileUrl);

    // then
    assertThat(result).isEqualTo("프로필 사진이 성공적으로 변경되었습니다.");
    assertThat(user.getProfileImageUrl()).isEqualTo(newProfileUrl); // 실제로 변경됐는지 확인

    verify(userRepository).findById(userId);
  }

  @Test
  @DisplayName("modifyUserProfileUrl - 유저가 존재하지 않을 때 UsernameNotFoundException 발생")
  void modifyUserProfileUrl_userNotFound() {
    // given
    Long userId = 999L;
    given(userRepository.findById(userId)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> userService.modifyUserProfileUrl(userId, "anyUrl"))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessage("해당 유저를 찾지 못했습니다.");

    verify(userRepository).findById(userId);
  }

  @Test
  @DisplayName("modifyUserNickname - 닉네임 중복 시 NicknameDuplicationException 발생")
  void modifyUserNickname_dupNickname() {
    // given
    Long userId = 100L;
    String newNick = "duplicateNick";

    given(userRepository.existsByNickname(newNick)).willReturn(true);

    // when & then
    assertThatThrownBy(() -> userService.modifyUserNickname(userId, newNick))
            .isInstanceOf(DuplicatedException.class)
            .hasMessage("이미 사용 중인 닉네임입니다.");

    verify(userRepository, never()).findById(anyLong());
  }

  @Test
  @DisplayName("modifyUserNickname - 유저 존재하지 않을 때 UsernameNotFoundException")
  void modifyUserNickname_userNotFound() {
    // given
    Long userId = 100L;
    String newNick = "newNick";

    // 닉네임은 사용중이 아님
    given(userRepository.existsByNickname(newNick)).willReturn(false);
    // 유저는 없음
    given(userRepository.findById(userId)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> userService.modifyUserNickname(userId, newNick))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessage("해당 유저를 찾지 못했습니다.");

    verify(userRepository).existsByNickname(newNick);
    verify(userRepository).findById(userId);
  }

  @Test
  @DisplayName("modifyUserNickname - 정상 변경")
  void modifyUserNickname_success() {
    // given
    Long userId = 100L;
    String newNick = "validNick";

    given(userRepository.existsByNickname(newNick)).willReturn(false);

    User user = User.builder().id(userId).nickname("oldNick").build();
    given(userRepository.findById(userId)).willReturn(Optional.of(user));

    // when
    String result = userService.modifyUserNickname(userId, newNick);

    // then
    assertThat(result).isEqualTo("닉네임이 성공적으로 변경되었습니다.");
    assertThat(user.getNickname()).isEqualTo(newNick);

    verify(userRepository).existsByNickname(newNick);
    verify(userRepository).findById(userId);
  }

  @Test
  @DisplayName("getUser - 정상 조회")
  void getUser_success() {
    // given
    Long userId = 10L;
    User user = User.builder().id(userId).username("user10").nickname("nick10").build();
    given(userRepository.findById(userId)).willReturn(Optional.of(user));

    // when
    UserResponseDto result = userService.getUser(userId);

    // then
    assertThat(result.getUsername()).isEqualTo("user10");
    assertThat(result.getNickname()).isEqualTo("nick10");

    verify(userRepository).findById(userId);
  }

  @Test
  @DisplayName("getUser - 유저가 없을 때 UsernameNotFoundException")
  void getUser_userNotFound() {
    // given
    Long userId = 999L;
    given(userRepository.findById(userId)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> userService.getUser(userId))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessage("해당 유저를 찾지 못했습니다.");

    verify(userRepository).findById(userId);
  }

  @Test
  @DisplayName("deleteUser - 정상 탈퇴")
  void deleteUser_success() {
    // given
    String username = "deleteMe";
    User user = User.builder().id(1L).username(username).build();
    given(userRepository.findByUsername(username)).willReturn(Optional.of(user));

    // when
    String result = userService.deleteUser(username);

    // then
    assertThat(result).isEqualTo("회원탈퇴가 성공적으로 처리되었습니다.");
    verify(userRepository).findByUsername(username);
    verify(userRepository).delete(user);
  }

  @Test
  @DisplayName("deleteUser - 유저가 없을 때 UsernameNotFoundException")
  void deleteUser_userNotFound() {
    // given
    String username = "nonExist";
    given(userRepository.findByUsername(username)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> userService.deleteUser(username))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessage("해당 유저를 찾지 못했습니다.");

    verify(userRepository).findByUsername(username);
    verify(userRepository, never()).delete(any(User.class));
  }

  @Test
  @DisplayName("loadUserByUsername - 유저가 존재할 때 정상 리턴")
  void loadUserByUsername_success() {
    // given
    String username = "someUser";
    User user = User.builder().id(1L).username(username).password("encodedPw").build();
    given(userRepository.findByUsername(username)).willReturn(Optional.of(user));

    // when
    UserDetails userDetails = userService.loadUserByUsername(username);

    // then
    assertThat(userDetails).isNotNull();
    assertThat(userDetails.getUsername()).isEqualTo(username);
    // 만약 User가 UserDetails를 직접 구현한다면,
    // userDetails == user 일 수도 있습니다.

    verify(userRepository).findByUsername(username);
  }

  @Test
  @DisplayName("loadUserByUsername - 유저가 없을 때 UsernameNotFoundException")
  void loadUserByUsername_notFound() {
    // given
    String username = "unknownUser";
    given(userRepository.findByUsername(username)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> userService.loadUserByUsername(username))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessage("해당 유저를 찾지 못했습니다.");

    verify(userRepository).findByUsername(username);
  }
}