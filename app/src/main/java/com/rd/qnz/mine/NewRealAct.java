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
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.rd.qnz.BaseActivity;
import com.rd.qnz.R;
import com.rd.qnz.custom.APIModel;
import com.rd.qnz.custom.MyApplication;
import com.rd.qnz.dialog.CustomProgressDialog;
import com.rd.qnz.dialog.GetDialog;
import com.rd.qnz.login.Login;
import com.rd.qnz.tools.AppTool;
import com.rd.qnz.tools.BaseParam;
import com.rd.qnz.tools.Check;
import com.rd.qnz.tools.webservice.JsonRequeatThreadNewReal;
import com.rd.qnz.xutils.Installation;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 1.3.0 实名认证
 *
 * @author Evonne
 */
public class NewRealAct extends BaseActivity {


    private static final String TAG ="NewRealAct" ;
    private Button real_next_btn;
    private EditText real_name, real_status;// 姓名 身份证
    private EditText mRealPayPassword;//交易密码

    private String imei;// 手机唯一标识
    private String name, status;
    private ArrayList<String> param = null;
    private ArrayList<String> value = null;
    private GetDialog dia;
    private MyHandler myHandler;
    private CustomProgressDialog progressDialog = null;
    private MyApplication myApp;

    APIModel apiModel = new APIModel();
    private String oauthToken = "";
    private String mRealPayPassWordValue;
    private Context context;

