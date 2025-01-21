package com.triptalk.triptalk.repository;

import com.triptalk.triptalk.domain.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
  List<ChatMessage> findByChatRoomIdOrderBySentAtDesc(Long chatRoomId);
}
