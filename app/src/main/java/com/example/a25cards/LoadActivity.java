package com.example.a25cards;

import android.app.Activity;

import android.os.Bundle;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.WindowManager;
import android.content.pm.ActivityInfo;
import android.widget.ProgressBar;
import android.widget.TextView;

public class LoadActivity extends Activity {

    //进度条
    private ProgressBar progressBar;
    //进度值
    private int p = 0;
    //进度条进度描述
    private TextView progressBar_desc;
    private MyHandler myHandler = new MyHandler();

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 横屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.load);

        progressBar = findViewById(R.id.progressBar);
        progressBar_desc = findViewById(R.id.progressBar_desc);
        Intent mainIntent = new Intent(LoadActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
       // new myThread().start();
    }

    public class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int code=msg.what;//接受处理码
            switch (code){
                case 1:
                    p++;
                    progressBar.setProgress(p);//给进度条的当前进度赋值
                    if(p>=100){
                        p =100;
                    }
                    progressBar_desc.setText(p+"%");//显示当前进度为多少
                    break;
                case 2:
                    Intent mainIntent = new Intent(LoadActivity.this, MainActivity.class);
                    startActivity(mainIntent);
                    finish();
                    break;
            }
        }
    }

    public class myThread extends Thread{
        @Override
        public void run() {
            while(p<=100){
                try {
                    Thread.sleep(100);//使线程休眠0.05秒
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Message msg = new Message();
                if(p<100){
                    msg.what = 1;
                } else{
                    msg.what = 2;
                }
                myHandler.sendMessage(msg);
            }
        }

    }
}