    private ImageButton reg_light_icon;
    private boolean isHidden = true;
    private String dataID = "qwertyuioplkjhgfdsazxcvbnmQWERTYUIOPLKJHGFDSAZXCVBNM1234567890";
    public static void start(Context context) {
        Intent i = new Intent(context, NewRealAct.class);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        Log.d(TAG,"NewRealAct的 onCreate()被调用");
        setContentView(R.layout.my_new_real);
        context = NewRealAct.this;
        this.dia = new GetDialog();
        myApp = (MyApplication) getApplication();
        this.myHandler = new MyHandler();
        SharedPreferences preferences = getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_USER, Context.MODE_PRIVATE);
        oauthToken = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN, "");
        imei = AppTool.imeiSave(NewRealAct.this);
        imei = imei + "-qian-" + Installation.id(NewRealAct.this);
        initBar();
        intView();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG,"NewRealAct的 onStop()被调用");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"NewRealAct的 onDestroy()被调用");
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
        actionbar_side_name.setText("实名认证");

    }

    public void intView() {
        mRealPayPassword = (EditText) findViewById(R.id.real_pay_password); //请输入交易密码
        real_name = (EditText) findViewById(R.id.real_name);     //请输入您的真实姓名
        real_status = (EditText) findViewById(R.id.real_status); //请输入二代身份证
        real_next_btn = (Button) findViewById(R.id.real_next_btn); //完成按钮
        reg_light_icon= (ImageButton) findViewById(R.id.reg_light_icon); //眼睛
        real_status.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
                name = real_name.getText().toString().trim();
                status = real_status.getText().toString().trim();
                mRealPayPassWordValue = mRealPayPassword.getText().toString().trim();

                if (status.length() > 18) {
                    real_status.setText(status.substring(0, status.length() - 1));
                    real_status.setText(status.substring(0, status.length() - 1));
                    real_status.setSelection(real_status.length());
                }

                if (status.equals("") || name.equals("") || mRealPayPassWordValue.equals("")) {
                    real_next_btn.setBackgroundResource(R.drawable.button_grauly_big);
                    real_next_btn.setClickable(false);
                } else {
                    real_next_btn.setBackgroundResource(R.drawable.button_org_big);
                    real_next_btn.setClickable(true);
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

        real_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                name = real_name.getText().toString().trim();
                status = real_status.getText().toString().trim();
                mRealPayPassWordValue = mRealPayPassword.getText().toString().trim();
                if (status.equals("") || name.equals("") || mRealPayPassWordValue.equals("")) {
                    real_next_btn.setBackgroundResource(R.drawable.button_grauly_big);
                    real_next_btn.setClickable(false);
                } else {
                    real_next_btn.setBackgroundResource(R.drawable.button_org_big);
                    real_next_btn.setClickable(true);
                }
            }
        });

        mRealPayPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                name = real_name.getText().toString().trim();
                status = real_status.getText().toString().trim();
                mRealPayPassWordValue = mRealPayPassword.getText().toString().trim();
                if (status.equals("") || name.equals("") || mRealPayPassWordValue.equals("")) {
                    real_next_btn.setBackgroundResource(R.drawable.button_grauly_big);
                    real_next_btn.setClickable(false);
                } else {
                    real_next_btn.setBackgroundResource(R.drawable.button_org_big);
                    real_next_btn.setClickable(true);
                }
            }
        });

        mRealPayPassword.setKeyListener(new DigitsKeyListener() {
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

        real_next_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                try {
                    name = URLEncoder.encode(real_name.getText().toString().trim(), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                status = real_status.getText().toString().trim();
                if (name.equals("") || name == null) {
                    showToast("你的姓名不能为空");
                    return;
                }
                if (status.equals("") || status == null) {
                    showToast("你的身份证不能为空");
                    return;
                }
                mRealPayPassWordValue = mRealPayPassword.getText().toString().trim();
                if (TextUtils.isEmpty(mRealPayPassWordValue)) {
                    showToast("请输入交易密码");
                    return;
                }
                int realPayPasswordValueLength = mRealPayPassWordValue.length();
                if (realPayPasswordValueLength < 8) {
                    showToast("交易密码长度太短");
                    return;
                }
                if (realPayPasswordValueLength > 20) {
                    showToast("交易密码长度太长");
                    return;
                }
                startDataRequest();
            }
        });

        //显示或隐藏密码
        reg_light_icon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isHidden) {
                    //show the password
                    mRealPayPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    reg_light_icon.setBackgroundResource(R.drawable.eye_default_btn1);
                } else {
                    //hide the password
                    mRealPayPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
//                    reg_light_icon.setImageResource(R.drawable.eye_default_btn);
                    reg_light_icon.setBackgroundResource(R.drawable.eye_selected_btn1);
                }
                isHidden = !isHidden;
                mRealPayPassword.setSelection(mRealPayPassword.getText().length());
            }
        });

    }


    private class MyHandler extends Handler {
        public void handleMessage(Message paramMessage) {
            Bundle localBundle1 = paramMessage.getData();

            ArrayList<Parcelable> reg_list = localBundle1.getParcelableArrayList(BaseParam.URL_REQUEAT_MY_OPENYJFACCOUNT);

            if (null != reg_list) {
                HashMap<String, String> map = (HashMap<String, String>) reg_list.get(0);
                String resultCode = map.get("resultCode");

//                resultCode="1";  //这里强制修改数据,一会删掉

                if (resultCode.equals("1")) {
                    showToast("实名认证成功");
                    AppTool.getUserStatusInfoRequest();
                    SharedPreferences sp = NewRealAct.this.getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_USER, MODE_PRIVATE);
                    Editor editor = sp.edit();
                    editor.putString(BaseParam.QIAN_SHAREDPREFERENCES_USER_REALSTATUS, "1");
                    editor.putString(BaseParam.QIAN_SHAREDPREFERENCES_USER_PHONESTATUS, "1");
                    editor.putString(BaseParam.QIAN_SHAREDPREFERENCES_USER_REALNAME, real_name.getText().toString().trim());
                    editor.putString("cardId", map.get("cardId"));
                    editor.commit();
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putInt("finish", 1000);
                    intent.putExtras(bundle);
                    setResult(RESULT_OK, intent);
                    //发个广播给产品详情界面,让它自己刷新数据
                    Intent i1=new Intent("NewRealAct");
                    sendBroadcast(i1);
                    finish();
                } else {
//                    if (map.get("errorCode").equals("TOKEN_NOT_EXIST")) {
                        if ("TOKEN_NOT_EXIST".equals(map.get("errorCode"))) {
                        startActivity(new Intent(NewRealAct.this, Login.class));
                    } else if ("TOKEN_EXPIRED".equals(map.get("errorCode"))) {
                        startActivity(new Intent(NewRealAct.this, Login.class));
                    } else {
                        String code=map.get("errorCode");
                            if (code!=null){
                       showToast(Check.checkReturn(code));
                            }

//                        finish(); //测试的时候用,一会删掉
                    }
                }
            }
            NewRealAct.this.progressDialog.dismiss();
            super.handleMessage(paramMessage);
        }
    }

    private void startDataRequest() {
        NewRealAct.this.initArray();
        NewRealAct.this.param.add(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN);
        NewRealAct.this.value.add(NewRealAct.this.oauthToken);
        NewRealAct.this.param.add(BaseParam.QIAN_LOGIN_REALNAME);
        NewRealAct.this.value.add(NewRealAct.this.name);
        NewRealAct.this.param.add(BaseParam.QIAN_LOGIN_CARDID);
        NewRealAct.this.value.add(NewRealAct.this.status);
        // 新增idfa字段
        NewRealAct.this.param.add("idfa");
        NewRealAct.this.value.add(imei);
        NewRealAct.this.param.add("payPwd");
        NewRealAct.this.value.add(mRealPayPassWordValue);
        NewRealAct.this.param.add(BaseParam.URL_QIAN_API_APPID);
        NewRealAct.this.value.add(myApp.appId);
        NewRealAct.this.param.add(BaseParam.URL_QIAN_API_SERVICE);
        NewRealAct.this.value.add("certification1xV4");
        NewRealAct.this.param.add(BaseParam.URL_QIAN_API_SIGNTYPE);
        NewRealAct.this.value.add(myApp.signType);
        String[] array = new String[]{
                BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN + "=" + NewRealAct.this.oauthToken,
                "payPwd=" + mRealPayPassWordValue,
                BaseParam.QIAN_LOGIN_REALNAME + "=" + NewRealAct.this.name,
                BaseParam.QIAN_LOGIN_CARDID + "=" + status,
                "idfa=" + imei,
                BaseParam.URL_QIAN_API_APPID + "=" + myApp.appId,
                BaseParam.URL_QIAN_API_SERVICE + "=certification1xV4",
                BaseParam.URL_QIAN_API_SIGNTYPE + "=" + myApp.signType};

        String sign = apiModel.sortStringArray(array);
        NewRealAct.this.param.add(BaseParam.URL_QIAN_API_SIGN);
        NewRealAct.this.value.add(sign);

        NewRealAct.this.progressDialog = NewRealAct.this.dia.getLoginDialog(NewRealAct.this, "正在验证信息..");
        NewRealAct.this.progressDialog.show();
        new Thread(new JsonRequeatThreadNewReal(
                NewRealAct.this,
                myApp,
                NewRealAct.this.myHandler,
                NewRealAct.this.param,
                NewRealAct.this.value)
        ).start();
    }

    private void initArray() {
        this.param = null;
        this.value = null;
        this.param = new ArrayList();
        this.value = new ArrayList();
        System.gc();
    }

}
