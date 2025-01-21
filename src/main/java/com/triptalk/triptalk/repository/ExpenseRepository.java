package com.triptalk.triptalk.repository;

import com.triptalk.triptalk.domain.entity.Expense;
import com.triptalk.triptalk.domain.entity.Schedule;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
  List<Expense> findBySchedule(Schedule schedule);

  // 예시: 특정 일정(Schedule)에 속한 경비 합계 계산
  @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.schedule = :schedule")
  Double sumAmountBySchedule(@Param("schedule") Schedule schedule);

  // 예시: 특정 여행 계획(Trip)에 속한 모든 경비 조회 (Schedule을 거쳐서)
  @Query("SELECT e FROM Expense e WHERE e.schedule.trip.id = :tripId")
  List<Expense> findByTripId(@Param("tripId") Long tripId);
}
