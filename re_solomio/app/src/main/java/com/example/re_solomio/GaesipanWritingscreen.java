package com.example.re_solomio;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;
import java.util.UUID;

import DB.Gesipan;

public class GaesipanWritingscreen extends AppCompatActivity {

    private EditText titleEditText;
    private EditText contentEditText;
    private ListView categoryList;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private DatabaseReference mDatabase;
    private static final int ITEMS_PER_PAGE = 6;
    private String selectedCategory;
    private Uri selectedFileUri;
    private ActivityResultLauncher<Intent> filePickerLauncher;
    private String userId;
    private String nickName; // 사용자 닉네임


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.writecommu_activity);

        // Firebase Storage 및 Realtime Database 초기화
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        titleEditText = findViewById(R.id.titleSet);
        contentEditText = findViewById(R.id.contentEditText);
        categoryList = findViewById(R.id.categoreSet);

        Button imsiButton = findViewById(R.id.imsibutton);
        Button gesiButton = findViewById(R.id.gesibutton);
        View closeButton = findViewById(R.id.cancel_write_btn);

        closeButton.setOnClickListener(v -> finish());

        imsiButton.setOnClickListener(v -> saveTempData());

        gesiButton.setOnClickListener(v -> {
            if (validateInputs()) {
                savePostToFirebase();
            }
        });

        setupCategoryList();
        setupBottomBar();

        filePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedFileUri = result.getData().getData();
                    }
                }
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences("MyApp", MODE_PRIVATE);
        String tempTitle = sharedPreferences.getString("tempTitle", "");
        String tempContent = sharedPreferences.getString("tempContent", "");

        titleEditText.setText(tempTitle);
        contentEditText.setText(tempContent);
    }

    private boolean validateInputs() {
        String title = titleEditText.getText().toString();
        String content = contentEditText.getText().toString();
        if (title.isEmpty() || content.isEmpty() || selectedCategory == null) {
            Toast.makeText(this, "제목, 내용, 카테고리를 모두 입력해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void setupCategoryList() {
        String[] categories = getResources().getStringArray(R.array.categore);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, categories);
        categoryList.setAdapter(adapter);
        categoryList.setOnItemClickListener((parent, view, position, id) -> showCategoryDialog(categories));
    }

    private void showCategoryDialog(String[] categories) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("카테고리 선택");

        builder.setItems(categories, (dialog, which) -> {
            selectedCategory = categories[which];
            Toast.makeText(getApplicationContext(), selectedCategory + " 선택됨", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("취소", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void setupBottomBar() {
        ImageButton smileButton = findViewById(R.id.smileButton);
        smileButton.setOnClickListener(v -> showEmojiDialog());

        ImageButton fileButton = findViewById(R.id.fileButton);
        fileButton.setOnClickListener(v -> selectFile());
    }

    private void saveTempData() {
        String title = titleEditText.getText().toString();
        String content = contentEditText.getText().toString();

        SharedPreferences sharedPreferences = getSharedPreferences("MyApp", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("tempTitle", title);
        editor.putString("tempContent", content);
        editor.apply();

        Toast.makeText(getApplicationContext(), "임시 저장 완료!", Toast.LENGTH_SHORT).show();
    }

    private void savePostToFirebase() {
        String title = titleEditText.getText().toString();
        String content = contentEditText.getText().toString();
        String photoUrl = null; // 기본값: 사진 URL 없음

        if (title.isEmpty() || content.isEmpty() || selectedCategory == null) {
            Toast.makeText(this, "제목, 내용, 카테고리를 모두 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        mDatabase.child("userIdCounter").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int currentUserId = dataSnapshot.getValue(Integer.class) != null ? dataSnapshot.getValue(Integer.class) : 2; // 기본값 2
                userId = String.valueOf(currentUserId); // 현재 사용자 ID
                nickName = "user"; // 여기에 실제 닉네임을 설정

                // 이미지 URL 확인
                if (selectedFileUri != null) {
                    uploadFileToFirebase(selectedFileUri, url -> {
                        // 업로드 이미지 URL
                        savePostToDatabase(title, content, userId, nickName, url);
                    });
                } else {
                    savePostToDatabase(title, content, userId, nickName, photoUrl);
                }

                // userId를 1 증가시켜 저장
                mDatabase.child("userIdCounter").setValue(currentUserId + 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(GaesipanWritingscreen.this, "userId 증가 실패: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadFileToFirebase(Uri fileUri, OnFileUploadCompleteListener listener) {
        if (fileUri != null) {
            final StorageReference fileReference = storageReference.child("/" + UUID.randomUUID().toString());

            fileReference.putFile(fileUri)
                    .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        String downloadUrl = uri.toString();
                        listener.onFileUploadComplete(downloadUrl); // URL 전달
                    }))
                    .addOnFailureListener(e -> {
                        Toast.makeText(GaesipanWritingscreen.this, "파일 업로드 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void savePostToDatabase(String title, String content, String userId, String nickName, String photoUrl) {
        mDatabase.child("gesipans").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int totalPosts = (int) dataSnapshot.getChildrenCount(); // 총 게시글 수
                int pageNum = (totalPosts / ITEMS_PER_PAGE) + 1; // 6개당 페이지 넘버 증가
                long currentTime = System.currentTimeMillis(); // 현재 시간
                boolean zzim = false; // zzim의 기본값을 false로 설정

                // 새로운 게시글 객체 생성 (zzim 추가)
                Gesipan post = new Gesipan(userId, nickName, content, title, currentTime, 0, 0, pageNum, selectedCategory, photoUrl, zzim);

                Map<String, Object> postMap = post.toMap();

                // 새로운 키 생성하여 게시글 저장
                String key = userId;

                if (key != null) {
                    mDatabase.child("gesipans").child(key).setValue(postMap)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(GaesipanWritingscreen.this, "게시글이 등록되었습니다.", Toast.LENGTH_SHORT).show();
                                finish();  // 액티비티 닫기
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(GaesipanWritingscreen.this, "게시글 등록 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                } else {
                    Toast.makeText(GaesipanWritingscreen.this, "게시글 저장 실패: 키 생성 오류", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(GaesipanWritingscreen.this, "게시글 조회 실패: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    interface OnFileUploadCompleteListener {
        void onFileUploadComplete(String photoUrl);
    }

    private void showEmojiDialog() {
        final String[] emojis = {"😀", "😂", "😍", "😎", "😭"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("이모티콘 선택");

        builder.setItems(emojis, (dialog, which) -> {
            String currentText = contentEditText.getText().toString();
            contentEditText.setText(currentText + emojis[which]);
            contentEditText.setSelection(contentEditText.getText().length());
        });

        builder.show();
    }

    private void selectFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            filePickerLauncher.launch(Intent.createChooser(intent, "파일 선택"));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "파일 선택을 지원하는 앱이 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }
}
