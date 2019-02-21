package com.rd.qnz.mine;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rd.qnz.BaseActivity;
import com.rd.qnz.R;
import com.rd.qnz.WebViewAct;
import com.rd.qnz.custom.APIModel;
import com.rd.qnz.custom.MineShow;
import com.rd.qnz.custom.MyApplication;
import com.rd.qnz.custom.Profile;
import com.rd.qnz.dialog.CustomProgressDialog;
import com.rd.qnz.dialog.GetDialog;
import com.rd.qnz.login.Login;
import com.rd.qnz.product.ProductContentAct;
import com.rd.qnz.tools.AppTool;
import com.rd.qnz.tools.BaseParam;
import com.rd.qnz.tools.Check;
import com.rd.qnz.tools.webservice.JsonRequeatThreadUseMoneyDetial;
import com.umeng.analytics.MobclickAgent;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Map;

import static com.rd.qnz.custom.Profile.TAG_USER_STATUE_NAME;

/**
 * 投资概况
 *
 * @author Evonne
 */
public class MyuseMoneyDetailAct extends BaseActivity {

    private static final String TAG = "投资概况";
    private ArrayList<String> param = null;
    private ArrayList<String> value = null;

    private MyHandler myHandler;
    private CustomProgressDialog progressDialog = null;
    private MyApplication myApp;
    private Context context;
    APIModel apiModel = new APIModel();

    private String oauthToken;
    private String tenderId;
    private String borrowId;
    private String bankid;

    private TextView detail_status;
    private RelativeLayout detail_rel_bg;
    private TextView detail_name;
    private TextView detail_tender_time;
    private TextView detail_tender_money;
    private TextView detail_rate;
    private TextView detail_benjin;
    private TextView detail_rate_money;  //待收基本收益

    private TextView detail_repay_time;
    private TextView detail_repay_address;

    DecimalFormat df = new DecimalFormat("0.00");
    private RelativeLayout bank_perfect_rel;
    private TextView detail_xieyi;
    private TextView detail_product;

