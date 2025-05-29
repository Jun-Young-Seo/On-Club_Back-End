package com.springboot.club_house_api_server.apn.service;

import com.eatthepath.pushy.apns.ApnsClient;
import com.eatthepath.pushy.apns.PushNotificationResponse;
import com.eatthepath.pushy.apns.util.SimpleApnsPayloadBuilder;
import com.eatthepath.pushy.apns.util.SimpleApnsPushNotification;
import com.eatthepath.pushy.apns.util.concurrent.PushNotificationFuture;
import com.springboot.club_house_api_server.apn.config.APNsConfig;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class APNsService {

    private final ApnsClient apnsClient;

    @Value("${apn.bundle.id}")
    private String bundleId;

    public ResponseEntity<?> sendPush(String deviceToken) throws ExecutionException, InterruptedException {
        String payload = new SimpleApnsPayloadBuilder()
                .setAlertTitle("On-Club 알림")
                .setAlertBody("On-Club에서 알림이 도착했어요.")
                .setSound("default")
                .build();

        SimpleApnsPushNotification notification =
                new SimpleApnsPushNotification(deviceToken, bundleId, payload);

        PushNotificationFuture<SimpleApnsPushNotification, PushNotificationResponse<SimpleApnsPushNotification>> future =
                apnsClient.sendNotification(notification);

        PushNotificationResponse<SimpleApnsPushNotification> response = future.get();
        if(response.isAccepted()){
            return ResponseEntity.ok("");
        }
        else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

}
