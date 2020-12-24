package com.example.a25cards.myCard;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.a25cards.R;

public class FirstView extends SurfaceView implements SurfaceHolder.Callback{
	SurfaceHolder surfaceHolder;
	private MyThread myThread; 
	Canvas canvas=null;
	Bitmap menuBitmap;    //背面
	int screen_height=getHeight();
	int screen_width=getWidth();
	public FirstView(Context context) {
		super(context);
		surfaceHolder = this.getHolder();
		surfaceHolder.addCallback(this);
		myThread = new MyThread(surfaceHolder);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		myThread.start();
//		canvas=surfaceHolder.lockCanvas();
//		drawBackground(canvas);
		Log.i("zxzxzx", "调用画图完毕");
	}
//	public void drawBackground() {
//		Log.i("zxzxzx", canvas.toString());
//		menuBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.a1_10).copy(Bitmap.Config.ARGB_8888, true);
//		Log.i("zxzxzx", menuBitmap.getHeight()+"gaodu");
//		Rect src = new Rect(0, 0, menuBitmap.getWidth(),
//				menuBitmap.getHeight());
//		Log.i("zxzxzx","menuBitmap.getWidth()*3/4="+menuBitmap.getWidth()*1/4+"menuBitmap.getHeight()*2/3="+menuBitmap.getHeight()*1/4);
////		System.out.println("menuBitmap.getWidth()*3/4"+menuBitmap.getWidth()*3/4+"menuBitmap.getHeight()*2/3"+menuBitmap.getHeight()*2/3);
//		Rect dst = new Rect(0, 0, screen_width, screen_height);
//		canvas.drawBitmap(menuBitmap, src, dst, null);
////		canvas.drawBitmap(menuBitmap, menuBitmap.getWidth(), menuBitmap.getHeight(), null);
//		Log.i("zxzxzx", "画图完毕");
//}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
	}

    class MyThread extends Thread
    {
        private SurfaceHolder holder;
        public  MyThread(SurfaceHolder holder)
        {
            this.holder =holder; 
        }
        @Override
        public void run()
        {
        	Canvas canvas=holder.lockCanvas();
        	Log.i("zxzxzx","holder"+holder.toString());
        	Log.i("zxzxzx","canvas"+canvas.toString());
    		menuBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.a1_10).copy(Bitmap.Config.ARGB_8888, true);
    		Log.i("zxzxzx", menuBitmap.getHeight()+"gaodu");
    		Rect src = new Rect(0, 0, menuBitmap.getWidth(),menuBitmap.getHeight());
    		Log.i("zxzxzx","menuBitmap.getWidth()*3/4="+menuBitmap.getWidth()*1/4+"menuBitmap.getHeight()*2/3="+menuBitmap.getHeight()*1/4);    		Rect dst = new Rect(0, 0, screen_width, screen_height);
    		canvas.drawBitmap(menuBitmap, src, dst, null);
    		Log.i("zxzxzx", "画图完毕");
        	
        }
    }
}
