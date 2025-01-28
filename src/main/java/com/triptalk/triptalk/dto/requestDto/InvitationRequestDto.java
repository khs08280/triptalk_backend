package com.triptalk.triptalk.dto.requestDto;

import com.triptalk.triptalk.domain.entity.User;
import com.triptalk.triptalk.domain.entity.Trip;
import com.triptalk.triptalk.domain.enums.InvitationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvitationRequestDto {

  @NotNull(message = "초대받을 사용자 ID는 필수입니다.")
  private Long invitedId;  // 초대받을 사용자 ID

  public static InvitationRequestDto fromEntity( Long invitedId, InvitationStatus status) {
    return InvitationRequestDto.builder()
            .invitedId(invitedId)
            .build();
  }
}
