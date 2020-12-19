package com.example.a25cards.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author xingkyh
 * @version 1.0
 * @date 2020/11/22
 * 封装判断是否能出牌，以及自动出牌等方法的工具类
 */
public class PokerPlayUtils {
    /**
     * 比较俩手牌大小
     * 当a > b，或a为正确牌型b为null时返回true
     * 王炸通吃，炸弹吃除比自身大的炸弹外所有的牌型
     * 其它牌型需相同才能比较
     */
    public static boolean comparePokers(List<Poker> a, List<Poker> b){
        PokerType aType = PokerTypeUtils.getType(a);
        if (aType == null) return false;// a为错误牌型
        if (aType.getType() == PokerTypeUtils.typeKingBoom) return true;// a为王炸
        if (b == null) return true;// b为null
        PokerType bType = PokerTypeUtils.getType(b);
        if (bType == null) return false;// b为错误牌型
        if (bType.getType() == PokerTypeUtils.typeKingBoom) return false;// b为王炸
        // a b都是炸弹
        if (aType.getType() == bType.getType() && aType.getType() == PokerTypeUtils.typeBoom){
            // a b的牌数量相同
            if (aType.getNum() == bType.getNum()){
                return aType.getValue() > bType.getValue();
            }
            // 牌数量不同则比较牌数量
            return aType.getNum() > bType.getNum();
        }
        // a为炸弹
        if (aType.getType() == PokerTypeUtils.typeBoom) return true;
        // b为炸弹
        if (bType.getType() == PokerTypeUtils.typeBoom) return false;
        // a b牌型相同
        if (aType.getType() == bType.getType()){
            // a b牌数量相同
            if (aType.getNum() == bType.getNum()){
                return aType.getValue() > bType.getValue();
            }
            // 牌数量不同不能比较
            return false;
        }
        // a b牌型不同且都不是炸弹则不能比较
        return false;
    }

    /**
     * 查找能压单张牌的牌的方法，找到则返回推荐牌型，未找到则返回null
     * @param pokers 当前手牌
     * @param pokerType 要压的牌型
     */
    private static List<Poker> seekSingle(List<Poker> pokers, PokerType pokerType){
        // 最大牌比要压的牌小
        if (pokers.get(0).getOrderValue() < pokerType.getValue()) {
            return null;
        }
        List<Poker> proposal = new ArrayList<>();// 推荐牌
        Poker poker = pokers.get(0);// 能压上家牌的最小牌
        // 遍历手牌，寻找能压上家牌的最小牌
        for (Poker p:pokers){
            if (p.getOrderValue() > pokerType.getValue()){
                if (p.getOrderValue() < poker.getOrderValue()){
                    poker = p;
                }
            }else {
                break;
            }
        }
        proposal.add(poker);
        return proposal;
    }

    /**
     * 查找能压对子的牌的方法，找到则返回推荐牌型，未找到则返回null
     * @param pokers 当前手牌
     * @param pokerType 要压的牌型
     */
    private static List<Poker> seekPair(List<Poker> pokers, PokerType pokerType){
        // 获取牌的种类以及其对应的数量
        Map<Integer, Integer> kind = PokerTypeUtils.getKindMap(pokers);
        int value = PokerUtils.MAX;// 能够压上家牌的最小类型的值
        for (Map.Entry<Integer, Integer> entry:kind.entrySet()){
            // 牌数量不少于2的类型
            if (entry.getValue() >= 2){
                int type = entry.getKey();
                if (type > pokerType.getValue() && type < value){
                    value = type;
                }
            }
        }
        // 未找到
        if (value == PokerUtils.MAX){
            return null;
        }
        List<Poker> proposal = new ArrayList<>();
        // 从手牌中找到对应的牌
        for (int i = 0; i < pokers.size(); i++){
            if (pokers.get(i).getOrderValue() == value){
                proposal.add(pokers.get(i));
                proposal.add(pokers.get(i + 1));
                break;
            }
        }
        return proposal;
    }

    /**
     * 判断是否有炸弹能压当前的牌型
     * @param pokers 当前手牌
     * @param pokerType 要压制的牌型，若为null则代表该牌型不为炸弹
     */
    private static List<Poker> seekBoom(List<Poker> pokers, PokerType pokerType){
        if (pokers.size() < 4){
            return null;
        }
        // 非炸弹牌型视为一个比最小的炸弹还小的炸弹
        if (pokerType == null){
            pokerType = new PokerType(PokerTypeUtils.typeBoom, 4, PokerUtils.MIN);
        }
        Map<Integer, Integer> kind = PokerTypeUtils.getKindMap(pokers);
        int value = PokerUtils.MAX;// 能压该牌型的牌的类型
        int num = 9;// 该牌的数量（炸弹最多为8张，故初始设为9）
        // 寻找能压该炸弹的最小牌型
        for (Map.Entry<Integer, Integer> entry:kind.entrySet()){
            // 该牌的数量于要压的炸弹牌数相等
            if (entry.getValue() == pokerType.getNum()){
                // 该牌比要压的炸弹的牌大，但比之前选出的能压的牌小
                if (entry.getKey() > pokerType.getValue() && entry.getKey() < value){
                    value = entry.getKey();
                    num = entry.getValue();
                }
            // 该牌的数量比要压的炸弹的牌数大
            }else if (entry.getValue() > pokerType.getNum()){
                // 该牌的大小比要压的炸弹的牌大
                if (entry.getKey() > pokerType.getValue()){
                    // 大小比之前推荐牌小
                    if (entry.getKey() < value){
                        value = entry.getKey();
                        num = pokerType.getNum();
                    }
                }else {// 该牌的大小比要压的炸弹的牌小
                    // 之前推荐的牌的数量比要压的炸弹的牌数大，且大小比当前牌大
                    if (num > pokerType.getNum() && entry.getKey() < value){
                        value = entry.getKey();
                        num = pokerType.getNum() + 1;
                    }
                }
            }
        }
        // 未找到
        if (value == PokerUtils.MAX){
            return null;
        }
        List<Poker> proposal = new ArrayList<>();
        // 从手牌中找到相应的牌
        for (int i = 0; i < pokers.size(); i++){
            if (pokers.get(i).getOrderValue() == value){
                for (int j = 0; j < num; j++){
                    proposal.add(pokers.get(i + j));
                }
                break;
            }
        }
        return proposal;
    }

