package com.rd.qnz.xutils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.rd.qnz.BaseActivity;
import com.rd.qnz.R;
import com.rd.qnz.tools.BaseParam;

/**
 * 产品申购界面底部服务协议的h5界面
 *
 * @author Evonne
 */
@SuppressLint("SetJavaScriptEnabled")
public class WebViewAct2 extends BaseActivity {

    private WebView webview;
    private String web_url;
    private String title;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_webview);
        web_url = getIntent().getStringExtra("web_url");

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
        TextView tv= (TextView) findViewById(R.id.actionbar_side_name);
        tv.setText("查看协议");

    }

    private void initView() {
        SharedPreferences preferences = getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_USER, Context.MODE_PRIVATE);
        String mAauthToken = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN, "");
        // String web_url = startWebViewRequest();

        webview = (WebView) findViewById(R.id.webview);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.setWebChromeClient(new WebChromeClient());
        webview.getSettings().setDefaultTextEncodingName("UTF-8");
        // 设置可以支持缩放
        webview.getSettings().setSupportZoom(true);
        WebSettings websetting = webview.getSettings();
        websetting.setJavaScriptEnabled(true);

        websetting.setDefaultZoom(WebSettings.ZoomDensity.FAR);
        // 设置出现缩放工具
        webview.getSettings().setBuiltInZoomControls(true);
        webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        webview.requestFocus();
        webview.loadUrl(web_url);


    }



}
