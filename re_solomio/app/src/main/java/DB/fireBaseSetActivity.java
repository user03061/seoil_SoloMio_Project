package DB;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.re_solomio.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class fireBaseSetActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference gesipanRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.community_article_list);

        // 예제: 데이터 수정
        String gesipanId = "1"; // 수정할 Gesipan 객체의 ID
        String photoUrl = "https://firebasestorage.googleapis.com/v0/b/solomio-52aa1.appspot.com" +
                "/o/testimg1.png?alt=media&token=97b2c3da-7564-4a59-a159-e1977d20aeb1";

        // 데이터 수정
        updateGesipanData(gesipanId, "이승주", "나 투썸왔는데 졸작하는게 너무 재미있다", "1");
    }

    public void updateGesipanData(String id, String newNickName, String newTitle, String photoUrl) {
        // FirebaseDatabase 인스턴스 가져오기
        database = FirebaseDatabase.getInstance();

        // 특정 Gesipan 데이터 참조하기
        gesipanRef = database.getReference("gesipans").child(id);

        // 수정할 데이터
        Map<String, Object> updates = new HashMap<>();
        updates.put("nickName", newNickName); // 닉네임 수정
        updates.put("title", newTitle); // 제목 수정
        updates.put("photoUrl", photoUrl); // 이미지 URL 수정

        // 데이터 수정
        gesipanRef.updateChildren(updates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // 수정 성공
                        System.out.println("데이터가 성공적으로 수정되었습니다.");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // 수정 실패
                        e.printStackTrace();
                    }
                });
    }
}
