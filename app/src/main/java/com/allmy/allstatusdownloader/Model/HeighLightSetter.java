package com.allmy.allstatusdownloader.Model;

import java.io.Serializable;

public class HeighLightSetter implements Serializable {
    private String heighlight_cover;
    private String heighlight_id;
    private String userTitle;

    public String getHeighlight_id() {
        return this.heighlight_id;
    }

    public void setHeighlight_id(String str) {
        this.heighlight_id = str;
    }

    public String getUserTitle() {
        return this.userTitle;
    }

    public void setUserTitle(String str) {
        this.userTitle = str;
    }

    public String getHeighlight_cover() {
        return this.heighlight_cover;
    }

    public void setHeighlight_cover(String str) {
        this.heighlight_cover = str;
    }
}
