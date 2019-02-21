package com.rd.qnz.mine;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.rd.qnz.BaseActivity;
import com.rd.qnz.R;
import com.rd.qnz.custom.APIModel;
import com.rd.qnz.custom.MineShow;
import com.rd.qnz.custom.MyApplication;
import com.rd.qnz.dialog.CustomProgressDialog;
import com.rd.qnz.dialog.GetDialog;
import com.rd.qnz.login.RetrievePhoneAct;
import com.rd.qnz.tools.BaseParam;
import com.rd.qnz.tools.Check;
import com.rd.qnz.tools.webservice.JsonRequeatThreadMyUpdatePassword;

import java.util.ArrayList;
import java.util.Map;

/**
 * 安全中心 修改密码
 * 管理登录密码/管理交易密码
 *
 * @author Evonne
 */
public class UpdatePasswordAct extends BaseActivity {

    private EditText update_ypassword;
    private EditText update_password;
    private EditText update_repassword;
    private TextView retrieve_password;
    private String ypassword;
    private String password;
    private String repassword;
    private Button update_password_btn;

    private ArrayList<String> param = null;
    private ArrayList<String> value = null;

    private GetDialog dia;
    private MyHandler myHandler;
    private CustomProgressDialog progressDialog = null;
    private MyApplication myApp;

    APIModel apiModel = new APIModel();

    private String title = ""; //标题
    private String upadte_ypwd = ""; //原密码
    private String upadte_pwd = ""; //新密码
    private String upadte_repwd = ""; //确认密码
    private String service = ""; //确认密码
    private String retrieve;//找回登陆密码


    private String login = "";//login =1  修改登录密码  =2 修改交易密码
    private String oauthToken = "";
    private String cardExist;
    private String phone;
    private String dataID = "qwertyuioplkjhgfdsazxcvbnmQWERTYUIOPLKJHGFDSAZXCVBNM1234567890";
    private class MyHandler extends Handler {
        public void handleMessage(Message paramMessage) {
            Bundle localBundle1 = paramMessage.getData();
            ArrayList<Parcelable> product_R = localBundle1
                    .getParcelableArrayList(BaseParam.URL_REQUEAT_MY_MODIFYPWD);
            if (null != progressDialog && progressDialog.isShowing()) {// 隐藏加载框
                UpdatePasswordAct.this.progressDialog.dismiss();
            }
            if (null != product_R) {
                Map<String, String> map = (Map<String, String>) product_R.get(0);

                String resultCode = map.get("resultCode");
                System.out.println("resultCode = " + resultCode);
                if (resultCode.equals("1")) {
                    showToast("修改成功");
                    if (login.equals("2")) {
                        Context ctx = UpdatePasswordAct.this;
                        SharedPreferences sp = ctx.getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_USER, MODE_PRIVATE);
                        //存入数据
                        Editor editor = sp.edit();
                        editor.putInt(BaseParam.QIAN_SHAREDPREFERENCES_USER_JYCOUNT, 0);
                        editor.putLong(BaseParam.QIAN_SHAREDPREFERENCES_USER_JYTIME, System.currentTimeMillis() / 1000);
                        editor.commit();
                    }
                    finish();
                } else {
                    showToast(Check.checkReturn(map.get("errorCode")));
                }
            }
            super.handleMessage(paramMessage);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myhome_management_safe_update_password);

        this.dia = new GetDialog();
        myApp = (MyApplication) getApplication();
        this.myHandler = new MyHandler();

