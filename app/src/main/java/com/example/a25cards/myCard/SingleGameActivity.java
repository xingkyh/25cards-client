package com.example.a25cards.myCard;

import android.R;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class SingleGameActivity extends Activity {
	MyView myView;
	String messString;
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if(msg.what==0){
				messString=msg.getData().getString("data");
				showDialog();
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFormat(PixelFormat.RGBA_8888);
		// 隐藏标题栏
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 隐藏状态栏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// 锁定横屏
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		myView = new MyView(this,handler);
		setContentView(myView);
	}
	public void showDialog(){
		new AlertDialog.Builder(this).setMessage(messString)
		.setPositiveButton("重新开始游戏", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				reGame();
			}
		}).setTitle("提示").create().show();
	}
	//重新开始游戏
	public void reGame(){
		myView = new MyView(this,handler);
		setContentView(myView);
	}
//	@Override
//	public void onBackPressed() {
//		// TODO Auto-generated method stub
//		super.onBackPressed();
//		new  AlertDialog.Builder(MyActivity.this)   
//		.setTitle("确认" )  
//		.setMessage("确定吗？" )  
//		.setPositiveButton("是" ,  null )  
//		.setNegativeButton("否" , null)  
//		.show();  
//	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode == KeyEvent.KEYCODE_BACK){
	        new AlertDialog.Builder(this) 
	        .setTitle("提示") 
	        .setMessage("是否退出？") 
	        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() { 
	            @Override 
	            public void onClick(DialogInterface dialog, int which) { 
	            } 
	        }) 
	        .setPositiveButton("退出", new DialogInterface.OnClickListener(){ 
	            public void onClick(DialogInterface dialog, int whichButton) { 
	                finish(); 
	            } 
		        }).show(); 
  
	        return true; 
		}else{ 
			
		} 
		return super.onKeyDown(keyCode, event);
	}

}
