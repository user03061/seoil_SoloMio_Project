package DBsetting;

import DB.items;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.commuuu.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ItemSettingActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_item);

        // Initialize Firebase
        mDatabase = FirebaseDatabase.getInstance().getReference();

        Button addItemButton = findViewById(R.id.plusItem);
        addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addGalaxyS22DIYCase();
            }
        });
    }

    private void addGalaxyS22DIYCase() {
        items galaxyS22Case = new items("GalS22_DIY", "갤럭시 S22 DIY 케이스", 1, 15000);

        mDatabase.child("items").child(galaxyS22Case.itemId).setValue(galaxyS22Case)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ItemSettingActivity.this, "갤럭시 S22 DIY 케이스가 추가되었습니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ItemSettingActivity.this, "아이템 추가 실패: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}