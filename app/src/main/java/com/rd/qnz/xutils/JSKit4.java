package com.rd.qnz.xutils;

import android.content.Context;
import android.widget.Toast;

/**
 * 红包和加息劵 h5 界面的交互类
 */
public class JSKit4 {
    private Context ma;

    public JSKit4(Context context) {
        this.ma = context;
    }

    public void showMsg(String msg) {
        Toast.makeText(ma, msg, Toast.LENGTH_SHORT).show();
    }
}
