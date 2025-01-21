package com.triptalk.triptalk.repository;

import com.triptalk.triptalk.domain.entity.Expense;
import com.triptalk.triptalk.domain.entity.Schedule;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
  List<Schedule> findByTripId(Long tripId);
}
