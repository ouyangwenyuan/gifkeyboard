package com.fotoable.gifkeyboard.ui;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.felipecsl.gifimageview.library.GifImageView;
import com.fotoable.gifkeyboard.R;
import com.fotoable.inputkeyboard.utils.ImageCacher;
import com.fotoable.inputkeyboard.utils.ImageManagerutils;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;
import cn.droidlover.xdroidmvp.imageloader.GlideLoader;
import cn.droidlover.xdroidmvp.mvp.XActivity;

/**
 * Created by ouyangwenyuan on 2017/3/14.
 */

public class DetailActivity extends XActivity {

    @BindView(R.id.bt_save)
    Button saveBtn;
    @BindView(R.id.bt_share)
    Button shareBtn;
    @BindView(R.id.iv_close)
    ImageView closeView;
    @BindView(R.id.iv_big_image)
    GifImageView imageView;
    private String imgUri;
    private Bitmap bmp;

    @Override
    public void initData(Bundle savedInstanceState) {
        imgUri = getIntent().getStringExtra("imgUri");
        Ion.with(imageView).load(imgUri).setCallback(new FutureCallback<ImageView>() {
            @Override
            public void onCompleted(Exception e, ImageView result) {

            }
        });
    }

    @OnClick({R.id.bt_share, R.id.bt_save, R.id.iv_close})
    public void clickEvent(View v) {
        switch (v.getId()) {
            case R.id.bt_share:
                ImageCacher.getInstance().loadImageForShare(this, Uri.parse(imgUri), this.getPackageName());
                break;
            case R.id.bt_save:
                try {
                    File file = ImageManagerutils.writePic2SDCard(bmp, imgUri);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                break;
            case R.id.iv_close:
                finish();
                break;
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_detail;
    }

    @Override
    public Object newP() {
        return null;
    }
}
