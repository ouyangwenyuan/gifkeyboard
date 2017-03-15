package com.fotoable.inputkeyboard.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

public class ImageManagerutils {

    private Map<String, SoftReference<Bitmap>> imgCache = new LinkedHashMap<String, SoftReference<Bitmap>>();
    private final static String PATH = "mnt/sdcard/gitkeyboard_imageCache/";

    /**
     * 从网络上下载
     *
     * @param url
     * @return
     */
    public Bitmap getBitMapFromUrl(String url) {
        Bitmap bitmap = null;
        URL u = null;
        HttpURLConnection conn = null;
        InputStream is = null;
        try {
            u = new URL(url);
            conn = (HttpURLConnection) u.openConnection();
            is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /*
   * 获得设置信息
   */
    public BitmapFactory.Options getOptions(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        return options;
    }

    /**
     * 从文件中读取
     *
     * @return
     * @throws Exception
     */
    private Bitmap getBitMapFromSDCard(String url) throws Exception {
        Bitmap bitmap = null;
        File file = new File(PATH + base64String(url));
        if (!file.exists()) {
            return null;
        }
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        opts.inPreferredConfig = Bitmap.Config.RGB_565;
        InputStream fis = new FileInputStream(file);
        try {
            bitmap = BitmapFactory.decodeStream(fis);
        } catch (Exception e) {
            return null;
        }
        return bitmap;
    }

    public byte[] readStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        outStream.close();
        inStream.close();
        return outStream.toByteArray();
    }

    /**
     * 从缓存中读取
     *
     * @param url
     * @return
     * @throws Exception
     */
    public Bitmap getImgFromCache(String url) throws Exception {
        Bitmap bitmap = null;
        // 从内存中读取
        if (imgCache.containsKey(base64String(url))) {
            synchronized (imgCache) {
                SoftReference<Bitmap> bitmapReference = imgCache.get(base64String(url));
                if (null != bitmapReference) {
                    bitmap = bitmapReference.get();
                }
            }
            // 否则从文件中读取
        } else if (null != (bitmap = getBitMapFromSDCard(url))) {
            // 将图片保存进内存中
            imgCache.put(base64String(url),
                    new SoftReference<Bitmap>(bitmap));
        }
        return bitmap;
    }


// public void removeImgFromCatche(String url) {
//  if (imgCache.containsKey(base64String(url))) {
//   Bitmap imageBit = imgCache.get(
//     Base64.encodeToString(url.getBytes(), Base64.DEFAULT))
//     .get();
//   if (imageBit != null && !imageBit.isRecycled()) {
//    imageBit.recycle();
//    imgCache.remove(base64String(url));
//    imageBit = null;
//   }
//  }
// }

    /**
     * 将图片写入sdcard中
     *
     * @param bitmap
     * @param url
     * @throws Exception
     */
    public static final File writePic2SDCard(Bitmap bitmap, String url)
            throws Exception {
        File folder = new File(PATH);
        if (!folder.exists()) {
            folder.mkdir();
        }
        String filename = PATH + base64String(url);
        File file = new File(filename);
        if (!file.exists()) {
            file.createNewFile();
        }
        FileOutputStream fos = null;
        ByteArrayInputStream bis = null;
        try {
            fos = new FileOutputStream(file);
            byte[] bitmapByte = bitmap2Byte(bitmap);
            bis = new ByteArrayInputStream(bitmapByte);
            int len = 0;
            byte[] b = new byte[bis.available()];
            while ((len = bis.read(b)) != -1) {
                fos.write(b, 0, len);
            }
        } catch (Exception e) {
            return null;
        } finally {
            if (null != bis) {
                bis.close();
            }
            if (null != fos) {
                fos.close();
            }
        }
        return file;
    }

    /**
     * bitMap 转化为数组
     *
     * @param bitmap
     * @return
     */
    public static final byte[] bitmap2Byte(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    private static String base64String(String url) {
        return Base64.encodeToString(url.getBytes(), Base64.DEFAULT);
    }

    /**
     * 自适应切割缩放工具<br/>
     * <p>
     * 无论你提供的是大图小图，是高的还是宽的，都能根据提供的目标尺寸进行“最合适的”切割缩放
     *
     * @param srcBMP    - 原图
     * @param dstWidth  - 目标宽度
     * @param dstHeight - 目标高度
     * @return - 切割缩放完后的Bitmap对象
     */

    public static final Bitmap adaptive(Bitmap srcBMP, int dstWidth, int dstHeight) {

        Matrix matrix = new Matrix();

        final int srcWidth = srcBMP.getWidth();

        final int srcHeight = srcBMP.getHeight();

        final float srcRatio = ((float) srcHeight) / srcWidth;

        final float dstRatio = ((float) dstHeight) / dstWidth;


        if (srcRatio < dstRatio) {

            final float scale = ((float) dstHeight) / srcHeight;

            matrix.postScale(scale, scale);

            final float width = dstWidth / scale;

            return Bitmap.createBitmap(srcBMP, (int) ((srcWidth - width) / 2), 0, (int) width, srcHeight, matrix, true);

        } else if (srcRatio > dstRatio) {

            final float scale = ((float) dstWidth) / srcWidth;

            matrix.postScale(scale, scale);

            final float height = dstHeight / scale;

            return Bitmap.createBitmap(srcBMP, 0, (int) ((srcHeight - height) / 2), srcWidth, (int) height, matrix, true);

        } else {

            final float scale = ((float) dstWidth) / srcWidth;

            matrix.postScale(scale, scale);

            return Bitmap.createBitmap(srcBMP, 0, 0, srcWidth, srcHeight, matrix, true);

        }

    }
}