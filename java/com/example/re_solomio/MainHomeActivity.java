package com.example.re_solomio;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainHomeActivity extends AppCompatActivity {

    private Fragment mainHomeFragment;
    private Fragment mainMyPageFragment;
    private Fragment mainZzimFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.main_home_frame);

        mainHomeFragment = new MainHomeFragment();
        mainMyPageFragment = new MainMyPageFragment();
        mainZzimFragment = new MainZzimFragment();

        if (savedInstanceState == null) {
            replaceFragment(new MainHomeFragment());
        }

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bnav_main);
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                Fragment selectedFragment = null;

                if (itemId == R.id.home_menu) {
                    selectedFragment = new MainHomeFragment();
                } else if (itemId == R.id.myPage_menu) {
                    selectedFragment = new MainMyPageFragment();
                } else if (itemId == R.id.favorite_menu){
                    selectedFragment = new MainZzimFragment();
                } else if (itemId == R.id.community_menu) {
                    Intent intent = new Intent(MainHomeActivity.this, CommunityArticleList.class);
                    startActivity(intent);
                }
                if (selectedFragment != null) {
                    replaceFragment(selectedFragment);
                }
                return true;
            }
        });

    }


    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.container_main, fragment);
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.notice_cart, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.notifi_main){
            Intent intent = new Intent(MainHomeActivity.this, MainNoticeActivity.class);
            startActivity(intent);
            return true;
        }
        else if (id == R.id.cart_main){
            Intent intent = new Intent(MainHomeActivity.this, MainCartActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}