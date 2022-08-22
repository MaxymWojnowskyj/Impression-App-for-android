package com.example.impressionapp;

public class PlayerClass {

    private String iconURL;
    private String userName;

    public PlayerClass(String iconURL, String userName) {
        this.iconURL = iconURL;
        this.userName = userName;
    }

    public String getIconURL() {
        return iconURL;
    }

    public void setIconURL(String iconURL) {
        this.iconURL = iconURL;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}