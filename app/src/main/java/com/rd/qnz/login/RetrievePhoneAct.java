package com.rd.qnz.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.rd.qnz.BaseActivity;
import com.rd.qnz.R;
import com.rd.qnz.custom.APIModel;
import com.rd.qnz.custom.MineShow;
import com.rd.qnz.custom.MyApplication;
import com.rd.qnz.custom.Profile;
import com.rd.qnz.dialog.CustomProgressDialog;
import com.rd.qnz.dialog.GetDialog;
import com.rd.qnz.tools.AppTool;
import com.rd.qnz.tools.BaseParam;
import com.rd.qnz.tools.Check;
import com.rd.qnz.tools.webservice.JsonRequeatThreadLogin;
import com.rd.qnz.tools.webservice.JsonRequeatThreadReseting;
import com.rd.qnz.tools.webservice.JsonRequeatThreadRetrieveGai;
import com.rd.qnz.tools.webservice.JsonRequeatThreadReturnVerify;

import java.util.ArrayList;
import java.util.HashMap;

import static android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD;
import static com.rd.qnz.custom.MyApplication.appId;

/**
 * 找回密码
 *
 * @author Evonne
 */
public class RetrievePhoneAct extends BaseActivity {


    private TextView retrieve_phone_mobile;// 手机号码
    private EditText retrieve_phone_verify;// 手机验证码
    private TextView retrieve_phone_verify_btn;// 验证码
    private Button retrieve_phone_secces_btn;// 提交
    private ArrayList<String> param = null;
    private ArrayList<String> value = null;
    private GetDialog dia;
    private MyHandler myHandler;
    private CustomProgressDialog progressDialog = null;
    private MyApplication myApp;
    private TextView actionbar_side_name;
    private String phone = "";  //手机号

    private String phoneCode = "";  //验证码
    private String password = "";  //密码
    private String repassword = "";  //确认密码
    private String userId = "";
    APIModel apiModel = new APIModel();
    private EditText retrieve_phone_password;
    private String cardExist;

