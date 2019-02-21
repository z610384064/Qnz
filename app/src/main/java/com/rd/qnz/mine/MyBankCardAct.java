package com.rd.qnz.mine;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rd.qnz.BaseActivity;
import com.rd.qnz.R;
import com.rd.qnz.WebViewAct;
import com.rd.qnz.custom.APIModel;
import com.rd.qnz.custom.MyApplication;
import com.rd.qnz.dialog.CustomProgressDialog;
import com.rd.qnz.dialog.GetDialog;
import com.rd.qnz.login.Login;
import com.rd.qnz.tools.AppTool;
import com.rd.qnz.tools.BaseParam;
import com.rd.qnz.tools.Check;
import com.rd.qnz.tools.webservice.JsonRequeatThreadMyBankGai;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 1.3.0 版本  我的银行卡
 *
 * @author Evonne
 */
public class MyBankCardAct extends BaseActivity implements OnClickListener {

    private Context context = MyBankCardAct.this;
    private String oauthToken;
    private MyBankListAdapter myBankListAdapter = null;

    private ArrayList<String> param = null;
    private ArrayList<String> value = null;
    private GetDialog dia;
    public static MyHandler myHandler;
    private CustomProgressDialog progressDialog = null;
    private MyApplication myApp;
    APIModel apiModel = new APIModel();
    private List<Map<String, String>> list = null;
    private TextView mAddBankInformationBtn;
    private TextView mBankName, mBankNumber, mBankUserName;
    private ImageView mBankIcon;
    private RelativeLayout mHaveBankCardLay, mNoBankCardLay;
    private String mBankCardId;

