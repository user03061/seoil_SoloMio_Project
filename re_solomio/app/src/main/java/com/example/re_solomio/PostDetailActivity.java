package com.example.re_solomio;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
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
    private static final String KEY_HAS_LIKED = "hasLiked_";
    private static final String KEY_HAS_ZZIM = "hasZzim_";

    private DatabaseReference mDatabase;
    private TextView readTitle, someId, viewsCount, readingText, categoryText;
    private ImageView commuReadingImageSet, downloadIcon, likeButton, zzimButton;
    private boolean hasLiked = false;
    private boolean hasZzim = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.commureading_activity);

        readTitle = findViewById(R.id.readTitle);
        someId = findViewById(R.id.some_id);
        viewsCount = findViewById(R.id.viewsCount);
        readingText = findViewById(R.id.readingText);
        categoryText = findViewById(R.id.category_text);
        commuReadingImageSet = findViewById(R.id.commuReading_imageSet);
        downloadIcon = findViewById(R.id.downloadIcon);
        likeButton = findViewById(R.id.like_button);
        zzimButton = findViewById(R.id.cummunity_zzim_button);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        String postId = getIntent().getStringExtra("postId");
        if (postId != null) {
            loadPostDetails(postId);
            checkIfUserLikedPost(postId);
            checkIfUserZzimmedPost(postId);
        }

        likeButton.setOnClickListener(v -> {
            if (!hasLiked) {
                incrementLikeCount(postId);
                likeButton.setImageResource(R.drawable.like_selected_icon);
                hasLiked = true;
                markPostAsLiked(postId);
            } else {
                Toast.makeText(PostDetailActivity.this, "이미 좋아요를 눌렀습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        zzimButton.setOnClickListener(v -> {
            toggleZzimStatus(postId);
        });

        downloadIcon.setOnClickListener(v -> {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_WRITE_STORAGE);
            } else {

                downloadImage();
            }
        });
    }

    private void checkIfUserZzimmedPost(String postId) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        hasZzim = prefs.getBoolean(KEY_HAS_ZZIM + postId, false);
        if (hasZzim) {
            zzimButton.setImageResource(R.drawable.cummunity_check_heart);
        } else {
            zzimButton.setImageResource(R.drawable.community_heart_icon);
        }
    }

    private void toggleZzimStatus(String postId) {
        DatabaseReference postRef = mDatabase.child("gesipans").child(postId).child("zzim");
        postRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                Boolean currentZzim = mutableData.getValue(Boolean.class);
                if (currentZzim == null) {
                    currentZzim = false;
                }
                mutableData.setValue(!currentZzim);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean committed, @Nullable DataSnapshot dataSnapshot) {
                if (databaseError == null) {
                    hasZzim = !hasZzim;
                    if (hasZzim) {
                        zzimButton.setImageResource(R.drawable.cummunity_check_heart);
                        Toast.makeText(PostDetailActivity.this, "찜 목록에 추가되었습니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        zzimButton.setImageResource(R.drawable.community_heart_icon);
                        Toast.makeText(PostDetailActivity.this, "찜 목록에서 제거되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                    markPostAsZzimmed(postId);
                } else {
                    Log.w("PostDetailActivity", "toggleZzimStatus:onComplete", databaseError.toException());
                }
            }
        });
    }

    private void markPostAsZzimmed(String postId) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_HAS_ZZIM + postId, hasZzim);
        editor.apply();
    }

    private void loadPostDetails(String postId) {
        DatabaseReference postRef = mDatabase.child("gesipans").child(postId);
        postRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Gesipan gesipan = dataSnapshot.getValue(Gesipan.class);
                if (gesipan != null) {
                    readTitle.setText(gesipan.title);
                    someId.setText(gesipan.nickName);
                    categoryText.setText("카테고리: " + gesipan.category);

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

    private void checkIfUserLikedPost(String postId) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        hasLiked = prefs.getBoolean(KEY_HAS_LIKED + postId, false);
        if (hasLiked) {
            likeButton.setImageResource(R.drawable.like_selected_icon);
        } else {
            likeButton.setImageResource(R.drawable.like);
        }
    }

    private void markPostAsLiked(String postId) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_HAS_LIKED + postId, true);
        editor.apply();
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

    private void incrementLikeCount(String postId) {
        DatabaseReference postRef = mDatabase.child("gesipans").child(postId);
        postRef.child("starCount").runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                Integer currentLikes = mutableData.getValue(Integer.class);
                if (currentLikes == null) {
                    currentLikes = 0;
                }
                mutableData.setValue(currentLikes + 1);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean committed, @Nullable DataSnapshot dataSnapshot) {
                if (databaseError != null) {
                    Log.w("PostDetailActivity", "incrementLikeCount:onComplete", databaseError.toException());
                }
            }
        });
    }
}
