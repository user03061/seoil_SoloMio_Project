package com.example.re_solomio;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SoloMioLoginActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private EditText textBox1; // 아이디 입력
    private EditText textBox2; // 비밀번호 입력
    private Button loginButton; // 로그인 버튼
    private boolean UserLoginOn = false; // 로그인 상태 변수 true면 로그인 유지 false면 로그아웃

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);// 레이아웃 파일 지정

        mDatabase = FirebaseDatabase.getInstance().getReference();


        textBox1 = findViewById(R.id.Username_Text);
        textBox2 = findViewById(R.id.Password_Text);
        loginButton = findViewById(R.id.btn_Login);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readLoginData();
            }
        });
    }

    private void readLoginData() {
        String inputUsername = textBox1.getText().toString().trim();
        String inputPassword = textBox2.getText().toString().trim();

        mDatabase.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean userFound = false;

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String storedNickname = userSnapshot.child("nickname").getValue(String.class);
                    String storedPassword = userSnapshot.child("passWord").getValue(String.class);

                    if (storedNickname != null && storedNickname.equals(inputUsername) && storedPassword != null && storedPassword.equals(inputPassword)) {

                        Toast.makeText(SoloMioLoginActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();
                        UserLoginOn = true;
                        userFound = true;

                        Intent intent = new Intent(SoloMioLoginActivity.this, MainHomeActivity.class);
                        startActivity(intent);

                        break;
                    }
                }

                if (!userFound) {

                    Toast.makeText(SoloMioLoginActivity.this, "존재하지 않거나 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(SoloMioLoginActivity.this, "데이터베이스 오류: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void Logincompleted() {
        Intent intent = new Intent(SoloMioLoginActivity.this, MainHomeActivity.class);

        startActivity(intent);
        finish();
    }
}
