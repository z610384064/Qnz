package com.rd.qnz.tools;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.text.TextUtils;

public class Utils {
    public static String getVolumePath(Context context) {
        String path = null;
        String[] paths = null;
        StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        try {
            Method mMethodGetPaths = storageManager.getClass().getMethod("getVolumePaths");

            paths = (String[]) mMethodGetPaths.invoke(storageManager);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            ArrayList<String> pathList = new ArrayList<String>();
            if (paths != null) {
                for (int i = 0; i < paths.length; i++) {
                    StatFs stat = new StatFs(paths[i]);
                    long blockSize = stat.getBlockSize();
                    long blockCount = stat.getBlockCount();
                    if (blockCount != 0 && blockSize != 0) {
                        pathList.add(paths[i]);
                    }
                }
            }
            if (pathList.size() == 0) {// 没有外部存储的时候放在内部存储
                pathList.add(Environment.getExternalStorageDirectory().toString()
                        // .getAbsolutePath()
                );
                path = pathList.get(0);
            }
            // File sd = new
            // File(Environment.getExternalStorageDirectory().toString());
            if (pathList.size() > 1) {// 有外部存储的时候就放在外部存储中
                path = pathList.get(pathList.size() - 1);
            }
            if (path == null || TextUtils.isEmpty(path)) {
                pathList.add(Environment.getExternalStorageDirectory().toString()
                        // .getAbsolutePath()
                );
                path = pathList.get(0);
            }
            boolean sdCardExit = Environment.getExternalStorageState().equals(
                    android.os.Environment.MEDIA_MOUNTED);
            if (sdCardExit) {
                path = Environment.getExternalStorageDirectory().getAbsolutePath();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }

    /**
     * 根据原图和变长绘制圆形图片
     *
     * @param source
     * @param min
     * @return
     */
    private Bitmap createCircleImage(Bitmap source, int min) {
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        Bitmap target = Bitmap.createBitmap(min, min, Config.ARGB_8888);
        /**
         * 产生一个同样大小的画布
         */
        Canvas canvas = new Canvas(target);
        /**
         * 首先绘制圆形
         */
        canvas.drawCircle(min / 2, min / 2, min / 2, paint);
        /**
         * 使用SRC_IN
         */
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        /**
         * 绘制图片
         */
        canvas.drawBitmap(source, 0, 0, paint);
        return target;
    }


    public static String getAbsoluteUrlPath(String url) {
        return BaseParam.URL_QIAN + url;
    }

    /**
     * encodeURIComponent 解码
     *
     * @param url http%3A%2F%2Fwww.w3school.com.cn%2FMy%20first%3Faaa%3D123%26bbb%3D456
     * @return http://www.w3school.com.cn/My first?aaa=123&bbb=456
     */
    public static String getURLDecoder(String url) {
        String searchtext = "";
        try {
            searchtext = java.net.URLDecoder.decode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return searchtext;
    }
}
