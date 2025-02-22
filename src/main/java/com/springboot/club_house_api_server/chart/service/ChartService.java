package com.springboot.club_house_api_server.chart.service;

import com.springboot.club_house_api_server.budget.entity.TransactionEntity;
import com.springboot.club_house_api_server.budget.repository.TransactionRepository;
import com.springboot.club_house_api_server.club.entity.ClubEntity;
import com.springboot.club_house_api_server.club.repository.ClubRepository;
import com.springboot.club_house_api_server.membership.entity.MembershipEntity;
import com.springboot.club_house_api_server.membership.repository.MembershipRepository;
import com.springboot.club_house_api_server.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.knowm.xchart.PieChart;
import org.knowm.xchart.PieChartBuilder;
import org.knowm.xchart.VectorGraphicsEncoder;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.*;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChartService {
    private final ClubRepository clubRepository;
    private final TransactionRepository transactionRepository;

    //Test API
    //return .svg file String
    public String makeMembershipChart(long clubId){
        Optional<ClubEntity> clubOpt = clubRepository.findById(clubId);
        if(clubOpt.isEmpty()){
            return "클럽 빔";
        }
        PieChart pieChart = new PieChartBuilder()
                .width(500)
                .height(500)
                .title("test pie Chart")
                .build();
        List<UserEntity> clubUsers = clubRepository.findAllClubMembers(clubId);
//        int i=0;
        for(UserEntity user : clubUsers){
            String n = user.getUserName();
            pieChart.addSeries(n,1);

        }
        Random random = new Random();
        Color[] randomColors = new Color[clubUsers.size()];
        for(int j=0; j< randomColors.length; j++){
            int randInt1 = random.nextInt(256);
            int randInt2 = random.nextInt(256);
            int randInt3 = random.nextInt(256);
            randomColors[j] = new Color(randInt1,randInt2,randInt3);
        }
        pieChart.getStyler().setSeriesColors(randomColors);

        String filePath = "./membership_chart.svg";
        try{
            VectorGraphicsEncoder.saveVectorGraphic(pieChart, filePath, VectorGraphicsEncoder.VectorGraphicsFormat.SVG);
            File svgFile = new File(filePath);
            return new String(Files.readAllBytes(svgFile.toPath()));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String makeTransactionChartByYM(long clubId, int year, int month){
        LocalDate startDate = LocalDate.of(year, month, 1);

        Optional<ClubEntity> clubOpt = clubRepository.findById(clubId);
        if(clubOpt.isEmpty()){
            return "Wrong club Id";
        }
        List<TransactionEntity> transactionList = transactionRepository.findByClubId(clubId);
        if(transactionList.isEmpty()){
            return "no data for account : "+clubId;
        }
        Map<String, Integer> transactionTypeCnt = new HashMap<>();
        transactionTypeCnt.put("입금",0);
        transactionTypeCnt.put("출금",0);
        PieChart pieChart = new PieChartBuilder()
                .width(500)
                .height(500)
                .title(year + "년 " + month + "월의 "+clubOpt.get().getClubName() +"의 거래 내역")
                .build();

        for(TransactionEntity t : transactionList){
            String type = t.getTransactionType();
            transactionTypeCnt.put(type, transactionTypeCnt.getOrDefault(0,transactionTypeCnt.get(type))+1);
        }
        Random random = new Random();
        //입금, 출금
        Color [] randomColors = new Color[2];
        for (int i = 0; i < 2; i++) {
            randomColors[i] = new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
        }
        pieChart.getStyler().setSeriesColors(randomColors);
        for (Map.Entry<String, Integer> entry : transactionTypeCnt.entrySet()) {
            pieChart.addSeries(entry.getKey(), entry.getValue());
        }
        String filePath = "./transaction_type_chart_"+clubOpt.get().getClubName()+".svg";
        try{
            VectorGraphicsEncoder.saveVectorGraphic(pieChart, filePath, VectorGraphicsEncoder.VectorGraphicsFormat.SVG);
            File svgFile = new File(filePath);
            return new String(Files.readAllBytes(svgFile.toPath()));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
