package com.triptalk.triptalk.service;

import com.triptalk.triptalk.domain.entity.*;
import com.triptalk.triptalk.dto.requestDto.TripRequestDto;
import com.triptalk.triptalk.dto.responseDto.TripResponseDto;
import com.triptalk.triptalk.exception.ResourceNotFoundException;
import com.triptalk.triptalk.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class TripService {

  private final TripRepository tripRepository;
  private final UserRepository userRepository;
  private final TripUserRepository tripUserRepository;
  private final ChatRoomRepository chatRoomRepository;
  private final ChatRoomUserRepository chatRoomUserRepository;

  public TripResponseDto saveTrip(TripRequestDto requestDto, User creator) {

    Trip trip = createTrip(requestDto, creator);
    Trip savedTrip;

    try {
      savedTrip = tripRepository.save(trip);
    } catch (DataAccessException e) {
      log.error("여행 정보 저장 중 오류 발생: {}", e.getMessage(), e);
      throw new RuntimeException("여행 정보 저장 중 오류가 발생했습니다.");
    }

    TripUser tripUser = createTripUser(savedTrip, creator);
    tripUserRepository.save(tripUser);

    ChatRoom chatRoom = createChatRoom(savedTrip);
    ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);

    ChatRoomUser chatRoomUser = createChatRoomUser(savedChatRoom, creator);
    chatRoomUserRepository.save(chatRoomUser);

    return TripResponseDto.fromEntity(savedTrip);
  }

  public TripResponseDto modifyTrip(Long tripId, TripRequestDto requestDto, Long userId) {

    Trip trip = findTripOrThrow(tripId);

    validateCreator(userId, trip);

    trip.update(requestDto);

    return TripResponseDto.fromEntity(trip);

  }

  @Transactional(readOnly = true)
  public TripResponseDto getTripById(Long tripId){
    Trip trip = tripRepository.findById(tripId).orElseThrow(() -> new ResourceNotFoundException("해당 여행을 찾을 수 없습니다."));

    return TripResponseDto.fromEntity(trip);
  }

  public String removeTripById(Long tripId, Long userId){
    Trip trip = findTripOrThrow(tripId);

    validateCreator(userId,trip);

    tripRepository.delete(trip);  // 연관된 tripUser 엔터티 함께 삭제 (cascade = CascadeType.ALL, orphanRemoval = true)
    return "여행이 정상적으로 삭제되었습니다.";
  }


  public List<User> getTripMembers(Long tripId) {
    Trip trip = tripRepository.findById(tripId)
            .orElseThrow(() -> new ResourceNotFoundException("해당 여행을 찾을 수 없습니다. ID: " + tripId));

    return tripUserRepository.findUsersByTrip(trip);
  }

  //  public List<TripResponseDto> getTripList(User user){
//    List<TripUser> tripList = tripUserRepository.findByUser(user);
//    return tripList;
//  }
  private Trip createTrip(TripRequestDto requestDto, User creator) {
    return Trip.builder()
            .title(requestDto.getTitle())
            .startDate(requestDto.getStartDate())
            .endDate(requestDto.getEndDate())
            .location(requestDto.getLocation())
            .visibility(requestDto.getVisibility())
            .creator(creator)
            .build();
  }

  private TripUser createTripUser(Trip trip, User user) {
    return TripUser.builder()
            .trip(trip)
            .user(user)
            .joinedAt(LocalDateTime.now())
            .build();
  }

  private ChatRoom createChatRoom(Trip trip) {
    return ChatRoom.builder()
            .trip(trip)
            .build();
  }

  private ChatRoomUser createChatRoomUser(ChatRoom chatRoom, User user) {
    return ChatRoomUser.builder()
            .chatRoom(chatRoom)
            .user(user)
            .joinedAt(LocalDateTime.now())
            .build();
  }

  private Trip findTripOrThrow(Long tripId) {
    return tripRepository.findById(tripId)
            .orElseThrow(() -> new ResourceNotFoundException("해당 여행 정보를 찾을 수 없습니다."));
  }

  private static void validateCreator(Long userId, Trip trip) {
    if (!trip.getCreator().getId().equals(userId)) {
      throw new SecurityException("본인이 소유한 여행 계획만 수정/삭제가 가능합니다.");
    }
  }

}
