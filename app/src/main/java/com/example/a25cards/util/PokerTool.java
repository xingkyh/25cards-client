package com.example.a25cards.util;



import android.os.Build;
import android.support.annotation.RequiresApi;

import com.example.a25cards.model.Deck;
import com.example.a25cards.model.Poker;
import com.example.a25cards.view.GameView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;


public class PokerTool {

    public static boolean canPlayCards(int lastType, int lastWeight, Deck thisDeck) {

        Rule.judgeType(thisDeck);

        // 错误牌型
        if (thisDeck.getType()==Rule.WRONG) {
            return false;
        }

        // 该轮首次出牌
        if (lastType==Rule.NONE) {
            return true;
        }

        // 同牌型，比较权重
        if (lastType==thisDeck.getType() && thisDeck.getWeight()>lastWeight ) {
            return true;
        }

        // 强压牌型，比较压制等级
        if (thisDeck.getType()>=Rule.BOMB && thisDeck.getType()>lastType ) {
            return true;
        }

        return false;
    }

    public static void addToMap(Map<Integer, Integer> cardsMap, int points) {
        if (cardsMap.containsKey(points)) {
            cardsMap.put(points, cardsMap.get(points)+1);
        } else {
            cardsMap.put(points, 1);
        }
    }

    public static void removeFromMap(Map<Integer, Integer> cardsMap, int points) {
        if (cardsMap.get(points)==1) {
            cardsMap.remove(points);
        } else {
            cardsMap.put(points, cardsMap.get(points)-1);
        }
    }

    public static void getNewPos(GameView game) {
        Deck deck = game.getMyDeck();
        int num = deck.getPokersHand().size();
        int mid;
        float spanX = (float)0.025*game.getScreenWidth();
        float initX = (float) (game.getScreenWidth() / 2.0);
        float initY = (float)0.75*game.getScreenHeight();
        mid = num / 2;

        for (int i=0; i<num; i++) {
            if (i<=mid) {
                deck.setNewPosX(initX-(mid-i)*spanX, i);
            } else {
                deck.setNewPosX(initX+(i-mid)*spanX, i);
            }
            deck.setNewPosY(initY, i);
        }
    }


    public static void sortPoker(Deck deck) {
        Collections.sort(deck.getPokersHand(), new Comparator<Poker>() {
            @Override
            public int compare(Poker o1, Poker o2) {
                if (o1.getPoints()==o2.getPoints()) {
                    int i1 = 0, i2 = 1;
                    int i = 0;
                    for (String kind: Poker.kinds) {
                        i++;
                        if (o1.getKind().equals(kind)) {
                            i1 = i;
                        }
                        if (o2.getKind().equals(kind)) {
                            i2 = i;
                        }
                    }
                    return i1 - i2;
                } else {
                    return o2.getPoints() - o1.getPoints();
                }
            }
        });

    }

    public static void eraseCards(Deck deck) {
        List<Poker> list = deck.getPokersHand();
        for (int i = list.size()-1; i>=0; i--) {
            if (list.get(i).isSelected()) {
                list.remove(i);
            }
        }
    }

    public static void gatherCards(Deck deck) {
        for (int i=0; i<deck.getPokersHand().size(); i++) {
            deck.setPosX(deck.getNewPosX()[i], i);
            deck.setPosY(deck.getNewPosY()[i], i);
        }
    }

    public static void resetMap(Deck deck) {
        deck.getCardsMap().clear();
    }

    public static void getPos(GameView game, Deck deck) {
        int num = deck.getPokersHand().size();
        int mid;
        float spanX = (float)0.025*game.getScreenWidth();
        float initX = (float) (game.getScreenWidth() / 2.0);
        float initY = (float)0.3*game.getScreenHeight();
        mid = num / 2;
        for (int i=0; i<num; i++) {
            if (i<=mid) {
                deck.setPosX(initX-(mid-i)*spanX, i);
            } else {
                deck.setPosX(initX+(i-mid)*spanX, i);
            }
            deck.setPosY(initY, i);
        }
    }


}
