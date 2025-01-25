package com.triptalk.triptalk.dto.requestDto;

import com.triptalk.triptalk.domain.entity.*;
import com.triptalk.triptalk.domain.enums.Visibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor // 기본 생성자 추가
@AllArgsConstructor
@Builder
public class TripRequestDto {

  @NotBlank(message = "제목은 필수 값입니다.")
  private String title;

  @NotBlank(message = "시작 날짜는 필수 값입니다.")
  private LocalDate startDate;

  @NotNull(message = "종료 날짜는 필수 값입니다.")
  private LocalDate endDate;

  @NotNull(message = "여행 장소는 필수 값입니다.")
  private String location;

  @NotBlank(message = "공개/비공개는 필수 값입니다.")
  private Visibility visibility;

  public static TripRequestDto fromEntity(Trip trip){
    return TripRequestDto.builder()
            .title(trip.getTitle())
            .startDate(trip.getStartDate())
            .endDate(trip.getEndDate())
            .location(trip.getLocation())
            .visibility(trip.getVisibility())
            .build();
  }
}
