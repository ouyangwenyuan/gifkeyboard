package com.fotoable.inputkeyboard.utils;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;

import com.fotoable.inputkeyboard.constants.Config;
import com.fotoable.inputkeyboard.constants.MessengerConstant;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by ouyangwenyuan on 2017/3/13.
 */

public class ImageCacher {
    static final Handler mainHandler = new Handler(Looper.getMainLooper());
    static int availableProcessors = Runtime.getRuntime().availableProcessors();
    static ExecutorService ioExecutorService = Executors.newFixedThreadPool(4);
    static ExecutorService bitmapExecutorService = availableProcessors > 2 ? Executors.newFixedThreadPool(availableProcessors - 1) : Executors.newFixedThreadPool(1);
//    static HashMap<String, Ion> instances = new HashMap<String, Ion>();

    private static Map<String, String> fileUris = new HashMap();
    private static Map<String, String> contentUris = new HashMap<>();

    private ImageCacher() {

    }

    private static ImageCacher instance;

    public static ImageCacher getInstance() {
        if (instance == null) {
            instance = new ImageCacher();
            instance.config();
        }
        return instance;
    }

    private void config() {
        fileUris.clear();
    }

    public void loadImageForShare(final Context context, final Uri uri, final String packageName) {
        final String url = uri.toString();
        if (fileUris.containsKey(url)) {
            String newurl = fileUris.get(url);
            if (context.getPackageName().equals(packageName)) {
                ShareHelperUtils.shareContentWithChooser(context, Uri.parse(newurl));
            } else {
                if (MessengerConstant.ALL_KNOWN_MESSENGERS.get(packageName) == null) {
                    ShareHelperUtils.shareToApp(context, packageName, "NONE", newurl);
                } else {
                    ShareHelperUtils.shareToApp(context, packageName, context.getString(MessengerConstant.ALL_KNOWN_MESSENGERS.get(packageName)), newurl);
                }
            }
        } else {
            Ion.with(context).load(uri.toString()).asInputStream().setCallback(new FutureCallback<InputStream>() {
                @Override
                public void onCompleted(Exception ex, InputStream result) {
//                        if (pd != null) {
//                            pd.dismiss();
//                        }
                    File dir = new File(Environment.getExternalStorageDirectory() + "/" + Config.imgCacheDir);
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
                        fileUris.put(url, pathuri.toString());

                        if (context.getPackageName().equals(packageName)) {
                            ShareHelperUtils.shareContentWithChooser(context, pathuri);
                        } else {
                            if (MessengerConstant.ALL_KNOWN_MESSENGERS.get(packageName) == null) {
                                ShareHelperUtils.shareToApp(context, packageName, "NONE", pathuri.toString());
                            } else {
                                ShareHelperUtils.shareToApp(context, packageName, context.getString(MessengerConstant.ALL_KNOWN_MESSENGERS.get(packageName)), pathuri.toString());
                            }
                        }
                    }
                }
            });
        }
    }

}


//    public static void loadImage(final Context context, final String url) {
//        Ion.with(context).load(url).asInputStream().setCallback(new FutureCallback<InputStream>() {
//            @Override
//            public void onCompleted(Exception ex, InputStream result) {
//
//                File dir = new File(Environment.getExternalStorageDirectory() + "/" + Config.imgCacheDir);
//                if (!dir.exists()) {
//                    dir.mkdirs();
//                }
//                Uri uri = Uri.parse(url);
//                String capturePath = dir.getAbsolutePath() + "/" + uri.getLastPathSegment();/*System.currentTimeMillis()*/
//                File imageFile = new File(capturePath);
//
//                byte[] buf = new byte[1024];
//                int len = 0;
//                BufferedOutputStream bos = null;
//                try {
//                    bos = new BufferedOutputStream(new FileOutputStream(imageFile));
//                    while ((len = result.read(buf)) != -1) {
//                        bos.write(buf, 0, len);
//                    }
//                        /* 调用flush()方法，更新BufferStream */
//                    bos.flush();
//
//                        /* 结束OutputStream */
////                            bos.close();
//
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } finally {
//                    if (bos != null) {
//                        try {
//                            bos.close();
//                        } catch (IOException e1) {
//                            e1.printStackTrace();
//                        }
//                    }
//                    if (result != null) {
//                        try {
//                            result.close();
//                        } catch (IOException e1) {
//                            e1.printStackTrace();
//                        }
//                    }
//                    Uri pathuri = Uri.fromFile(imageFile);
//                    fileUris.put(url, pathuri.toString());
//
//                    String key = MessengerConstant.ALL_KNOWN_MESSENGERS.keyAt(position);
//                    if ("unknown".equals(key)) {
//                        ShareHelperUtils.shareContentWithChooser(context, pathuri);
//                    } else {
//                        ShareHelperUtils.shareToApp(context, key, context.getString(MessengerConstant.ALL_KNOWN_MESSENGERS.get(key)), url);
//                    }
//                }
//            }
//        });
//    }

//}
