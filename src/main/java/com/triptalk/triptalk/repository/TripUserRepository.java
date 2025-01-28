package com.triptalk.triptalk.repository;

import com.triptalk.triptalk.domain.entity.Trip;
import com.triptalk.triptalk.domain.entity.TripUser;
import com.triptalk.triptalk.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TripUserRepository extends JpaRepository<TripUser, Long> {
  List<TripUser> findByUser(User user);
  List<TripUser> findByTrip(Trip trip);

  @Query("SELECT tu.user FROM TripUser tu WHERE tu.trip = :trip")
  List<User> findUsersByTrip(Trip trip);
}
