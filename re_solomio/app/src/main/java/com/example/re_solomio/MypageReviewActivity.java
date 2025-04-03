package com.example.re_solomio;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MypageReviewActivity extends AppCompatActivity {

    Button write_btn1, write_btn2, myreview_btn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.mypage_review_activity);

        //리뷰 작성하기 버튼
        write_btn1 = findViewById(R.id.writebtn1);
        write_btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MypageReviewActivity.this, ReviewWriteActivity.class);
                startActivity(intent);
            }
        });

        write_btn2 = findViewById(R.id.writebtn2);
        write_btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MypageReviewActivity.this, ReviewWriteActivity.class);
                startActivity(intent);
            }
        });

        //작성한 리뷰 버튼
        myreview_btn = findViewById(R.id.writehistorybtn);
        myreview_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MypageReviewActivity.this, ReviewMyActivity.class);
                startActivity(intent);
            }
        });

    }
}