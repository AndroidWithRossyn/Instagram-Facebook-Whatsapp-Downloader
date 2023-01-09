package com.allmy.allstatusdownloader.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class SavedFeed_setter implements Serializable {
    private String ID;
    ArrayList<Album> arr_album;
    private String caption_text;
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
    private String thumbnail_url;
    private String type;
    String userName;
    private String video_url;

    public SavedFeed_setter()
    {

    }
    public SavedFeed_setter(String image_url)
    {
        this.video_url=image_url;
    }
    public ArrayList<Album> getArr_album() {
        return this.arr_album;
    }

    public void setArr_album(ArrayList<Album> arrayList) {
        this.arr_album = arrayList;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String str) {
        this.type = str;
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

    public boolean isCheckmultiple() {
        return this.checkmultiple;
    }

    public void setCheckmultiple(boolean z) {
        this.checkmultiple = z;
    }

    public String getMedia_title() {
        return this.media_title;
    }

    public void setMedia_title(String str) {
        this.media_title = str;
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

    public String getThumbnail_url() {
        return this.thumbnail_url;
    }

    public void setThumbnail_url(String str) {
        this.thumbnail_url = str;
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
