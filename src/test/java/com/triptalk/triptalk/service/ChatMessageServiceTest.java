package com.triptalk.triptalk.service;

import com.triptalk.triptalk.chat.dto.response.ChatMessageResponseDto;
import com.triptalk.triptalk.domain.entity.ChatMessage;
import com.triptalk.triptalk.domain.entity.ChatRoom;
import com.triptalk.triptalk.domain.entity.User;
import com.triptalk.triptalk.exception.ResourceNotFoundException;
import com.triptalk.triptalk.repository.ChatMessageRepository;
import com.triptalk.triptalk.repository.ChatRoomRepository;
import com.triptalk.triptalk.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatMessageServiceTest {

  @InjectMocks
  private ChatMessageService chatMessageService;

  @Mock
  private ChatMessageRepository chatMessageRepository;
  @Mock
  private ChatRoomRepository chatRoomRepository;
  @Mock
  private UserRepository userRepository;


  @Test
  @DisplayName("최근 메시지 조회 성공")
  void getLastMessages_Success() {
    // Given
    Long roomId = 1L;
    int size = 10;

    User sender = User.builder().build();
    ReflectionTestUtils.setField(sender, "id", 1L);
    ChatRoom chatRoom = ChatRoom.builder().build();
    ReflectionTestUtils.setField(chatRoom, "id", 1L);

    ChatMessage message1 = ChatMessage.builder()
            .chatRoom(chatRoom)
            .sender(sender)
            .message("Message 1")
            .sentAt(LocalDateTime.now())
            .build();

    ChatMessage message2 = ChatMessage.builder()
            .chatRoom(chatRoom)
            .sender(sender)
            .message("Message 2")
            .sentAt(LocalDateTime.now().minusMinutes(1))
            .build();

    List<ChatMessage> messages = Arrays.asList(message1, message2);

    when(chatRoomRepository.existsById(roomId)).thenReturn(true);
    when(chatMessageRepository.findTop50ByChatRoomIdOrderBySentAtDesc(eq(roomId))).thenReturn(messages);

    // When
    List<ChatMessageResponseDto> result = chatMessageService.getLastMessages(roomId);

    // Then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(2);
    assertThat(result.get(0).getMessage()).isEqualTo("Message 1");
    assertThat(result.get(1).getMessage()).isEqualTo("Message 2");
    verify(chatRoomRepository, times(1)).existsById(roomId);
    verify(chatMessageRepository, times(1)).findTop50ByChatRoomIdOrderBySentAtDesc(eq(roomId));
  }

  @Test
  @DisplayName("최근 메시지 조회 - 채팅방 없음")
  void getLastMessages_ChatRoomNotFound_ThrowsException() {
    // Given
    Long nonExistentRoomId = 999L;
    int size = 10;

    when(chatRoomRepository.existsById(nonExistentRoomId)).thenReturn(false);

    // When & Then
    assertThatThrownBy(() -> chatMessageService.getLastMessages(nonExistentRoomId))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("해당 채팅방이 존재하지 않습니다.");

    verify(chatRoomRepository, times(1)).existsById(nonExistentRoomId);
    verify(chatMessageRepository, never()).findTop50ByChatRoomIdOrderBySentAtDesc(anyLong());
  }

  @Test
  @DisplayName("이전 메시지 조회 성공")
  void getMoreMessages_Success() {
    // Given
    Long roomId = 1L;
    int page = 1;
    int size = 10;

    User sender = User.builder().build();
    ReflectionTestUtils.setField(sender, "id", 1L);
    ChatRoom chatRoom = ChatRoom.builder().build();
    ReflectionTestUtils.setField(chatRoom, "id", 1L);
    ChatMessage message1 = ChatMessage.builder().chatRoom(chatRoom).sender(sender).message("Message 1").sentAt(LocalDateTime.now()).build();
    ChatMessage message2 = ChatMessage.builder().chatRoom(chatRoom).sender(sender).message("Message 2").sentAt(LocalDateTime.now().minusMinutes(1)).build();
    List<ChatMessage> messages = Arrays.asList(message1, message2);
    Page<ChatMessage> messagePage = new PageImpl<>(messages);


    when(chatRoomRepository.existsById(roomId)).thenReturn(true);
    when(chatMessageRepository.findTop50ByChatRoomIdOrderBySentAtDesc(eq(roomId))).thenReturn(messages);

    // When
    List<ChatMessageResponseDto> result = chatMessageService.getMoreMessages(roomId, page, size);

    // Then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(2);
    assertThat(result.get(0).getMessage()).isEqualTo("Message 1");
    assertThat(result.get(1).getMessage()).isEqualTo("Message 2");
    verify(chatRoomRepository, times(1)).existsById(roomId);
    verify(chatMessageRepository, times(1)).findTop50ByChatRoomIdOrderBySentAtDesc(eq(roomId));
  }

  @Test
  @DisplayName("이전 메시지 조회 - 채팅방 없음")
  void getMoreMessages_ChatRoomNotFound_ThrowsException() {
    // Given
    Long nonExistentRoomId = 999L;
    int page = 1;
    int size = 10;

    when(chatRoomRepository.existsById(nonExistentRoomId)).thenReturn(false);

    // When & Then
    assertThatThrownBy(() -> chatMessageService.getMoreMessages(nonExistentRoomId, page, size))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("해당 채팅방이 존재하지 않습니다.");

    verify(chatRoomRepository, times(1)).existsById(nonExistentRoomId);
    verify(chatMessageRepository, never()).findTop50ByChatRoomIdOrderBySentAtDesc(anyLong());
  }
  @Test
  @DisplayName("메시지 저장 성공")
  void saveMessage_Success() {
    // Given
    Long senderId = 1L;
    Long roomId = 2L;
    String messageContent = "Hello, world!";

    User mockUser = User.builder().build();
    ReflectionTestUtils.setField(mockUser, "id", senderId);
    ChatRoom mockChatRoom = ChatRoom.builder().build();
    ReflectionTestUtils.setField(mockChatRoom,"id", roomId);
    ChatMessage savedMessage = ChatMessage.builder()
            .chatRoom(mockChatRoom)
            .sender(mockUser)
            .message(messageContent)
            .build();
    ReflectionTestUtils.setField(savedMessage, "id", 1L);
    when(userRepository.findById(senderId)).thenReturn(Optional.of(mockUser));
    when(chatRoomRepository.findById(roomId)).thenReturn(Optional.of(mockChatRoom));
    when(chatMessageRepository.save(any(ChatMessage.class))).thenReturn(savedMessage);

    // When
    ChatMessageResponseDto result = chatMessageService.saveMessage(senderId, roomId, messageContent);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getMessage()).isEqualTo(messageContent);
    assertThat(result.getSenderId()).isEqualTo(senderId);
    assertThat(result.getRoomId()).isEqualTo(roomId);
    verify(userRepository, times(1)).findById(senderId);
    verify(chatRoomRepository, times(1)).findById(roomId);
    verify(chatMessageRepository, times(1)).save(any(ChatMessage.class));
  }

  @Test
  @DisplayName("메시지 저장 - 사용자 없음")
  void saveMessage_UserNotFound_ThrowsException() {
    // Given
    Long nonExistentUserId = 999L;
    Long roomId = 2L;
    String messageContent = "Hello, world!";

    when(userRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> chatMessageService.saveMessage(nonExistentUserId, roomId, messageContent))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("해당 유저를 찾지 못했습니다.");

    verify(userRepository, times(1)).findById(nonExistentUserId);
    verify(chatRoomRepository, never()).findById(anyLong());
    verify(chatMessageRepository, never()).save(any(ChatMessage.class));
  }

  @Test
  @DisplayName("메시지 저장 - 채팅방 없음")
  void saveMessage_ChatRoomNotFound_ThrowsException() {
    // Given
    Long senderId = 1L;
    Long nonExistentRoomId = 999L;
    String messageContent = "Hello, world!";

    User mockUser = User.builder().build();
    ReflectionTestUtils.setField(mockUser, "id", senderId);

    when(userRepository.findById(senderId)).thenReturn(Optional.of(mockUser));
    when(chatRoomRepository.findById(nonExistentRoomId)).thenReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> chatMessageService.saveMessage(senderId, nonExistentRoomId, messageContent))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("해당 채팅방을 찾지 못했습니다.");

    verify(userRepository, times(1)).findById(senderId);
    verify(chatRoomRepository, times(1)).findById(nonExistentRoomId);
    verify(chatMessageRepository, never()).save(any(ChatMessage.class));
  }

}