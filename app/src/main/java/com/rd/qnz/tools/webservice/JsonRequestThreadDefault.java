package com.rd.qnz.tools.webservice;

import android.content.Context;

import com.rd.qnz.custom.MyApplication;

import java.util.ArrayList;

/**
 * 推广
 */
public class JsonRequestThreadDefault implements Runnable {

    private ArrayList<String> param;
    private ArrayList<String> value;
    private Context context;
    private MyApplication myApp;

    public JsonRequestThreadDefault(Context context, MyApplication myApp, ArrayList<String> paramArrayList1, ArrayList<String> paramArrayList2) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.myApp = myApp;
        this.param = paramArrayList1;    //参数头
        this.value = paramArrayList2;    //参数值
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        JsonRequest request = new JsonRequest(context, myApp);
        request.getWebserviceResult_getQIANDefault(this.param, this.value);
    }

}
