package com.rd.qnz.mine;

import java.util.ArrayList;
import java.util.Map;

import com.rd.qnz.BaseActivity;
import com.rd.qnz.R;
import com.rd.qnz.custom.APIModel;
import com.rd.qnz.custom.MyApplication;
import com.rd.qnz.dialog.CustomProgressDialog;
import com.rd.qnz.dialog.GetDialog;
import com.rd.qnz.tools.AppTool;
import com.rd.qnz.tools.BaseParam;
import com.rd.qnz.tools.Check;
import com.rd.qnz.tools.webservice.JsonRequeatThreadForgetPassword;
import com.rd.qnz.tools.webservice.JsonRequeatThreadForgetVerify;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 找回交易密码
 *
 * @author Evonne
 */
public class ForgetPasswordAct extends BaseActivity {

    private GetDialog dia;
    private MyHandler myHandler;
    private CustomProgressDialog progressDialog = null;
    private MyApplication myApp;

    private ArrayList<String> param = null;
    private ArrayList<String> value = null;

    private TextView actionbar_side_name;


    private EditText forget_card;
    private EditText forget_verify;
    private EditText forget_jypassword;

    private TextView forget_verify_btn;
    private Button forget_secces_btn;

    private TextView forget_phone;

    private String phone;
    APIModel apiModel = new APIModel();
    private String oauthToken;

    private String card = "";
    private String verify = "";
    private String jypassword = "";

