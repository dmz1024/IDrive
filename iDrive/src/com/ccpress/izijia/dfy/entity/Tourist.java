package com.ccpress.izijia.dfy.entity;

import java.io.Serializable;

/**
 * Created by dmz1024 on 2016/3/27.
 */
public class Tourist implements Serializable{

    private String name;
    private String card;
    private String cardNum;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCard() {
        return card;
    }

    public void setCard(String card) {
        this.card = card;
    }

    public String getCardNum() {
        return cardNum;
    }

    public void setCardNum(String cardNum) {
        this.cardNum = cardNum;
    }

}
