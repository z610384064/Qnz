package com.rd.qnz.tools.webservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rd.qnz.custom.MyApplication;
import com.rd.qnz.tools.BaseParam;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;


/**
 * 红包及奖励
 */
public class JsonRequeatThreadMyRedpacketsList implements Runnable {

    private Handler myHandler;
    private ArrayList<String> param;
    private ArrayList<String> value;
    private Context context;
    private MyApplication myApp;
    private String type;

    public JsonRequeatThreadMyRedpacketsList(Context context, MyApplication myApp, Handler paramHandler, ArrayList<String> paramArrayList1, ArrayList<String> paramArrayList2, String type) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.myApp = myApp;
        this.myHandler = paramHandler;
        this.param = paramArrayList1;
        this.value = paramArrayList2;
        this.type = type;
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        JsonRequest request = new JsonRequest(context, myApp);
        String map;

        if (type.equals("1")) {//红包
            map = request.getWebserviceResult_getQIANMyRedpacketsGaiList(this.param, this.value);
        } else {//奖励
            map = request.getWebserviceResult_getQIANMyAwardList(this.param, this.value);
        }

        Message localMessage = new Message();
        Bundle localBundle = new Bundle();
        localBundle.putString(BaseParam.URL_REQUEAT_MY_REDPACKETS, map);
        localMessage.setData(localBundle);
        this.myHandler.sendMessage(localMessage);
    }

}
