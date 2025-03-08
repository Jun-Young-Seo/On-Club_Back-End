package com.springboot.club_house_api_server.s3.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.springboot.club_house_api_server.club.entity.ClubEntity;
import com.springboot.club_house_api_server.club.repository.ClubRepository;
import com.springboot.club_house_api_server.club_data.entity.ClubDataEntity;
import com.springboot.club_house_api_server.club_data.repository.ClubDataRepository;
import com.springboot.club_house_api_server.s3.dto.S3UploadDto;
import com.springboot.club_house_api_server.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class S3Service {
    private final AmazonS3 amazonS3;
    private final ClubRepository clubRepository;
    private final ClubDataRepository clubDataRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String uploadFile(S3UploadDto dto) throws IOException {
        MultipartFile image = dto.getFile();
        long clubId = dto.getClubId();
        long userId = dto.getUserId();
        String scenario = dto.getScenario();
        Optional<ClubEntity> clubOpt = clubRepository.findById(clubId);
        if(clubOpt.isEmpty()){
            return "clubId에 해당하는 클럽이 없습니다.";
        }
        ClubEntity club = clubOpt.get();
        String clubName = club.getClubName();
        String fileName;
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String now = LocalDateTime.now().format(dateTimeFormatter);
        switch (scenario){
            case "logo":
                fileName="logo_"+clubName+"_"+now+"_"+userId+"_"+image.getOriginalFilename();
                break;
            case "background":
                fileName="background_"+clubName+"_"+now+"_"+userId+"_"+image.getOriginalFilename();
                break;
            case "budget":
                fileName="budget_"+clubName+"_"+now+"_"+userId+"_"+image.getOriginalFilename();
                break;
            case "userlist":
                fileName="userlist_"+clubName+"_"+now+"_"+userId+"_"+image.getOriginalFilename();
                break;
            default:
                fileName="etc_"+clubName+"_"+now+"_"+userId+"_"+image.getOriginalFilename();
                break;
        }

        // 메타데이터 설정
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(image.getContentType());
        metadata.setContentLength(image.getSize());

        // S3에 파일 업로드 요청 생성
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, fileName, image.getInputStream(), metadata);
        String fileURL = getPublicUrl(fileName);

        // S3에 파일 업로드
        try {
            amazonS3.putObject(putObjectRequest);
        }catch (Exception e){
            return "오류 발생 "+e;
        }

        saveClubData(clubId, fileURL, fileName, LocalDateTime.now(), userId, scenario);
        return fileURL;
    }

    public void saveClubData(long clubId, String fileURL, String fileName,LocalDateTime time,long userId, String scenario){
        Optional<ClubEntity> clubOpt = clubRepository.findById(clubId);
        if (clubOpt.isEmpty()) {
            throw new IllegalArgumentException("clubId에 해당하는 클럽이 존재하지 않습니다.");
        }
        ClubEntity club = clubOpt.get();

        ClubDataEntity clubData = ClubDataEntity.builder()
                .club(club)
                .dataUrl(fileURL)
                .dataName(fileName)
                .dataDate(time)
                .dataWho(String.valueOf(userId))
                .dataScenario(scenario)
                .build();

        clubDataRepository.save(clubData);

    }

    private String getPublicUrl(String fileName) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucket, amazonS3.getRegionName(), fileName);
    }

}
