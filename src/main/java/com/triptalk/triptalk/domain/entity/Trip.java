package com.triptalk.triptalk.domain.entity;

import com.triptalk.triptalk.domain.enums.Visibility;
import com.triptalk.triptalk.dto.requestDto.TripRequestDto;
import com.triptalk.triptalk.dto.responseDto.UserResponseDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.GenerationType.*;

@Entity
@Table(name = "trips")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Trip {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "creator_id", nullable = false)
  private User creator;

  @Column(name = "title", nullable = false, length = 255)
  private String title;

  @Column(name = "start_date", nullable = false)
  private LocalDate startDate;

  @Column(name = "end_date", nullable = false)
  private LocalDate endDate;

  @Column(name = "location", nullable = false, length = 255)
  private String location;

  @Enumerated(EnumType.STRING)
  @Column(name = "visibility", nullable = false, length=20)
  private Visibility visibility;

  @OneToOne(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
  private ChatRoom chatRoom;

  @Column(name = "created_at", nullable = false, updatable = false)
  @CreatedDate
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  @LastModifiedDate
  private LocalDateTime updatedAt;

  @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<TripUser> tripUsers = new ArrayList<>();

  @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Schedule> schedules = new ArrayList<>();

  @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Invitation> invitations = new ArrayList<>();

  public void update(TripRequestDto requestDto) {
    this.title = requestDto.getTitle();
    this.location = requestDto.getLocation();
    this.startDate = requestDto.getStartDate();
    this.endDate = requestDto.getEndDate();
    this.visibility = requestDto.getVisibility();
    this.updatedAt = LocalDateTime.now();
  }
}
