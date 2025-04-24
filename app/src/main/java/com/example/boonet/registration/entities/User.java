package com.example.boonet.registration.entities;


import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class User implements Cloneable, Comparable<User> {
    private String userName;
    private String userEmail;
    private String key;
    private String UID;
    private String avatar;
    private UserType userType = UserType.READER;
    public User() {
    }

    public User(String userName, String userEmail, String key, String UID, String avatar, UserType userType) {
        this.userName = userName;
        this.userEmail = userEmail;
        this.key = key;
        this.UID = UID;
        this.avatar = avatar;
        this.userType = userType;
    }

    public User(String userName) {
        this.userName = userName;
    }

    public User(String userName, String email, String UID) {
        this.userName = userName;
        this.userEmail = email;
        this.UID = UID;
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    @NotNull
    @Override
    public User clone() {
        try {
            User cloned = (User) super.clone();
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Клонирование не поддерживается", e);
        }
    }

    @Override
    public int compareTo(@NotNull User other) {
        return this.userName.compareToIgnoreCase(other.userName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(UID, user.UID) &&
                Objects.equals(userEmail, user.userEmail);
    }

    @Override
    public int hashCode() {
        return Objects.hash(UID, userEmail);
    }

    /* Для удобного вывода */
    @Override
    public String toString() {
        return "User{" +
                "name='" + userName + '\'' +
                ", email='" + userEmail + '\'' +
                ", type=" + userType +
                '}';


    }
}
