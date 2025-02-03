package com.triptalk.triptalk.domain.entity;

import com.triptalk.triptalk.dto.requestDto.ScheduleRequestDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "schedules")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Schedule {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "trip_id", nullable = false, insertable = false, updatable = false)
  private Trip trip;

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

  @CreatedDate
  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @LastModifiedDate
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  public Schedule updateDetails(ScheduleRequestDto requestDto) {
    return Schedule.builder()
            .id(this.id)
            .trip(this.trip)
            .date(requestDto.getDate())
            .place(requestDto.getPlace())
            .startTime(requestDto.getStartTime())
            .endTime(requestDto.getEndTime())
            .memo(requestDto.getMemo())
            .createdAt(this.createdAt)
            .updatedAt(this.updatedAt)
            .build();
  }
}
