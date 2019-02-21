package com.rd.qnz.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.rd.qnz.R;
import com.rd.qnz.custom.APIModel;
import com.rd.qnz.custom.MineShow;
import com.rd.qnz.custom.MyApplication;
import com.rd.qnz.custom.Profile;
import com.rd.qnz.dialog.CustomProgressDialog;
import com.rd.qnz.dialog.GetDialog;
import com.rd.qnz.gustruelock.LockSetupActivity;
import com.rd.qnz.tools.AppTool;
import com.rd.qnz.tools.BaseParam;
import com.rd.qnz.tools.Check;
import com.rd.qnz.tools.webservice.JsonRequeatThreadNewReg;
import com.rd.qnz.tools.webservice.JsonRequeatThreadRegVerify;
import com.rd.qnz.xutils.Installation;
import com.umeng.analytics.MobclickAgent;
import com.zhy.autolayout.AutoLayoutActivity;

import java.util.ArrayList;
import java.util.HashMap;

import static android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD;

/**
 * 注册界面(已适配)
 *
 * @author Evonne
 */
public class Register extends AutoLayoutActivity {
    private static final String TAG = "注册";
    private Button reg_secces_btn;

    private EditText reg_password, reg_verify, codeOrPhoneNumber;
    private ImageView reg_light_icon;
    private boolean isHidden = false;

    private String password, mobile, verify, code;
    private ArrayList<String> param = null;
    private ArrayList<String> value = null;
    private GetDialog dia;
    private MyHandler myHandler;
    private CustomProgressDialog progressDialog = null;
    private MyApplication myApp;
    private ImageView reg_checkBox;
    private TextView reg_xieyi, reg_verify_btn;
    private String imei;
    APIModel apiModel = new APIModel();
    private boolean ischeck = true;

    private TextView text_phone;

