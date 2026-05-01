package com.onlinecourse.service.impl;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class FirebaseService {

    public void pushMessageToFirebase(Integer roomId, Integer senderId, String senderName, String avatar, String content) {

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("messages")
                .child(String.valueOf(roomId));

        DatabaseReference newMessageRef = ref.push();

        Map<String, Object> messageData = new HashMap<>();
        messageData.put("senderId", senderId);
        messageData.put("senderName", senderName);
        messageData.put("avatar", avatar);
        messageData.put("content", content);
        messageData.put("timestamp", System.currentTimeMillis());

        newMessageRef.setValueAsync(messageData);
    }
}
