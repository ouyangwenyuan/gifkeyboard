package com.fotoable.inputkeyboard.core;

import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fotoable.inputkeyboard.R;
import com.fotoable.inputkeyboard.constants.MessengerConstant;
import com.fotoable.inputkeyboard.utils.ShareHelperUtils;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import io.imoji.sdk.grid.HalfScreenWidget;
import io.imoji.sdk.grid.components.SearchResultAdapter;
import io.imoji.sdk.grid.components.WidgetDisplayOptions;
import io.imoji.sdk.grid.components.WidgetListener;
import io.imoji.sdk.objects.Imoji;

/**
 * Created by ouyangwenyuan on 2017/3/10.
 */

public class KeyBoardSwitcher implements View.OnClickListener {
    private View inputView;
    private LatinKeyboardView keyboardView;
    private RelativeLayout gifParentView;

    private Button menuBtn;
    private Button searchBtn;
    private Button newBtn;
    private Button hotBtn;
    private TextView dispView;
    private Map<String, String> cacheImages = new HashMap<>();

    private SoftKeyboardService softKeyboardService;
    private static KeyBoardSwitcher switcher = new KeyBoardSwitcher();

    private KeyBoardSwitcher() {

    }

    public void init(SoftKeyboardService softKeyboardService) {
        this.softKeyboardService = softKeyboardService;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bt_hot) {
            gifParentView.setVisibility(View.VISIBLE);
            inputView.setVisibility(View.GONE);
            dispView.setText("hotest");
        } else if (v.getId() == R.id.bt_new) {
            gifParentView.setVisibility(View.VISIBLE);
            inputView.setVisibility(View.GONE);
            dispView.setText("newest");
        } else if (v.getId() == R.id.bt_search) {
            gifParentView.setVisibility(View.VISIBLE);
            inputView.setVisibility(View.GONE);
            dispView.setText("search");
        } else {
            if (gifParentView.getVisibility() == View.VISIBLE) {
                gifParentView.setVisibility(View.GONE);
                inputView.setVisibility(View.VISIBLE);
            } else {
                gifParentView.setVisibility(View.VISIBLE);
                inputView.setVisibility(View.GONE);
            }
        }
    }

    public static KeyBoardSwitcher getInstance() {
        return switcher;
    }

    public View onCreateInputView() {
        View rootView = softKeyboardService.getLayoutInflater().inflate(R.layout.input, null);
        gifParentView = (RelativeLayout) rootView.findViewById(R.id.gif_parent);
        dispView = (TextView) rootView.findViewById(R.id.test);
        menuBtn = (Button) rootView.findViewById(R.id.set_menu);
        menuBtn.setOnClickListener(this);
        inputView = rootView.findViewById(R.id.keyboard);

        searchBtn = (Button) rootView.findViewById(R.id.bt_search);
        searchBtn.setOnClickListener(this);
        hotBtn = (Button) rootView.findViewById(R.id.bt_hot);
        hotBtn.setOnClickListener(this);
        newBtn = (Button) rootView.findViewById(R.id.bt_new);
        newBtn.setOnClickListener(this);
        HalfScreenWidget halfWidget = new HalfScreenWidget(softKeyboardService, new WidgetDisplayOptions(),
                new SearchResultAdapter.ImageLoader() {
                    @Override
                    public void loadImage(ImageView target, Uri uri, final SearchResultAdapter.ImageLoaderCallback callback) {
                        Ion.with(target).load(uri.toString()).setCallback(new FutureCallback<ImageView>() {
                            @Override
                            public void onCompleted(Exception e, ImageView result) {
                                callback.updateImageView();
                            }
                        });
                    }
                });
        halfWidget.setWidgetListener(new WidgetListener() {
            @Override
            public void onCloseButtonTapped() {

            }

            @Override
            public void onStickerTapped(Imoji imoji) {
                final Uri uri = imoji.getStandardFullSizeUri();
                final String url = uri.toString();
                if (cacheImages.containsKey(url)) {
                    String newurl = cacheImages.get(url);
                    String key = softKeyboardService.getCurrentPackage();
                    if (softKeyboardService.getPackageName().equals(key)) {
                        ShareHelperUtils.shareContentWithChooser(softKeyboardService, Uri.parse(newurl));
                    } else {
                        if (MessengerConstant.ALL_KNOWN_MESSENGERS.get(key) == null) {
                            ShareHelperUtils.shareToApp(softKeyboardService, key, "NONE", newurl);
                        } else {
                            ShareHelperUtils.shareToApp(softKeyboardService, key, softKeyboardService.getString(MessengerConstant.ALL_KNOWN_MESSENGERS.get(key)), newurl);
                        }
                    }
                } else {
                    Ion.with(softKeyboardService).load(uri.toString()).asInputStream().setCallback(new FutureCallback<InputStream>() {
                        @Override
                        public void onCompleted(Exception ex, InputStream result) {
//                        if (pd != null) {
//                            pd.dismiss();
//                        }
                            File dir = new File(Environment.getExternalStorageDirectory() + "/gifkeyboard");
                            if (!dir.exists()) {
                                dir.mkdirs();
                            }

                            String capturePath = dir.getAbsolutePath() + "/" + uri.getLastPathSegment();/*System.currentTimeMillis()*/
                            File imageFile = new File(capturePath);

                            byte[] buf = new byte[1024];
                            int len = 0;
                            BufferedOutputStream bos = null;
                            try {
                                bos = new BufferedOutputStream(new FileOutputStream(imageFile));
                                while ((len = result.read(buf)) != -1) {
                                    bos.write(buf, 0, len);
                                }
                        /* 调用flush()方法，更新BufferStream */
                                bos.flush();

                        /* 结束OutputStream */
//                            bos.close();

                            } catch (FileNotFoundException e) {
                                e.printStackTrace();

                            } catch (IOException e) {
                                e.printStackTrace();
                            } finally {
                                if (bos != null) {
                                    try {
                                        bos.close();
                                    } catch (IOException e1) {
                                        e1.printStackTrace();
                                    }
                                }
                                if (result != null) {
                                    try {
                                        result.close();
                                    } catch (IOException e1) {
                                        e1.printStackTrace();
                                    }
                                }
                                Uri pathuri = Uri.fromFile(imageFile);
                                cacheImages.put(url, pathuri.toString());

                                String key = softKeyboardService.getCurrentPackage();
                                if (softKeyboardService.getPackageName().equals(key)) {
                                    ShareHelperUtils.shareContentWithChooser(softKeyboardService, pathuri);
                                } else {
                                    if (MessengerConstant.ALL_KNOWN_MESSENGERS.get(key) == null) {
                                        ShareHelperUtils.shareToApp(softKeyboardService, key, "NONE", pathuri.toString());
                                    } else {
                                        ShareHelperUtils.shareToApp(softKeyboardService, key, softKeyboardService.getString(MessengerConstant.ALL_KNOWN_MESSENGERS.get(key)), pathuri.toString());
                                    }
                                }
                            }
                        }
                    });
                }
            }
        });
        gifParentView.addView(halfWidget);
        return rootView;
    }
}
