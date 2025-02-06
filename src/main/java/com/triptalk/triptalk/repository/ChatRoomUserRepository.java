package com.triptalk.triptalk.repository;

import com.triptalk.triptalk.domain.entity.ChatRoom;
import com.triptalk.triptalk.domain.entity.ChatRoomUser;
import com.triptalk.triptalk.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRoomUserRepository extends JpaRepository<ChatRoomUser,Long> {
  List<ChatRoomUser> findByUser(User user);
  List<ChatRoomUser> findByChatRoom(ChatRoom chatRoom);
  boolean existsByUserAndChatRoom(User user, ChatRoom chatRoom);
  void deleteByUserAndChatRoom(User user, ChatRoom chatRoom);
}
