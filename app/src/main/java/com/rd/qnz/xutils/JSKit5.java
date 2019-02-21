package com.rd.qnz.xutils;

import android.content.Intent;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.rd.qnz.BannerWebViewAct;

public class JSKit5 {
    private BannerWebViewAct ma;

    public JSKit5(BannerWebViewAct context) {
        this.ma = context;
    }

    public void showMsg(String msg) {
        Toast.makeText(ma, msg, Toast.LENGTH_SHORT).show();
    }
    @JavascriptInterface
    public void finish() {
//        Toast.makeText(ma, "完善银行卡信息成功", Toast.LENGTH_SHORT).show();
        Intent i=new Intent("freshbank");
        ma.sendBroadcast(i);
        ma.finish();
    }
}