    private ImageView reg_light_icon1; //带眼睛的图片
    private boolean ypass_isHidden = false;  //原登录密码默认是显示的
    private String dataID = "qwertyuioplkjhgfdsazxcvbnmQWERTYUIOPLKJHGFDSAZXCVBNM1234567890";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.retrieve_phone);
        this.dia = new GetDialog();
        myApp = (MyApplication) getApplication();
        this.myHandler = new MyHandler();
        Intent intent = getIntent();
        cardExist = intent.getStringExtra("cardExist");
        phone = intent.getStringExtra("phone");
        initBar();
        intView();
    }

    private void initBar() {
        ImageView actionbar_side_left_iconfont = (ImageView) findViewById(R.id.actionbar_side_left_iconfont);
        actionbar_side_left_iconfont.setVisibility(View.VISIBLE);
        actionbar_side_left_iconfont.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                myApp.isReg = true;
                finish();
            }
        });
        actionbar_side_name = (TextView) findViewById(R.id.actionbar_side_name);
        actionbar_side_name.setText("找回密码");
    }

    public void intView() {

        retrieve_phone_password = (EditText) findViewById(R.id.retrieve_phone_password);  //请输入登录密码

        retrieve_phone_password.setKeyListener(new DigitsKeyListener() {
            @Override
            public int getInputType() {

                return InputType.TYPE_CLASS_TEXT|TYPE_TEXT_VARIATION_PASSWORD;
            }

            @Override
            protected char[] getAcceptedChars() {
                char[] data =dataID.toCharArray();
                return data;

            }

        });
//        retrieve_phone_password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD|TYPE_TEXT_VARIATION_PASSWORD);
        // 手机号码
        retrieve_phone_mobile = (TextView) findViewById(R.id.retrieve_phone_mobile);
        retrieve_phone_mobile.setText(phone.substring(0, 3) + "****" + phone.substring(7, 11)+",请注意查收");




        retrieve_phone_verify = (EditText) findViewById(R.id.retrieve_phone_verify);  //验证吗

        // 获取验证码
        retrieve_phone_verify_btn = (TextView) findViewById(R.id.retrieve_phone_verify_btn);

        retrieve_phone_verify.setFocusable(true); //手机校验通过,显示输入密码文本框,并弹出键盘
        retrieve_phone_verify.setFocusableInTouchMode(true);
        retrieve_phone_verify.requestFocus();
        retrieve_phone_verify.findFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        retrieve_phone_verify_btn.setOnClickListener(new OnClickListener() {   //点击获取验证码
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                startDataRequestVerify();
            }
        });

        reg_light_icon1= (ImageView) findViewById(R.id.reg_light_icon1);//原密码眼睛图片
        reg_light_icon1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ypass_isHidden) {
                    //show the password   显示密码
                    retrieve_phone_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    reg_light_icon1.setBackgroundResource(R.drawable.eye_default_btn1);

                } else {
                    //hide the password   隐藏密码
                    retrieve_phone_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    reg_light_icon1.setBackgroundResource(R.drawable.eye_selected_btn1);
                }
                ypass_isHidden = !ypass_isHidden;
                retrieve_phone_password.setSelection(retrieve_phone_password.getText().length());
            }
        });


        // 完成按钮
        retrieve_phone_secces_btn = (Button) findViewById(R.id.retrieve_phone_secces_btn);
        retrieve_phone_secces_btn.setBackgroundResource(R.drawable.button_org_grauly);
        retrieve_phone_secces_btn.setOnClickListener(new OnClickListener() {  //点击完成
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                phoneCode = retrieve_phone_verify.getText().toString().trim();  //手机号
                password = retrieve_phone_password.getText().toString().trim();
                repassword=password;

                    if (phoneCode.equals("")) {
                        return;
                    }


                if (password.equals("")) {
                    showToast("密码不能为空");
                    return;
                }

                startDataRequestPhone();
            }
        });
        handleWatcher();
        startDataRequestVerify(); //进入这个界面 自动去获取验证码
    }
    private class MyHandler extends Handler {
        public void handleMessage(Message paramMessage) {
            Bundle localBundle1 = paramMessage.getData();
            ArrayList<Parcelable> pm_list = localBundle1.getParcelableArrayList(BaseParam.QIAN_REQUEAT_RETRIEVE_PHONE_MIBAO);//第二部校验,身份证,姓名
            ArrayList<Parcelable> regVerify_list = localBundle1.getParcelableArrayList(BaseParam.QIAN_REQUEAT_RETURN_VERIFY);//第一步校验,验证码
            ArrayList<Parcelable> reset_list = localBundle1.getParcelableArrayList(BaseParam.QIAN_REQUEAT_RESETING);  //第三部重置密码返回的数据
            ArrayList<Parcelable> login_map = localBundle1.getParcelableArrayList(BaseParam.QIAN_REQUEAT_LOGIN); //登录接口返回的数据
            if (null != regVerify_list) {
                HashMap<String, String> map = (HashMap<String, String>) regVerify_list.get(0);
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
            if (null != pm_list) {
                HashMap<String, String> retrieve = (HashMap<String, String>) pm_list.get(0);
                String resultCode = retrieve.get("resultCode");
                if (resultCode.equals("1")) {
                    userId = retrieve.get("userId");
                    startDataRequestResetting(password, repassword); //从第二个请求的数据中拿到用户id,发送请求到第三个接口去重置密码
                } else {
                    showToast(Check.checkReturn(retrieve.get("errorCode")));
                }
            }
            if (null != reset_list) {
                HashMap<String, String> map = (HashMap<String, String>) reset_list.get(0);
                String resultCode = map.get("resultCode");
                // 获取验证码
                if (resultCode.equals("1")) {
                    // 服务器已经发送验证码
                    showToast("修改成功");
                    myApp.isReg = true;
                    Intent i=new Intent("forgetPassword");
                    i.putExtra("password",password);
                    sendBroadcast(i);  //发个广播到登录界面,把登录界面关闭了,直接在这个界面登录
                    startDataRequestLogin();
                } else {
                    showToast(Check.checkReturn(map.get("errorCode")));
                }
            }
            if (null != login_map) {
                HashMap<String, String> map = (HashMap<String, String>) login_map.get(0);
                String resultCode = map.get("resultCode");
                if (resultCode.equals("1")) {//登录成功
                    myApp.time = (int) (System.currentTimeMillis() / 1000);
                    SharedPreferences sp =getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_USER, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();//存入数据
                    editor.putString(BaseParam.QIAN_SHAREDPREFERENCES_USER_EXPIRES, map.get(BaseParam.QIAN_SHAREDPREFERENCES_USER_EXPIRES));
                    editor.putString(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN, map.get(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN));
                    editor.putString(BaseParam.QIAN_SHAREDPREFERENCES_USER_REALSTATUS, map.get(BaseParam.QIAN_SHAREDPREFERENCES_USER_REALSTATUS));
                    editor.putString(BaseParam.QIAN_SHAREDPREFERENCES_USER_PHONESTATUS, map.get(BaseParam.QIAN_SHAREDPREFERENCES_USER_PHONESTATUS));
                    editor.putString(BaseParam.QIAN_SHAREDPREFERENCES_USER_EMAILSTATUS, map.get(BaseParam.QIAN_SHAREDPREFERENCES_USER_EMAILSTATUS));
                    editor.putString(BaseParam.QIAN_SHAREDPREFERENCES_USER_REFRESHTOKEN, map.get(BaseParam.QIAN_SHAREDPREFERENCES_USER_REFRESHTOKEN));
                    editor.putString(BaseParam.QIAN_SHAREDPREFERENCES_USER_BANKSTATUS, map.get(BaseParam.QIAN_SHAREDPREFERENCES_USER_BANKSTATUS));
                    editor.putString(BaseParam.QIAN_SHAREDPREFERENCES_USER_PAYPWDFLAG, map.get(BaseParam.QIAN_SHAREDPREFERENCES_USER_PAYPWDFLAG));
                    editor.putString(BaseParam.QIAN_SHAREDPREFERENCES_USER_USERID, map.get(BaseParam.QIAN_SHAREDPREFERENCES_USER_USERID));
                    editor.putString(BaseParam.QIAN_SHAREDPREFERENCES_USER_REALNAME, map.get(BaseParam.QIAN_SHAREDPREFERENCES_USER_REALNAME));
                    editor.putString(BaseParam.QIAN_SHAREDPREFERENCES_USER_PHONE, map.get(BaseParam.QIAN_SHAREDPREFERENCES_USER_PHONE));
                    editor.putString(BaseParam.QIAN_SHAREDPREFERENCES_USER_USERNAME, map.get(BaseParam.QIAN_SHAREDPREFERENCES_USER_USERNAME));
                    editor.putString("cardId", map.get("cardId"));
                    editor.putInt(BaseParam.QIAN_SHAREDPREFERENCES_USER_JYCOUNT, 0);
                    editor.putLong(BaseParam.QIAN_SHAREDPREFERENCES_USER_JYTIME, System.currentTimeMillis() / 1000);
                    editor.putString("login_phone",map.get(BaseParam.QIAN_SHAREDPREFERENCES_USER_PHONE));
                    Profile.setUserShare(map.get(BaseParam.QIAN_SHAREDPREFERENCES_USER_SHARE));
                    editor.commit();


                    Intent i=new Intent("login");
                    sendBroadcast(i);  //发个广播到首页和产品页,让它刷新数据
                    AppTool.getUserStatusInfoRequest(); //获取一波用户状态,存到本地
                    finish();   //登录完成,结束当前界面
                } else {
                    MineShow.toastShow(Check.checkReturn(map.get("errorCode")), RetrievePhoneAct.this);
                }
            }
            RetrievePhoneAct.this.progressDialog.dismiss();
            super.handleMessage(paramMessage);
        }
    }
    private void handleWatcher() {

            retrieve_phone_verify.addTextChangedListener(watcher); //验证码的输入框
             retrieve_phone_password.addTextChangedListener(watcher);
        }


    private TextWatcher watcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
            phoneCode = retrieve_phone_verify.getText().toString().trim(); //验证码

            password = retrieve_phone_password.getText().toString().trim();  //密码
            if (phoneCode.length()==4&&password.length()>=8){
                retrieve_phone_secces_btn.setBackgroundResource(R.drawable.button_org_big);
                retrieve_phone_secces_btn.setClickable(true);
            }else {
                retrieve_phone_secces_btn.setBackgroundResource(R.drawable.button_org_grauly);
                retrieve_phone_secces_btn.setClickable(false);
            }

//                if (phoneCode.equals("")) { //验证码不为空,立即找回按钮变红
//                    retrieve_phone_secces_btn.setBackgroundResource(R.drawable.button_org_grauly);
//                } else {
//                    retrieve_phone_secces_btn.setBackgroundResource(R.drawable.button_org_big);
//                }

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

    /**
     * 登录接口
     */
    private void startDataRequestLogin() {
        RetrievePhoneAct.this.initArray();

        RetrievePhoneAct.this.param.add(BaseParam.QIAN_LOGIN_USERNAME);
        RetrievePhoneAct.this.value.add(RetrievePhoneAct.this.phone);
        RetrievePhoneAct.this.param.add(BaseParam.QIAN_LOGIN_PASSWORD);
        RetrievePhoneAct.this.value.add(RetrievePhoneAct.this.password);
        RetrievePhoneAct.this.param.add(BaseParam.URL_QIAN_API_APPID);
        RetrievePhoneAct.this.value.add(appId);
        RetrievePhoneAct.this.param.add(BaseParam.URL_QIAN_API_SERVICE);
        RetrievePhoneAct.this.value.add(myApp.service);
        RetrievePhoneAct.this.param.add(BaseParam.URL_QIAN_API_SIGNTYPE);
        RetrievePhoneAct.this.value.add(myApp.signType);

        String[] array = new String[]{
                BaseParam.QIAN_LOGIN_USERNAME + "=" + RetrievePhoneAct.this.phone,
                BaseParam.QIAN_LOGIN_PASSWORD + "=" + RetrievePhoneAct.this.password,
                BaseParam.URL_QIAN_API_APPID + "=" + appId,
                BaseParam.URL_QIAN_API_SERVICE + "=" + myApp.service,
                BaseParam.URL_QIAN_API_SIGNTYPE + "=" + myApp.signType};

        String sign = apiModel.sortStringArray(array);
        RetrievePhoneAct.this.param.add(BaseParam.URL_QIAN_API_SIGN);
        RetrievePhoneAct.this.value.add(sign);
        RetrievePhoneAct.this.progressDialog = RetrievePhoneAct.this.dia.getLoginDialog(RetrievePhoneAct.this, "正在验证密码..");  //一个旋转的进度圈
        RetrievePhoneAct.this.progressDialog.show();
        new Thread(new JsonRequeatThreadLogin(
                RetrievePhoneAct.this, myApp,
                RetrievePhoneAct.this.myHandler,
                RetrievePhoneAct.this.param,
                RetrievePhoneAct.this.value)
        ).start();
    }

    /**
     * 找回登录密码/获取验证码
     */
    private void startDataRequestVerify() {
        RetrievePhoneAct.this.initArray();
        RetrievePhoneAct.this.param.add(BaseParam.QIAN_LOGIN_PHONE);
        RetrievePhoneAct.this.value.add(phone);
        RetrievePhoneAct.this.param.add(BaseParam.URL_QIAN_API_APPID);
        RetrievePhoneAct.this.value.add(myApp.appId);
        RetrievePhoneAct.this.param.add(BaseParam.URL_QIAN_API_SERVICE);
        RetrievePhoneAct.this.value.add("resetPwdPhoneCode");
        RetrievePhoneAct.this.param.add(BaseParam.URL_QIAN_API_SIGNTYPE);
        RetrievePhoneAct.this.value.add(myApp.signType);
        String[] array = new String[]{
                BaseParam.QIAN_LOGIN_PHONE + "=" + phone,
                BaseParam.URL_QIAN_API_APPID + "=" + myApp.appId,
                BaseParam.URL_QIAN_API_SERVICE + "=resetPwdPhoneCode",
                BaseParam.URL_QIAN_API_SIGNTYPE + "=" + myApp.signType};

        RetrievePhoneAct.this.param.add(BaseParam.URL_QIAN_API_SIGN);
        RetrievePhoneAct.this.value.add(apiModel.sortStringArray(array));
        RetrievePhoneAct.this.progressDialog = RetrievePhoneAct.this.dia.getLoginDialog(RetrievePhoneAct.this, "正在获取验证码..");

        RetrievePhoneAct.this.progressDialog.show();
        new Thread(new JsonRequeatThreadReturnVerify(
                RetrievePhoneAct.this,
                myApp,
                RetrievePhoneAct.this.myHandler,
                RetrievePhoneAct.this.param,
                RetrievePhoneAct.this.value)
        ).start();
    }

    // 找回登录密码效验
    private void startDataRequestPhone() {
        RetrievePhoneAct.this.initArray();

        RetrievePhoneAct.this.param.add("phone");
        RetrievePhoneAct.this.value.add(phone);
        RetrievePhoneAct.this.param.add("realname");
        RetrievePhoneAct.this.value.add("");
        RetrievePhoneAct.this.param.add("card");
        RetrievePhoneAct.this.value.add("");
        RetrievePhoneAct.this.param.add("phoneCode");
        RetrievePhoneAct.this.value.add(phoneCode);

        RetrievePhoneAct.this.param.add(BaseParam.URL_QIAN_API_APPID);
        RetrievePhoneAct.this.value.add(myApp.appId);
        RetrievePhoneAct.this.param.add(BaseParam.URL_QIAN_API_SERVICE);
        RetrievePhoneAct.this.value.add("checkResetLoginPwd");
        RetrievePhoneAct.this.param.add(BaseParam.URL_QIAN_API_SIGNTYPE);
        RetrievePhoneAct.this.value.add(myApp.signType);

        String[] array = new String[]{
                "phone=" + phone,
                "realname=" + "",
                "card=" + "",
                "phoneCode=" + phoneCode,
                BaseParam.URL_QIAN_API_APPID + "=" + myApp.appId,
                BaseParam.URL_QIAN_API_SERVICE + "=checkResetLoginPwd",
                BaseParam.URL_QIAN_API_SIGNTYPE + "=" + myApp.signType};
        RetrievePhoneAct.this.param.add(BaseParam.URL_QIAN_API_SIGN);
        RetrievePhoneAct.this.value.add(apiModel.sortStringArray(array));
        RetrievePhoneAct.this.progressDialog = RetrievePhoneAct.this.dia.getLoginDialog(RetrievePhoneAct.this, "正在验证...");
        RetrievePhoneAct.this.progressDialog.show();
        new Thread(new JsonRequeatThreadRetrieveGai(
                RetrievePhoneAct.this,
                myApp,
                RetrievePhoneAct.this.myHandler,
                RetrievePhoneAct.this.param,
                RetrievePhoneAct.this.value)
        ).start();
    }

    // 重置密码
    private void startDataRequestResetting(String password, String repassword) {
        RetrievePhoneAct.this.initArray();
        RetrievePhoneAct.this.param.add("password");
        RetrievePhoneAct.this.value.add(password);
        RetrievePhoneAct.this.param.add("confirmPassword");
        RetrievePhoneAct.this.value.add(repassword);
        RetrievePhoneAct.this.param.add("userId");
        RetrievePhoneAct.this.value.add(userId);
        RetrievePhoneAct.this.param.add(BaseParam.URL_QIAN_API_APPID);
        RetrievePhoneAct.this.value.add(myApp.appId);
        RetrievePhoneAct.this.param.add(BaseParam.URL_QIAN_API_SERVICE);
        RetrievePhoneAct.this.value.add("resetLoginPassword");
        RetrievePhoneAct.this.param.add(BaseParam.URL_QIAN_API_SIGNTYPE);
        RetrievePhoneAct.this.value.add(myApp.signType);
        String[] array = new String[]{
                "password=" + password,
                "confirmPassword=" + repassword,
                "userId=" + userId,
                BaseParam.URL_QIAN_API_APPID + "=" + myApp.appId,
                BaseParam.URL_QIAN_API_SERVICE + "=resetLoginPassword",
                BaseParam.URL_QIAN_API_SIGNTYPE + "=" + myApp.signType};

        RetrievePhoneAct.this.param.add(BaseParam.URL_QIAN_API_SIGN);
        RetrievePhoneAct.this.value.add(apiModel.sortStringArray(array));
        RetrievePhoneAct.this.progressDialog = RetrievePhoneAct.this.dia.getLoginDialog(RetrievePhoneAct.this, "正在获取验证码..");

        RetrievePhoneAct.this.progressDialog.show();
        new Thread(new JsonRequeatThreadReseting(
                RetrievePhoneAct.this,
                myApp,
                RetrievePhoneAct.this.myHandler,
                RetrievePhoneAct.this.param,
                RetrievePhoneAct.this.value)
        ).start();
    }

    /* 定义一个倒计时的内部类 */
    class MyCount extends CountDownTimer {
        public MyCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            retrieve_phone_verify_btn.setText(R.string.reg_btn_submit);
            retrieve_phone_verify_btn.setClickable(true);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            retrieve_phone_verify_btn.setText(millisUntilFinished / 1000 + "秒后重新获取");
            retrieve_phone_verify_btn.setClickable(false);
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