    /**
     *   加息收益
     * @param savedInstanceState
     */
    private RelativeLayout rl_jiaxi;
    private TextView tv_extraAward; //待收额外加息
    private TextView tv_interest; //待收基本利息
    private TextView tv_jiaxi_money; //待收加息收益
    private Double extraAward ;//额外加息收益
    private Double interest; //总的收益

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myhome_usemoney_detail);
        myApp = (MyApplication) getApplication();
        this.myHandler = new MyHandler();
        context = MyuseMoneyDetailAct.this;
        tenderId = getIntent().getStringExtra("tenderId");
        oauthToken = getIntent().getStringExtra("oauthToken");
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
        actionbar_side_name.setText("投资概况");
    }

    public void intView() {
        // TODO: 2017/4/20 0020
        rl_jiaxi= (RelativeLayout) findViewById(R.id.rl_jiaxi);
        tv_jiaxi_money= (TextView) findViewById(R.id.tv_jiaxi_money);
        tv_extraAward= (TextView) findViewById(R.id.tv_extraAward);
        tv_interest= (TextView) findViewById(R.id.tv_interest);

        detail_status = (TextView) findViewById(R.id.detail_status);
        detail_rel_bg = (RelativeLayout) findViewById(R.id.detail_rel_bg);
        detail_name = (TextView) findViewById(R.id.detail_name);

        detail_tender_time = (TextView) findViewById(R.id.detail_tender_time);
        detail_tender_money = (TextView) findViewById(R.id.detail_tender_money);
        detail_rate = (TextView) findViewById(R.id.detail_rate);
        detail_benjin = (TextView) findViewById(R.id.detail_benjin);
        detail_rate_money = (TextView) findViewById(R.id.detail_rate_money);



        detail_repay_time = (TextView) findViewById(R.id.detail_repay_time);
        detail_repay_address = (TextView) findViewById(R.id.detail_repay_address);
        detail_xieyi = (TextView) findViewById(R.id.detail_xieyi);
        detail_xieyi.setOnClickListener(new OnClickListener() {  //协议查看
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                if (MineShow.fastClick()) {

                    Intent intent = new Intent(context, WebViewAct.class);
                    intent.putExtra("web_url", startWebViewProtoclforHtml());
                    intent.putExtra("title", "协议查看");
                    startActivity(intent);
                }

            }
        });
        detail_product = (TextView) findViewById(R.id.detail_product);
        detail_product.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {  //项目详情
                // TODO Auto-generated method stub
                if (MineShow.fastClick()) {
                    //ProductDetailWebViewActivity.start(context, borrowId);
                    Intent intent = new Intent(MyuseMoneyDetailAct.this, ProductContentAct.class);
                    intent.putExtra(BaseParam.QIAN_PRODUCT_BORROWID, borrowId);
                    startActivity(intent);
                }
            }
        });
        //完善银行卡信息
        bank_perfect_rel = (RelativeLayout) findViewById(R.id.bank_perfect_rel);
        bank_perfect_rel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                if (MineShow.fastClick()) {
                    Intent intent = new Intent(context, WebViewAct.class);
                    intent.putExtra("web_url", Wanshan());
                    intent.putExtra("title", "完善银行卡信息");
                    context.startActivity(intent);
                }
            }
        });
        startDataRequest();
    }
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG); // 统计页面
        MobclickAgent.onResume(this); // 统计时长
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(TAG); // 保证 onPageEnd 在onPause 之前调用,因为 onPause中会保存信息
        MobclickAgent.onPause(this);
    }

    private class MyHandler extends Handler {
        @SuppressLint("ResourceAsColor")
        public void handleMessage(Message paramMessage) {
            Bundle localBundle1 = paramMessage.getData();

            if (null != progressDialog && progressDialog.isShowing()) {// 隐藏加载框
                MyuseMoneyDetailAct.this.progressDialog.dismiss();
            }
            ArrayList<Parcelable> detail = localBundle1.getParcelableArrayList(BaseParam.QIAN_REQUEAT_USEMONEYDETAIL);
            if (null != detail) {
                Map<String, String> map = (Map<String, String>) detail.get(0);
                String resultCode = map.get("resultCode");
                if (resultCode.equals("1")) {

                    if (map.get("status").equals("已还款")){
                        detail_status.setText("已还款");
                        tv_interest.setText("基本收益");
                        tv_extraAward.setText("加息收益");
                        detail_rel_bg.setBackgroundColor(Color.parseColor("#fc7946"));
                    }else {
                        detail_status.setText("还款中");
                        tv_interest.setText("待收基本收益");
                        tv_extraAward.setText("待收加息收益");
                        detail_rel_bg.setBackgroundColor(Color.parseColor("#77adf3"));
                    }

                    borrowId = (map.get(BaseParam.QIAN_USEMONEY_BORROWID));
                    detail_name.setText(map.get(BaseParam.QIAN_USEMONEY_BORROWNAME));

                    detail_tender_time.setText(AppTool.getMsgTwoDateDistance(map.get(BaseParam.QIAN_USEMONEY_ADDTIME)));
                    detail_tender_money.setText(df.format(Double.parseDouble(map.get(BaseParam.QIAN_USEMONEY_ACCOUNT))));
                    detail_rate.setText(df.format(Double.parseDouble(map.get(BaseParam.QIAN_USEMONEY_APR))) + "%");
                    detail_benjin.setText(df.format(Double.parseDouble(map.get(BaseParam.QIAN_USEMONEY_ACCOUNT))));


                    // TODO: 2017/3/30 0030    在这里设置待收加息收益的值
                    extraAward=Double.parseDouble(map.get(BaseParam.QIAN_USEMONEY_EXTRAAWARD));
                    interest=Double.parseDouble(map.get(BaseParam.QIAN_USEMONEY_INTEREST));
                    detail_rate_money.setText("+" + df.format(Math.abs(interest-extraAward)));

                    if (extraAward!=0){ //加息过的

                        tv_jiaxi_money.setText("+" + df.format(Math.abs(Double.parseDouble(map.get(BaseParam.QIAN_USEMONEY_EXTRAAWARD)))));
                    }else {
                        rl_jiaxi.setVisibility(View.GONE);
                    }

                    detail_repay_time.setText(AppTool.getMsgTwoDateDistance1(map.get(BaseParam.QIAN_USEMONEY_REPAYMENTTIME)));
                    detail_repay_address.setText(map.get(BaseParam.QIAN_USEMONEY_BACKPLACE));
                    if ("1".equals(map.get(BaseParam.QIAN_USEMONEY_ISNEED))) {
                        bank_perfect_rel.setVisibility(View.VISIBLE);
                    } else {
                        bank_perfect_rel.setVisibility(View.GONE);
                    }
                    bankid = map.get("bankid");
                } else {
                    showToast(Check.checkReturn(map.get("errorCode")));
                    Intent i=new Intent(MyuseMoneyDetailAct.this, Login.class);
                    startActivity(i);
                    loginOut();
                    setResult(1); //告诉我的投资界面,让它关闭
                    finish();
                }
            }
            super.handleMessage(paramMessage);
        }
    }
    //退出登录
    private void loginOut() {
        Context ctx = MyuseMoneyDetailAct.this;
        SharedPreferences sp = ctx.getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_USER, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
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
        editor.putString(BaseParam.QIAN_SHAREDPREFERENCES_USER_PHONE, "");
        editor.putString(BaseParam.QIAN_MY_AVAYARURL, "");
        editor.putString("cardId", "");
        editor.putString("cardExist", "");
        editor.putBoolean("fingerconfirm", false);
        Profile.setUserShare("");
        editor.commit();


        SharedPreferences sp1 = getSharedPreferences(TAG_USER_STATUE_NAME, MODE_PRIVATE);
        sp1.edit().putString(BaseParam.QIAN_USER_STATUS_INFO_NEWHAND_STATUS,"").commit(); //清空是否是新手


        sendBroadcast(new Intent("loginout"));

        myApp.tabHostId = 3;
        myApp.tabHost.setCurrentTab(myApp.tabHostId);

    }
    /* 协议查看URL */
    private String startWebViewProtoclforHtml() {
        String[] array = new String[]{
                "id=" + tenderId,
                BaseParam.QIAN_PRODUCT_BORROWID + "=" + borrowId + "",
                "oauthToken=" + oauthToken + "",
                BaseParam.URL_QIAN_API_APPID + "=" + myApp.appId,
                BaseParam.URL_QIAN_API_SERVICE + "=protoclforHtml",
                BaseParam.URL_QIAN_API_SIGNTYPE + "=" + myApp.signType};

        String sign = apiModel.sortStringArray(array);

        String url = BaseParam.URL_QIAN_PROTOCLFORHTML + "?"
                + "id=" + tenderId + "&"
                + BaseParam.QIAN_PRODUCT_BORROWID + "=" + borrowId + "&"
                + "oauthToken=" + oauthToken + "&"
                + BaseParam.URL_QIAN_API_APPID + "=" + myApp.appId + "&"
                + BaseParam.URL_QIAN_API_SERVICE + "=protoclforHtml&"
                + BaseParam.URL_QIAN_API_SIGNTYPE + "=" + myApp.signType + "&"
                + BaseParam.URL_QIAN_API_SIGN + "=" + sign;

        return url;
    }

    private void startDataRequest() {
        if (Check.hasInternet(MyuseMoneyDetailAct.this)) {
            MyuseMoneyDetailAct.this.initArray();
            MyuseMoneyDetailAct.this.param.add(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN);
            MyuseMoneyDetailAct.this.value.add(oauthToken);
            MyuseMoneyDetailAct.this.param.add(BaseParam.QIAN_USEMONEY_TENDERID);
            MyuseMoneyDetailAct.this.value.add(tenderId);

            MyuseMoneyDetailAct.this.param.add(BaseParam.URL_QIAN_API_APPID);
            MyuseMoneyDetailAct.this.value.add(myApp.appId);
            MyuseMoneyDetailAct.this.param.add(BaseParam.URL_QIAN_API_SERVICE);
            MyuseMoneyDetailAct.this.value.add("myTenderRecordDetail");
            MyuseMoneyDetailAct.this.param.add(BaseParam.URL_QIAN_API_SIGNTYPE);
            MyuseMoneyDetailAct.this.value.add(myApp.signType);

            String[] array = new String[]{
                    BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN + "=" + oauthToken,
                    BaseParam.QIAN_USEMONEY_TENDERID + "=" + tenderId,
                    BaseParam.URL_QIAN_API_APPID + "=" + myApp.appId,
                    BaseParam.URL_QIAN_API_SERVICE + "=myTenderRecordDetail",
                    BaseParam.URL_QIAN_API_SIGNTYPE + "=" + myApp.signType};

            String sign = apiModel.sortStringArray(array);
            MyuseMoneyDetailAct.this.param.add(BaseParam.URL_QIAN_API_SIGN);
            MyuseMoneyDetailAct.this.value.add(sign);

            GetDialog dia = new GetDialog();
            progressDialog = dia.getLoginDialog(MyuseMoneyDetailAct.this, "正在获取数据..");
            progressDialog.show();

            new Thread(new JsonRequeatThreadUseMoneyDetial(
                    MyuseMoneyDetailAct.this, myApp,
                    MyuseMoneyDetailAct.this.myHandler,
                    MyuseMoneyDetailAct.this.param,
                    MyuseMoneyDetailAct.this.value)
            ).start();
        } else {
            showToast("请检查网络连接是否正常");
        }

    }

    private String Wanshan() {
        SharedPreferences preferences = context.getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_USER, Context.MODE_PRIVATE);
        String oauthToken = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN, "");
        String url = BaseParam.URL_QIAN_EDITBANKINFO + "?" + "id=" + bankid + "&" + "oauthToken=" + oauthToken;
        return url;
    }

    private void initArray() {
        this.param = null;
        this.value = null;
        this.param = new ArrayList();
        this.value = new ArrayList();
        System.gc();
    }

}
