package com.example.re_solomio;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

public class CouponMyFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.coupon_my_fragment, container, false);

        Button register_btn;

        //쿠폰 등록하기 버튼 클릭 시 입력 받기 팝업 창
        register_btn = view.findViewById(R.id.mc_registerbtn);
        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder ab = new AlertDialog.Builder(getActivity());
                ab.setTitle("쿠폰 등록하기");
                ab.setMessage("쿠폰 코드를 입력해주세요.");
                final EditText editText = new EditText(getActivity());
                ab.setView(editText);

                ab.setPositiveButton("쿠폰 등록하기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String code = editText.getText().toString();
                        dialog.dismiss();
                    }

                });
                ab.show();
            }
        });
        return view;
    }
}