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
    @GetMapping("/club_members")
    public String getClubMemberPieChart(@RequestParam long clubId){
        return chartService.makeMembershipChart(clubId);
    }
}
