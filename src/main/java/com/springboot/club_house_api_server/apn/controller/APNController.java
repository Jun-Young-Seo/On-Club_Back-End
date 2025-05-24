package com.springboot.club_house_api_server.apn.controller;

import com.springboot.club_house_api_server.apn.service.APNsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/apn")
public class APNController {
    private final APNsService apnsService;

    @GetMapping("/test")
    public ResponseEntity<?> testAPN() throws Exception {
        apnsService.sendTestPush("ㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏ");
        return ResponseEntity.ok().build();
    }

}
