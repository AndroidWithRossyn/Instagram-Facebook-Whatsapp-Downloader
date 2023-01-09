package com.allmy.allstatusdownloader.Model;

import java.io.Serializable;

public class Downloadlist implements Serializable {
    private String code_HdUrl;
    private String imageUrl;
    private String media_title;
    private String type;
    private String videoUrl;

    public Downloadlist(String str, String str2, String str3, String str4, String str5) {
        this.imageUrl = str;
        this.videoUrl = str2;
        this.type = str3;
        this.code_HdUrl = str4;
        this.media_title = str5;
    }

    public String getMedia_title() {
        return this.media_title;
    }

    public void setMedia_title(String str) {
        this.media_title = str;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public void setImageUrl(String str) {
        this.imageUrl = str;
    }

    public String getVideoUrl() {
        return this.videoUrl;
    }

    public void setVideoUrl(String str) {
        this.videoUrl = str;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String str) {
        this.type = str;
    }

    public String getCode_HdUrl() {
        return this.code_HdUrl;
    }

    public void setCode_HdUrl(String str) {
        this.code_HdUrl = str;
    }
}
