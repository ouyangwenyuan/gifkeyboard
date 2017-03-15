package com.fotoable.gifkeyboard.ui;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.fotoable.gifkeyboard.R;
import com.fotoable.inputkeyboard.utils.MyLog;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import butterknife.BindView;
import cn.droidlover.xdroidmvp.mvp.XFragment;
import cn.droidlover.xdroidmvp.mvp.XLazyFragment;
import cn.droidlover.xdroidmvp.router.Router;
import io.imoji.sdk.grid.FullScreenWidget;
import io.imoji.sdk.grid.components.SearchResultAdapter;
import io.imoji.sdk.grid.components.WidgetDisplayOptions;
import io.imoji.sdk.grid.components.WidgetListener;
import io.imoji.sdk.objects.Imoji;
import io.imoji.sdk.objects.RenderingOptions;

/**
 * Created by ouyangwenyuan on 2017/3/14.
 */

public class ImojiFragment extends XLazyFragment {
    @BindView(R.id.widget_main_view)
    RelativeLayout widgetParent;

    @Override
    public void initData(Bundle savedInstanceState) {

        SearchResultAdapter.ImageLoader imageLoader = new SearchResultAdapter.ImageLoader() {
            @Override
            public void loadImage(@NonNull ImageView target, @NonNull Uri uri, @NonNull final SearchResultAdapter.ImageLoaderCallback callback) {
//                load by Ion
                Ion.with(target).load(uri.toString()).setCallback(new FutureCallback<ImageView>() {
                    @Override
                    public void onCompleted(Exception e, ImageView result) {
                        callback.updateImageView();
                    }
                });

            }
        };


        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        RenderingOptions renderingOptions = new RenderingOptions(
//                RenderingOptions.BorderStyle.Sticker,// :
                RenderingOptions.BorderStyle.None,
                RenderingOptions.ImageFormat.Png,
                RenderingOptions.Size.Thumbnail
        );
        WidgetDisplayOptions options = new WidgetDisplayOptions(renderingOptions);
        options.setIncludeRecentsAndCreate(true);
        FullScreenWidget widget = new FullScreenWidget(getActivity(), options, imageLoader);
        widgetParent.addView(widget, params);
        widget.setWidgetListener(new WidgetListener() {
            @Override
            public void onCloseButtonTapped() {
                NavUtils.navigateUpFromSameTask(getActivity());
            }

            @Override
            public void onStickerTapped(Imoji imoji) {
                Router.newIntent(getActivity()).to(DetailActivity.class).putString("imgUri", imoji.getStandardFullSizeUri().toString()).launch();
                MyLog.i(imoji.toString());
            }
        });

    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_enter;
    }

    @Override
    public Object newP() {
        return null;
    }

    public static ImojiFragment newInstance() {
        return new ImojiFragment();
    }

}
