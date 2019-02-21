package com.rd.qnz.product.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.rd.qnz.R;
import com.rd.qnz.tools.BaseParam;
import com.rd.qnz.tools.CustomViewpager;
import com.rd.qnz.util.MyViewPager;
import com.rd.qnz.xutils.JSKit4;

import static com.rd.qnz.R.id.product_more_webview;
import static com.rd.qnz.R.id.webview;

/**
 * Created by Administrator on 2017/5/15 0015.
 */

public class SaveFragmentForProduct extends Fragment {
    private Context context;
    private WebView webView;
    private CustomViewpager viewpager;
    private boolean loadFinish = false;
    public SaveFragmentForProduct(){};
    public SaveFragmentForProduct(Context context, CustomViewpager viewpager){
        this.context=context;
        this.viewpager=viewpager;
    }
    public SaveFragmentForProduct(Context context ){
        this.context=context;
    }
    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        View view=View.inflate(context, R.layout.fragment_save,null);
        webView= (WebView) view.findViewById(webview);
        initView();

        return view;
    }
    @JavascriptInterface
    private void initView() {
        JSKit4 js = new JSKit4(context);

        webView.addJavascriptInterface(js, "myjs");

        webView.getSettings().setDomStorageEnabled(true);

        // 设置可以支持缩放
        webView.getSettings().setSupportZoom(true);
        WebSettings websetting = webView.getSettings();
        websetting.setDefaultZoom(WebSettings.ZoomDensity.FAR);
        // 设置出现缩放工具
        webView.setWebChromeClient(new WebChromeClient());
        webView.getSettings().setBuiltInZoomControls(true);
        webView.requestFocus();

        webView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setVerticalScrollBarEnabled(false);
        webView.setClickable(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        webView.getSettings().setSaveFormData(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setLoadWithOverviewMode(false);
        webView.getSettings().setSavePassword(true);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setDatabaseEnabled(true);
        webView.setWebViewClient(new MyWebViewClient());

        webView.loadUrl(BaseParam.URL_QIAN_HOME_SECURITY);

    }
    class MyWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            loadFinish = true;
//            viewpager.setObjectForPosition(view,1);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            view.getSettings().setJavaScriptEnabled(true);
            super.onPageStarted(view, url, favicon);
            // html加载完成之后，添加监听图片的点击js函数
            int w = View.MeasureSpec.makeMeasureSpec(0,
                    View.MeasureSpec.UNSPECIFIED);
            int h = View.MeasureSpec.makeMeasureSpec(0,
                    View.MeasureSpec.UNSPECIFIED);
            //重新测量
            webView.measure(w, h);
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (loadFinish) {
            webView.stopLoading();
            webView.removeAllViews();
            webView.destroy();
            webView = null;
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
}
