package com.example.impressionapp;

public class PersonClass {
    private String fullName, imageURL;
    private boolean isSelected;

    //constructor for settings impression person selection
    public PersonClass(String fullName, String imageURL, boolean isSelected) {
        this.fullName = fullName;
        this.imageURL = imageURL;
        this.isSelected = isSelected;
    }
    //constructor for impression person in game
    public PersonClass(String fullName, String imageURL) {
        this.fullName = fullName;
        this.imageURL = imageURL;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
