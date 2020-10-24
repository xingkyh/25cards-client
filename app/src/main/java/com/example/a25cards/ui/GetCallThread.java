package com.example.a25cards.ui;

import com.example.a25cards.model.Deck;
import com.example.a25cards.model.GameState;
import com.example.a25cards.model.Poker;
import com.example.a25cards.util.PokerTool;
import com.example.a25cards.view.GameView;

import java.util.ArrayList;
import java.util.List;

public class GetCallThread extends Thread{

    GameView game;

    public GetCallThread(GameView game) {
        this.game = game;
    }

    @Override
    public void run() {
        try {
            sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<Poker> newPokers = new ArrayList<>();
        float initY = (float)0.75*game.getScreenHeight();
        Deck deck = game.getMyDeck();

        PokerTool.sortPoker(game.getMyDeck());
        PokerTool.getNewPos(game);

        for (int i=0; i<33; i++) {
            deck.setPosX(deck.getNewPosX()[i], i);
            if (deck.getPokersHand().get(i).isSelected()) {
                deck.setPosY(initY - 70, i);
            } else {
                deck.setPosY(initY, i);
            }
        }
        try {
            sleep(800);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (int i=0; i<33; i++) {
            game.getMyDeck().getPokersHand().get(i).setSelected(false);
            new CardSlideThread(deck, i).start();
        }
        try {
            sleep(800);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        game.setMyTurn(true);
        game.setState(GameState.MY_DISCARD);
    }
}
