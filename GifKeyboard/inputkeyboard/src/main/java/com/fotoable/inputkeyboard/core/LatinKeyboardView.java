package com.fotoable.inputkeyboard.core;

import android.annotation.SuppressLint;
import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;
import android.util.Log;

@SuppressLint("NewApi")
public class LatinKeyboardView extends KeyboardView {
    //网上说：当继承View的时候，会有个一个含有AttributeSet参数的构造方法，  
    //通过此类就可以得到自己定义的xml属性，也可以是android的内置的属性  
    //就好像TextView这东西也有个 View的基类  

    //干什么用的?好像是设了一个无用的键值，等到后面调用 

    static final int KEYCODE_OPTIONS = -100;

    public LatinKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LatinKeyboardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected boolean onLongPress(Keyboard.Key key) {
        //codes[0]代表当前按的值.按时间长了就失去了效果（cancel）
        if (key.codes[0] == Keyboard.KEYCODE_CANCEL) {
            getOnKeyboardActionListener().onKey(KEYCODE_OPTIONS, null);
            return true;
        } else {
            if (key.codes[0] == 32) {
                Log.i("press", "space");
            }
            return super.onLongPress(key);
        }
    }
}