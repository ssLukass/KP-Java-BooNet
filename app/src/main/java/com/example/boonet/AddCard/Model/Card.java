package com.example.boonet.AddCard.Model;

public class Card {
    private int numberCard;
    private int validThru;
    private String name;
    private int cvv;

    public Card(int numberCard, int validThru, String name, int cvv) {
        this.numberCard = numberCard;
        this.validThru = validThru;
        this.name = name;
        this.cvv=cvv;
    }

    public Card(){}


}
