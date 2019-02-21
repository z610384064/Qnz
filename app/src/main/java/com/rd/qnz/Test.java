package com.rd.qnz;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rd.qnz.bean.ProductContentBean;
import com.rd.qnz.custom.MineShow;
import com.rd.qnz.custom.Profile;
import com.rd.qnz.homepage.SharePopupWindow;
import com.rd.qnz.login.Login;
import com.rd.qnz.mine.ForgetPasswordAct;
import com.rd.qnz.mine.NewRealAct;
import com.rd.qnz.product.ProductContentPurchaseAct;
import com.rd.qnz.product.fragment.VerticalFragment1;
import com.rd.qnz.product.fragment.VerticalFragment2;
import com.rd.qnz.tools.BaseParam;
import com.rd.qnz.util.DragLayout;

import static com.rd.qnz.qiyu.DemoCache.context;

public class Test extends AppCompatActivity implements View.OnClickListener{
    private DragLayout draglayout;
    private String borrowId;
    private ImageView mBackImage;
    private TextView mTitle;
    private SharedPreferences preferences;
    private String  mAuthoToken,mRealStatus;
    private String realStatus;
    private LinearLayout countDown; //预售布局
    private VerticalFragment1 fragment1;
    private VerticalFragment2 fragment2;
    // 倒计时
    private TextView daysTv, hoursTv, minutesTv, secondsTv;

