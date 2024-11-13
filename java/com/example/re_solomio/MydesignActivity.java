package com.example.re_solomio;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MydesignActivity extends AppCompatActivity {

    private RecyclerView designRecyclerView;
    private List<Bitmap> designList;
    private MyDesignAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mypage_mydesign_activity);

        designRecyclerView = findViewById(R.id.designRecyclerView);
        designRecyclerView.setLayoutManager(new GridLayoutManager(this, 3)); // 3열 그리드 레이아웃 설정

        designList = new ArrayList<>();
        adapter = new MyDesignAdapter(designList, this::showFullImage);
        designRecyclerView.setAdapter(adapter);

        loadSavedDesigns();
    }

    private void loadSavedDesigns() {
        File directory = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString());
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null && files.length > 0) {
                for (File file : files) {
                    if (file.getAbsolutePath().endsWith(".png")) {
                        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                        designList.add(bitmap);
                    }
                }
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(this, "저장된 디자인이 없습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showFullImage(Bitmap bitmap) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.full_image_dialog);
        ImageView fullImageView = dialog.findViewById(R.id.full_image_view);
        fullImageView.setImageBitmap(bitmap);
        dialog.show();
    }
}