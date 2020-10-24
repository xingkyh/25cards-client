package com.example.a25cards;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.pm.ActivityInfo;


import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;


public class MenuActivity extends AppCompatActivity {

    private Button out;
    private Button classical;
    private Button competition;
    private Button rank;
    private Button ending;
    private ImageView imageView;
    private String username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        // 全屏、隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 横屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
          setContentView(R.layout.menu);
        out = (Button)findViewById(R.id.out);
        out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, MainActivity.class);
                startActivity(intent);
                MenuActivity.this.finish();
            }
        });
        classical = (Button)findViewById(R.id.classical);
        competition = (Button)findViewById(R.id.competition);
        rank = (Button)findViewById(R.id.rank);
        ending = (Button)findViewById(R.id.ending);
        imageView = (ImageView)findViewById(R.id.player);

        Resources resources = this.getResources();;
        DisplayMetrics dm  = resources.getDisplayMetrics();
        float density = dm.density;
        float screenWidth = dm.widthPixels;
        float screenHeight = dm.heightPixels;
        imageView.setX(0);
        imageView.setY((float)0.2*screenHeight);

        classical.setX((float)(0.7*screenWidth-130*3));
        classical.setY((float)0.4*screenHeight-100*3);
        competition.setX((float)0.7*screenWidth);
        competition.setY((float)0.4*screenHeight-100*3);
        rank.setX((float)0.7*screenWidth-130*3);
        rank.setY((float)0.4*screenHeight);
        ending.setX((float)0.7*screenWidth);
        ending.setY((float)0.4*screenHeight);

        Intent intent = getIntent();
        username = intent.getStringExtra("name");
        classical.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, GameActivity.class);
                intent.putExtra("name",username);
                startActivity(intent);
            }
        });
    }
}