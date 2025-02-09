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
    if (scheduleRequestDto.getPlaceId() != null) {
      Optional<Place> existingPlace = placeRepository.findById(scheduleRequestDto.getPlaceId());
      if (existingPlace.isPresent()) {
        return existingPlace.get();
      }
    }

    if (scheduleRequestDto.getPlaceType() == PlaceType.NAVER) {
      return createPlaceFromNaver(scheduleRequestDto);
    } else if (scheduleRequestDto.getPlaceType() == PlaceType.GOOGLE) {
      return createPlaceFromGoogle(scheduleRequestDto);
    } else {
      throw new IllegalArgumentException("Invalid source type or place ID");
    }
  }

  private Place createPlaceFromNaver(ScheduleRequestDto scheduleRequestDto) {
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