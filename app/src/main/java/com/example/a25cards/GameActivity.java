package com.example.a25cards;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.example.a25cards.R;
import com.example.a25cards.model.Deck;
import com.example.a25cards.model.Poker;
import com.example.a25cards.model.User;
import com.example.a25cards.view.GameView;

public class GameActivity extends AppCompatActivity {

    private Deck myDeck = new Deck();
    private User user;

    private void testPoker() {
        for (int points=3; points<=8; points++) {
            for (String kind: Poker.kinds) {
                myDeck.getPokersHand().add(new Poker(points, kind));
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//保持屏幕唤醒
        setContentView(R.layout.activity_main);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        GameView gameView = new GameView(this);
        //testPoker();
        Intent intent = getIntent();
        String username = intent.getStringExtra("name");
        user = new User(username," "," ");
        gameView.setMyDeck(myDeck);
        gameView.setUser(user);
        setContentView(gameView);
        super.onCreate(savedInstanceState);
    }
}
