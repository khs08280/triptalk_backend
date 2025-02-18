package com.triptalk.triptalk.repository;

import com.triptalk.triptalk.domain.entity.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
  List<ChatMessage> findTop50ByChatRoomIdOrderBySentAtDesc(Long roomId);
  List<ChatMessage> findByRoomIdOrderBySentAtDesc(String roomId, Pageable pageable);

  List<ChatMessage> findByRoomIdOrderByCreatedDateDesc(Long roomId, Pageable pageable);

  @Query("SELECT m FROM ChatMessage m WHERE m.roomId = :roomId AND m.createdDate < (SELECT c.createdDate FROM ChatMessage c WHERE c.id = :oldestMessageId) ORDER BY m.createdDate DESC")
  List<ChatMessage> findOlderMessages(@Param("roomId") Long roomId, @Param("oldestMessageId") String oldestMessageId, Pageable pageable);


  // hasMore를 위한 쿼리
  @Query("SELECT COUNT(m) > 0 FROM ChatMessage m WHERE m.roomId = :roomId AND m.createdDate < (SELECT c.createdDate FROM ChatMessage c WHERE c.id = :oldestMessageId)")
  boolean hasOlderMessages(@Param("roomId") Long roomId, @Param("oldestMessageId") String oldestMessageId);
}
