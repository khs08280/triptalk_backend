package com.triptalk.triptalk.service;

import com.triptalk.triptalk.domain.entity.Trip;
import com.triptalk.triptalk.domain.entity.TripUser;
import com.triptalk.triptalk.domain.entity.User;
import com.triptalk.triptalk.dto.requestDto.TripRequestDto;
import com.triptalk.triptalk.dto.responseDto.TripResponseDto;
import com.triptalk.triptalk.repository.TripRepository;
import com.triptalk.triptalk.repository.TripUserRepository;
import com.triptalk.triptalk.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TripService {

  private final TripRepository tripRepository;
  private final UserRepository userRepository;
  private final TripUserRepository tripUserRepository;

  @Transactional
  public TripResponseDto saveTrip(TripRequestDto requestDto, User creator) {

    Trip trip = Trip.builder()
            .title(requestDto.getTitle())
            .startDate(requestDto.getStartDate())
            .endDate(requestDto.getEndDate())
            .location(requestDto.getLocation())
            .visibility(requestDto.getVisibility())
            .creator(creator)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

    Trip savedTrip = tripRepository.save(trip);

    TripUser tripUser = TripUser.builder()
            .trip(savedTrip)
            .user(creator)
            .joinedAt(LocalDateTime.now())
            .build();

    tripUserRepository.save(tripUser);

    return TripResponseDto.fromEntity(savedTrip);
  }

  @Transactional
  public TripResponseDto modifyTrip(Long tripId, TripRequestDto requestDto, User user) {

    Trip trip = tripRepository.findById(tripId)
            .orElseThrow(() -> new EntityNotFoundException("해당 여행 정보를 찾을 수 없습니다."));

    if (!trip.getCreator().getId().equals(user.getId())) {
      throw new SecurityException("본인이 소유한 여행 계획만 수정할 수 있습니다.");
    }

    trip.update(requestDto);

    Trip updatedTrip = tripRepository.save(trip);

    return TripResponseDto.fromEntity(updatedTrip);
  }

  public TripResponseDto getTrip(Long tripId){
    Trip trip = tripRepository.findById(tripId).orElseThrow(() -> new EntityNotFoundException("해당 여행을 찾을 수 없습니다."));

    return TripResponseDto.fromEntity(trip);
  }

//  public List<TripResponseDto> getTripList(User user){
//    List<TripUser> tripList = tripUserRepository.findByUser(user);
//    return tripList;
//  }

}
