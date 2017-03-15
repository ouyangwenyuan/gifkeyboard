/*
 * Imoji Android SDK UI
 * Created by sajjadtabib
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

package io.imoji.sdk.editor.fragment;


import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.UUID;

import io.imoji.sdk.graphics.IG;
import io.imoji.sdk.graphics.IGEditorView;
import io.imoji.sdk.editor.ImojiCreateService;
import io.imoji.sdk.ui.R;
import io.imoji.sdk.editor.util.DisplayUtils;
import io.imoji.sdk.editor.util.EditorBitmapCache;
import io.imoji.sdk.editor.util.ScrimUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class ImojiEditorFragment extends Fragment implements ViewTreeObserver.OnGlobalLayoutListener {
    public static final String FRAGMENT_TAG = ImojiEditorFragment.class.getSimpleName();
    public static final String EDITOR_STATE_BUNDLE_ARG_KEY = "EDITOR_STATE_BUNDLE_ARG_KEY";
    public static final String IS_PROCESSING_BUNDLE_ARG_KEY = "IS_PROCESSING_BUNDLE_ARG_KEY";
    public static final String RETURN_IMMEDIATELY_BUNDLE_ARG_KEY = "RETURN_IMMEDIATELY_BUNDLE_ARG_KEY";

    static final String TAG_IMOJI_BUNDLE_ARG_KEY = "TAG_IMOJI_BUNDLE_ARG_KEY";
    private static final String LOG_TAG = ImojiEditorFragment.class.getSimpleName();


    private Handler mHandler = new Handler();
    private Bitmap mPreScaleBitmap;
    private BitmapRetainerFragment mBitmapRetainerFragment;


    private IGEditorView mIGEditorView;
    private ImageButton mUndoButton;
    private ImageButton mNextButton;
    private Toolbar mToolbar;
    private View mToolbarScrim;
    private View mBottomBarScrim;
    private ProgressBar mProgressBar;

    private byte[] mStateData;
    private boolean mDoTagging = true;
    private boolean mIsProcessing;
    private int mWidthBound = 0;
    private int mHeightBound = 0;
    private boolean mReturnImmediately;


    private IGEditorView.UndoListener mUndoListener = new IGEditorView.UndoListener() {
        @Override
        public void onUndone(boolean canUndo) {
            if (!canUndo) {
                if (mUndoButton != null && mIGEditorView != null) {
                    mUndoButton.setVisibility(View.GONE);
                }
            }
        }
    };

    public static ImojiEditorFragment newInstance(boolean tag) {
        return newInstance(tag, false);
    }

    public static ImojiEditorFragment newInstance(boolean tag, boolean returnImmediately) {
        ImojiEditorFragment f = new ImojiEditorFragment();
        Bundle args = new Bundle();
        args.putBoolean(TAG_IMOJI_BUNDLE_ARG_KEY, tag);
        args.putBoolean(RETURN_IMMEDIATELY_BUNDLE_ARG_KEY, returnImmediately);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mDoTagging = getArguments().getBoolean(TAG_IMOJI_BUNDLE_ARG_KEY, true);
            mReturnImmediately = getArguments().getBoolean(RETURN_IMMEDIATELY_BUNDLE_ARG_KEY);
        }
    }

    public void setEditorBitmap(Bitmap bitmap) {
        mPreScaleBitmap = bitmap;
        launchBitmapScaleTask();
    }

    private void launchBitmapScaleTask() {
        if (mPreScaleBitmap != null && mWidthBound != 0 && mHeightBound != 0) {
            new BitmapScaleAsyncTask(this).execute(new BitmapScaleAsyncTask.Params(mPreScaleBitmap, mWidthBound, mHeightBound));
        }
    }

    private void configureToolbar(View v) {
        //configure the toolbar
        mToolbar = (Toolbar) v.findViewById(R.id.imoji_toolbar);
        mToolbar.setNavigationIcon(R.drawable.create_back);
        mToolbar.inflateMenu(R.menu.menu_imoji_editor_fragment);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.imoji_mi_editor_help) {
                    if (isResumed()) {
                        TipsFragment f = TipsFragment.newInstance();
                        getFragmentManager().beginTransaction().addToBackStack(null).setCustomAnimations(R.anim.abc_fade_in, -1, -1, R.anim.imoji_fade_out).add(R.id.imoji_tag_container, f).commit();
                    }
                    return true;
                }
                return false;
            }
        });
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAdded()) {
                    getActivity().setResult(Activity.RESULT_CANCELED);
                    getActivity().finish();
                }
            }
        });

        mToolbarScrim = v.findViewById(R.id.imoji_toolbar_scrim);
        mBottomBarScrim = v.findViewById(R.id.imoji_bottom_bar_scrim);

        Drawable scrim = ScrimUtil.makeCubicGradientScrimDrawable(0x66000000, 32, Gravity.TOP);
        Drawable bottomBarScrim = ScrimUtil.makeCubicGradientScrimDrawable(0x66000000, 32, Gravity.BOTTOM);
        if (Build.VERSION.SDK_INT >= 16) {
            mToolbarScrim.setBackground(scrim);
            mBottomBarScrim.setBackground(bottomBarScrim);
        } else {
            mToolbarScrim.setBackgroundDrawable(scrim);
            mBottomBarScrim.setBackgroundDrawable(bottomBarScrim);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_imoji_editor, container, false);
        v.getViewTreeObserver().addOnGlobalLayoutListener(this); //listen for layout changes to get info on the parent view width/height
        return v;
    }

    private void initEditor() {

        TypedArray typedArray = getActivity().obtainStyledAttributes(new int[]{android.R.attr.windowBackground});
        int windowBackground = typedArray.getColor(0, Color.BLACK);
        typedArray.recycle();
        if (Build.VERSION.SDK_INT >= 11) { //preserve egl context on pause
            mIGEditorView.setPreserveEGLContextOnPause(true);
        }
        mIGEditorView.setGLBackgroundColor(windowBackground);
        mIGEditorView.gravitateTo(0, -1);
//        mIGEditorView.setGLBackgroundColor(Color.parseColor("#00000000"));
        mIGEditorView.setBackgroundColor(Color.TRANSPARENT);
        mIGEditorView.setImageAlpha(225);

        mIGEditorView.setStateListener(new IGEditorView.StateListener() {
            @Override
            public void onStateChanged(int igEditorState, int igEditorSubstate) {
                switch (igEditorState) {
                    case IG.EDITOR_DRAW:
                        mNextButton.setVisibility(View.GONE);
                        mIGEditorView.setImageAlpha(225);
                        break;
                    case IG.EDITOR_NUDGE:
                        mIGEditorView.setImageAlpha(0x66);
                        mNextButton.setVisibility(View.VISIBLE);
                        break;
                }

                if (mIGEditorView.canUndo()) {
                    mUndoButton.setVisibility(View.VISIBLE);
                } else {
                    mUndoButton.setVisibility(View.GONE);
                }
//                Log.d(LOG_TAG, "state changed to: " + igEditorState + " subState: " + igEditorSubstate);

                //if there's a state change, lets serialize the data
                if (mIGEditorView != null) {
                    mIGEditorView.serialize(new IGEditorView.DataListener() {
                        @Override
                        public void onDataReady(byte[] data) {
                            mStateData = data;
                        }
                    });
                }
            }
        });

        if (Build.VERSION.SDK_INT < 11 && mStateData != null) {
            mIGEditorView.deserialize(mStateData);
        }

    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {

        configureToolbar(v);

        if (savedInstanceState != null) {
            mIsProcessing = savedInstanceState.getBoolean(IS_PROCESSING_BUNDLE_ARG_KEY);
        }
        mProgressBar = (ProgressBar) v.findViewById(R.id.imoji_progress);
        mProgressBar.setVisibility(mIsProcessing ? View.VISIBLE : View.GONE);

        mUndoButton = (ImageButton) v.findViewById(R.id.imoji_ib_undo);
        mUndoButton.setVisibility(View.GONE);
        mUndoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIGEditorView.canUndo()) {
                    mIGEditorView.undo(mUndoListener);

                }
            }
        });

        mNextButton = (ImageButton) v.findViewById(R.id.imoji_ib_tag);
        mNextButton.setVisibility(View.GONE);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIGEditorView.isImojiReady() && !mIsProcessing) {
                    mIsProcessing = true;
                    mIGEditorView.getTrimmedOutputBitmap(new IGEditorView.BitmapListener() {
                        @Override
                        public void onBitmapOutputReady(Bitmap bitmap) {
                            handleBitmapReady(bitmap);

                        }
                    });

                }
            }
        });

        mIGEditorView = (IGEditorView) v.findViewById(R.id.imoji_editor_view);
        if (Build.VERSION.SDK_INT >= 11) { //init once because we will be preserving the egl context
            initEditor();
        }
    }

    private void handleBitmapReady(Bitmap bitmap) {
        if (isResumed()) {

            BitmapRetainerFragment f = findOrCreateRetainedFragment();
            f.mTrimmedBitmap = bitmap;

            EditorBitmapCache.getInstance().put(EditorBitmapCache.Keys.TRIMMED_BITMAP, bitmap);


            if (mDoTagging) {

                TagImojiFragment tagImojiFragment = TagImojiFragment.newInstance(mReturnImmediately);
                getFragmentManager().beginTransaction().addToBackStack(null).add(R.id.imoji_tag_container, tagImojiFragment).commit();
                mIsProcessing = false;

            } else {

                if (mReturnImmediately) {

                    //generate a token and pass it to the OutlineTaskFragment. The OutlineTaskFragment will finish this activity once complete
                    String token = UUID.randomUUID().toString();
                    EditorBitmapCache.getInstance().put(token, bitmap);
                    OutlineTaskFragment outlineTaskFragment = OutlineTaskFragment.newInstance(token);
                    getFragmentManager().beginTransaction().add(outlineTaskFragment, OutlineTaskFragment.FRAGMENT_TAG).commit();

                    //launch the service that will notify the server
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), ImojiCreateService.class);
                    intent.putExtra(ImojiCreateService.CREATE_TOKEN_BUNDLE_ARG_KEY, token);
                    getActivity().startService(intent);

                } else {
                    //The CreateTaskFragment will create the outline and will also notify the server. Once complete it will finish the activity
                    mProgressBar.setVisibility(View.VISIBLE);
                    CreateTaskFragment ct = (CreateTaskFragment) getFragmentManager().findFragmentByTag(CreateTaskFragment.FRAGMENT_TAG);
                    if (ct == null) {
                        ct = CreateTaskFragment.newInstance(new ArrayList<String>());
                        getFragmentManager().beginTransaction().add(ct, CreateTaskFragment.FRAGMENT_TAG).commit();
                    }
                }
            }


        } else {
            mIsProcessing = false;
        }
    }

    @Override
    public void onGlobalLayout() {
        if (Build.VERSION.SDK_INT >= 16) {
            getView().getViewTreeObserver().removeOnGlobalLayoutListener(this);
        } else {
            getView().getViewTreeObserver().removeGlobalOnLayoutListener(this);
        }

        mWidthBound = getView().getWidth();
        mHeightBound = getView().getHeight();

        launchBitmapScaleTask();

    }

    public BitmapRetainerFragment findOrCreateRetainedFragment() {
        BitmapRetainerFragment bitmapRetainerFragment = (BitmapRetainerFragment) getFragmentManager().findFragmentByTag(BitmapRetainerFragment.FRAGMENT_TAG);
        if (bitmapRetainerFragment == null) {
            bitmapRetainerFragment = new BitmapRetainerFragment();
            getFragmentManager().beginTransaction().add(bitmapRetainerFragment, BitmapRetainerFragment.FRAGMENT_TAG).commitAllowingStateLoss();
        }

        return bitmapRetainerFragment;
    }

    private void hideSystemUiVisibility() {
        if (Build.VERSION.SDK_INT >= 19) {
            final View decorView = getActivity().getWindow().getDecorView();
            int uiOptions =
                    View.SYSTEM_UI_FLAG_FULLSCREEN
//                                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
            decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int flags) {
                    if ((flags & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                            decorView.setSystemUiVisibility(
                                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                        }
                    }
                }
            });


        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT < 11) { //init because we won't have the egl context preserved
            initEditor();
        }
        mIGEditorView.onResume();

    }

    public static class BitmapRetainerFragment extends Fragment {
        public static final String FRAGMENT_TAG = BitmapRetainerFragment.class.getSimpleName();

        Bitmap mPreScaledBitmap; //store the bitmap across config changes
        Bitmap mTrimmedBitmap;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setRetainInstance(true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mIGEditorView.onPause();
    }

    static class BitmapScaleAsyncTask extends AsyncTask<BitmapScaleAsyncTask.Params, Void, Bitmap> {

        private WeakReference<ImojiEditorFragment> mFragmentWeakReference;

        public BitmapScaleAsyncTask(ImojiEditorFragment fragment) {
            mFragmentWeakReference = new WeakReference<>(fragment);
        }

        @Override
        final protected Bitmap doInBackground(Params... params) {
            Params p = params[0];
            int[] sizeInfo = DisplayUtils.getSizeWithinBounds(p.mSource.getWidth(), p.mSource.getHeight(), p.mWidthBound, p.mHeightBound, true);
            Bitmap outBitmap = Bitmap.createScaledBitmap(p.mSource, sizeInfo[0], sizeInfo[1], false);
            if (outBitmap.getConfig() != Bitmap.Config.ARGB_8888) {
                Bitmap tmp = outBitmap.copy(Bitmap.Config.ARGB_8888, true);
                outBitmap.recycle();
                outBitmap = tmp;
            }
            return outBitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            ImojiEditorFragment f = mFragmentWeakReference.get();
            if (f != null && f.mIGEditorView != null) {
//                ViewGroup.LayoutParams params = f.mIGEditorView.getLayoutParams();
//                params.width = bitmap.getWidth();
//                params.height = bitmap.getHeight();
//                f.mIGEditorView.setLayoutParams(params);
                f.mIGEditorView.setInputBitmap(bitmap);
            }
        }

        public static class Params {
            public Bitmap mSource;
            public int mWidthBound;
            public int mHeightBound;

            public Params(Bitmap source, int widthBound, int heightBound) {
                mSource = source;
                mWidthBound = widthBound;
                mHeightBound = heightBound;
            }
        }

    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mBitmapRetainerFragment = findOrCreateRetainedFragment();
        if (savedInstanceState != null) {
            mPreScaleBitmap = mBitmapRetainerFragment.mPreScaledBitmap;
            mStateData = savedInstanceState.getByteArray(EDITOR_STATE_BUNDLE_ARG_KEY);
            if (mStateData != null) {
                mIGEditorView.deserialize(mStateData);
            }

            if (mIGEditorView.canUndo()) {
                mUndoButton.setVisibility(View.VISIBLE);
            }

        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mPreScaleBitmap != null && mBitmapRetainerFragment != null) {
            mBitmapRetainerFragment.mPreScaledBitmap = mPreScaleBitmap;
        }

        outState.putByteArray(EDITOR_STATE_BUNDLE_ARG_KEY, mStateData);
        outState.putBoolean(IS_PROCESSING_BUNDLE_ARG_KEY, mIsProcessing);

        super.onSaveInstanceState(outState);

    }

}
