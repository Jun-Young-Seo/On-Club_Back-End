package com.springboot.club_house_api_server.chart.controller;

import com.springboot.club_house_api_server.chart.service.ChartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chart")
public class ChartController {
    private final ChartService chartService;
    //Test API
    @GetMapping("/club_members")
    public String getClubMemberPieChart(@RequestParam long clubId){
        return chartService.makeMembershipChart(clubId);
    }

    //해당 연월의 거래내역(입/출금만 구분) Type에 따른 pieChart 그리기
    @GetMapping("/transaction/year-month")
    public String getTransactionPieChartByYM(@RequestParam long clubId, @RequestParam int year, @RequestParam int month){
        return chartService.makeTransactionChartByYM(clubId,year,month);
    }
}
