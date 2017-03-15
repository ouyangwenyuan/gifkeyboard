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
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import java.util.List;

import io.imoji.sdk.editor.ImojiEditorActivity;
import io.imoji.sdk.editor.util.OutlineAsyncTask;
import io.imoji.sdk.editor.util.EditorBitmapCache;

/**
 * Created by sajjadtabib on 10/21/15.
 */
public class OutlineTaskFragment extends Fragment implements OutlineAsyncTask.OutlinedBitmapReadyListener {


    private static final int IMOJI_WIDTH_BOUND = 320;
    private static final int IMOJI_HEIGHT_BOUND = 320;

    public static final String FRAGMENT_TAG = OutlineTaskFragment.class.getSimpleName();
    private static final String LOG_TAG = OutlineTaskFragment.class.getSimpleName();
    public static final String CREATE_TOKEN_BUNDLE_ARG_KEY = "CREATE_TOKEN_BUNDLE_ARG_KEY";

    public static OutlineTaskFragment newInstance(String token) {
        OutlineTaskFragment f = new OutlineTaskFragment();

        Bundle args = new Bundle();
        args.putString(CREATE_TOKEN_BUNDLE_ARG_KEY, token);

        f.setArguments(args);

        return f;
    }

    private String mToken;
    private List<String> mTags;
    private boolean mIsDone;
    private boolean mSuccess;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (mIsDone) {
            if (mSuccess) {
                notifySuccess(mToken, activity);
            } else {
                notifyFailure(activity);
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        mToken = getArguments().getString(CREATE_TOKEN_BUNDLE_ARG_KEY);

        Bitmap b = EditorBitmapCache.getInstance().get(mToken);
        if (b == null) {
            Log.w(LOG_TAG, "token was not set to create outline");
            notifyFailure(getActivity());
            return;
        }

        OutlineAsyncTask task = new OutlineAsyncTask(b, IMOJI_WIDTH_BOUND, IMOJI_HEIGHT_BOUND, this);
        if (Build.VERSION.SDK_INT >= 11) {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            task.execute();
        }
    }

    private void notifyFailure(Activity a) {
        EditorBitmapCache.getInstance().clearNonOutlinedBitmaps();
        a.setResult(Activity.RESULT_CANCELED, null);
        a.finish();
    }

    private void notifySuccess(String token, Activity a) {
        EditorBitmapCache.getInstance().clearNonOutlinedBitmaps();
        Intent intent = new Intent();
        intent.putExtra(ImojiEditorActivity.CREATE_TOKEN_BUNDLE_ARG_KEY, token);
        a.setResult(Activity.RESULT_OK, intent);
        a.finish();
    }

    @Override
    public void onOutlinedBitmapReady(Bitmap outlinedBitmap) {

        //remove the trimmed bitmap from the cache that is attached to the token
        EditorBitmapCache.getInstance().remove(mToken);

        if (outlinedBitmap == null) {
            mSuccess = false;
            mIsDone = true;
            if (isAdded()) {
                notifyFailure(getActivity());
            }
            return;
        }

        EditorBitmapCache.getInstance().put(EditorBitmapCache.Keys.OUTLINED_BITMAP, outlinedBitmap);

        mIsDone = true;
        mSuccess = true;

        if (isAdded()) {
            notifySuccess(mToken, getActivity());
        }
    }
}
