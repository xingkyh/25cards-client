package com.example.a25cards.utils;

/**
 * @author xingkyh
 * @version 1.0
 * @date 2020/11/20
 * 牌的对象类
 */
public class Poker implements Comparable<Poker>{
    // 牌的id，每张牌都有唯一对应的id
    private int id;
    // 牌的大小排序值，用于手牌排序以及牌的大小比较
    private int orderValue;

    public Poker(int id, int orderValue){
        this.id = id;
        this.orderValue = orderValue;
    }

    public int getId() {
        return id;
    }

    public int getOrderValue() {
        return orderValue;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setOrderValue(int orderValue) {
        this.orderValue = orderValue;
    }

    @Override
    public boolean equals(Object o){
        Poker poker = (Poker)o;
        if(poker.id == this.id){
            return true;
        }
        return false;
    }

    @Override
    // 实现Comparable接口的方法，用于排序
    public int compareTo(Poker poker) {
        return this.orderValue - poker.orderValue;
    }
}
