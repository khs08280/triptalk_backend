package com.triptalk.triptalk.dto.responseDto;

import com.triptalk.triptalk.domain.entity.Place;
import com.triptalk.triptalk.domain.enums.PlaceType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaceResponseDto {

  private Long id;
  private String naverPlaceId;
  private String googlePlaceId;
  private PlaceType placeType;
  private String name;
  private String address;
  private String roadAddress;
  private Double latitude;
  private Double longitude;
  private Integer mapx;
  private Integer mapy;
  private Double rating;
  private String website;
  private String phoneNumber;
  private String photoUrls; // JSON 문자열
  private String types; // JSON 문자열
  private String openingHours; // JSON 문자열

  public static PlaceResponseDto fromEntity(Place place) {
    return PlaceResponseDto.builder()
            .id(place.getId())
            .googlePlaceId(place.getGooglePlaceId())
            .placeType(place.getPlaceType())
            .name(place.getName())
            .address(place.getAddress())
            .roadAddress(place.getRoadAddress())
            .latitude(place.getLatitude())
            .longitude(place.getLongitude())
            .mapx(place.getMapx())
            .mapy(place.getMapy())
            .rating(place.getRating())
            .website(place.getWebsite())
            .phoneNumber(place.getPhoneNumber())
            .photoUrls(place.getPhotoUrls())
            .types(place.getTypes())
            .openingHours(place.getOpeningHours())
            .build();
  }
}