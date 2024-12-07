package com.gcu.jobshorts;

import java.util.Objects;

public class CardModel {
    private String title;
    private String amount;
    private int image;

    public CardModel(String title, String amount, int image){
        this.title = title;
        this.amount = amount;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    @Override
    public String toString(){
        return "CardModel{" +
                "title = '" + title + '\'' +
                ", amount = '" + amount + '\'' +
                ", image = " + image +
                '}';
    }

    @Override
    public boolean equals(Object o){
        if(this == o)
            return true;

        if(o == null || getClass() != o.getClass())
            return false;

        CardModel cardModel = (CardModel) o;
        return image == cardModel.image && Objects.equals(title, cardModel.title) && Objects.equals(amount, cardModel.amount);
    }

    @Override
    public int hashCode(){
        return Objects.hash(title, amount, image);
    }
}
