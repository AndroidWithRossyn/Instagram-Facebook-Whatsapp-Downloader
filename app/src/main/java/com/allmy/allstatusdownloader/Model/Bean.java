package com.allmy.allstatusdownloader.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Bean implements Serializable {
    private static final long serialVersionUID = 1;
    List<Album> arr_album;
    private boolean checkmultiple;
    private String code_HdUrl;
    private String comments;
    private String created_time;
    private String date_of_post;
    private String full_name;
    private String imageUrl;
    private String imageheight;
    private String imagewidht;
    private boolean isDownload;
    private String isLiked;
    private boolean isRepost;
    private boolean isSelect = false;
    private String likes;
    private String locName;
    private String media_id;
    private String media_title;
    private String media_type;
    private String original_height;
    private String original_width;
    private String profile_picture;
    private String taken_at;
    private String text;
    private String type;
    private ArrayList<UserInPhotoInfo> userInPhoto;
    private String userName;
    private String user_id;
    private String videoUrl;

    public Bean(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13, ArrayList<UserInPhotoInfo> arrayList, String str14, String str15, boolean z, String str16, String str17, String str18, String str19, String str20, String str21, String str22, List<Album> list) {
        this.isLiked = str12;
        this.comments = str;
        this.likes = str2;
        this.imageUrl = str3;
        this.type = str4;
        this.userName = str5;
        this.profile_picture = str6;
        this.full_name = str7;
        this.user_id = str8;
        this.media_id = str9;
        this.text = str10;
        this.created_time = str11;
        this.videoUrl = str13;
        this.userInPhoto = arrayList;
        this.imageheight = str14;
        this.imagewidht = str15;
        this.checkmultiple = z;
        this.code_HdUrl = str16;
        this.original_width = str17;
        this.original_height = str18;
        this.date_of_post = str19;
        this.media_title = str20;
        this.media_type = str21;
        this.taken_at = str22;
        this.arr_album = list;
    }

    public Bean(String str3)
    {
        this.imageUrl = str3;
    }

    public String getTaken_at() {
        return this.taken_at;
    }

    public void setTaken_at(String str) {
        this.taken_at = str;
    }

    public String getMedia_type() {
        return this.media_type;
    }

    public void setMedia_type(String str) {
        this.media_type = str;
    }

    public String getMedia_title() {
        return this.media_title;
    }

    public void setMedia_title(String str) {
        this.media_title = str;
    }

    public String getDate_of_post() {
        return this.date_of_post;
    }

    public void setDate_of_post(String str) {
        this.date_of_post = str;
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

    public String getLocName() {
        return this.locName;
    }

    public void setLocName(String str) {
        this.locName = str;
    }

    public String getVideoUrl() {
        return this.videoUrl;
    }

    public void setVideoUrl(String str) {
        this.videoUrl = str;
    }

    public ArrayList<UserInPhotoInfo> getUserInPhoto() {
        return this.userInPhoto;
    }

    public void setUserInPhoto(ArrayList<UserInPhotoInfo> arrayList) {
        this.userInPhoto = arrayList;
    }

    public boolean isCheckmultiple() {
        return this.checkmultiple;
    }

    public void setCheckmultiple(boolean z) {
        this.checkmultiple = z;
    }

    public boolean isDownload() {
        return this.isDownload;
    }

    public void setDownload(boolean z) {
        this.isDownload = z;
    }

    public boolean isSelect() {
        return this.isSelect;
    }

    public void setSelect(boolean z) {
        this.isSelect = z;
    }

    public boolean isRepost() {
        return this.isRepost;
    }

    public void setRepost(boolean z) {
        this.isRepost = z;
    }

    public String getIsLike() {
        return this.isLiked;
    }

    public void setIsLike(String str) {
        this.isLiked = str;
    }

    public String getUser_id() {
        return this.user_id;
    }

    public void setUser_id(String str) {
        this.user_id = str;
    }

    public String getMedia_id() {
        return this.media_id;
    }

    public void setMedia_id(String str) {
        this.media_id = str;
    }

    public String getCreated_time() {
        return this.created_time;
    }

    public void setCreated_time(String str) {
        this.created_time = str;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String str) {
        this.text = str;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String str) {
        this.userName = str;
    }

    public String getProfile_picture() {
        return this.profile_picture;
    }

    public String getImageheight() {
        return this.imageheight;
    }

    public String getImagewidth() {
        return this.imagewidht;
    }

    public void setProfile_picture(String str) {
        this.profile_picture = str;
    }

    public String getFull_name() {
        return this.full_name;
    }

    public void setFull_name(String str) {
        this.full_name = str;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String str) {
        this.type = str;
    }

    public String getComments() {
        return this.comments;
    }

    public void setComments(String str) {
        this.comments = str;
    }

    public String getLikes() {
        return this.likes;
    }

    public void setLikes(String str) {
        this.likes = str;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public void setImageUrl(String str) {
        this.imageUrl = str;
    }

    public void setImagheight(String str) {
        this.imageheight = str;
    }

    public void setImagwidht(String str) {
        this.imagewidht = str;
    }

    public List<Album> getArr_album() {
        return this.arr_album;
    }

    public void setArr_album(List<Album> list) {
        this.arr_album = list;
    }
}
