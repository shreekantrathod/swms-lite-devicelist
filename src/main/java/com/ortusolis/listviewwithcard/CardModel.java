package com.ortusolis.listviewwithcard;



public class CardModel {
    private int imageId;
    private String title;
    private String subtitle;



    public CardModel(int imageId, String title) {
        this.imageId = imageId;
        this.title = title;

    }

    public CardModel() {
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitleId(String titleId) {
        this.title = titleId;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }
}
