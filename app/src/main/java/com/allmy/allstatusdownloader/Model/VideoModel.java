package com.allmy.allstatusdownloader.Model;


import android.graphics.Bitmap;

import java.io.File;

public class VideoModel {

    String videoPath;
    String videoName;
    Bitmap thumbNail;
    String duration;
    String size;
    String date;
    File videoFile;

    public VideoModel()
    {

    }
    public VideoModel(String videoPath)
    {
        this.videoPath=videoPath;
    }
    public String getVideoPath() {
        return videoPath;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDuration() {
        return duration;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public Bitmap getThumbNail() {
        return thumbNail;
    }

    public void setThumbNail(Bitmap thumbNail) {
        this.thumbNail = thumbNail;
    }

    public File getVideoFile() {
        return videoFile;
    }

    public void setVideoFile(File videoFile) {
        this.videoFile = videoFile;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }




}
