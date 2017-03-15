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
import android.graphics.Typeface;
import android.support.annotation.LayoutRes;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import io.imoji.sdk.ui.R;

/**
 * Created by engind on 4/21/16.
 */
public class SearchBarLayout extends ViewSwitcher {

    private View backCloseView;
    private View clearView;
    private ImojiEditText textBox;
    private LinearLayout actionsLayout;
    protected int recentsLayout = R.layout.imoji_recents_bar_large;

    private ImojiSearchBarListener imojiSearchBarListener;
    private boolean shouldTriggerAutoSearch = true;

    public SearchBarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(getContext(), R.layout.imoji_search_bar, this);


        backCloseView = this.findViewById(R.id.search_bar_back_close_view);
        textBox = (ImojiEditText) this.findViewById(R.id.search_bar_text_box);
        clearView = this.findViewById(R.id.search_bar_clear_view);
        actionsLayout = (LinearLayout) this.findViewById(R.id.search_bar_action_container);

        textBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (start == 0 && s.length() > 0) {
                    clearView.setVisibility(VISIBLE);
                } else if (before > 0 && s.length() == 0) {
                    clearView.setVisibility(GONE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                imojiSearchBarListener.onTextChanged(s.toString(), shouldTriggerAutoSearch);
            }
        });

        textBox.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (imojiSearchBarListener != null) {
                    imojiSearchBarListener.onFocusChanged(hasFocus);
                }
            }
        });

        textBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE ||
                        (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN)) {
                    if (imojiSearchBarListener != null) {
                        imojiSearchBarListener.onTextSubmit(textBox.getText().toString());
                    }
                    textBox.clearFocus();
                }
                return true;
            }
        });

        clearView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                textBox.setText("");
                textBox.requestFocus();
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(textBox, InputMethodManager.SHOW_IMPLICIT);
                imojiSearchBarListener.onTextCleared();
            }
        });

        findViewById(R.id.search_bar_search_view).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                textBox.requestFocus();
            }
        });

        findViewById(R.id.search_bar_recent_icon).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showRecentsView();
                imojiSearchBarListener.onRecentsButtonTapped();
            }
        });

        View createIcon = findViewById(R.id.search_bar_create_icon);
        if (createIcon != null) {
            createIcon.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    imojiSearchBarListener.onCreateButtonTapped();
                }
            });
        }
        textBox.requestFocus();
    }

    public void setImojiSearchListener(ImojiSearchBarListener searchListener) {
        this.imojiSearchBarListener = searchListener;
    }

    public void toggleTextFocus(boolean shouldRequest) {
        if (shouldRequest) {
            textBox.requestFocus();
        } else {
            textBox.clearFocus();
        }
    }

    public void setActionButtonsVisibility(boolean visible) {
        actionsLayout.setVisibility(visible ? VISIBLE : GONE);
    }

    public void setupBackCloseButton(final boolean isClose, boolean isVisible) {
        backCloseView.setBackgroundResource(isClose ? R.drawable.imoji_close : R.drawable.imoji_back);
        backCloseView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imojiSearchBarListener != null) {
                    if(isClose){
                        imojiSearchBarListener.onCloseButtonTapped();
                    }else{
                        imojiSearchBarListener.onBackButtonTapped();
                    }
                }
            }
        });
        setBackCloseButtonVisibility(isVisible);
    }

    public void setBackCloseButtonVisibility(boolean isVisible){
        backCloseView.setVisibility(isVisible ? VISIBLE : GONE);
    }

    public void setText(String text) {
        shouldTriggerAutoSearch = false;
        textBox.setText(text);
        shouldTriggerAutoSearch = true;
        textBox.clearFocus();
    }

    public void setRecentsLayout(@LayoutRes int recentsLayout) {
        this.recentsLayout = recentsLayout;
    }

    public void showRecentsView() {
        if (getChildAt(1) != null) {
            removeViewAt(1);
        }

        LayoutInflater.from(getContext()).inflate(recentsLayout, this);

        ((TextView) findViewById(R.id.recents_bar_text))
                .setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/Montserrat-Light.otf"));

        View backIcon = findViewById(R.id.recents_bar_back_icon);
        if (backIcon != null) {
            backIcon.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeViewAt(1);
                    setDisplayedChild(0);
                    imojiSearchBarListener.onBackButtonTapped();
                }
            });
        }

        View createIcon = findViewById(R.id.recents_bar_create_icon);
        if (createIcon != null) {
            createIcon.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    imojiSearchBarListener.onCreateButtonTapped();
                }
            });
        }

        findViewById(R.id.recents_bar_search_icon).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                removeViewAt(1);
                setDisplayedChild(0);
                toggleTextFocus(true);
            }
        });

        setDisplayedChild(1);
    }

    public interface ImojiSearchBarListener {

        void onTextSubmit(String term);

        void onTextCleared();

        void onBackButtonTapped();

        void onCloseButtonTapped();

        void onFocusChanged(boolean hasFocus);

        void onTextChanged(String term, boolean shouldTriggerAutoSearch);

        void onRecentsButtonTapped();

        void onCreateButtonTapped();
    }

}
