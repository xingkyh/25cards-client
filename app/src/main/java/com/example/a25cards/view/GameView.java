package com.example.a25cards.view;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import com.example.a25cards.Thread.ClientThread;
import com.example.a25cards.model.Deck;
import com.example.a25cards.model.GameState;
import com.example.a25cards.model.Poker;
import com.example.a25cards.model.User;
import com.example.a25cards.ui.CardsDistributionThread;
import com.example.a25cards.util.PokerTool;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    private Context context;
    public  User user;
    private int boss = -1;
    public ArrayList<User> users = new ArrayList<User>();
    private Deck myDeck;
    private FlushThread flushThread; //绘图线程
    private InputStream inputStream = null;
    private Bitmap desk;        // 牌桌背景
    private Bitmap pokerBack;   // 扑克背面
    private Bitmap bt_ready;
    private Bitmap player; //玩家形象
    private Bitmap bt_leave;
    private Bitmap bt_pass;
    private Bitmap bt_discard;
    private Bitmap bt_setting;
    private Bitmap bt_back;
    private Bitmap bt_rechoose;
    private Bitmap bt_call;
    private Bitmap bt_nocall;
    private Bitmap dz;
    private Bitmap nm;
    private AssetManager assetManager;
    private int screenWidth ;//屏幕宽度


    private int screenHeight;//屏幕宽度
    private final float rate = 1.65f; //图片放大比例
    public static final int TIME_IN_FRAME = 30;
    public Deck lastDeck = new Deck();
    private boolean myTurn = true;

    public Deck getLastDeck() {
        return lastDeck;
    }

    public void setLastDeck(Deck lastDeck) {
        this.lastDeck = lastDeck;
    }

    public Bitmap card;
    private float cardWidth;
    private float cardHeight;
    private int state = 0;      // 游戏阶段
    private int lastType = 0;   // 上次出牌类型
    private int lastWeight = 0; // 上次出牌权重
    private float initX = 350;  // 第一张卡牌左距
    private float initY = 350;  // 卡牌上距
    private float spanX = 45;   // 卡牌间距
    private float spanY = 35;   // 选牌升降距离
    private int myseat;

    public Handler handler;
    // 定义与服务器通信的子线程
    public ClientThread clientThread;




    public int getLastType() {
        return lastType;
    }

    public void setLastType(int lastType) {
        this.lastType = lastType;
    }

    public int getLastWeight() {
        return lastWeight;
    }

    public void setLastWeight(int lastWeight) {
        this.lastWeight = lastWeight;
    }

    public float getCardWidth() {
        return cardWidth;
    }

    public void setCardWidth(float cardWidth) {
        this.cardWidth = cardWidth;
    }

    public float getCardHeight() {
        return cardHeight;
    }

    public void setCardHeight(float cardHeight) {
        this.cardHeight = cardHeight;
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public void setScreenWidth(int screenWidth) {
        this.screenWidth = screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public void setScreenHeight(int screenHeight) {
        this.screenHeight = screenHeight;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public void setMyDeck(Deck myDeck) {
        this.myDeck = myDeck;
    }

    public Deck getMyDeck() {
        return myDeck;
    }

    public void setUser(User user){
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public boolean isMyTurn() {
        return myTurn;
    }

    public void setMyTurn(boolean myTurn) {
        this.myTurn = myTurn;
    }

    public void setBoss(int boss){
        this.boss = boss;
    }

    public int getBoss() {
        return boss;
    }
    public void setMyseat(int myseat){
        this.myseat = myseat;
    }

    public int getMyseat() {
        return myseat;
    }

    public void setUsers(ArrayList<User> users){
        this.users = users;
    }

    public ArrayList<User> getUsers(){
        return users;
    }

    public void resetStatus() {
        for (int i=0; i<myDeck.getPokersHand().size(); i++) {
            myDeck.getPosY()[i] = initY;
            myDeck.getPokersHand().get(i).setSelected(false);
        }
    }

    class FlushThread extends Thread {
        private final int span = 10;
        private boolean gaming = true;
        private final GameView game;
        private final SurfaceHolder holder;


        public FlushThread(SurfaceHolder holder, GameView gameView) {
            this.game = gameView;
            this.holder = holder;
        }

        @Override
        public void run() {
            Canvas canvas;
            boolean flag = true;
            game.setState(GameState.READY);
            while (this.gaming) {
                String s = "" + state;
                canvas = null;
                long startTime = System.currentTimeMillis();
                try {
                    canvas = holder.lockCanvas(null);
                    synchronized (this.holder) {
                        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                        game.baseDraw(canvas);
                        int state = game.getState();
                        if (state==GameState.MY_DISCARD) {
                            if (flag) {
                                resetStatus();
                                flag = false;
                            }
                            game.myDraw(canvas);
                        } else if (state==GameState.DEAL_CARDS) {
                            if (flag) {
                                (new CardsDistributionThread(game)).start();
                                flag = false;
                            }
                            game.distributionDraw(canvas);
                        } else if (state==GameState.CALL_SCORE) {
                            game.callDraw(canvas);
                        }else if (state ==GameState.READY){
                            game.readyDraw(canvas);
                        }else if(state == GameState.GET_CARDS){
                            game.distributionDraw(canvas);
                        }else if(state == GameState.GAME_END){

                        }
                    }
                } finally {
                    if (canvas != null) {
                        this.holder.unlockCanvasAndPost(canvas);
                    }
                }

                try {
                    Thread.sleep(span);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


                long endTime = System.currentTimeMillis();

                int diffTime  = (int)(endTime - startTime);

                while(diffTime <=TIME_IN_FRAME) {
                    diffTime = (int)(System.currentTimeMillis() - startTime);

                    Thread.yield();
                }


            }
        }

        public boolean isGaming() {
            return gaming;
        }

        public void setGaming(boolean flag) {
            this.gaming = flag;
        }
    }

    public GameView(Context context) {
        super(context);
        this.context = context;
        handler = new Handler();
        clientThread = new ClientThread(handler,this);
        // 客户端启动ClientThread线程创建网络连接、读取来自服务器的数据
        new Thread(clientThread).start();
        getHolder().addCallback(this);
        flushThread= new FlushThread(getHolder(), this);
        assetManager = context.getAssets();
        //获得资源文件
        Resources resources = this.getResources();
        //利用DisplayMetrics类获取屏幕大小
        DisplayMetrics dm  = resources.getDisplayMetrics();
        //获得显示器的逻辑密度
        float density = dm.density;
        //获得屏幕的像素宽度，单位是px
        screenWidth = dm.widthPixels;
        //获得屏幕的像素高度，单位是px
        screenHeight = dm.heightPixels;
        init();
    }

    private void baseDraw(Canvas canvas) {
        // 绘制牌桌，使用Canvas类绘制图形画布
        //deskPaint中绘制该区域画布
        deskPaint(canvas);

        // 人物
        userPaint(canvas);
    }

    private void myDraw(Canvas canvas) {

        // 手牌
        userCardsPaint(canvas);
        // 上次出牌
        lastDeckPaint(canvas);
        // 按钮
        buttonPaint(canvas);
    }

    public void callDraw(Canvas canvas) {

        userCardsPaint(canvas);

        buttonPaint(canvas);
    }

    public void readyDraw(Canvas canvas) {

        userPaint(canvas);
        if(isMyTurn()){
            buttonPaint(canvas);
        }
    }

    private void distributionDraw(Canvas canvas) {
        // 手牌
        userCardsPaint(canvas);
    }


    private void buttonPaint(Canvas canvas) {
        canvas.drawBitmap(bt_setting, (float)0.93* screenWidth ,(float)0.01*screenHeight,null);
        canvas.drawBitmap(bt_back, (float)0.01* screenWidth ,(float)0.01*screenHeight,null);
        //readyButtonPaint(canvas);
        if(getState()==GameState.CALL_SCORE){
            if(isMyTurn()){
                startButtonPaint(canvas);
            }
        }else if(getState()==GameState.READY){
            readyButtonPaint(canvas);
        }else if(getState()==GameState.MY_DISCARD){
            if(isMyTurn()){
                gameButtonPaint(canvas);
            }
        }
    }

    private void startButtonPaint(Canvas canvas) {
        canvas.drawBitmap(bt_nocall, (float)0.28* screenWidth ,(float)0.58*screenHeight,null);
        canvas.drawBitmap(bt_call, (float)0.55* screenWidth ,(float)0.58*screenHeight,null);
    }

    private void readyButtonPaint(Canvas canvas) {
        canvas.drawBitmap(bt_leave, (float)0.28* screenWidth ,(float)0.58*screenHeight,null);
        canvas.drawBitmap(bt_ready, (float)0.55* screenWidth ,(float)0.58*screenHeight,null);
    }

    private void gameButtonPaint(Canvas canvas) {
        canvas.drawBitmap(bt_pass, (float)0.2* screenWidth ,(float)0.58*screenHeight,null);
        canvas.drawBitmap(bt_rechoose, (float)0.45* screenWidth ,(float)0.58*screenHeight,null);
        canvas.drawBitmap(bt_discard, (float)0.7* screenWidth ,(float)0.58*screenHeight,null);
    }
    private void deskPaint(Canvas canvas) {
        //使用drawBitmap来对Bitmap图片对象进行偏移，左、顶偏移均为0，同时不用画笔

        canvas.drawBitmap(desk, 0, 0, null);
    }

    private void init() {
        String deskSrc = "images/牌桌.jpg";
        String cardSrc = "images/方块2.png";
        String pokerBackSrc = "images/牌背面.png";
        String playerSrc = "images/人物.png";
        String bt_readySrc ="images/准备.png";
        String bt_leaveSrc = "images/离开.png";
        String bt_passSrc = "images/不出.png";
        String bt_discardSrc = "images/出牌.png";
        String bt_settingSrc = "images/setting.png";
        String bt_backSrc = "images/back.png";
        String bt_rechooseSrc = "images/重选.png";
        String bt_callSrc = "images/抢地主.png";
        String bt_nocallSrc = "images/不叫.png";
        String dzSrc = "images/dz.png";
        String nmSrc = "images/nm.png";

        try {
            desk = BitmapFactory.decodeStream(assetManager.open(deskSrc));
            card = BitmapFactory.decodeStream(assetManager.open(cardSrc));
            pokerBack = BitmapFactory.decodeStream(assetManager.open(pokerBackSrc));
            player = BitmapFactory.decodeStream(assetManager.open(playerSrc));
            bt_ready = BitmapFactory.decodeStream(assetManager.open(bt_readySrc));
            bt_leave = BitmapFactory.decodeStream(assetManager.open(bt_leaveSrc));
            bt_discard = BitmapFactory.decodeStream(assetManager.open(bt_discardSrc));
            bt_pass = BitmapFactory.decodeStream(assetManager.open(bt_passSrc));
            bt_setting = BitmapFactory.decodeStream(assetManager.open(bt_settingSrc));
            bt_back = BitmapFactory.decodeStream(assetManager.open(bt_backSrc));
            bt_rechoose = BitmapFactory.decodeStream(assetManager.open(bt_rechooseSrc));
            bt_call = BitmapFactory.decodeStream(assetManager.open(bt_callSrc));
            bt_nocall = BitmapFactory.decodeStream(assetManager.open(bt_nocallSrc));
            dz = BitmapFactory.decodeStream(assetManager.open(dzSrc));
            nm = BitmapFactory.decodeStream(assetManager.open(nmSrc));

            //屏幕自适应大小
            Matrix matrix = new Matrix();
            matrix.postScale((float)(1.0*screenWidth/desk.getWidth()), (float)(1.0*screenHeight/desk.getHeight()));
            desk = Bitmap.createBitmap(desk, 0, 0, desk.getWidth(),desk.getHeight(),matrix,true);

            matrix = new Matrix();
            matrix.postScale(rate, rate);
            pokerBack = Bitmap.createBitmap(pokerBack, 0, 0, pokerBack.getWidth(), pokerBack.getHeight(),matrix,true);
            bt_ready = Bitmap.createBitmap(bt_ready, 0, 0, bt_ready.getWidth(), bt_ready.getHeight(),matrix,true);
            bt_leave = Bitmap.createBitmap(bt_leave, 0, 0, bt_leave.getWidth(), bt_leave.getHeight(),matrix,true);
            bt_pass = Bitmap.createBitmap(bt_pass, 0, 0, bt_pass.getWidth(), bt_pass.getHeight(),matrix,true);
            bt_discard = Bitmap.createBitmap(bt_discard, 0, 0, bt_discard.getWidth(), bt_discard.getHeight(),matrix,true);
            bt_rechoose = Bitmap.createBitmap(bt_rechoose, 0, 0, bt_rechoose.getWidth(), bt_rechoose.getHeight(),matrix,true);
            bt_call = Bitmap.createBitmap(bt_call, 0, 0, bt_call.getWidth(), bt_call.getHeight(),matrix,true);
            bt_nocall = Bitmap.createBitmap(bt_nocall, 0, 0, bt_nocall.getWidth(), bt_nocall.getHeight(),matrix,true);

            matrix.postScale(0.5f,0.5f);
            bt_setting = Bitmap.createBitmap(bt_setting, 0, 0, bt_setting.getWidth(), bt_setting.getHeight(),matrix,true);
            matrix.postScale(0.75f,0.75f);
            dz =  Bitmap.createBitmap(dz, 0, 0, dz.getWidth(), dz.getHeight(),matrix,true);
            matrix.postScale(0.35f,0.35f);
            bt_back = Bitmap.createBitmap(bt_back, 0, 0, bt_back.getWidth(), bt_back.getHeight(),matrix,true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        spanX = (float)0.025*screenWidth;
        initX = (float) ((float) getScreenWidth()/2.0);
        initY = (float)0.75*screenHeight;
        cardWidth = (float)(pokerBack.getWidth()*1.5);
    }



    private void userCardsPaint(Canvas canvas) {

        List<Poker> pokers = myDeck.getPokersHand();

        Matrix matrix = new Matrix();

        //  matrix.setSkew(0, 1);
        //  Bitmap apokerBack = Bitmap.createBitmap(pokerBack, 0, 0, pokerBack.getWidth(), pokerBack.getHeight(),matrix,true);
     //   canvas.drawBitmap(pokerBack, (float)0.19*screenWidth, (float)0.2*screenHeight,null);


        //  matrix = new Matrix();
        //  matrix.setSkew(1, 0);
        //  Bitmap bpokerBack = Bitmap.createBitmap(pokerBack, 0, 0, pokerBack.getWidth(), pokerBack.getHeight(),matrix,true);
     //   canvas.drawBitmap(pokerBack, (float)0.73*screenWidth, (float)0.2*screenHeight,null);

        matrix = new Matrix();
        matrix.postScale((float)1.5, (float)1.5);
        for (int i=0; i<pokers.size(); i++) {
            Poker poker = pokers.get(i);
            String name = "images/" + poker.getKind() + poker.getPoints() + ".png";
            try {
                inputStream = assetManager.open(name);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Bitmap thisCard = BitmapFactory.decodeStream(inputStream);
            thisCard = Bitmap.createBitmap(thisCard, 0, 0, thisCard.getWidth(),thisCard.getHeight(), matrix,true);
            canvas.drawBitmap(thisCard, myDeck.getPosX()[i], myDeck.getPosY()[i],null);
            String s = "" + myDeck.getPosX()[i];
           /* getHolder().unlockCanvasAndPost(canvas);
            try {
                flushThread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            canvas = getHolder().lockCanvas();*/
        }
    }

    private void userPaint(Canvas canvas) {
        if(((myseat+1)%4) == boss){
            canvas.drawBitmap(dz,(float) (screenWidth-dz.getWidth()-screenWidth*0.01), (float)0.3*screenHeight ,null);
        }else{
            canvas.drawBitmap(nm,(float) (screenWidth-dz.getWidth()-screenWidth*0.01), (float)0.3*screenHeight ,null);
        }
        if(((myseat+2)%4) == boss){
            canvas.drawBitmap(dz,(float) 0.35*screenWidth, (float)0.1*screenHeight ,null);
        }else{
            canvas.drawBitmap(nm,(float) 0.35*screenWidth, (float)0.1*screenHeight ,null);
        }
        if(((myseat+3)%4) == boss){
            canvas.drawBitmap(dz,(float) 0.005*screenWidth, (float)0.3*screenHeight ,null);
        }else{
            canvas.drawBitmap(nm,(float) 0.005*screenWidth, (float)0.3*screenHeight ,null);
        }
    }

    private void lastDeckPaint(Canvas canvas) {
        PokerTool.getPos(this, lastDeck);
        List<Poker> pokers = lastDeck.getPokersHand();
        Matrix matrix = new Matrix();
        matrix = new Matrix();
        matrix.postScale((float)1.5, (float)1.5);
        for (int i=0; i<pokers.size(); i++) {
            Poker poker = pokers.get(i);
            String name = "images/" + poker.getKind() + poker.getPoints() + ".png";
            try {
                inputStream = assetManager.open(name);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Bitmap thisCard = BitmapFactory.decodeStream(inputStream);
            thisCard = Bitmap.createBitmap(thisCard, 0, 0, thisCard.getWidth(),thisCard.getHeight(), matrix,true);
            canvas.drawBitmap(thisCard, lastDeck.getPosX()[i], lastDeck.getPosY()[i],null);
        }
    }


    private void statusChange(int index) {
        Poker poker = myDeck.getPokersHand().get(index);
        int points = poker.getPoints();
        if (poker.isSelected()) {
            myDeck.setSumCards(myDeck.getSumCards()-1);
            PokerTool.removeFromMap(myDeck.getCardsMap(), points);
            poker.setSelected(false);
            myDeck.getPosY()[index] += spanY;
        } else {
            myDeck.setSumCards(myDeck.getSumCards()+1);
            PokerTool.addToMap(myDeck.getCardsMap(), points);
            poker.setSelected(true);
            myDeck.getPosY()[index] -= spanY;
        }
    }



    private void playJudge(float x, float y) {

        int num = myDeck.getPokersHand().size();

        int mid = num / 2;

        if (x>=initX-mid*spanX && x<=initX+(num-mid)*spanX+getCardWidth()) {   // 手牌范围内
            int index = 0;
            // 判断点击的卡牌
            if (x<initX) {
                index = mid - (int) ((initX-x) / spanX) - 1;
            } else {
                index = mid + (int) ((x-initX) / spanX);
            }
            if (x>=initX+(num-mid)*spanX) {
                index = num - 1;
            }
            if (y>=myDeck.getPosY()[index]&&y<=myDeck.getPosY()[index]+card.getHeight()*1.5) {
                statusChange(index);    // 改变选取状态
                if (PokerTool.canPlayCards(lastType, lastWeight, myDeck)) {    // 出牌判定

                }

                String text = "type：" + myDeck.getType()
                        + "  weight: " + myDeck.getWeight()
                        + "  mapInf: " + myDeck.getCardsMap();
           //     Toast.makeText(context, text,Toast.LENGTH_SHORT).show();
            }
        }

        //点击重选
        if(x>=0.45*screenWidth && x<= 0.45*screenWidth+bt_rechoose.getWidth()
                && y>=0.58*screenHeight && y<=0.58*screenHeight+bt_rechoose.getHeight()){
            for(int i = 0; i < myDeck.getPokersHand().size(); i++){
                if(myDeck.getPokersHand().get(i).isSelected()){
                    statusChange(i);
                }
            }
            PokerTool.resetMap(myDeck);
        }

        //点击出牌
        if(x>=0.7*screenWidth && x<= 0.7*screenWidth+bt_discard.getWidth()
                && y>=0.58*screenHeight && y<=0.58*screenHeight+bt_discard.getHeight()){
            if (PokerTool.canPlayCards(lastType, lastWeight, myDeck)) {    // 出牌判定
                System.out.println("lastType  lastWeight:  "+String.valueOf(lastType)+"  "+String.valueOf(lastWeight));
                System.out.println("Type  Weight:  "+String.valueOf(getMyDeck().getType())+"  "+String.valueOf(getMyDeck().getWeight()));
                int nn = 0;
                for( int i = 0 ; i < getMyDeck().getPokersHand().size(); i++){
                    if(getMyDeck().getPokersHand().get(i).isSelected()){
                        nn++;
                    }
                }
                if(nn != getMyDeck().getPokersHand().size()){
                    Message msg = new Message();
                    msg.what = 0x7777;
                    clientThread.revHandler.sendMessage(msg);
                }else{
                    Message msg = new Message();
                    msg.what = 0x9999;
                    clientThread.revHandler.sendMessage(msg);
                }
            }
        }

        //点击不出
        if(x>=0.2*screenWidth && x<= 0.2*screenWidth+bt_pass.getWidth()
                && y>=0.58*screenHeight && y<=0.58*screenHeight+bt_pass.getHeight()){
            if(getLastType()!=0&& getLastWeight()!=0){
                for(int i = 0; i < myDeck.getPokersHand().size(); i++){
                    if(myDeck.getPokersHand().get(i).isSelected()){
                        statusChange(i);
                    }
                }
                PokerTool.resetMap(myDeck);
                Message msg = new Message();
                msg.what = 0x8888;
                clientThread.revHandler.sendMessage(msg);
            }
        }
    }


    private void butCallJudge(float x, float y){
        if(x>=0.28*screenWidth && x<=0.28*screenWidth+bt_call.getWidth() && y>=0.58*screenHeight && y<=0.58*screenHeight+bt_call.getHeight()){
            setMyTurn(false);
            Message msg = new Message();
            msg.what = 0x5678;
            clientThread.revHandler.sendMessage(msg);
        }else if(x>=0.55*screenWidth && x<=0.55*screenWidth+bt_nocall.getWidth() && y>=0.58*screenHeight && y<=0.58*screenHeight+bt_nocall.getHeight()){
            setMyTurn(false);
            Message msg = new Message();
            msg.what = 0x1234;
            clientThread.revHandler.sendMessage(msg);
        }
    }

    private void butReadyJudge(float x, float y){
        //点击准备
        if(x >= 0.55*screenWidth && x <= 0.55*screenWidth+bt_ready.getWidth() && y >= 0.58*screenHeight && y<=0.58*screenHeight+bt_ready.getHeight()){
            setMyTurn(false);
            Message msg = new Message();
            msg.what = 0x789;
            clientThread.revHandler.sendMessage(msg);
        }
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {    // 点击事件
        try{
           if(isMyTurn()){
               switch (event.getAction()) {
                   case MotionEvent.ACTION_DOWN:
                       float x = event.getRawX();
                       float y = event.getRawY();
                       if (getState() == GameState.MY_DISCARD) {
                           playJudge(x, y);
                       } else if (getState() == GameState.CALL_SCORE) {  //  叫地主
                           butCallJudge(x, y);
                       } else if (getState() == GameState.READY) { //准备离开
                           butReadyJudge(x, y);
                       }
                       break;
                   case MotionEvent.ACTION_MOVE:
                       break;
                   case MotionEvent.ACTION_UP:
                       break;
               }
           }
        }catch (Exception e) {

        }finally {
            return super.onTouchEvent(event);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        flushThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        flushThread.setGaming(false);
        while (retry) {
            try {
                flushThread.join();
                retry = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
