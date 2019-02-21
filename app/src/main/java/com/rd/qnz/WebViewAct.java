package com.rd.qnz;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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

import com.rd.qnz.entity.WebViewToAppBean;
import com.rd.qnz.homepage.SharePopupWindow;
import com.rd.qnz.mine.SuggestAct;
import com.rd.qnz.tools.BaseParam;
import com.rd.qnz.xutils.JSKit2;

/**
 *  常见问题
 * @author Evonne
 */
@SuppressLint("SetJavaScriptEnabled")
public class WebViewAct extends BaseActivity implements  OnClickListener{

    private WebView webview;
    private String web_url;
    private String title;
    private String help;//是否是帮助界面

    public static void start(Context context, String title, String url) {
        Intent i = new Intent();
        i.setClass(context, WebViewAct.class);
        i.putExtra("title", title);
        i.putExtra("web_url", url);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_webview);
        web_url = getIntent().getStringExtra("web_url");
        if (web_url.contains("editBankInfo.html")){
            web_url=web_url+"&type=2";
        }
        Log.i("pepe", web_url);
        title = getIntent().getStringExtra("title");
        help = getIntent().getStringExtra("help");
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
        ImageView actionbar_side_share= (ImageView) findViewById(R.id.actionbar_side_share);
        if (!TextUtils.isEmpty(help)){
            actionbar_side_share.setVisibility(View.VISIBLE);
            actionbar_side_share.setOnClickListener(this);
            actionbar_side_share.setImageResource(R.drawable.mine_fankui);
        }
    }

    private void initView() {
        SharedPreferences preferences = getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_USER, Context.MODE_PRIVATE);
        String mAauthToken = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN, "");

        JSKit2 js = new JSKit2(this);
        webview = (WebView) findViewById(R.id.webview);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.setWebChromeClient(new WebChromeClient());
        webview.getSettings().setDefaultTextEncodingName("UTF-8");
        // 设置可以支持缩放
        webview.getSettings().setSupportZoom(true);
        WebSettings websetting = webview.getSettings();
        websetting.setJavaScriptEnabled(true);
        webview.addJavascriptInterface(js, "myjs");
        websetting.setDefaultZoom(WebSettings.ZoomDensity.FAR);
        // 设置出现缩放工具
        webview.getSettings().setBuiltInZoomControls(true);
        webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webview.setWebChromeClient(new WebChromeClient());
        webview.setWebViewClient(new MyWebViewClient());
        webview.requestFocus();
        webview.loadUrl(web_url);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
           case  R.id.actionbar_side_share:
               startActivity(new Intent(WebViewAct.this, SuggestAct.class));
            break;
        }
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            WebViewToAppBean bean = WebViewToAppBean.decordUrlToBean(url);
             if (url.contains("mailto")){
                Intent data=new Intent(Intent.ACTION_SENDTO);
                data.setData(Uri.parse(url));
                data.putExtra(Intent.EXTRA_SUBJECT, "这是标题");
                data.putExtra(Intent.EXTRA_TEXT, "这是内容");
                startActivity(data);
                 return true;
            }
            if (bean.isQianToAppView()) {
                if (TextUtils.equals(bean.getAciton(), WebViewToAppBean.ACTION_TO_NEW_WEBVIEW)) {//打开一个新的网页
                    WebViewAct.start(WebViewAct.this, bean.getTitle(), bean.getUrl());
                } else if (TextUtils.equals(bean.getAciton(), WebViewToAppBean.ACTION_TO_SHARE)) {//打开分享
                    SharePopupWindow.start(WebViewAct.this, bean.getTitle(), bean.getLink(), "3", bean.getDesc(), bean.getImageUrl());
                } else if (TextUtils.equals(bean.getAciton(), WebViewToAppBean.ACTION_TO_PROJECTDETAIL)) {//申购成功，我知道了
                    finish();
                } else if (TextUtils.equals(bean.getAciton(), WebViewToAppBean.ACTION_TO_MY_BANK)) {//单卡设置
                    finish();
                } else if (TextUtils.equals(bean.getAciton(), WebViewToAppBean.ACTION_TO_HISTORY)) {//关闭当前页
                    finish();
                } else if (TextUtils.equals(bean.getAciton(), WebViewToAppBean.ACTION_TO_GET_BANK)) {//关闭当前页
                    finish();
                }
                return true;
            }
            else {
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
            super.onPageFinished(view, url);
        }
    }

}
