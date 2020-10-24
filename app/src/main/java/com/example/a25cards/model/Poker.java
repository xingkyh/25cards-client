package com.example.a25cards.model;


import com.example.a25cards.ui.CardSlideThread;

public class Poker {

    private int points;
    private String kind;
    public static int SMALL_JOKER = 100;
    public static int LARGE_JOKER = 200;
    public static int JACK = 11;
    public static int QUEEN = 12;
    public static int KING = 13;
    public static int ACE = 14;
    public static int TWO = 16;
    private boolean selected = false;
    public static int[] Pokers = {3, 4, 5, 6, 7, 8, 9, 10,
            JACK, QUEEN, KING, ACE, TWO, SMALL_JOKER, LARGE_JOKER};
    public static String[] kinds = {"方块", "梅花", "红桃", "黑桃"};

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int getPoints() {
        return points;
    }

    public String getKind() {
        return kind;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public Poker(int point, String kind) {
        this.kind = kind;
        this.points = point;
    }

    @Override
    public String toString() {
        return kind+"aa"+String.valueOf(points);
    }

}
