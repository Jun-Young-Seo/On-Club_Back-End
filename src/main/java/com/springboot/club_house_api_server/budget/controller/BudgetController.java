package com.springboot.club_house_api_server.budget.controller;

import com.springboot.club_house_api_server.budget.repository.TransactionRepository;
import com.springboot.club_house_api_server.budget.service.TransactionService;
import com.springboot.club_house_api_server.excel.service.BudgetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/budget")
public class BudgetController {
    private final TransactionRepository transactionRepository;
    private final TransactionService transactionService;
    @GetMapping("/get-all")
    public ResponseEntity<?> getAllBudget(@RequestParam int clubId, @RequestParam int accountId) {
        return transactionService.getAllTransactions(clubId, accountId);
    }
}
