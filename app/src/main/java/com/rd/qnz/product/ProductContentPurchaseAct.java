package com.rd.qnz.product;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.text.Editable;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.rd.qnz.mine.AddYiBankAct;
import com.rd.qnz.mine.ForgetPasswordAct;
import com.rd.qnz.mine.NewRealAct;
import com.rd.qnz.tools.AppTool;
import com.rd.qnz.tools.BaseParam;
import com.rd.qnz.tools.Check;
import com.rd.qnz.tools.webservice.JsonRequeatThreadCheckPayProductResult;
import com.rd.qnz.tools.webservice.JsonRequeatThreadPayProduct;
import com.rd.qnz.tools.webservice.JsonRequeatThreadRechargeBalance;
import com.rd.qnz.tools.webservice.JsonRequeatThreadRechargeResult;
import com.rd.qnz.tools.webservice.JsonRequeatThreadTenderCancel;
import com.rd.qnz.tools.webservice.JsonRequeatThreadavailableRedPacket;
import com.rd.qnz.xutils.Installation;
import com.rd.qnz.xutils.WebViewAct2;
import com.umeng.analytics.MobclickAgent;
import com.yintong.pay.utils.BaseHelper;
import com.yintong.pay.utils.Constants;
import com.yintong.pay.utils.MobileSecurePayer;
import com.yintong.pay.utils.ResultChecker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


/**
 * 1.3.0 改 版 数据多大多数从产品详情页传递过来
 * 确认申购
 *
 * @author Evonne
 */
public class ProductContentPurchaseAct extends BaseActivity implements OnClickListener {
    private static final String TAG = "确认申购";
    private ArrayList<String> param = null;
    private ArrayList<String> value = null;



    private GetDialog dia;
    private MyHandler myHandler;
    private CustomProgressDialog progressDialog = null;
    private MyApplication myApp;
    private Context context;
    APIModel apiModel = new APIModel();
    private String borrowId = "";
    private String productStatus = "";   //产品状态：0：预售 ，1：申购，2:已售完，3:还款中,4:已完结
    private String type = "";
    private String style = "";
    private String brType = "";

    private String title = "";
    private TextView name;
    private int balance = 0;// 剩余可投金额
    private String isday;
//    private String timeLimitDay;  //计算利息用的天数
    private int timeLimitDay;  //计算利息用的天数
    private String timeLimit;
    private Double apr;// 利率
    private float rateapr;// 预计收益利率
    private float extraAwardApr;// 额外加息利率
//    private int interestDay;// 天数
    private int lowestAccount;//最低投资金额
    private String oauthToken;
    private String lastRepayTime;
    private int buy_money;// 购买金额
    private int buy_money_realy;// 实际需要的金额（除掉红包使用的）
    private int buy_money_hongbao;// 红包使用额度

    private Double buy_money_touzizhunbeijin = 0.00;// 购买的时候 我的账户余额需要出的钱(我的账户余额)
    private Double buy_money_yinhangka = 0.00;// 银行卡 需要充值多少钱  使用额度

    private int flowMoney;
    private String flowCount;
    private String data;// 期限
    private String rate_money;// 期限
    private int minMoney = 100;// 最小红包
    private boolean mIsServiceHaveBankCard;// 服务端是否有银行卡
    private boolean mIsSelectedBankCard;// 有没有选择银行卡


    private int PAY_TYPE_BALANCE = 0;
    private int PAY_TYPE_BANK = 1;
    private int PAY_TYPE_BALANCE_AND_BANK = 2;
    private int mPayType;// 购买方式


    private boolean isLogin = false;
    int count = 0;

    SharedPreferences preferences;
    private String balance_web = "0.00";// 从服务端返回的 账户余额
    private Double balanceDouble_web = 0.0;//从服务端返回的账户余额  Doulbe型

    private String nearlyTender = "0";// 0表示用投资准备金投资
    private String bankCardList = "";
    private String allBankCardList = "";

    /*
     * 支付验证方式 0：标准版本， 1：卡前置方式，此两种支付方式接入时，只需要配置一种即可，Demo为说明用。可以在menu中选择支付方式。
     */
    private int pay_type_flag = 1;
    private Timer timer = null;
    private Timer timer14 = null;
    private TimerTask task;
    private TimerTask taskResult;
    private String orderNo;// 订单号
    private int resultDown = 3;
    private int resultDown1 = 3;
    private int resultDownZhunBeiJinResultTimes = 3;// 投资准备金购买结果查询次数

    private boolean yiCi = false;
    private String jypassword = "";


    boolean isUsedBank = false;// 是否用银行卡
    private String mBankId;// 银行卡id
    private String mOrderZhunBeiJinPay;
    private boolean mIsCanCommitBtn;// 提交按钮是否置灰 flase代表置灰
    private String money;
    private boolean isOverTime;//标是否超过30天(超过为true 低于30天为false)
    private String imei;
    private String imei1;
    private static final int STAFF_AUTHORITY_REQUEST_CODE = 2;



    /**
     * 剩余可投布局
     */
    private TextView tv_shengyu_money;   //剩余可投金额
    /**
     * 您要申购布局
     */
    private EditText et_buy_money;//输入要申购的金额

    private TextView tv_yjsy; //预计收益
    /**
     * 使用优惠这一整个布局
     */
    private RelativeLayout redpacket_btn;//红包整个布局
    private TextView purchase_redpacket_title; //优惠卡券
    private TextView tv_youhui; //优惠的文本
    private ImageView purchase_redpacket_right; //红包的箭头
    /**
     *  余额
     */
    private RelativeLayout my_touzi_zhunbei_btn;// 余额这一整个布局
    private TextView tv_ye_describe;// 余额描述文本
    private TextView tv_ye_used;// 本次投资需要使用的余额数
    /**
     * 银行卡这一个布局
     */
    private RelativeLayout my_bank_btn;//银行卡整个布局文件
    private TextView tv_mybank_name;// 我的银行卡名称(没有的时候显示 支付银行卡)
    private TextView tv_bankmoney_used;// 银行卡金额使用(没有银行卡的时候显示 未选择)
    private ImageView my_money_bank_right_arrow;// 银行卡右侧的箭头
    private Button purchase_btn;  //确认支付按钮

    /**
     *   使用优惠
     */
    List<Map<String, String>> jiaxi_list;//加息list
    List<Map<String, String>> jiaxi_list1;//可用加息劵list
    List<Map<String, String>> jiaxi_list2;//不可用加息劵list
    private String userCouponId;
    private List<Map<String, String>> jiaxi_select;

    List<Map<String, String>> list;//红包list
    List<Map<String, String>> list1;//可用红包list
    List<Map<String, String>> list2;//不可用红包list
    private String redpacket = ""; //被选中的红包对应的id
    private List<Map<String, String>> redpacket_select;

    private float select_jiaxi_count=0; //被选中的加息劵加息比例
    private int days; //加息天数
    int select_count = 0;  //被选中的红包优惠的金额
    /**
     * 1.5.1添加服务协议,资金承诺书
     */
    private ImageView iv_select;
    private boolean is_select=true; //是否被选中
    private TextView tv_xieyi; //协议
    /**
     * 1.6.0
     */
    private TextView tv_name;
    private TextView tv_product_limit_time; //理财期限
    private ImageView iv_wenhao; //问号图标
    private ImageView clear; //关闭图标
    DecimalFormat df = new DecimalFormat("0.00");

    /**
     * @param context       当前环境
     * @param borrowId      产品ID（标id）
     * @param productStatus 产品状态：0：预售 ，1：申购，2:已售完，3:还款中,4:已完结
     * @param title         标名
     * @param type          类型（102,103，110,112）其中（102,103借款类，110，112债券类，其中110是按照份数）
     * @param flowCount     份数
     * @param flowMoney     份金额
     * @param balance       余额（标总额-已投金额）
     * @param isDay         是否是天标(1天标 0月标)
     * @param timeLimitDay  借款期限(天标)
     * @param timeLimit     借款期限(月)
     * @param lowestAccount 最低投资金额
     * @param lastRepayTime 最后待收时间
     * @param style         还款方式（1;等额本息，2;一次性到期还款，3;每月还息到期还本）
     * @param apr           基本利率
     * @param rateapr       总利率
     * @param extraAwardApr 额外加息利率
     * @param interestDay   产品剩余天数(用来计算利息)
     * @param brType        产品具体类型 1新手标 2 普通标 3 钱贝勒
     */
    public static void start(Context context,
                             String borrowId,
                             String productStatus,
                             String title,
                             String type,
                             String flowCount,
                             String flowMoney,
                             double balance,
                             String isDay,
                             String timeLimitDay,
                             String timeLimit,
                             String lowestAccount,
                             String lastRepayTime,
                             String style,
                             String apr,
                             String rateapr,
                             String extraAwardApr,
                             String interestDay,
                             String brType) {
        DecimalFormat df = new DecimalFormat("0.00");
        Intent intent = new Intent(context, ProductContentPurchaseAct.class);
        intent.putExtra(BaseParam.QIAN_PRODUCT_BORROWID, borrowId);
        intent.putExtra(BaseParam.QIAN_PRODUCT_PRODUCTSTATUS, productStatus);
        intent.putExtra(BaseParam.QIAN_PRODUCT_NAME, title);
        intent.putExtra(BaseParam.QIAN_PRODUCT_TYPE, type);
        intent.putExtra(BaseParam.QIAN_PRODUCT_FLOWMONEY, flowMoney);
        intent.putExtra(BaseParam.QIAN_PRODUCT_FLOWCOUNT, flowCount);
        intent.putExtra(BaseParam.QIAN_PRODUCT_LASTREPAYTIME, lastRepayTime);
        intent.putExtra(BaseParam.QIAN_PRODUCT_STYLE, style);
        intent.putExtra(BaseParam.QIAN_PRODUCT_LOWESTACCOUNT, lowestAccount);
        intent.putExtra("balance", df.format(balance));
        intent.putExtra(BaseParam.QIAN_PRODUCT_ISDAY, isDay);
        intent.putExtra(BaseParam.QIAN_PRODUCT_TIMELIMITDAY, timeLimitDay + "");
        intent.putExtra(BaseParam.QIAN_PRODUCT_TIMELIMIT, timeLimit + "");
        intent.putExtra(BaseParam.QIAN_PRODUCT_APR, apr);
        intent.putExtra("apr", rateapr);
        intent.putExtra("extraAwardApr", extraAwardApr);
        intent.putExtra("interestDay", interestDay);
        intent.putExtra(BaseParam.QIAN_PRODUCT_BR_TYPE, brType);

        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage_product_list_content_purchase_gai2);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //设置顶部状态栏颜色

            window.setStatusBarColor(getResources().getColor(R.color.mine_top));

        }

        checkIsCanBuy();  //检测是否设置了交易密码和实名认证
        context = ProductContentPurchaseAct.this;
        this.dia = new GetDialog();
        myApp = (MyApplication) getApplication();
        myApp.bankId = "";
        myApp.bank_name = "";
        this.myHandler = new MyHandler();

        df = new DecimalFormat("0.00");
        IntentFilter intentFilter=new IntentFilter("bindbank"); //绑定银行卡的广播
        registerReceiver(myreceiver,intentFilter);


        myApp.real_back = false;
        myApp.uniqNo = "";
        Intent intent = getIntent();
        brType = intent.getStringExtra(BaseParam.QIAN_PRODUCT_BR_TYPE); //1新手标 2 普通标 3 钱贝勒
        borrowId = intent.getStringExtra(BaseParam.QIAN_PRODUCT_BORROWID); //产品id
        productStatus = intent.getStringExtra(BaseParam.QIAN_PRODUCT_PRODUCTSTATUS);// 产品状态：0：预售 ，1：申购，2:已售完，3:还款中,4:已完结
        title = intent.getStringExtra(BaseParam.QIAN_PRODUCT_NAME);//标名
        type = intent.getStringExtra(BaseParam.QIAN_PRODUCT_TYPE); //type 类型（102,103，110,112）其中（102,103借款类，110，112债券类，其中110是按照份数）
        flowCount = intent.getStringExtra(BaseParam.QIAN_PRODUCT_FLOWCOUNT); //份数

        flowMoney = Integer.parseInt(intent.getStringExtra(BaseParam.QIAN_PRODUCT_FLOWMONEY));//每份金额
        balance = (int) Double.parseDouble(intent.getStringExtra("balance")); //剩余可投金额

        isday = intent.getStringExtra(BaseParam.QIAN_PRODUCT_ISDAY); //是否是天标(1天标 0月标) 现在都是天标
