package com.springboot.club_house_api_server.apn.controller;

import com.springboot.club_house_api_server.apn.service.APNsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/apn")
public class APNController {
    private final APNsService apnsService;

    @GetMapping("/test")
    public ResponseEntity<?> sendPush(@RequestParam String deviceToken) throws Exception {
        try {
            apnsService.sendTestPush(deviceToken);
            return ResponseEntity.ok("푸시 전송 시도됨");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("푸시 전송 실패: " + e.getMessage());
        }

    }

}
