package com.leaf.godproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {
//    ImageView Welcomeimg;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_welcome);
//        Welcomeimg=(ImageView)findViewById(R.id.welcomeimg);
//        指定歡迎畫面圖片
//        Welcomeimg.setImageResource(R.drawable.weldog);
        mHandler.sendEmptyMessageDelayed(GOTO_MAIN_ACTIVITY, 500); //0.5秒跳轉
    }
    private static final int GOTO_MAIN_ACTIVITY = 0;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == GOTO_MAIN_ACTIVITY) {
                Intent intent = new Intent();
                intent.setClass(WelcomeActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }
    };
}
