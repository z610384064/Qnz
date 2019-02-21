package com.rd.qnz.mine;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;

import com.rd.qnz.BaseActivity;
import com.rd.qnz.R;
import com.rd.qnz.WebViewAct;
import com.rd.qnz.custom.APIModel;
import com.rd.qnz.custom.MyApplication;
import com.rd.qnz.tools.BaseParam;

/**
 * 设置单银行卡
 *
 * @author Evonne
 */
public class SettingOneBankcardActivity extends BaseActivity {
    private String oauthToken;

    public static void start(Context context) {
        Intent i = new Intent(context, SettingOneBankcardActivity.class);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO 自动生成的方法存根
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_to_bankcard);
        SharedPreferences preferences = getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_USER, Context.MODE_PRIVATE);
        oauthToken = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN, "");
        findViewById(R.id.setting_one_bankcard).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO 去设置单卡
                WebViewAct.start(SettingOneBankcardActivity.this, "设置银行卡", getToSetOneBankCardUrl());
                finish();
            }
        });
    }

    /**
     * 获取设置单张银行卡的h5
     *
     * @return
     */
    private String getToSetOneBankCardUrl() {
        MyApplication myApp = MyApplication.getInstance();
        ArrayList<String> param = new ArrayList<String>();
        ArrayList<String> value = new ArrayList<String>();
        param.add(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN);
        value.add(oauthToken);
        param.add(BaseParam.URL_QIAN_API_APPID);
        value.add(myApp.appId);
        param.add(BaseParam.URL_QIAN_API_SERVICE);
        value.add("setSingleBankCard");
        param.add(BaseParam.URL_QIAN_API_SIGNTYPE);
        value.add(myApp.signType);
        String[] array = new String[]{
                BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN + "=" + oauthToken,
                BaseParam.URL_QIAN_API_APPID + "=" + myApp.appId,
                BaseParam.URL_QIAN_API_SERVICE + "=" + "setSingleBankCard",
                BaseParam.URL_QIAN_API_SIGNTYPE + "=" + myApp.signType};
        APIModel apiModel = new APIModel();
        String sign = apiModel.sortStringArray(array);
        param.add(BaseParam.URL_QIAN_API_SIGN);
        value.add(sign);
        StringBuffer strBuffer = new StringBuffer();
        strBuffer.append(BaseParam.URL_QIAN_SETTING_ONE_BANK_CARD);
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

}
