package com.example.boonet.core.entities;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Objects;

public class Book implements Serializable, Cloneable {
    private String image;
    private String title;
    private String description;
    private String key;
    private String ownerUID;
    private int price;
    private long creationDate;
    private String author;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getOwnerUID() {
        return ownerUID;
    }

    public void setOwnerUID(String ownerUID) {
        this.ownerUID = ownerUID;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(long creationDate) {
        this.creationDate = creationDate;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Book() {}

    public Book(String image, String title, String description, String key, String ownerUID, int price, long creationDate, String author) {
        this.image = image;
        this.title = title;
        this.description = description;
        this.key = key;
        this.ownerUID = ownerUID;
        this.price = price;
        this.creationDate = creationDate;
        this.author = author;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return key.equals(book.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }


    @NonNull
    @Override
    protected Object clone() throws CloneNotSupportedException {
        super.clone();

        return new Book(this.image,
                this.title,
                this.description,
                this.key,
                this.ownerUID,
                this.price,
                this.creationDate,
                this.author
        );
    }
}
