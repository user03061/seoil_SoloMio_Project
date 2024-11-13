package com.example.re_solomio;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

public class MembershipActivity extends AppCompatActivity {

    ViewModelActivity userViewModel;
    TextView email_text, nick_text;
    Button save_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.setting_membership_activity);

        userViewModel = new ViewModelProvider(this).get(ViewModelActivity.class);

        email_text = findViewById(R.id.ms_email_text);
        nick_text = findViewById(R.id.ms_nick_text);
        save_btn = findViewById(R.id.ms_save_btn);

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newNick = nick_text.getText().toString();
                String newEmail = email_text.getText().toString();

                userViewModel.setNick(newNick);
                userViewModel.setEmail(newEmail);

                Toast.makeText(MembershipActivity.this, "회원 정보가 저장되었습니다.", Toast.LENGTH_LONG).show();

                getSupportFragmentManager().popBackStack();

                SharedPreferences sharedPreferences = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("nick", newNick);
                editor.putString("email", newEmail);
                editor.apply();

                setResult(RESULT_OK);
                finish();
            }
        });
    }
}
