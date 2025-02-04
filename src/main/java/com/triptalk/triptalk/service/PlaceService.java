package com.triptalk.triptalk.service;

import com.triptalk.triptalk.domain.entity.Place;
import com.triptalk.triptalk.domain.enums.PlaceType;
import com.triptalk.triptalk.dto.requestDto.ScheduleRequestDto;
import com.triptalk.triptalk.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlaceService {

  private final PlaceRepository placeRepository;

  @Transactional
  public Place getOrCreatePlace(ScheduleRequestDto scheduleRequestDto) {
    // 넘어온 placeId를 기준으로 먼저 Place 조회
    if (scheduleRequestDto.getPlaceId() != null) {
      Optional<Place> existingPlace = placeRepository.findById(Long.parseLong(scheduleRequestDto.getPlaceId()));
      if (existingPlace.isPresent()) {
        return existingPlace.get();
      }
    }

    // placeId가 없거나 조회되지 않을 경우, sourceType에 따라 처리
    if (scheduleRequestDto.getPlaceType() == PlaceType.NAVER) {
      return createPlaceFromNaver(scheduleRequestDto);
    } else if (scheduleRequestDto.getPlaceType() == PlaceType.GOOGLE) {
      return createPlaceFromGoogle(scheduleRequestDto);
    } else {
      throw new IllegalArgumentException("Invalid source type or place ID");
    }
  }

  private Place createPlaceFromNaver(ScheduleRequestDto scheduleRequestDto) {
    // 네이버로부터 받은 정보로 Place 객체 생성 및 저장
    Place newPlace = Place.builder()
            .placeType(scheduleRequestDto.getPlaceType())
            .name(scheduleRequestDto.getName())
            .category(scheduleRequestDto.getPlace().getCategory())
            .address(scheduleRequestDto.getPlace().getAddress())
            .roadAddress(scheduleRequestDto.getPlace().getRoadAddress())
            .mapx(scheduleRequestDto.getPlace().getMapx())
            .mapy(scheduleRequestDto.getPlace().getMapy())
            .build();
    return placeRepository.save(newPlace);
  }

  private Place createPlaceFromGoogle(ScheduleRequestDto scheduleRequestDto) {
    // 구글로부터 받은 정보로 Place 객체 생성 및 저장
    Place newPlace = Place.builder()
            .googlePlaceId(scheduleRequestDto.getPlace().getGooglePlaceId())
            .placeType(scheduleRequestDto.getPlaceType())
            .name(scheduleRequestDto.getName())
            .category(scheduleRequestDto.getPlace().getCategory())
            .address(scheduleRequestDto.getPlace().getAddress())
            .latitude(scheduleRequestDto.getPlace().getLatitude())
            .longitude(scheduleRequestDto.getPlace().getLongitude())
            .rating(scheduleRequestDto.getPlace().getRating())
            .website(scheduleRequestDto.getPlace().getWebsite())
            .phoneNumber(scheduleRequestDto.getPlace().getPhoneNumber())
            .photoUrls(scheduleRequestDto.getPlace().getPhotoUrls())
            .types(scheduleRequestDto.getPlace().getTypes())
            .openingHours(scheduleRequestDto.getPlace().getOpeningHours())
            .build();
    return placeRepository.save(newPlace);
  }
}