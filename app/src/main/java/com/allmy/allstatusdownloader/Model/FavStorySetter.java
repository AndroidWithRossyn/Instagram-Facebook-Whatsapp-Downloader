package com.allmy.allstatusdownloader.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class FavStorySetter implements Serializable {
    private String ID;
    ArrayList<HashMap<String, String>> data = new ArrayList<>();
    private String profile_picture;
    private String timeAgo;
    private String userFullName;
    String userName;

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String str) {
        this.userName = str;
    }

    public String getID() {
        return this.ID;
    }

    public void setID(String str) {
        this.ID = str;
    }

    public String getProfile_picture() {
        return this.profile_picture;
    }

    public void setProfile_picture(String str) {
        this.profile_picture = str;
    }

    public String getTimeAgo() {
        return this.timeAgo;
    }

    public void setTimeAgo(String str) {
        this.timeAgo = str;
    }

    public String getUserFullName() {
        return this.userFullName;
    }

    public void setUserFullName(String str) {
        this.userFullName = str;
    }
}
