package com.example.cspersonal_phone;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

// import com.example.cspersonal_phone.BuildConfig;

public class CameraActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST_CODE = 100;  // 권환 요청 시 사용할 식별자 상수
    public static String imageFilePath;           // 이미지 파일이 저장될 경로
    private File Capture_file;              // 파일 객체
    private Uri photoURI;                   // 사진의 uri 사용 할 변수

    // boolean flag = false;        // 사진 촬영 이후의 확인 버튼 생성을 위한 flag

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);                            // setContentView() activity_camera 를 띄움

        Button capture_button = findViewById(R.id.button1);                  // findViewById() 로 버튼 객체 (사진 촬영 버튼 객체) 알맞은 위젯 지정
        capture_button.setOnClickListener(new View.OnClickListener() {       // onClick() 지정
            @Override
            public void onClick(View v) {
                try {
                    Capture_file = createImageFile();                                           // createImageFile()를 사용해 file 생성 날짜와 시간, 이름 등의 내용으로 이미지 파일 생성
                    Intent intent_picture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);        // 사진 촬영의 intent 객체 생성
                    if (intent_picture.resolveActivity(getPackageManager()) != null) {          // 핸드폰 기기에 사진 촬영 기능이 있을 경우
                        if (ContextCompat.checkSelfPermission(CameraActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {  // 카메라 권환이 없는 경우
                            ActivityCompat.requestPermissions(CameraActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_REQUEST_CODE); // 사용자에게 권환을 요청
                        } else {
                            photoURI = FileProvider.getUriForFile(CameraActivity.this, "com.example.cspersonal_phone", Capture_file);
                            intent_picture.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                            startActivityResult.launch(intent_picture);                         // 찍고 사진의 내용을 화면에 띄우기 위해 돌아오는 startActivityResult()사용
                            // file.createNewFile();                                            // 찍은 file의 내용으로 createNewFile() 사용
                            ImageView imageView = findViewById(R.id.imageView1);                // 찍은 사진 띄울 ImageView 객체

                            if (imageView.getDrawable() != null) {                              // ImageView가 찍은 사진으로 채워져 있으면 확인 버튼 생성
                                addConfirmButton(capture_button, imageView, imageFilePath, Capture_file);   // 확인 버튼 생성 함수
                            }
                        }
                    }

                } catch (Exception e) {
                    Toast.makeText(CameraActivity.this, "사진 촬영 준비 중 오류 발생", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
    }

    private void addConfirmButton(Button capture_button, ImageView imageView, String imageFilePath, File file) {
        Intent intent = new Intent(getApplicationContext(), LoadingActivity.class);
        LinearLayout layout = findViewById(R.id.layout_camera);                                    // 레이아웃 객체 생성
        Button confirmButton = new Button(this);                                            // 추가할 확인 버튼 객체 생성
        confirmButton.setLayoutParams(new LinearLayout.LayoutParams(                               // 확인 버튼의 가로세로 크기 WRAP_CONTENT로 설정
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        confirmButton.setText("확인");
        confirmButton.setLayoutParams(capture_button.getLayoutParams());

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // File storageDir = new File(getFilesDir(), "Capture");
                /* if (!storageDir.exists()) {
                    storageDir.mkdirs();
                } */
                // 파일 경로와 이름을 적절히 설정
                // File file = new File(storageDir, "image_" + System.currentTimeMillis() + ".jpg");
                try (FileOutputStream output = new FileOutputStream(file)) {
                    // ImageView imageView = findViewById(R.id.imageView1);                        // 이미지 뷰에서 비트맵 가져오기
                    BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();            // 비트맵을 얻기 위한 함수 사용
                    if (drawable != null) {                                                        // drawable이 null이 아닌지 확인
                        Bitmap bitmap = drawable.getBitmap();                                      // PNG로 저장하기 위해 비트맵에 접근하기 위한 객체
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);            // PNG 형식으로 저장
                        Toast.makeText(getApplicationContext(), "이미지가 저장되었습니다: " + imageFilePath, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "이미지가 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "이미지 저장 실패: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
                startActivity(intent);
                finish();
            }
        });
        layout.addView(confirmButton); // 레이아웃에 버튼 추가
    }


    // 촬영한 사진을 가지고 provider 해서 내부로 이동할 임시 사진파일 생성 함수
    private File createImageFile() throws IOException {
        // String TempFileName = new SimpleDateFormat("yyyyMMdd_HHMMSS").format(new Date());   // 현재 날짜와 시간을 형식에 맞게 저장
        // File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);              // 외부 저장소의 사진 디렉토리 내용 가져옴
        File storageDir = new File(getFilesDir(), "Capture");
        // File image = File.createTempFile(TempFileName, ".jpg", storageDir);                // 찍은 사진을 임시로 저장할 객체 생성
        File image = new File(storageDir, "image_" + System.currentTimeMillis() + ".jpg");
        imageFilePath = image.getAbsolutePath();                                                // image 사진 파일의 경로를 저장
        return image;                                                                           // 각각의 정보를 가진 image 반환
    }

    // startActivityResult를 생성해 사용함
    ActivityResultLauncher<Intent> startActivityResult = registerForActivityResult(             // ActivityResultLauncher를 생성해 인텐트 실행
            new ActivityResultContracts.StartActivityForResult(),                               // 일반적인 인텐트 매개변수
            new ActivityResultCallback<ActivityResult>() {                                      // 결과 처리 콜백을 정의함 매개변수
                @Override
                public void onActivityResult(ActivityResult result) {

                    ImageView imageView;                                                        // 이미지 뷰 객체 (촬영한 사진 이미지 뷰 객체)
                    imageView = findViewById(R.id.imageView1);                                  // 알맞은 위젯 지정

                    if (result.getResultCode() == Activity.RESULT_OK) {                         // 사진촬영 인텐트가 성공적일 시 실행
                        Bitmap bitmap = BitmapFactory.decodeFile(imageFilePath);                // image 사진파일 경로인 imageFilePath를 디코딩해서 bitmap으로 표현
                        ExifInterface exif = null;                                              // exif 데이터를 위한 객체 초기화
                        try {
                            exif = new ExifInterface(imageFilePath);                            // 촬영한 이미지에서 exif(이미지포멧형식) 데이터 가져와 객체 생성
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        int exifOrientation;     // 방향 변수
                        int exifDegree;          // 회전 각도 변수

                        if (exif != null) {
                            exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                            exifDegree = exifOrientationToDegress(exifOrientation);
                        } else {
                            exifDegree = 0;
                        }
                        imageView.setImageBitmap(rotate(bitmap, exifDegree));
                    }
                }
            });

    private int exifOrientationToDegress(int exifOrientation) {                                 // 카메라에서 사진을 찍었을 때 화면을 돌리는 것에 대한 이미지 화전 함수
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if ((exifOrientation == ExifInterface.ORIENTATION_ROTATE_270)) {
            return 270;
        }
        return 0;
    }

    private Bitmap rotate(Bitmap bitmap, int exifDegree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(exifDegree);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
}