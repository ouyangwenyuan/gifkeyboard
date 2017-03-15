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

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.IntDef;
import android.support.v4.graphics.ColorUtils;
import android.support.v4.view.animation.PathInterpolatorCompat;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.felipecsl.gifimageview.library.GifImageView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import io.imoji.sdk.ui.R;
import io.imoji.sdk.grid.components.SearchResultAdapter;
import io.imoji.sdk.grid.components.SearchResult;

/**
 * Created by engind on 5/2/16.
 */
public class ResultView extends RelativeLayout {


    @IntDef({LARGE, SMALL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ResultViewSize {
    }

    public static final int LARGE = 0;
    public static final int SMALL = 1;

    private final static int GRADIENT_START_ALPHA = 0;
    private final static int GRADIENT_END_ALPHA = 16;
    private final RelativeLayout container;
    private final GifImageView imageView;
    private final ImageView placeholder;
    private final TextView textView;

    private Context context;
    private
    @ResultViewSize
    int viewSize;

    private SearchResultAdapter.TapListener listener;
    private SearchResult searchResult;

    public ResultView(Context context, @ResultViewSize int viewSize) {
        super(context);
        this.context = context;

        this.viewSize = viewSize;

        int resultWidth = getDimension(0);
        int resultHeight = getDimension(1);
        setLayoutParams(new StaggeredGridLayoutManager.LayoutParams(resultWidth, resultHeight));

        placeholder = new ImageView(context);
        int placeholderSide = getDimension(4);
        RelativeLayout.LayoutParams placeholderParams = new LayoutParams(placeholderSide, placeholderSide);
        placeholderParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        placeholder.setLayoutParams(placeholderParams);
        addView(placeholder);

        container = new RelativeLayout(context);
        addView(container, new LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));

        final Animation pressedAnimation = AnimationUtils.loadAnimation(context, R.anim.search_result_pressed);
        final Animation releasedAnimation = AnimationUtils.loadAnimation(context, R.anim.search_result_released);
        imageView = new GifImageView(context) {

            @Override
            public boolean onTouchEvent(MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (searchResult != null && !searchResult.isCategory()) {
                            imageView.startAnimation(pressedAnimation);
                        }
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        if (searchResult != null && !searchResult.isCategory()) {
                            imageView.startAnimation(releasedAnimation);
                        }
                        break;
                }
                return super.onTouchEvent(event);
            }
        };

        RelativeLayout.LayoutParams imageParams = new LayoutParams(resultWidth, resultWidth);
        imageParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        imageView.setLayoutParams(imageParams);
        container.addView(imageView);


        textView = new TextView(context);
        int titleHeight = getDimension(2);
        RelativeLayout.LayoutParams titleParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, titleHeight);
        titleParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        textView.setLayoutParams(titleParams);
        textView.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Montserrat-Light.otf"));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getDimension(3));
        //TODO // FIXME: 5/2/16
        textView.setTextColor(getResources().getColor(R.color.search_result_category_title));
        textView.setGravity(Gravity.CENTER);
        container.addView(textView);

        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onTap(searchResult);
            }
        });

    }

    public void setListener(SearchResultAdapter.TapListener tapListener, SearchResult searchResult) {
        listener = tapListener;
        this.searchResult = searchResult;
    }

    public void resetView(final int placeholderRandomizer, final int position) {
        textView.setVisibility(GONE);
        placeholder.setImageDrawable(getPlaceholder(placeholderRandomizer, position));

        placeholder.startAnimation(getAppearAnimation());

//        Animator animator = AnimatorInflater.loadAnimator(context, R.animator.search_result_appear);
//        animator.setTarget(placeholder);
//        animator.start();
    }

    private Drawable getPlaceholder(int placeholderRandomizer, int position) {
        int[] colorArray = context.getResources().getIntArray(R.array.search_widget_placeholder_colors);
        int color = colorArray[(placeholderRandomizer + position) % colorArray.length];

        GradientDrawable placeholder = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{ColorUtils.setAlphaComponent(Color.WHITE, GRADIENT_START_ALPHA),
                        ColorUtils.setAlphaComponent(Color.WHITE, GRADIENT_END_ALPHA)});
        placeholder.setColor(color);
        placeholder.setShape(GradientDrawable.OVAL);
        return placeholder;
    }

    public void loadCategory(String title) {
        int width = getDimension(5);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, width);
        int margin = getDimension(6);
        params.setMargins(0, margin, 0, 0);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        imageView.setLayoutParams(params);

        textView.setText(title);
        textView.setVisibility(VISIBLE);

        startResultAnimation();
    }

    public void loadSticker() {
        int width = getDimension(0);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, width);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        imageView.setLayoutParams(params);

        textView.setVisibility(GONE);
        startResultAnimation();
    }

    private void startResultAnimation() {

        Animation disappearAnimation = getDisappearAnimation();
        disappearAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                placeholder.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        placeholder.startAnimation(disappearAnimation);

        Animation appearAnimation = getAppearAnimation();
        appearAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                container.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        container.startAnimation(appearAnimation);


//        Animator appearAnimator = AnimatorInflater.loadAnimator(context, R.animator.search_result_disappear);
//        appearAnimator.setTarget(placeholder);
//
//        appearAnimator.addListener(new Animator.AnimatorListener() {
//            @Override
//            public void onAnimationStart(Animator animation) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                container.setVisibility(VISIBLE);
//            }
//
//            @Override
//            public void onAnimationCancel(Animator animation) {
//
//            }
//
//            @Override
//            public void onAnimationRepeat(Animator animation) {
//
//            }
//        });
//
//        Animator disappearAnimator = AnimatorInflater.loadAnimator(context, R.animator.search_result_appear);
//        disappearAnimator.setTarget(container);
//
//        disappearAnimator.addListener(new Animator.AnimatorListener() {
//            @Override
//            public void onAnimationStart(Animator animation) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                placeholder.setVisibility(GONE);
//            }
//
//            @Override
//            public void onAnimationCancel(Animator animation) {
//
//            }
//
//            @Override
//            public void onAnimationRepeat(Animator animation) {
//
//            }
//        });
//
//        AnimatorSet animatorSet = new AnimatorSet();
//        animatorSet.play(appearAnimator).with(disappearAnimator);
//        animatorSet.start();
    }


    public GifImageView getImageView() {
        return imageView;
    }

    private int getDimension(int position) {
        Resources res = getResources();
        TypedArray dimensions = res.obtainTypedArray(viewSize == LARGE ?
                R.array.search_result_large_dimens : R.array.search_result_small_dimens);
        int dimension = (int) dimensions.getDimension(position, 0f);
        dimensions.recycle();
        return dimension;
    }

    private Animation getAppearAnimation() {
        Animation fadeInAnimation = AnimationUtils.loadAnimation(context, R.anim.search_widget_result_fade_in);
        fadeInAnimation.setInterpolator(PathInterpolatorCompat.create(0.3f, 0.14f, 0.36f, 1.36f));
        return fadeInAnimation;
    }

    private Animation getDisappearAnimation() {
        Animation fadeOutAnimation = AnimationUtils.loadAnimation(context, R.anim.search_widget_result_fade_out);
        fadeOutAnimation.setInterpolator(PathInterpolatorCompat.create(0.25f, 0.1f, 0.25f, 1));
        return fadeOutAnimation;
    }
}
