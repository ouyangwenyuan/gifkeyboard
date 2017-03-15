/*
 * Copyright (c) 2017. @author ouyang copyright@fotoable Inc.  Anyone can copy this
 */

package com.fotoable.inputkeyboard.model;

/**
 * Created by ouyangwenyuan on 2017/3/10.
 */

public interface IAppInfo {

    int getId();

    int getName();

    String getPackageName();

    boolean suportOneKeyShare();

    boolean supportMultiMedia();

//    boolean isInstall();
}
