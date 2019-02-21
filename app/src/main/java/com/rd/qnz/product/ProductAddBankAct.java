package com.rd.qnz.product;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
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
import com.rd.qnz.tools.webservice.JsonRequeatThreadBuyAddBank;
import com.rd.qnz.tools.webservice.JsonRequeatThreadBuyAddBankCard;
import com.rd.qnz.tools.webservice.JsonRequeatThreadOneMoneyBinCard;
import com.umeng.analytics.MobclickAgent;
import com.yintong.pay.utils.BaseHelper;
import com.yintong.pay.utils.Constants;
import com.yintong.pay.utils.MobileSecurePayer;
import com.yintong.pay.utils.ResultChecker;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

/**
 * 申购前绑卡
 *
 * @author Evonne
 */
public class ProductAddBankAct extends BaseActivity {

    private static final String TAG = "申购绑卡";

    private ArrayList<String> param = null;
    private ArrayList<String> value = null;
    private GetDialog dia;
    private MyHandler myHandler;
    private CustomProgressDialog progressDialog = null;
    private MyApplication myApp;
    APIModel apiModel = new APIModel();
    private String oauthToken = "";
    private Context context;
    private EditText yi_bank_number;//银行卡号
    private ImageView clear;
    private TextView toast_bank;
    private Button yi_btn;//确定
    private String bankCardNo;

    private TextView realName;
    private TextView realCard;
    private String real_name;
    private String card_no;

