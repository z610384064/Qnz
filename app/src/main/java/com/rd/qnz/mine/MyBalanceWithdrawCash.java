package com.rd.qnz.mine;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rd.qnz.BaseActivity;
import com.rd.qnz.R;
import com.rd.qnz.WebViewAct;
import com.rd.qnz.custom.APIModel;
import com.rd.qnz.custom.MineShow;
import com.rd.qnz.custom.MyApplication;
import com.rd.qnz.dialog.CustomProgressDialog;
import com.rd.qnz.dialog.GetDialog;
import com.rd.qnz.tools.AppTool;
import com.rd.qnz.tools.BaseParam;
import com.rd.qnz.tools.Check;
import com.rd.qnz.tools.TextChangedListener;
import com.rd.qnz.tools.webservice.JsonRequeatThreadCommitWithdrawCash;
import com.rd.qnz.tools.webservice.JsonRequeatThreadGetWithdrowShowInfo;

import java.util.ArrayList;
import java.util.Map;

/**
 * 余额提现
 *
 * @author Evonne
 */
public class MyBalanceWithdrawCash extends BaseActivity implements OnClickListener {
    private TextView mCanWithdrawMoney;
    private EditText mWithdrawMoney, mPassWord;
    private TextView mForgetPWBtn, mMyBankName;
    private TextView mWithdrawSuccessMoney, mWithdrawSuccessBankName, mWithdrawFailBalanceMoney;

    private TextView mNoBankCardIntro;
    private View mNoBankCardIntroLay;
    private Button mGotoWithdrawBtn;
    private LinearLayout mWithdrawLay;
    private RelativeLayout mWithdrawSuccessLay, mWithdrawFailLay;
    private int mViewStatus;// 该页面的状态（申购，申购成功，申购失败）
    public static int VIEW_STATUS_BUY = 0;
    public static int VIEW_STATUS_SUCCESS = 1;
    public static int VIEW_STATUS_FAIL = 2;//
    public static String WITHDRAW_CASH_RESULT_SUCCESS = "1";
    public static String WITHDRAW_CASH_RESULT_FAIL = "0";
    private CustomProgressDialog mProgressDialog;
    private Map<String, String> mWithdrawMap;
    private String mBankId;
    private String mBankName; //银行姓名
    private String mBankShortNumber; //尾号
    private String mWithdrawMoneyValue;
    private String mBalance; //账户余额


    private ImageView mBankIcon;  //银行的图标
    private boolean mIsToAddBankInfo;// 是否完善银行卡信息
    private String tok = null;
    private TextView cash_text;
    private String currentCash;  //已提现次数

    public static void start(Context context, String currentMonthHasCash) {
        Intent intent = new Intent(context, MyBalanceWithdrawCash.class);
        intent.putExtra("currentMonthHasCash", currentMonthHasCash);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.balance_withdraw);
        Intent intent = getIntent();
        currentCash = intent.getStringExtra("currentMonthHasCash");
        IntentFilter filter=new IntentFilter("freshbank");
        registerReceiver(broadcastReceiver,filter);



