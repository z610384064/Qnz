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
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
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
 * 管理登录密码
 *
 * @author Evonne
 */
public class UpdateLoginPasswordAct extends BaseActivity {

    private EditText update_ypassword;
    private EditText update_password;
    private TextView retrieve_password;
    private String ypassword;
    private String password;
    private String repassword;
    private Button update_password_btn;
    private ImageView reg_light_icon,reg_light_icon1; //带眼睛的图片
    private boolean qrpass_isHidden = false;  //新密码默认是显示的

    private boolean ypass_isHidden = false;  //原登录密码默认是显示的

    private ArrayList<String> param;
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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myhome_management_safe_update_login_password);

        this.dia = new GetDialog();
        myApp = (MyApplication) getApplication();
        this.myHandler = new MyHandler();

            login ="1";

            upadte_pwd = "请输入8~20位新登录密码"; //新密码
            service = "modifyLoginPwd";
            retrieve = "忘记密码？";

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
        actionbar_side_name.setText("修改登录密码");
    }

    public void intView() {
        update_ypassword = (EditText) findViewById(R.id.update_ypassword); //请输入原登录密码
        update_password = (EditText) findViewById(R.id.update_password);  //请输入新登录密码
        retrieve_password = (TextView) findViewById(R.id.retrieve_password); //找回密码
        retrieve_password.setVisibility(View.GONE); //在登录界面,管理登录密码 不显示找回密码
        update_password_btn = (Button) findViewById(R.id.update_password_btn); //确认
        reg_light_icon = (ImageView) findViewById(R.id.reg_light_icon); //一个带眼睛的图片,用来判断确认密码是否需要隐藏
        reg_light_icon1= (ImageView) findViewById(R.id.reg_light_icon1);//原密码眼睛图片
        reg_light_icon1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ypass_isHidden) {
                    //show the password   显示密码
                    update_ypassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
//                    reg_light_icon.setImageResource(R.drawable.eye_default_btn1);
                    reg_light_icon1.setBackgroundResource(R.drawable.eye_default_btn1);

                } else {
                    //hide the password   隐藏密码
                    update_ypassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
//                    reg_light_icon.setImageResource(R.drawable.eye_selected_btn1);
                    reg_light_icon1.setBackgroundResource(R.drawable.eye_selected_btn1);
                }
                ypass_isHidden = !ypass_isHidden;
                update_ypassword.setSelection(update_ypassword.getText().length());
            }
        });



        //一个眼睛的图片
        reg_light_icon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (qrpass_isHidden) {
                    //show the password   显示密码
                    update_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
//                    reg_light_icon.setImageResource(R.drawable.eye_default_btn1);
                    reg_light_icon.setBackgroundResource(R.drawable.eye_default_btn1);

                } else {
                    //hide the password   隐藏密码
                    update_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
//                    reg_light_icon.setImageResource(R.drawable.eye_selected_btn1);
                    reg_light_icon.setBackgroundResource(R.drawable.eye_selected_btn1);
                }
                qrpass_isHidden = !qrpass_isHidden;
                update_password.setSelection(update_password.getText().length());
            }
        });




        retrieve_password.setText(retrieve);

        update_ypassword.addTextChangedListener(new TextWatcher() {  //监听原密码
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

                if (ypassword.equals("") || password.equals("")) {
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
                if (ypassword.equals("") || password.equals("")) {
                    update_password_btn.setBackgroundResource(R.drawable.button_org_grauly);
                    update_password_btn.setClickable(false);
                } else {
                    update_password_btn.setBackgroundResource(R.drawable.button_org_big);
                    update_password_btn.setClickable(true);
                }
            }
        });



        update_password_btn.setOnClickListener(new OnClickListener() {   //确认按钮
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                ypassword = update_ypassword.getText().toString().trim();
                password = update_password.getText().toString().trim();
                repassword =update_password.getText().toString().trim();

                if (password.equals("") || password == null) {
                    showToast("请填写新密码");
//					update_password.setError("请填写新密码");
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
                        Intent intent = new Intent(UpdateLoginPasswordAct.this, RetrievePhoneAct.class);
                        intent.putExtra("cardExist", cardExist);
                        intent.putExtra("phone", phone);
                        startActivity(intent);
                    }
                } else {
                    //找回交易密码
                    if (MineShow.fastClick()) {
                        finish();
                        Intent forget_pay_password = new Intent(UpdateLoginPasswordAct.this, ForgetPasswordAct.class);
                        startActivity(forget_pay_password);
                    }
                }
            }
        });

    }
    private class MyHandler extends Handler {
        public void handleMessage(Message paramMessage) {
            Bundle localBundle1 = paramMessage.getData();
            ArrayList<Parcelable> product_R = localBundle1
                    .getParcelableArrayList(BaseParam.URL_REQUEAT_MY_MODIFYPWD);
            if (null != progressDialog && progressDialog.isShowing()) {// 隐藏加载框
                UpdateLoginPasswordAct.this.progressDialog.dismiss();
            }
            if (null != product_R) {
                Map<String, String> map = (Map<String, String>) product_R.get(0);

                String resultCode = map.get("resultCode");
                System.out.println("resultCode = " + resultCode);
                if (resultCode.equals("1")) {
                    showToast("修改成功");
                    if (login.equals("2")) {
                        Context ctx = UpdateLoginPasswordAct.this;
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

    /*
    点击确认,进行密码的修改
     */
    private void startDataRequest() {

        if (Check.hasInternet(UpdateLoginPasswordAct.this)) {
            UpdateLoginPasswordAct.this.initArray();
            UpdateLoginPasswordAct.this.param.add(BaseParam.QIAN_MY_OLDPASSWORD);
            UpdateLoginPasswordAct.this.value.add(ypassword);
            UpdateLoginPasswordAct.this.param.add(BaseParam.QIAN_MY_NEWPASSWORD);
            UpdateLoginPasswordAct.this.value.add(password);
            UpdateLoginPasswordAct.this.param.add(BaseParam.QIAN_MY_CONFIRMPASSWORD);
            UpdateLoginPasswordAct.this.value.add(repassword);
            UpdateLoginPasswordAct.this.param.add(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN);
            UpdateLoginPasswordAct.this.value.add(oauthToken);

            UpdateLoginPasswordAct.this.param.add(BaseParam.URL_QIAN_API_APPID);
            UpdateLoginPasswordAct.this.value.add(myApp.appId);
            UpdateLoginPasswordAct.this.param.add(BaseParam.URL_QIAN_API_SERVICE);
            UpdateLoginPasswordAct.this.value.add(service);
            UpdateLoginPasswordAct.this.param.add(BaseParam.URL_QIAN_API_SIGNTYPE);
            UpdateLoginPasswordAct.this.value.add(myApp.signType);

            String[] array = new String[]{BaseParam.QIAN_MY_OLDPASSWORD + "=" + ypassword,
                    BaseParam.QIAN_MY_NEWPASSWORD + "=" + password,
                    BaseParam.QIAN_MY_CONFIRMPASSWORD + "=" + repassword,
                    BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN + "=" + oauthToken,
                    BaseParam.URL_QIAN_API_APPID + "=" + myApp.appId,
                    BaseParam.URL_QIAN_API_SERVICE + "=" + service,
                    BaseParam.URL_QIAN_API_SIGNTYPE + "=" + myApp.signType};

            String sign = apiModel.sortStringArray(array);
            UpdateLoginPasswordAct.this.param.add(BaseParam.URL_QIAN_API_SIGN);
            UpdateLoginPasswordAct.this.value.add(sign);
            UpdateLoginPasswordAct.this.progressDialog = UpdateLoginPasswordAct.this.dia.getLoginDialog(UpdateLoginPasswordAct.this, "正在获取数据..");
            UpdateLoginPasswordAct.this.progressDialog.show();
            new Thread(new JsonRequeatThreadMyUpdatePassword(
                    UpdateLoginPasswordAct.this,
                    myApp,
                    UpdateLoginPasswordAct.this.myHandler,
                    UpdateLoginPasswordAct.this.param,
                    UpdateLoginPasswordAct.this.value,
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
