package com.triptalk.triptalk.repository;

import com.triptalk.triptalk.domain.entity.ChatRoom;
import com.triptalk.triptalk.domain.entity.Trip;
import com.triptalk.triptalk.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
  Optional<ChatRoom> findByTrip(Trip trip);

  @Query("SELECT cr FROM ChatRoom cr JOIN FETCH cr.trip t WHERE cr.id IN " +
          "(SELECT cru.chatRoom.id FROM ChatRoomUser cru WHERE cru.user = :user)")
  List<ChatRoom> findChatRoomsAndTripByUser(@Param("user") User user);
}
