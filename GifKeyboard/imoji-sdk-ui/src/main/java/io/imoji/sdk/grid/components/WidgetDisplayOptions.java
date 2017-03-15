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

import android.support.annotation.NonNull;

import io.imoji.sdk.objects.RenderingOptions;

/**
 * Created by engind on 5/10/16.
 * A simple configuration POJO for developers pass their preferences
 */
public class WidgetDisplayOptions {

    private boolean includeRecentsAndCreate = false;

    @NonNull
    private RenderingOptions renderingOptions;


    public WidgetDisplayOptions() {
        this(RenderingOptions.borderedWebThumbnail());
    }

    public WidgetDisplayOptions(@NonNull RenderingOptions renderingOptions) {
        this.renderingOptions = renderingOptions;
    }

    public boolean isIncludeRecentsAndCreate() {
        return includeRecentsAndCreate;
    }

    public WidgetDisplayOptions setIncludeRecentsAndCreate(boolean includeRecentsAndCreate) {
        this.includeRecentsAndCreate = includeRecentsAndCreate;
        return this;
    }

    /**
     * @return Gets the RenderingOptions used for displaying content in the Search Widgets
     */
    @NonNull
    public RenderingOptions getRenderingOptions() {
        return renderingOptions;
    }

    /**
     * Sets the RenderingOptions to use for displaying content in the Search Widgets
     *
     * @param renderingOptions The Rendering options to use.
     */
    public void setRenderingOptions(@NonNull RenderingOptions renderingOptions) {
        this.renderingOptions = renderingOptions;
    }
}