        initView();
        getWithdrowInfo();
        showWithdrawView();
        new TextChangedListener(mWithdrawMoney);//只能输入2位小数
    }

    private void initView() {
        mWithdrawLay = (LinearLayout) findViewById(R.id.balance_withdraw_withdraw_base_lay);
        mWithdrawSuccessLay = (RelativeLayout) findViewById(R.id.balance_withdraw_withdraw_success_lay);
        mWithdrawFailLay = (RelativeLayout) findViewById(R.id.balance_withdraw_withdraw_fail_lay);
        mCanWithdrawMoney = (TextView) findViewById(R.id.balance_withdraw_can_withdraw);
        mWithdrawMoney = (EditText) findViewById(R.id.balance_withdraw_money_edittext);
        mPassWord = (EditText) findViewById(R.id.balance_withdraw_password_edittext);
        mForgetPWBtn = (TextView) findViewById(R.id.balance_withdraw_forget);
        mGotoWithdrawBtn = (Button) findViewById(R.id.balance_withdraw_bottom_btn);
        mMyBankName = (TextView) findViewById(R.id.balance_withdraw_to_card_name);
        mWithdrawSuccessMoney = (TextView) findViewById(R.id.balance_withdraw_success_money);
        mWithdrawSuccessBankName = (TextView) findViewById(R.id.balance_withdraw_success_bankcard);
        mWithdrawFailBalanceMoney = (TextView) findViewById(R.id.balance_withdraw_fail_money);
        mBankIcon = (ImageView) findViewById(R.id.balance_withdraw_to_card_icon);
        mGotoWithdrawBtn.setOnClickListener(this);
        TextView tv = (TextView) findViewById(R.id.actionbar_side_name);
        tv.setText("余额提现");
        ImageView mBackBtn = (ImageView) findViewById(R.id.actionbar_side_left_iconfont);
        mBackBtn.setVisibility(View.VISIBLE);
        mBackBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView text_use = (TextView) findViewById(R.id.text_use);
        text_use.setText("提现记录");
        LinearLayout linearLayout_use = (LinearLayout) findViewById(R.id.linearLayout_use);
        linearLayout_use.setVisibility(View.VISIBLE);
        linearLayout_use.setOnClickListener(this);
        mNoBankCardIntroLay = findViewById(R.id.balance_withdraw_no_real_lay); //完善银行卡信息
        findViewById(R.id.balance_withdraw_no_real_lay).setOnClickListener(this);
        findViewById(R.id.balance_withdraw_konw_btn).setOnClickListener(this);
        findViewById(R.id.balance_withdraw_fail_konw_btn).setOnClickListener(this);
        mForgetPWBtn.setOnClickListener(this);

        cash_text = (TextView) findViewById(R.id.cash_text);
        cash_text.setText(currentCash);
    }

    /**
     * 获取提现页信息
     */
    private void getWithdrowInfo() {
        if (Check.hasInternet(MyBalanceWithdrawCash.this)) {
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
            value.add("goWithdraw");
            param.add(BaseParam.URL_QIAN_API_SIGNTYPE);
            value.add(myApp.signType);
            String[] array = new String[]{
                    BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN + "=" + oauthToken,
                    BaseParam.URL_QIAN_API_APPID + "=" + myApp.appId,
                    BaseParam.URL_QIAN_API_SERVICE + "=goWithdraw",
                    BaseParam.URL_QIAN_API_SIGNTYPE + "=" + myApp.signType};

            String sign = apiModel.sortStringArray(array);
            param.add(BaseParam.URL_QIAN_API_SIGN);
            value.add(sign);

            if (mProgressDialog == null) {
                GetDialog dia = new GetDialog();
                mProgressDialog = dia.getLoginDialog(MyBalanceWithdrawCash.this, "正在获取数据..");
            }
            mProgressDialog.show();
            new Thread(new JsonRequeatThreadGetWithdrowShowInfo(
                    MyBalanceWithdrawCash.this,
                    myApp,
                    mHandler,
                    param,
                    value)
            ).start();
        } else {
            showToast("请检查网络连接是否正常");
        }
    }

    /**
     *  接收到广播刷新数据
     */
    private BroadcastReceiver broadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            getWithdrowInfo();
        }
    };
    /**
     * 提现
     */
    private void commitWithdrowCashDo() {

        String money = mWithdrawMoney.getText().toString().trim();
        String pwd = mPassWord.getText().toString().trim();
        if (TextUtils.isEmpty(pwd)) {
            showToast("密码不能为空");
            mGotoWithdrawBtn.setClickable(true);
            return;
        }
        if (TextUtils.isEmpty(money)) {
            showToast("请输入金额");
            mGotoWithdrawBtn.setClickable(true);
            return;
        }
        if (TextUtils.isEmpty(mBankId)) {
            showToast("请设置提现银行卡");
            mGotoWithdrawBtn.setClickable(true);
            return;
        }
        if (mIsToAddBankInfo) {
            showToast("请先完善银行卡信息");
            mGotoWithdrawBtn.setClickable(true);
            return;
        }
        mWithdrawMoneyValue = money;
        commitWithdrowCash(money, mBankId, pwd);
    }

    private void commitWithdrowCash(String money, String bankId, String pwd) {
        if (Check.hasInternet(MyBalanceWithdrawCash.this)) {
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
            value.add("withdrawBalance");
            param.add(BaseParam.URL_QIAN_API_SIGNTYPE);
            value.add(myApp.signType);
            param.add("money");
            value.add(money);
            param.add("bankId");
            value.add(bankId);
            param.add("pwd");
            value.add(pwd);
            Log.i("json格式", tok + "----123");
            param.add("tok");
            value.add(tok);
            String[] array = new String[]{
                    BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN + "=" + oauthToken,
                    BaseParam.URL_QIAN_API_APPID + "=" + myApp.appId,
                    BaseParam.URL_QIAN_API_SERVICE + "=withdrawBalance",
                    "money=" + money,
                    "bankId=" + bankId,
                    "pwd=" + pwd,
                    BaseParam.URL_QIAN_API_SIGNTYPE + "=" + myApp.signType,
                    "tok=" + tok};

            String sign = apiModel.sortStringArray(array);
            param.add(BaseParam.URL_QIAN_API_SIGN);
            value.add(sign);

            if (mProgressDialog == null) {
                GetDialog dia = new GetDialog();
                mProgressDialog = dia.getLoginDialog(MyBalanceWithdrawCash.this, "正在获取数据..");
            }
            mProgressDialog.show();
            new Thread(new JsonRequeatThreadCommitWithdrawCash(
                    MyBalanceWithdrawCash.this,
                    myApp,
                    mHandler,
                    param,
                    value)
            ).start();
        } else {
            showToast("请检查网络连接是否正常");
        }
    }

    Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            Bundle localBundle1 = msg.getData();
            ArrayList<Parcelable> product_Rlist = localBundle1
                    .getParcelableArrayList(BaseParam.URL_QIAN_BALANCE_WITHDROW_SHOW);//获取提现页的信息
            ArrayList<Parcelable> commitWithdrowResult = localBundle1
                    .getParcelableArrayList(BaseParam.URL_QIAN_BALANCE_WITHDROW_COMMIT);//获取余额提现 提交
            if (null != mProgressDialog && mProgressDialog.isShowing()) {// 隐藏加载框
                mProgressDialog.dismiss();
            }

            if (null != product_Rlist) {
                Map<String, String> list1 = (Map<String, String>) product_Rlist.get(0);
                Map<String, String> map = list1;
                Log.i("余额体现获取提现页的信息act", map.toString());
                String resultCode = map.get("resultCode");
                tok = map.get("tok");
                if (resultCode.equals("1")) {
                    mWithdrawMap = map;
                    updatePageData(map);
                } else {
                    String errorMsg = map.get("errorCode");
                    showToast(errorMsg);
                }
            }
            if (null != commitWithdrowResult) {
                Map<String, String> list1 = (Map<String, String>) commitWithdrowResult.get(0);
                Map<String, String> map = list1;
                String resultCode = map.get("resultCode");
                if (resultCode.equals("1")) {
                    String status = map.get(BaseParam.QIAN_BALANCE_WITHDROW_COMMIT_STATUS);
                    if (TextUtils.equals(WITHDRAW_CASH_RESULT_SUCCESS, status)) {// 提现成功
                        setResult(RESULT_OK);
                        showWithdrawSuccess();
                        updateWithDrowSuccessData();
                    } else if (TextUtils.equals(WITHDRAW_CASH_RESULT_FAIL, status)) {// 提现失败
                        showWithdrawFail();
                        updateWithDrowFailData();
                    }
                } else {
                    String errorMsg = map.get("errorCode");
                    showToast(errorMsg);
                }
                mGotoWithdrawBtn.setClickable(true);
            }
        }
    };

    @Override
    public void onClick(View v) {
        int vId = v.getId();
        switch (vId) {
            case R.id.balance_withdraw_forget:
                // 忘记交易密码
                if (MineShow.fastClick()) {
                    Intent intent = new Intent(MyBalanceWithdrawCash.this, ForgetPasswordAct.class);
                    startActivity(intent);
                }
                break;
            case R.id.balance_withdraw_bottom_btn:
                mGotoWithdrawBtn.setClickable(false);
                // 提现
                commitWithdrowCashDo();
                break;
            case R.id.balance_withdraw_no_real_lay:
                // 完善银行卡信息
                if (MineShow.fastClick()) {
                    MyBankCardAct.start(MyBalanceWithdrawCash.this);
                }
                break;
            case R.id.balance_withdraw_fail_konw_btn:
                finish();
                break;
            case R.id.balance_withdraw_konw_btn:
                finish();
                break;
            case R.id.linearLayout_use://提现记录
                if (MineShow.fastClick()) {
                    CrashRecordActivity.start(MyBalanceWithdrawCash.this);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 把银行卡数据传递进来更新一波界面
     * @param mapList
     */
    private void updatePageData(Map<String, String> mapList) {
        mBankName = mapList.get(BaseParam.QIAN_BALANCE_WITHDROW_SHOW_KEY_BANK_SHORT_NAME);
        mBankShortNumber = mapList.get(BaseParam.QIAN_BALANCE_WITHDROW_SHOW_KEY_HIDDEN_CARD_NO);
        mBalance = mapList.get(BaseParam.QIAN_BALANCE_WITHDROW_SHOW_KEY_BALANCE);
        Log.i("money", mBalance);
        String str = getString(R.string.balance_withdraw_bank_card_name_format, mBankName, mBankShortNumber); //建设银行 ( 尾号6196 ) 
        String money = getString(R.string.balance_withdraw_can_withdraw_money_format, mBalance + ""); //可提现金额：100.28 元
        if (TextUtils.isEmpty(mBankShortNumber)) {
            AddYiBankAct.start(MyBalanceWithdrawCash.this);
            showToast("请先绑定一张银行卡");
            finish();
            return;
        }
        if (TextUtils.equals(mapList.get(BaseParam.QIAN_BALANCE_WITHDROW_SHOW_IS_NEED), "1")) {  //是否需要完善银行卡信息 1代表需要完善
            mIsToAddBankInfo = true;
            //MyBankCardAct.start(MyBalanceWithdrawCash.this);
            //showToast("请先完善银行卡开户信息");
            //finish();
        } else {
            mIsToAddBankInfo = false;
        }
        mCanWithdrawMoney.setText(money);
        mMyBankName.setText(str);
        mBankIcon.setImageResource(AppTool.BankIcon2(mBankName));

        mBankId = mapList.get(BaseParam.QIAN_BALANCE_WITHDROW_SHOW_KEY_ID);
        boolean isNeedBankCardInfo = Boolean.parseBoolean(mapList.get(BaseParam.QIAN_BALANCE_WITHDROW_SHOW_IS_NEED));
        if (mIsToAddBankInfo) {
            mNoBankCardIntroLay.setVisibility(View.VISIBLE);

            LayoutInflater inflaterDl = LayoutInflater.from(MyBalanceWithdrawCash.this);
            LinearLayout layout = (LinearLayout) inflaterDl.inflate(R.layout.dialog_addbankinfo_layout, null);
            final Dialog dialog = new AlertDialog.Builder(MyBalanceWithdrawCash.this).create();
            dialog.show();
            dialog.getWindow().setContentView(layout);
            Button cancel = (Button) layout.findViewById(R.id.negativeButton);
            Button sure = (Button) layout.findViewById(R.id.positiveButton);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            sure.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    SharedPreferences preferences = getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_USER, Context.MODE_PRIVATE);
                    String oauthToken = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN, ""); //oauthToken
                    String url = BaseParam.URL_QIAN_EDITBANKINFO + "?" + "id=" + mBankId + "&" + "oauthToken=" + oauthToken;

                    Intent intent = new Intent(MyBalanceWithdrawCash.this, WebViewAct.class);
                    intent.putExtra("web_url", url);
                    intent.putExtra("title", "完善银行卡信息");
                    startActivity(intent);

                }
            });
        } else {

            mNoBankCardIntroLay.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    /**
     * 更新提现成功页面数据
     */
    private void updateWithDrowSuccessData() {
        String str = getString(R.string.balance_withdraw_success_to_bankcard_format, mBankName, mBankShortNumber);
        String money = getString(R.string.balance_withdraw_success_money_format, mWithdrawMoneyValue + "");
        mWithdrawSuccessMoney.setText(money);
        mWithdrawSuccessBankName.setText(str);
    }

    private void updateWithDrowFailData() {
        String balanceStr = getString(R.string.balance_withdraw_fail_balance_money_format, mBalance);
        mWithdrawFailBalanceMoney.setText(balanceStr);
    }

    /**
     * 展示余额提现页
     */
    private void showWithdrawView() {
        mViewStatus = VIEW_STATUS_BUY;
        updateView();
    }

    /**
     * 展示余额提现成功结果
     */
    private void showWithdrawSuccess() {
        mViewStatus = VIEW_STATUS_SUCCESS;
        updateView();
    }

    /**
     * 提现失败
     */
    private void showWithdrawFail() {
        mViewStatus = VIEW_STATUS_FAIL;
        updateView();
    }

    private void updateView() {
        if (mViewStatus == VIEW_STATUS_BUY) {
            HindOrShowWithdraw(true);
            HindOrShowWithdrawFail(false);
            HindOrShowWithdrawSuccess(false);
        } else if (mViewStatus == VIEW_STATUS_SUCCESS) {
            HindOrShowWithdraw(false);  //提现界面隐藏
            HindOrShowWithdrawFail(false);  //提现失败界面隐藏
            HindOrShowWithdrawSuccess(true); //提现成功界面显示
        } else if (mViewStatus == VIEW_STATUS_FAIL) {
            HindOrShowWithdraw(false);
            HindOrShowWithdrawFail(true);
            HindOrShowWithdrawSuccess(false);
        }

    }

    /**
     * 是否显示提现输入框
     *
     * @param isShow
     */
    private void HindOrShowWithdraw(boolean isShow) {
        if (isShow) {
            mWithdrawLay.setVisibility(View.VISIBLE);
        } else {
            mWithdrawLay.setVisibility(View.GONE); //提现界面隐藏
        }
    }

    /**
     * 是否显示提现成功
     *
     * @param isShow
     */
    private void HindOrShowWithdrawSuccess(boolean isShow) {
        if (isShow) {
            mWithdrawSuccessLay.setVisibility(View.VISIBLE);
        } else {
            mWithdrawSuccessLay.setVisibility(View.GONE);
        }
    }

    /**
     * 是否显示提现失败
     *
     * @param isShow
     */
    private void HindOrShowWithdrawFail(boolean isShow) {
        if (isShow) {
            mWithdrawFailLay.setVisibility(View.VISIBLE);
        } else {
            mWithdrawFailLay.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e("余额提现", "onRestart");
        getWithdrowInfo();
        showWithdrawView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (broadcastReceiver != null) {
            try{
                unregisterReceiver(broadcastReceiver);
            }catch (Exception e){
                e.printStackTrace();
            }

        }

    }
}
