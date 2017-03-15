package com.fotoable.gifkeyboard.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

import com.fotoable.gifkeyboard.R;

import java.util.List;

import butterknife.BindView;
import cn.droidlover.xdroidmvp.mvp.XActivity;
import cn.droidlover.xdroidmvp.router.Router;

/**
 * Created by ouyangwenyuan on 2017/3/14.
 */

public class SplashActivity extends XActivity {
    @BindView(R.id.rootview)
    LinearLayout rootView;
    Handler handler = new Handler();
    private InputMethodManager imm;

    @Override
    public void initData(Bundle savedInstanceState) {
        rootView.setBackgroundResource(R.drawable.loading_screen);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                List<InputMethodInfo> inputMethodInfos = imm.getEnabledInputMethodList();
                boolean isenable = false;
                for (InputMethodInfo info : inputMethodInfos) {
                    if (info.getPackageName().equals(getPackageName())) {
                        isenable = true;
                        break;
                    }
                }
                if (isenable) {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                } else {
                    Router.newIntent(SplashActivity.this).to(GuideActivity.class).launch();
                }
                finish();
            }
        }, 2000);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    public Object newP() {
        return null;
    }
}
