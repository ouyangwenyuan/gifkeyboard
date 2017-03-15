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

package io.imoji.sdk.grid.components;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.imoji.sdk.ui.R;
import io.imoji.sdk.grid.ui.ResultView;

/**
 * Created by engind on 4/22/16.
 */
public class SearchResultAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public final static int DIVIDER_VIEW_TYPE = 2343234;

    private List<SearchResult> results;
    private Context context;
    private TapListener tapListener;
    private ImageLoader imageLoader;

    private int placeholderRandomizer;
    private
    @ResultView.ResultViewSize
    int resultViewSize;
    private int dividerPosition = -1;
    private int orientation;
    private WidgetDisplayOptions options;


    public SearchResultAdapter(@NonNull Context context, @NonNull ImageLoader imageLoader,
                               @ResultView.ResultViewSize int resultViewSize, int orientation,
                               @NonNull WidgetDisplayOptions uiSDKOptions) {
        results = new ArrayList<>();
        this.context = context;
        this.imageLoader = imageLoader;
        this.options = uiSDKOptions;

        int[] colorArray = context.getResources().getIntArray(R.array.search_widget_placeholder_colors);
        this.placeholderRandomizer = new Random().nextInt(colorArray.length);

        this.resultViewSize = resultViewSize;
        this.orientation = orientation;
    }

    public void repopulate(List<SearchResult> newResults, int dividerPosition) {
        int size = this.results.size();
        if (size > 0) {
            results.clear();
            this.notifyItemRangeRemoved(0, size);
        }
        this.dividerPosition = dividerPosition;
        results.addAll(newResults);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == DIVIDER_VIEW_TYPE) {
            return new DividerHolder(new View(context));
        }

        return new ResultHolder(new ResultView(context, resultViewSize));
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (position != dividerPosition) {
            final SearchResult sr = results.get(position);
            final ResultView resultView = (ResultView) holder.itemView;
            resultView.setListener(tapListener, results.get(holder.getAdapterPosition()));
            resultView.resetView(placeholderRandomizer, position);

            imageLoader.loadImage(resultView.getImageView(), sr.getThumbnailUri(options), new ImageLoaderCallback() {
                @Override
                public void updateImageView() {
                    if (sr.isCategory()) {
                        resultView.loadCategory(sr.getTitle());
                    } else {
                        resultView.loadSticker();
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == dividerPosition) {
            return DIVIDER_VIEW_TYPE;
        }

        return super.getItemViewType(position);
    }

    public class ResultHolder extends RecyclerView.ViewHolder{

        public ResultHolder(View itemView) {
            super(itemView);
        }
    }

    public class DividerHolder extends RecyclerView.ViewHolder {

        public DividerHolder(View itemView) {
            super(itemView);
            itemView.setBackgroundColor(ColorUtils.setAlphaComponent(
                    //TODO // FIXME: 5/3/16
                    context.getResources().getColor(R.color.search_widget_category_divider), 18));

            int width = (int) context.getResources().getDimension(R.dimen.imoji_search_recycler_divider_width);
            StaggeredGridLayoutManager.LayoutParams layoutParams = new StaggeredGridLayoutManager.LayoutParams(
                    orientation == LinearLayout.HORIZONTAL ? width : ViewGroup.LayoutParams.MATCH_PARENT,
                    orientation == LinearLayout.HORIZONTAL ? ViewGroup.LayoutParams.MATCH_PARENT : width);
            if(orientation == LinearLayout.HORIZONTAL){
                int marg = (int) context.getResources().getDimension(R.dimen.imoji_search_horizontal_recycler_divider_padding);
                layoutParams.setMargins(0,marg,0,marg);
            }else{
                int marg = (int) context.getResources().getDimension(R.dimen.imoji_search_vertical_recycler_divider_padding);
                layoutParams.setMargins(marg,0,marg,0);
            }
            itemView.setLayoutParams(layoutParams);
        }
    }

    public void setSearchTapListener(@NonNull TapListener tapListener) {
        this.tapListener = tapListener;
    }

    public int getDividerPosition() {
        return dividerPosition;
    }

    public interface TapListener {

        void onTap(@NonNull SearchResult searchResult);

    }

    public interface ImageLoader {

        void loadImage(@NonNull ImageView target,
                       @NonNull Uri uri,
                       @NonNull ImageLoaderCallback callback);
    }

    public interface ImageLoaderCallback {

        void updateImageView();
    }
}
