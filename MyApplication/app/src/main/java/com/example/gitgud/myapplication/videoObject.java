package com.example.gitgud.myapplication;

import android.graphics.Bitmap;

public class videoObject {

    public String title;
    public String views;
    public String published;
    public String channel;
    public String URLs;
    public Bitmap videoBitmaps;

    public videoObject(String title, String views, String published, String channel, String URLs){

        this.title = title;
        this.views = views;
        this.published = published;
        this.channel = channel;
        this.URLs = URLs;

    }
    public String getTitle(){
        return this.title;
    }
    public String getViews(){
        return this.views;
    }
    public String getPublished(){
        return this.published;
    }
    public String getChannel(){
        return this.channel;
    }
    public String getURLs(){
        return this.URLs;
    }
    public String toString(){
        return "Title: " + this.title + " Channel: " + this.channel +
                " Published: " + this.published + " Views: " + this.views + " Link: " + this.URLs;
    }
    public void setBitmap(Bitmap videoBitmaps){
        this.videoBitmaps = videoBitmaps;
    }
    public Bitmap getBitmap(){
        return this.videoBitmaps;
    }

}
