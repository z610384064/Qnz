package com.rd.qnz;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.http.SslError;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.rd.qnz.custom.MyApplication;
import com.rd.qnz.entity.WebViewToAppBean;
import com.rd.qnz.homepage.SharePopupWindow;
import com.rd.qnz.tools.BaseParam;

/**
 * 余额申购产品 跳转的h5界面
 */
public class WebViewPayResultActivity extends BaseActivity {
    private WebView webview;
    private String web_url;
    private String title;
    private String type ; //产品类型(1:新手标,2:普通标)
    public static void start(Context context, String title, String url) {
        Intent i = new Intent();
        i.setClass(context, WebViewPayResultActivity.class);
        i.putExtra("title", title);
        i.putExtra("web_url", url);
        context.startActivity(i);
    }

    // TODO: 2017/3/10 0010  增加一个产品类别
    public static void start(Context context, String title, String url,String type) {
        Intent i = new Intent();
        i.setClass(context, WebViewPayResultActivity.class);
        i.putExtra("title", title);
        i.putExtra("web_url", url);
        i.putExtra("type",type);
        context.startActivity(i);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_webview);
        web_url = getIntent().getStringExtra("web_url");
        title = getIntent().getStringExtra("title");
        type = getIntent().getStringExtra("type");
        setSwipeBackEnable(false); //禁止滑动删除
        initBar();
        initView();
    }

    private void initBar() {
        ImageView actionbar_side_left_iconfont = (ImageView) findViewById(R.id.actionbar_side_left_iconfont);
        actionbar_side_left_iconfont.setVisibility(View.VISIBLE);
        actionbar_side_left_iconfont.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                finish();
            }
        });

        TextView actionbar_side_name = (TextView) findViewById(R.id.actionbar_side_name);
        actionbar_side_name.setText(title);
    }

    private void initView() {
        SharedPreferences preferences = getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_USER, Context.MODE_PRIVATE);
        String mAauthToken = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN, "");
        // String web_url = startWebViewRequest();
        webview = (WebView) findViewById(R.id.webview);
        webview.getSettings().setJavaScriptEnabled(true); ////设置为可调用js方法
        webview.setWebChromeClient(new WebChromeClient());
        webview.getSettings().setDefaultTextEncodingName("UTF-8");
        // 设置可以支持缩放
        webview.getSettings().setSupportZoom(true);
        WebSettings websetting = webview.getSettings();
        websetting.setJavaScriptEnabled(true);  //让webview支持js
        websetting.setDefaultZoom(WebSettings.ZoomDensity.FAR);
        // 设置出现缩放工具
        webview.getSettings().setBuiltInZoomControls(true);
        webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webview.setWebViewClient(new MyWebViewClient());  //让页面在app内执行
        webview.requestFocus();

        webview.loadUrl(web_url);
        Log.i("web_url", web_url);
//		webview.setWebViewClient(new WebViewClient() {
//			public boolean shouldOverrideUrlLoading(WebView view, String url) {
//				view.loadUrl(url);
//				return true;
//			}
//		});

    }

    private class MyWebViewClient extends WebViewClient {
        @Override  //点击了继续投资之后跳进来 ,拦截html的点击事件
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.e("web url", "web 和原生交互 = " + url);//mGetAccountJSMethod  projectDetail.html
            WebViewToAppBean bean = WebViewToAppBean.decordUrlToBean(url);
            if (bean.isQianToAppView()) {
                if (TextUtils.equals(bean.getAciton(), WebViewToAppBean.ACTION_TO_NEW_WEBVIEW)) {//打开一个新的网页
                    WebViewAct.start(WebViewPayResultActivity.this, bean.getTitle(), bean.getUrl());
                } else if (TextUtils.equals(bean.getAciton(), WebViewToAppBean.ACTION_TO_SHARE)) {//打开分享
                    SharePopupWindow.start(WebViewPayResultActivity.this, bean.getTitle(), bean.getLink(), "3", bean.getDesc(), bean.getImageUrl());
                } else if (TextUtils.equals(bean.getAciton(), WebViewToAppBean.ACTION_TO_PROJECTDETAIL)) {//申购成功，我知道了
                    finish();
                } else if (TextUtils.equals(bean.getAciton(), WebViewToAppBean.ACTION_TO_MY_BANK)) {//单卡设置
                    finish();
                } else if (TextUtils.equals(bean.getAciton(), WebViewToAppBean.ACTION_TO_HISTORY)) {//关闭当前页
                    if (type.equals("1")){
                        Intent i=new Intent("new");
                        sendBroadcast(i);
                    }
                    Intent i=new Intent("web"); //发送广播到产品详情页ProductContentAct,把它关闭了
                    sendBroadcast(i);
                    finish();   //从这里跳到产品界面
                    MyApplication.getInstance().tabHostId=1;
                    MyApplication.getInstance().tabHost.setCurrentTab(MyApplication.getInstance().tabHostId);
                }
                return true;
            } else {
                view.loadUrl(url);
                return true;
            }
        }
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            // super.onReceivedSslError(view, handler, error);
            handler.proceed();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO 自动生成的方法存根
            super.onPageFinished(view, url);
        }
    }
}
