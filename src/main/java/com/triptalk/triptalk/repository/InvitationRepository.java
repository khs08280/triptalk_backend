package com.triptalk.triptalk.repository;

import com.triptalk.triptalk.domain.entity.Invitation;
import com.triptalk.triptalk.domain.entity.Trip;
import com.triptalk.triptalk.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InvitationRepository extends JpaRepository<Invitation, Long> {
  List<Invitation> findByInvited(User user);
  List<Invitation> findByTrip(Trip trip);
  boolean existsByTripAndInvited(Trip trip, User invited);

  @Query("SELECT i FROM Invitation i JOIN FETCH i.trip t JOIN FETCH i.inviter JOIN FETCH i.invited WHERE i.invited.id = :userId")
  List<Invitation> findAllWithDetailsByInvitedId(@Param("userId") Long userId);
}
