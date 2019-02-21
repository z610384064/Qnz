package com.rd.qnz.product.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.rd.qnz.R;
import com.rd.qnz.custom.APIModel;
import com.rd.qnz.custom.MyApplication;
import com.rd.qnz.tools.BaseParam;
import com.rd.qnz.tools.CustomViewpager;
import com.rd.qnz.view.GalleryUrlActivity;

import java.util.ArrayList;


/**
 * 带CustomViewpager on 2017/5/9 0009.
 */

public class KnowProjectFragment extends Fragment {

    private MyApplication myApp;
    APIModel apiModel = new APIModel();
    private String borrowId = "";
    private String type = "";
    private ArrayList<String> list = new ArrayList<String>();

    private Context context;
    private CustomViewpager viewpager;
    private String web_url;
    private String id;
    public KnowProjectFragment() {
    }

    private View view;
    private boolean loadFinish = false;
    private WebView product_more_webview;

    public KnowProjectFragment(String borrowId, String type, Context context, CustomViewpager viewpager) {
        this.borrowId = borrowId;
        this.type = type;
        this.context = context;
        this.viewpager = viewpager;
    }

    public KnowProjectFragment(String borrowId, String type, Context context) {
        this.borrowId = borrowId;
        this.type = type;
        this.context = context;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = View.inflate(context, R.layout.homepage_product_more2, null);
        myApp = (MyApplication) getActivity().getApplication();

        product_more_webview = (WebView) view.findViewById(R.id.product_more_webview);
        startWebView();
        Log.e("pepe", "web 和原生交互 方法=" + web_url);

        // 添加与H5交互接口类，并起别名 imagelistner（名字可随意取,需要和下面接收方法名字一样）

        return view;
    }

    public void startWebView() {
        web_url=startWebViewRequest();
        product_more_webview.getSettings().setJavaScriptEnabled(true);
        product_more_webview.addJavascriptInterface(new JavascriptInterface(context), "imagelistner");
        product_more_webview.setWebViewClient(new MyWebViewClient());

        product_more_webview.getSettings().setDomStorageEnabled(true);  //开启缓存机制
        product_more_webview.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        product_more_webview.setVerticalScrollBarEnabled(false);
        product_more_webview.setClickable(true);
        product_more_webview.getSettings().setBlockNetworkImage(true); //图片下载阻塞
        product_more_webview.getSettings().setUseWideViewPort(true);
        product_more_webview.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        product_more_webview.getSettings().setSaveFormData(true);
        product_more_webview.getSettings().setAllowFileAccess(true);
        product_more_webview.getSettings().setLoadWithOverviewMode(false);
        product_more_webview.getSettings().setAppCacheEnabled(true);
        product_more_webview.getSettings().setDatabaseEnabled(true);

        product_more_webview.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);// 先查找缓存，没有的情况下从网络获取。

        product_more_webview.getSettings().setAppCacheEnabled(true);
        product_more_webview.loadUrl(web_url);

    }

    // 监听客户端的点击
    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            Log.i("url", url);

            view.getSettings().setJavaScriptEnabled(true);

            //拿到图片对应的网址
        view.loadUrl("javascript:window.imagelistner.showSource('<body>'+" +
                "document.getElementsByTagName('html')[0].innerHTML+'</body>');");  //调用js里面的方法
            super.onPageFinished(view, url);
        addImageClickListner();

        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            view.getSettings().setJavaScriptEnabled(true);
            Log.i("url", url);
            view.getSettings().setJavaScriptEnabled(true);
            super.onPageStarted(view, url, favicon);
            // html加载完成之后，添加监听图片的点击js函数
            int w = View.MeasureSpec.makeMeasureSpec(0,
                    View.MeasureSpec.UNSPECIFIED);
            int h = View.MeasureSpec.makeMeasureSpec(0,
                    View.MeasureSpec.UNSPECIFIED);
            //重新测量
            product_more_webview.measure(w, h);
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            product_more_webview.loadUrl(web_url);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
        }
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (loadFinish) {
            product_more_webview.stopLoading();
            product_more_webview.removeAllViews();
            product_more_webview.destroy();
            product_more_webview = null;
        }

        if (this != null) {
            FragmentManager f = getFragmentManager();
            if (f != null && !f.isDestroyed()) {
                final FragmentTransaction ft = f.beginTransaction();
                if (ft != null) {
                    ft.remove(this).commit();
                }
            }
        }


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

        }

    }

    /**
     * 判断是否有wifi,得到对应的网址
     * @return
     */
    private String startWebViewRequest() {
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