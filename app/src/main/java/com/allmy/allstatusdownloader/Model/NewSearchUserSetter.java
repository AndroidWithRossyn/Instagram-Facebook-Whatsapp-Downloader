package com.allmy.allstatusdownloader.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class NewSearchUserSetter implements Serializable {
    private String ID;
    ArrayList<HashMap<String, String>> data = new ArrayList<>();
    private String getDate;
    private String image_height;
    private String image_url;
    private String image_width;
    private String latest_reel_media;
    String profile_picture;
    private String type;
    private String userFullName;
    String userName;
    private String video_url;

    public String getUserFullName() {
        return this.userFullName;
    }

    public void setUserFullName(String str) {
        this.userFullName = str;
    }

    public String getGetDate() {
        return this.getDate;
    }

    public void setGetDate(String str) {
        this.getDate = str;
    }

    public void setLatest_reel_media(String str) {
        this.latest_reel_media = str;
    }

    public String getLatest_reel_media() {
        return this.latest_reel_media;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String str) {
        this.userName = str;
    }

    public String getUserID() {
        return this.ID;
    }

    public void setUserID(String str) {
        this.ID = str;
    }

    public String getProfile_picture() {
        return this.profile_picture;
    }

    public void setProfile_picture(String str) {
        this.profile_picture = str;
    }

    public String getvideourl() {
        return this.video_url;
    }

    public void setvideourl(String str) {
        this.video_url = str;
    }

    public String getimageurl() {
        return this.image_url;
    }

    public void setimageurl(String str) {
        this.image_url = str;
    }

    public String getimageheight() {
        return this.image_height;
    }

    public void setimageheight(String str) {
        this.image_height = str;
    }

    public String getimagewidth() {
        return this.image_width;
    }

    public void setimagewidth(String str) {
        this.image_width = str;
    }
}
