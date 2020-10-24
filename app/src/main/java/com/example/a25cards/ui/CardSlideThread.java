package com.example.a25cards.ui;


import android.util.Log;

import com.example.a25cards.model.Deck;
import com.example.a25cards.view.GameView;

public class CardSlideThread extends Thread{
    Deck deck;
    int index;
    float spanSlide;
    public CardSlideThread(Deck deck, int index) {
        this.deck = deck;
        this.index = index;
        this.spanSlide = 50;
    }
    @Override
    public void run() {
        try {
            float moveX = (deck.getNewPosX()[index] - deck.getPosX()[index]) / spanSlide;
            float moveY = (deck.getNewPosY()[index] - deck.getPosY()[index]) / spanSlide;
            for (int i=0; i<spanSlide; i++) {
                deck.setPosX(deck.getPosX()[index] + moveX, index);
                deck.setPosY(deck.getPosY()[index] + moveY, index);
                sleep(5);
            }
        } catch (InterruptedException e) { e.printStackTrace(); }
    }
}
