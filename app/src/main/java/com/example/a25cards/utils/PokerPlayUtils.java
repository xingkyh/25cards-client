package com.example.a25cards.utils;

import java.util.List;

/**
 * @author xingkyh
 * @version 1.0
 * @date 2020/11/22
 * 封装判断是否能出牌，以及自动出牌等方法的工具类
 */
public class PokerPlayUtils {
    /**
     * 比较俩手牌大小
     * 当a > b时返回true
     * 王炸通吃，炸弹吃除比自身大的炸弹外所有的牌型
     * 其它牌型需相同才能比较
     */
    public static boolean comparePokers(List<Poker> a, List<Poker> b){
        PokerType aType = PokerTypeUtils.getType(a);
        if (aType == null) return false;// a为错误牌型
        if (aType.getType() == PokerTypeUtils.typeKingBoom) return true;// a为王炸
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
}
