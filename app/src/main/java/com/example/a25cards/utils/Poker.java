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
        return this.id == poker.id;
    }

    /**
     * 判断是否为同一类型的牌
     */
    public boolean isSameType(Poker poker){
        return this.orderValue == poker.id;
    }

    @Override
    // 实现Comparable接口的方法，用于排序
    public int compareTo(Poker poker) {
        // 同类型的牌按黑桃、红桃、梅花、方块的顺序排序
        if (this.orderValue == poker.orderValue){
            return this.id - poker.id;
        }
        return poker.orderValue - this.orderValue;
    }

    /**
     * 获取牌id对应的牌名
     */
    public String getPoker(){
        StringBuilder pokerName = new StringBuilder();
        if (this.id == 53){
            pokerName.append("小王");
        }else if (this.id == 54){
            pokerName.append("大王");
        }else {
            // 牌的种类
            String[] kind = {"黑桃", "红桃", "梅花", "方块"};
            String[] name = {"J", "Q", "K", "A", "2"};
            pokerName.append(kind[this.id % 4]);
            if (this.orderValue <= 8){
                pokerName.append(this.orderValue + 2);
            }else {
                pokerName.append(name[this.orderValue - 8]);
            }
        }
        return new String(pokerName);
    }
}
