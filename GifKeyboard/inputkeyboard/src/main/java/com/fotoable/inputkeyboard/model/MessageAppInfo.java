/*
 * Copyright (c) 2017. @author ouyang copyright@fotoable Inc.  Anyone can copy this
 */

package com.fotoable.inputkeyboard.model;

/**
 * Created by ouyangwenyuan on 2017/3/10.
 */

public class MessageAppInfo implements IAppInfo {
    private int id;
    private int name;
    private boolean supportOneKeyShare = false;
    private boolean supportMultiMedia = false;
    private String packageName;

    public MessageAppInfo() {
    }

    public MessageAppInfo(String packageName) {
        this.packageName = packageName;
    }

    public MessageAppInfo(int id, int name, String packageName) {
        this.id = id;
        this.name = name;
        this.packageName = packageName;
    }

    public MessageAppInfo(int id, int name, boolean supportOneKeyShare, boolean supportMultiMedia, String packageName) {
        this.id = id;
        this.name = name;
        this.supportOneKeyShare = supportOneKeyShare;
        this.supportMultiMedia = supportMultiMedia;
        this.packageName = packageName;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public int getName() {
        return name;
    }

    @Override
    public String getPackageName() {
        return null;
    }

    @Override
    public boolean suportOneKeyShare() {
        return false;
    }

    @Override
    public boolean supportMultiMedia() {
        return false;
    }


    public void setName(int name) {
        this.name = name;
    }

    public void setSupportOneKeyShare(boolean supportOneKeyShare) {
        this.supportOneKeyShare = supportOneKeyShare;
    }

    public void setSupportMultiMedia(boolean supportMultiMedia) {
        this.supportMultiMedia = supportMultiMedia;
    }

}
