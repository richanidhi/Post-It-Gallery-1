package com.akshaybaweja.postitgallery.model;

import java.io.Serializable;

public class Image implements Serializable{
    private String name;
    private String image;
    private String timestamp;

    public Image() {
    }

    public Image(String name, String image, String timestamp) {
        this.name = name;
        this.image = image;
        this.timestamp = timestamp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage()
    {
        return image;
    }

    public void setImage(String image)
    {
        this.image=image;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
