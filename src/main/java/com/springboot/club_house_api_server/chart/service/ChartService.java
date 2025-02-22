package com.springboot.club_house_api_server.chart.service;

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
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class ChartService {
    private final ClubRepository clubRepository;
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
            ByteArrayOutputStream svgStream = new ByteArrayOutputStream();
            VectorGraphicsEncoder.saveVectorGraphic(pieChart, filePath, VectorGraphicsEncoder.VectorGraphicsFormat.SVG);
            File svgFile = new File(filePath);
            return new String(Files.readAllBytes(svgFile.toPath()));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
