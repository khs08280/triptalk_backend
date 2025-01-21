package com.triptalk.triptalk.repository;

import com.triptalk.triptalk.domain.entity.ChatMessage;
import com.triptalk.triptalk.domain.entity.ChatMessageAttachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageAttachMentRepository extends JpaRepository<ChatMessageAttachment, Long> {
  List<ChatMessageAttachment> findByMessage(ChatMessage chatMessage);
}
