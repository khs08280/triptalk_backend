package com.triptalk.triptalk.repository;

import com.triptalk.triptalk.domain.entity.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
  List<ChatMessage> findTop50ByChatRoomIdOrderBySentAtDesc(Long roomId);
  Page<ChatMessage> findByChatRoomIdOrderBySentAtDesc(Long roomId, Pageable pageable);
}
