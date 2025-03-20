package com.example.boonet.Registration.Model;


import org.jetbrains.annotations.NotNull;

public class User implements Cloneable {
    private String userName;
    private String userEmail;
    private String key;
    private String UID;
    private String avatar;
    private UserType userType = UserType.READER;

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

    public User() {
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
    protected Object clone() throws CloneNotSupportedException {
        super.clone();
        return new User(this.userName);
    }
}
