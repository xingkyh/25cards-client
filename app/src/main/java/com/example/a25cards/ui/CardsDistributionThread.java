package com.example.a25cards.ui;

import android.os.Build;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.example.a25cards.model.Deck;
import com.example.a25cards.model.GameState;
import com.example.a25cards.util.PokerTool;
import com.example.a25cards.view.GameView;

public class CardsDistributionThread extends Thread{
    private GameView game;
    private Deck deck;
    float initX;
    float initY;
    public CardsDistributionThread(GameView game) {
        this.game = game;
        this.deck = game.getMyDeck();
    }

            private void setPos() {

                initX = (game.getScreenWidth()-game.getCardWidth()) / 2;
                initY = (float) (game.getScreenHeight()*0.2);
                for (int i=0; i<25; i++) {
            deck.setPosX(initX, i);
            deck.setPosY(initY, i);
            String s = "" + deck.getPosX().toString();
            Log.i("pos", s);
        }
    }

    private void setSortPos() {
        for (int i=0; i<25; i++) {
            deck.setNewPosX(deck.getPosX()[0], i);
            deck.setNewPosY(deck.getPosY()[0], i);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void run() {


        setPos();

        try {
            sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        PokerTool.getNewPos(game);

        for (int i=0; i<5; i++) {
            for (int u=0; u<5; u++) {
                int index = i * 5 + u;
                (new CardSlideThread(deck, index)).start();
                try {
                    sleep(30);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            try {
                sleep(400);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        setSortPos();

        // 卡牌排序
        PokerTool.sortPoker(deck);


        for (int i=0; i<25; i++) {
            (new CardSlideThread(deck, i)).start();
        }

        try {
            sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        PokerTool.getNewPos(game);

        try {
            sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (int i=0; i<25; i++) {
            game.getMyDeck().getPokersHand().get(i).setSelected(false);
            (new CardSlideThread(deck, i)).start();
        }

        try {
            sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        Message msg = new Message();
        msg.what = 0x666;
        game.clientThread.revHandler.sendMessage(msg);
        game.setMyTurn(false);
        game.setState(GameState.CALL_SCORE);

        // 向服务器发送消息

    }
}
