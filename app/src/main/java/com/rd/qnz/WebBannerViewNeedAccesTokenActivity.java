package com.rd.qnz;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.http.SslError;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rd.qnz.custom.MineShow;
import com.rd.qnz.custom.MyApplication;
import com.rd.qnz.custom.Profile;
import com.rd.qnz.entity.WebViewToAppBean;
import com.rd.qnz.homepage.SharePopupWindow;
import com.rd.qnz.login.Login;
import com.rd.qnz.product.ProductContentAct;
import com.rd.qnz.tools.BaseParam;
import com.rd.qnz.tools.Check;

/**
 * 首页banner,活动有关的全都走这个类
 *
 * 需要客户端额外加 acces_token参数的 1. 点击首页BANNER 2. 点击首页BANNER下活动图片（包括新手、非新手图片） 4.更多-活动中心点击某个活动
 * 5、钱内助社区
 *
 * @author win7-64
 */
public class WebBannerViewNeedAccesTokenActivity extends BaseActivity implements  OnClickListener{
    private WebView webview;
    private String web_url;
    private String mShareUrl;// 分享出去的url
    private String fileName="";
    private String userId;
    private String type = "";
    private String imageUrl;// 分享的图片

    private String intro= "";  //分享出去的标题
    private String duce=""; //分享出去的文本
    private ImageView actionbar_side_left_iconfont; //左上角回退键
    private TextView   actionbar_side_name;
    private int currentPager=0;
    /**
     * 邀请好友
     */
    private Button btn_yaoqing; //邀请好友按钮
    private String url= "";
    private RelativeLayout rl_button;
    private String TAG="WebBannerViewNeedAccesTokenActivity";


    private String  mAauthToken;

    /**
     *   从活动中心点击某个item进入这个界面
     * @param context
     * @param webUrl
     * @param fileName
     * @param type
     * @param intro
     * @param imageUrl
     * @param duce
     */
    public static void start(Context context, String webUrl, String fileName, String type, String intro, String imageUrl,String duce) {
        Intent intent = new Intent(context, WebBannerViewNeedAccesTokenActivity.class);
        intent.putExtra("web_url", webUrl);
        intent.putExtra("type", type);
        intent.putExtra("fileName", fileName);
        intent.putExtra("intro", intro);
        intent.putExtra("imageUrl", imageUrl);
        intent.putExtra("duce", duce);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newact_webview);
        Intent i=getIntent();
        SharedPreferences preferences = getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_USER, Context.MODE_PRIVATE);
        userId = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_USERID, "");
        web_url = i.getStringExtra("web_url");
        fileName = i.getStringExtra("fileName");

        type = i.getStringExtra("type");
        imageUrl = i.getStringExtra("imageUrl");
        intro=i.getStringExtra("intro");
        duce=i.getStringExtra("duce");
        initBar();
        initView();
    }

    private void initBar() {
        actionbar_side_left_iconfont = (ImageView) findViewById(R.id.actionbar_side_left_iconfont);
        actionbar_side_left_iconfont.setVisibility(View.VISIBLE);

        actionbar_side_left_iconfont.setOnClickListener(this);
        actionbar_side_name = (TextView) findViewById(R.id.actionbar_side_name);


        if (!TextUtils.isEmpty(fileName)){
            actionbar_side_name.setText(fileName);
        }else {

            actionbar_side_name.setText("活动详情");
        }

        // 活动分享按钮
        ImageView actionbar_side_share = (ImageView) findViewById(R.id.actionbar_side_share);
        actionbar_side_share.setVisibility(View.VISIBLE);
        actionbar_side_share.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(WebBannerViewNeedAccesTokenActivity.this, SharePopupWindow.class);
                intent.putExtra("fileName", fileName);
                intent.putExtra("imageUrl", imageUrl);
                intent.putExtra("type", type);
                if (!TextUtils.isEmpty(intro)){
                    intent.putExtra("intro", intro); //标题
                }
                if (!TextUtils.isEmpty(duce)){
                    intent.putExtra("duce",duce);  //文本描述
                }
                String webUrl = "";
                String and = "?";
                int index = web_url.indexOf("?");
                if (index > 0) {
                    and = "&";
                } else {
                    and = "?";
                }
             //拿到sp文件里面share的字段
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
        mAauthToken = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN, "");
        btn_yaoqing= (Button) findViewById(R.id.btn_yaoqing);  //底部的邀请按钮
        btn_yaoqing.setOnClickListener(this);
        rl_button= (RelativeLayout) findViewById(R.id.rl_button);

        webview = (WebView) findViewById(R.id.webview);
        webview.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
        // 设置出现缩放工具
        webview.getSettings().setBuiltInZoomControls(true);
        webview.getSettings().setSupportZoom(true);
        webview.getSettings().setJavaScriptEnabled(true);
        loadWebView();


    }

    private void loadWebView() {

        String webUrl = "";
        String and = "?";
        int index = web_url.indexOf("?");
        if (index > 0) {
            and = "&";
        } else {
            and = "?";
        }
        if (web_url.contains("share")){    //如果包含share,就显示
            rl_button.setVisibility(View.VISIBLE);

        }else {  //不包含share,根本不显示按钮
            rl_button.setVisibility(View.GONE);
        }


        if ("".equals(mAauthToken) || null == mAauthToken) {
            webUrl = web_url;
        } else {
            webUrl = web_url + and + "access_token=" + mAauthToken;
        }

        String webUrl2 = "";
        String and2 = "?";
        int index2 = webUrl.indexOf("?");
        if (index2 > 0) {
            and2 = "&";
        } else {
            and2 = "?";
        }
        webUrl2 = webUrl + and2 + "native_view=true";

        webview.requestFocus();
        webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webview.setWebChromeClient(new WebChromeClient());

        webview.setWebViewClient(new WebViewClient() {
            //该方法只有在点击h5 上的超链接才会被调用
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.e("原生url = ", "url == "+ url);
                WebViewToAppBean bean = WebViewToAppBean.decordUrlToBean(url);
                if (bean.isQianToAppView()) {


                    if (TextUtils.equals(bean.getAciton(), WebViewToAppBean.ACTION_TO_NEW_WEBVIEW)) {// 打开一个新的网页
                        WebViewAct.start(WebBannerViewNeedAccesTokenActivity.this, bean.getTitle(), bean.getUrl());

                    } else if (TextUtils.equals(bean.getAciton(), WebViewToAppBean.ACTION_TO_SHARE)) {// 打开分享
                        SharePopupWindow.start(
                                WebBannerViewNeedAccesTokenActivity.this,
                                "活动详情",
                                bean.getLink(),
                                "3",
                                bean.getDesc(),
                                bean.getImageUrl());
                    } else if (TextUtils.equals(bean.getAciton(), WebViewToAppBean.ACTION_TO_PROJECTDETAIL)) {// 申购成功，我知道了
                        finish();
                    } else if (TextUtils.equals(bean.getAciton(), WebViewToAppBean.ACTION_TO_MY_BANK)) {// 单卡设置
                        finish();
                    } else if (TextUtils.equals(bean.getAciton(), WebViewToAppBean.ACTION_TO_HISTORY)) {// 关闭当前页
                        finish();
                    }else if (TextUtils.equals(bean.getAciton(), WebViewToAppBean.ACTION_TO_OPENNATIVEPAGE)){ //跳转到原生界面

                        if (MineShow.fastClick()) {
                            if (Check.hasInternet(WebBannerViewNeedAccesTokenActivity.this)) {
                                String type=bean.getType();
                                if (type!=null&&type.equals("register")){ //跳到登录界面
                                    Intent i=new Intent(WebBannerViewNeedAccesTokenActivity.this,Login.class);
                                    startActivity(i);
                                }
                                else if (type!=null&&type.equals("tender")){  //跳到产品界面

                                    MyApplication myApp=MyApplication.getInstance();
                                        myApp.tabHostId = 1;
                                        myApp.tabHost.setCurrentTab(myApp.tabHostId);   //跳转到产品界面
                                        finish();

                                }else if (type!=null&&type.equals("wallet")){
                                    MyApplication myApp=MyApplication.getInstance();
                                    myApp.tabHostId = 3;
                                    myApp.tabHost.setCurrentTab(myApp.tabHostId);   //跳转到钱袋界面
                                    finish();
                                }else if (type!=null&&type.contains("borrowId")){
                                    String id=type.substring(9,type.length());

                                    Intent intent = new Intent(WebBannerViewNeedAccesTokenActivity.this, ProductContentAct.class);  //跳转到产品详情页
                                    intent.putExtra(BaseParam.QIAN_PRODUCT_BORROWID, id);
                                    startActivity(intent);
                                    finish();

                                }
                            } else {
                                MineShow.toastShow("请检查网络连接是否正常", WebBannerViewNeedAccesTokenActivity.this);
                            }
                        }

                    }
                    return true;
                } else {
                    view.loadUrl(url);
                    currentPager++;
                    return true;
                }
            }
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                // super.onReceivedSslError(view, handler, error);
                handler.proceed();
            }


        });
