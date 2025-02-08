package com.triptalk.triptalk.domain.entity;

import com.triptalk.triptalk.dto.requestDto.ScheduleRequestDto;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "schedules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Schedule {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "trip_id", nullable = false, insertable = false, updatable = false)
  private Trip trip;

  @Column(name = "date")
  private LocalDate date;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "place_id")
  private Place place;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "start_time")
  private LocalTime startTime;

  @Column(name = "end_time")
  private LocalTime endTime;

  @Column(name = "memo", columnDefinition = "TEXT")
  private String memo;

  @CreatedDate
  @Column(name = "created_at", updatable = false, nullable = false)
  private LocalDateTime createdAt;

  @LastModifiedDate
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  public Schedule updateDetails(ScheduleRequestDto requestDto) {
    return Schedule.builder()
            .id(this.id)
            .trip(this.trip)
            .date(requestDto.getDate())
            .name(requestDto.getName())
            .startTime(requestDto.getStartTime())
            .endTime(requestDto.getEndTime())
            .memo(requestDto.getMemo())
            .createdAt(this.createdAt)
            .updatedAt(this.updatedAt)
            .build();
  }
}
