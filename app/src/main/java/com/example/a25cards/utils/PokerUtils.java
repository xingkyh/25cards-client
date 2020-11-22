package com.example.a25cards.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author xingkyh
 * @version 1.0
 * @date 2020/11/20
 * 用于生成牌的工具类
 */
public class PokerUtils {
    // 存放牌的大小的list
    private static List pokerSortValues = Arrays.asList(new int[]{
            1,1,1,1,// 3
            2,2,2,2,// 4
            3,3,3,3,// 5
            4,4,4,4,// 6
            5,5,5,5,// 7
            6,6,6,6,// 8
            7,7,7,7,// 9
            8,8,8,8,// 10
            9,9,9,9,// J
            10,10,10,10,// Q
            11,11,11,11,// K
            12,12,12,12,// A
            13,13,13,13,// 2
            14,15// 小王，大王
    });
    // 大于所有牌的大小的常量值，后续需要用到
    public static final int MAX = 16;
    // 小于所有牌的大小的常量值，后续需要用到
    public static final int MIN = 0;

    /**
     * 随机生成两副牌
     * @return 生成的牌
     */
    public static List<Poker> getRandomPokers(){
        // 用于存放最终生成的牌
        List<Poker> pokers = new ArrayList<>();
        // 随机生成的牌的id
        List<Integer> pokerId = new ArrayList<>();
        for(int i = 1; i <= 54; i++){
            pokerId.add(i);
            pokerId.add(i);
        }
        // 随机打乱list
        Collections.shuffle(pokerId);
        // 生成牌的类，并放入list中
        for(int id:pokerId){
            Poker poker = new Poker(id, (Integer) pokerSortValues.get(id - 1));
            pokers.add(poker);
        }
        return pokers;
    }
}
