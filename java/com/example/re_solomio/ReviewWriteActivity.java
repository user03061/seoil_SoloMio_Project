package com.example.re_solomio;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.bumptech.glide.Glide;

import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.provider.MediaStore;


public class ReviewWriteActivity extends AppCompatActivity {

    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private ImageView review_img;
    Button bad_btn, good_btn, dark_btn, same_btn, bright_btn, review_upload_btn, img_upload_btn;
    EditText review_text;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.review_write_activity);

        bad_btn = findViewById(R.id.btn_bad);
        good_btn = findViewById(R.id.btn_good);
        dark_btn = findViewById(R.id.btn_dark);
        same_btn = findViewById(R.id.btn_same);
        bright_btn = findViewById(R.id.btn_bright);
        review_upload_btn = findViewById(R.id.btn_ReviewUpload);
        img_upload_btn = findViewById(R.id.btn_ImgUpload);
        review_text = findViewById(R.id.text_review);
        review_img = findViewById(R.id.Image_review);

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            Glide.with(getApplicationContext())
                                    .load(data.getData())
                                    .override(500, 500)
                                    .into(review_img);
                        }
                    }
                }
        );

        //리뷰창 버튼 클릭 시 나머지 버튼 비활성화
        bad_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bad_btn.setSelected(true);
                good_btn.setSelected(false);
            }
        });

        good_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                good_btn.setSelected(true);
                bad_btn.setSelected(false);
            }
        });

        dark_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dark_btn.setSelected(true);
                same_btn.setSelected(false);
                bright_btn.setSelected(false);
            }
        });

        same_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                same_btn.setSelected(true);
                dark_btn.setSelected(false);
                bright_btn.setSelected(false);
            }
        });

        bright_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bright_btn.setSelected(true);
                dark_btn.setSelected(false);
                same_btn.setSelected(false);
            }
        });

        //리뷰 등록하기 버튼 클릭 시 메시지
        review_upload_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "리뷰가 등록되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        //사진 불러오기
        img_upload_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                imagePickerLauncher.launch(intent);
            }
        });
    }
}

