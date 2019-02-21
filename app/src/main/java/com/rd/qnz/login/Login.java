package com.rd.qnz.login;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
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
import com.rd.qnz.tools.webservice.JsonRequeatThreadLogin;
import com.rd.qnz.tools.webservice.JsonRequeatThreadPhoneCheckLogin;
import com.rd.qnz.view.CircularNetworkImageView;
import com.umeng.analytics.MobclickAgent;
import com.zhy.autolayout.AutoLayoutActivity;

import java.util.ArrayList;
import java.util.HashMap;

import static android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD;
import static com.rd.qnz.custom.MyApplication.appId;


/**
 * 1.6.0版本 已适配的登录界面
 *
 * @author Evonne
 */
public class Login extends AutoLayoutActivity {

    private static final String TAG = "登录";
    private Button login_btn;
    private EditText login_phone;
    private MyApplication myApp;
    private Context ctx;
    private ArrayList<String> param = null;
    private ArrayList<String> value = null;
    private GetDialog dia;
    private MyHandler myHandler;
    APIModel apiModel = new APIModel();

    private CustomProgressDialog progressDialog = null;

    private String username;
    private String password;

    private RelativeLayout login_phone_linear;//输入手机号界面，
    private RelativeLayout login_password_linear;// 输入密码界面
    private EditText login_password;
    private TextView login_forget, phone_login;
    private Button login_password_btn;
    private ImageView clear, clear_password, reg_light_icon;
    private String cardExist;
    private boolean isGo = false;  //代表现在是否是注册界面
    private boolean isHidden = false;  //密码默认是隐藏的

    private boolean forget=false; //是否从此忘记密码界面跳转过来的
    private boolean MainTabAct=false; //是否是从主界面点击底部跳转过来的
    private String logined_phone;


    /**
     *   1.6.0新版本修改登录注册
     *
     */
    private TextView tv_qiehuan; //右侧切换账号的文字
    private ImageView iv_left; //左侧图标
    private String userIcon;
    private CircularNetworkImageView mine_info_portrait_iv; //用户头像
    private String dataID = "qwertyuioplkjhgfdsazxcvbnmQWERTYUIOPLKJHGFDSAZXCVBNM1234567890";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        this.dia = new GetDialog();
        myApp = (MyApplication) getApplication();

        this.myHandler = new MyHandler();
        myApp.uniqNo = "";
        ctx = Login.this;


        Intent i=getIntent();
        forget=i.getBooleanExtra("forget",false);  //是否是从登录界面过来的
        MainTabAct=i.getBooleanExtra("MainTabAct",false);
        //注册动态广播,接收注册界面发来的数据
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction("register");
        myIntentFilter.addAction("forgetPassword"); //接收来自忘记密码界面发来的消息
        registerReceiver(myReceiver,myIntentFilter);

