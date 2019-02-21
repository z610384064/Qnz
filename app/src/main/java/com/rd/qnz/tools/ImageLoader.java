package com.rd.qnz.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.rd.qnz.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;


public class ImageLoader {
    private static ImageLoader imageLoader = new ImageLoader();

    private int defRequiredSize = 130;
    MemoryCache memoryCache = new MemoryCache();
    private FileCache fileCache;
    private Map<ImageView, String> imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
    // 线程池
    ExecutorService executorService;
    private Context context;

    private ImageLoader() {
        executorService = Executors.newFixedThreadPool(5);
    }

    // 单例模式
    public static ImageLoader getInstances(Context context) {
        imageLoader.setContext(context);

        return imageLoader;
    }

    private void setContext(Context context) {
        this.context = context;
        this.fileCache = new FileCache(context);
    }

    public void setDefRequiredSize(int defRequiredSize) {
        this.defRequiredSize = defRequiredSize;
    }

    // 当进入listview时默认的图片，可换成你自己的默认图片
    final int stub_id = R.drawable.jiazai;

    /**
     * 最主要的方法
     *
     * @param url
     * @param imageView
     * @param requiredSize 裁剪图片大小尺寸（一直裁剪到图片宽或高 至少有一个小与requiredSize的时候）
     */
    public void DisplayImage(String url, ImageView imageView, int requiredSize, OnImageLoaderListener listener) {
        imageViews.put(imageView, url);
        // 先从内存缓存中查找
        Bitmap bitmap = memoryCache.get(url);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
            if (null != listener) {
                listener.onFinishedImageLoader(imageView, bitmap); // 通知完成加载
            }
        } else {
            // 若没有的话则开启新线程加载图片
            queuePhoto(url, imageView, requiredSize, listener);
            imageView.setImageResource(stub_id);
        }
    }

    public void DisplayImage(String url, ImageView imageView) {
        DisplayImage(url, imageView, defRequiredSize, null);
    }

    public void DisplayImage(String url, ImageView imageView, int requiredSize) {
        DisplayImage(url, imageView, requiredSize, null);
    }

    public void DisplayImage(String url, ImageView imageView, OnImageLoaderListener listener) {
        DisplayImage(url, imageView, defRequiredSize, listener);
    }

    private void queuePhoto(String url, ImageView imageView, int requiredSize, OnImageLoaderListener listener) {
        PhotoToLoad p = new PhotoToLoad(url, imageView, listener);
        executorService.submit(new PhotosLoader(p, requiredSize));
    }

    private Bitmap getBitmap(String url, int requiredSize) {
        File f = fileCache.getFile(url);

        // 先从文件缓存中查找是否有
        Bitmap b = decodeFile(f, requiredSize);
        if (b != null)
            return b;

        // 最后从指定的url中下载图片
        try {
            Bitmap bitmap = null;
            URL imageUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setInstanceFollowRedirects(true);
            InputStream is = conn.getInputStream();
            OutputStream os = new FileOutputStream(f);
            CopyStream(is, os);
            os.close();
            bitmap = decodeFile(f, requiredSize);
            return bitmap;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    // decode这个图片并且按比例缩放以减少内存消耗，虚拟机对每张图片的缓存大小也是有限制的
    private static Bitmap decodeFile(File f, int requiredSize) {
        try {
            // decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            // Find the correct scale value. It should be the power of 2.

//                    int width_tmp = o.outWidth, height_tmp = o.outHeight;
//                    int scale = 1;

            float width_tmp = o.outWidth, height_tmp = o.outHeight;
            float scale = 1;
            while (true) {
//                            if (width_tmp / 2 < requiredSize
//                                            || height_tmp / 2 < requiredSize)
//                                    break;
//                            width_tmp /= 2;
//                            height_tmp /= 2;
//                            scale *= 2;
                if (width_tmp < requiredSize || height_tmp < requiredSize)
                    break;
                width_tmp /= 1.2;
                height_tmp /= 1.2;
                scale *= 1.2;
            }

            // decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = (int) scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
        }
        return null;
    }

    // Task for the queue
    private class PhotoToLoad {
        public String url;
        public ImageView imageView;
        public OnImageLoaderListener onImageLoaderListener;

        public PhotoToLoad(String u, ImageView i) {
            url = u;
            imageView = i;
        }

        public PhotoToLoad(String u, ImageView i, OnImageLoaderListener listener) {
            url = u;
            imageView = i;
            this.onImageLoaderListener = listener;
        }

    }

    class PhotosLoader implements Runnable {
        PhotoToLoad photoToLoad;
        int requiredSize;

        PhotosLoader(PhotoToLoad photoToLoad, int requiredSize) {
            this.photoToLoad = photoToLoad;
            this.requiredSize = requiredSize;
        }

        @Override
        public void run() {
            if (imageViewReused(photoToLoad))
                return;
            Bitmap bmp = getBitmap(photoToLoad.url, requiredSize);
            memoryCache.put(photoToLoad.url, bmp);
            if (imageViewReused(photoToLoad))
                return;
            BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
            // 更新的操作放在UI线程中
            Activity a = (Activity) photoToLoad.imageView.getContext();
            a.runOnUiThread(bd);
        }
    }

    /**
     * 防止图片错位
     *
     * @param photoToLoad
     * @return
     */
    boolean imageViewReused(PhotoToLoad photoToLoad) {
        String tag = imageViews.get(photoToLoad.imageView);
        if (tag == null || !tag.equals(photoToLoad.url))
            return true;
        return false;
    }

    // 用于在UI线程中更新界面
    class BitmapDisplayer implements Runnable {
        Bitmap bitmap;
        PhotoToLoad photoToLoad;

        public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
            bitmap = b;
            photoToLoad = p;
        }

        public void run() {
            if (imageViewReused(photoToLoad))
                return;
            if (bitmap != null) {
                photoToLoad.imageView.setImageBitmap(bitmap);
            } else {
                photoToLoad.imageView.setImageResource(stub_id);
            }
            // 如果设置了监听器
            if (null != photoToLoad.onImageLoaderListener) {
                // 通知观察者完成
                photoToLoad.onImageLoaderListener.onFinishedImageLoader(photoToLoad.imageView, bitmap);

            }

        }
    }

    public void clearCache() {
        memoryCache.clear();
        fileCache.clear();
    }

    public static void CopyStream(InputStream is, OutputStream os) {
        final int buffer_size = 1024;
        try {
            byte[] bytes = new byte[buffer_size];
            for (; ; ) {
                int count = is.read(bytes, 0, buffer_size);
                if (count == -1)
                    break;
                os.write(bytes, 0, count);
            }
        } catch (Exception ex) {
        }
    }

    /**
     * 加载完毕监听器
     */
    public interface OnImageLoaderListener {
        /**
         * 完成加载后回调
         *
         * @param imageView 要加载ImageView
         * @param bitmap    加载的图片
         */
        public void onFinishedImageLoader(ImageView imageView, Bitmap bitmap);
    }

}
