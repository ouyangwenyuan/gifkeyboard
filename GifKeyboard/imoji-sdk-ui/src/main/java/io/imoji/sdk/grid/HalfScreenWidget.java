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

package io.imoji.sdk.grid;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import io.imoji.sdk.ui.R;
import io.imoji.sdk.grid.components.BaseSearchWidget;
import io.imoji.sdk.grid.components.SearchResultAdapter;
import io.imoji.sdk.grid.components.WidgetDisplayOptions;
import io.imoji.sdk.grid.components.SearchResult;
import io.imoji.sdk.grid.ui.ResultView;

/**
 * Created by engind on 4/24/16.
 */
public class HalfScreenWidget extends BaseSearchWidget {

    public final static int SPAN_COUNT = 2;

    public HalfScreenWidget(Context context, WidgetDisplayOptions options, SearchResultAdapter.ImageLoader imageLoader) {
        super(context, SPAN_COUNT, HORIZONTAL, false, ResultView.SMALL, options, imageLoader);
        setBackgroundDrawable(getResources().getDrawable(R.drawable.base_widget_separator));

        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {

            private int verticalPadd = (int) getContext().getResources().getDimension(R.dimen.imoji_search_recycler_vertical_padding);
            private int horizontalPadd = (int) getContext().getResources().getDimension(R.dimen.imoji_search_recycler_horizontal_padding);

            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.left = horizontalPadd;
                outRect.bottom = verticalPadd;
                outRect.right = horizontalPadd;

                int position = parent.getChildLayoutPosition(view);

                if (position == resultAdapter.getDividerPosition()) {
                    outRect.right = 0;
                } else if (position == 0) {
                    outRect.left = horizontalPadd * 2;
                } else if (position >= state.getItemCount() - SPAN_COUNT) {
                    outRect.right = horizontalPadd * 2;
                }
            }
        });

        searchBarLayout.toggleTextFocus(false);
    }

    @Override
    public void onTextCleared() {

    }

    @Override
    public void onFocusChanged(final boolean hasFocus) {
        if (!hasFocus) {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(searchBarLayout.getWindowToken(), 0);
        }else{
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
            setBarState(true);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                switcher.setVisibility(hasFocus ? GONE : VISIBLE);
            }
        }, 100);
    }

    @Override
    public void onBackButtonTapped() {
        super.onBackButtonTapped();
        setBarState(false);
    }

    @Override
    public void onTap(@NonNull SearchResult searchResult) {
        super.onTap(searchResult);
        if(searchResult.isCategory()){
            setBarState(true);
        }
    }

    @Override
    protected View getNoStickerView(boolean isRecents) {
        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.imoji_half_search_widget_no_result, switcher);


        TextView textView = (TextView) view.findViewById(R.id.replacement_view_text);
        if(isRecents){
            textView.setText(getContext().getString(R.string.imoji_search_widget_no_recent_hint));
        }
        textView.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Montserrat-Regular.otf"));

        return view;
    }

    private void setBarState(boolean active) {
        searchBarLayout.setupBackCloseButton(false, active);
        searchBarLayout.setActionButtonsVisibility(options.isIncludeRecentsAndCreate() && !active);
    }
}
