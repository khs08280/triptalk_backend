package com.triptalk.triptalk.service;

import com.triptalk.triptalk.domain.entity.ChatRoom;
import com.triptalk.triptalk.domain.entity.ChatRoomUser;
import com.triptalk.triptalk.domain.entity.User;
import com.triptalk.triptalk.exception.ResourceNotFoundException;
import com.triptalk.triptalk.repository.ChatRoomRepository;
import com.triptalk.triptalk.repository.ChatRoomUserRepository;
import com.triptalk.triptalk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatRoomUserService {

  private final UserRepository userRepository;
  private final ChatRoomRepository chatRoomRepository;
  private final ChatRoomUserRepository chatRoomUserRepository;

  public boolean isUserInRoom(Long userId, Long roomId){
    User user = findUser(userId);
    ChatRoom chatRoom = findChatRoom(roomId);

    return chatRoomUserRepository.existsByUserAndChatRoom(user, chatRoom);
  }

  public ChatRoomUser addUserToRoom(Long userId, Long roomId){
    User user = findUser(userId);
    ChatRoom chatRoom = findChatRoom(roomId);

    ChatRoomUser chatRoomUser = ChatRoomUser.builder()
            .user(user)
            .chatRoom(chatRoom)
            .build();

    return chatRoomUserRepository.save(chatRoomUser);
  }

  public String deleteChatRoomUser(Long userId, Long roomId) {
    User user = findUser(userId);
    ChatRoom chatRoom = findChatRoom(roomId);

    if (!chatRoomUserRepository.existsByUserAndChatRoom(user, chatRoom)) {
      throw new ResourceNotFoundException("이미 탈퇴했거나 존재하지 않는 유저입니다.");
    }

    chatRoomUserRepository.deleteByUserAndChatRoom(user, chatRoom);

    return "채팅방을 성공적으로 탈퇴했습니다.";
  }


  private ChatRoom findChatRoom(Long roomId) {
    return chatRoomRepository.findById(roomId).orElseThrow(() -> new ResourceNotFoundException("해당 채팅방을 찾지 못했습니다. Id: " + roomId));
  }

  private User findUser(Long userId) {
    return userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("해당 유저를 찾지 못했습니다. Id: " + userId));
  }
}
