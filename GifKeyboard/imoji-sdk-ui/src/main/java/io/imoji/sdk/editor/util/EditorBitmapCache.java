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

/**
 * Created by sajjadtabib on 9/23/15.
 */
public final class EditorBitmapCache extends android.support.v4.util.LruCache<String, Bitmap> {

    public interface Keys{
        String INPUT_BITMAP = "INPUT_BITMAP";
        String TRIMMED_BITMAP = "TRIMMED_BITMAP";
        String OUTLINED_BITMAP = "OUTLINED_BITMAP";
    }

    private static EditorBitmapCache sEditorBitmapCache;

    public static EditorBitmapCache getInstance() {
        if (sEditorBitmapCache == null) {
            synchronized (EditorBitmapCache.class) {
                if (sEditorBitmapCache == null) {
                    sEditorBitmapCache = new EditorBitmapCache(3);
                }
            }
        }

        return sEditorBitmapCache;
    }

    /**
     * @param maxSize for caches that do not override {@link #sizeOf}, this is
     *                the maximum number of entries in the cache. For all other caches,
     *                this is the maximum sum of the sizes of the entries in this cache.
     */
    private EditorBitmapCache(int maxSize) {
        super(maxSize);
    }

    public void clearNonOutlinedBitmaps() {
        remove(EditorBitmapCache.Keys.TRIMMED_BITMAP);
        remove(EditorBitmapCache.Keys.INPUT_BITMAP);
    }

}