    private class MyHandler extends Handler {
        public void handleMessage(Message paramMessage) {
            Bundle localBundle1 = paramMessage.getData();
            ArrayList<Parcelable> verify_list = localBundle1.getParcelableArrayList(BaseParam.QIAN_REQUEAT_RETURN_FORGETVERIFY);
            ArrayList<Parcelable> commit_list = localBundle1.getParcelableArrayList(BaseParam.QIAN_REQUEAT_RETURN_FORGETPASSWORD);

            if (null != progressDialog && progressDialog.isShowing()) {// 隐藏加载框
                ForgetPasswordAct.this.progressDialog.dismiss();
            }
            if (null != verify_list) {
                Map<String, String> map = (Map<String, String>) verify_list.get(0);

                String resultCode = map.get("resultCode");
                // 获取验证码
                if (resultCode.equals("1")) {
                    // 服务器已经发送验证码
                    MyCount mc = new MyCount(60000, 1000);
                    mc.start();
                } else {
                    showToast(Check.checkReturn(map.get("errorCode")));
                }
            }
            if (null != commit_list) {
                Map<String, String> map = (Map<String, String>) commit_list.get(0);
                String resultCode = map.get("resultCode");
                if (resultCode.equals("1")) {
                    AppTool.getUserStatusInfoRequest();
                    showToast("修改成功");
                    finish();
                } else {

                    Check.checkMsg(map, ForgetPasswordAct.this);
                }
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forget_password);
        this.dia = new GetDialog();
        myApp = (MyApplication) getApplication();
        this.myHandler = new MyHandler();
        SharedPreferences preferences = getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_USER, Context.MODE_PRIVATE);
        oauthToken = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN, "");
        phone = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_PHONE, "");
        initBar();
        intView();
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
        actionbar_side_name = (TextView) findViewById(R.id.actionbar_side_name);
        actionbar_side_name.setText("找回交易密码");
    }

    public void intView() {

        forget_card = (EditText) findViewById(R.id.forget_card);
        forget_verify = (EditText) findViewById(R.id.forget_verify);
        forget_jypassword = (EditText) findViewById(R.id.forget_jypassword);

        forget_verify_btn = (TextView) findViewById(R.id.forget_verify_btn);
        forget_secces_btn = (Button) findViewById(R.id.forget_secces_btn);

        forget_phone = (TextView) findViewById(R.id.forget_phone);

        forget_phone.setText("你绑定的手机号码:" + replaceChar(phone));
        // 发送验证码
        forget_verify_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                startDataRequestVerify();
            }
        });

        forget_secces_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                if (card.equals("") || verify.equals("") || jypassword.equals("")) {
                    return;
                }
                if (jypassword.length() < 8) {
                    showToast("密码请满足8-20个字符,使用数字与字母");
                    return;
                }
                startDataRequest();
            }
        });

        handleWatcher();
    }

    private static String replaceChar(String temp) {
        if (temp != null) {
            return temp.replace(temp.substring(3, 7), "****");
        }
        return null;
    }

    private void handleWatcher() {
        forget_card.addTextChangedListener(watcher);
        forget_verify.addTextChangedListener(watcher);
        forget_jypassword.addTextChangedListener(watcher);
    }

    private TextWatcher watcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
            card = forget_card.getText().toString().trim();
            verify = forget_verify.getText().toString().trim();
            jypassword = forget_jypassword.getText().toString().trim();

            if (card.equals("") || verify.equals("") || jypassword.equals("")) {
                forget_secces_btn.setBackgroundResource(R.drawable.button_org_grauly);
            } else {
                forget_secces_btn.setBackgroundResource(R.drawable.button_org_big);

            }
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
        }

        @Override
        public void afterTextChanged(Editable arg0) {
            // TODO Auto-generated method stub
        }
    };


    private void startDataRequest() {
        ForgetPasswordAct.this.initArray();
        ForgetPasswordAct.this.param.add(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN);
        ForgetPasswordAct.this.value.add(oauthToken);

        ForgetPasswordAct.this.param.add("hiddenCardId");
        ForgetPasswordAct.this.value.add(card);
        ForgetPasswordAct.this.param.add("validCode");
        ForgetPasswordAct.this.value.add(verify);
        ForgetPasswordAct.this.param.add("payPwd");
        ForgetPasswordAct.this.value.add(jypassword);

        ForgetPasswordAct.this.param.add(BaseParam.URL_QIAN_API_APPID);
        ForgetPasswordAct.this.value.add(myApp.appId);
        ForgetPasswordAct.this.param.add(BaseParam.URL_QIAN_API_SERVICE);
        ForgetPasswordAct.this.value.add("findPayPwd");
        ForgetPasswordAct.this.param.add(BaseParam.URL_QIAN_API_SIGNTYPE);
        ForgetPasswordAct.this.value.add(myApp.signType);

        String[] array = new String[]{
                BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN + "=" + oauthToken,
                "hiddenCardId=" + card,
                "validCode=" + verify,
                "payPwd=" + jypassword,
                BaseParam.URL_QIAN_API_APPID + "=" + myApp.appId,
                BaseParam.URL_QIAN_API_SERVICE + "=findPayPwd",
                BaseParam.URL_QIAN_API_SIGNTYPE + "=" + myApp.signType};

        ForgetPasswordAct.this.param.add(BaseParam.URL_QIAN_API_SIGN);
        ForgetPasswordAct.this.value.add(apiModel.sortStringArray(array));
        ForgetPasswordAct.this.progressDialog = ForgetPasswordAct.this.dia.getLoginDialog(ForgetPasswordAct.this, "正在验证...");
        ForgetPasswordAct.this.progressDialog.show();
        new Thread(new JsonRequeatThreadForgetPassword(
                ForgetPasswordAct.this,
                myApp,
                ForgetPasswordAct.this.myHandler,
                ForgetPasswordAct.this.param,
                ForgetPasswordAct.this.value)
        ).start();
    }

    private void startDataRequestVerify() {
        if (Check.hasInternet(ForgetPasswordAct.this)) {

            ForgetPasswordAct.this.initArray();
            ForgetPasswordAct.this.param.add(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN);
            ForgetPasswordAct.this.value.add(oauthToken);
            ForgetPasswordAct.this.param.add(BaseParam.URL_QIAN_API_APPID);
            ForgetPasswordAct.this.value.add(myApp.appId);
            ForgetPasswordAct.this.param.add(BaseParam.URL_QIAN_API_SERVICE);
            ForgetPasswordAct.this.value.add("findPayPwdPhoneCode");
            ForgetPasswordAct.this.param.add(BaseParam.URL_QIAN_API_SIGNTYPE);
            ForgetPasswordAct.this.value.add(myApp.signType);
            String[] array = new String[]{
                    BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN + "=" + oauthToken,
                    BaseParam.URL_QIAN_API_APPID + "=" + myApp.appId,
                    BaseParam.URL_QIAN_API_SERVICE + "=findPayPwdPhoneCode",
                    BaseParam.URL_QIAN_API_SIGNTYPE + "=" + myApp.signType};

            ForgetPasswordAct.this.param.add(BaseParam.URL_QIAN_API_SIGN);
            ForgetPasswordAct.this.value.add(apiModel.sortStringArray(array));
            ForgetPasswordAct.this.progressDialog = ForgetPasswordAct.this.dia.getLoginDialog(ForgetPasswordAct.this, "正在获取验证码..");

            ForgetPasswordAct.this.progressDialog.show();
            new Thread(new JsonRequeatThreadForgetVerify(
                    ForgetPasswordAct.this,
                    myApp,
                    ForgetPasswordAct.this.myHandler,
                    ForgetPasswordAct.this.param,
                    ForgetPasswordAct.this.value)
            ).start();
        } else {
            showToast("请检查网络连接是否正常");
        }
    }

    /* 定义一个倒计时的内部类 */
    class MyCount extends CountDownTimer {
        public MyCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            forget_verify_btn.setText(R.string.reg_btn_submit);
            forget_verify_btn.setClickable(true);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            forget_verify_btn.setText(millisUntilFinished / 1000 + "秒后重新获取");
            forget_verify_btn.setClickable(false);
        }
    }

    private void initArray() {
        this.param = null;
        this.value = null;
        this.param = new ArrayList();
        this.value = new ArrayList();
        System.gc();
    }
}
