package com.example.re_solomio;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import Adapter.ZzimAdapter;
import Adapter.ZzimItem;
import DB.Gesipan;

public class MainZzimFragment extends Fragment {

    private FirebaseFirestore db;
    private List<ZzimItem> zzimItems;
    private ZzimAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_zzim_fragment, container, false);

        GridView zzimGridView = view.findViewById(R.id.zzim_gridview);
        zzimItems = new ArrayList<>();
        adapter = new ZzimAdapter(getContext(), zzimItems);
        zzimGridView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        // zzim 값이 true인 게시글 가져오기
        fetchZzimPosts();

        // 게시글 클릭 시 이동
        zzimGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ZzimItem item = zzimItems.get(position);
                Intent intent = new Intent(getContext(), PostDetailActivity.class);
                intent.putExtra("PostId", item.getId());  // getId() 메서드 사용
                startActivity(intent);
            }
        });

        return view;
    }

    private void fetchZzimPosts() {
        CollectionReference gesipansRef = db.collection("gesipans");
        gesipansRef.whereEqualTo("zzim", true)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    zzimItems.clear();
                    if (queryDocumentSnapshots.isEmpty()) {
                        // 데이터가 없는 경우 처리
                        // 예를 들어, 사용자에게 메시지를 보여줄 수 있음
                    } else {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Gesipan post = document.toObject(Gesipan.class);
                            if (post != null) {
                                zzimItems.add(new ZzimItem(post.getPhotoUrl(), post.getTitle(), document.getId()));
                            }
                        }
                    }
                    adapter.notifyDataSetChanged();  // 데이터 변경 시 어댑터 갱신
                })
                .addOnFailureListener(e -> {
                    // 에러 처리 (예: 사용자에게 오류 메시지 표시)
                });
    }

}