    // TODO: 2017/3/23
    private Timer timer = null;
    private int pay_type_flag = 1;
    private String orderNo;//订单号
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG); // 统计页面
        MobclickAgent.onResume(this); // 统计时长
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(TAG); // 保证 onPageEnd 在onPause 之前调用,因为 onPause
        // 中会保存信息
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_add_bank);
        this.dia = new GetDialog();
        myApp = (MyApplication) getApplication();
        this.myHandler = new MyHandler();
        context = ProductAddBankAct.this;
        SharedPreferences preferences = getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_USER, Context.MODE_PRIVATE);
        oauthToken = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN, "");
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
        actionbar_side_name.setText("填写卡号");
    }

    public void intView() {
        yi_bank_number = (EditText) findViewById(R.id.yi_bank_number);
        clear = (ImageView) findViewById(R.id.clear);
        toast_bank = (TextView) findViewById(R.id.toast_bank);
        yi_btn = (Button) findViewById(R.id.yi_btn);

        TextView textView = (TextView) findViewById(R.id.textview_koukuan);
        SpannableStringBuilder builder = new SpannableStringBuilder(textView.getText().toString());
        ForegroundColorSpan wSpan = new ForegroundColorSpan(getResources().getColor(R.color.home_text));
        ForegroundColorSpan wwSpan = new ForegroundColorSpan(getResources().getColor(R.color.home_text));
        builder.setSpan(wSpan, 5, 36, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(wwSpan, 40, 41, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(builder);

        realName = (TextView) findViewById(R.id.realName);
        realCard = (TextView) findViewById(R.id.realCard);

        clear.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                toast_bank.setVisibility(View.GONE);
                yi_bank_number.setText("");
                clear.setVisibility(View.GONE);
                yi_btn.setBackgroundResource(R.drawable.button_org_grauly);
                yi_btn.setClickable(false);
            }
        });
        yi_bank_number.addTextChangedListener(new TextWatcher() {

            @SuppressWarnings("deprecation")
            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
                String card = yi_bank_number.getText().toString().trim();
                if (card.equals("")) {
                    toast_bank.setVisibility(View.GONE);
                    clear.setVisibility(View.GONE);
                    yi_btn.setBackgroundResource(R.drawable.button_org_grauly);
                    yi_btn.setClickable(false);
                } else {
                    clear.setVisibility(View.VISIBLE);
                    toast_bank.setVisibility(View.VISIBLE);
                    yi_btn.setBackgroundResource(R.drawable.button_org_big);
                    yi_btn.setClickable(true);
                    if (card.length() <= 19) {
                        if (card.length() <= 4) {
                            toast_bank.setText(card);
                        } else if (card.length() <= 8) {
                            toast_bank.setText(card.substring(0, 4) + " "
                                    + card.substring(4, card.length()));
                        } else if (card.length() <= 12) {
                            toast_bank.setText(card.substring(0, 4) + " "
                                    + card.substring(4, 8) + " "
                                    + card.substring(8, card.length()));
                        } else if (card.length() <= 16) {
                            toast_bank.setText(card.substring(0, 4) + " "
                                    + card.substring(4, 8) + " " + card.substring(8, 12) + " "
                                    + card.substring(12, card.length()));
                        } else if (card.length() <= 20) {
                            toast_bank.setText(card.substring(0, 4) + " "
                                    + card.substring(4, 8) + " " + card.substring(8, 12) + " " + card.substring(12, 16) + " "
                                    + card.substring(16, card.length()));
                        } else {
                            toast_bank.setText(card.substring(0, 4) + " "
                                    + card.substring(4, 8) + " " + card.substring(8, 12) + " " + card.substring(12, 16) + " " + card.substring(16, 20) + " "
                                    + card.substring(20, card.length()));
                        }
                    } else {
                        toast_bank.setText(card.substring(0, card.length() - 1));
                        yi_bank_number.setText(card.substring(0, card.length() - 1));
                        yi_bank_number.setSelection(yi_bank_number.length());
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
        yi_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                bankCardNo = yi_bank_number.getText().toString().trim();
                if (bankCardNo.length() < 14) {
                    showToast("银行卡格式不正确");
                    return;
                }
                startDataRequestCommit();
            }
        });
        startDataRequest();
    }
    private Handler mHandler = createHandler();

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

                        if (Constants.RET_CODE_SUCCESS.equals(retCode)) {
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
                                retVal = ResultChecker.RESULT_CHECK_SIGN_SUCCEED;
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
                                    showToast("绑定成功");
                                    AppTool.getUserStatusInfoRequest();
                                    finish();

                                } else {
//                                    BaseHelper.showDialog(AddYiBankAct.this, "提示",
//                                            retMsg + "，交易状态码:" + retCode,
//                                            android.R.drawable.ic_dialog_alert);
                                }
                            } else {
                                // TODO 7、注意如果是使用MD5签名的去掉这里的验签判断。请参考TODO2和4的解释
//                                BaseHelper.showDialog(AddYiBankAct.this, "提示",
//                                        "您的订单信息已被非法篡改。",
//                                        android.R.drawable.ic_dialog_alert);
                            }
                        } else if (Constants.RET_CODE_PROCESS.equals(retCode)) {
                            // TODO 8、 支付处理中的情况
                            String resulPay = objContent.optString("result_pay");
                            if (Constants.RESULT_PAY_PROCESSING.equalsIgnoreCase(resulPay)) {
//                                BaseHelper.showDialog(AddYiBankAct.this, "提示",
//                                        objContent.optString("ret_msg") + "交易状态码："
//                                                + retCode,
//                                        android.R.drawable.ic_dialog_alert);
                            }

                        } else {
                            // TODO 9、 支付失败的情况
//                            BaseHelper.showDialog(AddYiBankAct.this, "提示", retMsg
//                                    + "，交易状态码:" + retCode,
//                                    android.R.drawable.ic_dialog_alert);
                        }
                    }
                    break;
                }
                super.handleMessage(msg);
            }
        };
    }
    @SuppressLint("HandlerLeak")
    private class MyHandler extends Handler {
        public void handleMessage(Message paramMessage) {
            Bundle localBundle1 = paramMessage.getData();

            ArrayList<Parcelable> real_list = localBundle1.getParcelableArrayList(BaseParam.QIAN_REQUEAT_REALINFOBACK);
            ArrayList<Parcelable> real_commin = localBundle1.getParcelableArrayList(BaseParam.QIAN_REQUEAT_ADDBANKCARD);
            ArrayList<Parcelable> oneMoneyBinCard = localBundle1
                    .getParcelableArrayList(BaseParam.QIAN_REQUEAT_ONEMONEYBINCARD);//一分钱绑卡接口
            if (null != real_list) {
                ProductAddBankAct.this.progressDialog.dismiss();
                @SuppressWarnings("unchecked")
                HashMap<String, String> map = (HashMap<String, String>) real_list.get(0);
                String resultCode = map.get("resultCode");
                if (resultCode.equals("1")) {
                    real_name = map.get("real_name");
                    card_no = map.get("card_no");
                    realName.setText("持卡人：" + real_name);
                    realCard.setText("身份证号：" + replaceChar(card_no));
                } else {
                    if (map.get("errorCode").equals("TOKEN_NOT_EXIST")) {
                        startActivity(new Intent(ProductAddBankAct.this, Login.class));
                    } else if (map.get("errorCode").equals("TOKEN_EXPIRED")) {
                        startActivity(new Intent(ProductAddBankAct.this, Login.class));
                    } else {
                        showToast(Check.checkReturn(map.get("errorCode")));
                    }
                }
            }
            if (null != real_commin) {
                ProductAddBankAct.this.progressDialog.dismiss();
                @SuppressWarnings("unchecked")
                HashMap<String, String> map = (HashMap<String, String>) real_commin.get(0);
                String resultCode = map.get("resultCode");
                if (resultCode.equals("1")) {
                    myApp.real_back = true;
                    myApp.uniqNo = map.get(BaseParam.QIAN_BANK_UNIQNO);
                    myApp.real_name = real_name;
                    myApp.hiddenCardNo = map.get(BaseParam.QIAN_BANK_HIDDENCARDNO);
                    myApp.bankId = map.get(BaseParam.QIAN_BANK_ID);
                    myApp.bank_name = map.get(BaseParam.QIAN_BANK_BANKSHORTNAME);
                    sendCardInfoToShenGou(
                            myApp.bankId,
                            map.get(BaseParam.QIAN_BANK_BANKSHORTNAME),
                            map.get(BaseParam.QIAN_BANK_BANKSHORTNAME),
                            map.get(BaseParam.QIAN_BANK_HIDDENCARDNO),
                            map.get(BaseParam.QIAN_BANK_REAL_USER_NAEM));

                    startDataRequestOnrMoneyBinCard();  //走一分钱绑卡这个接口
                } else {
                    if (map.get("errorCode").equals("TOKEN_NOT_EXIST")) {
                        startActivity(new Intent(ProductAddBankAct.this, Login.class));
                    } else if (map.get("errorCode").equals("TOKEN_EXPIRED")) {
                        startActivity(new Intent(ProductAddBankAct.this, Login.class));
                    } else {
                        showToast(Check.checkReturn(map.get("errorCode")));
                    }
                }
                ProductAddBankAct.this.finish();
            }
            if (null != oneMoneyBinCard) {
                Map<String, String> map = (Map<String, String>) oneMoneyBinCard.get(0);
                String resultCode = map.get("resultCode");
                if (resultCode.equals("1")) {
                    orderNo = map.get("orderNo");
                    if (timer != null) {
                        timer.cancel();  //将原任务从队列中移除
                    }
                    String content4Pay = map.get("paymentInfo");
                    MobileSecurePayer msp = new MobileSecurePayer();
                    boolean bRet = msp.payAuth(content4Pay, mHandler, Constants.RQF_PAY, ProductAddBankAct.this, false);
                } else {
                    if ((map.get("errorCode")).equals("TOKEN_NOT_EXIST")) {
                        startActivity(new Intent(context, Login.class));
                    } else if ((map.get("errorCode")).equals("TOKEN_EXPIRED")) {
                        startActivity(new Intent(context, Login.class));
                    } else {
                        showToast(Check.checkReturn(map.get("errorCode")));
                    }
                }
            }
            super.handleMessage(paramMessage);
        }
    }

    /**
     * 将信息发送到申购确认页
     */
    private void sendCardInfoToShenGou(String bankId, String bankName, String bankShortName, String hinddenCardNo, String realName) {
        Intent data = new Intent();
        data.putExtra("bankId", bankId);
        data.putExtra("bankName", bankName);
        data.putExtra("bankShortName", bankShortName);
        data.putExtra("hinddenCardNo", hinddenCardNo);
        data.putExtra("realName", realName);
        setResult(301, data);
    }

    /**
     * 一分钱绑卡这个接口
     */
    private void startDataRequestOnrMoneyBinCard() {
        initArray();

        param.add(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN);
        value.add(oauthToken);
        param.add("bankid");
        value.add(myApp.bankId);
        param.add("bankCard");
        value.add(bankCardNo);
        param.add(BaseParam.URL_QIAN_API_APPID);
        value.add(myApp.appId);
        param.add(BaseParam.URL_QIAN_API_SERVICE);
        value.add("oneMoneyBinCard");
        param.add(BaseParam.URL_QIAN_API_SIGNTYPE);
        value.add(myApp.signType);

        String[] array = new String[]{
                BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN + "=" + oauthToken,
                "bankid=" + myApp.bankId,
                "bankCard=" + bankCardNo,
                BaseParam.URL_QIAN_API_APPID + "=" + myApp.appId,
                BaseParam.URL_QIAN_API_SERVICE + "=oneMoneyBinCard",
                BaseParam.URL_QIAN_API_SIGNTYPE + "=" + myApp.signType};

        String sign = apiModel.sortStringArray(array);
        param.add(BaseParam.URL_QIAN_API_SIGN);
        value.add(sign);

        progressDialog = dia.getLoginDialog(ProductAddBankAct.this, "正在验证信息..");
        progressDialog.show();

        new Thread(new JsonRequeatThreadOneMoneyBinCard(
                context,
                myApp,
                this.myHandler,
                this.param,
                this.value)
        ).start();
    }
    /*
     走/api/account/addBankCard.html这个接口
     */
    private void startDataRequestCommit() {
        initArray();
        param.add(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN);
        value.add(oauthToken);
        param.add("bankCardNo");
        value.add(bankCardNo);
        param.add(BaseParam.URL_QIAN_API_APPID);
        value.add(myApp.appId);
        param.add(BaseParam.URL_QIAN_API_SERVICE);
        value.add("addBankCard");
        param.add(BaseParam.URL_QIAN_API_SIGNTYPE);
        value.add(myApp.signType);

        String[] array = new String[]{
                BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN + "=" + oauthToken,
                "bankCardNo=" + bankCardNo,
                BaseParam.URL_QIAN_API_APPID + "=" + myApp.appId,
                BaseParam.URL_QIAN_API_SERVICE + "=addBankCard",
                BaseParam.URL_QIAN_API_SIGNTYPE + "=" + myApp.signType};

        String sign = apiModel.sortStringArray(array);
        param.add(BaseParam.URL_QIAN_API_SIGN);
        value.add(sign);
        progressDialog = dia.getLoginDialog(ProductAddBankAct.this, "正在验证信息..");
        progressDialog.show();

        new Thread(new JsonRequeatThreadBuyAddBankCard(
                context,
                myApp,
                this.myHandler,
                this.param,
                this.value)
        ).start();
    }

    /**
     * 在绑定银行卡前得到用户的基本信息
     */
    private void startDataRequest() {
        initArray();
        param.add(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN);
        value.add(oauthToken);
        param.add(BaseParam.URL_QIAN_API_APPID);
        value.add(myApp.appId);
        param.add(BaseParam.URL_QIAN_API_SERVICE);
        value.add("realInfoBack");
        param.add(BaseParam.URL_QIAN_API_SIGNTYPE);
        value.add(myApp.signType);

        String[] array = new String[]{
                BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN + "=" + oauthToken,
                BaseParam.URL_QIAN_API_APPID + "=" + myApp.appId,
                BaseParam.URL_QIAN_API_SERVICE + "=realInfoBack",
                BaseParam.URL_QIAN_API_SIGNTYPE + "=" + myApp.signType};

        String sign = apiModel.sortStringArray(array);
        param.add(BaseParam.URL_QIAN_API_SIGN);
        value.add(sign);
        progressDialog = dia.getLoginDialog(ProductAddBankAct.this, "正在验证信息..");
        progressDialog.show();
        new Thread(new JsonRequeatThreadBuyAddBank(
                context,
                myApp,
                this.myHandler,
                this.param,
                this.value)
        ).start();
    }

    private static String replaceChar(String temp) {
        return temp.replace(temp.substring(4, temp.length() - 4), "**********");
    }

    private void initArray() {
        this.param = null;
        this.value = null;
        this.param = new ArrayList();
        this.value = new ArrayList();
        System.gc();
    }
}
