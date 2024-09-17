package com.example.commuuu;

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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;

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
    private static final int FILE_SELECT_CODE = 200;
    private String selectedCategory;
    private Uri selectedFileUri;

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
                Toast.makeText(GaesipanWritingscreen.this, "게시글이 등록되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        setupCategoryList();
        setupBottomBar();
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

        categoryList.setOnItemClickListener((parent, view, position, id) -> {
            selectedCategory = categories[position];
            Toast.makeText(getApplicationContext(), selectedCategory + " 선택됨", Toast.LENGTH_SHORT).show();
        });
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
        String title = titleEditText.getText().toString();       // 사용자가 입력한 제목
        String content = contentEditText.getText().toString();   // 사용자가 입력한 내용
        String userId = "2"; // 사용자 ID (기본값)
        String nickName = "lsj03"; // 사용자 닉네임 (기본값)
        String photoUrl = null; // 기본값: 사진 URL 없음

        if (title.isEmpty() || content.isEmpty() || selectedCategory == null) {
            Toast.makeText(this, "제목, 내용, 카테고리를 모두 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        //이미지 url 확인
        if (selectedFileUri != null) {
            uploadFileToFirebase(selectedFileUri, url -> {
                //업로드 이미지 url
                savePostToDatabase(title, content, userId, nickName, photoUrl);
            });
        } else {

            savePostToDatabase(title, content, userId, nickName, photoUrl);
        }
    }

    private void uploadFileToFirebase(Uri fileUri, OnFileUploadCompleteListener listener) {
        if (fileUri != null) {
            final StorageReference fileReference = storageReference.child("files/" + UUID.randomUUID().toString());

            fileReference.putFile(fileUri)
                    .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        String downloadUrl = uri.toString();
                        listener.onFileUploadComplete(downloadUrl);
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
                int pageNum = (totalPosts / 6) + 1; // 6개당 페이지 넘버 증가
                long currentTime = System.currentTimeMillis(); // 현재 시간

                //게시판 객체 생성
                Gesipan post = new Gesipan(userId, nickName, content, title, currentTime, 0, pageNum, selectedCategory, photoUrl);


                Map<String, Object> postMap = post.toMap();


                String key = userId; //유저 아이디를 db키값으로 지정

                // Save post under the new key
                mDatabase.child("gesipans").child(key).setValue(postMap)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(GaesipanWritingscreen.this, "게시글이 등록되었습니다.", Toast.LENGTH_SHORT).show();
                            finish();  // Close the activity
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(GaesipanWritingscreen.this, "게시글 등록 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
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
            startActivityForResult(Intent.createChooser(intent, "파일 선택"), FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "파일 선택을 지원하는 앱이 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FILE_SELECT_CODE && resultCode == RESULT_OK && data != null) {
            Uri selectedFileUri = data.getData();
            if (selectedFileUri != null) {
                uploadFileToFirebase(selectedFileUri);
            }
        }
    }

    private void uploadFileToFirebase(Uri fileUri) {
        if (fileUri != null) {
            final StorageReference fileReference = storageReference.child("files/" + UUID.randomUUID().toString());

            fileReference.putFile(fileUri)
                    .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        String downloadUrl = uri.toString();
                        appendFileUrlToContent(downloadUrl);
                        Toast.makeText(GaesipanWritingscreen.this, "파일 업로드 완료", Toast.LENGTH_SHORT).show();
                    }))
                    .addOnFailureListener(e -> Toast.makeText(GaesipanWritingscreen.this, "파일 업로드 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    private void appendFileUrlToContent(String fileUrl) {
        String currentContent = contentEditText.getText().toString();
        String updatedContent = currentContent + "\n\n첨부 파일: " + fileUrl;
        contentEditText.setText(updatedContent);
    }
}
