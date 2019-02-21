package com.rd.qnz.view;

/**
 * Created by Evonne on 2016/9/22.
 */
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.toolbox.ImageLoader.ImageCache;

/**
 * 单例模式 图片缓存 在CommunityAct界面有使用到
 */
public class LruImageCache implements ImageCache{

    private static LruCache<String, Bitmap> mMemoryCache;

    private static LruImageCache lruImageCache;

    //缓存大小为手机可用内存的八分之一
    private LruImageCache(){
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 8;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize){
            @Override
            protected int sizeOf(String key, Bitmap bitmap){
                return bitmap.getRowBytes() * bitmap.getHeight();
            }
        };
    }

    public static LruImageCache instance(){
        if(lruImageCache == null){
            lruImageCache = new LruImageCache();
        }
        return lruImageCache;
    }

    @Override
    public Bitmap getBitmap(String arg0) {
        return mMemoryCache.get(arg0);
    }

    @Override
    public void putBitmap(String arg0, Bitmap arg1) {
        if(getBitmap(arg0) == null){
            mMemoryCache.put(arg0, arg1);
        }
    }

}