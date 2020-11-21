package com.example.a25cards.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    /**
     * 判断牌型是否为单张，是则返回牌型的对象，否则返回null
     */
    public static PokerType isSingle(List<Poker> pokers){
        if (pokers.size() != 1){
            return null;
        }
        return new PokerType(typeSingle, 1, pokers.get(0).getOrderValue());
    }

    /**
     * 判断牌型是否为对子，是则返回牌型的对象，否则返回null
     */
    public static PokerType isPair(List<Poker> pokers){
        if (pokers.size() != 2){
            return null;
        }
        // 是否为同一类型的牌
        if (pokers.get(0).equals(pokers.get(1))){
            return new PokerType(typePair, 2, pokers.get(0).getOrderValue());
        }
        return null;
    }

    /**
     * 判断牌型是否为三张牌，是则返回牌型的对象，否则返回null
     */
    public static PokerType isThree(List<Poker> pokers){
        if (pokers.size() != 3){
            return null;
        }
        // 三张牌是否为同一类型的牌
        if (pokers.get(0).equals(pokers.get(1)) && pokers.get(1).equals(pokers.get(2))){
            return new PokerType(typeThree, 3, pokers.get(0).getOrderValue());
        }
        return null;
    }

    /**
     * 判断牌型是否为三带一对，是则返回牌型的对象，否则返回null
     */
    public static PokerType isThreePair(List<Poker> pokers){
        if (pokers.size() != 5){
            return null;
        }
        Map<Integer, Integer> kind = getKindMap(pokers);// 获取牌的种类以及其对应的数量
        int value = 0;
        int pair = 0;// 两张牌的数量
        int three = 0;// 三张牌的数量
        for (Map.Entry<Integer, Integer> entry:kind.entrySet()){
            if (entry.getValue() == 2){
                pair++;
            }else if (entry.getValue() == 3){
                three++;
                value = entry.getKey();
            }
        }
        if (pair == 1 && three == 1){
            return new PokerType(typeThreePair, 5, value);
        }
        return null;
    }

    /**
     * 判断牌型是否为顺子，是则返回牌型的对象，否则返回null
     */
    public static PokerType isStraight(List<Poker> pokers){
        if (pokers.size() < 5 || pokers.size() > 13){
            return null;
        }
        for (int i = 1; i < pokers.size(); i++){
            // 存在大小王
            if (pokers.get(i).getOrderValue() >= 14){
                return null;
            }
            // 除第一张牌外，每张牌要比前一张牌小一
            if (pokers.get(i - 1).getOrderValue() - pokers.get(i).getOrderValue() != 1){
                return null;
            }
        }
        return new PokerType(typeStraight, pokers.size(), pokers.get(pokers.size() - 1).getOrderValue());
    }

    /**
     * 获取牌的种类以及其对应牌数量的map
      */
    private static Map<Integer, Integer> getKindMap(List<Poker> pokers){
        Map<Integer, Integer> kind = new HashMap<>();
        for (Poker poker:pokers){
            if (kind.containsKey(poker.getOrderValue())){
                kind.put(poker.getOrderValue(), kind.get(poker.getOrderValue()) + 1);
            }else {
                kind.put(poker.getOrderValue(), 1);
            }
        }
        return kind;
    }
}
