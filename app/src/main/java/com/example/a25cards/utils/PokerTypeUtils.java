package com.example.a25cards.utils;

import java.util.List;

/**
 * @author xingkyh
 * @version 1.0
 * @date 2020/11/20
 * 判断牌型的工具类
 */
public class PokerTypeUtils {
    // 定义牌型
    private static final int typeBoom = 1;          // 炸弹
    private static final int typeKingBoom = 2;      // 王炸，四张王
    private static final int typeSingle = 3;        // 单张牌
    private static final int typePair = 4;          // 对子
    private static final int typeThree = 5;         // 三张牌
    private static final int typeThreePair = 6;     // 三带一对
    private static final int typeStraight = 7;      // 顺子
    private static final int typeStraightPair = 8;  // 连对
    private static final int typePlane = 9;         // 飞机
    private static final int typePlanePair = 10;    // 飞机带翅膀（仅能带对子）

    /**
     * 判断牌型是否为炸弹，是则返回牌型的对象，否则返回null
     */
    public static PokerType isBoom(List<Poker> pokers){
        if (pokers.size() < 4){
            return null;
        }
        // 判断牌是否相同
        for (int i = 1; i < pokers.size(); i++){
            if (pokers.get(i - 1).equals(pokers.get(i))){
                return null;
            }
        }
        return new PokerType(typeBoom, pokers.size(), pokers.get(0).getOrderValue());
    }

    /**
     * 判断牌型是否为王炸，是则返回牌型的对象，否则返回null
     */
    public static PokerType isKingBoom(List<Poker> pokers){
        if (pokers.size() != 4){
            return null;
        }
        int pokerId1 = pokers.get(0).getId();
        int pokerId2 = pokers.get(1).getId();
        int pokerId3 = pokers.get(2).getId();
        int pokerId4 = pokers.get(3).getId();
        // 判断牌是否为两张大王和两张小王
        if (pokerId1 == pokerId2 && pokerId3 == pokerId4 && pokerId1 == 53 && pokerId4 == 54){
            return new PokerType(typeKingBoom, pokers.size(), 15);
        }
        return null;
    }
}
