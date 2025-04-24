package com.example.boonet.core.entities;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Objects;

public class Book implements Serializable, Cloneable {
    private String imageBase64;
    private String title;
    private String description;
    private String key;
    private String ownerUID;
    private boolean subscription;
    private long creationDate;
    private String author;



    public Book() {}

    public Book( String title,String imageBase64, String description, String key, String ownerUID, boolean subscription, long creationDate, String author) {
        this.title = title;
        this.description = description;
        this.key = key;
        this.ownerUID = ownerUID;
        this.subscription = subscription;
        this.creationDate = creationDate;
        this.author = author;
    }

    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
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

    public boolean isSubscription() {
        return subscription;
    }

    public void setSubscription(boolean subscription) {
        this.subscription = subscription;
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
        return new Book(this.imageBase64, this.title, this.description, this.key, this.ownerUID, this.subscription, this.creationDate, this.author);
    }
}
