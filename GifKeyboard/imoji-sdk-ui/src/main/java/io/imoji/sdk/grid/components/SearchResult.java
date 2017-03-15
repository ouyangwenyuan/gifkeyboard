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

import android.net.Uri;

import io.imoji.sdk.objects.Category;
import io.imoji.sdk.objects.Imoji;
import io.imoji.sdk.objects.RenderingOptions;

/**
 * Created by engind on 4/29/16.
 */
public class SearchResult {

    private Imoji imoji;

    private Category category;

    public SearchResult(Imoji imoji) {
        this.imoji = imoji;
    }

    public SearchResult(Category category) {
        this.category = category;
    }

    public Imoji getImoji() {
        return imoji;
    }

    public Category getCategory() {
        return category;
    }

    public Uri getThumbnailUri(WidgetDisplayOptions options) {
        Imoji thumbnailImoji = this.imoji;
        if (isCategory()) {
            thumbnailImoji = category.getPreviewImoji();
        }
        if (thumbnailImoji.hasAnimationCapability()) {
            return thumbnailImoji.getStandardThumbnailUri(true);
        } else {
            return thumbnailImoji.urlForRenderingOption(options.getRenderingOptions());
        }
    }

    public String getTitle() {
        String title = null;
        if (isCategory()) {
            title = category.getTitle();
        }
        return title;
    }

    public boolean isCategory() {
        return category != null && imoji == null;
    }
}
