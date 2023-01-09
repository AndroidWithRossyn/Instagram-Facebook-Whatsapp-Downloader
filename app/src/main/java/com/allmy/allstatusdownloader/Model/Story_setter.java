package com.allmy.allstatusdownloader.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;

public class Story_setter implements Parcelable {
    public static final Creator<Story_setter> CREATOR = new Creator<Story_setter>() {
        public Story_setter createFromParcel(Parcel parcel) {
            return new Story_setter(parcel);
        }

        public Story_setter[] newArray(int i) {
            return new Story_setter[i];
        }
    };
    private String ID;
    private String caption_text;
    private boolean checkdownloaded;
    private boolean checkmultiple;
    private String code_HdUrl;
    ArrayList<HashMap<String, String>> data = new ArrayList<>();
    private String getDate;
    private String image_height;
    private String image_url;
    private String image_width;
    private String media_id;
    private String media_title;
    private String original_height;
    private String original_width;
    String profile_picture;
    private String taken_at;
    private String thimbnail_url;
    private String type;
    String userName;
    int checkAd;

    public int getCheckAd() {
        return checkAd;
    }

    public void setCheckAd(int checkAd) {
        this.checkAd = checkAd;
    }

    private String video_url;

    public int describeContents() {
        return 0;
    }

    protected Story_setter(Parcel parcel) {
        this.userName = parcel.readString();
        this.profile_picture = parcel.readString();
        this.type = parcel.readString();
        this.ID = parcel.readString();
        this.video_url = parcel.readString();
        this.image_url = parcel.readString();
        this.image_height = parcel.readString();
        this.image_width = parcel.readString();
        this.getDate = parcel.readString();
        this.thimbnail_url = parcel.readString();
        this.code_HdUrl = parcel.readString();
        this.original_height = parcel.readString();
        boolean z = true;
        this.checkmultiple = parcel.readByte() != 0;
        this.original_width = parcel.readString();
        if (parcel.readByte() == 0) {
            z = false;
        }
        this.checkdownloaded = z;
        this.media_id = parcel.readString();
        this.caption_text = parcel.readString();
    }

    public Story_setter(int check)
    {
        this.checkAd=check;
    }
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.userName);
        parcel.writeString(this.profile_picture);
        parcel.writeString(this.type);
        parcel.writeString(this.ID);
        parcel.writeString(this.video_url);
        parcel.writeString(this.image_url);
        parcel.writeString(this.image_height);
        parcel.writeString(this.image_width);
        parcel.writeString(this.getDate);
        parcel.writeString(this.thimbnail_url);
        parcel.writeString(this.code_HdUrl);
        parcel.writeString(this.original_height);
        parcel.writeByte(this.checkmultiple ? (byte) 1 : 0);
        parcel.writeString(this.original_width);
        parcel.writeByte(this.checkdownloaded ? (byte) 1 : 0);
        parcel.writeString(this.media_id);
        parcel.writeString(this.caption_text);
    }

    public String getCaption_text() {
        return this.caption_text;
    }

    public void setCaption_text(String str) {
        this.caption_text = str;
    }

    public String getMedia_id() {
        return this.media_id;
    }

    public void setMedia_id(String str) {
        this.media_id = str;
    }

    public boolean isCheckdownloaded() {
        return this.checkdownloaded;
    }

    public void setCheckdownloaded(boolean z) {
        this.checkdownloaded = z;
    }

    public String getMedia_title() {
        return this.media_title;
    }

    public void setMedia_title(String str) {
        this.media_title = str;
    }

    public boolean isCheckmultiple() {
        return this.checkmultiple;
    }

    public void setCheckmultiple(boolean z) {
        this.checkmultiple = z;
    }

    public String getOriginal_height() {
        return this.original_height;
    }

    public void setOriginal_height(String str) {
        this.original_height = str;
    }

    public String getOriginal_width() {
        return this.original_width;
    }

    public void setOriginal_width(String str) {
        this.original_width = str;
    }

    public String getCode_HdUrl() {
        return this.code_HdUrl;
    }

    public void setCode_HdUrl(String str) {
        this.code_HdUrl = str;
    }

    public String getThimbnail_url() {
        return this.thimbnail_url;
    }

    public void setThimbnail_url(String str) {
        this.thimbnail_url = str;
    }

    public String getGetDate() {
        return this.getDate;
    }

    public void setGetDate(String str) {
        this.getDate = str;
    }

    public String getTaken_at() {
        return this.taken_at;
    }

    public void setTaken_at(String str) {
        this.taken_at = str;
    }

    public Story_setter() {
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