    /**
     * 查找能压三张牌的牌的方法，找到则返回推荐牌型，未找到则返回null
     * @param pokers 当前手牌
     * @param pokerType 要压的牌型
     */
    private static List<Poker> seekThree(List<Poker> pokers, PokerType pokerType){
        if (pokers.size() < 3){
            return null;
        }
        Map<Integer, Integer> kind = PokerTypeUtils.getKindMap(pokers);
        int value = PokerUtils.MAX;
        // 寻找牌
        for (Map.Entry<Integer, Integer> entry:kind.entrySet()){
            if (entry.getValue() >= 3 && entry.getKey() < value && entry.getKey() > pokerType.getValue()){
                value = entry.getKey();
            }
        }
        // 未找到
        if (value == PokerUtils.MAX){
            return null;
        }
        List<Poker> proposal = new ArrayList<>();
        // 从手牌中找到相应的牌
        for (int i = 0; i < pokers.size(); i++){
            if (pokers.get(i).getOrderValue() == value){
                for (int j = 0; j < 3; j++){
                    proposal.add(pokers.get(i + j));
                }
                break;
            }
        }
        return proposal;
    }

    /**
     * 查找能压三带一对的牌的方法，找到则返回推荐牌型，未找到则返回null
     * @param pokers 当前手牌
     * @param pokerType 要压的牌型
     */
    private static List<Poker> seekThreePair(List<Poker> pokers, PokerType pokerType){
        if (pokers.size() < 5){
            return null;
        }
        Map<Integer, Integer> kind = PokerTypeUtils.getKindMap(pokers);
        int threeValue = PokerUtils.MAX;// 三张牌的牌类型
        int pairVale = PokerUtils.MAX;// 对子的牌类型
        // 寻找牌
        for (Map.Entry<Integer, Integer> entry:kind.entrySet()){
            int value = entry.getValue();
            int key = entry.getKey();
            // 寻找三张牌的牌类型
            if (value >= 3 && key < threeValue && key > pokerType.getValue()){
                threeValue = entry.getKey();
            }
            // 三张牌的牌类型与对子的牌类型应不为同一种
            if (value >= 2 && key != threeValue && key < pairVale){
                pairVale = entry.getKey();
            }
        }
        // 未找到
        if (threeValue == PokerUtils.MAX || pairVale == PokerUtils.MAX){
            return null;
        }
        List<Poker> proposal = new ArrayList<>();
        // 从手牌中找到相应的牌
        for (int i = 0; i < pokers.size(); i++){
            if (pokers.get(i).getOrderValue() == threeValue && proposal.size() < 3){
                proposal.add(pokers.get(i));
            }
            if (pokers.get(i).getOrderValue() == pairVale && proposal.size() < 2){
                proposal.add((pokers.get(i)));
            }
        }
        return proposal;
    }

    /**
     * 查找能压顺子的牌的方法，找到则返回推荐牌型，未找到则返回null
     * @param pokers 当前手牌
     * @param pokerType 要压的牌型
     */
    private static List<Poker> seekStraight(List<Poker> pokers, PokerType pokerType){
        if (pokers.size() < pokerType.getNum()){
            return null;
        }
        List<Poker> proposal = new ArrayList<>();
        int continuity = 0;// 牌类型连续数
        int value = PokerUtils.MIN;// 上一种牌类型
        for (int i = pokers.size() - 1; i >= 0; i--){
            Poker poker = pokers.get(i);
            if (poker.getOrderValue() > pokerType.getValue()){
                // 牌类型连续
                if (poker.getOrderValue() - value == 1){
                    continuity++;
                    proposal.add(poker);
                }else if (poker.getOrderValue() == value){// 牌类型相同
                    continue;
                }else {// 牌类型不连续
                    // 连续数重置为1
                    continuity = 1;
                    // 删除已添加的牌
                    proposal.clear();
                    // 添加当前牌
                    proposal.add(poker);
                }
                // 牌已符合要求
                if (continuity == pokerType.getNum()){
                    break;
                }
            }
        }
        // 牌数量不足
        if (proposal.size() < pokerType.getNum()){
            return null;
        }
        return proposal;
    }

    /**
     * 查找能压连对的牌的方法，找到则返回推荐牌型，未找到则返回null
     * @param pokers 当前手牌
     * @param pokerType 要压的牌型
     */
    private static List<Poker> seekStraightPair(List<Poker> pokers, PokerType pokerType){
        if (pokers.size() < 6){
            return null;
        }
        Map<Integer, Integer> kind = PokerTypeUtils.getKindMap(pokers);
        List<Integer> pairList = new ArrayList<>();// 不少于两张牌的类型
        for (Map.Entry<Integer, Integer> entry:kind.entrySet()){
            if (entry.getValue() >= 2){
                pairList.add(entry.getKey());
            }
        }
        // 排序
        Collections.sort(pairList);
        return null;
    }
}
