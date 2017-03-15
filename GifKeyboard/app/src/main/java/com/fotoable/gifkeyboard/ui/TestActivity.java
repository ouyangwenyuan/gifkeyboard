/*
 * Copyright (c) 2017. @author ouyang copyright@fotoable Inc.  Anyone can copy this
 */

package com.fotoable.gifkeyboard.ui;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.fotoable.gifkeyboard.R;

import butterknife.BindView;
import butterknife.OnClick;
import cn.droidlover.xdroidmvp.mvp.XActivity;

/**
 * Created by ouyangwenyuan on 2017/3/6.
 */
public class TestActivity extends XActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
//    @BindView(R.id.bt_setting)
//    private Button settingBt;
//    @BindView(R.id.bt_select)
//    private Button selectBt;
//    @BindView(R.id.et_input)
//    private EditText inputEt;
//    @BindView(R.id.bt_input_subtype)
//    private Button bt_input_subtype;
//    @BindView(R.id.bt_setting_input)
//    private Button bt_setting_input;
//    @BindView(R.id.bt_voiceinput)
//    private Button bt_voiceinput;
//    @BindView(R.id.bt_other)
//    private Button bt_other;
    private InputMethodManager imm;

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
//        selectBt = (Button) findViewById(R.id.bt_select);
//        selectBt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                imm.showInputMethodPicker();
//            }
//        });
//        settingBt = (Button) findViewById(R.id.bt_setting);
//        settingBt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                onSetting(view, 1);
//            }
//        });
//
//        findViewById(R.id.bt_input_subtype).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                onSetting(view, 2);
//            }
//        });
//
//        findViewById(R.id.bt_voiceinput).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                onSetting(view, 3);
//            }
//        });
//        findViewById(R.id.bt_setting_input).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                onSetting(v, 4);
//                AboutActivity.launch(MainActivity.this);
//            }
//        });
//        findViewById(R.id.bt_other).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(MainActivity.this, EnterActivity.class));
//            }
//        });
//
//    }

    @OnClick({
            R.id.bt_setting,
            R.id.bt_select,
            R.id.bt_input_subtype,
            R.id.bt_voiceinput,
            R.id.bt_setting_input,
            R.id.bt_other,
    })
    public void clickEvent(View view) {
        switch (view.getId()) {

            case R.id.bt_other:
                startActivity(new Intent(TestActivity.this, ShareListActivity.class));
                break;
            case R.id.bt_setting:
                onSetting(view, 1);
                break;
            case R.id.bt_select:
                imm.showInputMethodPicker();
                break;
            case R.id.bt_input_subtype:
                onSetting(view, 2);
                break;
            case R.id.bt_voiceinput:
                onSetting(view, 3);
                break;
            case R.id.bt_setting_input:
                AboutActivity.launch(TestActivity.this);
                break;
        }
    }


    void onSetting(View v, int type) {
        final Intent intent = new Intent();
        if (type == 1) {
            intent.setAction(Settings.ACTION_INPUT_METHOD_SETTINGS);
        } else if (type == 2) {
            intent.setAction(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        } else if (type == 3) {
            intent.setAction(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        } else {
            intent.setAction(Settings.ACTION_SETTINGS);
        }
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        setSupportActionBar(toolbar);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public Object newP() {
        return null;
    }
}

