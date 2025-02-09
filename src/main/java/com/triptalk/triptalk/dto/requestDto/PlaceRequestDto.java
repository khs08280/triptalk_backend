package com.triptalk.triptalk.dto.requestDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlaceRequestDto {
  // 네이버 검색 API를 이용한 경우
  private String category;
  private String address;
  private String roadAddress;
  private Integer mapx;
  private Integer mapy;

  // Google Places API를 통해 조회한 경우
  private String googlePlaceId;
  private Double latitude;
  private Double longitude;
  private Double rating;
  private String website;
  private String phoneNumber;
  private String photoUrls; // JSON 문자열
  private String types; // JSON 문자열
  private String openingHours; // JSON 문자열
}
