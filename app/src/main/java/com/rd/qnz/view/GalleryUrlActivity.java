package com.rd.qnz.view;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import com.rd.qnz.BaseActivity;
import com.rd.qnz.R;

import ru.truba.touchgallery.GalleryWidget.BasePagerAdapter.OnItemChangeListener;
import ru.truba.touchgallery.GalleryWidget.GalleryViewPager;
import ru.truba.touchgallery.GalleryWidget.UrlPagerAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 照片长廊
 *
 * @author Evonne
 */
public class GalleryUrlActivity extends BaseActivity {

    private GalleryViewPager mViewPager;
    private ProgressDialog mDialog;
    ImageMemoryCache memoryCache = new ImageMemoryCache(this);
    ImageFileCache fileCache = new ImageFileCache();
    private TextView current_Item;
    private String imagePath = null;
    private ArrayList<String> list = new ArrayList<String>();
    private String[] photo_list;
    private int photo_text;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_image);

        mDialog = new ProgressDialog(this);
        mDialog.setTitle("请稍等");
        mDialog.setMessage("正在加载...");

        Bundle bundle = this.getIntent().getExtras();
        imagePath = bundle.getString("image");
        list = bundle.getStringArrayList("list");
        photo_list = list.toString().substring(1, list.toString().length() - 1).split(",");

        new ImageAsynTask().execute();

        for (int i = 0; i < list.size(); i++) {
            photo_text = 0;
            if (list.get(i).equals(imagePath)) {
                photo_text = i;
                return;
            }
        }
    }

    private class ImageAsynTask extends AsyncTask<Void, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(Void... params) {
            String url = imagePath;
            return getBitmap(url);
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            mDialog.dismiss();

            List<String> items = new ArrayList<String>();
            Collections.addAll(items, photo_list);
            current_Item = (TextView) findViewById(R.id.current_Item);
            UrlPagerAdapter pagerAdapter = new UrlPagerAdapter(GalleryUrlActivity.this, items);
            pagerAdapter.setOnItemChangeListener(new OnItemChangeListener() {
                @Override
                public void onItemChange(int currentPosition) {
                    current_Item.setText(currentPosition + 1 + "/" + photo_list.length);
                }
            });
            mViewPager = (GalleryViewPager) findViewById(R.id.viewer);
            mViewPager.setOffscreenPageLimit(3);
            mViewPager.setAdapter(pagerAdapter);
            mViewPager.setCurrentItem(photo_text);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog.show();
        }
    }

    public Bitmap getBitmap(String url) {
        // 从内存缓存中获取图片
        Bitmap result = memoryCache.getBitmapFromCache(url);
        if (result == null) {
            // 文件缓存中获取
            result = fileCache.getImage(url);
            if (result == null) {
                // 从网络获取
                result = ImageGetFromHttp.downloadBitmap(url);
                if (result != null) {
                    fileCache.saveBitmap(result, url);
                    memoryCache.addBitmapToCache(url, result);
                }
            } else {
                // 添加到内存缓存
                memoryCache.addBitmapToCache(url, result);
            }
        }
        return result;
    }

}