//        webUrl2="http://test.qianneizhu.com/activity.html?returnUrl=app-test-jump";
        webview.loadUrl(webUrl2);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if((keyCode == KeyEvent.KEYCODE_BACK) &&webview.canGoBack()&&currentPager!=0) {
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
                break;
            case R.id.btn_yaoqing:  //邀请 按钮
                //点击的一瞬间判断是否登录,没登录就跳转到登录界面

                if ("".equals(mAauthToken) || null == mAauthToken) { //用户没登录跳转到登录界面
                    Intent i=new Intent(WebBannerViewNeedAccesTokenActivity.this, Login.class);
                    startActivity(i);

                } else { //已经登录了,就弹出分享
                    url=BaseParam.URL_QIAN_BANNER_YAOQING+ Profile.getUserShare();
                    Intent intent = new Intent(WebBannerViewNeedAccesTokenActivity.this, SharePopupWindow.class);
                    intent.putExtra("imageUrl", "");//https://testqnz.qianneizhu.com//data/images/banner/1489050963949.png
                    intent.putExtra("type", "3");
                    intent.putExtra("title", "【钱内助】您的土豪朋友送您528元");
                    intent.putExtra("intro", "【钱内助】您的土豪朋友送您528元");
                    intent.putExtra("message", BaseParam.QIAN_BANNER_INTRO);
                    intent.putExtra("duce","这是个选择大于努力的时代，这是个选择改变命运的时代，你真的选好了吗？\n");
                    intent.putExtra("web_url", url);

                    startActivity(intent);
                }

                break;
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        SharedPreferences preferences = getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_USER, Context.MODE_PRIVATE);
        mAauthToken = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN, "");

        if ("".equals(mAauthToken) || null == mAauthToken) { //用户没登录,那保持原样

        }else {
           loadWebView();
        }

    }
}
