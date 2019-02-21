package com.rd.qnz.tools.webservice;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.rd.qnz.custom.MyApplication;
import com.rd.qnz.tools.BaseParam;

import java.util.ArrayList;


/**
 * 首页
 */
public class JsonRequeatThreadHomePageGaiList implements Runnable {

    private Handler myHandler;
    private ArrayList<String> param;
    private ArrayList<String> value;
    private Context context;
    private MyApplication myApp;

    public JsonRequeatThreadHomePageGaiList(Context context, MyApplication myApp, Handler paramHandler, ArrayList<String> paramArrayList1, ArrayList<String> paramArrayList2) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.myApp = myApp;
        this.myHandler = paramHandler;
        this.param = paramArrayList1;
        this.value = paramArrayList2;
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        JsonRequest request = new JsonRequest(context, myApp);
        String map = request.getWebserviceResult_getQIANMyHomePageGaiList(this.param, this.value);
        Log.d("首页数据:",map);
        Message localMessage = new Message();
        Bundle localBundle = new Bundle();
        localBundle.putString(BaseParam.URL_REQUEAT_MY_HOMEPAGE, map);
        localMessage.setData(localBundle);
        this.myHandler.sendMessage(localMessage);
    }


}
