package com.triptalk.triptalk.repository;

import com.triptalk.triptalk.domain.entity.Trip;
import com.triptalk.triptalk.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TripRepository extends JpaRepository<Trip, Long> {
  List<Trip> findByCreator(User creator);
}
