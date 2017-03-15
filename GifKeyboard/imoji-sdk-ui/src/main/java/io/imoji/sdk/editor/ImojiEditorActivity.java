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

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;

import java.io.IOException;

import io.imoji.sdk.ui.R;
import io.imoji.sdk.editor.fragment.ImojiEditorFragment;
import io.imoji.sdk.editor.util.EditorBitmapCache;

public class ImojiEditorActivity extends AppCompatActivity {

    private final static int PICK_IMAGE_REQUEST_CODE = 1002;
    public static final int START_EDITOR_REQUEST_CODE = 1001;

    public static final String IMOJI_CREATION_FINISHED_BROADCAST_ACTION = "IMOJI_CREATION_FINISHED_BROADCAST_ACTION";
    public static final String IMOJI_MODEL_BUNDLE_ARG_KEY = "IMOJI_MODEL_BUNDLE_ARG_KEY";
    public static final String CREATE_TOKEN_BUNDLE_ARG_KEY = "CREATE_TOKEN_BUNDLE_ARG_KEY";
    public static final String TAG_IMOJI_BUNDLE_ARG_KEY = "TAG_IMOJI_BUNDLE_ARG_KEY";
    public static final String RETURN_IMMEDIATELY_BUNDLE_ARG_KEY = "RETURN_IMMEDIATELY_BUNDLE_ARG_KEY";
    public static final String IMOJI_EDITOR_IMAGE_CONTENT_URI = "IMOJI_EDITOR_IMAGE_CONTENT_URI";

    private ImojiEditorFragment mImojiEditorFragment;
    private boolean tagImojis = true;
    private boolean returnImmediately = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imoji_editor);

        if (savedInstanceState == null) {
            tagImojis = getIntent().getBooleanExtra(TAG_IMOJI_BUNDLE_ARG_KEY, true);
            returnImmediately = getIntent().getBooleanExtra(RETURN_IMMEDIATELY_BUNDLE_ARG_KEY, false);

            Bitmap inputBitmap = EditorBitmapCache.getInstance().get(EditorBitmapCache.Keys.INPUT_BITMAP);
            if(getIntent().hasExtra(IMOJI_EDITOR_IMAGE_CONTENT_URI)){
                Uri uri = Uri.parse(getIntent().getStringExtra(IMOJI_EDITOR_IMAGE_CONTENT_URI));
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    createFragment(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (inputBitmap == null) {
                pickImageFromGallery();
            }else {
                EditorBitmapCache.getInstance().remove(EditorBitmapCache.Keys.INPUT_BITMAP);
                createFragment(inputBitmap);
            }
        } else {
            mImojiEditorFragment = (ImojiEditorFragment) getSupportFragmentManager().findFragmentByTag(ImojiEditorFragment.FRAGMENT_TAG);
        }
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.imoji_editor_activity_image_picker_title)), PICK_IMAGE_REQUEST_CODE);
    }

    private void createFragment(Bitmap inputBitmap) {
        mImojiEditorFragment = ImojiEditorFragment.newInstance(tagImojis, returnImmediately);
        mImojiEditorFragment.setEditorBitmap(inputBitmap);
        getSupportFragmentManager().beginTransaction().add(R.id.container, mImojiEditorFragment, ImojiEditorFragment.FRAGMENT_TAG).commit();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                createFragment(bitmap);
            } catch (IOException e) {
                finishActivity();
            }
        } else {
            finishActivity();
        }
    }

    private void finishActivity() {
        setResult(Activity.RESULT_CANCELED, null);
        finish();
    }
}
