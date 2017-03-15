/*
 * Copyright (c) 2017. @author ouyang copyright@fotoable Inc.  Anyone can copy this
 */

package com.fotoable.gifkeyboard.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.fotoable.gifkeyboard.R;
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


/**
 * Created by ouyangwenyuan on 2017/3/10.
 */

public class ShareListActivity extends BaseActivity {

    private static final int PHOTO_REQUEST_GALLERY = 1;
    private static final int PHOTO_REQUEST_CAREMA = 2;
    private static final int PHOTO_REQUEST_CUT = 3;
    String out_file_path = "gitkeyboard";
    Uri capturePathUri = null;
    //    private static final String PHOTO_FILE_NAME = "gitkeyboard_test.jpg";
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE2 = 4;
    private TextView pathView;
    private ProgressDialog pd;
    private Map<String, String> cacheImages = new HashMap<>();

    private static class ShareHolder extends RecyclerView.ViewHolder {

        private TextView nameView;
        private TextView packageNameView;

        public ShareHolder(View itemView) {
            super(itemView);
            this.nameView = (TextView) itemView.findViewById(R.id.tv_name);
            this.packageNameView = (TextView) itemView.findViewById(R.id.tv_package_name);
        }
    }

    private class ShareAdapter extends RecyclerView.Adapter<ShareHolder> {


        @Override
        public ShareHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(ShareListActivity.this).inflate(R.layout.share_item, null);
            ShareHolder shareHolder = new ShareHolder(itemView);
            return shareHolder;
        }

        @Override
        public void onBindViewHolder(ShareHolder holder, int position) {
            String name = null;
            try {
                Integer integer = MessengerConstant.ALL_KNOWN_MESSENGERS.get(MessengerConstant.ALL_KNOWN_MESSENGERS.keyAt(position));
                if (integer == null) {
                    name = "app";
                } else {
                    name = getString(integer.intValue());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            holder.nameView.setText(name);
            holder.packageNameView.setText(MessengerConstant.ALL_KNOWN_MESSENGERS.keyAt(position));
            holder.itemView.setOnClickListener(new ShareListener(position));
        }


        @Override
        public int getItemCount() {
            return MessengerConstant.ALL_KNOWN_MESSENGERS.size();
        }
    }

    private class ShareListener implements View.OnClickListener {
        private int position;

        public ShareListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            final String url = pathView.getText().toString(); //"https://tenor.co/uy5e.gif";
            final Uri uri = Uri.parse(url);
            if (url.startsWith("http")) {
//                load by Ion
                if (cacheImages.containsKey(url)) {
                    String newurl = cacheImages.get(url);
                    String key = MessengerConstant.ALL_KNOWN_MESSENGERS.keyAt(position);
                    if ("unknown".equals(key)) {
                        ShareHelperUtils.shareContentWithChooser(ShareListActivity.this, Uri.parse(newurl));
                    } else {
                        ShareHelperUtils.shareToApp(ShareListActivity.this, key, getString(MessengerConstant.ALL_KNOWN_MESSENGERS.get(key)), newurl);
                    }
                } else {
                    pd.show();
                    Ion.with(ShareListActivity.this).load(url).asInputStream().setCallback(new FutureCallback<InputStream>() {
                        @Override
                        public void onCompleted(Exception ex, InputStream result) {
                            if (pd != null) {
                                pd.dismiss();
                            }
                            File dir = new File(Environment.getExternalStorageDirectory() + "/" + out_file_path);
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

                                String key = MessengerConstant.ALL_KNOWN_MESSENGERS.keyAt(position);
                                if ("unknown".equals(key)) {
                                    ShareHelperUtils.shareContentWithChooser(ShareListActivity.this, pathuri);
                                } else {
                                    ShareHelperUtils.shareToApp(ShareListActivity.this, key, getString(MessengerConstant.ALL_KNOWN_MESSENGERS.get(key)), url);
                                }
                            }
                        }
                    });
                }
            } else {
                String key = MessengerConstant.ALL_KNOWN_MESSENGERS.keyAt(position);
                if ("unknown".equals(key)) {
                    ShareHelperUtils.shareContentWithChooser(ShareListActivity.this, uri);
                } else {
                    ShareHelperUtils.shareToApp(ShareListActivity.this, key, getString(MessengerConstant.ALL_KNOWN_MESSENGERS.get(key)), url);
                }
            }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sharelist);

