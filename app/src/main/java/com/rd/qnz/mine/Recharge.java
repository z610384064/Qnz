package com.rd.qnz.mine;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rd.qnz.BaseActivity;
import com.rd.qnz.R;
import com.rd.qnz.WebViewPayResultActivity;
import com.rd.qnz.custom.APIModel;
import com.rd.qnz.custom.MineShow;
import com.rd.qnz.custom.MyApplication;
import com.rd.qnz.custom.Profile;
import com.rd.qnz.dialog.CustomProgressDialog;
import com.rd.qnz.dialog.GetDialog;
import com.rd.qnz.login.Login;
import com.rd.qnz.tools.AppTool;
import com.rd.qnz.tools.BaseParam;
import com.rd.qnz.tools.Check;
import com.rd.qnz.tools.webservice.JsonRequeatThreadRechargeBalance;
import com.rd.qnz.tools.webservice.JsonRequeatThreadRechargeResult;
import com.rd.qnz.tools.webservice.JsonRequeatThreadTenderCancel;
import com.rd.qnz.tools.webservice.JsonRequeatThreadavailableRedPacket;
import com.umeng.analytics.MobclickAgent;
import com.yintong.pay.utils.BaseHelper;
import com.yintong.pay.utils.Constants;
import com.yintong.pay.utils.MobileSecurePayer;
import com.yintong.pay.utils.ResultChecker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 账户充值
 *
 * @author Evonne
 */
public class Recharge extends BaseActivity implements OnClickListener {
    private static final String TAG = "账户充值";
    private ArrayList<String> param = null;
    private ArrayList<String> value = null;
    private GetDialog dia;
    private MyHandler myHandler;  //在支付失败,申购可用红包的时候使用到
    private CustomProgressDialog progressDialog = null;
    private MyApplication myApp;
    private Context context;
    APIModel apiModel = new APIModel();

    private String oauthToken;
    private Double buy_money_yinhangka = 0.00;// 银行卡使用额度
    private EditText purchase_buy_money;//输入要申购的金额
    private TextView mBankCardName;// 银行卡名称
    private TextView payment_ceiling;// 银行卡名称

    private ImageView mBankIcon;
    private Button purchase_btn;
    private boolean isLogin = false;
    int count = 0;
    SharedPreferences preferences;
    private String bankCardList = "";
    private String allBankCardList = "";
    private List<Map<String, String>> all_BankCardList = new ArrayList<Map<String, String>>();
    private String mybankname;
    /*
     * 支付验证方式 0：标准版本， 1：卡前置方式，此两种支付方式接入时，只需要配置一种即可，Demo为说明用。可以在menu中选择支付方式。
     */
    private int pay_type_flag = 1;
    private Timer timer = null;
    private TimerTask task;
    private String orderNo;// 订单号
    private int resultDown = 3;
    private int resultDown1 = 3;
    private String jypassword = "";
    private String mBankId;// 银行卡id

