package com.rd.qnz.mine;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.qiyukf.nimlib.sdk.NimIntent;
import com.qiyukf.unicorn.api.Unicorn;
import com.qiyukf.unicorn.api.UnreadCountChangeListener;
import com.rd.qnz.NewStartAct;
import com.rd.qnz.R;
import com.rd.qnz.WebBannerViewAct;
import com.rd.qnz.WebViewAct;
import com.rd.qnz.custom.APIModel;
import com.rd.qnz.custom.KeyPatternActivity;
import com.rd.qnz.custom.MineShow;
import com.rd.qnz.custom.MyApplication;
import com.rd.qnz.custom.Profile;
import com.rd.qnz.dialog.CustomProgressDialog;
import com.rd.qnz.dialog.GetDialog;
import com.rd.qnz.login.Login;
import com.rd.qnz.mine.setting.SettingAct;
import com.rd.qnz.tools.AppTool;
import com.rd.qnz.tools.BaseParam;
import com.rd.qnz.tools.Check;
import com.rd.qnz.tools.ChildClickableLinearLayout;
import com.rd.qnz.tools.webservice.JsonRequeatThreadProductMy;
import com.rd.qnz.tools.webservice.JsonRequeatThreadPublishUserNotice;
import com.rd.qnz.tools.webservice.JsonRequeatThreadUpdateUserNoticeRead;
import com.rd.qnz.view.CircularNetworkImageView;
import com.rd.qnz.webview.ShareWebViewActivity;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import static com.handmark.pulltorefresh.library.PullToRefreshBase.Mode.DISABLED;
import static com.rd.qnz.tools.BaseParam.URL_QIAN_API_APPID;