        // 清空sp_user里面的数据
        SharedPreferences sp = ctx.getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_USER, MODE_PRIVATE);
        //取出曾经登录过的手机号
        logined_phone=sp.getString("login_phone","");
        //存入数据
        Editor editor = sp.edit();
        editor.putString(BaseParam.QIAN_SHAREDPREFERENCES_USER_EXPIRES, "");
        editor.putString(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN, "");
        editor.putString(BaseParam.QIAN_SHAREDPREFERENCES_USER_REALSTATUS, "");
        editor.putString(BaseParam.QIAN_SHAREDPREFERENCES_USER_PHONESTATUS, "");
        editor.putString(BaseParam.QIAN_SHAREDPREFERENCES_USER_EMAILSTATUS, "");
        editor.putString(BaseParam.QIAN_SHAREDPREFERENCES_USER_REFRESHTOKEN, "");
        editor.putString(BaseParam.QIAN_SHAREDPREFERENCES_USER_BANKSTATUS, "");
        editor.putString(BaseParam.QIAN_SHAREDPREFERENCES_USER_PAYPWDFLAG, "");
        editor.putString(BaseParam.QIAN_SHAREDPREFERENCES_USER_USERID, "");
        editor.putString(BaseParam.QIAN_SHAREDPREFERENCES_USER_REALNAME, "");
        editor.putString(BaseParam.QIAN_SHAREDPREFERENCES_USER_ISRED, "");
        editor.putString(BaseParam.QIAN_SHAREDPREFERENCES_USER_REALNAME, "");
        editor.putString(BaseParam.QIAN_SHAREDPREFERENCES_USER_PHONE, "");
        editor.putString(BaseParam.QIAN_SHAREDPREFERENCES_USER_USERNAME, "");
        editor.commit();
        clearUserStatue();   //清空个人账户,银行卡信息
        initBar();
        initView();

    }

    private void clearUserStatue() {
        Profile.setUserBankCardStatus("");
        Profile.setUserNeedPopStatus("");
        Profile.setUserPayPassWordStatus("");
        Profile.setUserRealNameStatus("");
    }



    private void initBar() {
        iv_left= (ImageView) findViewById(R.id.iv_left);
        iv_left.setImageResource(R.drawable.login_first_left);
        iv_left.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myApp.isAnzhuang) {   //如果是第一次打开 ,跳到主界面
                     finish();
                }
                if (isGo) {   //如果已经是注册界面了,这时候点击左上角箭头
                    login_phone_linear.setVisibility(View.VISIBLE);
                    login_password_linear.setVisibility(View.GONE);
                    login_password.setText("");
                    tv_qiehuan.setVisibility(View.GONE); //隐藏右侧
                    iv_left.setImageResource(R.drawable.login_first_left);
                    isGo = false;
                } else {
                    finish();
                }
            }
        });




        tv_qiehuan= (TextView) findViewById(R.id.tv_qiehuan);
        tv_qiehuan.setOnClickListener(new OnClickListener() {  //点击切换账号
            @Override
            public void onClick(View v) {
                login_phone_linear.setVisibility(View.VISIBLE);
                login_password_linear.setVisibility(View.GONE);
                login_password.setText("");
                tv_qiehuan.setVisibility(View.GONE);
                iv_left.setImageResource(R.drawable.login_first_left);
                isGo = false;
            }
        });
    }

    @SuppressLint("NewApi")
    private void initView() {
        mine_info_portrait_iv= (CircularNetworkImageView) findViewById(R.id.mine_info_portrait_iv); //用户头像
        login_phone = (EditText) findViewById(R.id.login_phone);  //账号输入框
        login_btn = (Button) findViewById(R.id.login_btn);   //登录/注册按钮
        clear = (ImageView) findViewById(R.id.clear);  //清空手机号的按钮
        clear_password= (ImageView) findViewById(R.id.clear_password);
        login_phone_linear = (RelativeLayout) findViewById(R.id.login_phone_linear);
        login_password_linear = (RelativeLayout) findViewById(R.id.login_password_linear);
        login_phone_linear.setVisibility(View.VISIBLE);
        login_password_linear.setVisibility(View.GONE);

        login_password = (EditText) findViewById(R.id.login_password);  //请输入登录密码
        login_forget = (TextView) findViewById(R.id.login_forget);  //忘记密码`
        phone_login = (TextView) findViewById(R.id.phone_login);
        login_password_btn = (Button) findViewById(R.id.login_password_btn);  //确定按钮
        reg_light_icon = (ImageView) findViewById(R.id.reg_light_icon); //一个带眼睛的图片,用来判断密码是否需要隐藏



        //点击x图标,清空号码
        clear.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                login_phone.setText("");
                clear.setVisibility(View.GONE);
                login_btn.setBackgroundResource(R.drawable.button_org_grauly);
                login_btn.setClickable(false);
            }
        });

        //点击x图标,清空密码
        clear_password.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                login_password.setText("");
                clear_password.setVisibility(View.GONE);
                login_password_btn.setBackgroundResource(R.drawable.button_org_grauly);
                login_password_btn.setClickable(false);
            }
        });


            //一个眼睛的图片
        reg_light_icon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isHidden) {
                    //show the password   显示密码
                    login_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    reg_light_icon.setBackgroundResource(R.drawable.eye_default_btn1);

                } else {
                    //hide the password   隐藏密码
                    login_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    reg_light_icon.setBackgroundResource(R.drawable.eye_selected_btn1);
                }
                isHidden = !isHidden;
            }
        });
            //监听手机号的输入 ,是不是给他加点东西
        login_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
                String phone = login_phone.getText().toString().trim();
                if (phone.length() < 11) { //如果手机号小于11位,那么这个按钮是不能点击的,背景图片也不会改变
                    login_btn.setBackgroundResource(R.drawable.button_org_grauly);
                    login_btn.setClickable(false);
                } else {
                    login_btn.setBackgroundResource(R.drawable.button_org_big);
                    login_btn.setClickable(true);
                }
                if (phone.equals("")) {

                    clear.setVisibility(View.GONE);
                } else {
                    clear.setVisibility(View.VISIBLE);

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
        //监听密码框的输入
        login_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
                String phone = login_password.getText().toString().trim();
                if (phone.equals("")) {
                    clear_password.setVisibility(View.GONE);
                    login_password_btn.setBackgroundResource(R.drawable.button_org_grauly);
                    login_password_btn.setClickable(false);  //立即登录置灰
                } else {
                    clear_password.setVisibility(View.VISIBLE);
                    if (phone.length()>=8&&phone.length() <= 20) {
                        login_password_btn.setBackgroundResource(R.drawable.button_org_big);
                        login_password_btn.setClickable(true);
                    }else {
                        login_password_btn.setBackgroundResource(R.drawable.button_org_grauly);
                        login_password_btn.setClickable(false);
                    }

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

        //  检测手机号监听登录/注册按钮
        login_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                username = login_phone.getText().toString().trim();
                if (username.equals("") || username == null) {
                    return;
                }
                startDataRequest();   //进行手机是否存在校验,他会返回resultData,你就可以知道这个手机号是准备登录或者新注册
            }
        });
        //忘记密码
        login_forget.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(Login.this, RetrievePhoneAct.class);
                intent.putExtra("cardExist", cardExist);
                intent.putExtra("phone", username);
                startActivity(intent);
            }
        });
         //监听确定按钮
        login_password_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                password = login_password.getText().toString().trim();
                if (password.equals("")) {
                    return;
                }
                startDataRequestLogin();
            }
        });

        if (!TextUtils.isEmpty(logined_phone)){
            login_phone.setText(logined_phone);
        }
    }

    /**
     * 校验手机号是否存在
     */
    private void startDataRequest() {
        Login.this.initArray();
        Login.this.param.add(BaseParam.QIAN_LOGIN_USERNAME);
        Login.this.value.add(Login.this.username);
        Login.this.param.add(BaseParam.URL_QIAN_API_APPID);
        Login.this.value.add(appId);
        Login.this.param.add(BaseParam.URL_QIAN_API_SERVICE);
        Login.this.value.add("phoneCheck");
        Login.this.param.add(BaseParam.URL_QIAN_API_SIGNTYPE);
        Login.this.value.add(myApp.signType);

        String[] array = new String[]{
                BaseParam.QIAN_LOGIN_USERNAME + "=" + Login.this.username,
                BaseParam.URL_QIAN_API_APPID + "=" + appId,
                BaseParam.URL_QIAN_API_SERVICE + "=phoneCheck",
                BaseParam.URL_QIAN_API_SIGNTYPE + "=" + myApp.signType};

        String sign = apiModel.sortStringArray(array);
        Login.this.param.add(BaseParam.URL_QIAN_API_SIGN);
        Login.this.value.add(sign);


        Login.this.progressDialog = Login.this.dia.getLoginDialog(Login.this, "手机号码检测中");
        Login.this.progressDialog.show();
        new Thread(new JsonRequeatThreadPhoneCheckLogin(   //登陆 点击输入手机号的下一步按钮调用，手机存在校验
                Login.this,
                myApp,
                Login.this.myHandler,
                Login.this.param,
                Login.this.value)
        ).start();


    }

    /**
     * 请求登录接口
     */
    private void startDataRequestLogin() {
        Login.this.initArray();

        Login.this.param.add(BaseParam.QIAN_LOGIN_USERNAME);
        Login.this.value.add(Login.this.username);
        Login.this.param.add(BaseParam.QIAN_LOGIN_PASSWORD);
        Login.this.value.add(Login.this.password);
        Login.this.param.add(BaseParam.URL_QIAN_API_APPID);
        Login.this.value.add(appId);
        Login.this.param.add(BaseParam.URL_QIAN_API_SERVICE);
        Login.this.value.add(myApp.service);
        Login.this.param.add(BaseParam.URL_QIAN_API_SIGNTYPE);
        Login.this.value.add(myApp.signType);

        String[] array = new String[]{
                BaseParam.QIAN_LOGIN_USERNAME + "=" + Login.this.username,
                BaseParam.QIAN_LOGIN_PASSWORD + "=" + Login.this.password,
                BaseParam.URL_QIAN_API_APPID + "=" + appId,
                BaseParam.URL_QIAN_API_SERVICE + "=" + myApp.service,
                BaseParam.URL_QIAN_API_SIGNTYPE + "=" + myApp.signType};

        String sign = apiModel.sortStringArray(array);
        Login.this.param.add(BaseParam.URL_QIAN_API_SIGN);
        Login.this.value.add(sign);
        Login.this.progressDialog = Login.this.dia.getLoginDialog(Login.this, "登录中....");  //一个旋转的进度圈
        Login.this.progressDialog.show();

        new Thread(new JsonRequeatThreadLogin(
                Login.this, myApp,
                Login.this.myHandler,
                Login.this.param,
                Login.this.value)
        ).start();

    }

    @Override
    protected void onRestart() {
        // TODO Auto-generated method stub
        super.onRestart();
        //如果我是从忘记密码界面,注册界面点击了左上角返回回来的那么它是true,那就停留在登录界面
        //如果我是从这两个界面返回回来的,并且是忘记密码成功,注册成功返回回来的就把登录界面关闭
        if (myApp.isReg) {
            myApp.isReg = false;

        } else {
            finish();
        }
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG); // 统计页面
        MobclickAgent.onResume(this); // 统计时长
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(TAG); // 保证 onPageEnd 在onPause 之前调用,因为 onPause
        MobclickAgent.onPause(this);
    }
    /**
     * 跳转到注册界面的时候,登录界面没有被关闭(让用户在注册界面点击左上角能够退回来),在注册完成之后发送广播到登录界面,
     * 把登录界面给finish
     */
    private BroadcastReceiver myReceiver = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
            String action=intent.getAction();
            if (action.equals("register")){
                Login.this.finish();
            }else if (action.equals("forgetPassword")){
                    password= intent.getStringExtra("password");
                    if (!TextUtils.isEmpty(username)){ //手机号不为空
                        startDataRequestLogin();
                    }
            }

        }

    };



    private class MyHandler extends Handler {
        public void handleMessage(Message paramMessage) {
            Bundle localBundle1 = paramMessage.getData();
            ArrayList<Parcelable> phone_check = localBundle1.getParcelableArrayList(BaseParam.URL_QIAN_PHONECHECK);
            ArrayList<Parcelable> login_map = localBundle1.getParcelableArrayList(BaseParam.QIAN_REQUEAT_LOGIN);

            if (null != phone_check) {   //验证手机号码是否存在
                HashMap<String, String> map = (HashMap<String, String>) phone_check.get(0);
                String resultCode = map.get("resultCode");
                if (resultCode.equals("1")) {   //代表请求成功
                    cardExist = map.get("cardExist");         //银行卡已经绑定
                    SharedPreferences sp = ctx.getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_USER, MODE_PRIVATE);
                    Editor editor = sp.edit();
                    editor.putString("cardExist", cardExist);
                    editor.commit();
                    if (map.get("phoneExist").equals("1")) { //手机号码已经存在(登录)
                        userIcon=BaseParam.URL_QIAN+map.get("userIcon");

                        iv_left.setImageResource(R.drawable.login_second_left);
                        login_phone_linear.setVisibility(View.GONE);
                        login_password_linear.setVisibility(View.VISIBLE);

                        ImageLoader.getInstance().displayImage(userIcon, mine_info_portrait_iv, new ImageLoadingListener() {
                            @Override
                            public void onLoadingStarted(String s, View view) {

                            }

                            @Override
                            public void onLoadingFailed(String s, View view, FailReason failReason) {

                                myApp.bt_head=null;
                                mine_info_portrait_iv.setImageResource(R.drawable.person);
                            }

                            @Override
                            public void onLoadingComplete(String s, View view, Bitmap bitmap) {

                                myApp.bt_head=bitmap;
                                mine_info_portrait_iv.setImageBitmap(bitmap);
                            }

                            @Override
                            public void onLoadingCancelled(String s, View view) {

                            }
                        });




                        login_password.setFocusable(true); //手机校验通过,显示输入密码文本框,并弹出键盘
                        login_password.setFocusableInTouchMode(true);
                        login_password.requestFocus();
                        login_password.findFocus();
                        login_password.setKeyListener(new DigitsKeyListener() {
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
                        //将输入法弹出的右下角的按钮改为完成，不改的话会是下一步。
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE); //打开字母键盘,而不是英文键盘


                        phone_login.setText(username.replace(username.substring(3, 7), "****"));
                        tv_qiehuan.setVisibility(View.VISIBLE);

                        isGo = true;
                    } else { //手机号码不存在(注册)
                        Intent intent = new Intent(Login.this, Register.class);
                        intent.putExtra("mobile", username);
                        startActivity(intent);
                    }
                } else {
                    MineShow.toastShow(Check.checkReturn(map.get("errorCode")), Login.this);
                }
            }
            if (null != login_map) {  //检验登录密码是否正确
                HashMap<String, String> map = (HashMap<String, String>) login_map.get(0);
                String resultCode = map.get("resultCode");
                if (resultCode.equals("1")) { //登录成功,判断
                    myApp.time = (int) (System.currentTimeMillis() / 1000);
                    SharedPreferences sp = ctx.getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_USER, MODE_PRIVATE);
                    Editor editor = sp.edit();//存入数据
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
                    if (!TextUtils.equals(map.get(BaseParam.QIAN_SHAREDPREFERENCES_USER_PHONE),logined_phone)){//如果现在登录的号跟之前的不一样,就清空手势密码
                     SharedPreferences  preferences=getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_LOCK, Context.MODE_PRIVATE);
                        preferences.edit().putString(BaseParam.QIAN_SHAREDPREFERENCES_LOCKKEY,"").commit(); //清除手势密码
                    }
                    editor.putString("login_phone",map.get(BaseParam.QIAN_SHAREDPREFERENCES_USER_PHONE));
                    Profile.setUserShare(map.get(BaseParam.QIAN_SHAREDPREFERENCES_USER_SHARE));
                    editor.commit();
                    if (forget){  //如果是从忘记密码界面来登录的话,登陆成功还需要设置一下手势密码并发送一个广播
                        forget=false;
                        startActivity(new Intent(Login.this, LockSetupActivity.class));
                    }else if (MainTabAct){
                        Intent i=new Intent("MainTabAct");
                        sendBroadcast(i);  //发个广播到首页,切换底部到钱袋界面
                    }
                    Intent i=new Intent("login");
                    sendBroadcast(i);  //发个广播到首页和产品页,让它刷新数据
                    AppTool.getUserStatusInfoRequest(); //获取一波用户状态,存到本地
                    finish();   //登录完成,结束当前界面
                } else {
                    if (!TextUtils.isEmpty(map.get("resultMsg"))){
                        MineShow.toastShow(map.get("resultMsg"),Login.this);
                    }else if (!TextUtils.isEmpty(map.get("errorMsg"))){
                        MineShow.toastShow(map.get("errorMsg"),Login.this);
                    }else {
                        MineShow.toastShow(map.get("服务器异常"),Login.this);
                    }

                }
            }
            Login.this.progressDialog.dismiss();
            super.handleMessage(paramMessage);
        }
    }

    @Override
    public void finish() {
        super.finish();
        this.overridePendingTransition(0,R.anim.activity_close);
    }





    private void initArray() {
        this.param = null;
        this.value = null;
        this.param = new ArrayList();
        this.value = new ArrayList();
        System.gc();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myReceiver != null) {
            try{
                unregisterReceiver(myReceiver);
            }catch (Exception e){
                e.printStackTrace();
            }

        }
        if (null != progressDialog && progressDialog.isShowing()) {// 隐藏加载框
            Login.this.progressDialog.dismiss();
            progressDialog=null;
        }


    }
}
