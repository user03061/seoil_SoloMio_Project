package com.example.re_solomio;

public class DesignData {
    private String designName;
    private String imagePath;

    public DesignData(String designName, String imagePath) {
        this.designName = designName;
        this.imagePath = imagePath;
    }

    public String getDesignName() {
        return designName;
    }

    public String getImagePath() {
        return imagePath;
    }
}