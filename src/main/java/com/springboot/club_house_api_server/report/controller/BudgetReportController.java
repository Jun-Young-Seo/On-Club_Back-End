package com.springboot.club_house_api_server.report.controller;

import com.springboot.club_house_api_server.report.service.BudgetReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/report")
public class BudgetReportController {
    private final BudgetReportService budgetReportService;

    @GetMapping("/budget/analyze")
    public ResponseEntity<?> aiBudgetAnalyze(@RequestParam Long clubId, @RequestParam int month){
        int year = LocalDate.now().getYear();
        if (month == 1) {
            year -= 1;
            month = 12;
        } else {
            month -= 1;
        }

        LocalDate targetMonth = LocalDate.of(year, month, 1);
        return budgetReportService.getAIBudgetReport(2L,targetMonth);
    }

    @GetMapping("/budget/data")
    public ResponseEntity<?> getChartData(@RequestParam Long clubId, @RequestParam int month){
        int year = LocalDate.now().getYear();
        if (month == 1) {
            year -= 1;
            month = 12;
        } else {
            month -= 1;
        }

        LocalDate targetMonth = LocalDate.of(year, month, 1);
        return budgetReportService.getBudgetReportChartData(clubId, targetMonth);
    }
}
