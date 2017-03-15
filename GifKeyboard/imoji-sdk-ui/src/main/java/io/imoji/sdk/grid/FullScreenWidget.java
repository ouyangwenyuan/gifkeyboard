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
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
public class FullScreenWidget extends BaseSearchWidget {

    private final static int SPAN_COUNT = 3;

    public FullScreenWidget(Context context, WidgetDisplayOptions options, SearchResultAdapter.ImageLoader imageLoader) {
        super(context, SPAN_COUNT, VERTICAL, true, ResultView.LARGE, options, imageLoader);

        searchBarLayout.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                (int) getResources().getDimension(R.dimen.imoji_search_bar_height_full_widget)));

        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {

            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                //TODO // FIXME: 5/3/16
                int resultSize = (int) getResources().getDimension(R.dimen.imoji_result_width_large);
                int padding = (recyclerView.getWidth() - resultSize * SPAN_COUNT) / (SPAN_COUNT * 2);
                if (parent.getChildLayoutPosition(view) != resultAdapter.getDividerPosition()) {
                    outRect.right = outRect.right - padding;
                    outRect.left = padding;
                    outRect.bottom = (int) getContext().getResources().getDimension(R.dimen.imoji_search_recycler_vertical_padding);
                } else {
                    outRect.top = (int) getContext().getResources().getDimension(R.dimen.imoji_search_vertical_recycler_divider_top_margin);
                    outRect.bottom = (int) getContext().getResources().getDimension(R.dimen.imoji_search_vertical_recycler_divider_bottom_margin);
                }
            }
        });

        searchBarLayout.toggleTextFocus(false);
        searchBarLayout.setupBackCloseButton(true, true);
    }

    @Override
    protected View getNoStickerView(final boolean isRecents) {
        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.imoji_full_search_widget_no_result, switcher);

        TextView text = (TextView) view.findViewById(R.id.replacement_view_text);
        if (isRecents) {
            text.setText(getContext().getString(R.string.imoji_search_widget_no_recent_hint));
        }
        text.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Montserrat-Light.otf"));

        return view;
    }

    @Override
    public void onTextCleared() {

    }

    @Override
    public void onTap(@NonNull SearchResult searchResult) {
        super.onTap(searchResult);
        if (searchResult.isCategory()) {
            setBarState(true);
        }
    }

    @Override
    public void onFocusChanged(boolean hasFocus) {
        if (hasFocus) {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
            setBarState(true);
        } else if (!hasFocus) {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(searchBarLayout.getWindowToken(), 0);
        }
    }

    @Override
    public void onBackButtonTapped() {
        super.onBackButtonTapped();
        setBarState(false);
    }

    private void setBarState(boolean active) {
        searchBarLayout.setupBackCloseButton(!active, true);
        searchBarLayout.setActionButtonsVisibility(options.isIncludeRecentsAndCreate() && !active);
    }

    public void setBackButtonVisibility(boolean isVisible){
        searchBarLayout.setBackCloseButtonVisibility(isVisible);
    }
}
