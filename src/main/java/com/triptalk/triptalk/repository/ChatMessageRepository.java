package com.triptalk.triptalk.repository;

import com.triptalk.triptalk.domain.entity.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
  List<ChatMessage> findTop50ByChatRoomIdOrderBySentAtDesc(Long roomId);

  boolean existsByChatRoomIdAndSentAtBefore(Long roomId, LocalDateTime sentAt);

  List<ChatMessage> findByChatRoomIdOrderByIdDesc(Long roomId, Pageable pageable);
  List<ChatMessage> findByChatRoomIdAndIdLessThanOrderByIdDesc(Long roomId, Long id, Pageable pageable);
  boolean existsByChatRoomIdAndIdLessThan(Long roomId, Long id);
}
