package com.example.commuuu;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import DB.Gesipan;

public class CommunityArticleList extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private TextView[] textViews; // 데이터를 표시할 TextView 배열
    private ImageView[] imageViews; // 데이터를 표시할 ImageView 배열
    private CheckBox phoneCheckBox, earCheckBox, gripTalkCheckBox;
    private EditText searchEditText;
    private List<String> selectedCategories = new ArrayList<>();
    private String searchText = "";
    private int currentPage = 1;
    private static final int ITEMS_PER_PAGE = 6; // 페이지당 아이템 수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.community_article_list);

        // TextView 및 ImageView 배열 연결
        textViews = new TextView[] {
                findViewById(R.id.community_TextSetBox_1),
                findViewById(R.id.community_TextSetBox_2),
                findViewById(R.id.community_TextSetBox_3),
                findViewById(R.id.community_TextSetBox_4),
                findViewById(R.id.community_TextSetBox_5),
                findViewById(R.id.community_TextSetBox_6)
        };

        imageViews = new ImageView[] {
                findViewById(R.id.community_TitleImage_1),
                findViewById(R.id.community_TitleImage_2),
                findViewById(R.id.community_TitleImage_3),
                findViewById(R.id.community_TitleImage_4),
                findViewById(R.id.community_TitleImage_5),
                findViewById(R.id.community_TitleImage_6)
        };

        phoneCheckBox = findViewById(R.id.community_PhoneCheckBox);
        earCheckBox = findViewById(R.id.community_earCheckBox);
        gripTalkCheckBox = findViewById(R.id.community_GripTalkCheckBox);
        searchEditText = findViewById(R.id.editTextText);

        // 버튼 연결 및 클릭 리스너 설정
        View writeButton = findViewById(R.id.Community_WriteButton);
        writeButton.setOnClickListener(v -> {
            Intent intent = new Intent(CommunityArticleList.this, GaesipanWritingscreen.class);
            startActivity(intent);
        });

        Button pageBtn1 = findViewById(R.id.Community_PageBtn1);
        Button pageBtn2 = findViewById(R.id.Community_PageBtn2);
        Button pageBtn3 = findViewById(R.id.Community_PageBtn3);
        Button pageBtn4 = findViewById(R.id.Community_PageBtn4);
        Button pageBtn5 = findViewById(R.id.Community_PageBtn5);
        Button buttonLeft = findViewById(R.id.Community_buttonLeft);
        Button buttonRight = findViewById(R.id.Community_buttonRight);

        pageBtn1.setOnClickListener(v -> setPage(1));
        pageBtn2.setOnClickListener(v -> setPage(2));
        pageBtn3.setOnClickListener(v -> setPage(3));
        pageBtn4.setOnClickListener(v -> setPage(4));
        pageBtn5.setOnClickListener(v -> setPage(5));
        buttonLeft.setOnClickListener(v -> {
            if (currentPage > 1) {
                setPage(currentPage - 1);
            }
        });
        buttonRight.setOnClickListener(v -> {
            if (currentPage < 5) {
                setPage(currentPage + 1);
            }
        });

        phoneCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> updateFilter());
        earCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> updateFilter());
        gripTalkCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> updateFilter());
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchText = s.toString();
                updateFilter();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // 데이터베이스 참조 초기화
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // 초기 데이터 로드
        readGesipanData();
    }

    private void readGesipanData() {
        DatabaseReference gesipanRef = mDatabase.child("gesipans");
        Query query = gesipanRef.orderByChild("writeTime");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Gesipan> gesipanList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Gesipan gesipan = snapshot.getValue(Gesipan.class);
                    if (gesipan != null) {
                        gesipanList.add(gesipan);
                    }
                }
                // 필터링 및 페이지네이션 처리
                updateUI(gesipanList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("CommunityArticleList", "loadGesipan:onCancelled", databaseError.toException());
            }
        });
    }

    private void updateFilter() {
        selectedCategories.clear();
        if (phoneCheckBox.isChecked()) {
            selectedCategories.add("phone");
        }
        if (earCheckBox.isChecked()) {
            selectedCategories.add("ear");
        }
        if (gripTalkCheckBox.isChecked()) {
            selectedCategories.add("gripTalk");
        }
        readGesipanData();
    }

    private void updateUI(List<Gesipan> gesipanList) {
        // 필터링
        List<Gesipan> filteredList = new ArrayList<>();
        for (Gesipan gesipan : gesipanList) {
            boolean matchesSearchTerm = searchText.isEmpty() || gesipan.title.contains(searchText);
            boolean matchesCategory = selectedCategories.isEmpty() || selectedCategories.contains(gesipan.category);

            if (matchesSearchTerm && matchesCategory) {
                filteredList.add(gesipan);
            }
        }

        // 페이지네이션
        int startIndex = (currentPage - 1) * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, filteredList.size());

        for (int i = 0; i < textViews.length; i++) {
            if (i + startIndex < endIndex) {
                Gesipan gesipan = filteredList.get(i + startIndex);
                Date date = new Date(gesipan.writeTime);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String formattedDate = dateFormat.format(date);
                textViews[i].setText(gesipan.title + "\n" + gesipan.nickName + "   " + formattedDate);

                if (gesipan.photoUrl != null && !gesipan.photoUrl.isEmpty()) {
                    Glide.with(this)
                            .load(gesipan.photoUrl)
                            .placeholder(R.drawable.loding) // 이미지 로딩 중 표시할 이미지
                            .into(imageViews[i]);
                } else {
                    Glide.with(this)
                            .load(R.drawable.noimage_set) // 이미지가 없을 때 표시할 이미지
                            .into(imageViews[i]);
                }

                final Gesipan currentGesipan = gesipan;
                textViews[i].setOnClickListener(v -> openPostDetail(currentGesipan));
                imageViews[i].setOnClickListener(v -> openPostDetail(currentGesipan));
            } else {
                textViews[i].setText(""); // 빈 데이터
                imageViews[i].setImageDrawable(null); // 빈 이미지
            }
        }

        // 페이지 버튼 텍스트 업데이트
        updatePageButtons();
    }

    private void openPostDetail(Gesipan gesipan) {
        Intent intent = new Intent(CommunityArticleList.this, PostDetailActivity.class);
        intent.putExtra("postId", gesipan.id); // 게시글 ID
        intent.putExtra("pageNum", currentPage); // 현재 페이지 번호
        startActivity(intent);
    }


    private void updatePageButtons() {
        Button pageBtn1 = findViewById(R.id.Community_PageBtn1);
        Button pageBtn2 = findViewById(R.id.Community_PageBtn2);
        Button pageBtn3 = findViewById(R.id.Community_PageBtn3);
        Button pageBtn4 = findViewById(R.id.Community_PageBtn4);
        Button pageBtn5 = findViewById(R.id.Community_PageBtn5);

        // 각 버튼에 페이지 번호 설정
        pageBtn1.setText("1");
        pageBtn2.setText("2");
        pageBtn3.setText("3");
        pageBtn4.setText("4");
        pageBtn5.setText("5");

        // 버튼 상태 업데이트
        pageBtn1.setEnabled(currentPage != 1);
        pageBtn2.setEnabled(currentPage != 2);
        pageBtn3.setEnabled(currentPage != 3);
        pageBtn4.setEnabled(currentPage != 4);
        pageBtn5.setEnabled(currentPage != 5);
    }

    private void setPage(int pageNumber) {
        currentPage = pageNumber;
        readGesipanData();
    }
}
