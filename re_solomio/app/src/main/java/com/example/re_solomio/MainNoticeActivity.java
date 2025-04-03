package com.example.re_solomio;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainNoticeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.main_notice_activity);

        //이거 주석처리 해놓은 동작됨 이유는 머르게씁;;;;;
        /*getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/
    }
}
