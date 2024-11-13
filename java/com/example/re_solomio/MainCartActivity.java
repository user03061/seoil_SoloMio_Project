package com.example.re_solomio;


import DB.items;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


public class MainCartActivity extends AppCompatActivity {

    CheckBox checkBox01, checkAll;
    Button plus_btn, min_btn, order_btn;
    TextView count;
    int num = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.main_cart_activity);

        Toolbar myToolbar = findViewById(R.id.cart_toolbar);
        setSupportActionBar(myToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        plus_btn = findViewById(R.id.cart_plusbtn);
        count = findViewById(R.id.cart_count);
        min_btn = findViewById(R.id.cart_minbtn);

        //수량 버튼 +
        plus_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                num++;
                count.setText(num + "");

                items Items = new items();
                Items.setItemCount(num);
            }
        });

        //수량 버튼 -
        min_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                items Items = new items();
                if(num >1){
                    num--;
                    count.setText(num+"");

                    Items.setItemCount(num);
                }}

        });

        checkAll = findViewById(R.id.cart_checkall);
        checkBox01 = findViewById(R.id.cart_check1);

        //전체 상품 선택 클릭
        checkAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = checkAll.isChecked();
                checkBox01.setChecked(isChecked);
                if (isChecked){
                    order_btn.setEnabled(true);
                }
                else {
                    order_btn.setEnabled(false);
                }
            }
        });

        checkBox01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = checkBox01.isChecked();
                checkBox01.setChecked(isChecked);
                if (isChecked){
                    order_btn.setEnabled(true);
                }
                else {
                    order_btn.setEnabled(false);
                }
            }
        });

        //결제 창 intent
        order_btn = findViewById(R.id.cart_orderbtn);
        order_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainCartActivity.this, PaymentActivity.class);
                intent.putExtra("itemCount", num); //수량 설정값을 전달해야됨.
                startActivity(intent);
            }
        });
    }
}
