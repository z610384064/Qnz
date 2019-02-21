package com.rd.qnz.xutils;

import android.widget.Toast;

import com.rd.qnz.mine.RedpackageAndAwardList;


public class JSKit3 {
    private RedpackageAndAwardList ma;

    public JSKit3(RedpackageAndAwardList context) {
        this.ma = context;
    }

    public void showMsg(String msg) {
        Toast.makeText(ma, msg, Toast.LENGTH_SHORT).show();
    }
}
