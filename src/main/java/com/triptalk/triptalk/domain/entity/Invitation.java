package com.triptalk.triptalk.domain.entity;

import com.triptalk.triptalk.domain.enums.InvitationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "invitations")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Invitation {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "trip_id", nullable = false)
  private Trip trip;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "inviter_id", nullable = false)
  private User inviter;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "invited_id", nullable = false)
  private User invited;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 20)
  private InvitationStatus status;

  @Column(name = "created_at", nullable = false, updatable = false)
  @CreatedDate
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  @LastModifiedDate
  private LocalDateTime updatedAt;


  @Column(name = "responded_at")
  private LocalDateTime respondedAt;

  public void updateInvitation(InvitationStatus status, LocalDateTime updatedAt){
    this.status = status;
    this.updatedAt = updatedAt;
  }

  public void updateInvitation(InvitationStatus status, LocalDateTime updatedAt, LocalDateTime respondedAt){
    this.status = status;
    this.updatedAt = updatedAt;
    this.respondedAt = respondedAt;
  }
}