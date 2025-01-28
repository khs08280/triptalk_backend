package com.triptalk.triptalk.dto.responseDto;

import com.triptalk.triptalk.domain.entity.Invitation;
import com.triptalk.triptalk.domain.entity.User;
import com.triptalk.triptalk.domain.entity.Trip;
import com.triptalk.triptalk.domain.enums.InvitationStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvitationResponseDto {

  private Long id;
  private Long tripId;
  private Long inviterId;
  private String inviterNickname;
  private Long invitedId;
  private String invitedNickname;
  private InvitationStatus status;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  // 정적 팩토리 메서드 (Invitation 엔티티로부터 InvitationResponseDto 생성)
  public static InvitationResponseDto fromEntity(Invitation invitation) {
    return InvitationResponseDto.builder()
            .id(invitation.getId())
            .tripId(invitation.getTrip().getId())
            .inviterId(invitation.getInviter().getId())
            .inviterNickname(invitation.getInviter().getNickname())
            .invitedId(invitation.getInvited().getId())
            .invitedNickname(invitation.getInvited().getNickname())
            .status(invitation.getStatus())
            .createdAt(invitation.getCreatedAt())
            .updatedAt(invitation.getUpdatedAt())
            .build();
  }
}