    private RelativeLayout racharg_success_lay, recharg_fail_lay;
    private LinearLayout recharg_linearLayout;
    private TextView recharg_success_money;
    private Button recharg_konw_btn, balance_withdraw_fail_konw_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_recharge);
        context = Recharge.this;
        this.dia = new GetDialog();
        myApp = (MyApplication) getApplication();
        myApp.bankId = "";
        myApp.bank_name = "";
        this.myHandler = new MyHandler();
        preferences = getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_USER, Context.MODE_PRIVATE);  //sp_user
        oauthToken = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN, ""); //oauthToken
        initBar();
        initView();
    }

    /**
     * 弹出一个对话框,里面让你输入交易密码
     */
    private void showDialog() {//输入交易密码
        LayoutInflater inflaterDl = LayoutInflater.from(context);
        RelativeLayout layout = (RelativeLayout) inflaterDl.inflate(R.layout.jypassword_dialog_layout, null);
        final Dialog dialog = new AlertDialog.Builder(context).create();
        dialog.show();
        dialog.getWindow().setContentView(layout);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE;//显示dig的时候就显示软键盘
        params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;//就是这个属性导致不能获取焦点，默认是FLAG_NOT_FOCUSABLE
        dialog.getWindow().setAttributes(params);

        Button buttonOk = (Button) dialog.findViewById(R.id.confirm);
        Button buttonNo = (Button) dialog.findViewById(R.id.cancel);
        TextView forget_jypassword = (TextView) dialog.findViewById(R.id.forget_jypassword); //忘记密码
        buttonOk.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                EditText ed = (EditText) dialog.findViewById(R.id.login_password);
                jypassword = ed.getText().toString().trim();
                if (jypassword.equals("")) {
                    return;
                }
                dialog.dismiss();
                gotoPay();  //去支付

            }
        });

        buttonNo.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                dialog.dismiss();
            }
        });
        forget_jypassword.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                startActivity(new Intent(context, ForgetPasswordAct.class));
            }
        });
        dialog.show();
    }

    /* 银行卡list */
    private void initJsonBankCardList() {
        try {
            JSONArray bankList = new JSONArray(bankCardList);
            for (int i = 0; i < bankList.length(); i++) {
                JSONObject bank = new JSONObject(bankList.getString(i));
                Map<String, String> map = new HashMap<String, String>();
                map.put(BaseParam.QIAN_MY_BANK_ADDTIME, Check.jsonGetStringAnalysis(bank, BaseParam.QIAN_MY_BANK_ADDTIME));
                map.put(BaseParam.QIAN_MY_BANK_BANKCODE, Check.jsonGetStringAnalysis(bank, BaseParam.QIAN_MY_BANK_BANKCODE));
                map.put(BaseParam.QIAN_MY_BANK_BANKSHORTNAME, Check.jsonGetStringAnalysis(bank, BaseParam.QIAN_MY_BANK_BANKSHORTNAME));
                map.put(BaseParam.QIAN_MY_BANK_HIDDENCARDNO, Check.jsonGetStringAnalysis(bank, BaseParam.QIAN_MY_BANK_HIDDENCARDNO));
                map.put(BaseParam.QIAN_MY_BANK_ID, Check.jsonGetStringAnalysis(bank, BaseParam.QIAN_MY_BANK_ID));
                map.put(BaseParam.QIAN_MY_BANK_PERDAYLIMIT, Check.jsonGetStringAnalysis(bank, BaseParam.QIAN_MY_BANK_PERDAYLIMIT));
                map.put(BaseParam.QIAN_MY_BANK_PERDEALLIMIT, Check.jsonGetStringAnalysis(bank, BaseParam.QIAN_MY_BANK_PERDEALLIMIT));
                map.put(BaseParam.QIAN_MY_BANK_STATUS, Check.jsonGetStringAnalysis(bank, BaseParam.QIAN_MY_BANK_STATUS));
                map.put(BaseParam.QIAN_MY_BANK_UNIQNO, Check.jsonGetStringAnalysis(bank, BaseParam.QIAN_MY_BANK_UNIQNO));
                if (i == 0) {
                    mBankId = Check.jsonGetStringAnalysis(bank, BaseParam.QIAN_MY_BANK_ID);
                    mBankCardName.setText(
                            Check.jsonGetStringAnalysis(bank, BaseParam.QIAN_MY_BANK_BANKSHORTNAME)
                                    + "(尾号"
                                    + Check.jsonGetStringAnalysis(bank, BaseParam.QIAN_MY_BANK_HIDDENCARDNO)
                                    + ")"
                    );
                    mBankIcon.setImageResource(AppTool.BankIcon2(Check.jsonGetStringAnalysis(bank, BaseParam.QIAN_MY_BANK_BANKSHORTNAME)));
                    myApp.uniqNo = Check.jsonGetStringAnalysis(bank, BaseParam.QIAN_MY_BANK_UNIQNO);
                    mybankname = Check.jsonGetStringAnalysis(bank, BaseParam.QIAN_MY_BANK_BANKSHORTNAME);
                    return;
                }
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void initBar() {
        ImageView actionbar_side_left_iconfont = (ImageView) findViewById(R.id.actionbar_side_left_iconfont); //左边回退图片
        actionbar_side_left_iconfont.setVisibility(View.VISIBLE);
        actionbar_side_left_iconfont.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                finish();
            }
        });
        TextView actionbar_side_name = (TextView) findViewById(R.id.actionbar_side_name);
        actionbar_side_name.setText("账户充值");
        TextView text_use = (TextView) findViewById(R.id.text_use);
        text_use.setText("充值记录");
        LinearLayout linearLayout_use = (LinearLayout) findViewById(R.id.linearLayout_use);
        linearLayout_use.setVisibility(View.VISIBLE);
        linearLayout_use.setOnClickListener(this);
    }

    private void initView() {

        recharg_linearLayout = (LinearLayout) findViewById(R.id.recharg_linearLayout); //整一大块

        racharg_success_lay = (RelativeLayout) findViewById(R.id.racharg_success_lay);  //充值成功对应的布局,一开始是隐藏着的
        recharg_fail_lay = (RelativeLayout) findViewById(R.id.recharg_fail_lay);        //充值失败对应的布局,一开始是隐藏着的
        recharg_success_money = (TextView) findViewById(R.id.recharg_success_money);  //充值成功:金额
        recharg_konw_btn = (Button) findViewById(R.id.recharg_konw_btn);  //充值成功: 我知道了 按钮
        recharg_konw_btn.setOnClickListener(this);
        balance_withdraw_fail_konw_btn = (Button) findViewById(R.id.balance_withdraw_fail_konw_btn); //充值失败: 我知道了 按钮
        balance_withdraw_fail_konw_btn.setOnClickListener(this);

        mBankCardName = (TextView) findViewById(R.id.my_money_bank_name);   //卡名:建设银行(尾号6196)
        payment_ceiling = (TextView) findViewById(R.id.payment_ceiling);    //本卡日支付上限--元
        mBankIcon = (ImageView) findViewById(R.id.balance_withdraw_to_card_icon); //银行卡对应的图片
        purchase_buy_money = (EditText) findViewById(R.id.purchase_buy_money);  //请输入充值金额

        /* 确认去支付按钮 */
        purchase_btn = (Button) findViewById(R.id.purchase_btn);
        purchase_btn.setOnClickListener(this);

        purchase_buy_money.setInputType(InputType.TYPE_CLASS_NUMBER);
        // getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        // 对输入金额实时监听
        purchase_buy_money.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                String money = purchase_buy_money.getText().toString().trim();// 填写的投资金额

                if (money.equals("")) {
                    purchase_btn.setClickable(false);
                    purchase_btn.setBackgroundResource(R.drawable.button_org_grauly);
                } else {
                    int length=money.length();
                    if (length<3){ //位数小于两位直接不判断,大于两位的话需要判断一波
                        purchase_btn.setClickable(false);
                        purchase_btn.setBackgroundResource(R.drawable.button_org_grauly);
                    }else {

                        if (Integer.valueOf(money) < 100) {
                        purchase_btn.setClickable(false);
                        purchase_btn.setBackgroundResource(R.drawable.button_org_grauly);
                    } else {
                        purchase_btn.setClickable(true);
                        purchase_btn.setBackgroundResource(R.drawable.button_org_big);
                    }
                          }


                }

            }
        });
        startDataRequestRedpackets();      //	申购-可使用红包和银行卡

        task = new TimerTask() {
            @Override
            public void run() {
                // // TODO Auto-generated method stub
                Message message = new Message();
                message.what = 2;
                handler.sendMessage(message);
            }
        };

    }

    private Handler mHandler = createHandler();

    /**
     *   支付信息回调
     * @return
     */
    private Handler createHandler() {
        return new Handler() {
            public void handleMessage(Message msg) {
                String strRet = (String) msg.obj;
                switch (msg.what) {
                    case Constants.RQF_PAY: {

                        JSONObject objContent = BaseHelper.string2JSON(strRet);
                        String retCode = objContent.optString("ret_code");
                        String retMsg = objContent.optString("ret_msg");

                        // 先判断状态码，状态码为 成功或处理中 的需要 验签

                        if (Constants.RET_CODE_SUCCESS.equals(retCode)) {  //交易成功
                            ResultChecker resultChecker = new ResultChecker(strRet);
                        /*
                         * TODO 2、支付结果校验，注意校验分为MD5和RSA两种，是根据支付时的签名方式来的。
						 * ResultChecker
						 * 内部配置了签名key，请注意修改。另外如果是使用的MD5验签，MD5的key建议放到服务器
						 * ，这样会再次请求服务器,导致用户体验不好。所以
						 * 如果使用MD5的可以直接把SDK支付返回结果的校验去掉。前端直接提示SDK的结果就可以了
						 * 。后台会以我们异步通知为依据的。RSA验签可以将RSA_YT_PUBLIC银通的公钥配置到客户端。
						 */
                            int retVal;
                            // TODO 3、单独签约模式的返回结果不需要验证。
                            if (pay_type_flag == 1) {
                                retVal = ResultChecker.RESULT_CHECK_SIGN_SUCCEED;  //2
                            } else {
                                retVal = resultChecker.checkSign();
                            }

                            // TODO 4、注意如果是MD5 签名的去掉这里的验签判断。
                            if (retVal == ResultChecker.RESULT_CHECK_SIGN_SUCCEED) {
                                // TODO
                                // 5、卡前置模式返回的银行卡绑定协议号，用来下次支付时使用，此处仅作为示例使用。正式接入时去掉
                                if (pay_type_flag == 1) {

                                }

                                String resulPay = objContent.optString("result_pay");

                                if (Constants.RESULT_PAY_SUCCESS.equalsIgnoreCase(resulPay)) {
                                    // TODO 6、支付成功后续处理
                                    Log.i("6、支付成功后续处理  ", "");
                                    progressDialog = dia.getLoginDialog(Recharge.this, "正在验证信息..");
                                    progressDialog.show();
                                    handler.postDelayed(task, 3000);
                                } else {
                                    // BaseHelper.showDialog(
                                    // ProductContentPurchaseGaiAct.this,
                                    // "提示", retMsg + "，交易状态码:" + retCode,
                                    // android.R.drawable.ic_dialog_alert);
                                }
                            } else {
                                // TODO 7、注意如果是使用MD5签名的去掉这里的验签判断。请参考TODO2和4的解释
                                // BaseHelper.showDialog(
                                // ProductContentPurchaseGaiAct.this, "提示",
                                // "您的订单信息已被非法篡改。",
                                // android.R.drawable.ic_dialog_alert);
                            }
                        } else if (Constants.RET_CODE_PROCESS.equals(retCode)) {
                            // TODO 8、 支付处理中的情况
                            String resulPay = objContent.optString("result_pay");
                            if (Constants.RESULT_PAY_PROCESSING.equalsIgnoreCase(resulPay)) {
                                // BaseHelper.showDialog(
                                // ProductContentPurchaseGaiAct.this, "提示",
                                // objContent.optString("ret_msg") + "交易状态码："
                                // + retCode,
                                // android.R.drawable.ic_dialog_alert);
                            }
                        } else {
                            // TODO 9、 支付失败的情况
                            // BaseHelper.showDialog(
                            // ProductContentPurchaseGaiAct.this, "提示", retMsg
                            // + "，交易状态码:" + retCode,
                            // android.R.drawable.ic_dialog_alert);
                            startDataRequestCancel();
                        }
                    }
                    break;
                }
                super.handleMessage(msg);
            }
        };

    }


    /**
     * 充值到投资准备金 1.4.0
     */
    private void RechargeBalance(Double buyMoneyYinhangka) {
        if (Check.hasInternet(Recharge.this)) {
            initArray();
            param.add(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN);
            value.add(oauthToken);
            param.add("bankId");
            value.add(mBankId);//
            param.add("money");
            value.add(buyMoneyYinhangka + "");
            param.add(BaseParam.URL_QIAN_API_APPID);
            value.add(myApp.appId);
            param.add(BaseParam.URL_QIAN_API_SERVICE);
            value.add("rechargeBalance");
            param.add(BaseParam.URL_QIAN_API_SIGNTYPE);
            value.add(myApp.signType);
            param.add("paypwd");
            value.add(jypassword);
            String[] array = new String[]{
                    BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN + "=" + oauthToken,
                    "bankId=" + mBankId,
                    "money=" + buyMoneyYinhangka, "paypwd=" + jypassword,
                    BaseParam.URL_QIAN_API_APPID + "=" + myApp.appId,
                    BaseParam.URL_QIAN_API_SERVICE + "=rechargeBalance",
                    BaseParam.URL_QIAN_API_SIGNTYPE + "=" + myApp.signType};
            String sign = apiModel.sortStringArray(array);
            param.add(BaseParam.URL_QIAN_API_SIGN);
            value.add(sign);
            progressDialog = dia.getLoginDialog(Recharge.this, "正在验证信息..");
            progressDialog.show();
            new Thread(new JsonRequeatThreadRechargeBalance(
                    context,
                    myApp,
                    this.LiuHandler,
                    this.param,
                    this.value)
            ).start();
        } else {
            showToast("请检查网络连接是否正常");
        }
    }

    /**
     * 查询充值订单结果
     */
    private void CheckRechargeBalanceResult() {         // URL_QIAN_RECHARGE_RESULT
        if (Check.hasInternet(Recharge.this)) {
            initArray();
            param.add(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN);
            value.add(oauthToken);
            param.add("orderNo");
            value.add(orderNo);//
            param.add(BaseParam.URL_QIAN_API_APPID);
            value.add(myApp.appId);
            param.add(BaseParam.URL_QIAN_API_SERVICE);
            value.add("rechargeResult");
            param.add(BaseParam.URL_QIAN_API_SIGNTYPE);
            value.add(myApp.signType);
            String[] array = new String[]{
                    BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN + "=" + oauthToken,
                    "orderNo=" + orderNo,
                    BaseParam.URL_QIAN_API_APPID + "=" + myApp.appId,
                    BaseParam.URL_QIAN_API_SERVICE + "=rechargeResult",
                    BaseParam.URL_QIAN_API_SIGNTYPE + "=" + myApp.signType};
            String sign = apiModel.sortStringArray(array);
            param.add(BaseParam.URL_QIAN_API_SIGN);
            value.add(sign);
            progressDialog = dia.getLoginDialog(Recharge.this, "正在验证信息..");
            progressDialog.show();
            new Thread(new JsonRequeatThreadRechargeResult(
                    context,
                    myApp,
                    this.LiuHandler,
                    this.param,
                    this.value)
            ).start();

        } else {
            showToast("请检查网络连接是否正常");
        }
    }

    /**
     * 获取充值结果错误页的url
     */
    private String getRechargeBalanceResultUrl(String status) {
        initArray();
        param.add(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN);
        value.add(oauthToken);
        param.add("orderNo");
        value.add(orderNo);//
        param.add(BaseParam.URL_QIAN_API_APPID);
        value.add(myApp.appId);
        param.add(BaseParam.URL_QIAN_API_SERVICE);
        value.add("rechargeResult");
        param.add(BaseParam.URL_QIAN_API_SIGNTYPE);
        value.add(myApp.signType);
        String[] array;
        if (!TextUtils.isEmpty(status)) {
            param.add("status");
            value.add(status);
            array = new String[]{
                    BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN + "=" + oauthToken,
                    "orderNo=" + orderNo,
                    "status=" + status,
                    BaseParam.URL_QIAN_API_APPID + "=" + myApp.appId,
                    BaseParam.URL_QIAN_API_SERVICE + "=rechargeResult",
                    BaseParam.URL_QIAN_API_SIGNTYPE + "=" + myApp.signType
            };
        } else {
            array = new String[]{
                    BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN + "=" + oauthToken,
                    "orderNo=" + orderNo,
                    BaseParam.URL_QIAN_API_APPID + "=" + myApp.appId,
                    BaseParam.URL_QIAN_API_SERVICE + "=rechargeResult",
                    BaseParam.URL_QIAN_API_SIGNTYPE + "=" + myApp.signType
            };
        }

        String sign = apiModel.sortStringArray(array);
        param.add(BaseParam.URL_QIAN_API_SIGN);
        value.add(sign);
        StringBuffer strBuffer = new StringBuffer();
        strBuffer.append(BaseParam.URL_QIAN_RECHARGE_RESULT);
        strBuffer.append("?");
        for (int i = 0; i < value.size(); i++) {
            strBuffer.append(param.get(i));
            strBuffer.append("=");
            strBuffer.append(value.get(i));
            if (i < value.size() - 1) {
                strBuffer.append("&");
            }
        }
        return strBuffer.toString().trim();

    }

    private void gotoPay() {
        RechargeBalance(buy_money_yinhangka);
    }


    /**
     * 支付失败 订单取消
     */
    private void startDataRequestCancel() {
        if (Check.hasInternet(Recharge.this)) {
            initArray();
            param.add(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN);
            value.add(oauthToken);
            param.add("orderNo");
            value.add(orderNo);// 订单号

            param.add(BaseParam.URL_QIAN_API_APPID);
            value.add(myApp.appId);
            param.add(BaseParam.URL_QIAN_API_SERVICE);
            value.add("changTenderStatus");
            param.add(BaseParam.URL_QIAN_API_SIGNTYPE);
            value.add(myApp.signType);

            String[] array = new String[]{
                    BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN + "=" + oauthToken,
                    "orderNo=" + orderNo,
                    BaseParam.URL_QIAN_API_APPID + "=" + myApp.appId,
                    BaseParam.URL_QIAN_API_SERVICE + "=changTenderStatus",
                    BaseParam.URL_QIAN_API_SIGNTYPE + "=" + myApp.signType
            };

            String sign = apiModel.sortStringArray(array);
            param.add(BaseParam.URL_QIAN_API_SIGN);
            value.add(sign);

            progressDialog = dia.getLoginDialog(Recharge.this, "正在验证信息..");
            progressDialog.show();

            new Thread(new JsonRequeatThreadTenderCancel(
                    context,
                    myApp,
                    this.myHandler,
                    this.param,
                    this.value)
            ).start();

        } else {
            showToast("请检查网络连接是否正常");
        }

    }

    /**
     *  //	申购-可使用红包和银行卡,在点击充值进入这个界面后马上发一个请求查询当前账户信息,通过myHandler返回消息
     */
    private void startDataRequestRedpackets() {
        if (Check.hasInternet(Recharge.this)) {

            Recharge.this.initArray();

            Recharge.this.param.add("oauthToken");
            Recharge.this.value.add(oauthToken);

            Recharge.this.param.add(BaseParam.URL_QIAN_API_APPID);
            Recharge.this.value.add(myApp.appId);
            Recharge.this.param.add(BaseParam.URL_QIAN_API_SERVICE);
            Recharge.this.value.add("getPayInfo");
            Recharge.this.param.add(BaseParam.URL_QIAN_API_SIGNTYPE);
            Recharge.this.value.add(myApp.signType);
            String[] array = new String[]{
                    "oauthToken=" + oauthToken,
                    BaseParam.URL_QIAN_API_APPID + "=" + myApp.appId,
                    BaseParam.URL_QIAN_API_SERVICE + "=getPayInfo",
                    BaseParam.URL_QIAN_API_SIGNTYPE + "=" + myApp.signType
            };
            String sign = apiModel.sortStringArray(array);
            Recharge.this.param.add(BaseParam.URL_QIAN_API_SIGN);
            Recharge.this.value.add(sign);
            Recharge.this.progressDialog = Recharge
                    .this.dia.getLoginDialog(Recharge.this, "正在获取数据..");
            Recharge.this.progressDialog.show();
            new Thread(new JsonRequeatThreadavailableRedPacket(
                    Recharge.this, myApp,
                    Recharge.this.myHandler,
                    Recharge.this.param,
                    Recharge.this.value)
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

    protected void onDestroy() {
        super.onDestroy();
        if (null != progressDialog && progressDialog.isShowing()) {// 隐藏加载框
            Recharge.this.progressDialog.dismiss();
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            // 要做的事情
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    if (resultDown > 0) {
                        resultDown--;
                    } else {
                        if (timer != null) {
                            timer.cancel(); // 将原任务从队列中移除
                        }
                        showToast("服务器响应过慢,请稍后再试...");
                    }
                    break;
                case 2:
                    if (resultDown1 > 0) {
                        // startDataRequestResult();
                        resultDown1--;
                        CheckRechargeBalanceResult();
                    } else {
                        WebViewPayResultActivity.start(Recharge.this, "申购结果", getRechargeBalanceResultUrl("0"));
                        if (timer != null) {
                            timer.cancel(); // 将原任务从队列中移除
                        }
                        showToast("服务器响应过慢,请稍后再试...");
                    }
                    break;
            }
        }
    };

    /**
     * 发送请求到 充值到投资准备金  接口 等待服务器返回结果
     */
    private Handler LiuHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle localBundle1 = msg.getData();
            ArrayList<Parcelable> mRechargeBalance = localBundle1
                    .getParcelableArrayList(BaseParam.URL_QIAN_RECHARGE_BALANCE);// 充值
            ArrayList<Parcelable> mRechargeBalanceResult = localBundle1
                    .getParcelableArrayList(BaseParam.URL_QIAN_RECHARGE_RESULT);// 轮询结果


            if (null != mRechargeBalance) {  // 充值 获取和连连支付相关的数据
                Map<String, String> map = (Map<String, String>) mRechargeBalance.get(0);
                String resultCode = map.get("resultCode");
                if (TextUtils.equals(resultCode, "1")) {
                    cancelProgress();
                    String paymentInfo = map.get(BaseParam.QIAN_RECHARGE_BALANCE_PAYMENTINFO);
                    orderNo = map.get(BaseParam.QIAN_RECHARGE_BALANCE_NO_ORDER);
                    if (null != paymentInfo) {
                        MobileSecurePayer msp = new MobileSecurePayer();
                        boolean bRet = msp.payAuth(
                                paymentInfo,
                                mHandler,
                                Constants.RQF_PAY,
                                Recharge.this,
                                false
                        );
                    }
                } else {   //充值失败
                    cancelProgress();
                    String errorCode = map.get("errorCode");
                    if (null == errorCode) {
                        return;
                    }
                    if (errorCode.equals("TOKEN_NOT_EXIST")) {
                        showToast("身份证验证不存在,请重新登录");
                        isLogin = true;
                        startActivity(new Intent(Recharge.this, Login.class));

                    } else if (errorCode.equals("TOKEN_EXPIRED")) {
                        showToast("身份证验证不存在,请重新登录");
                        isLogin = true;
                        startActivity(new Intent(Recharge.this, Login.class));
                    } else {
                        showToast(errorCode);
                    }
                }
            }
            if (null != mRechargeBalanceResult) {  // 充值结果查询
                Map<String, String> map = (Map<String, String>) mRechargeBalanceResult.get(0);
                String resultCode = map.get("resultCode");
                if (TextUtils.equals(resultCode, "1")) {
                    cancelProgress();
                    String status = map.get(BaseParam.QIAN_RECHARGE_RESULT_STATUS);
                    if (TextUtils.equals(status, "0")) {// 充值失败，跳到结果页
                        showToast("充值失败");
                        recharg_linearLayout.setVisibility(View.GONE);
                        recharg_fail_lay.setVisibility(View.VISIBLE);
                        racharg_success_lay.setVisibility(View.GONE);
                        // WebViewAct.start(ProductContentPurchaseGaiAct.this,"申购结果", getRechargeBalanceResultUrl("0"));
                    } else if (TextUtils.equals(status, "1")) {
                        // buy_money_touzizhunbeijin = Double.valueOf(buy_money - buy_money_hongbao);// 充值成功后，更新实际投资准备金提交购买的 金额
                        // payProductByZhunBeiJin();// 充值成功
                        recharg_linearLayout.setVisibility(View.GONE);
                        recharg_fail_lay.setVisibility(View.GONE);
                        racharg_success_lay.setVisibility(View.VISIBLE);
                        recharg_success_money.setText("金额：" + buy_money_yinhangka + "元");
                        showToast("充值成功");
                        setResult(RESULT_OK);

                    } else if (TextUtils.equals(status, "2")) {
                        // TODO LIU 充值中，需要继续轮询
                        handler.postDelayed(task, 3000);
                    }
                } else {
                    cancelProgress();
                    String errorCode = map.get("errorCode");
                    if (null == errorCode) {
                        return;
                    }
                    if (errorCode.equals("TOKEN_NOT_EXIST")) {
                        showToast("身份证验证不存在,请重新登录");
                        isLogin = true;
                        startActivity(new Intent(Recharge.this, Login.class));

                    } else if (errorCode.equals("TOKEN_EXPIRED")) {
                        showToast("身份证验证不存在,请重新登录");
                        isLogin = true;
                        startActivity(new Intent(Recharge.this, Login.class));
                    } else {
                        showToast(errorCode);
                    }
                }
            }
        }
    };

    private void cancelProgress() {
        if (null != progressDialog && progressDialog.isShowing()) {// 隐藏加载框
            Recharge.this.progressDialog.dismiss();
        }
    }

    private class MyHandler extends Handler {
        public void handleMessage(Message paramMessage) {
            Bundle localBundle1 = paramMessage.getData();
            ArrayList<Parcelable> cancel_Result = localBundle1.getParcelableArrayList(BaseParam.QIAN_REQUEAT_CHANGTENDERSTATUS);//申购失败
            String result = localBundle1.getString(BaseParam.URL_REQUEAT_MY_AVAILABLEREDPACKET);//申购选择红包和银行卡
            if (null != result) {
                if (null != progressDialog && progressDialog.isShowing()) {// 隐藏加载框
                    Recharge.this.progressDialog.dismiss();
                }
                if (result.equals("unusual")) {
                    return;
                }
                try {
                    JSONObject oj = new JSONObject(result);
                    Log.i("红包和银行卡", result);
                    if (Check.jsonGetStringAnalysis(oj, "resultCode").equals("1")) {
                        JSONObject oj1 = new JSONObject(Check.jsonGetStringAnalysis(oj, "resultData"));
                        bankCardList = Check.jsonGetStringAnalysis(oj1, "bankCardList");
                        allBankCardList = Check.jsonGetStringAnalysis(oj1, "allBankCardList");//获取银行卡list
                        initJsonBankCardList();
                        initData();
                        initJsonAllBankCardList();
                    } else {
                        if (null == Check.jsonGetStringAnalysis(oj, "errorCode")) {
                            return;
                        }
                        if (Check.jsonGetStringAnalysis(oj, "errorCode").equals("TOKEN_NOT_EXIST")) {
                            showToast("身份证验证不存在,请重新登录");
                            isLogin = true;
                            startActivity(new Intent(Recharge.this, Login.class));

                        } else if (Check.jsonGetStringAnalysis(oj, "errorCode").equals("TOKEN_EXPIRED")) {
                            showToast("身份证验证不存在,请重新登录");
                            isLogin = true;
                            startActivity(new Intent(Recharge.this, Login.class));
                        } else {
                            showToast(Check.checkReturn(Check.jsonGetStringAnalysis(oj, "errorCode")));
                        }
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            if (null != cancel_Result) {
                cancelProgress();
                Map<String, String> map = (Map<String, String>) cancel_Result.get(0);
                String resultCode = map.get("resultCode");
                if (resultCode.equals("1")) {
                    if (map.get("success").equals("1")) {
                        showToast("支付取消");
                    }
                } else {
                    Check.checkMsg(map, Recharge.this);
                }
            }

            super.handleMessage(paramMessage);
        }
    }

    private void initData() {
        if (myApp == null) {
            myApp = MyApplication.getInstance();
        }

        if (isLogin) {
            oauthToken = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN, "");
            count = 0;
            if (oauthToken.equals("") || oauthToken == null) {
                finish();
            } else {
                startDataRequestRedpackets();
            }
        } else {
            if (!TextUtils.isEmpty(myApp.bankId)) {
                mBankId = myApp.bankId;
                String bankName = myApp.bank_name + " ( 尾号" + myApp.hiddenCardNo + " )";
                mBankCardName.setText(bankName);
            }

        }
    }

    @Override
    protected void onRestart() {
        // TODO Auto-generated method stub
        super.onRestart();

        initData();
    }

    private void initJsonAllBankCardList() {
        try {
            JSONArray jArray = new JSONArray(allBankCardList);
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject bank = jArray.getJSONObject(i);
                // Check.jsonGetStringAnalysis(bank, BaseParam.QIAN_ALL_BANK_PERDAYLIMIT));
                //   Check.jsonGetStringAnalysis(bank, BaseParam.QIAN_ALL_BANK_PERDEALLIMIT));
                if (Check.jsonGetStringAnalysis(bank, BaseParam.QIAN_ALL_BANK_BANKNAME).equals(mybankname)) {
                    payment_ceiling.setText("本卡日支付上限" + AppTool.zhuanhua(Check.jsonGetStringAnalysis(bank, BaseParam.QIAN_ALL_BANK_PERDAYLIMIT)) + "万元");
                    //payment_ceiling.setText("本卡日支付上限" + Check.jsonGetStringAnalysis(bank, BaseParam.QIAN_ALL_BANK_PERDEALLIMIT) + "元");
                }
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        int vId = v.getId();
        switch (vId) {
            case R.id.purchase_btn:  //确认充值按钮
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); //打开键盘
                // TODO Auto-generated method stub

                String money = purchase_buy_money.getText().toString().trim();
                if (money.equals("") || money == null) {
                    return;
                }
                buy_money_yinhangka = Double.parseDouble(money);
                resultDown = 3;
                resultDown1 = 3;
                if (TextUtils.equals(Profile.getUserPayPassWordStatus(), "0")) {   // 未设置交易密码
                    Intent intent = new Intent(Recharge.this, ForgetPasswordAct.class);
                    startActivity(intent);
                } else {
                    showDialog();// 先提示输入交易密码
                }
                break;
            case R.id.linearLayout_use://充值记录
                if (MineShow.fastClick()) {
                    RechargeRecordActivity.start(Recharge.this);
                }
                break;
            case R.id.recharg_konw_btn: //充值成功
                finish();
            case R.id.balance_withdraw_fail_konw_btn:
                finish();
                break;
            default:
                break;
        }
    }

}
