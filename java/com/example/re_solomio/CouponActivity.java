package com.example.re_solomio;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

public class CouponActivity extends AppCompatActivity {

    private final int myCouponFragment = 1;
    private final int downCouponFragment = 2;
    private final int exCouponFrgament = 3;
    Button mycoupon_btn, download_btn, expire_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.mypage_coupon_activity);

        Toolbar toolbar = findViewById(R.id.coupon_toolbar);
        setSupportActionBar(toolbar);

        mycoupon_btn = findViewById(R.id.coupon_mycoupon);
        mycoupon_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentView(myCouponFragment);
            }
        });

        download_btn = findViewById(R.id.coupon_dlcoupon);
        download_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentView(downCouponFragment);
            }
        });

        expire_btn = findViewById(R.id.coupon_excoupon);
        expire_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentView(exCouponFrgament);
            }
        });

        if (savedInstanceState == null){
            FragmentView(myCouponFragment);
        }
    }

    private void FragmentView(int fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        switch (fragment){
            case 1:
                CouponMyFragment myCouponFragment = new CouponMyFragment();
                transaction.replace(R.id.coupon_container, myCouponFragment);
                transaction.commit();
                break;

            case 2:
                CouponDlFragment downloadCouponFragment = new CouponDlFragment();
                transaction.replace(R.id.coupon_container, downloadCouponFragment);
                transaction.commit();
                break;
            case 3:
                CouponExFragment expireCouponFragment = new CouponExFragment();
                transaction.replace(R.id.coupon_container, expireCouponFragment);
                transaction.commit();
                break;
        }
    }
}
