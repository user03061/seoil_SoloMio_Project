package com.example.re_solomio;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class OrderDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Button return_home;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.orderdetails);

        return_home = findViewById(R.id.returnHomeButton);
        return_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderDetailActivity.this, MainHomeActivity.class);
                startActivity(intent);
            }
        });


    }
}
