package com.example.a25cards.utils;

import java.util.ArrayList;
import java.util.Collections;
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
    public static final int typeBoom = 1;          // 炸弹
    public static final int typeKingBoom = 2;      // 王炸，四张王
    public static final int typeSingle = 3;        // 单张牌
    public static final int typePair = 4;          // 对子
    public static final int typeThree = 5;         // 三张牌
    public static final int typeThreePair = 6;     // 三带一对
    public static final int typeStraight = 7;      // 顺子
    public static final int typeStraightPair = 8;  // 连对
    public static final int typePlane = 9;         // 飞机
    public static final int typePlanePair = 10;    // 飞机带翅膀（仅能带对子）

    /**
     * 判断该牌所属的牌型，属于正确牌型则返回该牌牌型的对象，错误牌型则返回null
     */
    public static PokerType getType(List<Poker> pokers){
        // 对牌进行排序
        Collections.sort(pokers);
        PokerType pokerType = null;
        if (pokers.size() < 4){
            // 是否为单张牌
            if ((pokerType = isSingle(pokers)) != null) return pokerType;
            // 是否为对子
            if ((pokerType = isPair(pokers)) != null) return pokerType;
            // 是否为三张牌
            if ((pokerType = isThree(pokers)) != null) return pokerType;
        }else {
            // 是否为炸弹
            if ((pokerType = isBoom(pokers)) != null) return pokerType;
            // 是否为王炸
            if ((pokerType = isKingBoom(pokers)) != null) return pokerType;
            // 是否为三带一对
            if ((pokerType = isThreePair(pokers)) != null) return pokerType;
            // 是否为顺子
            if ((pokerType = isStraight(pokers)) != null) return pokerType;
            // 是否为连对
            if ((pokerType = isStraightPair(pokers)) != null) return pokerType;
            // 是否为飞机
            if ((pokerType = isPlane(pokers)) != null) return pokerType;
            // 是否为飞机带翅膀
            if ((pokerType = isPlanePair(pokers)) != null) return pokerType;
        }
        // 全不是则为错误牌型
        return null;
    }

    /**
     * 判断牌型是否为炸弹，是则返回牌型的对象，否则返回null
     */
    public static PokerType isBoom(List<Poker> pokers){
        if (pokers.size() < 4){
            return null;
        }
        // 判断牌是否相同
        for (int i = 1; i < pokers.size(); i++){
            if (pokers.get(i - 1).isSameType(pokers.get(i))){
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
        if (pokers.get(0).isSameType(pokers.get(1))){
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
        if (pokers.get(0).isSameType(pokers.get(1)) && pokers.get(1).isSameType(pokers.get(2))){
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
     * 判断牌型是否为连对，是则返回牌型的对象，否则返回null
     */
    public static PokerType isStraightPair(List<Poker> pokers){
        if (pokers.size() < 6 || pokers.size() % 2 != 0){
            return null;
        }
        for (int i = 0; i < pokers.size(); i+=2){
            // 判断牌是否为对子
            if (!pokers.get(i).isSameType(pokers.get(i + 1))){
                return null;
            }
            // 判断相邻的对子牌大小是否连续
            if (i + 2 < pokers.size()){
                if (pokers.get(i).getOrderValue() - pokers.get(i + 2).getOrderValue() != 1){
                    return null;
                }
            }
        }
        return new PokerType(typeStraightPair, pokers.size(), pokers.get(pokers.size() - 1).getOrderValue());
    }

    /**
     * 判断牌型是否为飞机，是则返回牌型的对象，否则返回null
     */
    public static PokerType isPlane(List<Poker> pokers){
        if (pokers.size() < 6 || pokers.size() % 3 != 0){
            return null;
        }
        for (int i = 0; i < pokers.size(); i +=3){
            // 判断是否为三张同类型的牌
            if (!pokers.get(i).isSameType(pokers.get(i + 1)) || !pokers.get(i).isSameType(pokers.get(i + 2))){
                return null;
            }
            // 判断相邻的三张牌大小是否连续
            if (i + 3 < pokers.size()){
                if (pokers.get(i).getOrderValue() - pokers.get(i + 3).getOrderValue() != 1){
                    return null;
                }
            }
        }
        return new PokerType(typePlane, pokers.size(), pokers.get(pokers.size() - 1).getOrderValue());
    }

    /**
     * 判断牌型是否为飞机带翅膀，是则返回牌型的对象，否则返回null
     */
    public static PokerType isPlanePair(List<Poker> pokers){
        if (pokers.size() < 10 || pokers.size() % 5 != 0){
            return null;
        }
        Map<Integer, Integer> kind = getKindMap(pokers);
        int pair = 0;// 对子的数量
        int three = 0;// 三张牌的数量
        List<Integer> threeList = new ArrayList<>();// 存放三张牌牌的类型
        for (Map.Entry<Integer, Integer> entry:kind.entrySet()){
            if (entry.getValue() == 2){
                pair++;
            }else if (entry.getValue() == 3){
                three++;
                threeList.add(entry.getKey());
            }
        }
        // 对子的数量与三张牌的数量相等并且等于牌的数量除以5
        if (pair == three && pair == pokers.size() / 5){
            // 将三张牌牌的类型进行排序
            Collections.sort(threeList);
            // 判断牌的大小是否连续
            for (int i = 0; i < threeList.size() - 1; i++){
                if (threeList.get(i + 1) - threeList.get(i) != 1){
                    return null;
                }
            }
            return new PokerType(typePlanePair, pokers.size(), threeList.get(threeList.size() - 1));
        }
        return null;
    }

    /**
     * 获取牌的种类以及其对应牌数量的map
      */
    public static Map<Integer, Integer> getKindMap(List<Poker> pokers){
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
