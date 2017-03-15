/*
 * Imoji Android SDK UI
 * Created by thor
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

package io.imoji.sdk.graphics;

import android.graphics.Bitmap;

/**
 * Created by thor on 18/05/15.
 */
public class IG {
    static {
        System.loadLibrary("imojigraphics");
    }

    // Any IG_FUNCTION declared in the IG*.h header files can be imported with this file.
    // See IGCommon.h and the other header files for further documentation.

    // IGContext.h
    public static native int ContextCreate();

    public static native int ContextCreateHosted();

    public static native void ContextDestroy(int igContext);

    public static native void ContextMakeCurrent(int igContext);


    // IGImage.h
    public static native int ImageCreate(int igContext, int width, int height);

    public static native void ImageDestroy(int igImage);

    public static native int ImageGetWidth(int igImage);

    public static native int ImageGetHeight(int igImage);

    public static native int ImagePad(int igImage, int perSideX, int perSideY);

    public static native Bitmap ImageToNative(int igImage);

    public static native int ImageFromNative(int igContext, Bitmap bitmap, int padding);


    // IGBorder.h
    public static final int BORDER_CLASSIC = 1;
    public static final int BORDER_LITE = 2;
    public static final int BORDER_DEBUG = 3;

    public static final int BORDER_ELEMENT_FILL = 1;
    public static final int BORDER_ELEMENT_STROKE = 2;

    public static native int BorderCreatePreset(int width, int height, int igBorderPreset);

    public static native int BorderCreate(int width, int height);

    public static native void BorderDestroy(int igBorder, boolean destroyElements);

    public static native void BorderSetEdgePaths(int igBorder, int igPaths);

    public static native void BorderSetShadow(int igBorder, float diameter, float offsetX, float offsetY, int red, int green, int blue, int alpha);

    public static native void BorderAddElement(int igBorder, int igBorderElement);

    public static native void BorderElementCreate(int igBorderElementType, float offset, float width, int red, int green, int blue, int alpha);

    public static native void BorderElementDestroy(int igBorderElement);

    public static native int BorderGetPadding(int igBorder);

    public static native int BorderRender(int igBorder, int igInputImage, int igOutputImage, float x, float y, float scaleX, float scaleY);

    // IGWebP.h
    public static native int WebPGetPaths(byte[] data, float x, float y, float width, float height);

    // IGEditor.h
    public static final int TOUCH_BEGAN = 1; // IGTouchType
    public static final int TOUCH_MOVED = 2;
    public static final int TOUCH_ENDED = 3;
    public static final int TOUCH_CANCELED = 4;

    public static final int EDITOR_DRAW = 1; // IGEditorState
    public static final int EDITOR_NUDGE = 2;

    public static final int EDITOR_IDLE = 1; // IGEditorSubstate
    public static final int EDITOR_DRAG = 2;
    public static final int EDITOR_PINCH = 3;
    public static final int EDITOR_HOLD = 4;

    public static native int EditorCreate(int igInputImage);

    public static native void EditorDestroy(int igEditor);

    public static native void EditorDisplay(int igEditor);

    public static native void EditorSetBackgroundColor(int igEditor, int red, int green, int blue, int alpha);

    public static native void EditorSetStrokeColor(int igEditor, int red, int green, int blue, int alpha);

    public static native void EditorSetDotColor(int igEditor, int red, int green, int blue, int alpha);

    public static native void EditorSetStrokeWidth(int igEditor, float strokeWidth);

    public static native void EditorSetImageAlpha(int igEditor, int alpha);

    public static native int EditorGetState(int igEditor);

    public static native int EditorGetSubstate(int igEditor);

    public static native void EditorTouchEvent(int igEditor, int igTouchType, int igTouchID, float x, float y);

    public static native void EditorScrollTo(int igEditor, float x, float y);

    public static native void EditorZoomTo(int igEditor, float zoom);

    public static native void EditorGravitateTo(int igEditor, float x, float y);

    public static native boolean EditorCanUndo(int igEditor);

    public static native void EditorUndo(int igEditor);

    public static native int EditorGetEdgePaths(int igEditor);

    public static native int EditorGetOutputImage(int igEditor);

    public static native int EditorGetTrimmedOutputImage(int igEditor);

    public static native boolean EditorImojiIsReady(int igEditor);

    public static native byte[] EditorSerialize(int igEditor);

    public static native void EditorDeserialize(int igEditor, byte[] data);
}
