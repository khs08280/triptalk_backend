package com.triptalk.triptalk.repository;

import com.triptalk.triptalk.domain.entity.Invitation;
import com.triptalk.triptalk.domain.entity.Trip;
import com.triptalk.triptalk.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvitationRepository extends JpaRepository<Invitation, Long> {
  List<Invitation> findByInvited(User user);
  List<Invitation> findByTrip(Trip trip);
}
