package com.example.a25cards.myCard;

import java.util.List;
import java.util.Random;
import java.util.Vector;

import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.a25cards.R;

public class MyView extends SurfaceView implements SurfaceHolder.Callback,
		Runnable {

	SurfaceHolder surfaceHolder;
	Canvas canvas;
	Boolean repaint=false;
	Boolean start;
	Thread gameThread,drawThread;
	// 判断当前是否要牌
	int []flag=new int[4];
	// 屏幕宽度和高度
	int screen_height;
	int screen_width;
	// 图片资源
	Bitmap cardBitmap[] = new Bitmap[108];
	Bitmap bgBitmap;    //背面
	Bitmap cardBgBitmap;//图片背面
	Bitmap dizhuBitmap;//地主图标
	Bitmap identityBitmap[] = new Bitmap[4];
	// 基本参数
	int cardWidth, cardHeight;
	//画笔
	Paint paint;
	// 牌对象
	Card card[] = new Card[108];
	//按钮
	String buttonText[]=new String[2];
	Bitmap buttonBitmap[]=new Bitmap[2];
	//提示
	String message[]=new String[4];
	boolean hideButton=true;
	// List
	List<Card> playerList[]=new Vector[4];
	//地主牌
	List<Card> dizhuList=new Vector<Card>();
	//谁是地主
	int dizhuFlag=-1;
	//轮流
	int turn=-1;
	//已出牌表
	List<Card> outList[]=new Vector[4];
	Handler handler;
	// 构造函数
	public MyView(Context context,Handler handler) {
		super(context);
		Common.view=this;
		this.handler=handler;
		surfaceHolder = this.getHolder();
		surfaceHolder.addCallback(this);
	}
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		start=true;
		screen_height = getHeight();
		screen_width = getWidth();
		// 初始化
		InitBitMap();
		// 洗牌
		washCards();
		// 开始游戏进程
		gameThread=new Thread(new Runnable() {
			@Override
			public void run() {
				//开始发牌
				handCards();
				
				//等待地主选完
				while(start){
					switch (turn) {
					case 0:
					case 2:
					case 3:
						playerAI(turn);
						break;
					case 1:
						player1();
						break;
					default:
						break;
					}
					win();
				}
			}
		});
		gameThread.start();
		// 开始绘图进程
		drawThread=new Thread(this);
		drawThread.start();
	}
	// 初始化图片,参数
	public void InitBitMap() {
		for(int i=0;i<4;i++)
			flag[i]=0;
		dizhuFlag=-1;
		turn=-1;
		int count=0;
		for (int i = 1; i <= 4; i++) {
			for (int j = 3; j <= 15; j++) {
				//根据名字找出ID
				for (int k = 0;k < 2; k++){
					String name = "a" + i + "_" + j;
					ApplicationInfo appInfo = getContext().getApplicationInfo();
					int id = getResources().getIdentifier(name, "drawable",
							appInfo.packageName);
					cardBitmap[count] = BitmapFactory.decodeResource(getResources(),
							id);
					cardBitmap[count] = changeBitmapSize(cardBitmap[count], 95, 125);
					card[count] = new Card(cardBitmap[count].getWidth(),cardBitmap[count].getHeight(), cardBitmap[count]);
					//设置Card的名字
					card[count].setName(name);
					count ++;
				}

			}
		}
		//最后小王，大王
		for (int k = 0; k < 2; k++){
			int i = count + k;
			cardBitmap[i] = BitmapFactory.decodeResource(getResources(),
					R.drawable.a5_16);
			cardBitmap[i] = changeBitmapSize(cardBitmap[i], 95, 125);
			card[i]=new Card(cardBitmap[i].getWidth(), cardBitmap[i].getHeight(),cardBitmap[i]);
			card[i].setName("a5_16");
			i += 2;
			cardBitmap[i] = BitmapFactory.decodeResource(getResources(),
					R.drawable.a5_17);
			cardBitmap[i] = changeBitmapSize(cardBitmap[i], 95, 125);
			card[i]=new Card(cardBitmap[i].getWidth(), cardBitmap[i].getHeight(),cardBitmap[i]);
			card[i].setName("a5_17");
		}
		cardWidth=card[0].width;
		cardHeight=card[0].height;

		//地主图标
		Bitmap temp = BitmapFactory.decodeResource(getResources(), R.drawable.identity_1);
		dizhuBitmap=changeBitmapSize(temp, 51, 96);
		temp = BitmapFactory.decodeResource(getResources(), R.drawable.identity_2);
		identityBitmap[0] = identityBitmap[1] = identityBitmap[2] = identityBitmap [3] =
				changeBitmapSize(temp, 51, 96);
		//背景
		bgBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.gamebackground);
		bgBitmap = changeBitmapSize(bgBitmap, 808, 538);
		cardBgBitmap=BitmapFactory.decodeResource(getResources(), R.drawable.cardbg1);
		cardBgBitmap=changeBitmapSize(cardBgBitmap, 95, 125);
		//按钮
		for(int i=0;i<2;i++)
		{
			buttonText[i]=new String();
		}
		buttonText[0]="抢地主";
		buttonText[1]="不抢";
		temp = BitmapFactory.decodeResource(getResources(), R.drawable.button_1);
		buttonBitmap[0] = changeBitmapSize(temp, 190, 83);
		temp = BitmapFactory.decodeResource(getResources(), R.drawable.button_2);
		buttonBitmap[1] = changeBitmapSize(temp, 190, 83);
		//消息,已出牌
		for(int i=0;i<4;i++)
		{
			message[i]=new String("");
			outList[i]=new Vector<Card>();
		}
		paint=new Paint();
		paint.setColor(Color.YELLOW);
		paint.setTextSize(cardWidth*2/3);
		paint.setAntiAlias(true);
		paint.setStyle(Style.FILL);
		paint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));
		paint.setStrokeWidth(1.0f);
		paint.setTextAlign(Align.CENTER);

	}

	// 改变图片的大小
	private Bitmap changeBitmapSize(Bitmap bitmap, int newWidth, int newHeight) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		//计算压缩的比率
		float scaleWidth=((float)newWidth)/width;
		float scaleHeight=((float)newHeight)/height;

		//获取想要缩放的matrix
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth,scaleHeight);

		//获取新的bitmap
		bitmap=Bitmap.createBitmap(bitmap,0,0,width,height,matrix,true);
		bitmap.getWidth();
		bitmap.getHeight();
		return bitmap;
	}

	// 画背景
	public void drawBackground() {
			Rect src = new Rect(0, 0, bgBitmap.getWidth(),
					bgBitmap.getHeight());
			Rect dst = new Rect(0, 0, screen_width, screen_height);
			canvas.drawBitmap(bgBitmap, src, dst, null);
	}
	// 玩家牌
	public void drawPlayer(int player){
			if(playerList[player]!=null&&playerList[player].size()>0)
			{
				for(Card card:playerList[player])
					drawCard(card);
			}
	}
	//画牌
	public void drawCard(Card card){
		Bitmap tempbitBitmap;
		if(card.rear)
			tempbitBitmap=cardBgBitmap;
		else {
			tempbitBitmap=card.bitmap;
		}
		canvas.drawBitmap(tempbitBitmap, card.getSRC(),
				card.getDST(), null);
	}
	//洗牌
	public void washCards() {
		//打乱顺序
		for(int i=0;i<200;i++){
			Random random=new Random();
			int a=random.nextInt(108);
			int b=random.nextInt(108);
			Card k=card[a];
			card[a]=card[b];
			card[b]=k;
		}
	}
	//发牌
	public void handCards(){
		//开始发牌
		int t=0;
		for(int i=0;i<4;i++){
			playerList[i]=new Vector<Card>();
		}
		for(int i=0;i<108;i++)
		{
			if(i > 99)//地主牌
			{
				//放置地主牌
				card[i].setLocation(screen_width/2-(104-i)*cardWidth/2,screen_height/2-cardHeight/2);
				dizhuList.add(card[i]);
				update();
				continue;
			}
			switch ((t++)%4) {
			case 0:
				//左边玩家
				card[i].setLocation(cardWidth/2,cardHeight/2+i*cardHeight/28);
				playerList[0].add(card[i]);
				break;
			case 1:
				//我
				card[i].setLocation(screen_width/2- (13 - i / 4) * cardWidth /3,screen_height-cardHeight);
				card[i].rear=false;//翻开
				playerList[1].add(card[i]);
				break;
			case 2:
				//右边玩家
				card[i].setLocation(screen_width-3*cardWidth/2,cardHeight/2+i*cardHeight/28);
				playerList[2].add(card[i]);
				break;
			case 3:
				//上方玩家
				card[i].setLocation(screen_width/2- (13 - i / 4) * cardWidth /3, 0);
				playerList[3].add(card[i]);
				break;
			}
			update();
			Sleep(100);
		}
		//重新排序
		for(int i=0;i<4;i++){
			Common.setOrder(playerList[i]);
			Common.rePosition(this, playerList[i],i);
		}
		//打开按钮
		hideButton=false;
		update();
	}
	//sleep();
	public void Sleep(long i){
		try {
			Thread.sleep(i);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//按钮(抢地主，不抢，出牌，不抢)
	public void drawButton(){
		if(!hideButton)
		{
			//canvas.drawText(buttonText[0],screen_width/2-2*cardWidth,screen_height-cardHeight*2, paint);
			//canvas.drawText(buttonText[1],screen_width/2+2*cardWidth,screen_height-cardHeight*2, paint);
			//canvas.drawRect(new RectF(screen_width/2-3*cardWidth, screen_height-cardHeight*5/2,
			//		screen_width/2-cardWidth,screen_height-cardHeight*11/6), paint);
			//canvas.drawRect(new RectF(screen_width/2+cardWidth, screen_height-cardHeight*5/2,
			//		screen_width/2+3*cardWidth,screen_height-cardHeight*11/6), paint);
			canvas.drawBitmap(buttonBitmap[0], screen_width/2-3*cardWidth, screen_height-cardHeight*5/2, null);
			canvas.drawBitmap(buttonBitmap[1], screen_width/2+cardWidth, screen_height-cardHeight*5/2, null);
		}
		
	}
	//Message
	public void drawMessage(){

		if(!message[1].equals(""))
		{
			canvas.drawText(message[1],screen_width/2,screen_height-cardHeight*2,paint);
		}
		if(!message[0].equals(""))
		{
			canvas.drawText(message[0],cardWidth*3,screen_height/4,paint);
		}
		if(!message[2].equals(""))
		{
			canvas.drawText(message[2],screen_width-cardWidth*3,screen_height/4,paint);
		}
		if(!message[3].equals(""))
		{
			canvas.drawText(message[3],screen_width/2,cardHeight*3/2,paint);
		}
	}
	//下一个玩家
	public void nextTurn(){
		turn=(turn+1)%4;
	}
	//画地主头像
	public void drawDizhuIcon(){
		if(dizhuFlag>=0){
			identityBitmap[dizhuFlag] = dizhuBitmap;
			canvas.drawBitmap(identityBitmap[0],cardWidth*7/4f,cardHeight/2f,null);
			canvas.drawBitmap(identityBitmap[1],cardWidth*1.75f,screen_height-cardHeight,null);
			canvas.drawBitmap(identityBitmap[2], screen_width-cardWidth*7/4f-51,cardHeight/2f,null);
			canvas.drawBitmap(identityBitmap[3],cardWidth*3f,0,null);
			/*
			float x=0f,y=0f;
			if(dizhuFlag==0)
			{
				x=cardWidth/2f;
				y=dizhuBitmap.getHeight()/2;
			}
			if(dizhuFlag==1)
			{
				x=cardWidth*1.5f;
				y=screen_height-cardHeight;
			}
			if(dizhuFlag==2)
			{
				x=screen_width-cardWidth/2f-dizhuBitmap.getWidth();
				y=dizhuBitmap.getHeight()/2;
			}
			if(dizhuFlag==3)
			{
				x=cardWidth*1.5f;
				y=2f*cardHeight;
			}
			canvas.drawBitmap(dizhuBitmap,x,y,null);*/
		}
	}
	//画已走的牌
	public void drawOutList(){
		int x=0,y=0;
		for(int i=0,len=outList[1].size();i<len;i++)
		{
			x=screen_width/2+(i-len/2)*cardWidth/3;
			y=screen_height-5*cardHeight/2;
			canvas.drawBitmap(outList[1].get(i).bitmap, x,y, null);
		}
		for(int i=0,len=outList[0].size();i<len;i++)
		{
			x=3*cardWidth;
			y=screen_height/2+(i-len/2-4)*cardHeight*7/24;
			canvas.drawBitmap(outList[0].get(i).bitmap, x,y, null);
		}
		for(int i=0,len=outList[2].size();i<len;i++)
		{
			x=screen_width-cardWidth*4;
			y=screen_height/2+(i-len/2-4)*cardHeight*7/24;
			canvas.drawBitmap(outList[2].get(i).bitmap, x,y, null);
		}
		for(int i=0,len=outList[3].size();i<len;i++)
		{
			x=screen_width/2+(i-len/2)*cardWidth/3;
			y=3*cardHeight/2;
			canvas.drawBitmap(outList[3].get(i).bitmap, x,y, null);
		}
	}
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	//player0
	public void player0(){
		//Log.i("mylog", "玩家0");
		List<Card> player0=null;
		Common.currentFlag=0;
		int upperFlag = getUpperFlag(0);
		if(flag[1]==0&&flag[2]==0&&flag[3]==0)
		{
			player0=Common.getBestAI(playerList[0],null);
			
		}
		else if(flag[2]==0&&flag[3]==0)
		{
			Common.oppoerFlag=1;
			player0=Common.getBestAI(playerList[0],outList[1]);
		}
		else if(flag[3]==0){
			Common.oppoerFlag=2;
			player0=Common.getBestAI(playerList[0],outList[2]);
		}
		else {
			Common.oppoerFlag=3;
			player0=Common.getBestAI(playerList[0],outList[3]);
		}
		message[0]="";
		outList[0].clear();
		setTimer(3, 0);
		if(player0!=null)
		{
			outList[0].addAll(player0);
			playerList[0].removeAll(player0);
			Common.rePosition(this, playerList[0], 0);
			message[0]="";
			flag[0]=1;
		}else {
			message[0]="不要";
			flag[0]=0;
		}
		update();
		nextTurn();
	}
	//player2
	public void player2(){
		//Log.i("mylog", "玩家2");
		Common.currentFlag=2;
		List<Card> player2=null;
		if(flag[1]==0&&flag[0]==0&&flag[3]==0)
		{
			player2=Common.getBestAI(playerList[2],null);
		}
		else if(flag[1]==0)
		{
			player2=Common.getBestAI(playerList[2],outList[0]);
			Common.oppoerFlag=0;
		}
		else {
			player2=Common.getBestAI(playerList[2],outList[1]);
			Common.oppoerFlag=1;
		}
		message[2]="";
		outList[2].clear();
		setTimer(3, 2);
		if(player2!=null)
		{
			outList[2].addAll(player2);
			playerList[2].removeAll(player2);
			Common.rePosition(this, playerList[2], 2);
			message[2]="";
			flag[2]=1;
		}else {
			message[2]="不要";
			flag[2]=0;
		}
		update();
		nextTurn();
	}
	// 返回上一个出牌的玩家，若为自己则返回-1
	private int getUpperFlag(int id){
		id += 4;
		for (int i = 0; i < 3; i++){
			id--;
			int upperFlag = id % 4;
			if (flag[upperFlag] != 0){
				return upperFlag;
			}
		}
		return -1;
	}
	// 非玩家调用这个方法
	public void playerAI(int id){
		Common.currentFlag=id;
		List<Card> player=null;
		int upperFlag = getUpperFlag(id);
		if(upperFlag == -1)
		{
			player=Common.getBestAI(playerList[id],null);
		}
		else {
			player=Common.getBestAI(playerList[id],outList[upperFlag]);
			Common.oppoerFlag=upperFlag;
		}
		message[id]="";
		outList[id].clear();
		setTimer(3, id);
		if(player!=null)
		{
			outList[id].addAll(player);
			playerList[id].removeAll(player);
			Common.rePosition(this, playerList[id], id);
			message[id]="";
			flag[id]=1;
		}else {
			message[id]="不要";
			flag[id]=0;
		}
		update();
		nextTurn();
	}
	//player1
	public void player1(){
		Sleep(1000);
		//开始写出牌的了
		buttonText[0]="出牌";
		buttonText[1]="不要";
		Bitmap temp = BitmapFactory.decodeResource(getResources(), R.drawable.button_3);
		buttonBitmap[0] = changeBitmapSize(temp, 190, 83);
		temp = BitmapFactory.decodeResource(getResources(), R.drawable.button_4);
		buttonBitmap[1] = changeBitmapSize(temp, 190, 83);
		hideButton=false;
		outList[1].clear();
		update();
		//倒计时
		int i=28;
		while((turn==1)&&(i-->0)){
			//计时器函数draw timer.画出计时画面
			message[1]=i+"";
			update();
			Sleep(1000);
		}
		hideButton=true;
		update();
		if(turn==1&&i<=0)//说明用户没有任何操作
		{
			//自动不要，或者选一张随便出
			if (getUpperFlag(1)  == -1){
				List<Card> player=Common.getBestAI(playerList[1],null);
				outList[1].addAll(player);
				playerList[1].removeAll(player);
				Common.rePosition(this, playerList[1], 1);
				flag[1] = 1;
			}
			else {
				message[1]="不要";
				flag[1]=0;
			}
			nextTurn();
		}
		update();
	}
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		start=false;
	}
	//主要绘图线程
	@Override
	public void run() {
		while (start) {
			if(repaint)
			{
				draw();
				repaint=false;
				Sleep(33);
			}
		}
	}

	private void draw() {
		//枷锁
		synchronized (surfaceHolder) {
			try {
				canvas = surfaceHolder.lockCanvas();
				// 画背景
				drawBackground();
				// 画牌
				for(int i=0;i<4;i++)
					drawPlayer(i);
				// 地主牌
				for(int i=0,len=dizhuList.size();i<len;i++)
					drawCard(dizhuList.get(i));
				// 画按钮( 抢地主,不抢,出牌,不出)
				drawButton();
				// message部分 用3个String存
				drawMessage();
				// 画地主图标
				drawDizhuIcon();
				// 出牌界面(3个地方,用3个vector存)
				drawOutList();

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (canvas != null)
					surfaceHolder.unlockCanvasAndPost(canvas);
			}
		}
	}

	//更新函数
	public void update(){
		repaint=true;
	}
	//触摸事件
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		//只接受按下事件
		if(event.getAction()!=MotionEvent.ACTION_UP)
			return true;
		//点选牌
		EventAction eventAction=new EventAction(this,event);
		Card card=eventAction.getCard();
		if(card!=null)
		{
			Log.i("mylog", card.name);
			if(card.clicked)
				card.y+=card.height/3;
			else 
				card.y-=card.height/3;
			card.clicked=!card.clicked;
			update();//重绘
		}
		//按钮事件
		eventAction.getButton();
		return true;
	}
	//计时器
	public void setTimer(int t,int flag)//延时
	{
		while(t-->0){
			Sleep(1000);
			message[flag]=t+"";
			update();
		}
		message[flag]="";
	}
	//判断成功
	public void win(){
		int flag=-1;
		if(playerList[0].size()==0)
			flag=0;
		if(playerList[1].size()==0)
			flag=1;
		if(playerList[2].size()==0)
			flag=2;
		if(flag>-1){
			for(int i=0;i<108;i++)
			{
				card[i].rear=false;
			}
			update();
			start=false;
			Message msg=new Message();
			msg.what=0;
			Bundle builder=new Bundle();
			if(flag==1)
				builder.putString("data","恭喜你赢了");
			if(flag==dizhuFlag&&flag!=1)
				builder.putString("data","恭喜电脑"+flag+"赢了");
			if(flag!=dizhuFlag&&flag!=1)
				builder.putString("data","恭喜你同伴赢了");
			msg.setData(builder);
			handler.sendMessage(msg);
		}
	}
}
