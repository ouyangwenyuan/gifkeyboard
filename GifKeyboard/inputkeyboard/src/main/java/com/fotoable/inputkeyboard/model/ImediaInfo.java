/*
 * Copyright (c) 2017. @author ouyang copyright@fotoable Inc.  Anyone can copy this
 */

package com.fotoable.inputkeyboard.model;

/**
 * Created by ouyangwenyuan on 2017/3/10.
 */

public interface ImediaInfo {
    public static final String Logo = "fotoable";
    String getId();

    String getUrl();

    String getName();

    String shareTitle();

    String shareContent();

    boolean needAddLogo();

    int getMediaType();
}
