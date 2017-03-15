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
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.List;

import io.imoji.sdk.ApiTask;
import io.imoji.sdk.ImojiSDK;
import io.imoji.sdk.Session;
import io.imoji.sdk.objects.Imoji;
import io.imoji.sdk.response.CreateImojiResponse;
import io.imoji.sdk.editor.ImojiEditorActivity;
import io.imoji.sdk.editor.util.OutlineAsyncTask;
import io.imoji.sdk.editor.util.EditorBitmapCache;

/**
 * Created by sajjadtabib on 10/19/15.
 */
public class CreateTaskFragment extends Fragment implements OutlineAsyncTask.OutlinedBitmapReadyListener {

    private static final int IMOJI_WIDTH_BOUND = 320;
    private static final int IMOJI_HEIGHT_BOUND = 320;

    public static final String FRAGMENT_TAG = CreateTaskFragment.class.getSimpleName();

    public static final String TAGS_BUNDLE_ARG_KEY = "TAGS_BUNDLE_ARG_KEY";
    public static final String CREATE_OUTLINE_BITMAP_BUNDLE_ARG_KEY = "CREATE_OUTLINE_BITMAP_BUNDLE_ARG_KEY";
    private List<String> mTags;
    private boolean mIsDone;
    private Imoji mResultImoji;
    private Session mImojiSession;

    public static CreateTaskFragment newInstance(ArrayList<String> tags) {
        return newInstance(tags, true);
    }

    public static CreateTaskFragment newInstance(ArrayList<String> tags, boolean createOutlineBitmap) {
        CreateTaskFragment f = new CreateTaskFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(TAGS_BUNDLE_ARG_KEY, tags);
        args.putBoolean(CREATE_OUTLINE_BITMAP_BUNDLE_ARG_KEY, createOutlineBitmap);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (mIsDone) {
            if (mResultImoji == null) {
                notifyFailure(activity);
            } else {
                notifySuccess(mResultImoji, activity);
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        mTags = getArguments().getStringArrayList(TAGS_BUNDLE_ARG_KEY);

        mImojiSession = ImojiSDK.getInstance().createSession(getActivity().getApplicationContext());

        Bitmap b = EditorBitmapCache.getInstance().get(EditorBitmapCache.Keys.TRIMMED_BITMAP);
        if (b == null) {
            notifyFailure(getActivity());
            return;
        }

        boolean createOutlinedBitmap = getArguments().getBoolean(CREATE_OUTLINE_BITMAP_BUNDLE_ARG_KEY);

        //start the task

        if (createOutlinedBitmap) {
            OutlineAsyncTask task = new OutlineAsyncTask(b, IMOJI_WIDTH_BOUND, IMOJI_HEIGHT_BOUND, this);
            if (Build.VERSION.SDK_INT >= 11) {
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                task.execute();
            }
        } else {
            Bitmap outlined = EditorBitmapCache.getInstance().get(EditorBitmapCache.Keys.TRIMMED_BITMAP);
            if (outlined == null) {
                notifyFailure(getActivity());
                return;
            }
            createImoji(outlined);
        }
    }


    @Override
    public void onOutlinedBitmapReady(Bitmap outlinedBitmap) {
        EditorBitmapCache.getInstance().put(EditorBitmapCache.Keys.OUTLINED_BITMAP, outlinedBitmap);
        createImoji(outlinedBitmap);
    }

    private void createImoji(Bitmap outlinedBitmap) {
        mImojiSession.createImojiWithRawImage(outlinedBitmap, outlinedBitmap, mTags)
                .executeAsyncTask(new ApiTask.WrappedAsyncTask<CreateImojiResponse>() {
                    @Override
                    protected void onPostExecute(CreateImojiResponse createImojiResponse) {
                        mIsDone = true;
                        mResultImoji = createImojiResponse.getImoji();
                        if (isAdded()) {
                            Activity a = getActivity();
                            notifySuccess(mResultImoji, a);
                        }
                    }

                    @Override
                    protected void onError(@NonNull Throwable error) {
                        mIsDone = true;
                        if (isAdded()) {
                            Activity a = getActivity();
                            notifyFailure(a);
                        }
                    }
                });
    }

    private void notifyFailure(Activity a) {
        EditorBitmapCache.getInstance().clearNonOutlinedBitmaps();
        a.setResult(Activity.RESULT_CANCELED, null);
        a.finish();
    }

    private void notifySuccess(Imoji result, Activity a) {
        EditorBitmapCache.getInstance().clearNonOutlinedBitmaps();
        Intent intent = new Intent();
        intent.putExtra(ImojiEditorActivity.IMOJI_MODEL_BUNDLE_ARG_KEY, result);
        a.setResult(Activity.RESULT_OK, intent);
        LocalBroadcastManager.getInstance(a).sendBroadcast(new Intent(ImojiEditorActivity.IMOJI_CREATION_FINISHED_BROADCAST_ACTION));
        a.finish();
    }
}