@SuppressLint("NewApi")
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MineAct extends KeyPatternActivity implements OnClickListener {
    private static final String TAG = "钱袋";

    private ArrayList<String> param = null;
    private ArrayList<String> value = null;
    private MyHandler myHandler;
    private CustomProgressDialog progressDialog = null;
    private GetDialog dia;
    private MyApplication myApp;
    private Context context;
    APIModel apiModel = new APIModel();
    private String phone;
    private TextView my_useMoney;// 用户在投资金
    private TextView my_day_money;
    private TextView my_count_money;
    private String oauthToken, user_phone, avayar_url, user_id, realStatus, cardId;
    private CircularNetworkImageView iv_head;
    private SharedPreferences preferences;
    private String latestDate;



    private String currentMonthHasCash;

    private RelativeLayout  mine_more, my_redpackets_btn_war, mine_customer_service,mine_yaoqing;
    private RelativeLayout tender_btn;
    private LinearLayout total_assets, mine_income;
    private String useMoney;// 投资中资产
    private String noticeid;
    private PullToRefreshScrollView my_scroll;
    private TextView my_account_balance;// 账户余额
    private Double investingCapital, disposeCash, balance, investingWaitInterest;
    private Double received, useRedpacketSum, cashreward, tobe_cashreward;


    DecimalFormat df = new DecimalFormat("0.00");
    private TextView tvUnread;   //客服未读消息数





    /**
     * 自动投标
     * @param savedInstanceState
     */
    private RelativeLayout  rl_auto_toubiao;
    private boolean isopen=false; //自动投资是否开启
    private TextView tv_auto;

    /**
     * Viewpager
     */
    private LinearLayout linear_top; //顶部的布局
    private List<View> viewpager_list;
    private List<View> point_list;
    private int last_point=0; //上一个原点的位置,默认是0
    private SpannableString all_money; //总资产
    private SpannableString count_money; //我的收益



    private Double interestNormal,interestExtra,waitInterestNormal,waitInterestExtra; //已收基础利息,已收额外利息,待收基础利息,待收额外利息

    private int redCount; //可用红包的个数
    private int couponCount; //可用加息劵的个数

    /**
     * 1.5.1 测评分数
     * @param savedInstanceState
     */
    private int score;

    /**
     * 1.6.0新版本
     *
     */
    private ChildClickableLinearLayout line_top;
    private RelativeLayout repay_btn; //回款日历
    private TextView tv_phone;
    private RelativeLayout my_management_btn;
    private RelativeLayout more_help; //常见问题(帮助中心)
    private ImageView iv_home_eye;
    private boolean eye_open=true; //金额可见
    private Double kyye; //可用余额
    private LinearLayout rl_mybalance;
    private LinearLayout rl_nologin;//没有登录的时候对应的布局
    private Button btn_login;
    private RelativeLayout rl_top; //顶部透明的view
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.minehome2);
        line_top= (ChildClickableLinearLayout) findViewById(R.id.line_top);
        rl_nologin= (LinearLayout) findViewById(R.id.rl_nologin);
        btn_login= (Button) findViewById(R.id.btn_login);
        btn_login.setOnClickListener(this);
        myApp = (MyApplication) getApplication();

        this.myHandler = new MyHandler();
        context = MineAct.this;
        this.dia = new GetDialog();

        if (myApp.redPacketOpen.equals("1")) {
            dialogShow();  //弹出红包开关
            myApp.redPacketOpen = "";
        } else {
            myApp.redPacketOpen = "";
        }

         registerBoradcastReceiver();
         intView();            //做一下findviewByid
        //点击我的钱袋界面,如果发现还没有登录显示遮罩层
        if (Profile.getoAutoToken().equals("") || Profile.getoAutoToken() == null) {
            rl_nologin.setVisibility(View.VISIBLE);
            line_top.setChildClickable(false);
            my_scroll.setMode(Mode.DISABLED); //不能上拉和下拉
        }else {

            my_scroll.setMode(Mode.PULL_FROM_START); //只能够下拉刷新
            rl_nologin.setVisibility(View.GONE);
            line_top.setChildClickable(true);
            //获取sp_user.xml的数据
            preferences = getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_USER, Context.MODE_PRIVATE);
            latestDate = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_LATESTDATE_XIAOXI, "");
            oauthToken = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN, "");
            realStatus = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_REALSTATUS, "");
            user_phone = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_PHONE, "");
            user_id = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_USERID, "");
            cardId = preferences.getString("cardId", "");
            startDataRequest();   //请求一下我的内助接口
        }
    }

    @Override
    protected void onRestart() {
        // TODO Auto-generated method stub
        super.onRestart();

        //点击我的钱袋界面,如果发现还没有登录显示遮罩层
        if (Profile.getoAutoToken().equals("") || Profile.getoAutoToken() == null) {
            rl_nologin.setVisibility(View.VISIBLE);
            line_top.setChildClickable(false);
            my_scroll.setMode(Mode.DISABLED);

        }else {
            rl_nologin.setVisibility(View.GONE);
            line_top.setChildClickable(true);
            my_scroll.setMode(Mode.PULL_FROM_START);
            //获取sp_user.xml的数据
            preferences = getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_USER, Context.MODE_PRIVATE);
            latestDate = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_LATESTDATE_XIAOXI, "");
            oauthToken = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN, "");
            realStatus = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_REALSTATUS, "");
            user_phone = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_PHONE, "");
            user_id = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_USERID, "");
            cardId = preferences.getString("cardId", "");
            startDataRequest();   //请求一下我的内助接口

            preferences = getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_USER, Context.MODE_PRIVATE);
            AppTool.getUserStatusInfoRequest();
            preferences = getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_USER, Context.MODE_PRIVATE);
            user_phone = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_PHONE, "");
            realStatus = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_REALSTATUS, "");
            user_id = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_USERID, "");
            latestDate = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_LATESTDATE_XIAOXI, "");
            init();
        }

        Log.e(TAG, "onRestart");


        if (myApp.redPacketOpen.equals("1")) {
            dialogShow();
            myApp.redPacketOpen = "";
        } else {
            myApp.redPacketOpen = "";
        }



    }


    @SuppressWarnings("deprecation")
    public void intView() {
        // TODO: 2017/4/20 0020   viewpager

        mine_income= (LinearLayout) findViewById(R.id.mine_income);
        total_assets= (LinearLayout) findViewById(R.id.total_assets);
        total_assets.setOnClickListener(this);
        mine_income.setOnClickListener(this);
        linear_top= (LinearLayout) findViewById(R.id.linear_top); //顶部的linearLayout布局

        iv_home_eye=(ImageView) findViewById(R.id.iv_home_eye);
        iv_home_eye.setOnClickListener(this);
        repay_btn= (RelativeLayout) findViewById(R.id.repay_btn);
        repay_btn.setOnClickListener(this);

        more_help= (RelativeLayout) findViewById(R.id.more_help);
        more_help.setOnClickListener(this);
        my_management_btn= (RelativeLayout) findViewById(R.id.my_management_btn);
        my_management_btn.setOnClickListener(this);
        iv_head = (CircularNetworkImageView) findViewById(R.id.mine_info_portrait_iv);


        my_account_balance = (TextView) findViewById(R.id.my_account_balance);
        my_useMoney = (TextView) findViewById(R.id.my_useMoney);

        rl_mybalance= (LinearLayout) findViewById(R.id.rl_mybalance);
        rl_mybalance.setOnClickListener(this);

        my_count_money = (TextView) findViewById(R.id.my_count_money);

        tender_btn = (RelativeLayout) findViewById(R.id.tender_btn); /* 投资记录 */
        tender_btn.setOnClickListener(this);
        tv_auto= (TextView) findViewById(R.id.tv_auto);
        tv_phone= (TextView) findViewById(R.id.tv_phone);

        // 余额明细

        // 充值
        findViewById(R.id.account_balance_recharge).setOnClickListener(this);
        // 提现
        findViewById(R.id.account_balance_bottom_lift).setOnClickListener(this);
        // 账户中心


        /* 我的红包及奖励 */
        my_redpackets_btn_war = (RelativeLayout) findViewById(R.id.my_redpackets_btn_war);
        my_redpackets_btn_war.setOnClickListener(this);
        mine_more = (RelativeLayout) findViewById(R.id.mine_more);
        mine_more.setOnClickListener(this);
        mine_yaoqing= (RelativeLayout) findViewById(R.id.mine_yaoqing);
        mine_yaoqing.setOnClickListener(this);


        mine_customer_service = (RelativeLayout) findViewById(R.id.mine_customer_service);
        mine_customer_service.setOnClickListener(this);
        tvUnread = (TextView) findViewById(R.id.tv_unread);

        rl_auto_toubiao= (RelativeLayout) findViewById(R.id.rl_auto_toubiao);
        rl_auto_toubiao.setOnClickListener(this);

        //七鱼
        Unicorn.addUnreadCountChangeListener(mUnreadCountListener, true);  //添加一下监听来跟踪未读数变化，更新界面
        updateUnreadCount(Unicorn.getUnreadCount());
        parseIntent();

        iv_head.setImageResource(R.drawable.person);



        my_scroll = (PullToRefreshScrollView) findViewById(R.id.my_scroll);
        my_scroll.setMode(Mode.PULL_DOWN_TO_REFRESH); //只能够下拉刷新
        my_scroll.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ScrollView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                // TODO Auto-generated method stub
                refreshView.getLoadingLayoutProxy().setLoadingDrawable(getResources().getDrawable(R.drawable.default_ptr_rotate));
                refreshView.getLoadingLayoutProxy().setPullLabel("释放财运刷新");
                refreshView.getLoadingLayoutProxy().setRefreshingLabel("财运刷新中...");
                refreshView.getLoadingLayoutProxy().setReleaseLabel("释放财运刷新");
                startDataRequest();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                // TODO Auto-generated method stub
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel("  ");
                refreshView.getLoadingLayoutProxy().setLoadingDrawable(null);
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel("");
                refreshView.getLoadingLayoutProxy().setPullLabel("");
                refreshView.getLoadingLayoutProxy().setRefreshingLabel("");
                refreshView.getLoadingLayoutProxy().setReleaseLabel("");
                my_scroll.onRefreshComplete();
            }
        });
    }








    /**
     * 获取我的钱袋数据
     */
    private void startDataRequest() {
        if (Check.hasInternet(MineAct.this)) {
            MineAct.this.initArray();
            MineAct.this.param.add(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN);
            MineAct.this.value.add(Profile.getoAutoToken());
            MineAct.this.param.add(URL_QIAN_API_APPID);
            MineAct.this.value.add(myApp.appId);
            MineAct.this.param.add(BaseParam.URL_QIAN_API_SERVICE);
            MineAct.this.value.add("myAccount");
            MineAct.this.param.add(BaseParam.URL_QIAN_API_SIGNTYPE);
            MineAct.this.value.add(myApp.signType);

            String[] array = new String[]{
                    BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN + "=" + Profile.getoAutoToken(),
                    URL_QIAN_API_APPID + "=" + myApp.appId, BaseParam.URL_QIAN_API_SERVICE + "=myAccount",
                    BaseParam.URL_QIAN_API_SIGNTYPE + "=" + myApp.signType};

            String sign = apiModel.sortStringArray(array);
            MineAct.this.param.add(BaseParam.URL_QIAN_API_SIGN);
            MineAct.this.value.add(sign);
            new Thread(new JsonRequeatThreadProductMy(
                    MineAct.this,
                    myApp,
                    MineAct.this.myHandler,
                    MineAct.this.param,
                    MineAct.this.value)
            ).start();
        } else {
            MineShow.toastShow("请检查网络连接是否正常", context);
        }
    }


    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG); // 统计页面
        MobclickAgent.onResume(this); // 统计时长
