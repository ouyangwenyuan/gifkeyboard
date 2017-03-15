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

package io.imoji.sdk.editor;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import io.imoji.sdk.ApiTask;
import io.imoji.sdk.ImojiSDK;
import io.imoji.sdk.Session;
import io.imoji.sdk.editor.util.EditorBitmapCache;
import io.imoji.sdk.objects.Imoji;
import io.imoji.sdk.response.CreateImojiResponse;

/**
 * Created by sajjadtabib on 10/21/15.
 */
public class ImojiCreateService extends IntentService {

    public static final String IMOJI_CREATE_INTERNAL_INTENT_ACTION = "IMOJI_CREATE_INTERNAL_INTENT_ACTION";

    public static final String STATUS_BUNDLE_ARG_KEY = "STATUS_BUNDLE_ARG_KEY";
    public static final String IMOJI_MODEL_BUNDLE_ARG_KEY = "IMOJI_MODEL_BUNDLE_ARG_KEY";

    public static final String CREATE_TOKEN_BUNDLE_ARG_KEY = "CREATE_TOKEN_BUNDLE_ARG_KEY";
    public static final String TAGS_BUNDLE_ARG_KEY = "TAGS_BUNDLE_ARG_KEY";

    private Session imojiSession;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public ImojiCreateService() {
        super(ImojiCreateService.class.getSimpleName());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.imojiSession = ImojiSDK.getInstance().createSession(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final String token = intent.getStringExtra(CREATE_TOKEN_BUNDLE_ARG_KEY); //the token that will contain the bitmap in memory and also used to bind to the newly created imoji
        List<String> tags = intent.getStringArrayListExtra(TAGS_BUNDLE_ARG_KEY);

        Bitmap b = EditorBitmapCache.getInstance().remove(token);
        if (b == null) {
            //notify failure
            notifyFailure(token);
            return;
        }


        final CountDownLatch latch = new CountDownLatch(1);
        this.imojiSession
                .createImojiWithRawImage(b, b, tags)
                .executeAsyncTask(new ApiTask.WrappedAsyncTask<CreateImojiResponse>() {
                    @Override
                    protected void onPostExecute(CreateImojiResponse createImojiResponse) {
                        notifySuccess(createImojiResponse.getImoji(), token);
                        latch.countDown();
                    }

                    @Override
                    protected void onError(@NonNull Throwable error) {
                        notifyFailure(token);
                        latch.countDown();
                    }
                });

        waitForCreateToFinish(latch, token);

        //clear editor bitmaps excpet for the outlined one
        EditorBitmapCache.getInstance().clearNonOutlinedBitmaps();

    }

    private void waitForCreateToFinish(CountDownLatch latch, String token) {
        //wait for the response to come back because we rely on it to notify using the broadcast receivers
        try {
            latch.await(30000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
            notifyFailure(token);
        }
    }

    private void notifyFailure(String token) {

        Intent intent = new Intent();
        intent.setAction(IMOJI_CREATE_INTERNAL_INTENT_ACTION);
        intent.putExtra(STATUS_BUNDLE_ARG_KEY, false);
        intent.putExtra(CREATE_TOKEN_BUNDLE_ARG_KEY, token);

        LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(intent);

    }

    private void notifySuccess(Imoji imoji, String token) {

        Intent intent = new Intent();
        intent.setAction(IMOJI_CREATE_INTERNAL_INTENT_ACTION);
        intent.putExtra(STATUS_BUNDLE_ARG_KEY, true);
        intent.putExtra(CREATE_TOKEN_BUNDLE_ARG_KEY, token);
        intent.putExtra(IMOJI_MODEL_BUNDLE_ARG_KEY, imoji);

        LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(intent);
    }
}
