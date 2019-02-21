package com.rd.qnz.custom;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rd.qnz.R;
import com.rd.qnz.gustruelock.LockActivity;
import com.rd.qnz.tools.BaseParam;
import com.zhy.autolayout.AutoLayoutActivity;

/**
 *  主界面四个底部导航栏对应activity 继承自autoLayout适配
 */
public class KeyPatternActivity2 extends AutoLayoutActivity {
    MyApplication myApp = null;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        myApp = MyApplication.getInstance();
    }

    @Override
    protected void onStart() {  //当界面从不可见变为可见的时候会调用,现在的话改了,myApp.redPacketOpen的值不会等于1了
        // TODO Auto-generated method stub
        super.onStart();
        Log.i("myApp.redPacketOpen = ", myApp.redPacketOpen);

        if (("1").equals(myApp.redPacketOpen)) {
            dialogShow(); //弹出一个红包弹窗
            myApp.redPacketOpen = "";
        } else {
            myApp.redPacketOpen = "";
        }
    }

    @Override
    protected void onRestart() {
        // TODO Auto-generated method stub
        super.onRestart();
        if (myApp.isApplication == 1) {
            // MyApplication的时候改为1
            myApp.isApplication = 2;
            myApp.time = (int) (System.currentTimeMillis() / 1000); //得到当前的时间
        }
        if (myApp.isApplication == 2) {
            //正在执行
            int now_time = (int) (System.currentTimeMillis() / 1000);
            SharedPreferences preferences = getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_USER, Context.MODE_PRIVATE);
            String oauthToken = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN, "");
            if (!oauthToken.equals("")) {
                if (now_time - myApp.time > 300) {
                    if (myApp.isSeccess == 0) {
                        Intent intent = new Intent(KeyPatternActivity2.this, LockActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        myApp.homeCount = 1;
                        myApp.isSeccess = 1;
                        myApp.time = now_time;
                    }
                } else {
                    myApp.time = now_time;
                }
            } else {
                myApp.time = (int) (System.currentTimeMillis() / 1000);
            }
        }
    }

    public void dialogShow() {

        LayoutInflater inflaterDl = LayoutInflater.from(KeyPatternActivity2.this);
        RelativeLayout layout = (RelativeLayout) inflaterDl.inflate(R.layout.toast_reg, null);
        final Dialog dialog = new AlertDialog.Builder(KeyPatternActivity2.this).create();
        dialog.show();
        dialog.getWindow().setContentView(layout);

        ImageView close = (ImageView) dialog.findViewById(R.id.close);

        //关闭
        close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                dialog.dismiss();
            }
        });
        dialog.show();
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
                        mToast = new Toast(KeyPatternActivity2.this);
                        View tview = LayoutInflater.from(KeyPatternActivity2.this).inflate(R.layout.layout_toast, null);
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
                    mToast = new Toast(KeyPatternActivity2.this);
                    View tview = LayoutInflater.from(KeyPatternActivity2.this).inflate(R.layout.layout_toast, null);
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



}
