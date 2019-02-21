package com.rd.qnz;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.rd.qnz.util.MyVolley;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

/**
 * 基类
 *
 * @author Evonne
 */
public class BaseActivity extends SwipeBackActivity {
    public RequestQueue requestQueue;
    public ImageLoader imageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestQueue = MyVolley.getRequestQueue();
        imageLoader = MyVolley.getImageLoader();
    }

    Toast mToast;
    TextView toast_tv;

    public void showToast(final String text) {
        if (!TextUtils.isEmpty(text)) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (mToast == null) {
//						mToast = Toast.makeText(BaseActivity.this, text, Toast.LENGTH_SHORT);
                        mToast = new Toast(BaseActivity.this);
                        View tview = LayoutInflater.from(BaseActivity.this).inflate(R.layout.layout_toast, null);
                        mToast.setView(tview);
                        toast_tv = (TextView) tview.findViewById(R.id.toast_text);
                    } else {
//						mToast.setText(text);
                    }
                    toast_tv.setText(text);
                    mToast.show();
                }
            });
        }
    }

    public void showToast(final int resId) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (mToast == null) {
//					mToast = Toast.makeText(BaseActivity.this, resId, Toast.LENGTH_SHORT);
                    mToast = new Toast(BaseActivity.this);
                    View tview = LayoutInflater.from(BaseActivity.this).inflate(R.layout.layout_toast, null);
                    mToast.setView(tview);
                    toast_tv = (TextView) tview.findViewById(R.id.toast_text);
                } else {
//					mToast.setText(resId);
                }
                toast_tv.setText(resId);
                mToast.show();
            }
        });
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {    //是否获取焦点
            if (getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null) {
                InputMethodManager immanager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                immanager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);  //表示软键盘总是隐藏着
            }
        }
   //     mGestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    public void dialogShow() {

        LayoutInflater inflaterDl = LayoutInflater.from(BaseActivity.this);
        RelativeLayout layout = (RelativeLayout) inflaterDl.inflate(R.layout.toast_reg, null);
        final Dialog dialog = new AlertDialog.Builder(BaseActivity.this).create();
        dialog.show();
        dialog.getWindow().setContentView(layout);

        ImageView close = (ImageView) dialog.findViewById(R.id.close);

        //关闭
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    public  boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = { 0, 0 };
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }


}
