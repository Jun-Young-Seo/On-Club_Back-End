package com.springboot.club_house_api_server.apn.service;

import com.eatthepath.pushy.apns.ApnsClient;
import com.eatthepath.pushy.apns.PushNotificationResponse;
import com.eatthepath.pushy.apns.util.SimpleApnsPayloadBuilder;
import com.eatthepath.pushy.apns.util.SimpleApnsPushNotification;
import com.eatthepath.pushy.apns.util.concurrent.PushNotificationFuture;
import com.springboot.club_house_api_server.apn.config.APNsConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class APNsService {

    private final String bundleId = "algorithm.capstone-2025";
    private final APNsConfig apnsConfig;
    private final ApnsClient apnsClient;

    public SimpleApnsPushNotification sendTestPush(String deviceToken) throws Exception {
        String payload = new SimpleApnsPayloadBuilder()
                .setAlertTitle("테스트 푸시")
                .setAlertBody("이 알림이 보이면 연결 성공!")
                .setSound("default")
                .build();

        SimpleApnsPushNotification notification =
                new SimpleApnsPushNotification(deviceToken, bundleId, payload);

        ApnsClient apnsClient = apnsConfig.apnsClient();

        PushNotificationFuture<SimpleApnsPushNotification, PushNotificationResponse<SimpleApnsPushNotification>> sendNotificationFuture = apnsClient.sendNotification(notification);
        PushNotificationResponse<SimpleApnsPushNotification> pushNotificationResponse = sendNotificationFuture.get();
        if (!pushNotificationResponse.isAccepted()) {
            System.err.println("Notification rejected by the APNs gateway: " + pushNotificationResponse.getRejectionReason());
        }
        return pushNotificationResponse.getPushNotification();
    };
 }