        login = getIntent().getStringExtra("login");
        if (login.equals("1")) {    //是登录密码
            title = "修改登录密码"; //标题
            upadte_ypwd = "请输入原登录密码"; //原密码
            upadte_pwd = "请输入8~20位新登录密码"; //新密码
//            upadte_repwd = "请再次输入新的登录密码"; //确认密码
            service = "modifyLoginPwd";
            retrieve = "忘记密码？";
        } else {  //2代表修改交易密码
            title = "修改交易密码"; //标题
            upadte_ypwd = "请输入原交易密码"; //原密码
            upadte_pwd = "请输入8~20位新交易密码"; //新密码
            upadte_repwd = "请再次输入新的交易密码"; //确认密码
            service = "modifyPayPwd";
            retrieve = "忘记密码？";
        }
        SharedPreferences preferences = getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_USER, Context.MODE_PRIVATE);
        oauthToken = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN, "");
        cardExist = preferences.getString("cardExist", "");
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

        TextView actionbar_side_name = (TextView) findViewById(R.id.actionbar_side_name);
        actionbar_side_name.setText(title);
    }

    public void intView() {
        update_ypassword = (EditText) findViewById(R.id.update_ypassword);
        update_password = (EditText) findViewById(R.id.update_password);
        update_repassword = (EditText) findViewById(R.id.update_repassword);
        retrieve_password = (TextView) findViewById(R.id.retrieve_password);
        update_password_btn = (Button) findViewById(R.id.update_password_btn);
        retrieve_password.setText(retrieve);
        update_ypassword.setHint(upadte_ypwd);
        update_password.setHint(upadte_pwd);
        update_repassword.setHint(upadte_repwd);

        update_ypassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                ypassword = update_ypassword.getText().toString().trim();
                password = update_password.getText().toString().trim();
                repassword = update_repassword.getText().toString().trim();
                if (ypassword.equals("") || password.equals("") || repassword.equals("")) {
                    update_password_btn.setBackgroundResource(R.drawable.button_org_grauly);
                    update_password_btn.setClickable(false);
                } else {
                    update_password_btn.setBackgroundResource(R.drawable.button_org_big);
                    update_password_btn.setClickable(true);
                }
            }
        });
        update_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                ypassword = update_ypassword.getText().toString().trim();
                password = update_password.getText().toString().trim();
                repassword = update_repassword.getText().toString().trim();
                if (ypassword.equals("") || password.equals("") || repassword.equals("")) {
                    update_password_btn.setBackgroundResource(R.drawable.button_org_grauly);
                    update_password_btn.setClickable(false);
                } else {
                    update_password_btn.setBackgroundResource(R.drawable.button_org_big);
                    update_password_btn.setClickable(true);
                }
            }
        });
        update_repassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                ypassword = update_ypassword.getText().toString().trim();
                password = update_password.getText().toString().trim();
                repassword = update_repassword.getText().toString().trim();
                if (ypassword.equals("") || password.equals("") || repassword.equals("")) {
                    update_password_btn.setBackgroundResource(R.drawable.button_org_grauly);
                    update_password_btn.setClickable(false);
                } else {
                    update_password_btn.setBackgroundResource(R.drawable.button_org_big);
                    update_password_btn.setClickable(true);
                }
            }
        });


        update_password_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                ypassword = update_ypassword.getText().toString().trim();
                password = update_password.getText().toString().trim();
                repassword = update_repassword.getText().toString().trim();

                if (password.equals("") || password == null) {
                    showToast("请填写新密码");
//					update_password.setError("请填写新密码");
                    return;
                }
                if (!repassword.equals(password)) {
                    update_repassword.setError("两次密码不同");
                    return;
                }

                startDataRequest();

            }
        });
        retrieve_password.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (login.equals("1")) {
                    //找回登陆密码
                    if (MineShow.fastClick()) {
                        finish();
                        Intent intent = new Intent(UpdatePasswordAct.this, RetrievePhoneAct.class);
                        intent.putExtra("cardExist", cardExist);
                        intent.putExtra("phone", phone);
                        startActivity(intent);
                    }
                } else {
                    //找回交易密码
                    if (MineShow.fastClick()) {
                        finish();
                        Intent forget_pay_password = new Intent(UpdatePasswordAct.this, ForgetPasswordAct.class);
                        startActivity(forget_pay_password);
                    }
                }
            }
        });


        update_password.setKeyListener(new DigitsKeyListener() {
            @Override
            public int getInputType() {

                return InputType.TYPE_TEXT_VARIATION_PASSWORD;
            }

            @Override
            protected char[] getAcceptedChars() {
                //char[] data = getStringData(R.string.login_only_can_input).toCharArray();
                char[] data =dataID.toCharArray();
                return data;

            }

        });
        update_repassword.setKeyListener(new DigitsKeyListener() {
            @Override
            public int getInputType() {

                return InputType.TYPE_TEXT_VARIATION_PASSWORD;
            }

            @Override
            protected char[] getAcceptedChars() {
                //char[] data = getStringData(R.string.login_only_can_input).toCharArray();
                char[] data =dataID.toCharArray();
                return data;

            }

        });

    }
    /*
    点击确认,进行密码的修改
     */
    private void startDataRequest() {

        if (Check.hasInternet(UpdatePasswordAct.this)) {
            UpdatePasswordAct.this.initArray();
            UpdatePasswordAct.this.param.add(BaseParam.QIAN_MY_OLDPASSWORD);
            UpdatePasswordAct.this.value.add(ypassword);
            UpdatePasswordAct.this.param.add(BaseParam.QIAN_MY_NEWPASSWORD);
            UpdatePasswordAct.this.value.add(password);
            UpdatePasswordAct.this.param.add(BaseParam.QIAN_MY_CONFIRMPASSWORD);
            UpdatePasswordAct.this.value.add(repassword);
            UpdatePasswordAct.this.param.add(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN);
            UpdatePasswordAct.this.value.add(oauthToken);

            UpdatePasswordAct.this.param.add(BaseParam.URL_QIAN_API_APPID);
            UpdatePasswordAct.this.value.add(myApp.appId);
            UpdatePasswordAct.this.param.add(BaseParam.URL_QIAN_API_SERVICE);
            UpdatePasswordAct.this.value.add(service);
            UpdatePasswordAct.this.param.add(BaseParam.URL_QIAN_API_SIGNTYPE);
            UpdatePasswordAct.this.value.add(myApp.signType);

            String[] array = new String[]{BaseParam.QIAN_MY_OLDPASSWORD + "=" + ypassword,
                    BaseParam.QIAN_MY_NEWPASSWORD + "=" + password,
                    BaseParam.QIAN_MY_CONFIRMPASSWORD + "=" + repassword,
                    BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN + "=" + oauthToken,
                    BaseParam.URL_QIAN_API_APPID + "=" + myApp.appId,
                    BaseParam.URL_QIAN_API_SERVICE + "=" + service,
                    BaseParam.URL_QIAN_API_SIGNTYPE + "=" + myApp.signType};

            String sign = apiModel.sortStringArray(array);
            UpdatePasswordAct.this.param.add(BaseParam.URL_QIAN_API_SIGN);
            UpdatePasswordAct.this.value.add(sign);
            UpdatePasswordAct.this.progressDialog = UpdatePasswordAct.this.dia.getLoginDialog(UpdatePasswordAct.this, "正在获取数据..");
            UpdatePasswordAct.this.progressDialog.show();
            new Thread(new JsonRequeatThreadMyUpdatePassword(
                    UpdatePasswordAct.this,
                    myApp,
                    UpdatePasswordAct.this.myHandler,
                    UpdatePasswordAct.this.param,
                    UpdatePasswordAct.this.value,
                    login)
            ).start();
        } else {
            showToast("请检查网络连接是否正常");
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
