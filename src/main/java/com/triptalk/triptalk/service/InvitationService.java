package com.triptalk.triptalk.service;

import com.triptalk.triptalk.domain.entity.*;
import com.triptalk.triptalk.domain.enums.InvitationStatus;
import com.triptalk.triptalk.dto.responseDto.InvitationResponseDto;
import com.triptalk.triptalk.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class InvitationService {

  private final InvitationRepository invitationRepository;
  private final UserRepository userRepository;
  private final TripRepository tripRepository;
  private final TripUserRepository tripUserRepository;
  private final ChatRoomUserRepository chatRoomUserRepository;

  public List<InvitationResponseDto> getUserInvitations(Long userId) {
    List<Invitation> invitations = invitationRepository.findAllWithDetailsByInvitedId(userId);

    return invitations.stream()
            .map(InvitationResponseDto::fromEntity)
            .toList();
  }

  public void sendInvitation(Long tripId, Long inviterId, String invitedNickname) {
    // 초대할 여행과 초대할 사용자를 찾음
    Trip trip = tripRepository.findById(tripId)
            .orElseThrow(() -> new EntityNotFoundException("해당 여행을 찾을 수 없습니다."));

    User inviter = userRepository.findById(inviterId)
            .orElseThrow(() -> new EntityNotFoundException("해당 초대자는 존재하지 않습니다."));

    User invited = userRepository.findByNickname(invitedNickname)
            .orElseThrow(() -> new EntityNotFoundException("해당 유저를 찾지 못했습니다."));

    // 이미 초대된 상태인지 확인
    if (invitationRepository.existsByTripAndInvited(trip, invited)) {
      throw new IllegalStateException("이미 초대된 사용자입니다.");
    }

    // 초대 저장
    Invitation invitation = Invitation.builder()
            .trip(trip)
            .inviter(inviter)
            .invited(invited)
            .status(InvitationStatus.PENDING)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

    invitationRepository.save(invitation);
  }


  public void cancelInvitation(Long invitationId, Long userId) {
    // 초대 정보 가져오기
    Invitation invitation = invitationRepository.findById(invitationId)
            .orElseThrow(() -> new EntityNotFoundException("초대 정보를 찾을 수 없습니다."));

    // 초대자의 유효성 체크 (초대자는 초대 취소 권한이 있음)
    if (!invitation.getInviter().getId().equals(userId)) {
      throw new AccessDeniedException("초대만 취소할 수 있습니다.");
    }

    // 초대 상태 변경 (취소된 상태로)
    invitation.updateInvitation(InvitationStatus.CANCELED, LocalDateTime.now());

    invitationRepository.save(invitation);
  }


  public void acceptInvitation(Long invitationId, User user) {
    // 초대 정보 가져오기
    Invitation invitation = invitationRepository.findById(invitationId)
            .orElseThrow(() -> new EntityNotFoundException("초대 정보를 찾을 수 없습니다."));

    // 초대받은 사용자 확인
    if (!invitation.getInvited().getId().equals(user.getId())) {
      throw new AccessDeniedException("초대받은 사용자만 수락할 수 있습니다.");
    }

    invitation.updateInvitation(InvitationStatus.ACCEPTED, LocalDateTime.now(), LocalDateTime.now());
    invitationRepository.save(invitation);


    TripUser tripUser = TripUser.builder()
            .trip(invitation.getTrip())
            .user(user)
            .joinedAt(LocalDateTime.now())
            .build();

    ChatRoomUser chatRoomUser = ChatRoomUser.builder()
            .chatRoom(invitation.getTrip().getChatRoom())
            .user(user)
            .joinedAt(LocalDateTime.now()).build();

    tripUserRepository.save(tripUser);
    chatRoomUserRepository.save(chatRoomUser);
  }

  public void rejectInvitation(Long invitationId, Long userId) {
    // 초대 정보 가져오기
    Invitation invitation = invitationRepository.findById(invitationId)
            .orElseThrow(() -> new EntityNotFoundException("초대 정보를 찾을 수 없습니다."));

    // 초대받은 사용자 확인
    if (!invitation.getInvited().getId().equals(userId)) {
      throw new AccessDeniedException("초대받은 사용자만 거절할 수 있습니다.");
    }

    // 초대 상태 변경 (거절된 상태로)
    invitation.updateInvitation(InvitationStatus.REJECTED,LocalDateTime.now(),LocalDateTime.now());

    invitationRepository.save(invitation);
  }

}
