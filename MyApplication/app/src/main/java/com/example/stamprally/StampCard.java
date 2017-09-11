package com.example.stamprally;

/**
 * Created by mb-347 on 2017/09/10.
 */

public class StampCard {
    String cardName;
    int num;
    int cardId[];
    String description;
    String userName;
    public StampCard(String cardName,int num,int cardId[],String userName,String description){
        this.cardName=cardName;
        this.num=num;
        this.cardId=cardId;
        this.description=description;
        this.userName=userName;
    }

}
