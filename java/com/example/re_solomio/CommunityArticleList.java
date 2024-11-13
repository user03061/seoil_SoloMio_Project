package com.example.re_solomio;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ImageButton;
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
    private TextView[] textViews;
    private ImageView[] imageViews;
    private CheckBox phoneCheckBox, earCheckBox, gripTalkCheckBox;
    private EditText searchEditText;
    private List<String> selectedCategories = new ArrayList<>();
    private String searchText = "";
    private int currentPage = 1;
    private static final int ITEMS_PER_PAGE = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.community_article_list);

        // TextView 및 ImageView 배열 연결
        textViews = new TextView[]{
                findViewById(R.id.community_TextSetBox_1),
                findViewById(R.id.community_TextSetBox_2),
                findViewById(R.id.community_TextSetBox_3),
                findViewById(R.id.community_TextSetBox_4),
                findViewById(R.id.community_TextSetBox_5),
        };

        imageViews = new ImageView[]{
                findViewById(R.id.community_TitleImage_1),
                findViewById(R.id.community_TitleImage_2),
                findViewById(R.id.community_TitleImage_3),
                findViewById(R.id.community_TitleImage_4),
                findViewById(R.id.community_TitleImage_5),
        };

        phoneCheckBox = findViewById(R.id.community_PhoneCheckBox);
        earCheckBox = findViewById(R.id.community_earCheckBox);
        gripTalkCheckBox = findViewById(R.id.community_GripTalkCheckBox);
        searchEditText = findViewById(R.id.community_Search);

        // 작성 버튼 설정
        ImageButton writeButton = findViewById(R.id.Community_WriteButton);
        writeButton.setOnClickListener(v -> {
            Intent intent = new Intent(CommunityArticleList.this, GaesipanWritingscreen.class);
            intent.putExtra("pageNum", currentPage);
            startActivity(intent);
        });

        // 체크박스 및 검색 텍스트 변경 리스너 설정
        setupCheckBoxListeners();
        setupSearchTextWatcher();

        // 페이지 버튼 설정
        setupPageButtons();

        // 데이터베이스 참조 초기화
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // 초기 데이터 로드
        readGesipanData();
    }

    private void setupPageButtons() {
        Button buttonLeft = findViewById(R.id.Community_buttonLeft);
        Button buttonRight = findViewById(R.id.Community_buttonRight);

        for (int i = 1; i <= 5; i++) {
            int buttonId = getResources().getIdentifier("Community_PageBtn" + i, "id", getPackageName());
            Button pageBtn = findViewById(buttonId);
            final int pageNumber = i;
            pageBtn.setOnClickListener(v -> setPage(pageNumber)); // 페이지 번호 설정
        }

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
    }

    private void setupCheckBoxListeners() {
        phoneCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> updateFilter());
        earCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> updateFilter());
        gripTalkCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> updateFilter());
    }

    private void setupSearchTextWatcher() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchText = s.toString().trim();
                updateFilter();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void readGesipanData() {
        DatabaseReference gesipanRef = mDatabase.child("gesipans");
        Query query = gesipanRef.orderByChild("writeTime"); // 필요에 따라 수정 가능

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Gesipan> gesipanList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Gesipan gesipan = snapshot.getValue(Gesipan.class);
                    if (gesipan != null) {
                        // 현재 페이지와 일치하는 게시글만 추가
                        if (gesipan.pageNum == currentPage) {
                            gesipanList.add(gesipan);
                        }
                    }
                }
                updateUI(gesipanList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("CommunityArticleList", "loadGesipan:onCancelled", databaseError.toException());
            }
        });
    }


    private void updateUI(List<Gesipan> gesipanList) {
        List<Gesipan> filteredList = new ArrayList<>();
        for (Gesipan gesipan : gesipanList) {
            boolean matchesSearchTerm = searchText.isEmpty() || gesipan.title.contains(searchText);
            boolean matchesCategory = selectedCategories.isEmpty() || selectedCategories.contains(gesipan.category);

            if (matchesSearchTerm && matchesCategory) {
                filteredList.add(gesipan);
            }
        }

        int startIndex = 0; // 페이지 번호와 관계없이 모든 결과를 표시
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
                            .placeholder(R.drawable.loding)
                            .into(imageViews[i]);
                } else {
                    Glide.with(this)
                            .load(R.drawable.noimage_set)
                            .into(imageViews[i]);
                }

                final Gesipan currentGesipan = gesipan;
                textViews[i].setOnClickListener(v -> openPostDetail(currentGesipan));
                imageViews[i].setOnClickListener(v -> openPostDetail(currentGesipan));
            } else {
                textViews[i].setText("");
                imageViews[i].setImageDrawable(null);
            }
        }

        updatePageButtons();
    }

    private void readCategoryGesipanData() {
        DatabaseReference gesipanRef = mDatabase.child("gesipans");

        gesipanRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Gesipan> gesipanList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Gesipan gesipan = snapshot.getValue(Gesipan.class);
                    if (gesipan != null) {
                        // 선택한 카테고리와 일치하는 게시글만 추가
                        if (selectedCategories.contains(gesipan.category)) {
                            gesipanList.add(gesipan);
                        }
                    }
                }
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
            selectedCategories.add("핸드폰");
        }
        if (earCheckBox.isChecked()) {
            selectedCategories.add("이어폰");
        }
        if (gripTalkCheckBox.isChecked()) {
            selectedCategories.add("그립톡");
        }

        // 카테고리에 따른 게시글 로드
        if (!selectedCategories.isEmpty()) {
            readCategoryGesipanData();
        } else {
            // 선택된 카테고리가 없을 경우 모든 게시글 로드
            readGesipanData();
        }
    }


    private void openPostDetail(Gesipan gesipan) {
        Intent intent = new Intent(CommunityArticleList.this, PostDetailActivity.class);
        intent.putExtra("postId", gesipan.id);
        intent.putExtra("pageNum", currentPage);
        startActivity(intent);
    }

    private void updatePageButtons() {
        for (int i = 1; i <= 5; i++) {
            int buttonId = getResources().getIdentifier("Community_PageBtn" + i, "id", getPackageName());
            Button pageBtn = findViewById(buttonId);
            pageBtn.setText(String.valueOf(i));
            pageBtn.setEnabled(currentPage != i);
        }
    }

    private void setPage(int pageNumber) {
        currentPage = pageNumber;
        readGesipanData();
    }


}
