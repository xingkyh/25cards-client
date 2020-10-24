package com.example.a25cards.util;

import android.util.Log;

import com.example.a25cards.model.Deck;
import com.example.a25cards.model.Poker;

import java.util.Map;

public class Rule {
    public static final int NONE = 0;
    public static final int WRONG = 0;
    public static final int SINGLE = 1;         // 单张
    public static final int PAIR = 2;           // 对子
    public static final int TRIPLET = 3;        // 三张
    public static final int TRIPLET_PAIR = 4;   // 三带对
    public static final int PAIRS = 10;         // 连对
    public static final int TRIPLETS = 40;      // 飞机
    public static final int TRIPLETS_PAIR = 70; // 飞机带对
    public static final int STRAIGHT = 100;     // 顺子
    public static final int BOMB = 130;         // 炸弹
    public static final int ROCKET = 150;       // 王炸
    public static final int SUPER_ROCKET = 200; // 开天王炸


    public static void judgeType(Deck deck) {

        deck.setType(WRONG);

        Map<Integer, Integer> cardsMap = deck.getCardsMap();

        int difNum = cardsMap.size();   // 不同点数牌的数量
        int sumNum = deck.getSumCards();// 总牌数

        //开天王炸
        if ( sumNum==4 && difNum==2 ) {
            int flag = 0;
            for (int points: cardsMap.keySet()) {
                if (points== Poker.LARGE_JOKER || points==Poker.SMALL_JOKER) {
                    flag ++;
                } else {
                    break;
                }
            }
            if (flag==2) {
                deck.setType(SUPER_ROCKET);
                return;
            }
        }

        // 单点数牌
        if (difNum==1) {
            int points = 0;
            for(int key :cardsMap.keySet()) {
                points = key;
            }
            if (sumNum==1) { // 单牌
                deck.setType(SINGLE);
            } else if (sumNum==2) { // 对子 王炸
                if ( points==Poker.SMALL_JOKER || points==Poker.LARGE_JOKER ) {
                    deck.setType(ROCKET);
                } else {
                    deck.setType(PAIR);
                }
            } else if (sumNum==3) {  // 三
                deck.setType(TRIPLET);
            } else {    // 炸弹
                deck.setType(Rule.BOMB + sumNum-4);
            }
            deck.setWeight(points);
            return;
        }

        // 顺子
        if ( sumNum>=5 && sumNum==difNum ) {
            int pre = 0;
            for (int points: cardsMap.keySet()) {
                if (pre==0) {
                    pre = points;
                    continue;
                }
                if (points!=pre+1) {
                    return;
                }
                pre = points;
                deck.setWeight(points);
            }
            deck.setType(Rule.STRAIGHT + difNum);
            return;
        }

        // 三带对
        if ( difNum==2 && sumNum==5 ) {
            boolean flag = true;
            for (int points: cardsMap.keySet()) {
                int num = cardsMap.get(points);
                if (num==2 || num==3) {
                    if (num==3) {
                        deck.setWeight(points);
                    }
                } else {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                deck.setType(TRIPLET_PAIR);
                return;
            }
        }

        // 连对
        if ( difNum*2==sumNum && difNum>=3 ) {
            int pre = 0;
            boolean flag = true;
            for (int points: cardsMap.keySet()) {
                if ( cardsMap.get(points) != 2 ) {
                    flag = false;
                    break;
                }
                if (pre==0) {
                    pre = points;
                    continue;
                }
                if (points!=pre+1) {
                    flag = false;
                    break;
                }
                pre = points;
                deck.setWeight(points);
            }
            if (flag) {
                deck.setType(PAIRS + difNum);
                return;
            }
        }

        //飞机
        if ( difNum*3==sumNum && difNum>=2 ) {
            int pre = 0;
            boolean flag = true;
            for (int points: cardsMap.keySet()) {
                if ( cardsMap.get(points) != 3 ) {
                    flag = false;
                    break;
                }
                if (pre==0) {
                    pre = points;
                    continue;
                }
                if (points!=pre+1) {
                    return;
                }
                pre = points;
                deck.setWeight(points);
            }
            if (flag) {
                deck.setType(TRIPLETS + difNum);
                return;
            }
        }

        // 飞机带对
        if ( sumNum%5==0 && sumNum/5*2==difNum && difNum>=4 ) {
            int pre = 0;
            int triplets = 0;
            int pairs = 0;
            boolean flag = true;
            for (int points: cardsMap.keySet()) {
                int num = cardsMap.get(points);
                if (num==3&&pre==0) {
                    pre = points;
                    triplets ++;
                    continue;
                }
                if (num==3&&points!=pre+1) {
                    flag = false;
                    break;
                }

                if (num==2 || num==3) {
                    if (num==3) {
                        if (points!=pre+1) {
                            flag = false;
                        }
                        deck.setWeight(points);
                        triplets ++;
                        pre = points;
                    } else {
                        pairs ++;
                    }
                } else {
                    flag = false;
                    break;
                }
            }
            if (flag && triplets==pairs) {
                deck.setType(TRIPLETS_PAIR + difNum);
            }
        }
    }
}
