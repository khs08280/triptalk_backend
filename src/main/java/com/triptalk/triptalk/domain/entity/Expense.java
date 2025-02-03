package com.triptalk.triptalk.domain.entity;

import com.triptalk.triptalk.dto.requestDto.ExpenseRequestDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "expenses")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Expense {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "schedule_id", nullable = false)
  private Schedule schedule;

  @Column(name = "category", nullable = false, length = 255)
  private String category;

  @Column(name = "amount", nullable = false, precision = 10, scale = 2)
  private BigDecimal amount;

  @Column(name = "memo", columnDefinition = "TEXT")
  private String memo;

  @Column(name = "created_at")
  @CreatedDate
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  @LastModifiedDate
  private LocalDateTime updatedAt;

  public Expense updateDetails(ExpenseRequestDto requestDto) {
    return Expense.builder()
            .id(this.id)
            .schedule(this.schedule)
            .category(requestDto.getCategory())
            .amount(requestDto.getAmount())
            .memo(requestDto.getMemo())
            .createdAt(this.createdAt)
            .updatedAt(this.updatedAt)
            .build();
  }
}
