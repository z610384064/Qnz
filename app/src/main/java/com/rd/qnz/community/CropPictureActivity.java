package com.rd.qnz.community;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.rd.qnz.BaseActivity;
import com.rd.qnz.R;
import com.rd.qnz.util.BitmapUtil;
import com.rd.qnz.view.CropView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * 头像选取界面
 *
 * @author Evonne
 */
public class CropPictureActivity extends BaseActivity implements OnTouchListener, OnClickListener {
    private String path;

    private ImageView srcPic;
    private View sure, cancel;
    private CropView clipview;

    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();

    /**
     * 动作标志：无
     */
    private static final int NONE = 0;
    /**
     * 动作标志：拖动
     */
    private static final int DRAG = 1;
    /**
     * 动作标志：缩放
     */
    private static final int ZOOM = 2;
    /**
     * 初始化动作标志
     */
    private int mode = NONE;

    /**
     * 记录起始坐标
     */
    private PointF start = new PointF();
    /**
     * 记录缩放时两指中间点坐标
     */
    private PointF mid = new PointF();
    private float oldDist = 1f;

    private Bitmap bitmap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_crop);
        final DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        path = getIntent().getStringExtra("path");

        srcPic = (ImageView) this.findViewById(R.id.crop_src_pic);
        srcPic.setOnTouchListener(this);

        ViewTreeObserver observer = srcPic.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

            @SuppressWarnings("deprecation")
            public void onGlobalLayout() {
                initClipView(srcPic.getTop(), srcPic.getBottom(), dm.widthPixels);
                srcPic.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        sure = this.findViewById(R.id.crop_ok);
        cancel = this.findViewById(R.id.crop_cancel);
        sure.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    /**
     * 初始化截图区域，并将源图按裁剪框比例缩放
     *
     * @param top, bottom
     */
    private void initClipView(int top, final int bottom, final int swidth) {
//		LinearLayout bottom_layout = (LinearLayout) findViewById(R.id.crop_bottom);
        try {
            int degree = BitmapUtil.readPictureDegree(path);
            bitmap = BitmapUtil.readBitmap(path);
            bitmap = BitmapUtil.rotateImageView(degree, bitmap);

//			ImageView view = new ImageView(this);
//			view.setImageBitmap(bitmap);
//			new AlertDialog.Builder(this).setView(view).show();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        clipview = new CropView(CropPictureActivity.this);
        clipview.setCustomTopBarHeight(top);
        clipview.setCustomBottomBarHeight(bottom);
//		clipview.setCustomBottomBarHeight(blTop);
        clipview.addOnDrawCompleteListener(new CropView.OnDrawListenerComplete() {

            public void onDrawCompelete() {
                clipview.removeOnDrawCompleteListener();
                int clipHeight = clipview.getClipHeight();
                int clipWidth = clipview.getClipWidth();
                int midX = clipview.getClipLeftMargin() + (clipWidth / 2);
                int midY = clipview.getClipTopMargin() + (clipHeight / 2);

                int imageWidth = bitmap.getWidth();
                int imageHeight = bitmap.getHeight();

                // 按裁剪框求缩放比例
                float scale = (swidth * 1.0f) / imageWidth;
                if (imageWidth > imageHeight) {
                    scale = (clipHeight * 1.0f) / imageHeight;
                }

                // 起始中心点
                float imageMidX = imageWidth * scale / 2;
                float imageMidY = clipview.getCustomTopBarHeight()
                        + imageHeight * scale / 2;
                srcPic.setScaleType(ScaleType.MATRIX);

                // 缩放
                matrix.postScale(scale, scale);
                // 平移
                matrix.postTranslate(midX - imageMidX, midY - imageMidY);

                srcPic.setImageMatrix(matrix);
                srcPic.setImageBitmap(bitmap);

            }
        });

        this.addContentView(clipview, new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }

    public boolean onTouch(View v, MotionEvent event) {
        ImageView view = (ImageView) v;
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                savedMatrix.set(matrix);
                // 设置开始点位置
                start.set(event.getX(), event.getY());
                mode = DRAG;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                oldDist = spacing(event);
                if (oldDist > 10f) {
                    savedMatrix.set(matrix);
                    midPoint(mid, event);
                    mode = ZOOM;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG) {
                    matrix.set(savedMatrix);
                    matrix.postTranslate(event.getX() - start.x, event.getY()
                            - start.y);
                } else if (mode == ZOOM) {
                    float newDist = spacing(event);
                    if (newDist > 10f) {
                        matrix.set(savedMatrix);
                        float scale = newDist / oldDist;
                        matrix.postScale(scale, scale, mid.x, mid.y);
                    }
                }
                break;
        }
        view.setImageMatrix(matrix);
        return true;
    }

    /**
     * 多点触控时，计算最先放下的两指距离
     *
     * @param event
     * @return
     */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * 多点触控时，计算最先放下的两指中心坐标
     *
     * @param point
     * @param event
     */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.crop_ok:
                Bitmap clipBitmap = getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                clipBitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                byte[] bitmapByte = baos.toByteArray();

                Intent intent = new Intent();
                intent.putExtra("bitmap", bitmapByte);
                intent.putExtra("suffix", path.substring(path.lastIndexOf(".") + 1));
                setResult(RESULT_OK, intent);

                finish();
                break;
            case R.id.crop_cancel:
                finish();
                break;
        }
    }

    /**
     * 获取裁剪框内截图
     *
     * @return
     */
    private Bitmap getBitmap() {
        // 获取截屏
        View view = this.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();

		/*
        Bitmap finalBitmap = Bitmap.createBitmap(view.getDrawingCache(),
				clipview.getClipLeftMargin(), 
				clipview.getClipTopMargin(),
				clipview.getClipWidth(),
				clipview.getClipHeight());
				*/

        // 获取状态栏高度
        Rect frame = new Rect();
        this.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        Bitmap finalBitmap = Bitmap.createBitmap(view.getDrawingCache(),
                clipview.getClipLeftMargin(), clipview.getClipTopMargin()
                        + statusBarHeight, clipview.getClipWidth(),
                clipview.getClipHeight());

        // 释放资源
        view.destroyDrawingCache();
        return finalBitmap;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }
    }

}