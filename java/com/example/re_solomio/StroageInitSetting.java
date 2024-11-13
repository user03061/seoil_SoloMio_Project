package com.example.re_solomio;

import android.app.Application;
import com.google.firebase.FirebaseApp;

public class StroageInitSetting extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Firebase 초기화
        FirebaseApp.initializeApp(this);
    }
}
