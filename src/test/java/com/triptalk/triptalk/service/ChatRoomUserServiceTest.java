package com.triptalk.triptalk.service;

import com.triptalk.triptalk.domain.entity.ChatRoom;
import com.triptalk.triptalk.domain.entity.ChatRoomUser;
import com.triptalk.triptalk.domain.entity.User;
import com.triptalk.triptalk.exception.ResourceNotFoundException;
import com.triptalk.triptalk.repository.ChatRoomRepository;
import com.triptalk.triptalk.repository.ChatRoomUserRepository;
import com.triptalk.triptalk.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatRoomUserServiceTest {

  @InjectMocks
  private ChatRoomUserService chatRoomUserService;

  @Mock
  private UserRepository userRepository;
  @Mock
  private ChatRoomRepository chatRoomRepository;
  @Mock
  private ChatRoomUserRepository chatRoomUserRepository;

  @Test
  @DisplayName("사용자가 채팅방에 있는지 확인 - 존재하는 경우")
  void isUserInRoom_UserExistsInRoom_ReturnsTrue() {
    // Given
    Long userId = 1L;
    Long roomId = 2L;
    User mockUser = User.builder().build();
    ReflectionTestUtils.setField(mockUser, "id", userId);
    ChatRoom mockChatRoom = ChatRoom.builder().build();
    ReflectionTestUtils.setField(mockChatRoom, "id", roomId);

    when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
    when(chatRoomRepository.findById(roomId)).thenReturn(Optional.of(mockChatRoom));
    when(chatRoomUserRepository.existsByUserAndChatRoom(mockUser, mockChatRoom)).thenReturn(true);

    // When
    boolean result = chatRoomUserService.isUserInRoom(userId, roomId);

    // Then
    assertThat(result).isTrue();
    verify(userRepository).findById(userId);
    verify(chatRoomRepository).findById(roomId);
    verify(chatRoomUserRepository).existsByUserAndChatRoom(mockUser, mockChatRoom);
  }

  @Test
  @DisplayName("사용자가 채팅방에 있는지 확인 - 존재하지 않는 경우")
  void isUserInRoom_UserNotInRoom_ReturnsFalse() {
    // Given
    Long userId = 1L;
    Long roomId = 2L;
    User mockUser = User.builder().build();
    ReflectionTestUtils.setField(mockUser, "id", userId);
    ChatRoom mockChatRoom = ChatRoom.builder().build();
    ReflectionTestUtils.setField(mockChatRoom, "id", roomId);
    when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
    when(chatRoomRepository.findById(roomId)).thenReturn(Optional.of(mockChatRoom));
    when(chatRoomUserRepository.existsByUserAndChatRoom(mockUser, mockChatRoom)).thenReturn(false);

    // When
    boolean result = chatRoomUserService.isUserInRoom(userId, roomId);

    // Then
    assertThat(result).isFalse();
    verify(userRepository, times(1)).findById(userId);
    verify(chatRoomRepository, times(1)).findById(roomId);
    verify(chatRoomUserRepository, times(1)).existsByUserAndChatRoom(mockUser, mockChatRoom);
  }

  @Test
  @DisplayName("사용자가 채팅방에 있는지 확인 - 사용자 찾을 수 없음")
  void isUserInRoom_UserNotFound_ThrowsException() {
    // Given
    Long nonExistentUserId = 999L;
    Long roomId = 2L;
    when(userRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> chatRoomUserService.isUserInRoom(nonExistentUserId, roomId))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("해당 유저를 찾지 못했습니다.");
    verify(userRepository, times(1)).findById(nonExistentUserId);
    verify(chatRoomRepository, never()).findById(anyLong());
    verify(chatRoomUserRepository, never()).existsByUserAndChatRoom(any(User.class), any(ChatRoom.class));
  }

  @Test
  @DisplayName("사용자가 채팅방에 있는지 확인 - 채팅방 찾을 수 없음")
  void isUserInRoom_ChatRoomNotFound_ThrowsException() {
    // Given
    Long userId = 1L;
    Long nonExistentRoomId = 999L;
    User mockUser = User.builder().build();
    ReflectionTestUtils.setField(mockUser, "id", userId);

    when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
    when(chatRoomRepository.findById(nonExistentRoomId)).thenReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> chatRoomUserService.isUserInRoom(userId, nonExistentRoomId))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("해당 채팅방을 찾지 못했습니다.");
    verify(userRepository, times(1)).findById(userId);
    verify(chatRoomRepository, times(1)).findById(nonExistentRoomId);
    verify(chatRoomUserRepository, never()).existsByUserAndChatRoom(any(User.class), any(ChatRoom.class));
  }

  @Test
  @DisplayName("채팅방에 사용자 추가 성공")
  void addUserToRoom_Success() {
    // Given
    Long userId = 1L;
    Long roomId = 2L;
    User mockUser = User.builder().build();
    ReflectionTestUtils.setField(mockUser, "id", userId);
    ChatRoom mockChatRoom = ChatRoom.builder().build();
    ReflectionTestUtils.setField(mockChatRoom, "id", roomId);
    ChatRoomUser savedChatRoomUser = ChatRoomUser.builder().user(mockUser).chatRoom(mockChatRoom).build();
    ReflectionTestUtils.setField(savedChatRoomUser, "id", 1L);

    when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
    when(chatRoomRepository.findById(roomId)).thenReturn(Optional.of(mockChatRoom));
    when(chatRoomUserRepository.save(any(ChatRoomUser.class))).thenReturn(savedChatRoomUser);

    // When
    ChatRoomUser result = chatRoomUserService.addUserToRoom(userId, roomId);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(1L); // Assuming savedChatRoomUser has id 1
    assertThat(result.getUser()).isEqualTo(mockUser);
    assertThat(result.getChatRoom()).isEqualTo(mockChatRoom);
    verify(userRepository, times(1)).findById(userId);
    verify(chatRoomRepository, times(1)).findById(roomId);
    verify(chatRoomUserRepository, times(1)).save(any(ChatRoomUser.class));
  }

  @Test
  @DisplayName("채팅방에 사용자 추가 - 사용자 찾을 수 없음")
  void addUserToRoom_UserNotFound_ThrowsException() {
    // Given
    Long nonExistentUserId = 999L;
    Long roomId = 2L;
    when(userRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> chatRoomUserService.addUserToRoom(nonExistentUserId, roomId))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("해당 유저를 찾지 못했습니다.");
    verify(userRepository, times(1)).findById(nonExistentUserId);
    verify(chatRoomRepository, never()).findById(anyLong());
    verify(chatRoomUserRepository, never()).save(any(ChatRoomUser.class));
  }
  @Test
  @DisplayName("채팅방에 사용자 추가 - 채팅방 찾을 수 없음")
  void addUserToRoom_ChatRoomNotFound_ThrowsException() {
    // Given
    Long userId = 1L;
    Long nonExistentRoomId = 999L;
    User mockUser = User.builder().build();
    ReflectionTestUtils.setField(mockUser, "id", userId);

    when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
    when(chatRoomRepository.findById(nonExistentRoomId)).thenReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> chatRoomUserService.addUserToRoom(userId, nonExistentRoomId))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("해당 채팅방을 찾지 못했습니다.");
    verify(userRepository, times(1)).findById(userId);
    verify(chatRoomRepository, times(1)).findById(nonExistentRoomId);
    verify(chatRoomUserRepository, never()).save(any(ChatRoomUser.class));
  }

  @Test
  @DisplayName("채팅방에서 사용자 삭제 성공")
  void deleteChatRoomUser_Success() {
    // Given
    Long userId = 1L;
    Long roomId = 2L;
    User mockUser = User.builder().build();
    ReflectionTestUtils.setField(mockUser, "id", userId);
    ChatRoom mockChatRoom = ChatRoom.builder().build();
    ReflectionTestUtils.setField(mockChatRoom, "id", roomId);

    when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
    when(chatRoomRepository.findById(roomId)).thenReturn(Optional.of(mockChatRoom));
    when(chatRoomUserRepository.existsByUserAndChatRoom(mockUser, mockChatRoom)).thenReturn(true);
    doNothing().when(chatRoomUserRepository).deleteByUserAndChatRoom(mockUser, mockChatRoom);

    // When
    String result = chatRoomUserService.deleteChatRoomUser(userId, roomId);

    // Then
    assertThat(result).isEqualTo("채팅방을 성공적으로 탈퇴했습니다.");
    verify(userRepository, times(1)).findById(userId);
    verify(chatRoomRepository, times(1)).findById(roomId);
    verify(chatRoomUserRepository, times(1)).existsByUserAndChatRoom(mockUser, mockChatRoom);
    verify(chatRoomUserRepository, times(1)).deleteByUserAndChatRoom(mockUser, mockChatRoom);
  }

  @Test
  @DisplayName("채팅방에서 사용자 삭제 - 사용자 찾을 수 없음")
  void deleteChatRoomUser_UserNotFound_ThrowsException() {
    // Given
    Long nonExistentUserId = 999L;
    Long roomId = 2L;

    when(userRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());

    //When & Then
    assertThatThrownBy(()-> chatRoomUserService.deleteChatRoomUser(nonExistentUserId, roomId))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("해당 유저를 찾지 못했습니다.");
    verify(userRepository, times(1)).findById(nonExistentUserId);
    verify(chatRoomRepository, never()).findById(anyLong());
    verify(chatRoomUserRepository, never()).existsByUserAndChatRoom(any(), any());
    verify(chatRoomUserRepository, never()).deleteByUserAndChatRoom(any(), any());

  }
  @Test
  @DisplayName("채팅방에서 사용자 삭제 - 채팅방 찾을 수 없음")
  void deleteChatRoomUser_ChatRoomNotFound_ThrowsException() {
    // Given
    Long userId = 1L;
    Long nonExistentRoomId = 999L;

    User mockUser = User.builder().build();
    ReflectionTestUtils.setField(mockUser, "id", userId);

    when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
    when(chatRoomRepository.findById(nonExistentRoomId)).thenReturn(Optional.empty());

    //When & Then
    assertThatThrownBy(() -> chatRoomUserService.deleteChatRoomUser(userId, nonExistentRoomId))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("해당 채팅방을 찾지 못했습니다.");
    verify(userRepository, times(1)).findById(userId);
    verify(chatRoomRepository, times(1)).findById(nonExistentRoomId);
    verify(chatRoomUserRepository, never()).existsByUserAndChatRoom(any(), any());
    verify(chatRoomUserRepository, never()).deleteByUserAndChatRoom(any(), any());
  }

  @Test
  @DisplayName("채팅방에서 사용자 삭제 - 이미 탈퇴했거나 존재하지 않는 사용자")
  void deleteChatRoomUser_UserNotInRoom_ThrowsException() {
    // Given
    Long userId = 1L;
    Long roomId = 2L;
    User mockUser = User.builder().build();
    ReflectionTestUtils.setField(mockUser, "id", userId);
    ChatRoom mockChatRoom = ChatRoom.builder().build();
    ReflectionTestUtils.setField(mockChatRoom, "id", roomId);

    when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
    when(chatRoomRepository.findById(roomId)).thenReturn(Optional.of(mockChatRoom));
    when(chatRoomUserRepository.existsByUserAndChatRoom(mockUser, mockChatRoom)).thenReturn(false); // Not in the room

    // When & Then
    assertThatThrownBy(() -> chatRoomUserService.deleteChatRoomUser(userId, roomId))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("이미 탈퇴했거나 존재하지 않는 유저입니다.");
    verify(userRepository, times(1)).findById(userId);
    verify(chatRoomRepository, times(1)).findById(roomId);
    verify(chatRoomUserRepository, times(1)).existsByUserAndChatRoom(mockUser, mockChatRoom);
    verify(chatRoomUserRepository, never()).deleteByUserAndChatRoom(any(User.class), any(ChatRoom.class)); // delete not called
  }

}