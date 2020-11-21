package com.example.a25cards;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.pm.ActivityInfo;


import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button login;
    private Button register;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        // 全屏、隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 横屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_main);
        login = (Button)findViewById(R.id.login);
        register = (Button)findViewById(R.id.register);
        Resources resources = this.getResources();;
        DisplayMetrics dm  = resources.getDisplayMetrics();
        float density = dm.density;
        float screenWidth = dm.widthPixels;
        float screenHeight = dm.heightPixels;
        //分别设置登录和注册按钮的位置，通过获取到的设置的长款来动态设置按钮的位置
        register.setX((float) 0.07*screenWidth);
        register.setY((float)0.73*screenHeight);
        login.setX((float)0.54*screenWidth);
        login.setY((float)0.73*screenHeight);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

}