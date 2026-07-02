package com.chemistry.demo.services.notification.Impl;

import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.AndroidNotification;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.stereotype.Service;

@Service
public class FcmSenderService {

    public void sendToToken(
            String token,
            String title,
            String body,
            String type,
            String screen
    ) throws FirebaseMessagingException {

        Message message = Message.builder()
                .setToken(token)
                .setNotification(
                        Notification.builder()
                                .setTitle(title)
                                .setBody(body)
                                .build()
                )
                .putData("type", type)
                .putData("screen", screen)
                .setAndroidConfig(
                        AndroidConfig.builder()
                                .setPriority(AndroidConfig.Priority.HIGH)
                                .setNotification(
                                        AndroidNotification.builder()
                                                .setChannelId("ar_chemistry_high_importance")
                                                .setClickAction("FLUTTER_NOTIFICATION_CLICK")
                                                .build()
                                )
                                .build()
                )
                .build();

        FirebaseMessaging.getInstance().send(message);
    }
}