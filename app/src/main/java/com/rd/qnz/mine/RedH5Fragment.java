package com.rd.qnz.mine;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.rd.qnz.R;
import com.rd.qnz.custom.APIModel;
import com.rd.qnz.custom.MyApplication;
import com.rd.qnz.custom.Profile;
import com.rd.qnz.tools.BaseParam;
import com.rd.qnz.xutils.JSKit3;
import com.rd.qnz.xutils.JSKit4;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;



/**
 * Created by Administrator on 2017/4/21 0021.
 */

public class RedH5Fragment extends Fragment {
    private Context context;
    private WebView redpackets_webView;
    private LinearLayout no_note;
    private ImageView no_note_img;
    private List<Map<String, String>> list;//红包list
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    public RedH5Fragment(Context context){
        this.context=context;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=View.inflate(context, R.layout.red_h5,null);

        redpackets_webView = (WebView)view.findViewById(R.id.my_redpackets_webView);
        no_note = (LinearLayout) view.findViewById(R.id.no_note);//无数据
        no_note_img = (ImageView) view.findViewById(R.id.no_note_img);
        initView();
        return view;
    }

    @JavascriptInterface
    private void initView() {
        JSKit4 js = new JSKit4(context);


        list = new ArrayList<Map<String, String>>();





        redpackets_webView.addJavascriptInterface(js, "myjs");

        redpackets_webView.getSettings().setDomStorageEnabled(true);

        // 设置可以支持缩放
        redpackets_webView.getSettings().setSupportZoom(true);
        WebSettings websetting = redpackets_webView.getSettings();
        websetting.setJavaScriptEnabled(true);
        websetting.setDefaultZoom(WebSettings.ZoomDensity.FAR);
        redpackets_webView.setWebChromeClient(new WebChromeClient());
        // 设置出现缩放工具
        redpackets_webView.getSettings().setBuiltInZoomControls(true);
        redpackets_webView.requestFocus();

        redpackets_webView.loadUrl(getRechargeBalanceResultUrl());

    }
    /**
     * 获取红包url
     */
    private String getRechargeBalanceResultUrl() {
        MyApplication myApp = MyApplication.getInstance();
        ArrayList<String> param = new ArrayList<String>();
        ArrayList<String> value = new ArrayList<String>();
        param.add(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN);
        value.add(Profile.getoAutoToken());
        param.add(BaseParam.URL_QIAN_API_APPID);
        value.add(myApp.appId);
        param.add(BaseParam.URL_QIAN_API_SERVICE);
        value.add("redPacketH5");
        param.add(BaseParam.URL_QIAN_API_SIGNTYPE);
        value.add(myApp.signType);
        param.add("new");
        value.add("1");
        String[] array = new String[]{
                BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN + "=" + Profile.getoAutoToken(),
                BaseParam.URL_QIAN_API_APPID + "=" + myApp.appId,
                BaseParam.URL_QIAN_API_SERVICE + "=" + "redPacketH5",
                BaseParam.URL_QIAN_API_SIGNTYPE + "=" + myApp.signType,
                "new=1"};
        APIModel apiModel = new APIModel();
        String sign = apiModel.sortStringArray(array);
        param.add(BaseParam.URL_QIAN_API_SIGN);
        value.add(sign);
        StringBuffer strBuffer = new StringBuffer();
        strBuffer.append(BaseParam.URL_QIAN_MY_H5_READPACKAGE);
        strBuffer.append("?");
        for (int i = 0; i < value.size(); i++) {
            strBuffer.append(param.get(i));
            strBuffer.append("=");
            strBuffer.append(value.get(i));
            if (i < value.size() - 1) {
                strBuffer.append("&");
            }
        }
        return strBuffer.toString().trim();
    }
}
