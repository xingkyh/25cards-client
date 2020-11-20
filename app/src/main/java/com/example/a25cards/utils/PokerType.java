package com.example.a25cards.utils;

/**
 * @author xingkyh
 * @version 1.0
 * @date 2020/11/20
 * 牌类型对象类
 */
public class PokerType {
    // 牌型
    private int type;
    // 牌的数量
    private  int num;
    // 牌型的大小排序值
    private int value;

    public PokerType(int type, int num, int value){
        this.type=type;
        this.num=num;
        this.value=value;
    }

    public int getType() {
        return type;
    }

    public int getNum() {
        return num;
    }

    public int getValue() {
        return value;
    }
}
