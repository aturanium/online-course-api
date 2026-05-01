package com.onlinecourse.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void initialize() {
        try {

            if (FirebaseApp.getApps().isEmpty()) {

                InputStream serviceAccount
                        = getClass().getClassLoader().getResourceAsStream("firebase-service-account.json");

                if (serviceAccount == null) {
                    throw new RuntimeException("Không tìm thấy file firebase-service-account.json trong thư mục resources!");
                }

                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .setDatabaseUrl("https://onlinecourse-728de-default-rtdb.firebaseio.com/")
                        .build();

                FirebaseApp.initializeApp(options);
                System.out.println("====== FIREBASE ĐÃ KHỞI TẠO THÀNH CÔNG! ======");
            }
        } catch (IOException | RuntimeException e) {
            System.err.println("====== LỖI KHỞI TẠO FIREBASE ======");
        }
    }
}
