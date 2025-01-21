package com.triptalk.triptalk.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "schedules")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Schedule {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "trip_id", nullable = false, insertable = false, updatable = false)
  private Trip trip;

  @Column(name = "trip_id", nullable = false)
  private Long tripId;

  @Column(name = "date")
  private LocalDate date;

  @Column(name = "place", nullable = false, length = 255)
  private String place;

  @Column(name = "start_time")
  private LocalTime startTime;

  @Column(name = "end_time")
  private LocalTime endTime;

  @Column(name = "memo", columnDefinition = "TEXT")
  private String memo;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;
}
