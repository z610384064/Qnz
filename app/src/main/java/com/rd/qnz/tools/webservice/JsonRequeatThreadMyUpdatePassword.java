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
 * 修改密码
 */
public class JsonRequeatThreadMyUpdatePassword implements Runnable {

    private Handler myHandler;
    private ArrayList<String> param;
    private ArrayList<String> value;
    private Context context;
    private MyApplication myApp;
    private String login;

    public JsonRequeatThreadMyUpdatePassword(Context context, MyApplication myApp, Handler paramHandler, ArrayList<String> paramArrayList1, ArrayList<String> paramArrayList2, String login) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.myApp = myApp;
        this.myHandler = paramHandler;
        this.param = paramArrayList1;
        this.value = paramArrayList2;
        this.login = login;
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        JsonRequest request = new JsonRequest(context, myApp);
        Map<String, String> map = request.getWebserviceResult_getQIANMyUpdatePassword(this.param, this.value, login);
        Message localMessage = new Message();
        Bundle localBundle = new Bundle();
        ArrayList tmp_list = new ArrayList();
        tmp_list.add(map);
        localBundle.putParcelableArrayList(BaseParam.URL_REQUEAT_MY_MODIFYPWD, tmp_list);
        localMessage.setData(localBundle);
        this.myHandler.sendMessage(localMessage);
    }

}