    private long mDay;
    private long mHour;
    private long mMin;
    private long mSecond;// 天 ,小时,分钟,秒
    private boolean isRun = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test3);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //设置顶部状态栏颜色

            window.setStatusBarColor(getResources().getColor(R.color.productcontent_title_background));

        }
        borrowId=getIntent().getStringExtra("borrowId");
        preferences = getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_USER, Context.MODE_PRIVATE); //sp_user
        mAuthoToken = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN, "");
        mRealStatus = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_REALSTATUS, ""); //是否实名认证
        realStatus = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_REALSTATUS, "");
        initBar();
        initView();
    }
    private void initView() {
        countDown = (LinearLayout) findViewById(R.id.countdown_layout);// 预售
        countDown.setOnClickListener(this);
        daysTv = (TextView) findViewById(R.id.days_tv);
        hoursTv = (TextView) findViewById(R.id.hours_tv);
        minutesTv = (TextView) findViewById(R.id.minutes_tv);
        secondsTv = (TextView) findViewById(R.id.seconds_tv);


          product_tender_btn= (Button) findViewById(R.id.product_tender_btn);
          product_tender_btn.setOnClickListener(this);
          fragment1 = new VerticalFragment1(Test.this,borrowId);
          fragment2 = new VerticalFragment2(Test.this);

        DragLayout.ShowNextPageNotifier nextIntf = new DragLayout.ShowNextPageNotifier() {
            @Override
            public void onDragNext() { //在这里面初始化fragment2
                Toast.makeText(Test.this, "滑到下一个界面", Toast.LENGTH_SHORT).show();
                fragment2 = new VerticalFragment2(Test.this);
            }
        };

        getSupportFragmentManager().beginTransaction()
                .add(R.id.first, fragment1)
                .add(R.id.second, fragment2)
                .commit();


        draglayout = (DragLayout) findViewById(R.id.draglayout);
        draglayout.setNextPageListener(nextIntf);




    }

    private void initBar() {
        mBackImage = (ImageView) findViewById(R.id.actionbar_side_left_iconfont);
        mBackImage.setVisibility(View.VISIBLE);
        mBackImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                finish();
            }
        });


        mTitle = (TextView) findViewById(R.id.actionbar_side_name);
        mTitle.setText("项目详情"); //标题先使用产品详情,一旦从服务器获取到数据再去修改

        ImageView actionbar_side_share = (ImageView) findViewById(R.id.actionbar_side_share);
        actionbar_side_share.setVisibility(View.VISIBLE);
        actionbar_side_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                SharePopupWindow.start(Test.this, true, "2", "","钱内助");
            }
        });

    }
    private int accountYes;
    private int account;
    private String productStatus = "";
    private boolean mProductIsNewHand = false;//产品是否是新手产品
    private String title;
    private String type;
    private double balance = 0;// 余额
    private String isday;
    private int timeLimitDay;
    private int isNewHand;
    private int timeLimit;
    private String lastRepayTime = "";
    private String flowCount, flowMoney;
    private String lowestAccount;
    private String style;
    private String apr,rateapr,interestDay,extraAwardApr;
    private String mBrType;
    private Button product_tender_btn;
    private ProductContentBean productContentBean;
    private ProductContentBean.ProductBean productBean;
    public void setData2(ProductContentBean productContentBean,double balance ){
            this.productContentBean=productContentBean;
            this.balance=balance;
             productBean=productContentBean.getProduct();
    }
    public void setData(int accountYes,int account,String productStatus,boolean mProductIsNewHand, String title,String type,double balance,String isday
                       , int timeLimitDay,int isNewHand,int timeLimit,String lastRepayTime ,String flowCount,String flowMoney,
                        String lowestAccount,String style,String apr,String rateapr,String interestDay,String extraAwardApr,String mBrType){
        this.accountYes=accountYes;
        this.account=account;
        this.productStatus=productStatus;
        this.mProductIsNewHand=mProductIsNewHand;
        this.title=title;
        this.type=type;
        this.balance=balance;
        this.isday=isday;
        this.isNewHand=isNewHand;
        this.timeLimitDay=timeLimitDay;
        this.timeLimit=timeLimit;
        this.lastRepayTime=lastRepayTime;
        this.flowCount=flowCount;
        this.flowMoney=flowMoney;
        this.lowestAccount=lowestAccount;
        this.style=style;
        this.apr=apr;
        this.rateapr=rateapr;
        this.interestDay=interestDay;
        this.extraAwardApr=extraAwardApr;
        this.mBrType=mBrType;

    }
    public void setCoudownButtomVisible(int  context){
        countDown.setVisibility(context);
    }
    public void setButtomText(String context){
        product_tender_btn.setText(context);
    }
    public void setButtomVisible(int  context){
        product_tender_btn.setVisibility(context);
    }
    public void setButtomBackground(int  id){
        product_tender_btn.setBackgroundResource(id);
    }
    public void setButtomClickble(boolean b){
        product_tender_btn.setClickable(b);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.product_tender_btn:  //点击了立即投资
                if (MineShow.fastClick()) {
                    if (mAuthoToken.equals("") || mAuthoToken == null) {//未登录
                        startActivity(new Intent(context, Login.class));
                        return;
                    } else {
                        if (realStatus.equals("0") || realStatus == null) {//未实名认证
                            startActivity(new Intent(context, NewRealAct.class));
                            return;
                        }
                    }
                    if (account == accountYes) {//余额为0（标总额=已投金额）
                        return;
                    }
                    if (TextUtils.equals(Profile.getUserPayPassWordStatus(), "0")) {//未设置交易密码
                        Intent intent = new Intent(Test.this, ForgetPasswordAct.class);
                        startActivity(intent);
                        return;
                    }

                    if (mProductIsNewHand && TextUtils.equals(Profile.getUserIsNewHandStatus(), "0")) {//新手产品
                        showToast("该产品为新手标您无法投资");
                    } else {
                        ProductContentPurchaseAct.start(
                                Test.this,
                                borrowId,//产品ID（标id）
                                productContentBean.getProduct().getProductStatus(), //产品状态：0：预售 ，1：申购，2:已售完，3:还款中,4:已完结
                                productBean.getName(), //标名
                                productBean.getType() , //类型（102,103，110,112）其中（102,103借款类，110，112债券类，其中110是按照份数）
                                productBean.getFlowCount() , //份数
                                productBean.getFlowMoney(),//份金额
                                balance, //余额（标总额-已投金额）
                                productBean.getIsday(),//是否是天标(1天标 0月标)
                                productBean.getTimeLimitDay() + "", //借款期限(天标)
                                productBean.getTimeLimit() + "",//借款期限(月)
                                productBean.getLowestAccount(),//最低投资金额
                                productBean.getLastRepayTime(), //最后待收时间
                                productBean.getStyle(),//还款方式（1;等额本息，2;一次性到期还款，3;每月还息到期还本）
                                productBean.getNormalApr(), //基本几率
                                productBean.getApr(), //总利率
                                productBean.getExtraAwardApr(), //额外加息
                                productContentBean.getInterestDay(), //天数
                                productBean.getBrType()//品具体类型 1新手标 2 普通标 3 钱贝勒
                        );
                    }
                }
                break;
        }
    }


    public Handler gitHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 10) {
                fragment1.startDataRequest();
            }
        }
    };


    /**
     * 开启倒计时
     */
    public void startRun() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                while (isRun) {
                    try {
                        Thread.sleep(1000);
                        Message message = Message.obtain();
                        message.what = 1;
                        timeHandler.sendMessage(message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private Handler timeHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                computeTime();
                if (mDay < 10) {
                    daysTv.setText("0" + mDay + "天");
                } else {
                    daysTv.setText(mDay + "天");
                }
                if (mHour < 10) {
                    hoursTv.setText("0" + mHour + "小时");
                } else {
                    hoursTv.setText(mHour + "小时");
                }
                if (mMin < 10) {
                    minutesTv.setText("0" + mMin + "分");
                } else {
                    minutesTv.setText(mMin + "分");
                }
                if (mSecond < 10) {
                    secondsTv.setText("0" + mSecond + "秒");
                } else {
                    secondsTv.setText(mSecond + "秒");
                }

                if (mDay <= 0) {
                    daysTv.setVisibility(View.GONE);
                }

                if (mDay == 0 && mHour == 0 && mMin == 0 && mSecond == 0) {
                    countDown.setVisibility(View.GONE);
                    product_tender_btn.setVisibility(View.VISIBLE);
                     fragment1.startDataRequest();
                    if (mAuthoToken.equals("") || mAuthoToken == null) {
                        product_tender_btn.setText("立即投资");
                    } else {
                        if (mRealStatus.equals("0") || mRealStatus == null) {
                            product_tender_btn.setText("立即投资");
                        } else {
                            if (isNewHand == 1) {
                                product_tender_btn.setText("仅限新手投资");
                                mProductIsNewHand = true;
                            } else {
                                product_tender_btn.setText("立即投资");
                            }
                        }
                    }
                }
            }
        }
    };

    /**
     * 倒计时计算
     */
    private void computeTime() {
        mSecond--;
        if (mSecond < 0) {
            mMin--;
            mSecond = 59;
            if (mMin < 0) {
                mMin = 59;
                mHour--;
                if (mHour < 0) {
                    // 倒计时结束
                    mHour = 23;
                    mDay--;
                }
            }
        }
    }


    Toast mToast;
    TextView toast_tv;

    public void showToast(final String text) {
        if (!TextUtils.isEmpty(text)) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (mToast == null) {
//						mToast = Toast.makeText(BaseActivity.this, text, Toast.LENGTH_SHORT);
                        mToast = new Toast(context);
                        View tview = LayoutInflater.from(context).inflate(R.layout.layout_toast, null);
                        mToast.setView(tview);
                        toast_tv = (TextView) tview.findViewById(R.id.toast_text);
                    } else {
//						mToast.setText(text);
                    }
                    toast_tv.setText(text);
                    mToast.show();
                }
            });
        }
    }

}
