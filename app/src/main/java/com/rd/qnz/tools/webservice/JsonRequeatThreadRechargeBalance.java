package com.rd.qnz.tools.webservice;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.rd.qnz.custom.MyApplication;
import com.rd.qnz.tools.BaseParam;

/**
 * 充值到投资准备金 接口
 * @author win7-64
 *
 */
public class JsonRequeatThreadRechargeBalance implements Runnable{
	 private Handler myHandler; 
	 private ArrayList<String> param;
	 private ArrayList<String> value;
	 private Context context;
	 private MyApplication myApp;
	 
	 public JsonRequeatThreadRechargeBalance(Context context, MyApplication myApp, Handler paramHandler, ArrayList<String> paramArrayList1, ArrayList<String> paramArrayList2) {
		// TODO Auto-generated constructor stub
		 this.context = context;
		 this.myApp = myApp;
		 this.myHandler = paramHandler;
		 this.param = paramArrayList1;
		 this.value = paramArrayList2;
	}

	@Override
	public void run() {
		JsonRequest request = new JsonRequest(context,myApp);
		Map<String,String> map = request.JsonRequeatThreadRechargeBalance(this.param, this.value);
		Message localMessage = new Message();
		Bundle localBundle = new Bundle();
		ArrayList tmp_list = new ArrayList();
		tmp_list.add(map);
		localBundle.putParcelableArrayList(BaseParam.URL_QIAN_RECHARGE_BALANCE,tmp_list);
		localMessage.setData(localBundle);
		this.myHandler.sendMessage(localMessage);
	}
}
