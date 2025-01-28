package com.triptalk.triptalk.dto.responseDto;

import com.triptalk.triptalk.domain.entity.Trip;
import com.triptalk.triptalk.domain.enums.Visibility;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripResponseDto {

  private Long id;
  private String title;
  private LocalDate startDate;
  private LocalDate endDate;
  private String location;
  private Visibility visibility;
  private String creatorNickname;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  // 정적 팩토리 메서드 (Trip 엔티티로부터 TripResponseDto 생성)
  public static TripResponseDto fromEntity(Trip trip) {
    return TripResponseDto.builder()
            .id(trip.getId())
            .title(trip.getTitle())
            .startDate(trip.getStartDate())
            .endDate(trip.getEndDate())
            .location(trip.getLocation())
            .visibility(trip.getVisibility())
            .creatorNickname(trip.getCreator().getNickname())
            .createdAt(trip.getCreatedAt())
            .updatedAt(trip.getUpdatedAt())
            .build();
  }
}