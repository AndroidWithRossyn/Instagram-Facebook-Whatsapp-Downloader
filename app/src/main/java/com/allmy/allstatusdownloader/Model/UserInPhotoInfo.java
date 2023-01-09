package com.allmy.allstatusdownloader.Model;

import java.io.Serializable;

public class UserInPhotoInfo implements Serializable {
    private static final long serialVersionUID = 1;
    boolean check_tap;
    String user_name;
    float x_pos;
    float y_pos;

    public UserInPhotoInfo(float f, float f2, String str, boolean z) {
        this.x_pos = f;
        this.y_pos = f2;
        this.user_name = str;
        this.check_tap = z;
    }

    public boolean isCheck_tap() {
        return this.check_tap;
    }

    public void setCheck_tap(boolean z) {
        this.check_tap = z;
    }

    public float getX_pos() {
        return this.x_pos;
    }

    public void setX_pos(float f) {
        this.x_pos = f;
    }

    public float getY_pos() {
        return this.y_pos;
    }

    public void setY_pos(float f) {
        this.y_pos = f;
    }

    public String getUser_name() {
        return this.user_name;
    }

    public void setUser_name(String str) {
        this.user_name = str;
    }
}
