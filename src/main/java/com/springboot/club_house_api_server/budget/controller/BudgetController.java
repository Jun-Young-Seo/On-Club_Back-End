package com.springboot.club_house_api_server.budget.controller;

import com.springboot.club_house_api_server.budget.dto.AddNewTransactionDto;
import com.springboot.club_house_api_server.budget.dto.BudgetResponseDto;
import com.springboot.club_house_api_server.budget.dto.ExpenseSummaryDto;
import com.springboot.club_house_api_server.budget.dto.IncomeSummaryDto;
import com.springboot.club_house_api_server.budget.repository.TransactionRepository;
import com.springboot.club_house_api_server.budget.service.TransactionService;
import com.springboot.club_house_api_server.excel.service.BudgetService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/budget")
public class BudgetController {
    private final TransactionRepository transactionRepository;
    private final TransactionService transactionService;

    @GetMapping("/get-all")
    public ResponseEntity<?> getAllTransactions(@RequestParam int clubId) {
        return transactionService.getAllTransactions(clubId);
    }

    @GetMapping("/get-all/account_id")
    public ResponseEntity<?> getAllTransactionsByAccountId(@RequestParam int accountId) {
        return transactionService.getAllTransactionByAccountId(accountId);
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateTransaction(@RequestBody BudgetResponseDto dto){
        return transactionService.updateTransaction(dto);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addNewTransaction(@RequestBody AddNewTransactionDto dto){
        return transactionService.addNewTransaction(dto);
    }

    //for dashboard
    @GetMapping("/get/latest-three")
    public ResponseEntity<?> getLastThreeTransactions(@RequestParam long clubId){
        return transactionService.getLastThreeTransactions(clubId);
    }

    @GetMapping("/get/budget-info")
    public ResponseEntity<?> getBudgetInfo(@RequestParam long clubId){
        return transactionService.getBudgetInfo(clubId);
    }

    @GetMapping("/chart-income")
    public ResponseEntity<?> findIncomeSummary(@RequestParam long clubId){
        return transactionService.findIncomeSummary(clubId);
    }

    @GetMapping("/chart-expense")
    public ResponseEntity<?> findExpenseSummary(@RequestParam long clubId){
        return transactionService.findExpenseSummary(clubId);
    }

    @GetMapping("/chart-monthly")
    public ResponseEntity<?> findMonthlyDataSummary(@RequestParam long clubId){
        System.out.println("Chart-MOnthly");
        return transactionService.findMonthlyDataSummary(clubId);
    }
}
