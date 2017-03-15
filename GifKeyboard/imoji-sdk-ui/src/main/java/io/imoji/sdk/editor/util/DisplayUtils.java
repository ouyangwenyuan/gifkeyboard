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

import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

/**
 * Created by sajjadtabib on 10/1/15.
 */
public class DisplayUtils {


    public static float getAspectRatio(Point point) {
        return point.x / point.y;
    }

    public static float getWindowAspectRatio(Context context) {
        Point p = getWindowSize(context);
        return getAspectRatio(p);
    }

    public static Point getWindowSize(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point windowDisplaySize = new Point();
        display.getSize(windowDisplaySize);
        return windowDisplaySize;
    }

    public static int[] getSizeWithinBounds(int width, int height, int boundsWidth, int boundsHeight, boolean expandToFitBounds){
        int[] size = new int[2];

        //if we fit within the bounds then don't scale
        if(!expandToFitBounds && (width <= boundsWidth && height <= boundsHeight)){
            size[0] = width;
            size[1] = height;
            return size;
        }

        //get the aspect ratio of the original size
        float originalAspectRatio = (float)width / (float) height;
        float boundsAspectRatio = (float)boundsWidth / (float) boundsHeight;

        if(originalAspectRatio > boundsAspectRatio){
            size[0] = boundsWidth;
            size[1] = (int)( (float)boundsWidth / originalAspectRatio);
        }else{
            size[1] = boundsHeight;
            size[0] = (int)( (float)boundsHeight * originalAspectRatio);
        }

        return size;
    }
}
