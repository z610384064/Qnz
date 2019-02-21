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
 * 申购  选择红包  和   银行卡
 */
public class JsonRequeatThreadavailableRedPacket implements Runnable {

    private Handler myHandler;
    private ArrayList<String> param;
    private ArrayList<String> value;
    private Context context;
    private MyApplication myApp;

    public JsonRequeatThreadavailableRedPacket(Context context, MyApplication myApp, Handler paramHandler, ArrayList<String> paramArrayList1, ArrayList<String> paramArrayList2) {
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
        String map = request.getWebserviceResult_getQIANGetpayInfo(this.param, this.value);
        Message localMessage = new Message();
        Bundle localBundle = new Bundle();
        localBundle.putString(BaseParam.URL_REQUEAT_MY_AVAILABLEREDPACKET, map);
        localMessage.setData(localBundle);
        this.myHandler.sendMessage(localMessage);
    }


}
