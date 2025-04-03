package com.example.re_solomio;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class CouponDlFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.coupon_dl_fragment, container, false);

        ImageButton save1, save2;

//쿠폰 저장 후 버튼 클릭 시 메시지 출력 후 버튼 비활성화
        save1 = view.findViewById(R.id.dc_save1);
        save1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "쿠폰이 저장되었습니다.", Toast.LENGTH_SHORT).show();
                save1.setEnabled(false);
            }
        });

        save2 = view.findViewById(R.id.dc_save2);
        save2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "쿠폰이 저장되었습니다.", Toast.LENGTH_SHORT).show();
                save2.setEnabled(false);
            }
        });return view;
    }
}
