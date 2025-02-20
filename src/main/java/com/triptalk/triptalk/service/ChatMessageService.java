package com.triptalk.triptalk.service;

import com.triptalk.triptalk.chat.dto.MessagesResponseDto;
import com.triptalk.triptalk.chat.dto.response.ChatMessageResponseDto;
import com.triptalk.triptalk.domain.entity.ChatMessage;
import com.triptalk.triptalk.domain.entity.ChatRoom;
import com.triptalk.triptalk.domain.entity.User;
import com.triptalk.triptalk.exception.ResourceNotFoundException;
import com.triptalk.triptalk.repository.ChatMessageRepository;
import com.triptalk.triptalk.repository.ChatRoomRepository;
import com.triptalk.triptalk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ChatMessageService {

  private final ChatMessageRepository chatMessageRepository;
  private final ChatRoomRepository chatRoomRepository;
  private final UserRepository userRepository;
  private static final int PAGE_SIZE = 30;

  @Transactional(readOnly = true)
  public MessagesResponseDto getLastMessages(Long roomId) {
    if(!chatRoomRepository.existsById(roomId)){
      throw new ResourceNotFoundException("해당 채팅방이 존재하지 않습니다. Id: " + roomId);
    }

    List<ChatMessage> messages = chatMessageRepository.findTop50ByChatRoomIdOrderBySentAtDesc(roomId);
    Collections.reverse(messages);

    Long lastId = messages.isEmpty() ? null : messages.getFirst().getId();

    boolean hasMore = lastId != null && chatMessageRepository.existsByChatRoomIdAndIdLessThan(roomId, lastId);
    Integer nextPageCursor = hasMore ? lastId.intValue() : null;

    List<ChatMessageResponseDto> list = messages.stream()
            .map(ChatMessageResponseDto::fromEntity)
            .toList();

    return new MessagesResponseDto(list, nextPageCursor);
  }
  @Transactional(readOnly = true)
  public MessagesResponseDto getMoreMessages(Long roomId, Long oldestMessageId, int size) {
    ChatMessage oldestMessage = chatMessageRepository.findById(oldestMessageId).orElseThrow(() -> new ResourceNotFoundException("해당 메시지를 찾을 수 없습니다."));

    log.info("roomid:{}, oldestMessageId:{} , size:{}",roomId, oldestMessageId, size);

    Pageable pageable = PageRequest.of(0, size);
    List<ChatMessage> messages = chatMessageRepository.findByChatRoomIdAndIdLessThanOrderByIdDesc(roomId, oldestMessageId, pageable);
    Collections.reverse(messages);

    Long lastId = messages.isEmpty() ? null : messages.getFirst().getId();

    boolean hasMore = lastId != null && chatMessageRepository.existsByChatRoomIdAndIdLessThan(roomId, lastId);
    Integer nextPageCursor = hasMore ? lastId.intValue() : null;


    List<ChatMessageResponseDto> messagesDto = messages.stream()
            .map(ChatMessageResponseDto::fromEntity)
            .toList();

    return new MessagesResponseDto(messagesDto, nextPageCursor);
  }

//  @Transactional(readOnly = true)
//  public MessagesResponseDto getMoreMessages(Long roomId, int page, int size) {
//    if (!chatRoomRepository.existsById(roomId)) {
//      throw new ResourceNotFoundException("해당 채팅방이 존재하지 않습니다. Id: " + roomId);
//    }
//    log.info("페이지:{}", page);
//    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "sentAt")); // ✅ 오름차순 정렬 추가
//    Page<ChatMessage> pageResult = chatMessageRepository.findByChatRoomId(roomId, pageable);
//
//    List<ChatMessageResponseDto> messages = pageResult.getContent()
//            .stream()
//            .map(ChatMessageResponseDto::fromEntity)
//            .collect(Collectors.toList());
//
//    log.info("getMoreMessages: {}", messages.size());
//    log.info("getMoreMessages: {}", pageable.getPageNumber());
//
//    Long nextPage = pageResult.hasNext() ? (long) page + 1 : null; // ✅ Integer 대신 Long으로 반환
//
//    return new MessagesResponseDto(messages, nextPage);
//  }

  public ChatMessageResponseDto saveMessage(Long senderId, Long roomId, String message) {
    User user = userRepository.findById(senderId).orElseThrow(() -> new ResourceNotFoundException("해당 유저를 찾지 못했습니다. Id: " + senderId));
    ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(() -> new ResourceNotFoundException("해당 채팅방을 찾지 못했습니다. Id: " + roomId));

    ChatMessage chatMessage = ChatMessage.builder()
            .chatRoom(chatRoom)
            .sender(user)
            .nickname(user.getNickname())
            .message(message)
            .build();

    return ChatMessageResponseDto.fromEntity(chatMessageRepository.save(chatMessage));

  }
}
