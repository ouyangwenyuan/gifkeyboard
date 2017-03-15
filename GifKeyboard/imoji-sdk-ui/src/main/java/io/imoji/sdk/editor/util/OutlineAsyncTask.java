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

package io.imoji.sdk.editor.util;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;

import io.imoji.sdk.graphics.IG;

/**
 * Created by sajjadtabib on 10/19/15.
 */
public class OutlineAsyncTask extends AsyncTask<Void, Void, Bitmap> {

    private final Bitmap mImojiBitmap;
    private final int mWidth;
    private final int mHeight;

    private WeakReference<OutlinedBitmapReadyListener> mListenerWeakReference;

    public OutlineAsyncTask(Bitmap imojiBitmap, int width, int height, OutlinedBitmapReadyListener f) {
        mListenerWeakReference = new WeakReference<>(f);
        mImojiBitmap = imojiBitmap;
        mWidth = width;
        mHeight = height;
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        int igContext = IG.ContextCreate();
        if (igContext == 0) {
            System.err.println("Unable to create IG context");
            return null;
        }


        int[] size = DisplayUtils.getSizeWithinBounds(mImojiBitmap.getWidth(), mImojiBitmap.getHeight(), mWidth, mHeight, true);
        Bitmap b = Bitmap.createScaledBitmap(mImojiBitmap, size[0], size[1], false);
        int igBorder = IG.BorderCreatePreset(mImojiBitmap.getWidth(), mImojiBitmap.getHeight(), IG.BORDER_CLASSIC);
        int padding = IG.BorderGetPadding(igBorder);

        int igInputImage = IG.ImageFromNative(igContext, mImojiBitmap, 1);
        int igOutputImage = IG.ImageCreate(igContext, IG.ImageGetWidth(igInputImage) + padding * 2, IG.ImageGetHeight(igInputImage) + padding * 2);
        IG.BorderRender(igBorder, igInputImage, igOutputImage, padding - 1, padding - 1, 1, 1);
        Bitmap outputBitmap = IG.ImageToNative(igOutputImage);
        IG.ImageDestroy(igOutputImage);
        IG.ImageDestroy(igInputImage);
        IG.BorderDestroy(igBorder, true);
        IG.ContextDestroy(igContext);

        return outputBitmap;

    }


    @Override
    protected void onPostExecute(Bitmap b) {
        OutlinedBitmapReadyListener outlinedBitmapReadyListener = mListenerWeakReference.get();

        if (outlinedBitmapReadyListener != null) {
            outlinedBitmapReadyListener.onOutlinedBitmapReady(b);
        }
    }

    public interface OutlinedBitmapReadyListener {
        void onOutlinedBitmapReady(Bitmap outlinedBitmap);
    }
}
