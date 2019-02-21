package com.rd.qnz.webview;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.http.SslError;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JsPromptResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.rd.qnz.BaseActivity;
import com.rd.qnz.R;
import com.rd.qnz.homepage.SharePopupWindow;
import com.rd.qnz.login.Login;
import com.rd.qnz.tools.BaseParam;

/**
 *  邀请好友功能对应的h5  总共两个h5页面 一个是邀请好友,一个是邀请成果
 */
public class ShareWebViewActivity extends BaseActivity implements View.OnClickListener {

    private WebView webview;
    private TextView actionbar_side_name; //标题
    private  ImageView actionbar_side_left_iconfont; //左上角回退键
    private String oauthToken;
    private int currentPager=0;  //当前页面,默认是第一页
    private String baseUrl="/invite/showMyInvite.html?accessToken=";  //基础网页
    private String url;
    private String share_url; //分享的网址
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_web_view);
        SharedPreferences preferences = getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_USER, Context.MODE_PRIVATE);
        oauthToken=preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN, "");
        if (TextUtils.isEmpty(oauthToken)){
            startActivity(new Intent(ShareWebViewActivity.this, Login.class));
        }
        url=BaseParam.URL_QIAN+baseUrl+oauthToken;
        initBar();
        initView();
    }

    private void initBar() {
        actionbar_side_left_iconfont = (ImageView) findViewById(R.id.actionbar_side_left_iconfont);
        actionbar_side_left_iconfont.setVisibility(View.VISIBLE);
        actionbar_side_left_iconfont.setOnClickListener(this);

        actionbar_side_name = (TextView) findViewById(R.id.actionbar_side_name);
        actionbar_side_name.setText("邀请有礼");

    }

        /**
         * 需要后台给我一个基础网页
         */
    private void initView() {

        webview = (WebView) findViewById(R.id.webview);
        webview.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR); //针对特定屏幕调整分辨率



        // 设置出现缩放工具
        webview.getSettings().setBuiltInZoomControls(true);
        webview.getSettings().setSupportZoom(true);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.addJavascriptInterface(new JavascriptInterface(),"js");


        webview.requestFocus();
        webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);


        webview.setWebChromeClient(new WebChromeClient());
        webview.setWebViewClient(new WebViewClient() {

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.e("原生url = ", "url == " + url);
                if (url.contains("inviteIndex")){ //说明是准备分享给好友了,那么就不跳转

                    Intent intent = new Intent(ShareWebViewActivity.this, SharePopupWindow.class);

                    intent.putExtra("imageUrl", "");//https://testqnz.qianneizhu.com//data/images/banner/1489050963949.png
                    intent.putExtra("type", "3");
                    intent.putExtra("title", "【钱内助】您的土豪朋友送您528元");
                    intent.putExtra("intro", "【钱内助】您的土豪朋友送您528元");
                    intent.putExtra("message", BaseParam.QIAN_BANNER_INTRO);
//                    intent.putExtra("duce","注册即得528元红包，享专属14.4%超高收益，还有精彩活动等着你哦");
                    intent.putExtra("duce","这是个选择大于努力的时代，这是个选择改变命运的时代，你真的选好了吗？\n");
                    intent.putExtra("web_url", share_url);
                    startActivity(intent);

                }
                     else {
                    webview.loadUrl(url);

                }
            return  true;
            }
            //让webview处理https的请求
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                // super.onReceivedSslError(view, handler, error);
                handler.proceed();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
//                Toast.makeText(ShareWebViewActivity.this, "onPageFinished()被调用", Toast.LENGTH_SHORT).show();
                if (url.contains("showMyInvite")){ //分享有礼界面
                    currentPager=0;
                    actionbar_side_name.setText("邀请好友");
                    addButtonClickListner(); //网页加载完成后,给立即邀请按钮重新设置点击事件
                }
                else if (url.contains("inviteAchievement")){  //推广成果界面
                    actionbar_side_name.setText("我的邀请记录");
                    currentPager=1;
                }

            }

        });
        webview.loadUrl(url);
    }
    class MyWebChromeClient extends WebChromeClient{
        @Override
        public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
            return super.onJsPrompt(view, url, message, defaultValue, result);
        }
    }
    /**
     * java和js交互的类
     */
    public class JavascriptInterface {
        @android.webkit.JavascriptInterface  //得到分享的网址
        public void getShareUrl(String url){
            share_url=url;
        }
        @android.webkit.JavascriptInterface  //得到分享的网址
        public void Share(String url){
//           showToast("得到要分享的网址了:"+share_url);
            share_url=url;
        }
    }

    /**
     * 动态的给btn设置点击事件   this.getAttribute('data-src')
     */
    private void addButtonClickListner() {
        // 这段js函数的功能就是，遍历所有的img几点，并添加onclick函数，函数的功能是在图片点击的时候调用本地java接口并传递url过去
        webview.loadUrl("javascript:(function(){" +
                "var objs = document.getElementsByClassName(\"btn\"); " +
                "objs[0].onclick=function() " +
                "  {      window.js.Share(this.getAttribute('href'));  " +
                "    }  " +
                "})()");

    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if((keyCode == KeyEvent.KEYCODE_BACK) &&webview.canGoBack()) {
            webview.goBack();
            currentPager--;
            return true;

        }

        return super.onKeyDown(keyCode,event);

    }

    @Override
    public void onClick(View v) {
            switch (v.getId()){
                case R.id.actionbar_side_left_iconfont:  //左上角回退
                      if (currentPager==0){
                         finish();
                    }else if (currentPager==1){
                          webview.goBack();
                          currentPager--;
                    }
            }
    }
}
