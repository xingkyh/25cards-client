package com.example.a25cards.utils;

import java.util.ArrayList;
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
            }
        }
        return proposal;
    }
}
