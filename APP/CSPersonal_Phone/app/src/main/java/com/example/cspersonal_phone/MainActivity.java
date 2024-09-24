package com.example.cspersonal_phone;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button1 = findViewById(R.id.button);
        Button button2 = findViewById(R.id.button2);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

   /* public void onButtonClicked(View v) {
        Intent intent_translate = new Intent(MainActivity.this, CameraActivity.class);
        startActivity(intent_translate);
    }
    // GET STARTED Button 의 onClick 이벤트이고 Intent 객체 를 사용하여 데이터를 넘겨 화면을 전환함*/
}