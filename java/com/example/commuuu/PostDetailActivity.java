package com.example.commuuu;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import DB.Gesipan;

public class PostDetailActivity extends AppCompatActivity {

    private static final int REQUEST_WRITE_STORAGE = 112;
    private static final String PREFS_NAME = "PostPrefs";
    private static final String KEY_HAS_VIEWED = "hasViewed_";

    private DatabaseReference mDatabase;
    private TextView readTitle, someId, viewsCount, readingText, categoryText;
    private ImageView commuReadingImageSet, downloadIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.commureading_activity);

        readTitle = findViewById(R.id.readTitle);
        someId = findViewById(R.id.some_id);
        viewsCount = findViewById(R.id.viewsCount);
        readingText = findViewById(R.id.readingText);
        categoryText = findViewById(R.id.categoryText);
        commuReadingImageSet = findViewById(R.id.commuReading_imageSet);
        downloadIcon = findViewById(R.id.downloadIcon);

        // Initialize Firebase database reference
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Get postId from intent
        String postId = getIntent().getStringExtra("postId");
        if (postId != null) {
            loadPostDetails(postId);
        }

        downloadIcon.setOnClickListener(v -> {
            // Check for write permission
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_WRITE_STORAGE);
            } else {
                // Permission already granted
                downloadImage();
            }
        });
    }

    private void loadPostDetails(String postId) {
        DatabaseReference postRef = mDatabase.child("gesipans").child(postId);
        postRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Gesipan gesipan = dataSnapshot.getValue(Gesipan.class);
                if (gesipan != null) {
                    // Update UI with post details
                    readTitle.setText(gesipan.title);
                    someId.setText(gesipan.nickName);
                    categoryText.setText(gesipan.category);

                    Date date = new Date(gesipan.writeTime);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                    viewsCount.setText(dateFormat.format(date) + " 조회수:" + gesipan.Views + " 좋아요:" + gesipan.starCount);

                    readingText.setText(gesipan.writeText + "\n\nPage: " + gesipan.pageNum);

                    if (gesipan.photoUrl != null && !gesipan.photoUrl.isEmpty()) {
                        Glide.with(PostDetailActivity.this)
                                .load(gesipan.photoUrl)
                                .placeholder(R.drawable.loding)
                                .into(commuReadingImageSet);
                    } else {
                        Glide.with(PostDetailActivity.this)
                                .load(R.drawable.noimage_set)
                                .into(commuReadingImageSet);
                    }

                    // Increment view count if not already viewed
                    if (!hasViewedPost(postId)) {
                        incrementViewCount(postId);
                        markPostAsViewed(postId);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("PostDetailActivity", "loadPostDetails:onCancelled", databaseError.toException());
            }
        });
    }

    private void incrementViewCount(String postId) {
        DatabaseReference postRef = mDatabase.child("gesipans").child(postId);
        postRef.child("Views").runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                Integer currentViews = mutableData.getValue(Integer.class);
                if (currentViews == null) {
                    currentViews = 0;
                }
                mutableData.setValue(currentViews + 1);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean committed, @Nullable DataSnapshot dataSnapshot) {
                if (databaseError != null) {
                    Log.w("PostDetailActivity", "incrementViewCount:onComplete", databaseError.toException());
                }
            }
        });
    }

    private void downloadImage() {
        Glide.with(this)
                .asBitmap()
                .load(((BitmapDrawable) commuReadingImageSet.getDrawable()).getBitmap())
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        // Save bitmap to file
                        File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Commuuu");
                        if (!directory.exists()) {
                            directory.mkdirs();
                        }
                        File file = new File(directory, "downloaded_image.jpg");
                        try (FileOutputStream out = new FileOutputStream(file)) {
                            resource.compress(Bitmap.CompressFormat.JPEG, 100, out);
                            Toast.makeText(PostDetailActivity.this, "Image downloaded successfully", Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(PostDetailActivity.this, "Failed to download image", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });
    }

    private boolean hasViewedPost(String postId) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_HAS_VIEWED + postId, false);
    }

    private void markPostAsViewed(String postId) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_HAS_VIEWED + postId, true);
        editor.apply();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_WRITE_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                downloadImage();
            } else {
                Toast.makeText(this, "Permission required to save images", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
