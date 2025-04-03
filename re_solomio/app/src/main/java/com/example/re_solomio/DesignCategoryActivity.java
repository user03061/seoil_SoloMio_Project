package com.example.re_solomio;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DesignCategoryActivity extends AppCompatActivity {

    private LinearLayout categoryMenu;
    private LinearLayout subCategoryMenu;
    private LinearLayout modelMenu;
    private Button btnNext;
    private String selectedModel;
    private Button selectedCategoryButton;
    private Button selectedSubCategoryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.design_category_activity);

        categoryMenu = findViewById(R.id.category_menu);
        subCategoryMenu = findViewById(R.id.sub_category_menu);
        modelMenu = findViewById(R.id.model_menu);
        btnNext = findViewById(R.id.btn_next);

        Map<String, List<String>> smartphoneData = new HashMap<>();
        smartphoneData.put("안드로이드", Arrays.asList("갤럭시 S20", "갤럭시Z 폴드6"));
        smartphoneData.put("아이폰", Arrays.asList("아이폰 13", "아이폰 12 ProMax"));

        Map<String, List<String>> earphoneData = new HashMap<>();
        earphoneData.put("에어팟", Arrays.asList("에어팟 3세대", "에어팟 프로"));
        earphoneData.put("버즈", Arrays.asList("버즈 라이브", "버즈 프로"));

        Map<String, List<String>> gripTokData = new HashMap<>();
        gripTokData.put("그립톡", Arrays.asList("기본형", "슬림형"));

        View.OnClickListener categoryClickListener = v -> {
            Button button = (Button) v;
            String category = button.getText().toString();
            showSubCategories(category, smartphoneData, earphoneData, gripTokData);
            if (selectedCategoryButton != null) {
                selectedCategoryButton.setBackgroundTintList(getResources().getColorStateList(R.color.button_normal, null));
            }
            button.setBackgroundTintList(getResources().getColorStateList(R.color.button_selected, null));
            selectedCategoryButton = button;

            selectedModel = null;
            btnNext.setEnabled(false);
        };

        for (int i = 0; i < categoryMenu.getChildCount(); i++) {
            View view = categoryMenu.getChildAt(i);
            if (view instanceof Button) {
                view.setOnClickListener(categoryClickListener);
            }
        }

        btnNext.setOnClickListener(v -> {
            if (selectedModel != null) {
                Intent intent = new Intent(DesignCategoryActivity.this, DesignActivity.class);
                intent.putExtra("selectedModel", selectedModel);
                startActivity(intent);
            } else {
                Toast.makeText(DesignCategoryActivity.this, "모델을 선택하세요", Toast.LENGTH_SHORT).show();
            }
        });

        Button btnCancel = findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(v -> {
            Intent intent = new Intent(DesignCategoryActivity.this, MainHomeActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void showSubCategories(String category, Map<String, List<String>> smartphoneData,
                                   Map<String, List<String>> earphoneData, Map<String, List<String>> gripTokData) {
        subCategoryMenu.removeAllViews();
        modelMenu.removeAllViews();


        final Map<String, List<String>> data;
        switch (category) {
            case "스마트폰":
                data = smartphoneData;
                break;
            case "무선 이어폰":
                data = earphoneData;
                break;
            case "그립톡":
                data = gripTokData;
                break;
            default:
                data = null;
                break;
        }

        if (data != null) {
            for (String subCategory : data.keySet()) {
                Button subCategoryButton = new Button(this);
                subCategoryButton.setText(subCategory);
                subCategoryButton.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                subCategoryButton.setBackgroundTintList(getResources().getColorStateList(R.color.button_normal, null));
                subCategoryButton.setTextColor(getResources().getColor(android.R.color.white));
                subCategoryButton.setOnClickListener(v -> {
                    List<String> models = data.get(subCategory);
                    selectedModel = null;

                    if (models != null) {
                        showModels(models);
                    }
                    if (selectedSubCategoryButton != null) {
                        selectedSubCategoryButton.setBackgroundTintList(getResources().getColorStateList(R.color.button_normal, null));
                    }
                    subCategoryButton.setBackgroundTintList(getResources().getColorStateList(R.color.button_selected, null));
                    selectedSubCategoryButton = subCategoryButton;
                });
                subCategoryMenu.addView(subCategoryButton);
            }
        }
    }

    private void showModels (List<String> models) {
        modelMenu.removeAllViews();
        for (String model : models) {
            Button modelButton = new Button(this);
            modelButton.setText(model);
            modelButton.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            modelButton.setBackgroundTintList(getResources().getColorStateList(R.color.button_normal, null));
            modelButton.setTextColor(getResources().getColor(android.R.color.white));
            modelButton.setOnClickListener(v -> {
                selectedModel = model;
                for (int i = 0; i < modelMenu.getChildCount(); i++) {
                    modelMenu.getChildAt(i).setBackgroundTintList(getResources().getColorStateList(R.color.button_normal, null));
                }
                modelButton.setBackgroundTintList(getResources().getColorStateList(R.color.button_selected, null));
                btnNext.setEnabled(true);
            });
            modelMenu.addView(modelButton);
        }
    }
}