    public static void start(Context context) {
        Intent i = new Intent(context, MyBankCardAct.class);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mybankcard);
        this.dia = new GetDialog();
        myApp = (MyApplication) getApplication();
        this.myHandler = new MyHandler();
        //sp_user.xml
        SharedPreferences preferences = getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_USER, Context.MODE_PRIVATE);

        oauthToken = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN, "");
        initBar();
        intView();
    }

    @Override
    protected void onRestart() {
        // TODO Auto-generated method stub
        super.onRestart();
        Log.e("我的银行卡", "onRestart");
        startDataRequest();
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
        actionbar_side_name.setText("我的银行卡");
    }

    private void intView() {
        mHaveBankCardLay = (RelativeLayout) findViewById(R.id.my_bank_lay);  //我的银行卡界面(橙色背景)
        mNoBankCardLay = (RelativeLayout) findViewById(R.id.my_bank_no_lay);  //添加银行卡的背景(一旦已经添加了银行卡,这个界面就隐藏)
        mBankName = (TextView) findViewById(R.id.my_bank_name);  //银行名
        mBankUserName = (TextView) findViewById(R.id.my_bank_user_name); //持卡人姓名
        mBankNumber = (TextView) findViewById(R.id.my_bank_code); //银行卡号

        mBankIcon = (ImageView) findViewById(R.id.my_bank_icon); //银行商标
        mAddBankInformationBtn = (TextView) findViewById(R.id.my_bank_add_bank_information); //点击完善开户信息
        mAddBankInformationBtn.setOnClickListener(this);

        list = new ArrayList<Map<String, String>>();
        myBankListAdapter = new MyBankListAdapter(MyBankCardAct.this, list);
        mNoBankCardLay.setOnClickListener(this);
        startDataRequest();
    }


    private void startDataRequest() {
        MyBankCardAct.this.initArray();
        MyBankCardAct.this.param.add(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN);
        MyBankCardAct.this.value.add(MyBankCardAct.this.oauthToken);
        MyBankCardAct.this.param.add(BaseParam.URL_QIAN_API_APPID);
        MyBankCardAct.this.value.add(myApp.appId);
        MyBankCardAct.this.param.add(BaseParam.URL_QIAN_API_SERVICE);
        MyBankCardAct.this.value.add("myBank");
        MyBankCardAct.this.param.add(BaseParam.URL_QIAN_API_SIGNTYPE);
        MyBankCardAct.this.value.add(myApp.signType);

        String[] array = new String[]{
                BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN + "=" + MyBankCardAct.this.oauthToken,
                BaseParam.URL_QIAN_API_APPID + "=" + myApp.appId,
                BaseParam.URL_QIAN_API_SERVICE + "=myBank",
                BaseParam.URL_QIAN_API_SIGNTYPE + "=" + myApp.signType};

        String sign = apiModel.sortStringArray(array);
        MyBankCardAct.this.param.add(BaseParam.URL_QIAN_API_SIGN);
        MyBankCardAct.this.value.add(sign);

        MyBankCardAct.this.progressDialog = MyBankCardAct.this.dia.getLoginDialog(
                MyBankCardAct.this, "正在验证信息..");
        MyBankCardAct.this.progressDialog.show();
        new Thread(new JsonRequeatThreadMyBankGai(
                MyBankCardAct.this, myApp,
                MyBankCardAct.this.myHandler,
                MyBankCardAct.this.param,
                MyBankCardAct.this.value)
        ).start();
    }

    private void JsonList(String result) {
        if (result.equals("unusual")) {
            return;
        }
        try {
            JSONObject oj = new JSONObject(result);
            if (Check.jsonGetStringAnalysis(oj, "resultCode").equals("1")) {
                startDataRequest();

            } else {
                if (Check.jsonGetStringAnalysis(oj, "errorCode").equals("TOKEN_NOT_EXIST")) {
                    startActivity(new Intent(MyBankCardAct.this, Login.class));
                } else if (
                        Check.jsonGetStringAnalysis(oj, "errorCode").equals("TOKEN_EXPIRED")) {
                    startActivity(new Intent(MyBankCardAct.this, Login.class));
                } else {
                    showToast(Check.checkReturn(Check.jsonGetStringAnalysis(oj, "errorCode")));
                }
                return;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
    }

    /**
     * 加载银行卡信息
     */
    private void initMyBankInformation(Map<String, String> map) {
        String bankName = map.get(BaseParam.QIAN_BANK_BANKSHORTNAME);
        String bankNumber = map.get(BaseParam.QIAN_BANK_HIDDENCARDNO);
        String bankUserName = map.get(BaseParam.QIAN_BANK_REAL_USER_NAEM);
        mBankName.setText(bankName);
        mBankUserName.setText(bankUserName);
        mBankNumber.setText(bankNumber);
        mBankIcon.setImageResource(AppTool.BankIcon2(bankName));
        if (TextUtils.equals(map.get(BaseParam.QIAN_BANK_IS_NEED_ADD_INFORMATION), "1")) {
            mAddBankInformationBtn.setVisibility(View.VISIBLE);
        } else {
            mAddBankInformationBtn.setVisibility(View.INVISIBLE);
        }
        mBankCardId = map.get(BaseParam.QIAN_BANK_ID);
    }

    private void initArray() {
        this.param = null;
        this.value = null;
        this.param = new ArrayList();
        this.value = new ArrayList();
        System.gc();
    }

    @Override
    public void onClick(View v) {
        int vId = v.getId();
        switch (vId) {
            case R.id.my_bank_add_bank_information:  //完善银行卡信息(跳h5)
                // TODO 完善银行卡信息
                Intent intent = new Intent(context, WebViewAct.class);
                intent.putExtra("web_url", Wanshan());
                intent.putExtra("title", "完善银行卡信息");
                context.startActivity(intent);
                finish();
                break;
            case R.id.my_bank_no_lay:  //添加银行卡
                //TODO 去一分钱绑卡
                AddYiBankAct.start(MyBankCardAct.this);
                finish();
                break;
            default:
                break;
        }
    }

    public class MyHandler extends Handler {
        public void handleMessage(Message paramMessage) {
            Bundle localBundle1 = paramMessage.getData();
            ArrayList<Parcelable> myBank_list = localBundle1.getParcelableArrayList(BaseParam.URL_REQUEAT_MY_BANK);  //我的银行卡 对应的信息

            String default_list = localBundle1.getString(BaseParam.QIAN_REQUEAT_SETDEFAULTCARD);
            if (null != myBank_list) {
                List<Map<String, String>> list1 = (List<Map<String, String>>) myBank_list.get(0);
                String resultCode = list1.get(0).get("resultCode");
                if ("1".equals(resultCode)) {
                    // 有绑定银行卡
                    initMyBankInformation(list1.get(0));
                    mNoBankCardLay.setVisibility(View.GONE);
                    mHaveBankCardLay.setVisibility(View.VISIBLE);
                    myBankListAdapter.notifyDataSetChanged(list1);
                } else {
                    try {
                        if (list1.get(0).get("errorCode").equals("no_note")) {
                            mNoBankCardLay.setVisibility(View.VISIBLE);  //显示添加银行卡(带个加号的图片)
                            mHaveBankCardLay.setVisibility(View.GONE);   //隐藏银行卡背景
                        } else if (list.get(0).get("errorCode").equals("TOKEN_NOT_EXIST")) { //还没登陆
                            startActivity(new Intent(MyBankCardAct.this, Login.class));
                        } else if (list.get(0).get("errorCode").equals("TOKEN_EXPIRED")) {
                            startActivity(new Intent(MyBankCardAct.this, Login.class));
                        } else {
                            showToast(Check.checkReturn(list.get(0).get("errorCode")));
                        }
                    } catch (IndexOutOfBoundsException e) {
                        // TODO: handle exception
                    }
                }
            }
            if (null != default_list) {
                JsonList(default_list);
            }
            MyBankCardAct.this.progressDialog.dismiss();
            super.handleMessage(paramMessage);
        }
    }

    private String Wanshan() {
        list = myBankListAdapter.getInfoList();
        //sp_user.xml
        SharedPreferences preferences = context.getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_USER, Context.MODE_PRIVATE);
        String oauthToken = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN, ""); //oauthToken
        String url = BaseParam.URL_QIAN_EDITBANKINFO + "?" + "id=" + mBankCardId + "&" + "oauthToken=" + oauthToken;
        return url;
    }

}
