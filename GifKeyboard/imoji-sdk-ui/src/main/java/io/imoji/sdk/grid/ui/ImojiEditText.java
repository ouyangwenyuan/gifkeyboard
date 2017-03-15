/*
 * Imoji Android SDK UI
 * Created by engind
 *
 * Copyright (C) 2016 Imoji
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to
 * deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KID, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 *
 */

package io.imoji.sdk.grid.ui;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

import io.imoji.sdk.ui.R;

/**
 * Created by engind on 4/21/16.
 */
public class ImojiEditText extends EditText {

    public ImojiEditText(Context context) {
        super(context);
        init();
    }

    public ImojiEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ImojiEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        super.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/Montserrat-Light.otf"));
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        setHintTextColor(focused ? getResources().getColor(R.color.search_widget_search_bar_focused_hint_color) :
                getResources().getColor(R.color.search_widget_search_bar_unfocused_text_color));
        setTextColor(focused ? getResources().getColor(R.color.search_widget_search_bar_focused_text_color) :
                getResources().getColor(R.color.search_widget_search_bar_unfocused_text_color));
    }
}
