package com.example.re_solomio;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

public class MainMyPageFragment extends Fragment {

    private ViewModelActivity userViewModel;
    private Button coupon_btn, review_btn, setting_btn, mydesign_btn;
    private TextView email_text, nick_text;
    private ActivityResultLauncher<Intent> settingActivityLauncher;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_mypage_fragment, container, false);

        coupon_btn = view.findViewById(R.id.mypage_couponbtn);
        review_btn = view.findViewById(R.id.mypage_reviewbtn);
        setting_btn = view.findViewById(R.id.mypage_settingbtn);
        mydesign_btn = view.findViewById(R.id.mypage_mydesignbtn);

        email_text = view.findViewById(R.id.mypage_email_text);
        nick_text = view.findViewById(R.id.mypage_id_text);

        userViewModel = new ViewModelProvider(requireActivity()).get(ViewModelActivity.class);

        userViewModel.getNick().observe(getViewLifecycleOwner(), nick -> nick_text.setText(nick));

        userViewModel.getEmail().observe(getViewLifecycleOwner(), email -> email_text.setText(email));


        settingActivityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        SharedPreferences updatedPrefs = getActivity().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
                        String updatedNick = updatedPrefs.getString("nick", "기본 닉네임");
                        String updatedEmail = updatedPrefs.getString("email", "기본 이메일");

                        userViewModel.setNick(updatedNick);
                        userViewModel.setEmail(updatedEmail);
                    }
                }
        );


        coupon_btn.setOnClickListener(v -> startActivity(new Intent(getActivity(), CouponActivity.class)));

        review_btn.setOnClickListener(v -> startActivity(new Intent(getActivity(), MypageReviewActivity.class)));

        setting_btn.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SettingActivity.class);
            settingActivityLauncher.launch(intent);
        });

        mydesign_btn.setOnClickListener(v -> startActivity(new Intent(getActivity(), MydesignActivity.class)));

            return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        String nick = sharedPreferences.getString("nick", "기본 닉네임");
        String email = sharedPreferences.getString("email", "기본 이메일");

        nick_text.setText(nick);
        email_text.setText(email);
    }
}