    /**
     * 1.6.0
     */
    private ImageView iv_left;
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG); // 统计页面
        MobclickAgent.onResume(this); // 统计时长
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(TAG); // 保证 onPageEnd 在onPause 之前调用,因为 onPause
        // // 中会保存信息
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        this.dia = new GetDialog();
        myApp = (MyApplication) getApplication();
        mobile = getIntent().getStringExtra("mobile");  //手机号
        this.myHandler = new MyHandler();
        imei = AppTool.imeiSave(Register.this);
        imei = imei + "-qian-" + Installation.id(Register.this);
        initBar();
        intView();
    }

    private void initBar() {
        iv_left= (ImageView) findViewById(R.id.iv_left);
        iv_left.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                myApp.isReg = true;   //是从注册跳转,如果点击左上角
                finish();
            }
        });
    }

    private static String replaceChar(String temp) {
        return temp.replace(temp.substring(3, 7), "****");
    }
    private String dataID = "qwertyuioplkjhgfdsazxcvbnmQWERTYUIOPLKJHGFDSAZXCVBNM1234567890";
    private void intView() {
        codeOrPhoneNumber = (EditText) findViewById(R.id.reg_code_or_phonenumber);  //推荐人手机号
        reg_password = (EditText) findViewById(R.id.reg_password);              //设置8~20位字符登录密码

        reg_password.setKeyListener(new DigitsKeyListener() {
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

        reg_light_icon = (ImageView) findViewById(R.id.reg_light_icon);           //眼睛
        reg_verify = (EditText) findViewById(R.id.reg_verify);          //请输入验证码

        reg_verify.setFocusable(true); //手机校验通过,显示输入密码文本框,并弹出键盘
        reg_verify.setFocusableInTouchMode(true);
        reg_verify.requestFocus();
        reg_verify.findFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);


        reg_xieyi = (TextView) findViewById(R.id.reg_xieyi);        //reg_xieyi 钱内助注册协议
        reg_checkBox = (ImageView) findViewById(R.id.reg_checkBox);  //打钩
        text_phone = (TextView) findViewById(R.id.text_phone);      //手机号
        text_phone.setText(replaceChar(mobile)+"");      //显示手机号码

        //打了勾并且密码,验证码不为空,才能够点击注册按钮
        reg_checkBox.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                ischeck = !ischeck;
                if (ischeck) { //打钩了
                    reg_checkBox.setBackgroundResource(R.drawable.pitchon_btn);
                    password = reg_password.getText().toString().trim();
                    verify = reg_verify.getText().toString().trim();


                    if (password.length()>=8&&verify.length()==4){
                        reg_secces_btn.setBackgroundResource(R.drawable.button_org_big);
                        reg_secces_btn.setClickable(true);
                    }else {
                        reg_secces_btn.setBackgroundResource(R.drawable.button_org_grauly);
                        reg_secces_btn.setClickable(false);
                    }


                } else {
                    reg_checkBox.setBackgroundResource(R.drawable.unchecke_btn);
                    reg_secces_btn.setBackgroundResource(R.drawable.button_org_grauly);
                    reg_secces_btn.setClickable(false);
                }
            }
        });
                     //钱内助注册协议
        reg_xieyi.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO Auto-generated method stub
                showPopupWindow(view);
            }
        });
        //立即获取验证码
        reg_verify_btn = (TextView) findViewById(R.id.reg_verify_btn);

        reg_verify_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                startDataRequestVerify();
            }
        });
        //显示或隐藏密码
        reg_light_icon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isHidden) {
                    //show the password
                    reg_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
//                    reg_light_icon.setImageResource(R.drawable.eye_selected_btn);
                       reg_light_icon.setBackgroundResource(R.drawable.eye_default_btn1);
                } else {
                    //hide the password
                    reg_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
//                    reg_light_icon.setImageResource(R.drawable.eye_default_btn);
                    reg_light_icon.setBackgroundResource(R.drawable.eye_selected_btn1);
                }
                isHidden = !isHidden;
                reg_password.setSelection(reg_password.getText().length());
            }
        });

        //确认按钮
        reg_secces_btn = (Button) findViewById(R.id.reg_secces_btn);
        reg_secces_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                password = reg_password.getText().toString().trim();        //密码
                code = codeOrPhoneNumber.getText().toString().trim();       //推荐人用户名
                if (password.equals("") || password == null) {              //密码为空
                    return;
                } else if (!Check.hasInternet(Register.this)) {  //当前没有网络
                    MineShow.toastShow(BaseParam.ERRORCODE_CHECKNET, Register.this);
                    return;
                }
                verify = reg_verify.getText().toString().trim();//验证码
                if (!ischeck) {
                    return;
                }
                startDataRequest();
            }
        });
        //监听密码输入框
        reg_password.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {

                // TODO Auto-generated method stub
                password = reg_password.getText().toString().trim();
                verify = reg_verify.getText().toString().trim();
                if (password.length()>=8&&verify.length()==4&&ischeck){
                    reg_secces_btn.setBackgroundResource(R.drawable.button_org_big);
                    reg_secces_btn.setClickable(true);
                }else {
                    reg_secces_btn.setBackgroundResource(R.drawable.button_org_grauly);
                    reg_secces_btn.setClickable(false);
                }


            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                // TODO Auto-generated method stub
            }

        });
        //监听验证码输入框
        reg_verify.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
                password = reg_password.getText().toString().trim();
                verify = reg_verify.getText().toString().trim();

                if (password.length()>=8&&verify.length()==4&&ischeck){
                    reg_secces_btn.setBackgroundResource(R.drawable.button_org_big);
                    reg_secces_btn.setClickable(true);
                }else {
                    reg_secces_btn.setBackgroundResource(R.drawable.button_org_grauly);
                    reg_secces_btn.setClickable(false);
                }


            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                // TODO Auto-generated method stub
            }

        });

        startDataRequestVerify();
    }

    private class MyHandler extends Handler {
        public void handleMessage(Message paramMessage) {
            Bundle localBundle1 = paramMessage.getData();
            ArrayList<Parcelable> regVerify_list = localBundle1.getParcelableArrayList(BaseParam.QIAN_REQUEAT_REG_VERIFY); //用户请求验证码返回的数据
            ArrayList<Parcelable> reg_list = localBundle1.getParcelableArrayList(BaseParam.QIAN_REQUEAT_REG); //用户注册返回的数据
            if (null != regVerify_list) {
                HashMap<String, String> map = (HashMap<String, String>) regVerify_list.get(0);
                String resultCode = map.get("resultCode");
                if (resultCode.equals("1")) {
                    // 服务器已经发送验证码
                    MyCount mc = new MyCount(60000, 1000);
                    mc.start();
                } else {
                    MineShow.toastShow(Check.checkReturn(map.get("errorCode")), Register.this); //打印一个吐司
                }
            }
            if (null != reg_list) {
                HashMap<String, String> map = (HashMap<String, String>) reg_list.get(0);
                String resultCode = map.get("resultCode");
                if (resultCode.equals("1")) {

                    Context ctx = Register.this;
                    SharedPreferences sp = ctx.getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_USER, MODE_PRIVATE);
                    // 存入数据
                    Editor editor = sp.edit();
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
                    editor.putInt(BaseParam.QIAN_SHAREDPREFERENCES_USER_JYCOUNT, 0);
                    editor.putLong(BaseParam.QIAN_SHAREDPREFERENCES_USER_JYTIME, System.currentTimeMillis() / 1000);
                    Profile.setUserShare(map.get(BaseParam.QIAN_SHAREDPREFERENCES_USER_SHARE));
                    editor.commit();
                    myApp.isReg = false;
                    myApp.redPacketAmount = map.get(BaseParam.QIAN_REDPACKETAMOUNT);



                    myApp.redPacketOpen="";

                    Intent intent = new Intent(Register.this, LockSetupActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("reg", "reg");
                    intent.putExtras(bundle);
                    startActivity(intent);//登录成功,进入手势密码页
                    sendBroadcast(new Intent("register"));//发送广播给login让它关闭自身.让HomePager弹出一个红包界面
                    myApp.tabHost.setCurrentTab(0); //回到首页

                    finish();
                } else {
                    MineShow.toastShow(Check.checkReturn(map.get("errorCode")), Register.this);
                }
            }
            Register.this.progressDialog.dismiss();
            super.handleMessage(paramMessage);
        }
    }

    /* 定义一个倒计时的内部类 */
    class MyCount extends CountDownTimer {
        public MyCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            reg_verify_btn.setText(R.string.reg_btn_submit);
            reg_verify_btn.setClickable(true);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            reg_verify_btn.setText("重发（" + millisUntilFinished / 1000 + "）");
            reg_verify_btn.setClickable(false);
        }
    }

    /**
     * 3.	注册获取验证码
     */
    private void startDataRequestVerify() {
        Register.this.initArray();

        Register.this.param.add(BaseParam.QIAN_LOGIN_PHONE);
        Register.this.value.add(mobile);
        Register.this.param.add(BaseParam.URL_QIAN_API_APPID);
        Register.this.value.add(myApp.appId);
        Register.this.param.add(BaseParam.URL_QIAN_API_SERVICE);
        Register.this.value.add("regPhoneCode");
        Register.this.param.add(BaseParam.URL_QIAN_API_SIGNTYPE);
        Register.this.value.add(myApp.signType);
        String[] array = new String[]{
                BaseParam.QIAN_LOGIN_PHONE + "=" + mobile,
                BaseParam.URL_QIAN_API_APPID + "=" + myApp.appId,
                BaseParam.URL_QIAN_API_SERVICE + "=regPhoneCode",
                BaseParam.URL_QIAN_API_SIGNTYPE + "=" + myApp.signType};

        Register.this.param.add(BaseParam.URL_QIAN_API_SIGN);
        Register.this.value.add(apiModel.sortStringArray(array));
        Register.this.progressDialog = Register.this.dia.getLoginDialog(Register.this, "正在获取验证码..");
        Register.this.progressDialog.show();
        new Thread(new JsonRequeatThreadRegVerify(
                Register.this,
                myApp,
                Register.this.myHandler,
                Register.this.param,
                Register.this.value)
        ).start();
    }

    /**
     * 用户注册
     */
    private void startDataRequest() {
        Register.this.initArray();

        Register.this.param.add(BaseParam.QIAN_LOGIN_PASSWORD);
        Register.this.value.add(Register.this.password);
        param.add("systemType");
        value.add("24");//android 版本
        param.add("version");
        value.add(BaseParam.getVersionName(Register.this));
        Register.this.param.add(BaseParam.QIAN_LOGIN_PHONE);
        Register.this.value.add(Register.this.mobile);
        Register.this.param.add(BaseParam.QIAN_LOGIN_PHONECODE);
        Register.this.value.add(Register.this.verify);
        Register.this.param.add(BaseParam.URL_QIAN_API_APPID);
        Register.this.value.add(myApp.appId);
        Register.this.param.add(BaseParam.URL_QIAN_API_SERVICE);
        Register.this.value.add("newRegister");
        Register.this.param.add(BaseParam.URL_QIAN_API_SIGNTYPE);
        Register.this.value.add(myApp.signType);
        Register.this.param.add("idfa");
        Register.this.value.add(imei);
        Register.this.param.add("invite");
        Register.this.value.add(code);

        String[] array = new String[]{
                BaseParam.QIAN_LOGIN_PASSWORD + "=" + Register.this.password,
                "systemType=24",
                "version=" + BaseParam.getVersionName(Register.this),
                BaseParam.QIAN_LOGIN_PHONE + "=" + Register.this.mobile,
                BaseParam.QIAN_LOGIN_PHONECODE + "=" + Register.this.verify,
                BaseParam.URL_QIAN_API_APPID + "=" + myApp.appId,
                BaseParam.URL_QIAN_API_SERVICE + "=newRegister",
                BaseParam.URL_QIAN_API_SIGNTYPE + "=" + myApp.signType,
                "idfa=" + imei,
                "invite=" + code};

        String sign = apiModel.sortStringArray(array);
        Register.this.param.add(BaseParam.URL_QIAN_API_SIGN);
        Register.this.value.add(sign);

        Register.this.progressDialog = Register.this.dia.getLoginDialog(Register.this, "正在注册用户..");
        Register.this.progressDialog.show();
        new Thread(new JsonRequeatThreadNewReg(
                Register.this,
                myApp,
                Register.this.myHandler,
                Register.this.param,
                Register.this.value)
        ).start();
    }

    private void initArray() {
        this.param = null;
        this.value = null;
        this.param = new ArrayList();
        this.value = new ArrayList();
        System.gc();
    }

    /***
     * 注册须知
     */
    private void showPopupWindow(View view) {
        View contentView = LayoutInflater.from(this).inflate(R.layout.register_agreement, null);
        final PopupWindow popupWindow = new PopupWindow(contentView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, true);

        contentView.findViewById(R.id.button_xieyi).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        popupWindow.showAtLocation(contentView, Gravity.CENTER, 0, 0);
        popupWindow.setFocusable(true);
        popupWindow.showAsDropDown(view);
    }

}
