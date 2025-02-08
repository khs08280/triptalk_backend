package com.triptalk.triptalk.service;

import com.triptalk.triptalk.chat.dto.response.ChatMessageResponseDto;
import com.triptalk.triptalk.domain.entity.ChatMessage;
import com.triptalk.triptalk.domain.entity.ChatRoom;
import com.triptalk.triptalk.domain.entity.User;
import com.triptalk.triptalk.exception.ResourceNotFoundException;
import com.triptalk.triptalk.repository.ChatMessageRepository;
import com.triptalk.triptalk.repository.ChatRoomRepository;
import com.triptalk.triptalk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatMessageService {

  private final ChatMessageRepository chatMessageRepository;
  private final ChatRoomRepository chatRoomRepository;
  private final UserRepository userRepository;

  @Transactional(readOnly = true)
  public List<ChatMessageResponseDto> getLastMessages(Long roomId, int size) {
    if(!chatRoomRepository.existsById(roomId)){
      throw new ResourceNotFoundException("해당 채팅방이 존재하지 않습니다. Id: " + roomId);
    }

    Pageable pageable = PageRequest.of(0, size);
    List<ChatMessage> messages = chatMessageRepository.findByChatRoomIdOrderBySentAtDesc(roomId, pageable).getContent();

    return messages.stream()
            .map(ChatMessageResponseDto::fromEntity)
            .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public List<ChatMessage> getMoreMessages(Long roomId, int page, int size) {
    if (!chatRoomRepository.existsById(roomId)) {
      throw new ResourceNotFoundException("해당 채팅방이 존재하지 않습니다. Id: " + roomId);
    }

    Pageable pageable = PageRequest.of(page, size);
    return chatMessageRepository.findByChatRoomIdOrderBySentAtDesc(roomId, pageable).getContent();
  }

  public ChatMessageResponseDto saveMessage(Long senderId, Long roomId, String message) {
    User user = userRepository.findById(senderId).orElseThrow(() -> new ResourceNotFoundException("해당 유저를 찾지 못했습니다. Id: " + senderId));
    ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(() -> new ResourceNotFoundException("해당 채팅방을 찾지 못했습니다. Id: " + roomId));

    ChatMessage chatMessage = ChatMessage.builder()
            .chatRoom(chatRoom)
            .sender(user)
            .message(message)
            .sentAt(LocalDateTime.now())
            .build();

    return ChatMessageResponseDto.fromEntity(chatMessageRepository.save(chatMessage));

  }
}
