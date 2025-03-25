package com.example.boonet.addCard.entities;

public class Card {

    private int numberCard;
    private int validThru;
    private String name;
    private int cvv;

    public  Card(){}
    public Card(int numberCard, int validThru, String name, int cvv) {
        this.numberCard = numberCard;
        this.validThru = validThru;
        this.name = name;
        this.cvv=cvv;
    }

    public int getNumberCard() {
        return numberCard;
    }

    public void setNumberCard(int numberCard) {
        this.numberCard = numberCard;
    }

    public int getValidThru() {
        return validThru;
    }

    public void setValidThru(int validThru) {
        this.validThru = validThru;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCvv() {
        return cvv;
    }

    public void setCvv(int cvv) {
        this.cvv = cvv;
    }
}
