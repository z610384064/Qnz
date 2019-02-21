package com.rd.qnz.mine;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.rd.qnz.BaseActivity;
import com.rd.qnz.R;
import com.rd.qnz.custom.APIModel;
import com.rd.qnz.custom.MyApplication;
import com.rd.qnz.dialog.CustomProgressDialog;
import com.rd.qnz.dialog.GetDialog;
import com.rd.qnz.login.Login;
import com.rd.qnz.tools.BaseParam;
import com.rd.qnz.tools.Check;
import com.rd.qnz.tools.webservice.JsonRequestThreadGetAccountBalance;

import java.util.ArrayList;
import java.util.Map;

/**
 * 账户余额
 *
 * @author Evonne
 */
public class AccountBalanceActivity extends BaseActivity   {
    private TextView mTitle;
    private ImageView mBackBtn;

    private CustomProgressDialog mProgressDialog;
    private TextView cash_text;

    public static void start(Context context) {
        Intent i = new Intent(context, AccountBalanceActivity.class);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_balance);
        initView();
        getData();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i("账户余额", "onRestart");
        getData();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (null != mProgressDialog && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    private void initView() {
        mTitle = (TextView) findViewById(R.id.actionbar_side_name);
        mBackBtn = (ImageView) findViewById(R.id.actionbar_side_left_iconfont);
        mTitle.setVisibility(View.VISIBLE);
        mBackBtn.setVisibility(View.VISIBLE);

        mBackBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        cash_text = (TextView) findViewById(R.id.cash_text);
        mTitle.setText("账户余额");

    }

    private void getData() {
        if (Check.hasInternet(AccountBalanceActivity.this)) {
            MyApplication myApp = MyApplication.getInstance();
            SharedPreferences preferences = getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_USER, Context.MODE_PRIVATE);
            String oauthToken = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN, "");
            APIModel apiModel = new APIModel();
            ArrayList<String> param = new ArrayList<String>();
            ArrayList<String> value = new ArrayList<String>();
            param.add(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN);
            value.add(oauthToken);
            param.add(BaseParam.URL_QIAN_API_APPID);
            value.add(myApp.appId);
            param.add(BaseParam.URL_QIAN_API_SERVICE);
            value.add("accountBalance");
            param.add(BaseParam.URL_QIAN_API_SIGNTYPE);
            value.add(myApp.signType);
            String[] array = new String[]{
                    BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN + "=" + oauthToken,
                    BaseParam.URL_QIAN_API_APPID + "=" + myApp.appId,
                    BaseParam.URL_QIAN_API_SERVICE + "=accountBalance",
                    BaseParam.URL_QIAN_API_SIGNTYPE + "=" + myApp.signType};

            String sign = apiModel.sortStringArray(array);
            param.add(BaseParam.URL_QIAN_API_SIGN);
            value.add(sign);

            if (mProgressDialog == null) {
                GetDialog dia = new GetDialog();
                mProgressDialog = dia.getLoginDialog(AccountBalanceActivity.this, "正在获取数据..");
            }
            mProgressDialog.show();
            new Thread(new JsonRequestThreadGetAccountBalance(
                    AccountBalanceActivity.this,
                    myApp,
                    mHandler,
                    param,
                    value)
            ).start();
        } else {
            showToast("请检查网络连接是否正常");
        }
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            Bundle localBundle1 = msg.getData();
            ArrayList<Parcelable> product_R = localBundle1.getParcelableArrayList(BaseParam.URL_QIAN_ACCOUNT_BALANCE);
            if (null != mProgressDialog && mProgressDialog.isShowing() && !AccountBalanceActivity.this.isFinishing()) {// 隐藏加载框
                mProgressDialog.dismiss();
            }
            MyApplication myApp = MyApplication.getInstance();
            if (null != product_R) {
                Map<String, String> map = (Map<String, String>) product_R.get(0);
                String resultCode = map.get("resultCode");
                if (TextUtils.equals(resultCode, "1")) {

                    cash_text.setText("每月前"
                            + map.get(BaseParam.QIAN_ACCOUNT_BALANCE_KEY_FREECASHCOUNT)
                            + "笔提现免手续费，超过每笔收取"
                            + map.get(BaseParam.QIAN_ACCOUNT_BALANCE_KEY_CASHFEE)
                            + "元手续费，本月您已累计提现"
                            + map.get(BaseParam.QIAN_ACCOUNT_BALANCE_KEY_CURRENMONTHHASCASH)
                            + "笔");

                } else {
                    if (map.get("errorCode").equals("TOKEN_NOT_EXIST")) {
                        showToast("身份证验证不存在,请重新登录");
                        startActivity(new Intent(AccountBalanceActivity.this, Login.class));
                        myApp.tabHostId = 0;
                        myApp.tabHost.setCurrentTab(myApp.tabHostId);
                    } else if (map.get("errorCode").equals("TOKEN_EXPIRED")) {
                        startActivity(new Intent(AccountBalanceActivity.this, Login.class));
                        myApp.tabHostId = 0;
                        myApp.tabHost.setCurrentTab(myApp.tabHostId);
                    } else {
                        showToast(Check.checkReturn(map.get("errorCode")));
                    }
                }
            }
        }
    };

}
