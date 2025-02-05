package com.triptalk.triptalk.controller;

import com.triptalk.triptalk.dto.requestDto.ExpenseRequestDto;
import com.triptalk.triptalk.dto.responseDto.ExpenseResponseDto;
import com.triptalk.triptalk.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/expenses")
@RequiredArgsConstructor
public class ExpenseController {

  private final ExpenseService expenseService;

  @PostMapping
  public ExpenseResponseDto createExpense(@Valid @RequestBody ExpenseRequestDto requestDto) {
    return expenseService.createExpense(requestDto);
  }

  @GetMapping("/{expenseId}")
  public ExpenseResponseDto getExpense(@PathVariable Long expenseId) {
    return expenseService.getExpense(expenseId);
  }

  @GetMapping
  public List<ExpenseResponseDto> getAllExpenses() {
    return expenseService.getAllExpenses();
  }

  @PutMapping("/{expenseId}")
  public ExpenseResponseDto updateExpense(
          @PathVariable Long expenseId,
          @Valid @RequestBody ExpenseRequestDto requestDto
  ) {
    return expenseService.updateExpense(expenseId, requestDto);
  }

  @DeleteMapping("/{expenseId}")
  public void deleteExpense(@PathVariable Long expenseId) {
    expenseService.deleteExpense(expenseId);
  }
}