        pd = new ProgressDialog(this);
        pd.setMessage("loading images...");

        Button chooseBtn = (Button) findViewById(R.id.bt_select);
        pathView = (TextView) findViewById(R.id.tv_path);
        pathView.setText("https://media.tenor.co/images/249758bbb421e1fa7a2ac26f99b81e1d/tenor.gif");
        chooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (ContextCompat.checkSelfPermission(ShareListActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ShareListActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_CALL_PHONE2);

                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ShareListActivity.this);
                    builder.setItems(new CharSequence[]{"拍照", "照片"}, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) {
                                camera(v);
                            } else {
                                gallery(v);
                            }
                        }
                    });
                    builder.create();
                    builder.show();
                }
            }
        });
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().hide();

        RecyclerView sharelistView = (RecyclerView) findViewById(R.id.share_list);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        sharelistView.setLayoutManager(gridLayoutManager);
        sharelistView.setAdapter(new ShareAdapter());
        sharelistView.setRecyclerListener(new RecyclerView.RecyclerListener() {
            @Override
            public void onViewRecycled(RecyclerView.ViewHolder holder) {

            }
        });
    }

    /*
      * 从相册获取
      */
    public void gallery(View view) {
        // 激活系统图库，选择一张图片
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_GALLERY
        startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
    }

    /*
      * 从相机获取
      */
    public void camera(View view) {
        // 激活相机
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");

        File dir = new File(Environment.getExternalStorageDirectory() + "/" + out_file_path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String capturePath = dir.getAbsolutePath() + "/test.jpg";/*System.currentTimeMillis()*/
        File imageFile = new File(capturePath);

          /*获取当前系统的android版本号*/
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        Log.e("currentapiVersion", "currentapiVersion====>" + currentapiVersion);
        if (currentapiVersion < 24) {
            capturePathUri = Uri.fromFile(imageFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, capturePathUri);
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        } else {
            ContentValues contentValues = new ContentValues(1);
            contentValues.put(MediaStore.Images.Media.DATA, imageFile.getAbsolutePath());
            capturePathUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            if (capturePathUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, capturePathUri);
            } else {
                capturePathUri = Uri.fromFile(imageFile);
            }
        }

        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CAREMA
        startActivityForResult(intent, PHOTO_REQUEST_CAREMA);
    }

    /*
* 剪切图片
*/
    private void crop(Uri uri) {
        // 裁剪图片意图
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // 裁剪框的比例，1：1
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // 裁剪后输出图片的尺寸大小
        intent.putExtra("outputX", 250);
        intent.putExtra("outputY", 250);

        intent.putExtra("outputFormat", "JPEG");// 图片格式
        intent.putExtra("noFaceDetection", true);// 取消人脸识别
        intent.putExtra("return-data", true);
        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CUT
        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }

    /*
       * 判断sdcard是否被挂载
       */
    private boolean hasSdcard() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {


        if (requestCode == MY_PERMISSIONS_REQUEST_CALL_PHONE2) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ShareListActivity.this);
                builder.setItems(new CharSequence[]{"拍照", "照片"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            camera(null);
                        } else {
                            gallery(null);
                        }
                    }
                });
                builder.create();
                builder.show();
            } else {
                // Permission Denied
                Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PHOTO_REQUEST_GALLERY) {
            // 从相册返回的数据
            if (data != null) {
                // 得到图片的全路径
                Uri uri = data.getData();
//                crop(uri);
                pathView.setText(uri.toString());
            }

        } else if (requestCode == PHOTO_REQUEST_CAREMA) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    Uri uri = data.getData();
                    if (uri != null) {
                        pathView.setText(uri.toString());
                    } else {
                        pathView.setText(capturePathUri.toString());
                    }
                } else {
                    pathView.setText(capturePathUri.toString());
                }
            }

            // 从相机返回的数据
//                if (hasSdcard()) {
//                    crop(uri);
//                } else {
//                    Toast.makeText(ShareListActivity.this, "未找到存储卡，无法存储照片！", Toast.LENGTH_LONG).show();
//                }

        } else if (requestCode == PHOTO_REQUEST_CUT) {
            // 从剪切图片返回的数据
            if (data != null) {
//                Bitmap bitmap = data.getParcelableExtra("data");
//                this.iv_image.setImageBitmap(bitmap);
//                pathView.setText(uri.toString());
            }
            try {
                // 将临时文件删除
//                tempFile.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

}
