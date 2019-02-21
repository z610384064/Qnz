package com.rd.qnz;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.rd.qnz.bean.JpushBean;
import com.rd.qnz.tools.BaseParam;
import com.rd.qnz.tools.Utils;

import java.util.List;

import cn.jpush.android.api.JPushInterface;

/**
 * 自定义接收器
 * 
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class MyReceiver extends BroadcastReceiver {
	private static final String TAG = "JPush";

	@Override
	public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        boolean actIsTop = false;
		Log.d(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));
	    ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
	    List<RunningTaskInfo>  tasksInfo = activityManager.getRunningTasks(1);
	    if(tasksInfo.size() > 0){  
	           //应用程序位于堆栈的顶层  
	           if("com.rd.qnz".equals(tasksInfo.get(0).topActivity.getPackageName())){  
	           	actIsTop = true;
	          }  
	     }  
        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {    //用户注册SDK的intent
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Log.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
            //send the Registration Id to your server...
                        
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {   //  用户接收SDK消息的intent
        	Log.d(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
//        	processCustomMessage(context, bundle);
        
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) { //用户接收SDK通知栏信息的intent
            Log.d(TAG, "[MyReceiver] 接收到推送下来的通知");
            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            Log.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);
        	
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {    //用户点击打开了通知
            Log.d(TAG, "[MyReceiver] 用户点击打开了通知");
            
            JPushInterface.reportNotificationOpened(context, bundle.getString(JPushInterface.EXTRA_MSG_ID));

			bundle = intent.getExtras();
			String message = bundle.getString(JPushInterface.EXTRA_EXTRA);
		  if (actIsTop){ //app已经打开
			  if (TextUtils.isEmpty(message)){   //链接上面没有网址,那么不动
//				  Intent i = new Intent(context, MainTabAct.class);
//				  i.putExtras(bundle);
//				  i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				  context.startActivity(i);
			  }else if (message.contains("locationUrl")){  //跳h5
				  message = bundle.getString(JPushInterface.EXTRA_EXTRA);
				  Gson gson=new Gson();
				  JpushBean jpushbean=gson.fromJson(message, JpushBean.class);
				  startWebView(jpushbean,context);
			  }
		  }else {  //app还没被打开
			  if (TextUtils.isEmpty(message)){    //链接上面没有网址,那么直接打开主界面
				  Intent i = new Intent(context, MainTabAct.class);
				  i.putExtras(bundle);
				  i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				  context.startActivity(i);
			  }else if (message.contains("locationUrl")){  //跳h5
				  message = bundle.getString(JPushInterface.EXTRA_EXTRA);
				  Gson gson=new Gson();
				  JpushBean jpushbean=gson.fromJson(message, JpushBean.class);
				  Intent i = new Intent(context, MainTabAct.class);
				  i.putExtras(bundle);
				  i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				  context.startActivity(i);
				  startWebView(jpushbean,context);
			  }else {
				  Intent i = new Intent(context, MainTabAct.class);
				  i.putExtras(bundle);
				  i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				  context.startActivity(i);
			  }
		  }






			/**
        	//打开自定义的Activity
            if(!actIsTop){  //应用程序已经打开,但不是在桌面,先判断网址
				bundle = intent.getExtras();
				String message = bundle.getString(JPushInterface.EXTRA_EXTRA);
				if (TextUtils.isEmpty(message)){ //链接上面没有网址,那么直接打开主界面
					Intent i = new Intent(context, MainTabAct.class);
					i.putExtras(bundle);
					i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(i);
				}else {  //跳h5
					message = bundle.getString(JPushInterface.EXTRA_EXTRA);
					Gson gson=new Gson();
					JpushBean jpushbean=gson.fromJson(message, JpushBean.class);
					startWebView(jpushbean,context);
				}

            }else { //如果当前应用已经打开,那么用户点击会进行跳转
				bundle = intent.getExtras();
				String message = bundle.getString(JPushInterface.EXTRA_EXTRA);
				Gson gson=new Gson();
				JpushBean jpushbean=gson.fromJson(message, JpushBean.class);
				startWebView(jpushbean,context);
			}
        	*/
        	
        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
            //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..
        	
        } else if(JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
        	boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
			if (connected){  //跟极光推送连接上了
				JPushInterface.init(context);
			}else { //跟极光推送的连接断开了
				 JPushInterface.init(context);
			}
        	Log.e(TAG, "[MyReceiver]" + intent.getAction() +" connected state change to "+connected);
        } else {
        	Log.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
        }
	}

	private void startWebView(JpushBean jpush, Context context) {

		String intro= jpush.getIntro();
		String duce=jpush.getDuce();
		String web_url= BaseParam.URL_QIAN +jpush.getLocationUrl();
		String imageUrl= Utils.getAbsoluteUrlPath(jpush.getUrl());
		if (TextUtils.isEmpty(intro)&&TextUtils.isEmpty(duce)){ //标题和内容都为空
			Intent intent=new Intent(context,WebBannerViewNeedAccesTokenActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra("web_url", web_url);
			intent.putExtra("type", "3");
			intent.putExtra("imageUrl",imageUrl);
			context.startActivity(intent);
		}else  if (TextUtils.isEmpty(intro)){  //标题为空
			Intent intent=new Intent(context,WebBannerViewNeedAccesTokenActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra("web_url",web_url);
			intent.putExtra("type", "3");

			intent.putExtra("imageUrl", imageUrl);
			intent.putExtra("duce",duce);
			context.startActivity(intent);
		}else if (TextUtils.isEmpty(duce)){ //内容为空
			Intent intent=new Intent(context,WebBannerViewNeedAccesTokenActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra("web_url",web_url);
			intent.putExtra("type", "3");

			intent.putExtra("imageUrl", imageUrl);
			intent.putExtra("intro",intro);
			context.startActivity(intent);
		}else { //两者都不为空
			Intent intent=new Intent(context,WebBannerViewNeedAccesTokenActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra("web_url", web_url);
			intent.putExtra("type", "3");
			intent.putExtra("imageUrl", imageUrl);
			intent.putExtra("intro",intro);  //标题
			intent.putExtra("duce",duce);    //分享出去的文本介绍
			context.startActivity(intent);
		}
	}


	// 打印所有的 intent extra 数据
	private static String printBundle(Bundle bundle) {
		StringBuilder sb = new StringBuilder();
		for (String key : bundle.keySet()) {
			if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
				sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
			}else if(key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)){
				sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
			} 
			else {
				sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
			}
		}
		return sb.toString();
	}

}