//        timeLimitDay = intent.getStringExtra(BaseParam.QIAN_PRODUCT_TIMELIMITDAY);
        timeLimitDay =Integer.parseInt(intent.getStringExtra(BaseParam.QIAN_PRODUCT_TIMELIMITDAY)) ;
        timeLimit = intent.getStringExtra(BaseParam.QIAN_PRODUCT_TIMELIMIT);
        lowestAccount = (int) Double.parseDouble(intent.getStringExtra(BaseParam.QIAN_PRODUCT_LOWESTACCOUNT));
        lastRepayTime = intent.getStringExtra(BaseParam.QIAN_PRODUCT_LASTREPAYTIME);
        style = intent.getStringExtra(BaseParam.QIAN_PRODUCT_STYLE);
        apr = Double.parseDouble(intent.getStringExtra(BaseParam.QIAN_PRODUCT_APR));//基本利率
        rateapr = Float.parseFloat(intent.getStringExtra(BaseParam.QIAN_PRODUCT_APR));//总利率
        extraAwardApr = Float.parseFloat(intent.getStringExtra("extraAwardApr"));//额外加息
//        interestDay = Integer.parseInt(intent.getStringExtra("interestDay"));//产品剩余还款日期

        preferences = getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_USER, Context.MODE_PRIVATE);
        oauthToken = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN, "");
        //0不是天标 1 是天标
        if (isday.equals("0")) { //月标
            isOverTime = true;
        } else if (Integer.valueOf(timeLimitDay) >= 30) { //天标，但>=30天
            isOverTime = true;
        } else { //天标，<30天
            isOverTime = false;
        }


        initBar();
        initView();

        imei1 = AppTool.imeiSave(ProductContentPurchaseAct.this);
        imei = imei + "-qian-" + Installation.id(ProductContentPurchaseAct.this);


    }
    private BroadcastReceiver myreceiver =new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
           String action=intent.getAction();
            if (action.equals("bindbank")){
                startDataRequestRedpackets(); //再次获取红包和银行卡信息
            }
        }
    };
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
        actionbar_side_name.setText("确认投资");

    }

    private class TextClick2 extends ClickableSpan {
        @Override
        public void onClick(View widget) {  //跳转到h5
            if (Check.hasInternet(ProductContentPurchaseAct.this)) {
                if (MineShow.fastClick()) {
                    Intent i=new Intent(ProductContentPurchaseAct.this, WebViewAct2.class);
                    i.putExtra("web_url",BaseParam.URL_WLJD);
                    startActivity(i);
                }
            } else {
                MineShow.toastShow("请检查网络连接是否正常", context);
            }


        }

        @Override
        public void updateDrawState(TextPaint ds) {

        }
    }
    private class TextClick3 extends ClickableSpan {
        @Override
        public void onClick(View widget) {  //跳转到h5
            if (Check.hasInternet(ProductContentPurchaseAct.this)) {
                if (MineShow.fastClick()) {
                    Intent i=new Intent(ProductContentPurchaseAct.this, WebViewAct2.class);
                    i.putExtra("web_url",BaseParam.URL_ZJLY);
                    startActivity(i);
                }
            } else {
                MineShow.toastShow("请检查网络连接是否正常", context);
            }


        }

        @Override
        public void updateDrawState(TextPaint ds) {


        }
    }
    private void initView() {

        clear= (ImageView) findViewById(R.id.clear);
        clear.setOnClickListener(this);
        tv_xieyi= (TextView) findViewById(R.id.tv_xieyi);
        SpannableStringBuilder spannable = new SpannableStringBuilder("已阅读并同意《风险提示书》和《借款协议》所有内容,充分了解并清楚知晓相应的权利义务。");

        spannable.setSpan(new ForegroundColorSpan(Color.rgb(255,91,29)),6,14, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(Color.rgb(255,91,29)),14,20, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(Color.BLACK),0,6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(Color.BLACK),13,14, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(Color.BLACK),19,41, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new TextClick2(),6,12 , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new TextClick3(),14,20 , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


        tv_xieyi.setText(spannable);
        tv_xieyi.setHighlightColor(getResources().getColor(android.R.color.transparent));
        tv_xieyi.setMovementMethod(LinkMovementMethod.getInstance());


        iv_select= (ImageView) findViewById(R.id.iv_select);
        iv_select.setOnClickListener(this);
        redpacket_btn = (RelativeLayout) findViewById(R.id.redpacket_btn);// 选择红包
        redpacket_btn.setOnClickListener(this);
        my_touzi_zhunbei_btn = (RelativeLayout) findViewById(R.id.my_touzi_zhunbei_btn);
        my_touzi_zhunbei_btn.setOnClickListener(this);
        tv_ye_describe = (TextView) findViewById(R.id.tv_ye_describe);
        tv_ye_describe.setOnClickListener(this);
        tv_ye_used = (TextView) findViewById(R.id.tv_ye_used);
        tv_mybank_name = (TextView) findViewById(R.id.tv_mybank_name);
        tv_bankmoney_used = (TextView) findViewById(R.id.tv_bankmoney_used);// 支付银行卡 未选择
        tv_bankmoney_used.setOnClickListener(this);
        my_bank_btn = (RelativeLayout) findViewById(R.id.my_bank_btn); //支付银行卡
        my_bank_btn.setOnClickListener(this);

        iv_wenhao= (ImageView) findViewById(R.id.iv_wenhao);
        iv_wenhao.setOnClickListener(this);
        my_money_bank_right_arrow = (ImageView) findViewById(R.id.my_money_bank_right_arrow);
        tv_shengyu_money = (TextView) findViewById(R.id.tv_sskt_money);   //剩余可投多少元

        et_buy_money = (EditText) findViewById(R.id.et_buy_money); //申购多少元(投资多少钱)

        tv_yjsy = (TextView) findViewById(R.id.tv_yjsy);

        purchase_redpacket_title = (TextView) findViewById(R.id.purchase_redpacket_title);
        tv_youhui = (TextView) findViewById(R.id.tv_youhui);
        purchase_redpacket_right = (ImageView) findViewById(R.id.purchase_redpacket_right);

        /* 确认去支付按钮 */
        purchase_btn = (Button) findViewById(R.id.purchase_btn);
        purchase_btn.setOnClickListener(this);
        tv_product_limit_time= (TextView) findViewById(R.id.tv_product_limit_time);
        tv_product_limit_time.setText(timeLimitDay+"天");
        tv_name= (TextView) findViewById(R.id.tv_name);
        tv_name.setText(title);
        redpacket_btn.setVisibility(View.VISIBLE);


        if (balance < lowestAccount) {    //如果。产品余额小于最低投资金额

            yiCi = true;
            buy_money = balance;
            tv_shengyu_money.setText(df.format(buy_money) + "元");  //剩余可投


            et_buy_money.setText(buy_money + "");  //申购的金额也是这么多元

            et_buy_money.setFocusable(false);  //购买金额这个编辑框不获取焦点

//            int tian = Integer.parseInt(timeLimitDay);
            int tian =timeLimitDay;
            int yue = Integer.parseInt(timeLimit);

            if (isday.equals("1")) {
                data = tian + "天";
            } else {
                data = yue + "个月";
            }

            String rate = "";
            if (type.equals("112")) {
                String date1 = AppTool.getMsgTwoDateDistance3(lastRepayTime);
                String date2 = AppTool.getDate();
                long day = AppTool.getQuot(date1, date2);
                int timeLimitDay = (int) day;

                rate = AppTool.rateReceipt(buy_money, rateapr,extraAwardApr, "1", timeLimitDay, 0);
            } else {
                if (style.equals("1")) {
                    rate = AppTool.denge(buy_money, apr, yue);
                } else {
                    rate = AppTool.rateReceipt(buy_money, rateapr,extraAwardApr, isday, timeLimitDay, yue);
                }

            }
            rate_money = rate;
            tv_yjsy.setText(Math.abs(Double.parseDouble(rate)) + "");

        } else {  //标的剩余金额大于100
            tv_shengyu_money.setText(df.format(balance)+"元");
            et_buy_money.setHint(lowestAccount + "元起投");

        }
        et_buy_money.setInputType(InputType.TYPE_CLASS_NUMBER);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        // 对输入金额实时监听
        et_buy_money.addTextChangedListener(new TextWatcher() {

            @Override  //文本刚刚被修改的时候调用
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (!isOverTime) {//没有超过期限30天，则红包不跟着变化，一直为0
                    return;
                }
                if (!et_buy_money.getText().toString().equals("")) {  //输入申购金额不为0
                    List<Map<String, String>> redpacket_list = redpacket_select;
                    if (redpacket_list.size() == 0) { //当前这个号没有红包
                        minMoney = 0;//最小红包,默认值100
                        return;
                    }
                    for (int i = 0; i < redpacket_list.size(); i++) {
                        if (redpacket_list.get(i).get("status").equals("0")) {
                            int current = Integer.parseInt(redpacket_list.get(i).get("redPacketAmount"));//红包的优惠金额
                            minMoney = current < minMoney ? current : minMoney;   //当前未被选中的红包的最小优惠金额
                        }
                    }
                    redpacket_select = redpacket_list;

                }


            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                money = et_buy_money.getText().toString().trim();// 填写的投资金额
                if (redpacket.equals("")) {
                } else {//当前已经有红包被选中了,把红包列表的状态都改为未被选中  取消最小红包的状态
                    cencalRedpackets();
                }
                select_count = 0;
                redpacket = "";

                if (TextUtils.isEmpty(money)) {
                    buy_money = 0;
                    clear.setVisibility(View.GONE);
                } else {
                    buy_money = (int) Double.parseDouble(money);
                    clear.setVisibility(View.VISIBLE);
                }
                // TODO: 2017/4/17 0017    动态判断输入的金额是否能够符合红包或者加息劵
                    showYouHui();  //动态修改优惠的文本


                if (buy_money > balance) {//购买金额大于产品的剩余金额
                    et_buy_money.setText(balance + "");
                    et_buy_money.setSelection((balance + "").length());
                }

                if (!isOverTime) {   //这个标的期限小于30天，红包始终为0 一旦你重新输入了文字,那么被选中红包的金额改为0
                    select_count = 0;
                } else {  //红包超过30天
                    if (redpacket_select != null || redpacket_select.size() != 0) {

                        for (int i = 0; i < redpacket_select.size(); i++) {
                            if (redpacket_select.get(i).get("status").equals("1")) {
                                redpacket_select.get(i).put("status", "0");
                            }
                        }
                    }
                    select_count = 0;
                }
                if (extraAwardApr!=0){ //如果是加息标,那么加息劵肯定不可用
                  select_jiaxi_count=0;
                }else {   //如果不是加息标,那么一旦输入数字就把被选中的金额清空
                    if (jiaxi_select != null || jiaxi_select.size() != 0) {

                        for (int i = 0; i < jiaxi_select.size(); i++) {
                            if (jiaxi_select.get(i).get("status").equals("1")) {
                                jiaxi_select.get(i).put("status", "0");
                            }
                        }
                    }
                    select_jiaxi_count = 0;
                }

                buy_money_hongbao = select_count;  //红包优惠的金额(在这里是0)

                Double myBalance = Double.parseDouble(balance_web);  //我账户的余额
                if (buy_money - buy_money_hongbao >= 0) {// 要用余额了
                    if (buy_money - buy_money_hongbao > myBalance) {//购买的金额-红包的优惠金>账户余额  需要 要用银行卡



                        buy_money_touzizhunbeijin = myBalance; //我的账户余额
                        buy_money_yinhangka = Double.valueOf(buy_money - buy_money_hongbao - buy_money_touzizhunbeijin);//需要银行卡充值多少钱
                        isUsedBank = true;
                    } else {// 不用银行
                        buy_money_touzizhunbeijin = Double.valueOf((buy_money - buy_money_hongbao));
                        isUsedBank = false;// 只用投资准备金就可以搞定了
                        buy_money_yinhangka = 0.0;
                    }
                }
                // TODO: 2017/4/17 0017



                if (mIsServiceHaveBankCard || mIsSelectedBankCard) {//服务端是否有银行卡||有没有选择银行卡
                    my_money_bank_right_arrow.setVisibility(View.INVISIBLE);//选择银行卡右边箭头隐藏
                    if (isUsedBank) {  //使用银行卡
                        tv_bankmoney_used.setText(new DecimalFormat("0.00").format(buy_money_yinhangka) + "元");
                    } else { //不使用银行卡
                        tv_bankmoney_used.setText("0.00");
                    }
                    mIsCanCommitBtn = true;  //有银行卡,按钮可点击
                } else {  //服务端没有银行卡或者不选择银行卡
                    my_money_bank_right_arrow.setVisibility(View.VISIBLE);
                    tv_bankmoney_used.setText("未选择");
                    if (buy_money_yinhangka > 0) { //在没有银行卡的前提下,如果余额不足
                        // TODO: 2017/3/15 0015
                        mIsCanCommitBtn = true;

                    } else {
                        mIsCanCommitBtn = true;
                    }
                }

                if (mIsServiceHaveBankCard) { // 服务端有银行卡
//                    tv_bankmoney_used.setClickable(false);
//                    tv_bankmoney_used.setClickable(true);
                } else {  //没有银行卡的话,那么金额这一块可以被点击
//                    tv_bankmoney_used.setClickable(true);
                }

                if (mIsServiceHaveBankCard) { //服务端有银行卡,那么向右的箭头隐藏
                    my_money_bank_right_arrow.setVisibility(View.INVISIBLE);
                } else {
                    my_money_bank_right_arrow.setVisibility(View.VISIBLE);
                }


                tv_ye_used.setText(new DecimalFormat("0.00").format(buy_money_touzizhunbeijin) + "元");// 投资准备金使用额
                //Log.d("pepe投资准备金已用", mMyTouZiZhunBeiJinYong.getText().toString());
                //Log.d("pepe银行卡金额使用", mBankMoneyUsed.getText().toString());

                if (money.equals("") || null == money) {  //用户有没有输入金额
                    money = "0";
                    purchase_btn.setClickable(false);
                    purchase_btn.setBackgroundResource(R.drawable.button_org_grauly);
                } else {  //用户输入了金额
//                    if (mIsCanCommitBtn&&is_select) {  //按钮可点击
                    if (mIsCanCommitBtn) {  //按钮可点击
                        purchase_btn.setClickable(true);
                        purchase_btn.setBackgroundResource(R.drawable.button_org_big); //红色
                    } else {
                        purchase_btn.setClickable(false);
                        purchase_btn.setBackgroundResource(R.drawable.button_org_grauly); //灰色
                    }
                }

                String rate = AppTool.rateReceipt(buy_money, rateapr,extraAwardApr, "1", timeLimitDay, 0);
                tv_yjsy.setText(Math.abs(Double.parseDouble(rate)) + ""); //预计收益
            }


        });

        startDataRequestRedpackets();

        task = new TimerTask() {
            @Override
            public void run() {
                // // TODO Auto-generated method stub
                Message message = new Message();
                message.what = 2;
                handler.sendMessage(message);
            }
        };

        taskResult = new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = 3;
                handler.sendMessage(message);
            }
        };
    }

    /**
     * 根据用户输入的金额,动态刷新一下优惠textview
     */
    private void showYouHui() {
        if (buy_money<100&&!yiCi){  //金额小于一百,标的剩余价值是大于100的  那么啥也不做
            tv_youhui.setText("暂无可用优惠卡券");
        }else {
            double timeLimitDay1=Integer.valueOf(timeLimitDay);
            boolean redused=false; //红包是否可用
            //动态判断红包可否使用
            for (int i = 0; i < redpacket_select.size(); i++) {
                if ( timeLimitDay1>= Integer.valueOf(redpacket_select.get(i).get("timeLimit"))
                        && buy_money >= Double.valueOf(redpacket_select.get(i).get("dayLimit"))) {//标的期限大于红包可用期限,购买的金额大于使用该红包需要使用的金额
                    //有可以使用的红包
                    tv_youhui.setText("未使用");
                    redused=true;
                    break;
                } else {  //红包已经不可用了,我们在这里判断加息劵是否可用
                    tv_youhui.setText("暂无可用优惠卡券");
                }

            }

            if (redused){  //如果当前金额已经可以使用红包我们就不判断加息劵

            }else {  //红包没法使用,我们判断一下加息劵能不能用
                        if (extraAwardApr!=0){ //是加息标,那么加息劵不可用
                            tv_youhui.setText("暂无可用优惠卡券");
                        }else { //不是加息标,我们判断一下加息劵的情况
                            for (int i = 0; i < jiaxi_select.size(); i++) {
                                int timeMin = Integer.valueOf(jiaxi_select.get(i).get("timeMin"));
                                int timeMax = Integer.valueOf(jiaxi_select.get(i).get("timeMax"));
                                int amountMax = Integer.valueOf(jiaxi_select.get(i).get("amountMax"));
                                int amountMin = Integer.valueOf(jiaxi_select.get(i).get("amountMin"));

                                if (timeMax == 0 && timeMin == 0) { //代表没有时间限制,那就判断金额

                                    if (amountMin==0&&amountMax==0){ //代表金额也没有限制
                                        tv_youhui.setText("未使用");
                                                break;
                                    }else if(amountMin==0){ //金额最小值为0,最大值不为0  金额<= 1000
                                        if (buy_money <= amountMax){ //这张加息劵可用
                                            tv_youhui.setText("未使用");
                                            break;
                                        }else {
                                            tv_youhui.setText("暂无可用优惠卡券");
                                        }
                                    }else if (amountMax==0){ //金额最大值为0,最小值不为0  金额>=1000
                                        if (buy_money >= amountMin){ //这张加息劵可用
                                            tv_youhui.setText("未使用");
                                            break;
                                        }else {
                                            tv_youhui.setText("暂无可用优惠卡券");
                                        }
                                    }else {  //最大值和最小值都不为0
                                        if (buy_money>=amountMin&&buy_money<=amountMax ){ //这张加息劵可用
                                            tv_youhui.setText("未使用");
                                            break;
                                        }else {
                                            tv_youhui.setText("暂无可用优惠卡券");
                                        }
                                    }

                                } else if (timeMax == 0) {  //加息劵的最大值为0那么就只需要判断最低期限就行了,最小值不为0,判断最小值
                                    if (timeLimitDay1 >= timeMin) {  //标的期限大于加息劵最小期限,判断金额
                                        if (amountMin==0&&amountMax==0){ //代表金额也没有限制
                                            tv_youhui.setText("未使用");
                                            break;
                                        }else if(amountMin==0){ //金额最小值为0,最大值不为0  金额<= 1000
                                            if (buy_money <= amountMax){ //这张加息劵可用
                                                tv_youhui.setText("未使用");
                                                break;
                                            }else {
                                                tv_youhui.setText("暂无可用优惠卡券");
                                            }
                                        }else if (amountMax==0){ //金额最大值为0,最小值不为0  金额>=1000
                                            if (buy_money >= amountMin){ //这张加息劵可用
                                                tv_youhui.setText("未使用");
                                                break;
                                            }else {
                                                tv_youhui.setText("暂无可用优惠卡券");
                                            }
                                        }else {  //最大值和最小值都不为0
                                            if (buy_money>=amountMin&&buy_money<=amountMax ){ //这张加息劵可用
                                                tv_youhui.setText("未使用");
                                                break;
                                            }else {
                                                tv_youhui.setText("暂无可用优惠卡券");
                                            }
                                        }


                                    } else {
                                        tv_youhui.setText("暂无可用优惠卡券");
                                    }

                                } else if (timeMin == 0) {  //加息劵最低期限是0,那么只需要判断最高期限就行了
                                    if (timeLimitDay1 <= timeMax) { //期限没问题,判断金额

                                        if (amountMin==0&&amountMax==0){ //代表金额也没有限制
                                            tv_youhui.setText("未使用");
                                                break;
                                        }else if(amountMin==0){ //金额最小值为0,最大值不为0  金额<= 1000
                                            if (buy_money <= amountMax){ //这张加息劵可用
                                                tv_youhui.setText("未使用");
                                                break;
                                            }else {
                                                tv_youhui.setText("暂无可用优惠卡券");
                                            }
                                        }else if (amountMax==0){ //金额最大值为0,最小值不为0  金额>=1000
                                            if (buy_money >= amountMin){ //这张加息劵可用
                                                tv_youhui.setText("未使用");
                                                break;
                                            }else {
                                                tv_youhui.setText("暂无可用优惠卡券");
                                            }
                                        }else {  //最大值和最小值都不为0
                                            if (buy_money>=amountMin&&buy_money<=amountMax ){ //这张加息劵可用
                                                tv_youhui.setText("未使用");
                                                break;
                                            }else {
                                                tv_youhui.setText("暂无可用优惠卡券");
                                            }
                                        }

                                    } else {
                                        tv_youhui.setText("暂无可用优惠卡券");
                                    }
                                } else { //加息劵最大和最小期限都不为0
                                    if (timeLimitDay1 >= timeMin && timeLimitDay1 <= timeMax) {

                                        if (amountMin==0&&amountMax==0){ //代表金额也没有限制
                                            tv_youhui.setText("未使用");
                                              break;
                                        }else if(amountMin==0){ //金额最小值为0,最大值不为0  金额<= 1000
                                            if (buy_money <= amountMax){ //这张加息劵可用
                                                tv_youhui.setText("未使用");
                                                break;
                                            }else {
                                                tv_youhui.setText("暂无可用优惠卡券");
                                            }
                                        }else if (amountMax==0){ //金额最大值为0,最小值不为0  金额>=1000
                                            if (buy_money >= amountMin){ //这张加息劵可用
                                                tv_youhui.setText("未使用");
                                                break;
                                            }else {
                                                tv_youhui.setText("暂无可用优惠卡券");
                                            }
                                        }else {  //最大值和最小值都不为0
                                            if (buy_money>=amountMin&&buy_money<=amountMax ){ //这张加息劵可用
                                                tv_youhui.setText("未使用");
                                                break;
                                            }else {
                                                tv_youhui.setText("暂无可用优惠卡券");
                                            }
                                        }


                                    } else {
                                        tv_youhui.setText("暂无可用优惠卡券");
                                    }
                                }
                            }
                        }


            }



        }
    }

    /**
     * 检测是否实名认证和设置交易密码
     *
     * @return isCanBuy
     */
    private boolean checkIsCanBuy() {
        boolean isCanBuy = false;
        if (TextUtils.equals(Profile.getUserPayPassWordStatus(), "0")) {// 未设置交易密码
            Intent intent = new Intent(ProductContentPurchaseAct.this, ForgetPasswordAct.class);
            startActivity(intent);
            finish();
        }
        if (TextUtils.equals(Profile.getUserRealNameStatus(), "0")) {// 未实名认证
            NewRealAct.start(ProductContentPurchaseAct.this);
            finish();
        }
        return isCanBuy;
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
                    tv_mybank_name.setText(
                            Check.jsonGetStringAnalysis(bank, BaseParam.QIAN_MY_BANK_BANKSHORTNAME)
                                    + "卡支付(尾号 "
                                    + Check.jsonGetStringAnalysis(bank, BaseParam.QIAN_MY_BANK_HIDDENCARDNO)
                                    + ")"
                    );
                    myApp.uniqNo = Check.jsonGetStringAnalysis(bank, BaseParam.QIAN_MY_BANK_UNIQNO);
                    mIsServiceHaveBankCard = true;
                    tv_bankmoney_used.setText("0.00");
//                    tv_bankmoney_used.setClickable(true);
                    if (mIsServiceHaveBankCard) {//若服务端有银行卡 右侧箭头隐藏
                        my_money_bank_right_arrow.setVisibility(View.INVISIBLE);
                    } else {
                        my_money_bank_right_arrow.setVisibility(View.VISIBLE);
                    }
                    return;
                }
            }
            if (bankList.length() <= 0) {// 没有银行卡
                tv_bankmoney_used.setText("未选择");
//                tv_bankmoney_used.setClickable(true);
                mIsServiceHaveBankCard = false;  //服务端没有绑定银行卡
            }
            if (mIsServiceHaveBankCard) {
                my_money_bank_right_arrow.setVisibility(View.INVISIBLE);
            } else {
                my_money_bank_right_arrow.setVisibility(View.VISIBLE); //没有绑银行卡的话就显示右边的箭头
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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
                                retVal = ResultChecker.RESULT_CHECK_SIGN_SUCCEED;  //平时都是走这里的
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
                                    progressDialog = dia.getLoginDialog(ProductContentPurchaseAct.this, "正在验证信息..");
                                    progressDialog.show();

                                    handler.postDelayed(task, 3000);
                                } else {
                                }
                            } else {
                            }
                        } else if (Constants.RET_CODE_PROCESS.equals(retCode)) {
                            // TODO 8、 支付处理中的情况
                            String resulPay = objContent.optString("result_pay");
                            if (Constants.RESULT_PAY_PROCESSING.equalsIgnoreCase(resulPay)) {
                            }
                        } else {
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
     * (充钱到余额去)
     */
    private void RechargeBalance(Double buyMoneyYinhangka) {
        if (Check.hasInternet(ProductContentPurchaseAct.this)) {
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
            progressDialog = dia.getLoginDialog(ProductContentPurchaseAct.this, "正在验证信息..");
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
     * 查询充值订单结果 rechargeResult.html
     */
    private void CheckRechargeBalanceResult() {// URL_QIAN_RECHARGE_RESULT
        if (Check.hasInternet(ProductContentPurchaseAct.this)) {
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
            progressDialog = dia.getLoginDialog(ProductContentPurchaseAct.this, "正在验证信息..");
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
        if (isUsedBank) {  //使用银行卡
            if (buy_money_touzizhunbeijin <= 0) {
                mPayType = PAY_TYPE_BANK;  //支付方式就是使用银行支付
            } else {
                mPayType = PAY_TYPE_BALANCE_AND_BANK; //支付方式是使用银行卡 和余额
            }
            RechargeBalance(buy_money_yinhangka);
        } else {
            mPayType = PAY_TYPE_BALANCE;
            payProductByZhunBeiJin();// 直接投资准备金
        }
    }

    /**
     *
     * 直接走申购接口  tender1xV4
     */
    private void payProductByZhunBeiJin() {
        if (Check.hasInternet(ProductContentPurchaseAct.this)) {
            initArray();
            buy_money_realy = buy_money - buy_money_hongbao;
            param.add(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN);
            value.add(oauthToken);
            param.add("id");
            value.add(borrowId);// 产品id
            param.add("money");
            value.add(buy_money + "");
            param.add("redpacket");
            value.add(redpacket);
            param.add("coupon");
            value.add(userCouponId);
            param.add("buyType");
            value.add(mPayType + "");
            param.add("bankMoney");
            value.add(buy_money_yinhangka + "");
            param.add("useMoney");
            value.add("" + buy_money_realy);
            param.add("systemType");
            value.add(BaseParam.systemType);// android 版本
            param.add("version");
            value.add(BaseParam.getVersionName(ProductContentPurchaseAct.this));
            param.add("paypwd");
            value.add(jypassword);
            param.add(BaseParam.URL_QIAN_API_APPID);
            value.add(myApp.appId);
            param.add(BaseParam.URL_QIAN_API_SERVICE);
            value.add("tender1xV4");
            param.add(BaseParam.URL_QIAN_API_SIGNTYPE);
            value.add(myApp.signType);

            String[] array = new String[]{
                    BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN + "=" + oauthToken,
                    "id=" + borrowId,
                    "useMoney=" + buy_money_realy,
                    "money=" + buy_money,
                    "redpacket=" + redpacket,
                    "coupon=" + userCouponId,
                    "buyType=" + mPayType,
                    "bankMoney=" + buy_money_yinhangka,
                    "systemType=" + BaseParam.systemType,
                    "version=" + BaseParam.getVersionName(ProductContentPurchaseAct.this),
                    "paypwd=" + jypassword,
                    BaseParam.URL_QIAN_API_APPID + "=" + myApp.appId,
                    BaseParam.URL_QIAN_API_SERVICE + "=tender1xV4",
                    BaseParam.URL_QIAN_API_SIGNTYPE + "=" + myApp.signType
            };

            String sign = apiModel.sortStringArray(array);
            param.add(BaseParam.URL_QIAN_API_SIGN);
            value.add(sign);

            progressDialog = dia.getLoginDialog(ProductContentPurchaseAct.this, "正在验证信息..");
            progressDialog.show();

            new Thread(new JsonRequeatThreadPayProduct(
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
     * 投资准备金 申购结果轮询接口
     */
    private void CheckPayProductByZhunBeiJinResult() {
        if (Check.hasInternet(ProductContentPurchaseAct.this)) {
            initArray();
            param.add(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN);
            value.add(oauthToken);
            param.add("id");
            value.add(borrowId);// 产品id
            param.add("orderNo");
            value.add(mOrderZhunBeiJinPay);//
            param.add(BaseParam.URL_QIAN_API_APPID);
            value.add(myApp.appId);
            param.add(BaseParam.URL_QIAN_API_SERVICE);
            value.add("tenderResult1xV4");
            param.add(BaseParam.URL_QIAN_API_SIGNTYPE);
            value.add(myApp.signType);
            String[] array;

            if (resultDownZhunBeiJinResultTimes == 0) {// 最后一次轮询
                param.add("status");
                value.add("0");
                array = new String[]{
                        BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN + "=" + oauthToken,
                        "id=" + borrowId,
                        "status=0",
                        "orderNo=" + mOrderZhunBeiJinPay,
                        BaseParam.URL_QIAN_API_APPID + "=" + myApp.appId,
                        BaseParam.URL_QIAN_API_SERVICE + "=tenderResult1xV4",
                        BaseParam.URL_QIAN_API_SIGNTYPE + "=" + myApp.signType
                };
            } else {
                array = new String[]{
                        BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN + "=" + oauthToken,
                        "id=" + borrowId,
                        "orderNo=" + mOrderZhunBeiJinPay,
                        BaseParam.URL_QIAN_API_APPID + "=" + myApp.appId,
                        BaseParam.URL_QIAN_API_SERVICE + "=tenderResult1xV4",
                        BaseParam.URL_QIAN_API_SIGNTYPE + "=" + myApp.signType
                };
            }

            String sign = apiModel.sortStringArray(array);
            param.add(BaseParam.URL_QIAN_API_SIGN);
            value.add(sign);
            if (progressDialog == null) {
                progressDialog = dia.getLoginDialog(ProductContentPurchaseAct.this, "正在验证信息..");
            }
            if (!progressDialog.isShowing()) {
                progressDialog.show();
            }

            new Thread(new JsonRequeatThreadCheckPayProductResult(
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
     * 获取申购结果页的url
     *   投资准备金申购结果轮询
     */
    private String getPayProductResultUrl(String status) {
        initArray();
        param.add(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN);
        value.add(oauthToken);
        param.add("orderNo");
        value.add(mOrderZhunBeiJinPay);//
        param.add(BaseParam.URL_QIAN_API_APPID);
        value.add(myApp.appId);
        param.add(BaseParam.URL_QIAN_API_SERVICE);
        value.add("tenderResult1xV4");
        param.add(BaseParam.URL_QIAN_API_SIGNTYPE);
        value.add(myApp.signType);
        param.add("id");
        value.add(borrowId);// 产品id
        String[] array;
        if (!TextUtils.isEmpty(status)) {
            param.add("status");
            value.add(status);
            array = new String[]{
                    BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN + "=" + oauthToken,
                    "orderNo=" + mOrderZhunBeiJinPay,
                    "status=" + status, "id=" + borrowId,
                    BaseParam.URL_QIAN_API_APPID + "=" + myApp.appId,
                    BaseParam.URL_QIAN_API_SERVICE + "=tenderResult1xV4",
                    BaseParam.URL_QIAN_API_SIGNTYPE + "=" + myApp.signType
            };
        } else {
            array = new String[]{
                    BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN + "=" + oauthToken,
                    "id=" + borrowId,
                    "orderNo=" + mOrderZhunBeiJinPay,
                    BaseParam.URL_QIAN_API_APPID + "=" + myApp.appId,
                    BaseParam.URL_QIAN_API_SERVICE + "=tenderResult1xV4",
                    BaseParam.URL_QIAN_API_SIGNTYPE + "=" + myApp.signType
            };
        }

        String sign = apiModel.sortStringArray(array);
        param.add(BaseParam.URL_QIAN_API_SIGN);
        value.add(sign);
        StringBuffer strBuffer = new StringBuffer();
        strBuffer.append(BaseParam.URL_QIAN_TENDER_RESULT_1XV4);
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

    /**
     * 支付失败 订单取消
     */
    private void startDataRequestCancel() {
        if (Check.hasInternet(ProductContentPurchaseAct.this)) {
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

            progressDialog = dia.getLoginDialog(ProductContentPurchaseAct.this, "正在验证信息..");
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
     * 申购界面,获取 银行卡和红包信息
     * getPayInfo.html
     */
    private void startDataRequestRedpackets() {
        if (Check.hasInternet(ProductContentPurchaseAct.this)) {
            ProductContentPurchaseAct.this.initArray();
            ProductContentPurchaseAct.this.param.add("oauthToken");
            ProductContentPurchaseAct.this.value.add(oauthToken);
            ProductContentPurchaseAct.this.param.add(BaseParam.URL_QIAN_API_APPID);
            ProductContentPurchaseAct.this.value.add(myApp.appId);
            ProductContentPurchaseAct.this.param.add(BaseParam.URL_QIAN_API_SERVICE);
            ProductContentPurchaseAct.this.value.add("getPayInfo");
            ProductContentPurchaseAct.this.param.add(BaseParam.URL_QIAN_API_SIGNTYPE);
            ProductContentPurchaseAct.this.value.add(myApp.signType);

            String[] array = new String[]{
                    "oauthToken=" + oauthToken,
                    BaseParam.URL_QIAN_API_APPID + "=" + myApp.appId,
                    BaseParam.URL_QIAN_API_SERVICE + "=getPayInfo",
                    BaseParam.URL_QIAN_API_SIGNTYPE + "=" + myApp.signType

            };
            String sign = apiModel.sortStringArray(array);
            ProductContentPurchaseAct.this.param.add(BaseParam.URL_QIAN_API_SIGN);
            ProductContentPurchaseAct.this.value.add(sign);
            ProductContentPurchaseAct.this.progressDialog = ProductContentPurchaseAct
                    .this.dia.getLoginDialog(ProductContentPurchaseAct.this, "正在获取数据..");
            ProductContentPurchaseAct.this.progressDialog.show();
            new Thread(new JsonRequeatThreadavailableRedPacket(
                    ProductContentPurchaseAct.this, myApp,
                    ProductContentPurchaseAct.this.myHandler,
                    ProductContentPurchaseAct.this.param,
                    ProductContentPurchaseAct.this.value)
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
            ProductContentPurchaseAct.this.progressDialog.dismiss();
        }
        if (myreceiver!=null){
            try{
                unregisterReceiver(myreceiver);
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 2:
                    if (resultDown1 > 0) {
                        resultDown1--;
                        CheckRechargeBalanceResult();  //查询一下支付结果
                    } else {  //获取到充值错误页面
                        WebViewPayResultActivity.start(ProductContentPurchaseAct.this, "申购结果", getRechargeBalanceResultUrl("0"));
                        if (timer != null) {
                            timer.cancel(); // 将原任务从队列中移除
                        }
                        showToast("服务器响应过慢,请稍后再试...");
                    }
                    break;
                case 3:// 投资准备金申购结果查询 140
                    CheckPayProductByZhunBeiJinResult();
                    break;
            }
        }
    };

    /**
     * 投资准备金申购结果轮询
     */
    private void gotoCheckZhunBeiJinPayResult() {
        handler.postDelayed(taskResult, 3000);//3秒后执行此方法
    }

    private void cencalRedpackets() {
        for (int i = 0; i < redpacket_select.size(); i++) {
            redpacket_select.get(i).put("status", "0");
        }

    }

    private Handler LiuHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle localBundle1 = msg.getData();
            ArrayList<Parcelable> mRechargeBalance = localBundle1
                    .getParcelableArrayList(BaseParam.URL_QIAN_RECHARGE_BALANCE);// 充值到余额(52.	充值到投资准备金)
            ArrayList<Parcelable> mRechargeBalanceResult = localBundle1
                    .getParcelableArrayList(BaseParam.URL_QIAN_RECHARGE_RESULT);//  连连支付充值结果(53.	充值结果查询)
            ArrayList<Parcelable> mPayProductByZhunBeiJin = localBundle1
                    .getParcelableArrayList(BaseParam.URL_QIAN_TENDER_1XV4);// 余额购买(申购接口)
            ArrayList<Parcelable> mPayProductByZhunBeiJinResult = localBundle1
                    .getParcelableArrayList(BaseParam.URL_QIAN_TENDER_RESULT_1XV4);// 用余额购买结果查询

            if (null != mRechargeBalance) {// 充值 获取和连连支付相关的数据
                Map<String, String> map = (Map<String, String>) mRechargeBalance.get(0);
                String resultCode = map.get("resultCode");
                if (TextUtils.equals(resultCode, "1")) {
                    cancelProgress();
                    String paymentInfo = map.get(BaseParam.QIAN_RECHARGE_BALANCE_PAYMENTINFO);
                    orderNo = map.get(BaseParam.QIAN_RECHARGE_BALANCE_NO_ORDER); //订单号
                    if (null != paymentInfo) {
                        MobileSecurePayer msp = new MobileSecurePayer();
                        boolean bRet = msp.payAuth(
                                paymentInfo,
                                mHandler,
                                Constants.RQF_PAY,
                                ProductContentPurchaseAct.this,
                                false
                        );
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
                        startActivity(new Intent(ProductContentPurchaseAct.this, Login.class));

                    } else if (errorCode.equals("TOKEN_EXPIRED")) {
                        showToast("身份证验证不存在,请重新登录");
                        isLogin = true;
                        startActivity(new Intent(ProductContentPurchaseAct.this, Login.class));
                    } else {
                        showToast(errorCode);
                    }
                }
            }
            if (null != mRechargeBalanceResult) {// 充值结果查询
                Map<String, String> map = (Map<String, String>) mRechargeBalanceResult.get(0);
                String resultCode = map.get("resultCode");
                if (TextUtils.equals(resultCode, "1")) {
                    cancelProgress();
                    String status = map.get(BaseParam.QIAN_RECHARGE_RESULT_STATUS);//
                    if (TextUtils.equals(status, "0")) {// 充值失败，跳到结果页
                        showToast("申购失败");
                        finish();
                    } else if (TextUtils.equals(status, "1")) {
                        buy_money_touzizhunbeijin = Double.valueOf(buy_money - buy_money_hongbao);// 充值成功后,算出实际购买需要的钱(原价-红包)
                        payProductByZhunBeiJin();// 充值成功后直接投资准备金购买
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
                        startActivity(new Intent(ProductContentPurchaseAct.this, Login.class));

                    } else if (errorCode.equals("TOKEN_EXPIRED")) {
                        showToast("身份证验证不存在,请重新登录");
                        isLogin = true;
                        startActivity(new Intent(ProductContentPurchaseAct.this, Login.class));
                    } else {
                        showToast(errorCode);
                    }
                }
            }

            if (null != mPayProductByZhunBeiJin) {// 投资准备金购买返回

                Map<String, String> map = (Map<String, String>) mPayProductByZhunBeiJin.get(0);
                String resultCode = map.get("resultCode");
                if (TextUtils.equals(resultCode, "1")) {
                    mOrderZhunBeiJinPay = map.get(BaseParam.QIAN_TENDER_1XV4_ORDER_NO);//
                    // TODO LIU 申购订单形成，接下来轮询
                    CheckPayProductByZhunBeiJinResult();
                    resultDownZhunBeiJinResultTimes = 3;
                } else {
                    cancelProgress();
                    String errorCode = map.get("errorCode");
                    if (null == errorCode) {
                        return;
                    }
                    if (errorCode.equals("TOKEN_NOT_EXIST")) {
                        showToast("身份证验证不存在,请重新登录");
                        isLogin = true;
                        startActivity(new Intent(ProductContentPurchaseAct.this, Login.class));
                    } else if (errorCode.equals("TOKEN_EXPIRED")) {
                        showToast("身份证验证不存在,请重新登录");
                        isLogin = true;
                        startActivity(new Intent(ProductContentPurchaseAct.this, Login.class));
                    } else {
                        showToast(errorCode);
                    }
                }
            }
            if (null != mPayProductByZhunBeiJinResult) {// 余额购买结果
                Map<String, String> map = (Map<String, String>) mPayProductByZhunBeiJinResult.get(0);
                String resultCode = map.get("resultCode");
                if (TextUtils.equals(resultCode, "1")) {
                    cancelProgress();  //关闭进度条
                    String payResult = map.get(BaseParam.QIAN_TENDER_RESULT_STATUS);//
                    WebViewPayResultActivity.start(ProductContentPurchaseAct.this, "申购结果", getPayProductResultUrl("0"),brType); //打开购买结果的h5界面

                    if (TextUtils.equals(Profile.getUserIsNewHandStatus(), "1")) {// 新手购买产品后去重新初始化个人信息
                        AppTool.getUserStatusInfoRequest();
                    }
                    doLcbInvestSuccess(); //得到手机号
                    finish();
                } else if (TextUtils.equals(resultCode, "2")) {// 继续轮询
                    if (resultDownZhunBeiJinResultTimes > 0) {
                        gotoCheckZhunBeiJinPayResult();
                        resultDownZhunBeiJinResultTimes--;
                    } else {
                        // 查询次数超限
                        cancelProgress();
                        showToast("系统繁忙，请查看投资记录");
                    }
                } else {
                    cancelProgress();
                    if (resultDownZhunBeiJinResultTimes == 0) {
                        showToast("系统繁忙，请查看投资记录");
                    } else {
                        showToast("购买失败");
                    }
                    String errorCode = map.get("errorCode");
                    if (null == errorCode) {
                        return;
                    }
                    if (errorCode.equals("TOKEN_NOT_EXIST")) {
                        showToast("身份证验证不存在,请重新登录");
                        isLogin = true;
                        startActivity(new Intent(ProductContentPurchaseAct.this, Login.class));
                    } else if (errorCode.equals("TOKEN_EXPIRED")) {
                        showToast("身份证验证不存在,请重新登录");
                        isLogin = true;
                        startActivity(new Intent(ProductContentPurchaseAct.this, Login.class));
                    } else {
                        showToast(errorCode);
                    }
                }
            }
        }
    };

    private void cancelProgress() {
        if (null != progressDialog && progressDialog.isShowing()) {// 隐藏加载框
            ProductContentPurchaseAct.this.progressDialog.dismiss();
        }
    }

    /**
     * 返回红包和银行卡,把数据显示在控件上
     */
    private class MyHandler extends Handler {
        public void handleMessage(Message paramMessage) {
            Bundle localBundle1 = paramMessage.getData();

            ArrayList<Parcelable> cancel_Result = localBundle1.getParcelableArrayList(BaseParam.QIAN_REQUEAT_CHANGTENDERSTATUS);//申购失败
            String result = localBundle1.getString(BaseParam.URL_REQUEAT_MY_AVAILABLEREDPACKET);//申购选择红包和银行卡
            if (null != result) {
                if (null != progressDialog && progressDialog.isShowing()) { // 隐藏加载框
                    ProductContentPurchaseAct.this.progressDialog.dismiss();
                }
                if (result.equals("unusual")) {
                    return;
                }
                try {
                    JSONObject oj = new JSONObject(result);
                    Log.i("pepe申购选择红包和银行卡", result);
                    if (Check.jsonGetStringAnalysis(oj, "resultCode").equals("1")) {
                        JSONObject oj1 = new JSONObject(Check.jsonGetStringAnalysis(oj, "resultData"));
                        // 我的银行卡
                        bankCardList = Check.jsonGetStringAnalysis(oj1, "bankCardList");

                        double userMoneyDouble = Double.parseDouble(Check.jsonGetStringAnalysis(oj1, "balance"));
                        balance_web = df.format(userMoneyDouble);// 我账户里面的余额
                        nearlyTender = Check.jsonGetStringAnalysis(oj1, "nearlyTender");

                        tv_ye_describe.setText("余额:" + balance_web + "元");

                        // 所有银行卡 列表
                        allBankCardList = Check.jsonGetStringAnalysis(oj1, "allBankCardList");//获取所有银行卡list
                        initJsonBankCardList();

                        // 红包选择
                        JSONArray redpacket_list = oj1.getJSONArray("redPacketList");//获取红包list
                        Log.i("----红包list----", redpacket_list + "------length==" + redpacket_list.length());

                        list = new ArrayList<Map<String, String>>();  //可用红包
                        list2 = new ArrayList<Map<String, String>>(); //不可用红包
                        for (int i = 0; i < redpacket_list.length(); i++) {
                            JSONObject redpacket = redpacket_list.getJSONObject(i);
                            Log.i("----redpacket----", redpacket.toString());
                            Map<String, String> map = new HashMap<String, String>();
                            Map<String, String> map2 = new HashMap<String, String>();
                            // 状态判断的逻辑
                            if (Check.jsonGetStringAnalysis(redpacket, BaseParam.QIAN_MY_REDPACKETS_OVERDUEFLAG).equals("true")) {//红包已过期
                            } else if (Check.jsonGetStringAnalysis(redpacket, BaseParam.QIAN_MY_REDPACKETS_USEFLAG).equals("true")) {//红包已被使用
                            } else {
                                   //使用这个红包需要至少购买多少钱 300,1 得到需要30000
                                int dayLimit = (int) (Double.valueOf(Check.jsonGetStringAnalysis(redpacket, "redPacketAmount"))
                                        / Double.valueOf(Check.jsonGetStringAnalysis(redpacket, "aprLimit")) * 100);
                                if (Double.valueOf(timeLimitDay) >= Double.valueOf((Check.jsonGetStringAnalysis(redpacket, "timeLimit")))) {
                                    //可用的红包    标的期限大于红包的期限
                                    map.put("status", "0");
                                    map.put("createDate", Check.jsonGetStringAnalysis(redpacket, "createDate"));
                                    map.put("redPacketAmount", Check.jsonGetStringAnalysis(redpacket, "redPacketAmount"));
                                    map.put("redPacketType", Check.jsonGetStringAnalysis(redpacket, "redPacketType"));
                                    map.put("redpacketId", Check.jsonGetStringAnalysis(redpacket, "redpacketId"));
                                    map.put("validDate", Check.jsonGetStringAnalysis(redpacket, "validDate"));
                                    /* 红包的新加2个字段：使用期限限制和抵扣比例限制 */
                                    map.put("timeLimit", Check.jsonGetStringAnalysis(redpacket, "timeLimit"));
                                    map.put("aprLimit", Check.jsonGetStringAnalysis(redpacket, "aprLimit"));
                                    map.put("dayLimit", dayLimit + "");
                                    list.add(map);
                                } else {   //不可用红包  标的期限小于红包可使用期限
                                    map.put("status", "0");
                                    map.put("createDate", Check.jsonGetStringAnalysis(redpacket, "createDate"));
                                    map.put("redPacketAmount", Check.jsonGetStringAnalysis(redpacket, "redPacketAmount"));
                                    map.put("redPacketType", Check.jsonGetStringAnalysis(redpacket, "redPacketType"));
                                    map.put("redpacketId", Check.jsonGetStringAnalysis(redpacket, "redpacketId"));
                                    map.put("validDate", Check.jsonGetStringAnalysis(redpacket, "validDate"));
                                    map.put("timeLimit", Check.jsonGetStringAnalysis(redpacket, "timeLimit"));
                                    map.put("aprLimit", Check.jsonGetStringAnalysis(redpacket, "aprLimit"));
                                    map.put("dayLimit", dayLimit + "");
                                    list2.add(map);
                                }
                            }
                        }
                        Collections.sort(list, new SortByCount()); //按期限从小到大排序
                        list.addAll(list2);
                        /**
                         * Comparator定义一个你想要的排序规则
                         * 在Collections.sort(list, Comparator)中传入Comparator的实现的时候
                         * sort方法中会调用你的compare方法排序
                         */

                        redpacket_select = list;  //把用户拥有的已过期和未过期红包的集合赋值给它






                        JSONArray couponJsonList = oj1.getJSONArray("couponJsonList");//获取加息劵list
                        Log.i("----红包list----", couponJsonList + "------length==" + couponJsonList.length());

                        jiaxi_list = new ArrayList<Map<String, String>>();  //可用红包
                        jiaxi_list2 = new ArrayList<Map<String, String>>(); //不可用红包
                        //加息劵数据,这个时候没有金额,所以我们只能够根据标的期限进行第一波可用加息劵和不可用加息劵的分析
                        if (extraAwardApr==0){  //如果不是加息标,那么可以使用加息劵,我们再判断
                            for (int i = 0; i < couponJsonList.length(); i++) {
                                JSONObject coupon = couponJsonList.getJSONObject(i);
                                Log.i("----redpacket----", coupon.toString());
                                Map<String, String> map = new HashMap<String, String>();
                                Map<String, String> map2 = new HashMap<String, String>();
                                // 状态判断的逻辑
                                double timeMin=Double.valueOf((Check.jsonGetStringAnalysis(coupon, "timeMin")));
                                double timeMax=Double.valueOf((Check.jsonGetStringAnalysis(coupon, "timeMax")));
                                double timeLimitDay1 =Double.valueOf(timeLimitDay);

                                if (timeMax==0&&timeMin==0){ //代表没有时间限制,您随意
                                    map.put("status", "0");
                                    map.put("amountMax",Check.jsonGetStringAnalysis(coupon, "amountMax") );
                                    map.put("amountMin", Check.jsonGetStringAnalysis(coupon, "amountMin"));
                                    map.put("userCouponId", Check.jsonGetStringAnalysis(coupon, "userCouponId")); //加息劵的id,如果使用加息劵购物,就把这个值传给服务器
                                    map.put("apr", Check.jsonGetStringAnalysis(coupon, "apr"));
                                    map.put("days", Check.jsonGetStringAnalysis(coupon, "days"));
                                    map.put("name", Check.jsonGetStringAnalysis(coupon, "name")); //加息劵名称
                                    map.put("timeMax", Check.jsonGetStringAnalysis(coupon, "timeMax")); //加息劵最高使用期限
                                    /* 红包的新加2个字段：使用期限限制和抵扣比例限制 */
                                    map.put("timeMin", Check.jsonGetStringAnalysis(coupon, "timeMin"));//加息劵最低使用期限
                                    map.put("validDate", Check.jsonGetStringAnalysis(coupon, "validDate")); //过期时间
                                    jiaxi_list.add(map);
                                }else if (timeMax==0){  //最大值为0,最小值不为0,判断最小值
                                    if (timeLimitDay1>=timeMin){  //标的期限大于加息劵最小期限
                                        map.put("status", "0");
                                        map.put("amountMax",Check.jsonGetStringAnalysis(coupon, "amountMax") );
                                        map.put("amountMin", Check.jsonGetStringAnalysis(coupon, "amountMin"));
                                        map.put("userCouponId", Check.jsonGetStringAnalysis(coupon, "userCouponId")); //加息劵的id,如果使用加息劵购物,就把这个值传给服务器
                                        map.put("apr", Check.jsonGetStringAnalysis(coupon, "apr"));
                                        map.put("days", Check.jsonGetStringAnalysis(coupon, "days"));
                                        map.put("name", Check.jsonGetStringAnalysis(coupon, "name")); //加息劵名称
                                        map.put("timeMax", Check.jsonGetStringAnalysis(coupon, "timeMax")); //加息劵最高使用期限
                                                /* 红包的新加2个字段：使用期限限制和抵扣比例限制 */
                                        map.put("timeMin", Check.jsonGetStringAnalysis(coupon, "timeMin"));//加息劵最低使用期限
                                        map.put("validDate", Check.jsonGetStringAnalysis(coupon, "validDate")); //过期时间
                                        jiaxi_list.add(map);
                                    }else {
                                        map.put("status", "0");
                                        map.put("amountMax",Check.jsonGetStringAnalysis(coupon, "amountMax") );
                                        map.put("amountMin", Check.jsonGetStringAnalysis(coupon, "amountMin"));
                                        map.put("userCouponId", Check.jsonGetStringAnalysis(coupon, "userCouponId")); //加息劵的id,如果使用加息劵购物,就把这个值传给服务器
                                        map.put("apr", Check.jsonGetStringAnalysis(coupon, "apr"));
                                        map.put("days", Check.jsonGetStringAnalysis(coupon, "days"));
                                        map.put("name", Check.jsonGetStringAnalysis(coupon, "name")); //加息劵名称
                                        map.put("timeMax", Check.jsonGetStringAnalysis(coupon, "timeMax")); //加息劵最高使用期限
                                                /* 红包的新加2个字段：使用期限限制和抵扣比例限制 */
                                        map.put("timeMin", Check.jsonGetStringAnalysis(coupon, "timeMin"));//加息劵最低使用期限
                                        map.put("validDate", Check.jsonGetStringAnalysis(coupon, "validDate")); //过期时间
                                        jiaxi_list2.add(map);
                                    }

                                }else if(timeMin==0){  //加息劵最小值为0,最大值不为0
                                    if (timeLimitDay1<=timeMax){
                                        map.put("status", "0");
                                        map.put("amountMax",Check.jsonGetStringAnalysis(coupon, "amountMax") );
                                        map.put("amountMin", Check.jsonGetStringAnalysis(coupon, "amountMin"));
                                        map.put("userCouponId", Check.jsonGetStringAnalysis(coupon, "userCouponId")); //加息劵的id,如果使用加息劵购物,就把这个值传给服务器
                                        map.put("apr", Check.jsonGetStringAnalysis(coupon, "apr"));
                                        map.put("days", Check.jsonGetStringAnalysis(coupon, "days"));
                                        map.put("name", Check.jsonGetStringAnalysis(coupon, "name")); //加息劵名称
                                        map.put("timeMax", Check.jsonGetStringAnalysis(coupon, "timeMax")); //加息劵最高使用期限
                                                /* 红包的新加2个字段：使用期限限制和抵扣比例限制 */
                                        map.put("timeMin", Check.jsonGetStringAnalysis(coupon, "timeMin"));//加息劵最低使用期限
                                        map.put("validDate", Check.jsonGetStringAnalysis(coupon, "validDate")); //过期时间
                                        jiaxi_list.add(map);
                                    }else {
                                        map.put("status", "0");
                                        map.put("amountMax",Check.jsonGetStringAnalysis(coupon, "amountMax") );
                                        map.put("amountMin", Check.jsonGetStringAnalysis(coupon, "amountMin"));
                                        map.put("userCouponId", Check.jsonGetStringAnalysis(coupon, "userCouponId")); //加息劵的id,如果使用加息劵购物,就把这个值传给服务器
                                        map.put("apr", Check.jsonGetStringAnalysis(coupon, "apr"));
                                        map.put("days", Check.jsonGetStringAnalysis(coupon, "days"));
                                        map.put("name", Check.jsonGetStringAnalysis(coupon, "name")); //加息劵名称
                                        map.put("timeMax", Check.jsonGetStringAnalysis(coupon, "timeMax")); //加息劵最高使用期限
                                                /* 红包的新加2个字段：使用期限限制和抵扣比例限制 */
                                        map.put("timeMin", Check.jsonGetStringAnalysis(coupon, "timeMin"));//加息劵最低使用期限
                                        map.put("validDate", Check.jsonGetStringAnalysis(coupon, "validDate")); //过期时间
                                        jiaxi_list2.add(map);
                                    }
                                }else { //加息劵最大和最小期限都不为0
                                    if (timeLimitDay1>=timeMin&&timeLimitDay1<=timeMax){
                                        map.put("status", "0");
                                        map.put("amountMax",Check.jsonGetStringAnalysis(coupon, "amountMax") );
                                        map.put("amountMin", Check.jsonGetStringAnalysis(coupon, "amountMin"));
                                        map.put("userCouponId", Check.jsonGetStringAnalysis(coupon, "userCouponId")); //加息劵的id,如果使用加息劵购物,就把这个值传给服务器
                                        map.put("apr", Check.jsonGetStringAnalysis(coupon, "apr"));
                                        map.put("days", Check.jsonGetStringAnalysis(coupon, "days"));
                                        map.put("name", Check.jsonGetStringAnalysis(coupon, "name")); //加息劵名称
                                        map.put("timeMax", Check.jsonGetStringAnalysis(coupon, "timeMax")); //加息劵最高使用期限
                                                /* 红包的新加2个字段：使用期限限制和抵扣比例限制 */
                                        map.put("timeMin", Check.jsonGetStringAnalysis(coupon, "timeMin"));//加息劵最低使用期限
                                        map.put("validDate", Check.jsonGetStringAnalysis(coupon, "validDate")); //过期时间
                                        jiaxi_list.add(map);
                                    }else {
                                        map.put("status", "0");
                                        map.put("amountMax",Check.jsonGetStringAnalysis(coupon, "amountMax") );
                                        map.put("amountMin", Check.jsonGetStringAnalysis(coupon, "amountMin"));
                                        map.put("userCouponId", Check.jsonGetStringAnalysis(coupon, "userCouponId")); //加息劵的id,如果使用加息劵购物,就把这个值传给服务器
                                        map.put("apr", Check.jsonGetStringAnalysis(coupon, "apr"));
                                        map.put("days", Check.jsonGetStringAnalysis(coupon, "days"));
                                        map.put("name", Check.jsonGetStringAnalysis(coupon, "name")); //加息劵名称
                                        map.put("timeMax", Check.jsonGetStringAnalysis(coupon, "timeMax")); //加息劵最高使用期限
                                                /* 红包的新加2个字段：使用期限限制和抵扣比例限制 */
                                        map.put("timeMin", Check.jsonGetStringAnalysis(coupon, "timeMin"));//加息劵最低使用期限
                                        map.put("validDate", Check.jsonGetStringAnalysis(coupon, "validDate")); //过期时间
                                        jiaxi_list2.add(map);
                                    }
                                }


                            }

                            Collections.sort(jiaxi_list, new SortByApr()); //按期限从小到大排序
                            jiaxi_list.addAll(jiaxi_list2);
                            /**
                             * Comparator定义一个你想要的排序规则
                             * 在Collections.sort(list, Comparator)中传入Comparator的实现的时候
                             * sort方法中会调用你的compare方法排序
                             */
                            jiaxi_select=jiaxi_list;  //把已过去和未过期的加息劵列表赋值给它

                        }else { //是加息标,那么加息劵根本不可用
                            for (int i = 0; i < couponJsonList.length(); i++) {
                                JSONObject coupon = couponJsonList.getJSONObject(i);
                                Log.i("----coupon----", coupon.toString());
                                Map<String, String> map = new HashMap<String, String>();
                                Map<String, String> map2 = new HashMap<String, String>();


                                        map.put("status", "0");
                                        map.put("amountMax",Check.jsonGetStringAnalysis(coupon, "amountMax") );
                                        map.put("amountMin", Check.jsonGetStringAnalysis(coupon, "amountMin"));
                                        map.put("userCouponId", Check.jsonGetStringAnalysis(coupon, "userCouponId")); //加息劵的id,如果使用加息劵购物,就把这个值传给服务器
                                        map.put("apr", Check.jsonGetStringAnalysis(coupon, "apr"));
                                        map.put("days", Check.jsonGetStringAnalysis(coupon, "days"));
                                        map.put("name", Check.jsonGetStringAnalysis(coupon, "name")); //加息劵名称
                                        map.put("timeMax", Check.jsonGetStringAnalysis(coupon, "timeMax")); //加息劵最高使用期限
                                                /* 红包的新加2个字段：使用期限限制和抵扣比例限制 */
                                        map.put("timeMin", Check.jsonGetStringAnalysis(coupon, "timeMin"));//加息劵最低使用期限
                                        map.put("validDate", Check.jsonGetStringAnalysis(coupon, "validDate")); //过期时间
                                        jiaxi_list2.add(map);

                            }

                            Collections.sort(jiaxi_list2, new SortByApr()); //按加息比例从小到大排序
                            jiaxi_list.addAll(jiaxi_list2);
                            /**
                             * Comparator定义一个你想要的排序规则
                             * 在Collections.sort(list, Comparator)中传入Comparator的实现的时候
                             * sort方法中会调用你的compare方法排序
                             */
                            jiaxi_select=jiaxi_list;  //把已过去和未过期的加息劵列表赋值给它

                        }
                        isLogin=false;
                        initData();//初始化数据
                    } else {  //返回的数据错误
                        if (null == Check.jsonGetStringAnalysis(oj, "errorCode")) {
                            return;
                        }
                        if (Check.jsonGetStringAnalysis(oj, "errorCode").equals("TOKEN_NOT_EXIST")) {
                            showToast("请重新登录");
                            isLogin = true;
                            startActivity(new Intent(ProductContentPurchaseAct.this, Login.class));

                        } else if (Check.jsonGetStringAnalysis(oj, "errorCode").equals("TOKEN_EXPIRED")) {
                            showToast("请重新登录");
                            isLogin = true;
                            startActivity(new Intent(ProductContentPurchaseAct.this, Login.class));
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
                if (null != progressDialog && progressDialog.isShowing()) {// 隐藏加载框
                    ProductContentPurchaseAct.this.progressDialog.dismiss();
                }
                Map<String, String> map = (Map<String, String>) cancel_Result.get(0);
                String resultCode = map.get("resultCode");
                if (resultCode.equals("1")) {
                    if (map.get("success").equals("1")) {
                    }
                } else {
                    Check.checkMsg(map, ProductContentPurchaseAct.this);
                }
            }
            super.handleMessage(paramMessage);
        }
    }

    /**
     * 第一次访问接口和从优惠界面返回都走这个方法去修改界面
     */
    private void initData() {
        if (myApp == null) {
            myApp = MyApplication.getInstance();
        }

        if (isLogin) {  //在得到错误的token的时候,会把isLogin设置为true,并且去登录界面,登陆完了回到这个界面,需要清空原来的数据,再次请求一次数据
            oauthToken = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN, "");
            if (redpacket_select != null) {
                redpacket_select.clear();
            }
            count = 0;
            select_count = 0;
            redpacket = "";
            if (oauthToken.equals("") || oauthToken == null) {  //如果没有登录信息,就关闭当前界面
                if (redpacket_select != null) {
                    redpacket_select.clear();
                }
                finish();
            } else {
                startDataRequestRedpackets();  //因为是重新登录,所以需要再次去获取一次银行卡和红包数据
            }
        } else {
            //不需要重新登录,那么就根据数据刷新一下布局
            select_count = 0;  //当前选中的红包金额
            select_jiaxi_count=0; //当前选中的加息劵 比例
            redpacket = "";

            if (redpacket_select != null || redpacket_select.size() != 0) {
                for (int i = 0; i < redpacket_select.size(); i++) {
                    if (redpacket_select.get(i).get("status").equals("1")) {
                        select_count = select_count + Integer.parseInt(redpacket_select.get(i).get("redPacketAmount")); //所有选中的红包总的优惠金额
                        redpacket = redpacket + redpacket_select.get(i).get("redpacketId") + ","; //所有被选中的红包的id
                    }
                }
                if (redpacket.length() > 2) { //说明有红包选中了
                    redpacket = redpacket.substring(0, redpacket.length() - 1);
                }
            //判断被选中的红包所优惠的金额,如果金额是0代表没有选中红包,再去判断加息劵的时候情况,如果加息劵也没使用
                //那就根据当前用户输入的金额动态判断文字的显示问题

                if (select_count == 0) {
                    redpacket = "";

                    tv_youhui.setText("暂无可用优惠卡券");

                    // TODO: 2017/4/18 0018 在红包没有被选中的情况下 去判断加息劵的情况


                    if (jiaxi_select != null || jiaxi_select.size() != 0) {
                        for (int i = 0; i < jiaxi_select.size(); i++) {
                            if (jiaxi_select.get(i).get("status").equals("1")) {
                                select_jiaxi_count =Float.parseFloat(jiaxi_select.get(i).get("apr"));
                                userCouponId = jiaxi_select.get(i).get("userCouponId") ; //所有被选中的红包的id
                                days=Integer.parseInt(jiaxi_select.get(i).get("days"));
                            }

                        }

                        //判断被选中的红包所优惠的金额,如果金额是0代表没有选中红包,再去判断加息劵的时候情况,如果加息劵也没使用
                        //那就根据当前用户输入的金额动态判断文字的显示问题
                        if (select_jiaxi_count == 0) {
                            userCouponId = "";
                            days=0;
                            String rate = AppTool.rateReceipt(buy_money, rateapr,extraAwardApr+select_jiaxi_count, "1", timeLimitDay, 0);  //预计收益这里一直是0
                            tv_yjsy.setText(Math.abs(Double.parseDouble(rate)) + "");
                            tv_youhui.setText("暂无可用优惠卡券");
                            //发现用户从优惠界面回来既没有选择红包也没有选择加息劵,那么就根据当前的金额判断一下优惠信息
                            showYouHui();
                        } else {
                            String rate;
                            if (days==0){ //加息时间:不限制
                               String extra_money = AppTool.rateReceipt3(buy_money,select_jiaxi_count,"1",timeLimitDay,0);  //额外加息金额
                                 rate = AppTool.rateReceipt2(buy_money, rateapr,select_jiaxi_count, "1", timeLimitDay,timeLimitDay, 0);
                            }else {
                                if (days<timeLimitDay){ //加息时长小于 标剩余的时间
                                  rate = AppTool.rateReceipt2(buy_money, rateapr,select_jiaxi_count, "1", timeLimitDay,days, 0);
                                }else {     //加息时长大于标剩余的时间,那么就直接用标剩余的时长去计算
                                  rate = AppTool.rateReceipt2(buy_money, rateapr,select_jiaxi_count, "1", timeLimitDay,timeLimitDay, 0);
                                }
                            }

                            //预计收益这里一直是0
                            tv_yjsy.setText(Math.abs(Double.parseDouble(rate)) + "");
                            tv_youhui.setText("加息"+select_jiaxi_count + "%");
                        }
                    }


                } else {  //使用了红包
                    tv_youhui.setText(select_count + "元");
                    userCouponId = "";
                    days=0;
                    String rate = AppTool.rateReceipt(buy_money, rateapr,extraAwardApr+select_jiaxi_count, "1", timeLimitDay, 0);  //预计收益这里一直是0
                    tv_yjsy.setText(Math.abs(Double.parseDouble(rate)) + "");

                }
            }




            if (!TextUtils.isEmpty(myApp.bankId)) {  //没有绑定银行卡的话这个值是""
                mIsSelectedBankCard = true;
                mBankId = myApp.bankId;
                String bankName = myApp.bank_name + " ( 尾号" + myApp.hiddenCardNo + " )";
                tv_mybank_name.setText(bankName);
                if (isUsedBank) {
                    tv_bankmoney_used.setText(new DecimalFormat("0.00").format(buy_money_yinhangka) + "元");
                } else {
                    tv_bankmoney_used.setText("0.00");
                }
            }
            buy_money_hongbao = select_count;

            if (buy_money - buy_money_hongbao > 0) {
                balanceDouble_web = 0.0;
                if (TextUtils.isEmpty(balance_web)) { //判断服务端返回的账户余额是否为空
                    balanceDouble_web = 0.0;
                } else {
                    balanceDouble_web = Double.parseDouble(balance_web);
                }

                if (buy_money - buy_money_hongbao <= balanceDouble_web) {// 总钱数 前去
                    // 红包使用的
                    // 钱比
                    // 余额小
                    // //只用投资准备金就可以搞定了
                    buy_money_touzizhunbeijin = Double.valueOf(buy_money - buy_money_hongbao);
                    buy_money_yinhangka = 0.0;
                    isUsedBank = false;
                } else {
                    isUsedBank = true;
                    buy_money_touzizhunbeijin = balanceDouble_web;// 投资准备金全部用掉
                    buy_money_yinhangka = buy_money - buy_money_hongbao - buy_money_touzizhunbeijin;
                }
            }

            //如果银行卡 或者选了银行卡
            if (mIsServiceHaveBankCard || mIsSelectedBankCard) { //服务端有银行
                if (isUsedBank) {//使用银行卡里面的钱
                    tv_bankmoney_used.setText(new DecimalFormat("0.00").format(buy_money_yinhangka) + "元");
                } else {
                    tv_bankmoney_used.setText("0.00");
                }
                mIsCanCommitBtn = true;
            } else {  //服务端没有银行卡
                tv_bankmoney_used.setText("未选择");
                if (buy_money_yinhangka > 0) {  //需要用到银行卡去购买
//                    mIsCanCommitBtn = false;
                    mIsCanCommitBtn = true;
                } else {
                    mIsCanCommitBtn = true;
                }
            }

            if (mIsServiceHaveBankCard) {// 服务器有卡就不可选
//                tv_bankmoney_used.setClickable(true);
//                tv_bankmoney_used.setClickable(false);
            } else {// 服务器没卡就可选
//                tv_bankmoney_used.setClickable(true); //未选择这三个字可以被点击
            }

            if (mIsServiceHaveBankCard) {
                my_money_bank_right_arrow.setVisibility(View.INVISIBLE);
            } else {
                my_money_bank_right_arrow.setVisibility(View.VISIBLE);
            }

            if (mIsCanCommitBtn && buy_money > 0) { //按钮是可点击的,并且购买金额大于0
                purchase_btn.setClickable(true);
                purchase_btn.setBackgroundResource(R.drawable.button_org_big);
            } else {
                purchase_btn.setClickable(false);
                purchase_btn.setBackgroundResource(R.drawable.button_org_grauly);
            }
            tv_ye_used.setText(new DecimalFormat("0.00").format(buy_money_touzizhunbeijin) + "元");// 投资准备金使用额

        }
    }

    @Override
    protected void onRestart() {
        // TODO Auto-generated method stub
        super.onRestart();
        Log.e("确认申购", "onRestart");
        initData();  //初始化数据
    }

    @Override
    public void onClick(View v) {
        int vId = v.getId();
        switch (vId) {
            case R.id.tv_bankmoney_used: //未选择按钮
                if (!mIsServiceHaveBankCard) {  //如果没有绑定银行卡
                    AddYiBankAct.start(ProductContentPurchaseAct.this);
                }
                break;
            case R.id.clear:
                et_buy_money.setText("");

                break;
            case R.id.iv_select:
                if (is_select){
                    is_select=false; //状态改为未选中
                    iv_select.setBackgroundResource(R.drawable.select_default);
                }else {
                    is_select=true;
                    iv_select.setBackgroundResource(R.drawable.select_confirm);
                }
                break;
            case R.id.tv_ye_describe: //点击余额
                Double yh_balance=Double.parseDouble(balance_web);
                if (balance<100){  //标的余额小于100

                    if (yh_balance>=balance){

                        et_buy_money.setText(balance+"");
                    }else {
                        showToast("余额不足,无法购买");
                    }
                }else {  //标的余额大于100
                    if (yh_balance>=balance){
                        et_buy_money.setText(balance+"");
                    }else {
                        et_buy_money.setText( (new   Double(yh_balance)).intValue()+"");
                    }
                }

                break;
            case R.id.my_bank_btn:  //支付银行卡对应布局
                if (!mIsServiceHaveBankCard) {  //如果没有绑定银行卡
                    AddYiBankAct.start(ProductContentPurchaseAct.this);
                }
                break;
            case R.id.iv_wenhao: //查看银行卡限额

                Intent intent = new Intent(ProductContentPurchaseAct.this, ProductContentQuotaBankAct.class);
                intent.putExtra("allBankCardList", allBankCardList);
                startActivity(intent);
                break;
            case R.id.redpacket_btn://使用优惠对应的布局

                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                if (buy_money == 0) {
                    showToast("请填写申购金额");
                } else {
                    if (buy_money < lowestAccount && !yiCi) { //如果购买金额小于一百,并且标剩余金额是大于100百
                        showToast("申购金额不能低于最低申购金额");
                    } else {
                        //进入优惠界面
                        Intent first = new Intent(ProductContentPurchaseAct.this, ProductYouHui.class);

                        first.putExtra("isnew", "1");  //现在只能够使用一个红包,默认所有用户是新手
                        first.putExtra("buy_money", buy_money);  //用户输入的金额
                        first.putExtra("timeLimitDay", Integer.valueOf(timeLimitDay)); //标的期限

                        //红包的数据
                        list1 = new ArrayList<Map<String, String>>();
                        list2 = new ArrayList<Map<String, String>>();
                        int timeLimitDay1=Integer.valueOf(timeLimitDay);
                        for (int i = 0; i < redpacket_select.size(); i++) {
                            if (timeLimitDay1>= Integer.valueOf(redpacket_select.get(i).get("timeLimit"))
                                    && buy_money >= Double.valueOf(redpacket_select.get(i).get("dayLimit"))) {//标的期限大于红包可用期限,购买的金额大于使用该红包需要使用的金额
                                //可用的红包
                                list1.add(redpacket_select.get(i));
                            } else {
                                list2.add(redpacket_select.get(i));
                            }
                        }
                        first.putExtra("list1", (Serializable) list1);
                        first.putExtra("list2", (Serializable) list2);
                        first.putExtra("redpacket_select", (Serializable) redpacket_select);
                        Log.e("redpacket_select222", redpacket_select.toString());

                        if (extraAwardApr!=0){  //是加息标,所有的加息劵都不能用
                            //加息劵的数据
                            jiaxi_list1 = new ArrayList<Map<String, String>>();
                            jiaxi_list2 = new ArrayList<Map<String, String>>();
                            for (int i = 0; i < jiaxi_select.size(); i++) {
                               jiaxi_list2.add(jiaxi_select.get(i));
                            }
                            first.putExtra("jiaxi_list1", (Serializable) jiaxi_list1);
                            first.putExtra("jiaxi_list2", (Serializable) jiaxi_list2);
                            first.putExtra("jiaxi_select", (Serializable) jiaxi_select);
                            first.putExtra("extraAwardApr",extraAwardApr);
                        }else { //不是加息标,可以使用加息劵
                            //加息劵的数据
                            jiaxi_list1 = new ArrayList<Map<String, String>>();
                            jiaxi_list2 = new ArrayList<Map<String, String>>();
                            for (int i = 0; i < jiaxi_select.size(); i++) {
                                int timeMin = Integer.valueOf(jiaxi_select.get(i).get("timeMin"));
                                int timeMax = Integer.valueOf(jiaxi_select.get(i).get("timeMax"));
                                int amountMax = Integer.valueOf(jiaxi_select.get(i).get("amountMax"));
                                int amountMin = Integer.valueOf(jiaxi_select.get(i).get("amountMin"));


                                if (timeMax == 0 && timeMin == 0) { //代表没有时间限制,那就判断金额

                                    if (amountMin==0&&amountMax==0){ //代表金额也没有限制
                                        jiaxi_list1.add(jiaxi_select.get(i));
                                    }else if(amountMin==0){ //金额最小值为0,最大值不为0  金额<= 1000
                                        if (buy_money <= amountMax){ //这张加息劵可用
                                            jiaxi_list1.add(jiaxi_select.get(i));
                                        }else {
                                            jiaxi_list2.add(jiaxi_select.get(i));
                                        }
                                    }else if (amountMax==0){ //金额最大值为0,最小值不为0  金额>=1000
                                        if (buy_money >= amountMin){ //这张加息劵可用
                                            jiaxi_list1.add(jiaxi_select.get(i));
                                        }else {
                                            jiaxi_list2.add(jiaxi_select.get(i));
                                        }
                                    }else {  //最大值和最小值都不为0
                                        if (buy_money>=amountMin&&buy_money<=amountMax ){ //这张加息劵可用
                                            jiaxi_list1.add(jiaxi_select.get(i));
                                        }else {
                                            jiaxi_list2.add(jiaxi_select.get(i));
                                        }
                                    }

                                } else if (timeMax == 0) {  //加息劵的最大值为0那么就只需要判断最低期限就行了,最小值不为0,判断最小值
                                    if (timeLimitDay1 >= timeMin) {  //标的期限大于加息劵最小期限,判断金额

                                        if (amountMin==0&&amountMax==0){ //代表金额也没有限制
                                            jiaxi_list1.add(jiaxi_select.get(i));
                                        }else if(amountMin==0){ //金额最小值为0,最大值不为0  金额<= 1000
                                            if (buy_money <= amountMax){ //这张加息劵可用
                                                jiaxi_list1.add(jiaxi_select.get(i));
                                            }else {
                                                jiaxi_list2.add(jiaxi_select.get(i));
                                            }
                                        }else if (amountMax==0){ //金额最大值为0,最小值不为0  金额>=1000
                                            if (buy_money >= amountMin){ //这张加息劵可用
                                                jiaxi_list1.add(jiaxi_select.get(i));
                                            }else {
                                                jiaxi_list2.add(jiaxi_select.get(i));
                                            }
                                        }else {  //最大值和最小值都不为0
                                            if (buy_money>=amountMin&&buy_money<=amountMax ){ //这张加息劵可用
                                                jiaxi_list1.add(jiaxi_select.get(i));
                                            }else {
                                                jiaxi_list2.add(jiaxi_select.get(i));
                                            }
                                        }


                                    } else {
                                        jiaxi_list2.add(jiaxi_select.get(i));
                                    }

                                } else if (timeMin == 0) {  //加息劵最低期限是0,那么只需要判断最高期限就行了
                                    if (timeLimitDay1 <= timeMax) { //期限没问题,判断金额

                                        if (amountMin==0&&amountMax==0){ //代表金额也没有限制
                                            jiaxi_list1.add(jiaxi_select.get(i));
                                        }else if(amountMin==0){ //金额最小值为0,最大值不为0  金额<= 1000
                                            if (buy_money <= amountMax){ //这张加息劵可用
                                                jiaxi_list1.add(jiaxi_select.get(i));
                                            }else {
                                                jiaxi_list2.add(jiaxi_select.get(i));
                                            }
                                        }else if (amountMax==0){ //金额最大值为0,最小值不为0  金额>=1000
                                            if (buy_money >= amountMin){ //这张加息劵可用
                                                jiaxi_list1.add(jiaxi_select.get(i));
                                            }else {
                                                jiaxi_list2.add(jiaxi_select.get(i));
                                            }
                                        }else {  //最大值和最小值都不为0
                                            if (buy_money>=amountMin&&buy_money<=amountMax ){ //这张加息劵可用
                                                jiaxi_list1.add(jiaxi_select.get(i));
                                            }else {
                                                jiaxi_list2.add(jiaxi_select.get(i));
                                            }
                                        }

                                    } else {
                                        jiaxi_list2.add(jiaxi_select.get(i));
                                    }
                                } else { //加息劵最大和最小期限都不为0
                                    if (timeLimitDay1 >= timeMin && timeLimitDay1 <= timeMax) {

                                        if (amountMin==0&&amountMax==0){ //代表金额也没有限制
                                            jiaxi_list1.add(jiaxi_select.get(i));
                                        }else if(amountMin==0){ //金额最小值为0,最大值不为0  金额<= 1000
                                            if (buy_money <= amountMax){ //这张加息劵可用
                                                jiaxi_list1.add(jiaxi_select.get(i));
                                            }else {
                                                jiaxi_list2.add(jiaxi_select.get(i));
                                            }
                                        }else if (amountMax==0){ //金额最大值为0,最小值不为0  金额>=1000
                                            if (buy_money >= amountMin){ //这张加息劵可用
                                                jiaxi_list1.add(jiaxi_select.get(i));
                                            }else {
                                                jiaxi_list2.add(jiaxi_select.get(i));
                                            }
                                        }else {  //最大值和最小值都不为0
                                            if (buy_money>=amountMin&&buy_money<=amountMax ){ //这张加息劵可用
                                                jiaxi_list1.add(jiaxi_select.get(i));
                                            }else {
                                                jiaxi_list2.add(jiaxi_select.get(i));
                                            }
                                        }

                                    } else {
                                        jiaxi_list2.add(jiaxi_select.get(i));
                                    }
                                }
                            }
                            first.putExtra("jiaxi_list1", (Serializable) jiaxi_list1);
                            first.putExtra("jiaxi_list2", (Serializable) jiaxi_list2);
                            first.putExtra("jiaxi_select", (Serializable) jiaxi_select);
                            first.putExtra("extraAwardApr", (Serializable) extraAwardApr);
                        }


                        Log.e("jiaxi_select", jiaxi_select.toString());

                        startActivityForResult(first, STAFF_AUTHORITY_REQUEST_CODE);
                    }

                }

                break;
            /* 确认去支付按钮  弹出一个对话框 打开软键盘 */
            case R.id.purchase_btn:
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                // TODO Auto-generated method stub 购买金额
                String money = et_buy_money.getText().toString().trim();
                if (money.equals("") || money == null) {
                    return;
                }
                float a = (float) (balance);// 产品可投金额
                float b = Float.parseFloat(balance_web);// 我的余额

                if (balance < lowestAccount) {   // 产品剩余金额小于100，不做限制

                } else { // 产品剩余金额大于等于100，
                    if (buy_money < lowestAccount) { //购买金额小于100
                        showToast("申购金额不能低于最低申购金额");
                        return;
                    }
                }
                if (buy_money > a) {
                    showToast("申购金额不可以超过最大可申购金额");
                    return;
                }
                resultDown = 3;
                resultDown1 = 3;

                if (TextUtils.equals(Profile.getUserPayPassWordStatus(), "0")) {    // 未设置交易密码
                    Intent forget = new Intent(ProductContentPurchaseAct.this, ForgetPasswordAct.class);
                    startActivity(forget);
                } else {  //设置了交易密码,准备买东西了
                            if (isUsedBank){  //需要使用到银行卡
                                 if (!mIsServiceHaveBankCard) { // 服务端没有银行卡,就弹出对话框
                                    LayoutInflater inflaterDl = LayoutInflater.from(ProductContentPurchaseAct.this);
                                    LinearLayout layout = (LinearLayout) inflaterDl.inflate(R.layout.dialog_bind_bank_layout, null);
                                    final Dialog dialog = new AlertDialog.Builder(ProductContentPurchaseAct.this).create();
                                    dialog.show();
                                    dialog.getWindow().setContentView(layout);
                                    Button cancel = (Button) layout.findViewById(R.id.negativeButton);
                                    Button sure = (Button) layout.findViewById(R.id.positiveButton);
                                    cancel.setOnClickListener(new OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialog.dismiss();
                                        }
                                    });
                                    sure.setOnClickListener(new OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialog.dismiss();
//                                            Intent intent2 = new Intent(context, ProductAddBankAct.class);
//                                            startActivityForResult(intent2, 301);
                                            AddYiBankAct.start(ProductContentPurchaseAct.this);
                                        }
                                    });
                                }else {
                                     if (is_select){
                                         showDialog();// 先提示输入交易密码
                                     }else {
                                         Toast.makeText(ProductContentPurchaseAct.this, "请先阅读以下相关协议,并勾选", Toast.LENGTH_SHORT).show();
                                     }

                                 }
                            }else{  //不需要使用银行卡

                                if (is_select){
                                    showDialog();// 先提示输入交易密码
                                }else {
                                    Toast.makeText(ProductContentPurchaseAct.this, "请先阅读以下相关协议,并勾选", Toast.LENGTH_SHORT).show();
                                }
                            }

                }

                break;
            default:
                break;
        }
    }

    /**
     *   红包按照从小到大排序
     */
class SortByCount implements Comparator {
        public int compare(Object o1, Object o2) {
            Map<String, String> s1 = (Map<String, String>) o1;
            Map<String, String> s2 = (Map<String, String>) o2;
            Double counts1 = Double.valueOf(s1.get("dayLimit"));
            Double counts2 = Double.valueOf(s2.get("dayLimit"));

            if (counts1 > counts2) {
                return 1;
            } else if (counts1 < counts2) {
                return -1;
            }
            return 0;
        }
    }

/**
 *   红包按照从小到大排序
 */
class SortByApr implements Comparator {
    public int compare(Object o1, Object o2) {
        Map<String, String> s1 = (Map<String, String>) o1;
        Map<String, String> s2 = (Map<String, String>) o2;
        Double counts1 = Double.valueOf(s1.get("apr"));
        Double counts2 = Double.valueOf(s2.get("apr"));

        if (counts1 > counts2) {
            return 1;
        } else if (counts1 < counts2) {
            return -1;
        }
        return 0;
    }
}

    private void doLcbInvestSuccess() {
        SharedPreferences sp = context.getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_USER, MODE_PRIVATE);
        String phone = sp.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_PHONE, "");
    }

    @Override  //从红包界面返回的
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (resultCode == RESULT_OK && requestCode == STAFF_AUTHORITY_REQUEST_CODE) { //从红包界面返回数据
            /* 取得返回的权限 */
            Bundle bundle = intent.getExtras(); //接收数据
            redpacket_select = (List<Map<String, String>>) bundle.getSerializable("redpacket_select");
            refreshJiaXi();
            Log.e("redpacket_select111", redpacket_select.toString());
//            initData();
        }else if (resultCode == 2 && requestCode == STAFF_AUTHORITY_REQUEST_CODE){ //从加息界面返回的数据
            Bundle bundle = intent.getExtras();
            jiaxi_select = (List<Map<String, String>>) bundle.getSerializable("jiaxi_select");
            refreshRed();
            Log.e("jiaxi_select111", jiaxi_select.toString());
//            initData();
        }
    }

    /**
     *  刷新加息界面,把所有已选中的改为未选中
     */
    public void refreshJiaXi(){
            if (jiaxi_select!=null&&jiaxi_select.size()!=0){
                int length=jiaxi_select.size();
                for (int i=0;i<length;i++){
                    if (jiaxi_select.get(i).get("status").equals("1")) {
                        jiaxi_select.get(i).put("status", "0");
                    }
                }
            }
    }
    /**
     *  刷新加息界面,把所有已选中的改为未选中
     */
    public void refreshRed(){
        if (redpacket_select!=null&&redpacket_select.size()!=0){
            int length=redpacket_select.size();
            for (int i=0;i<length;i++){
                if (redpacket_select.get(i).get("status").equals("1")) {
                    redpacket_select.get(i).put("status", "0");
                }
            }
        }
    }

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
        TextView forget_jypassword = (TextView) dialog.findViewById(R.id.forget_jypassword);
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
                gotoPay();

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
}
