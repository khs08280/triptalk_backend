package com.triptalk.triptalk.service;

import com.triptalk.triptalk.domain.entity.Expense;
import com.triptalk.triptalk.domain.entity.Schedule;
import com.triptalk.triptalk.dto.requestDto.ExpenseRequestDto;
import com.triptalk.triptalk.dto.responseDto.ExpenseResponseDto;
import com.triptalk.triptalk.exception.ResourceNotFoundException;
import com.triptalk.triptalk.repository.ExpenseRepository;
import com.triptalk.triptalk.repository.ScheduleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

  @InjectMocks
  private ExpenseService expenseService;

  @Mock
  private ExpenseRepository expenseRepository;

  @Mock
  private ScheduleRepository scheduleRepository;

  @Test
  @DisplayName("비용 생성 성공")
  void createExpense_Success() {
    // Given
    Long scheduleId = 1L;
    ExpenseRequestDto requestDto = ExpenseRequestDto.builder()
            .scheduleId(scheduleId)
            .category("Food")
            .amount(BigDecimal.valueOf(50000))
            .memo("Dinner")
            .build();

    Schedule mockSchedule = Schedule.builder().build(); // Assuming Schedule has a builder
    ReflectionTestUtils.setField(mockSchedule, "id", scheduleId); // Set id using reflection

    Expense expenseToSave = Expense.builder()
            .schedule(mockSchedule)
            .category(requestDto.getCategory())
            .amount(requestDto.getAmount())
            .memo(requestDto.getMemo())
            .build();
    Expense savedExpense = Expense.builder()
            .schedule(mockSchedule)
            .category(requestDto.getCategory())
            .amount(requestDto.getAmount())
            .memo(requestDto.getMemo())
            .build();

    ReflectionTestUtils.setField(savedExpense, "id", 1L);


    when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.of(mockSchedule));
    when(expenseRepository.save(any(Expense.class))).thenReturn(savedExpense);

    // When
    ExpenseResponseDto result = expenseService.createExpense(requestDto);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(1L); // Assuming savedExpense has id 1
    assertThat(result.getCategory()).isEqualTo(requestDto.getCategory());
    assertThat(result.getAmount()).isEqualTo(requestDto.getAmount());
    verify(scheduleRepository, times(1)).findById(scheduleId);
    verify(expenseRepository, times(1)).save(any(Expense.class));
  }
  @Test
  @DisplayName("비용 생성 실패 - Schedule 없음")
  void createExpense_ScheduleNotFound_ThrowsException() {
    // Given
    Long nonExistentScheduleId = 999L;
    ExpenseRequestDto requestDto = ExpenseRequestDto.builder()
            .scheduleId(nonExistentScheduleId)
            .category("Food")
            .amount(BigDecimal.valueOf(50000))
            .memo("Dinner")
            .build();

    when(scheduleRepository.findById(nonExistentScheduleId)).thenReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> expenseService.createExpense(requestDto))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("해당 일정을 찾을 수 없습니다.");
    verify(scheduleRepository, times(1)).findById(nonExistentScheduleId);
    verify(expenseRepository, never()).save(any(Expense.class)); // save()가 호출되지 않아야 함
  }

  @Test
  @DisplayName("비용 조회 성공")
  void getExpense_Success() {
    // Given
    Long expenseId = 1L;
    Schedule mockSchedule = Schedule.builder().build();
    ReflectionTestUtils.setField(mockSchedule, "id", 1L);
    Expense mockExpense = Expense.builder()
            .schedule(mockSchedule)
            .category("Food")
            .amount(BigDecimal.valueOf(50000))
            .memo("Lunch")
            .build();
    ReflectionTestUtils.setField(mockExpense, "id", expenseId);


    when(expenseRepository.findById(expenseId)).thenReturn(Optional.of(mockExpense));

    // When
    ExpenseResponseDto result = expenseService.getExpense(expenseId);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(expenseId);
    assertThat(result.getCategory()).isEqualTo("Food");
    assertThat(result.getAmount()).isEqualTo(BigDecimal.valueOf(50000));
    verify(expenseRepository, times(1)).findById(expenseId);
  }

  @Test
  @DisplayName("비용 조회 실패 - Expense 없음")
  void getExpense_NotFound_ThrowsException() {
    // Given
    Long nonExistentExpenseId = 999L;
    when(expenseRepository.findById(nonExistentExpenseId)).thenReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> expenseService.getExpense(nonExistentExpenseId))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("해당 비용 데이터를 찾을 수 없습니다.");

    verify(expenseRepository, times(1)).findById(nonExistentExpenseId);
  }


  @Test
  @DisplayName("모든 비용 조회 성공")
  void getAllExpenses_Success() {
    // Given
    Schedule schedule = Schedule.builder().build();
    ReflectionTestUtils.setField(schedule, "id", 1L);
    Expense expense1 = Expense.builder().schedule(schedule).category("Food").amount(BigDecimal.valueOf(10000)).build();
    Expense expense2 = Expense.builder().schedule(schedule).category("Transport").amount(BigDecimal.valueOf(5000)).build();
    ReflectionTestUtils.setField(expense1, "id", 1L);
    ReflectionTestUtils.setField(expense2, "id", 2L);

    List<Expense> expenses = Arrays.asList(expense1, expense2);
    when(expenseRepository.findAll()).thenReturn(expenses);


    // When
    List<ExpenseResponseDto> result = expenseService.getAllExpenses();

    // Then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(2);
    assertThat(result.get(0).getCategory()).isEqualTo("Food");
    assertThat(result.get(1).getCategory()).isEqualTo("Transport");
    verify(expenseRepository, times(1)).findAll();

  }

  @Test
  @DisplayName("비용 업데이트 성공")
  void updateExpense_Success() {
    // Given
    Long expenseId = 1L;
    Schedule schedule = Schedule.builder().build();
    ReflectionTestUtils.setField(schedule, "id", 1L);
    Expense existingExpense = Expense.builder()
            .schedule(schedule)
            .category("Food")
            .amount(BigDecimal.valueOf(10000))
            .memo("Before Update")
            .build();
    ReflectionTestUtils.setField(existingExpense, "id", expenseId);
    ExpenseRequestDto requestDto = ExpenseRequestDto.builder()
            .scheduleId(1L) // Same Schedule ID
            .category("Updated Category")
            .amount(BigDecimal.valueOf(20000))
            .memo("After Update")
            .build();

    when(expenseRepository.findById(expenseId)).thenReturn(Optional.of(existingExpense));

    // When
    ExpenseResponseDto result = expenseService.updateExpense(expenseId, requestDto);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(expenseId);
    assertThat(result.getCategory()).isEqualTo("Updated Category");
    assertThat(result.getAmount()).isEqualTo(BigDecimal.valueOf(20000));
    assertThat(result.getMemo()).isEqualTo("After Update");
    verify(expenseRepository, times(1)).findById(expenseId);

  }

  @Test
  @DisplayName("비용 업데이트 실패 - Expense 없음")
  void updateExpense_ExpenseNotFound_ThrowsException() {
    // Given
    Long nonExistentExpenseId = 999L;
    ExpenseRequestDto requestDto = new ExpenseRequestDto(); // Doesn't matter for this test

    when(expenseRepository.findById(nonExistentExpenseId)).thenReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> expenseService.updateExpense(nonExistentExpenseId, requestDto))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("해당 비용 데이터를 찾을 수 없습니다.");

    verify(expenseRepository, times(1)).findById(nonExistentExpenseId);
  }


  @Test
  @DisplayName("비용 삭제 성공")
  void deleteExpense_Success() {
    // Given
    Long expenseId = 1L;
    when(expenseRepository.existsById(expenseId)).thenReturn(true);
    doNothing().when(expenseRepository).deleteById(expenseId); // void 메서드 모킹

    // When
    expenseService.deleteExpense(expenseId);

    // Then
    verify(expenseRepository, times(1)).existsById(expenseId);
    verify(expenseRepository, times(1)).deleteById(expenseId);
  }

  @Test
  @DisplayName("비용 삭제 실패 - Expense 없음")
  void deleteExpense_NotFound_ThrowsException() {
    // Given
    Long nonExistentExpenseId = 999L;
    when(expenseRepository.existsById(nonExistentExpenseId)).thenReturn(false);

    // When & Then
    assertThatThrownBy(() -> expenseService.deleteExpense(nonExistentExpenseId))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("해당 비용 데이터를 찾을 수 없습니다.");

    verify(expenseRepository, times(1)).existsById(nonExistentExpenseId);
    verify(expenseRepository, never()).deleteById(nonExistentExpenseId); // deleteById가 호출되지 않아야 함
  }

}