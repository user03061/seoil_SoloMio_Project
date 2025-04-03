package com.example.re_solomio;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class DesignActivity extends AppCompatActivity {

    private ImageView ivPhone, ivOverlay;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private ScaleGestureDetector scaleGestureDetector;
    private float scaleFactor = 1f;
    private float dX, dY;
    private boolean isScaling = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.design_activity);

        ivPhone = findViewById(R.id.iv_phone);
        ivOverlay = findViewById(R.id.iv_overlay);

        String selectedModel = getIntent().getStringExtra("selectedModel");
        if (selectedModel != null) {
            switch (selectedModel) {
                case "갤럭시 S20":
                    ivPhone.setImageResource(R.drawable.galaxy_s20_image);
                    break;
                case "아이폰 13":
                    ivPhone.setImageResource(R.drawable.iphone_13_image);
                    break;
                case "아이폰 12 ProMax":
                    ivPhone.setImageResource(R.drawable.iphone_12_promax_image);
                    break;
            }
        }

        ivPhone.setOnTouchListener((v, event) -> true);

        // 갤러리에서 이미지 선택 처리 ActivityResultLauncher
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImage = result.getData().getData();
                        ivOverlay.setImageURI(selectedImage); // 선택한 이미지를 이미지뷰에 적용
                        ivOverlay.setVisibility(View.VISIBLE);
                    }
                }
        );

        // 내 이미지 버튼 클릭 (갤러리 호출)
        Button btnMyImage = findViewById(R.id.btn_my_image);
        btnMyImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });

        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleTouchListener(ivOverlay));
        ivOverlay.setOnTouchListener((v, event) -> {
            if (event.getPointerCount() == 1 && !isScaling) {  // 한 손가락일 때만 이동
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        dX = v.getX() - event.getRawX();
                        dY = v.getY() - event.getRawY();
                        break;

                    case MotionEvent.ACTION_MOVE:
                        v.setX(event.getRawX() + dX);
                        v.setY(event.getRawY() + dY);
                        break;

                    case MotionEvent.ACTION_UP:
                        v.performClick();
                        break;
                    default:
                        return false;
                }
            }
            return scaleGestureDetector.onTouchEvent(event);
        });

        Button btnDecorate = findViewById(R.id.btn_decorate);
        btnDecorate.setOnClickListener(v -> showStickerDialog());

        Button btnPrevious = findViewById(R.id.btn_prev); // '이전' 버튼의 ID
        btnPrevious.setOnClickListener(v -> {
            // '이전' 버튼
            finish();
        });

        Button btnSaveModel = findViewById(R.id.btn_savemodel); // '이전' 버튼의 ID
        btnSaveModel.setOnClickListener(v -> {
            // 저장을 위한 AlertDialog
            AlertDialog.Builder builder = new AlertDialog.Builder(DesignActivity.this);
            View dialogView = getLayoutInflater().inflate(R.layout.design_name_input, null);
            builder.setView(dialogView);

            EditText editDesignName = dialogView.findViewById(R.id.edit_design_name);

            builder.setPositiveButton("저장", (dialog, which) -> {
                String designName = editDesignName.getText().toString().trim();
                if (!designName.isEmpty()) {
                    // 디자인 캡처
                    View designLayout = findViewById(R.id.design_palette);
                    Bitmap capturedBitmap = captureLayoutAsBitmap(designLayout);

                    // 파일명으로 저장
                    saveBitmapAsImage(capturedBitmap, designName);

                    // MainHomeActivity로 이동
                    Intent intent = new Intent(DesignActivity.this, MainHomeActivity.class);
                    startActivity(intent);
                    finish();  // 현재 액티비티 종료
                } else {
                    Toast.makeText(DesignActivity.this, "디자인 이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
            });

            builder.setNegativeButton("취소", (dialog, which) -> dialog.dismiss());

            AlertDialog dialog = builder.create();
            dialog.show();
        });
    }


    private void showStickerDialog() {
        // 다이얼로그 뷰를 인플레이트합니다.
        View stickerView = getLayoutInflater().inflate(R.layout.sticker_dialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(stickerView);

        AlertDialog dialog = builder.create();
        dialog.show();

        // 스티커를 선택하는 버튼을 만듭니다.
        ImageView sticker1 = stickerView.findViewById(R.id.sticker1); // sticker_dialog.xml에 있는 이미지뷰 ID
        ImageView sticker2 = stickerView.findViewById(R.id.sticker2);
        ImageView sticker3 = stickerView.findViewById(R.id.sticker3);
        ImageView sticker4 = stickerView.findViewById(R.id.sticker4);
        ImageView sticker5 = stickerView.findViewById(R.id.sticker5);
        ImageView sticker6 = stickerView.findViewById(R.id.sticker6);

        sticker1.setOnClickListener(v -> {
            addStickerToLayout(R.drawable.sticker_hellokitty); // 선택된 스티커를 레이아웃에 추가
            dialog.dismiss(); // 다이얼로그 닫기
        });

        sticker2.setOnClickListener(v -> {
            addStickerToLayout(R.drawable.sticker_hangyodon);
            dialog.dismiss();
        });

        sticker3.setOnClickListener(v -> {
            addStickerToLayout(R.drawable.sticker_chiikawa);
            dialog.dismiss();
        });

        sticker4.setOnClickListener(v -> {
            addStickerToLayout(R.drawable.sticker_manggom);
            dialog.dismiss();
        });

        sticker5.setOnClickListener(v -> {
            addStickerToLayout(R.drawable.sticker_panzzuusagi);
            dialog.dismiss();
        });

        sticker6.setOnClickListener(v -> {
            addStickerToLayout(R.drawable.sticker_pompompurin);
            dialog.dismiss();
        });
    }

    private void addStickerToLayout(int stickerResId) {
        ImageView stickerView = new ImageView(this);
        stickerView.setImageResource(stickerResId);

        // 스티커 크기 및 레이아웃 설정
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        stickerView.setLayoutParams(params);

        // 각 스티커에 대해 ScaleGestureDetector를 생성
        ScaleGestureDetector stickerScaleDetector = new ScaleGestureDetector(this, new ScaleTouchListener(stickerView));

        // 스티커 터치 리스너 설정 (드래그 및 크기 조절)
        stickerView.setOnTouchListener(new View.OnTouchListener() {
            private float dX, dY;
            private boolean isDragging = false;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // 크기 조절 이벤트 처리
                stickerScaleDetector.onTouchEvent(event);

                // 드래그 동작 처리
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        dX = v.getX() - event.getRawX();
                        dY = v.getY() - event.getRawY();
                        isDragging = false;
                        break;

                    case MotionEvent.ACTION_MOVE:
                        // 만약 두 손가락이 아닌 경우에만 드래그를 허용합니다.
                        if (event.getPointerCount() == 1) {
                            v.setX(event.getRawX() + dX);
                            v.setY(event.getRawY() + dY);
                            isDragging = true;
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                        if (!isDragging) {
                            v.performClick();
                        }
                        break;

                    default:
                        return false;
                }
                return true;
            }
        });

        // 디자인 레이아웃에 스티커 추가
        ((ViewGroup) findViewById(R.id.design_palette)).addView(stickerView);
    }

    private class StickerTouchListener implements View.OnTouchListener {
        private float dX, dY;

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    dX = view.getX() - event.getRawX();
                    dY = view.getY() - event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    view.animate()
                            .x(event.getRawX() + dX)
                            .y(event.getRawY() + dY)
                            .setDuration(0)
                            .start();
                    break;
                default:
                    return false;
            }
            return true;
        }
    }

    // 크기 조절 리스너
    private class ScaleTouchListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        private final View targetView;

        public ScaleTouchListener(View view) {
            this.targetView = view;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scaleFactor = detector.getScaleFactor();

            if (targetView != null) {
                float newScaleX = targetView.getScaleX() * scaleFactor;
                float newScaleY = targetView.getScaleY() * scaleFactor;

                // 크기 제한 설정
                newScaleX = Math.max(0.2f, Math.min(newScaleX, 5.0f));
                newScaleY = Math.max(0.2f, Math.min(newScaleY, 5.0f));

                targetView.setScaleX(newScaleX);
                targetView.setScaleY(newScaleY);
            }
            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            isScaling = true;
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            isScaling = false;
        }
    }

    private Bitmap captureLayoutAsBitmap(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    private void saveBitmapAsImage(Bitmap bitmap, String designName) {
        try {
            File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), designName + ".png");
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            Toast.makeText(this, "디자인이 저장되었습니다.", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "저장 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
        }
    }
}