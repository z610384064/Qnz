package com.rd.qnz.mine;

import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rd.qnz.BaseActivity;
import com.rd.qnz.R;
import com.rd.qnz.WebViewAct;
import com.rd.qnz.custom.APIModel;
import com.rd.qnz.custom.MineShow;
import com.rd.qnz.custom.MyApplication;
import com.rd.qnz.custom.Profile;
import com.rd.qnz.tools.BaseParam;
import com.rd.qnz.xutils.JSKit3;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Evonne on 2016.11.22.
 * 我的红包及奖励
 */

public class RedpackageAndAwardList extends BaseActivity implements View.OnClickListener {

    private String oauthToken = "";
    private LinearLayout no_note;
    private ImageView no_note_img;
    private WebView redpackets_webView;
  //  private ListView my_redpackets_listview;
    private List<Map<String, String>> list;//红包list
   // private MyContentRedpacketAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.redpackage_award);
        initBar();
        initView();
      //  getData();
    }

    private void initBar() {
        ImageView actionbar_side_left_iconfont = (ImageView) findViewById(R.id.actionbar_side_left_iconfont);
        actionbar_side_left_iconfont.setVisibility(View.VISIBLE);
        actionbar_side_left_iconfont.setOnClickListener(this);
        TextView actionbar_side_name = (TextView) findViewById(R.id.actionbar_side_name);
        actionbar_side_name.setText("我的红包");
        LinearLayout linearLayout_use = (LinearLayout) findViewById(R.id.linearLayout_use); //使用规则
        linearLayout_use.setVisibility(View.VISIBLE);
        linearLayout_use.setOnClickListener(this);
    }

    @JavascriptInterface
    private void initView() {
        JSKit3 js = new JSKit3(this);
        redpackets_webView = (WebView) findViewById(R.id.my_redpackets_webView);

      //  my_redpackets_listview = (ListView) findViewById(R.id.my_redpackets_listview);
        list = new ArrayList<Map<String, String>>();

       // listAdapter = new MyContentRedpacketAdapter(this, list);

       // my_redpackets_listview.setAdapter(listAdapter);

        no_note = (LinearLayout) findViewById(R.id.no_note);//无数据
        no_note_img = (ImageView) findViewById(R.id.no_note_img);

        redpackets_webView.addJavascriptInterface(js, "myjs");

        redpackets_webView.getSettings().setDomStorageEnabled(true);

        // 设置可以支持缩放
        redpackets_webView.getSettings().setSupportZoom(true);
        WebSettings websetting = redpackets_webView.getSettings();
        websetting.setJavaScriptEnabled(true);
        websetting.setDefaultZoom(WebSettings.ZoomDensity.FAR);
        // 设置出现缩放工具
        redpackets_webView.getSettings().setBuiltInZoomControls(true);
        redpackets_webView.requestFocus();

        redpackets_webView.loadUrl(getRechargeBalanceResultUrl());

    }

    @Override
    public void onClick(View v) {
        int vId = v.getId();
        switch (vId) {
            case R.id.linearLayout_use:
                if (MineShow.fastClick()) {
                    WebViewAct.start(RedpackageAndAwardList.this, "使用规则", BaseParam.URL_QIAN_FORWARD);
                }
                break;
            case R.id.actionbar_side_left_iconfont:
                finish();
                break;
            default:
                break;
        }
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