//        startDataRequest();
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(TAG); // 保证 onPageEnd 在onPause 之前调用,因为 onPause
        // 中会保存信息
        MobclickAgent.onPause(this);
    }

    private long exitTime = 0;

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - exitTime > 2000) {
            MineShow.toastShow("再按一次退出程序", getApplicationContext());
            exitTime = System.currentTimeMillis();
        } else {
            finish();
            System.exit(0);
        }
    }

    private SpannableString changTVsize(String value) {
        SpannableString spannableString = new SpannableString(value);
        if (value.contains(".")) {
            spannableString.setSpan(new RelativeSizeSpan(0.86f), value.indexOf("."), value.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannableString;
    }

    private String formatMoney(Double money) {
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(money);
    }

    private class MyHandler extends Handler {
        public void handleMessage(Message paramMessage) {
            Bundle localBundle1 = paramMessage.getData();
            ArrayList<Parcelable> product_R = localBundle1.getParcelableArrayList(BaseParam.QIAN_REQUEAT_MY);  //我的内助
            ArrayList<Parcelable> publishUserNotice = localBundle1.getParcelableArrayList(BaseParam.QIAN_REQUEAT_PUBLISHUSERNOTICE); //针对手机端的公告
            if (null != progressDialog && progressDialog.isShowing()) {// 隐藏加载框
                MineAct.this.progressDialog.dismiss();
            }
            if (null != publishUserNotice) {    //针对手机端的公告
                @SuppressWarnings("unchecked")
                List<Map<String, String>> list1 = (List<Map<String, String>>) publishUserNotice.get(0);
                Map<String, String> map = list1.get(0);
                String resultCode = map.get("resultCode");
                Log.d("resultCode值", resultCode);
                final String type = map.get("type");
                final String url = map.get("url");
                final String title = map.get("title");
                if (resultCode.equals("1")) {   //弹出一个对话框
                    noticeid = map.get(BaseParam.QIAN_MY_NOTICEID);
                    LayoutInflater inflaterDl = LayoutInflater.from(MineAct.this);
                    RelativeLayout layout = (RelativeLayout) inflaterDl.inflate(R.layout.my_dialog_notice, null);
                    final Dialog dialog = new AlertDialog.Builder(MineAct.this).create();
                    dialog.show();
                    dialog.getWindow().setContentView(layout);
                    TextView notice_message = (TextView) dialog.findViewById(R.id.notice_message);
                    TextView notice_title = (TextView) dialog.findViewById(R.id.notice_title);
                    notice_title.setText(map.get(BaseParam.QIAN_MY_TITLE));
                    notice_message.setText(map.get(BaseParam.QIAN_MY_CONTENT));
                    Button confirm = (Button) dialog.findViewById(R.id.confirm);
                    confirm.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            // TODO Auto-generated method stub
                            if ("0".equals(type)) {
                                Intent intent = new Intent(MineAct.this, MyTenderRecordListAct.class);
                                intent.putExtra("isFlag", false);
                                startActivity(intent);
                            } else {
                                if ("".equals(url) || null == url) {
                                } else {   //打开h5
                                    Intent intent = new Intent(context, WebBannerViewAct.class);
                                    intent.putExtra("web_url", url);
                                    intent.putExtra("title", title);
                                    startActivity(intent);
                                }
                                startDataRequestUpdateUserNoticeRead();
                            }
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
            }
            if (null != product_R) {
                @SuppressWarnings("unchecked")
                Map<String, String> map = (Map<String, String>) product_R.get(0);
                //判断当前时间是上午还是下午
                long time = System.currentTimeMillis();
                final Calendar mCalendar = Calendar.getInstance();
                mCalendar.setTimeInMillis(time);
                int apm = mCalendar.get(Calendar.AM_PM);
                if (apm == 0) {
                } else if (apm == 1) {
                }



                String resultCode = map.get("resultCode");
                if (resultCode.equals("1")) {
                    if (TextUtils.equals(Profile.getUserNeedPopStatus(), "1")) {// 设置单卡 我本地的needPopStatus 是0
                        // 单卡设置页 "1"为设置
                        SettingOneBankcardActivity.start(MineAct.this);
                    }// TODO LIU 为了方便测试，先不弹出设置单卡了
                    Context ctx = MineAct.this;
                    SharedPreferences sp = ctx.getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_USER, MODE_PRIVATE);
                    Editor editor = sp.edit();
                    latestDate = map.get(BaseParam.QIAN_SHAREDPREFERENCES_USER_LATESTDATE);
                    editor.commit();
                    phone = map.get(BaseParam.QIAN_MY_PHONE);
                    // TODO: 2017/4/20 0020
                    if (map.get("borrowAuto").equals("1")){
                        tv_auto.setText("开启");
                    }else {
                        tv_auto.setText("关闭");
                    }
                    score=Integer.parseInt(map.get("score"));


                    tv_phone.setText(map.get("phone").replace(map.get("phone").substring(3, 7), "****"));

                    interestNormal=Math.abs(Double.parseDouble(map.get("interestNormal"))); ////已收基础利息0.0
                    interestExtra=Math.abs(Double.parseDouble(map.get("interestExtra"))); ////已收额外利息 0.0
                    waitInterestNormal=Math.abs(Double.parseDouble(map.get("waitInterestNormal"))); ////待收基础利息 4.6
                    waitInterestExtra=Math.abs(Double.parseDouble(map.get("waitInterestExtra"))); ////待收额外利息 0.0


                    double use = Double.parseDouble(map.get(BaseParam.QIAN_MY_COLLECTION)); //collection:当前持有资产0.0
                    useMoney = df.format(use);


                    avayar_url = BaseParam.URL_QIAN + map.get(BaseParam.QIAN_MY_AVAYARURL);  //头像url  https://testqnz.qianneizhu.com//data/images/avatar/app_default_portrait.png

                     ImageLoader.getInstance().displayImage(avayar_url, iv_head, new ImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String s, View view) {

                        }

                        @Override
                        public void onLoadingFailed(String s, View view, FailReason failReason) {
                            myApp.bt_head=null;
                            iv_head.setImageResource(R.drawable.person);
                        }

                        @Override
                        public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                            myApp.bt_head=bitmap;
                            iv_head.setImageBitmap(bitmap);
                        }

                        @Override
                        public void onLoadingCancelled(String s, View view) {

                        }
                    });



                    currentMonthHasCash = map.get("currentMonthHasCash");  //已提现次数

                    redCount=Integer.parseInt(map.get(BaseParam.QIAN_MY_REDPACKETCOUNT));//可用红包个数
                    couponCount=Integer.parseInt(map.get("couponCount")); //可用优惠券个数

                    if (map.get("balanceChangeTimes").equals("0")) {  //新入账记录

                    } else {

                    }

                    if (map.get("todayCollectionNum") != null && map.get("todayCollectionNum").equals("0")) {
                    } else {
                    }

                    try {
                        disposeCash = Math.abs(Double.parseDouble(map.get(BaseParam.QIAN_ACCOUNT_BALANCE_KEY_DISPOSECASH))); //提现中金额0.0
                        balance = Math.abs(Double.parseDouble(map.get(BaseParam.QIAN_MY_BALANCE)));  //账户余额 998900
                        investingWaitInterest = Math.abs(Double.parseDouble(map.get(BaseParam.QIAN_MY_INVESTING_WAIT_INTEREST)));//待收总利息0.0
                        investingCapital = Math.abs(Double.parseDouble(map.get(BaseParam.QIAN_MY_INVESTINGCAPITAL))); //待收本金 0.0
                        all_money=changTVsize(formatMoney(disposeCash + balance + investingWaitInterest + investingCapital)); //总资产 998900
                        my_useMoney.setText(formatMoney(disposeCash + balance + investingWaitInterest + investingCapital));  //
//                        my_useMoney.setText(changTVsize(formatMoney(disposeCash + balance + investingWaitInterest + investingCapital)));  //总资产

                        received = Math.abs(Double.parseDouble(map.get("accumulatedIncome")));  //已收利息 0.0
                        useRedpacketSum = Math.abs(Double.parseDouble(map.get("useRedpacketSum"))); //已用红包0.0
                        cashreward = Math.abs(Double.parseDouble(map.get("hasGetExtraAward")));
                        tobe_cashreward = Math.abs(Double.parseDouble(map.get("notGetExtraAward")));
                        my_count_money.setText(formatMoney(received + investingWaitInterest + useRedpacketSum)); //我的收益
//                        my_count_money.setText(changTVsize(formatMoney(received + investingWaitInterest + useRedpacketSum))); //我的收益
                        count_money=changTVsize(formatMoney(received + investingWaitInterest + useRedpacketSum));//0.0
                    } catch (Exception e) {
                        Log.d("jsonGetStringAnalysis", "参数" + param + "不存在");
                    }
                    kyye=Double.parseDouble(map.get(BaseParam.QIAN_MY_BALANCE));
                    my_account_balance.setText(df.format(kyye)); //可用余额
                    // my_day_money.setText(df.format(Double.parseDouble(map.get(BaseParam.QIAN_MY_TENDER_YES_INTEREST))));// 昨日收益

                    if (myApp.isMyGongGao) {  //每次启动都检测一下公告
                        myApp.isMyGongGao = false;
                        startDataRequestPublishUserNotice();//  	发布公告给手机客户端-（针对已登录用户）
                    }
                } else {
                    if (map.get("errorCode").equals("TOKEN_NOT_EXIST")) {
                        MineShow.toastShow("身份证验证不存在,请重新登录", context);
                        startActivity(new Intent(MineAct.this, Login.class));
                        myApp.tabHostId = 0;
                        myApp.tabHost.setCurrentTab(myApp.tabHostId);
                    } else if (map.get("errorCode").equals("TOKEN_EXPIRED")) {
                        startActivity(new Intent(MineAct.this, Login.class));
                        myApp.tabHostId = 0;
                        myApp.tabHost.setCurrentTab(myApp.tabHostId);
                    } else {
                        MineShow.toastShow(Check.checkReturn(map.get("errorCode")), context);
                    }
                }
            }
            my_scroll.onRefreshComplete();
            super.handleMessage(paramMessage);
        }
    }


    /**
     * 从个人中心界面返回时调用
     */
    private void initData() {
        File file = new File(MyApplication.IMG_SAVE_PATH, MyApplication.PORTRAIT_FILE_NAME);
        if (file.exists()) {
            iv_head.setImageBitmap(BitmapFactory.decodeFile(String.valueOf(file)));
        } else {
            iv_head.setImageResource(R.drawable.person);
        }

        long time = System.currentTimeMillis();
        final Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(time);
        int apm = mCalendar.get(Calendar.AM_PM);

    }

    /**
     * 初始化头像 onRestart()的时候调用
     */
    public void init() {
        iv_head.setImageResource(R.drawable.person);

        ImageLoader.getInstance().displayImage(avayar_url, iv_head, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {

            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {
                myApp.bt_head=null;
                iv_head.setImageResource(R.drawable.person);
            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                myApp.bt_head=bitmap;
                iv_head.setImageBitmap(bitmap);
            }

            @Override
            public void onLoadingCancelled(String s, View view) {

            }
        });


    }


    /**
     * 根据一个网络连接(String)获取bitmap图像
     *
     * @param UrlPath
     * @return Bitmap
     */
    public Bitmap getInternetPicture(String UrlPath) {
        Bitmap bm = null;
        String urlpath = UrlPath;
        try {
            URL uri = new URL(urlpath);
            HttpURLConnection connection = (HttpURLConnection) uri.openConnection();
            connection.setRequestMethod("GET");
            connection.setReadTimeout(5000);
            connection.setConnectTimeout(5000);
            connection.connect();
            if (connection.getResponseCode() == 200) {
                InputStream is = connection.getInputStream();
                File file = new File(getCacheDir(), getFileName(urlpath));
                FileOutputStream fos = new FileOutputStream(file);
                int len = 0;
                byte[] b = new byte[1024];
                while ((len = is.read(b)) != -1) {
                    fos.write(b, 0, len);
                }
                fos.close();
                is.close();
                bm = BitmapFactory.decodeFile(file.getAbsolutePath());
            } else {
                bm = null;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bm;
    }

    public String getFileName(String path) {
        int index = path.lastIndexOf("/");
        return path.substring(index + 1);
    }

    /**
     * 动态注册的广播
     */
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
         if (action.equals("loginout")){ //说明用户退出登录了
                my_scroll.getRefreshableView().fullScroll(ScrollView.FOCUS_UP);//移动到顶部
                my_useMoney.setText("--");
                my_count_money.setText("--");
                my_account_balance.setText("--");
                tv_phone.setText("");
                rl_nologin.setVisibility(View.VISIBLE);
                line_top.setChildClickable(false);
                avayar_url="";
                my_scroll.setMode(DISABLED);
                iv_head.setImageResource(R.drawable.person);
            }

        }
    };

    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction("loginout");
        // 注册广播
        registerReceiver(mBroadcastReceiver, myIntentFilter);
    }


    /* 发布公告给手机客户端--针对已登录用户 */
    private void startDataRequestPublishUserNotice() {
        if (Check.hasInternet(MineAct.this)) {
            MineAct.this.initArray();
            MineAct.this.param.add(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN);
            MineAct.this.value.add(Profile.getoAutoToken());
            MineAct.this.param.add(URL_QIAN_API_APPID);
            MineAct.this.value.add(myApp.appId);
            MineAct.this.param.add(BaseParam.URL_QIAN_API_SERVICE);
            MineAct.this.value.add("publishUserNotice");
            MineAct.this.param.add(BaseParam.URL_QIAN_API_SIGNTYPE);
            MineAct.this.value.add(myApp.signType);

            String[] array = new String[]{
                    BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN + "=" + Profile.getoAutoToken(),
                    URL_QIAN_API_APPID + "=" + myApp.appId,
                    BaseParam.URL_QIAN_API_SERVICE + "=publishUserNotice",
                    BaseParam.URL_QIAN_API_SIGNTYPE + "=" + myApp.signType};

            String sign = apiModel.sortStringArray(array);
            MineAct.this.param.add(BaseParam.URL_QIAN_API_SIGN);
            MineAct.this.value.add(sign);
            new Thread(new JsonRequeatThreadPublishUserNotice(
                    MineAct.this,
                    myApp,
                    MineAct.this.myHandler,
                    MineAct.this.param,
                    MineAct.this.value)
            ).start();
        } else {
            MineShow.toastShow("请检查网络连接是否正常", context);
        }
    }

    /**
     * 用户点击改变为已读状态
     */
    private void startDataRequestUpdateUserNoticeRead() {
        if (Check.hasInternet(MineAct.this)) {
            MineAct.this.initArray();
            MineAct.this.param.add(BaseParam.QIAN_MY_NOTICEID);
            MineAct.this.value.add(noticeid);
            MineAct.this.param.add(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN);
            MineAct.this.value.add(Profile.getoAutoToken());
            MineAct.this.param.add(URL_QIAN_API_APPID);
            MineAct.this.value.add(myApp.appId);
            MineAct.this.param.add(BaseParam.URL_QIAN_API_SERVICE);
            MineAct.this.value.add("updateUserNoticeRead");
            MineAct.this.param.add(BaseParam.URL_QIAN_API_SIGNTYPE);
            MineAct.this.value.add(myApp.signType);

            String[] array = new String[]{BaseParam.QIAN_MY_NOTICEID + "=" + noticeid,
                    BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN + "=" + Profile.getoAutoToken(),
                    URL_QIAN_API_APPID + "=" + myApp.appId,
                    BaseParam.URL_QIAN_API_SERVICE + "=updateUserNoticeRead",
                    BaseParam.URL_QIAN_API_SIGNTYPE + "=" + myApp.signType};

            String sign = apiModel.sortStringArray(array);
            MineAct.this.param.add(BaseParam.URL_QIAN_API_SIGN);
            MineAct.this.value.add(sign);

            new Thread(new JsonRequeatThreadUpdateUserNoticeRead(
                    MineAct.this,
                    myApp,
                    MineAct.this.myHandler,
                    MineAct.this.param,
                    MineAct.this.value)
            ).start();
        } else {
            MineShow.toastShow("请检查网络连接是否正常", context);
        }
    }

    private void initArray() {
        this.param = null;
        this.value = null;
        this.param = new ArrayList();
        this.value = new ArrayList();
        System.gc();
    }

    private String formatTwoPercentPointDouble(String value) {
        double DingQiDaiDaoZhangDouble = 0;
        if (!TextUtils.isEmpty(value)) {
            DingQiDaiDaoZhangDouble = Double.parseDouble(value);
        }
        return df.format(DingQiDaiDaoZhangDouble);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login: //点击登录按钮
                if (MineShow.fastClick()) {
                    Intent payment = new Intent(MineAct.this, Login.class);
                    startActivity(payment);
                    MineAct.this.overridePendingTransition(R.anim.activity_open,0);
                }
                break;
            case R.id.repay_btn: //回款日历
                if (MineShow.fastClick()) {
                    Intent payment = new Intent(MineAct.this, MyPaymentListAct.class);
                    startActivity(payment);
                }
                break;


            case R.id.iv_home_eye:
                    if (eye_open){//当前是可见的,把数字变成*
                        my_useMoney.setText("****");
                        my_count_money.setText("****");
                        my_account_balance.setText("****");
                        iv_home_eye.setBackgroundResource(R.drawable.home_noeye);
                        eye_open=false;
                    }else {

                        my_useMoney.setText(all_money);
                        my_count_money.setText(count_money);
                        my_account_balance.setText(df.format(kyye));//可用余额
                        iv_home_eye.setBackgroundResource(R.drawable.home_eye);
                        eye_open=true;
                    }
                break;
            case R.id.more_help:  //常见问题
                if (MineShow.fastClick()) {
                    Intent help = new Intent(MineAct.this, WebViewAct.class);
                    help.putExtra("web_url", BaseParam.URL_QIAN + "api/common/help.html");
                    help.putExtra("title", "常见问题");
                    help.putExtra("help","help");
                    startActivity(help);
                }
                break;
            case R.id.my_management_btn: /* 个人中心 */
                if (Check.hasInternet(MineAct.this)) {
                    if (MineShow.fastClick()) {
                        Intent management = new Intent(MineAct.this, PersonManagementAct.class);
                        management.putExtra("phone", phone + "");
                        management.putExtra("score", score);
                        management.putExtra("avatarUrl",avayar_url);
                        startActivityForResult(management, MODIFY_REQUEST_CODE);
                    }
                } else {
                    MineShow.toastShow("请检查网络连接是否正常", context);
                }
                break;

            case R.id.my_redpackets_btn_war: /*我的优惠 */
                SharedPreferences sp1 = context.getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_USER, MODE_PRIVATE);
                Editor editor1 = sp1.edit();
                editor1.putString(BaseParam.QIAN_SHAREDPREFERENCES_USER_ISRED, "0");
                editor1.putString(BaseParam.QIAN_SHAREDPREFERENCES_USER_LATESTDATE, latestDate);
                editor1.commit();
                if (MineShow.fastClick()) {
                    Intent redAndAward = new Intent(MineAct.this, RedAndJiaXiH5.class);
                    startActivity(redAndAward);
                }
                break;

            case R.id.mine_customer_service:  /* 联系客服 */
                Intent intent=new Intent(MineAct.this, ServerAct.class);
                startActivity(intent);

                break;

            case R.id.mine_more:  /* 设置 */
                if (MineShow.fastClick()) {
                    startActivity(new Intent(MineAct.this, SettingAct.class));
                }
                break;
            case R.id.mine_yaoqing:  /* 邀请好友有礼界面 */
                if (MineShow.fastClick()) {

                         intent=new Intent(MineAct.this, ShareWebViewActivity.class);
                        startActivity(intent);

                }
                break;


            case R.id.tender_btn:  /* 我的投资 */
                if (MineShow.fastClick()) {
                    Intent repay = new Intent(MineAct.this, MyRecordList.class);
                    startActivity(repay);
                }
                break;


            case R.id.rl_mybalance: //可用余额
                if (MineShow.fastClick()) {
                    Intent i=new Intent(MineAct.this,MyAccountBalanceListActivity.class);
                    i.putExtra("currentMonthHasCash",currentMonthHasCash); //可提现次数
                    i.putExtra("balance", balance);  //可用余额
                    i.putExtra("disposeCash", disposeCash);  //提现中金额
                    startActivity(i);
                }
                break;


            case R.id.account_balance_recharge:   /* 充值 */
                if (MineShow.fastClick()) {
                    if (TextUtils.equals(Profile.getUserRealNameStatus(), "0")) {//未实名认证
                        NewRealAct.start(MineAct.this);
                        MineShow.toastShow("请先实名认证", MineAct.this);
                    } else {
                        if (TextUtils.equals(Profile.getUserPayPassWordStatus(), "0")) {//未设置交易密码
                            startActivity(new Intent(MineAct.this, ForgetPasswordAct.class));
                            MineShow.toastShow("请先设置交易密码", MineAct.this);
                        } else {
                            if (TextUtils.equals(Profile.getUserBankCardStatus(), "0")) {//未绑卡
                                AddYiBankAct.start(MineAct.this);
                                MineShow.toastShow("请先绑定一张银行卡", MineAct.this);
                            } else {
                                startActivityForResult(new Intent(MineAct.this, Recharge.class), RECHARGE_SUCCESS);
                            }
                        }
                    }
                }
                break;

            case R.id.account_balance_bottom_lift:        /* 提现 */
                if (MineShow.fastClick()) {
                    if (TextUtils.equals(Profile.getUserRealNameStatus(), "0")) {//未实名认证
                        NewRealAct.start(MineAct.this);
                        MineShow.toastShow("请先实名认证", MineAct.this);
                    } else {
                        if (TextUtils.equals(Profile.getUserPayPassWordStatus(), "0")) {//未设置交易密码
                            startActivity(new Intent(MineAct.this, ForgetPasswordAct.class));
                            MineShow.toastShow("请先设置交易密码", MineAct.this);
                        } else {
                            if (TextUtils.equals(Profile.getUserBankCardStatus(), "0")) {//未绑卡
                                AddYiBankAct.start(MineAct.this);
                                MineShow.toastShow("请先绑定一张银行卡", MineAct.this);
                            } else {
                                MyBalanceWithdrawCash.start(MineAct.this, currentMonthHasCash);
                            }
                        }
                    }
                }
                break;

            case R.id.total_assets:    /* 总资产 */
                if (MineShow.fastClick()) {
                    Intent assets = new Intent(MineAct.this, Asset.class);
                    assets.putExtra("investingCapital", investingCapital); //待收本金
                    assets.putExtra("disposeCash", disposeCash);  //提现中金额
                    assets.putExtra("balance", balance);  //可用余额
                    assets.putExtra("investingWaitInterest", investingWaitInterest); //待收利息

                    startActivity(assets);
                }
                break;

            case R.id.mine_income:  /* 我的收益 */
                if (MineShow.fastClick()) {
                    Intent income = new Intent(MineAct.this, Income.class);
                    income.putExtra("interestNormal", interestNormal); //已收基础利息
                    income.putExtra("interestExtra", interestExtra);  //已收额外利息
                    income.putExtra("waitInterestNormal", waitInterestNormal); //待收基础利息
                    income.putExtra("waitInterestExtra", waitInterestExtra);  //待收额外利息
                    income.putExtra("useRedpacketSum", useRedpacketSum);  //已用红包
                    // TODO: 2017/3/30 0030  还需要传入 待收加息和已收加息两个参数
                    startActivity(income);
                }
                break;
            case R.id.rl_auto_toubiao:   //自动投标
                if (MineShow.fastClick()) {
                    Intent i = new Intent(MineAct.this, AutoBuyAct.class);

                    startActivity(i);

                }
                break;
        }
    }




    private UnreadCountChangeListener mUnreadCountListener = new UnreadCountChangeListener() {
        @Override
        public void onUnreadCountChange(int count) {
            updateUnreadCount(count);
        }
    };

    private void updateUnreadCount(int count) {
        tvUnread.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
        if (count > 99) {
            tvUnread.setText("(99+)");
        } else if (count > 0) {
            tvUnread.setText("(" + String.valueOf(count) + ")");
        }
    }

    @Override
    public void onDestroy() {
        Unicorn.addUnreadCountChangeListener(mUnreadCountListener, false);
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        parseIntent();
    }

    private void parseIntent() {
        Intent intent = getIntent();
        if (intent.hasExtra(NimIntent.EXTRA_NOTIFY_CONTENT)) {
            NewStartAct.consultService(this, null, null, null);
            // 最好将intent清掉，以免从堆栈恢复时又打开客服窗口
            setIntent(new Intent());
        }
    }

    private static final int MODIFY_REQUEST_CODE = 0x15;
    private static final int RECHARGE_SUCCESS = 0x22;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case MODIFY_REQUEST_CODE:
                    initData();
                    break;
                case RECHARGE_SUCCESS:
                    startDataRequest();
                    break;
            }
        }
    }


}
