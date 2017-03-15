/*
 * Copyright (c) 2017. @author ouyang copyright@fotoable Inc.  Anyone can copy this
 */

package com.fotoable.inputkeyboard.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Result implements Serializable {
    private static final long serialVersionUID = -4037633614634142811L;
    @SerializedName("aspect_ratio")
    private float aspectRatio;

    @SerializedName("bg_color")
    private String bgColor;

    @SerializedName("created")
    private double created;

    @SerializedName("embed")
    private String embed;

    @SerializedName("hasaudio")
    private boolean hasAudio;

    @SerializedName("id")
    private String id;

    @SerializedName("itemurl")
    private String itemUrl;

    @SerializedName("shares")
    private int shareCount;

    @SerializedName("tags")
    private List<String> tags;

    private String tagsString;

    @SerializedName("title")
    private String title;

    @SerializedName("url")
    private String url;

    private String name = "";
    private int viewIndex = -1;
//    @SerializedName("media")
//    private RealmList<MediaCollection> medias;
//    private GifPoster user;
//    @SerializedName("composite")
//    private Media composite;


    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public float getAspectRatio() {
        return aspectRatio;
    }

    public void setAspectRatio(float aspectRatio) {
        this.aspectRatio = aspectRatio;
    }

    public String getBgColor() {
        return bgColor;
    }

    public void setBgColor(String bgColor) {
        this.bgColor = bgColor;
    }

    public double getCreated() {
        return created;
    }

    public void setCreated(double created) {
        this.created = created;
    }

    public String getEmbed() {
        return embed;
    }

    public void setEmbed(String embed) {
        this.embed = embed;
    }

    public boolean isHasAudio() {
        return hasAudio;
    }

    public void setHasAudio(boolean hasAudio) {
        this.hasAudio = hasAudio;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getItemUrl() {
        return itemUrl;
    }

    public void setItemUrl(String itemUrl) {
        this.itemUrl = itemUrl;
    }

    public int getShareCount() {
        return shareCount;
    }

    public void setShareCount(int shareCount) {
        this.shareCount = shareCount;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getTagsString() {
        return tagsString;
    }

    public void setTagsString(String tagsString) {
        this.tagsString = tagsString;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getViewIndex() {
        return viewIndex;
    }

    public void setViewIndex(int viewIndex) {
        this.viewIndex = viewIndex;
    }
}