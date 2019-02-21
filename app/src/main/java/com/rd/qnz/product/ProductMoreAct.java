package com.rd.qnz.product;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.rd.qnz.BaseActivity;
import com.rd.qnz.R;
import com.rd.qnz.custom.APIModel;
import com.rd.qnz.custom.MyApplication;
import com.rd.qnz.dialog.CustomProgressDialog;
import com.rd.qnz.dialog.GetDialog;
import com.rd.qnz.tools.BaseParam;
import com.rd.qnz.view.GalleryUrlActivity;

import java.util.ArrayList;

/**
 * 产品详情里的更多
 *
 * @author Evonne
 */
@SuppressLint("SetJavaScriptEnabled")
public class ProductMoreAct extends BaseActivity {

    private GetDialog dia;
    private CustomProgressDialog progressDialog = null;
    private MyApplication myApp;
    APIModel apiModel = new APIModel();
    private WebView product_more_webview;
    private String borrowId = "";
    private String type = "";
    private ArrayList<String> list = new ArrayList<String>();


    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage_product_more);

        this.dia = new GetDialog();
        myApp = (MyApplication) getApplication();

        borrowId = getIntent().getStringExtra(BaseParam.QIAN_PRODUCT_BORROWID);
        type = getIntent().getStringExtra("type");
        initBar();

    }

    @SuppressLint("JavascriptInterface")
    private void initBar() {
        ImageView actionbar_side_left_iconfont = (ImageView) findViewById(R.id.actionbar_side_left_iconfont); //返回图标
        actionbar_side_left_iconfont.setVisibility(View.VISIBLE);

        actionbar_side_left_iconfont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                finish();
            }
        });
        TextView actionbar_side_name = (TextView) findViewById(R.id.actionbar_side_name);
        actionbar_side_name.setText("项目介绍");

        product_more_webview = (WebView) findViewById(R.id.product_more_webview);
        String web_url = startWebViewRequest();  //得到网址

        if (null != progressDialog && progressDialog.isShowing()) {// 隐藏加载框
            ProductMoreAct.this.progressDialog.dismiss();
        }

        // 启用javascript（相当于H5界面的源码）
        product_more_webview.getSettings().setJavaScriptEnabled(true);
        product_more_webview.loadUrl(web_url);
        Log.e("pepe", "web 和原生交互 方法=" + web_url);

        // 添加与H5交互接口类，并起别名 imagelistner（名字可随意取,需要和下面接收方法名字一样）
        product_more_webview.addJavascriptInterface(new JavascriptInterface(ProductMoreAct.this), "imagelistner");
        product_more_webview.setWebViewClient(new MyWebViewClient());

    }

    // 注入js函数监听（查看img标签下的所有点截取出来）
    private void addImageClickListner() {
        // 这段js函数的功能就是，遍历所有的img几点，并添加onclick函数，函数的功能是在图片点击的时候调用本地java接口并传递url过去
        product_more_webview.loadUrl("javascript:(function(){" +
                "var objs = document.getElementsByTagName(\"img\"); " +
                "for(var i=0;i<objs.length;i++)  " +
                "{" +
                "    objs[i].onclick=function()  " +
                "    {  " +
                "        window.imagelistner.openImage(this.getAttribute('data-src'));  " +
                "    }  " +
                "}" +
                "})()");

    }

    // 与H5的通信接口
    public class JavascriptInterface {

        private Context context;

        public JavascriptInterface(Context context) {
            this.context = context;
        }

        //点击图片回调方法 (必须添加注解,否则无法响应)
        @android.webkit.JavascriptInterface
        public void openImage(String img) {

            Log.i("点击图片回调(img) = ", img);

            //注释了点击事件
            if (type.equals("putong")) {
                Intent intent = new Intent();
                intent.putExtra("image", BaseParam.URL_QIAN + "/" + img);
                intent.putStringArrayListExtra("list", list);
                intent.setClass(context, GalleryUrlActivity.class);
                context.startActivity(intent);
            }
        }

        @android.webkit.JavascriptInterface  //把整个网页解析成文本传进去
        public void showSource(String html) {   //js来调用android本地方法
            //得到borrowFileList到 </body>直接的文本
            String html_re = html.substring(html.indexOf("borrowFileList"), html.indexOf("</body>"));
            String resture = html_re.substring(html_re.indexOf("[") + 1, html_re.indexOf("]"));
            String[] sure = resture.split(",");
            Log.e("resture**--", resture);

            list.clear();
            for (int i = 0; i < sure.length; i++) {
                if (sure[i].contains("fileUrl")) {
                    list.add(BaseParam.URL_QIAN + "/" + sure[i].substring(11, sure[i].length() - 1));
                }
            }
            // Log.e("list**--", list.toString().substring(1, list.toString().length() - 1));

            /*for (int i=0;i<lisiii.length;i++){
                Log.e("list**--", lisiii[i]);
            }*/
        }

    }

    // 监听客户端的点击
    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {

            view.getSettings().setJavaScriptEnabled(true);

            /*view.loadUrl("javascript:window.imagelistner.showSource('<head>'+" +
                    "document.getElementsByTagName('html')[0].innerHTML+'</head>');");*/
     //拿到图片对应的网址
            view.loadUrl("javascript:window.imagelistner.showSource('<body>'+" +
                    "document.getElementsByTagName('html')[0].innerHTML+'</body>');");  //调用js里面的方法


            super.onPageFinished(view, url);
            // html加载完成之后，添加监听图片的点击js函数
            addImageClickListner();

        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            view.getSettings().setJavaScriptEnabled(true);
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
        }
    }

    /**
     * 判断是否有wifi,得到对应的网址
     * @return
     */
    private String startWebViewRequest() {
//        String network = isWifi(ProductMoreAct.this);
        String[] array = new String[]{
                BaseParam.QIAN_PRODUCT_BORROWID + "=" + borrowId + "",
                "networkType=" + "1",
                BaseParam.URL_QIAN_API_APPID + "=" + myApp.appId,
                BaseParam.URL_QIAN_API_SERVICE + "=projectDetail",
                BaseParam.URL_QIAN_API_SIGNTYPE + "=" + myApp.signType};

        String sign = apiModel.sortStringArray(array);

        String url = BaseParam.URL_QIAN_PROJECTDETAIL + "?"
                + BaseParam.QIAN_PRODUCT_BORROWID + "=" + borrowId + "&"
                + "networkType=" + "1" + "&" + BaseParam.URL_QIAN_API_APPID
                + "=" + myApp.appId + "&" + BaseParam.URL_QIAN_API_SERVICE
                + "=projectDetail&" + BaseParam.URL_QIAN_API_SIGNTYPE + "="
                + myApp.signType + "&" + BaseParam.URL_QIAN_API_SIGN + "="
                + sign;
        Log.e("webview = ", url);
        return url;
    }

    /**
     *  有wifi返回1,没有返回0
     * @param mContext
     * @return
     */
    private static String isWifi(Context mContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return "1";
        }
        return "0";
    }
}
