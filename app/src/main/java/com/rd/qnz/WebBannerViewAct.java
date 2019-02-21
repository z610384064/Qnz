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
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.rd.qnz.custom.Profile;
import com.rd.qnz.entity.WebViewToAppBean;
import com.rd.qnz.homepage.SharePopupWindow;
import com.rd.qnz.tools.BaseParam;

/**
 * 钱袋-针对手机端公告接口,点击确定打开的webview(基本没用到)
 *
 * @author Evonne
 */
public class WebBannerViewAct extends BaseActivity {

    private WebView webview;
    private String web_url;
    private String title;
    private String userId;
    private String type = "";
    private String message = "";

    public static void start(Context context, String webUrl, String title, String type, String message) {
        Intent intent = new Intent(context, WebBannerViewAct.class);
        intent.putExtra("web_url", webUrl);
        intent.putExtra("type", type);
        intent.putExtra("title", title);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_webview);
        SharedPreferences preferences = getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_USER, Context.MODE_PRIVATE);
        web_url = getIntent().getStringExtra("web_url");
        title = getIntent().getStringExtra("title");
        userId = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_USERID, "");
        type = getIntent().getStringExtra("type");
        message = getIntent().getStringExtra("message");
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

        // 分享按钮
        ImageView actionbar_side_share = (ImageView) findViewById(R.id.actionbar_side_share);
        actionbar_side_share.setVisibility(View.VISIBLE);
        actionbar_side_share.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                Intent intent = new Intent(WebBannerViewAct.this, SharePopupWindow.class);
                intent.putExtra("title", title);
                intent.putExtra("type", type);
                intent.putExtra("message", message);

                String webUrl = "";
                String and = "?";
                int index = web_url.indexOf("?");
                if (index > 0) {
                    and = "&";
                } else {
                    and = "?";
                }

                if ("".equals(Profile.getUserShare()) || null == Profile.getUserShare()) {
                    intent.putExtra("web_url", web_url);
                } else {
                    intent.putExtra("web_url", web_url + and + "share=" + Profile.getUserShare());
                }
                startActivity(intent);
            }
        });

    }

    private void initView() {
        SharedPreferences preferences = getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_USER, Context.MODE_PRIVATE);
        String mAauthToken = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN, "");
        // String web_url = startWebViewRequest();

        webview = (WebView) findViewById(R.id.webview);
        webview.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
        // 设置出现缩放工具
        webview.getSettings().setBuiltInZoomControls(true);
        webview.getSettings().setSupportZoom(true);

        webview.getSettings().setJavaScriptEnabled(true);
        // webview.getSettings().setBlockNetworkImage(true);
        Log.i("内嵌网页", web_url);
        webview.loadUrl(web_url);
        webview.requestFocus();
        webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webview.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                WebViewToAppBean bean = WebViewToAppBean.decordUrlToBean(url);
                if (bean.isQianToAppView()) {
                    if (TextUtils.equals(bean.getAciton(), WebViewToAppBean.ACTION_TO_NEW_WEBVIEW)) {//打开一个新的网页
                        WebViewAct.start(WebBannerViewAct.this, bean.getTitle(), bean.getUrl());
                    } else if (TextUtils.equals(bean.getAciton(), WebViewToAppBean.ACTION_TO_SHARE)) {//打开分享
                        SharePopupWindow.start(WebBannerViewAct.this, "活动详情", bean.getLink(), "3", bean.getDesc(), bean.getImageUrl());
                    } else if (TextUtils.equals(bean.getAciton(), WebViewToAppBean.ACTION_TO_HISTORY)) {//关闭当前页
                        finish();
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

        });
    }

}
