package com.rd.qnz.product;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.rd.qnz.R;
import com.rd.qnz.WebViewAct;
import com.rd.qnz.custom.APIModel;
import com.rd.qnz.custom.MineShow;
import com.rd.qnz.custom.MyApplication;
import com.rd.qnz.custom.Profile;
import com.rd.qnz.dialog.CustomProgressDialog;
import com.rd.qnz.dialog.GetDialog;
import com.rd.qnz.homepage.SharePopupWindow;
import com.rd.qnz.login.Login;
import com.rd.qnz.mine.ForgetPasswordAct;
import com.rd.qnz.mine.NewRealAct;
import com.rd.qnz.product.fragment.KnowProjectFragment;
import com.rd.qnz.product.fragment.SaveFragmentForProduct;
import com.rd.qnz.product.fragment.TouziHistoryFragment;
import com.rd.qnz.tools.AppTool;
import com.rd.qnz.tools.BaseParam;
import com.rd.qnz.tools.Check;
import com.rd.qnz.tools.CustomViewpager;
import com.rd.qnz.tools.webservice.JsonRequeatThreadProductContent;
import com.rd.qnz.view.RedCursorView2;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * 产品详情页(已适配)
 *
 * @author Evonne
 */
public class ProductContentAct3 extends AppCompatActivity implements OnClickListener {
    private static final String TAG = "产品详情页";
    private ArrayList<String> param = null;
    private ArrayList<String> value = null;
    private GetDialog dia;
    private MyHandler myHandler;
    private CustomProgressDialog progressDialog = null;
    private MyApplication myApp;
    private String oauthToken;
    private String realStatus;
    private boolean mProductIsNewHand = false;//产品是否是新手产品
    APIModel apiModel = new APIModel();

    private String borrowId;// 产品Id
    private String isAdvanceRepay;//是否支持提前还款  1支持 0 不
    private String productStatus = "";
    private SharedPreferences preferences;
    private String mAuthoToken;
    private String mRealStatus;
    private String countDownTime;

    private TextView product_total_money;// 剩余可投
    private TextView my_day_money;//起投金额
    private RelativeLayout  normal_standard;



    private RelativeLayout repayment;
    private TextView repayment_text,out_time;
    private ImageView repayment_image;


    private PullToRefreshScrollView mScrollView;





    private TextView product_apr, qian_rate, bank_rate,
            product_extraApr, product_limit_time;
    private EditText qian_edit;
    private String mBrType;
    private RelativeLayout lin_product_extraApr;






    private Button product_tender_btn;
    private LinearLayout countDown;
    private String title;
    private String type;
    private String style;
    private String borrowType;
    private double balance = 0;// 余额
    private String isday;
    private int timeLimitDay;
    private int isNewHand;
    private int timeLimit;
    private String apr,rateapr,interestDay,extraAwardApr;
    private int account;
    private int duan;// 分成一百份 一份duan
    private int accountYes;
    private String lastRepayTime = "";
    private String flowCount, flowMoney;
    private String lowestAccount;

    private String franchiseeId;
    private TextView mTitle;
    private ImageView mBackImage;

    private int percent;
    private int j = 0;

    private long mDay;
    private long mHour;
    private long mMin;
    private long mSecond;// 天 ,小时,分钟,秒

    // 倒计时
    private TextView daysTv, hoursTv, minutesTv, secondsTv;
    private boolean isRun = true;


    private String act_title = "";
    private String act_url = "";
    private int friststart = 1;

    private static final int SUCCESS = 1;
    private static final int FALL = 2;

    private SharedPreferences sp;

    /**
     *  1.6.0版本新增viewpager
     * @param savedInstanceState
     */

    List<Fragment> fragments = new ArrayList<>();
    private LinearLayout linear_group_buttom;
    private RadioGroup product_radiogroup_buttom;
    private RadioButton product_radiobutton1_top, product_radiobutton2_top,product_radiobutton3_top;


    //顶部三个导航栏,一开始隐藏着
    private LinearLayout linear_group_top;
    private RadioGroup product_radiogroup_top;

    private RadioButton product_radiobutton1_buttom, product_radiobutton2_buttom,product_radiobutton3_buttom;

    private CustomViewpager viewpager;

    private KnowProjectFragment knowProjectFragment;
    private SaveFragmentForProduct saveFragmentForProduct;
    private TouziHistoryFragment touziHistoryFragment;
    private int currentItem=0;

    private int top_height;
    private LinearLayout linearlayout_top;
    private RelativeLayout main_top_relativeLayout;
    private   ScrollView s; //下拉刷新控件对应的scrollView
    private boolean isbuttom=false; //是否位于底部
    private int scroll_y;
    //顶部事先隐藏的布局

    /**
     *  还款日
     * @param savedInstanceState
     */
    private TextView product_apr1;  //总利率
    private TextView product_apr_baifen1;//百分号
    private double allApr; //总利息
    private LinearLayout rl_apr1;
    private RelativeLayout rl_apr2;
    private TextView tv_interestStartTime,tv_interestEndTime,tv_validTime;//计息时间,回款日,募集时间
    private TextView tv_name; //项目名称
    private TextView tv_allmoney; //项目总额
    private TextView product_content_mProgressBar; //进度条的值
    private ImageView iv_progress; //进度条对应的图片
    private int iv_progress_width;
    private RedCursorView2 cursor_view_buttom,cursor_view_top;
    private int progress_width=0; //进度条宽度
    private int text_value;
    private ImageView progress_background;
    private boolean isRestart=false; //项目调用了onRestart方法


    Toast mToast;
    TextView toast_tv;

    public void showToast(final String text) {
        if (!TextUtils.isEmpty(text)) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (mToast == null) {
//						mToast = Toast.makeText(BaseActivity.this, text, Toast.LENGTH_SHORT);
                        mToast = new Toast(com.rd.qnz.product.ProductContentAct3.this);
                        View tview = LayoutInflater.from(ProductContentAct3.this).inflate(R.layout.layout_toast, null);
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

    public void showToast(final int resId) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (mToast == null) {
//					mToast = Toast.makeText(BaseActivity.this, resId, Toast.LENGTH_SHORT);
                    mToast = new Toast(ProductContentAct3.this);
                    View tview = LayoutInflater.from(ProductContentAct3.this).inflate(R.layout.layout_toast, null);
                    mToast.setView(tview);
                    toast_tv = (TextView) tview.findViewById(R.id.toast_text);
                } else {
//					mToast.setText(resId);
                }
                toast_tv.setText(resId);
                mToast.show();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage_product_list_content);
        Log.d(TAG,"ProductContentAct3 onCreate()被调用");
        //安卓版本大于5.0
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

        IntentFilter intentFilter=new IntentFilter("NewRealAct");
        intentFilter.addAction("web");
        registerReceiver(myReceiver,intentFilter);




        sp=getSharedPreferences("start",MODE_PRIVATE);

        this.dia = new GetDialog();
        myApp = (MyApplication) getApplication();
        this.myHandler = new MyHandler();
        Intent intent = getIntent();
        borrowId = intent.getStringExtra(BaseParam.QIAN_PRODUCT_BORROWID);  //产品id
        preferences = getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_USER, Context.MODE_PRIVATE); //sp_user
        mAuthoToken = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN, "");
        mRealStatus = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_REALSTATUS, ""); //是否实名认证
        initBar();
        initView();
        startDataRequest();
        linearlayout_top.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            /**
             * 当ll_safe_container在父View中执行完layout(布局)之后才调用该方法；
             * 所以在该方法中肯定可以获取到宽高的值
             */
            @Override
            public void onGlobalLayout() {
                //一般用完立即移除，因为只要该view的宽高改变都会再引起回调该方法
                linearlayout_top.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                //获取到高度
                top_height =linearlayout_top.getHeight(); //1732
                Log.d(TAG,"top_height:"+top_height);
            }
        });


    }
    /**
     * 做一些头部界面的修改
     */
    private void initBar() {
        mBackImage = (ImageView) findViewById(R.id.actionbar_side_left_iconfont);
        mBackImage.setVisibility(View.VISIBLE);
        mBackImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                finish();
            }
        });


        mTitle = (TextView) findViewById(R.id.actionbar_side_name);
        mTitle.setText("项目详情"); //标题先使用产品详情,一旦从服务器获取到数据再去修改

        ImageView actionbar_side_share = (ImageView) findViewById(R.id.actionbar_side_share);
        actionbar_side_share.setVisibility(View.VISIBLE);
        actionbar_side_share.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                SharePopupWindow.start(ProductContentAct3.this, true, "2", "","钱内助");
            }
        });

    }
    /**
     * 做控件的声明
     */
    private void initView() {
        progress_background= (ImageView) findViewById(R.id.progress_background);
        cursor_view_top= (RedCursorView2) findViewById(R.id.cursor_view_top);
//        cursor_view_buttom= (RedCursorView2) findViewById(R.id.cursor_view_buttom);
//        cursor_view_buttom.setVisibility(View.GONE);
        tv_name= (TextView) findViewById(R.id.tv_name);
        tv_interestStartTime= (TextView) findViewById(R.id.tv_interestStartTime);
        tv_interestEndTime= (TextView) findViewById(R.id.tv_interestEndTime);
        tv_validTime= (TextView) findViewById(R.id.tv_validTime);

        rl_apr1= (LinearLayout) findViewById(R.id.rl_apr1);
        rl_apr2= (RelativeLayout) findViewById(R.id.rl_apr2);
        rl_apr1.setOnClickListener(this);
        rl_apr2.setOnClickListener(this);
        tv_allmoney= (TextView) findViewById(R.id.tv_allmoney);
        product_apr = (TextView) findViewById(R.id.product_apr);// 总利率
        lin_product_extraApr = (RelativeLayout) findViewById(R.id.lin_product_extraApr);
        product_apr1= (TextView) findViewById(R.id.product_apr1);
        product_apr_baifen1= (TextView) findViewById(R.id.product_apr_baifen1);
        product_extraApr = (TextView) findViewById(R.id.product_extraApr);
        product_limit_time = (TextView) findViewById(R.id.product_limit_time);

        product_content_mProgressBar = (TextView) findViewById(R.id.product_content_mProgressBar);
        product_tender_btn = (Button) findViewById(R.id.product_tender_btn);//立即投资
        product_tender_btn.setOnClickListener(this);
        countDown = (LinearLayout) findViewById(R.id.countdown_layout);// 预售
        countDown.setOnClickListener(this);
        daysTv = (TextView) findViewById(R.id.days_tv);
        hoursTv = (TextView) findViewById(R.id.hours_tv);
        minutesTv = (TextView) findViewById(R.id.minutes_tv);
        secondsTv = (TextView) findViewById(R.id.seconds_tv);


        product_total_money = (TextView) findViewById(R.id.product_total_money);
        my_day_money = (TextView) findViewById(R.id.my_day_money);

        repayment = (RelativeLayout) findViewById(R.id.repayment);
        repayment.setOnClickListener(this);

        out_time = (TextView) findViewById(R.id.out_time);
        repayment_text = (TextView) findViewById(R.id.repayment_text);
        repayment_image = (ImageView) findViewById(R.id.repayment_image);
        iv_progress= (ImageView) findViewById(R.id.iv_progress);



        // 投资有奖

        //了解项目

        //安全保障
        mScrollView = (PullToRefreshScrollView) findViewById(R.id.scrollVeiw);
        mScrollView.setMode(PullToRefreshBase.Mode.BOTH);
        s=mScrollView.getRefreshableView();
            //下拉加载
        mScrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ScrollView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {

                // TODO Auto-generated method stub
                refreshView.getLoadingLayoutProxy().setLoadingDrawable(getResources().getDrawable(R.drawable.default_ptr_rotate));
                refreshView.getLoadingLayoutProxy().setPullLabel("下拉加载更多");
                refreshView.getLoadingLayoutProxy().setRefreshingLabel("加载中...");
                refreshView.getLoadingLayoutProxy().setReleaseLabel("下拉加载更多");
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel("  ");
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel("");
                if (isbuttom){  //把上面显示出来
                    mScrollView.onRefreshComplete();
                    linear_group_buttom.setVisibility(View.VISIBLE);    //底部三个导航栏显示出来
                    linear_group_top.setVisibility(View.GONE);//顶部导航栏隐藏
                    linearlayout_top.setVisibility(View.VISIBLE); //顶部布局显示出来

                    showTop();
                }else {
                    startDataRequest();
                }

            }

            @Override   //上拉加载
            public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {  //上拉加载更多

                if(isbuttom){ //当前界面位于底部
                    if (currentItem==2){  //处于投资记录界面

                        refreshView.getLoadingLayoutProxy().setLoadingDrawable(getResources().getDrawable(R.drawable.default_ptr_rotate));
                        refreshView.getLoadingLayoutProxy().setPullLabel("上拉加载更多");
                        refreshView.getLoadingLayoutProxy().setRefreshingLabel("加载中...");
                        refreshView.getLoadingLayoutProxy().setReleaseLabel("上拉加载更多");
                        mScrollView.onRefreshComplete();
                        sendBroadcast(new Intent("productcontent"));
                    }else {
                        mScrollView.onRefreshComplete();
                    }
                }else { //一开始界面位于顶部,现在位于底部
                    linear_group_buttom.setVisibility(View.VISIBLE);  //底部的显示出来
                    viewpager.setVisibility(View.VISIBLE);  //底部viewpager显示出来

                    refreshView.getLoadingLayoutProxy().setLoadingDrawable(getResources().getDrawable(R.drawable.default_ptr_rotate));
                    refreshView.getLoadingLayoutProxy().setPullLabel("上拉加载更多");
                    refreshView.getLoadingLayoutProxy().setRefreshingLabel("加载中...");
                    refreshView.getLoadingLayoutProxy().setReleaseLabel("上拉加载更多");
                    mScrollView.onRefreshComplete();
                    scroll_y=s.getScrollY();
                    showButtom();
                }

            }
        });

        /**
         * 1.6.0版本
         */

        linear_group_top= (LinearLayout) findViewById(R.id.linear_group_top);  // 顶部三个导航栏
        product_radiogroup_top = (RadioGroup) findViewById(R.id.product_radiogroup_top);
        product_radiobutton1_top= (RadioButton) findViewById(R.id.product_radiobutton1_top);
        product_radiobutton2_top= (RadioButton) findViewById(R.id.product_radiobutton2_top);
        product_radiobutton3_top= (RadioButton)findViewById(R.id.product_radiobutton3_top);


        main_top_relativeLayout= (RelativeLayout) findViewById(R.id.main_top_relativeLayout); //标题布局

        linearlayout_top= (LinearLayout) findViewById(R.id.linearlayout_top); //上半部布局


        linear_group_buttom= (LinearLayout) findViewById(R.id.linear_group_buttom);  //底部三个导航栏
        product_radiogroup_buttom = (RadioGroup) findViewById(R.id.product_radiogroup_buttom);
        product_radiobutton1_buttom= (RadioButton) findViewById(R.id.product_radiobutton1_buttom);
        product_radiobutton2_buttom= (RadioButton) findViewById(R.id.product_radiobutton2_buttom);
        product_radiobutton3_buttom= (RadioButton)findViewById(R.id.product_radiobutton3_buttom);

        viewpager= (CustomViewpager) findViewById(R.id.viewpager);
//        viewpager= (ViewPager) findViewById(R.id.viewpager);
//        viewpager viewpager= (WrapContentHeightViewPager) findViewById(R.id.viewpager);
        linear_group_top.setVisibility(View.GONE);  //顶部三个标题栏隐藏
        linear_group_buttom.setVisibility(View.GONE);
        viewpager.setVisibility(View.GONE);

    }
    private void showTop() {

        ValueAnimator animator = ValueAnimator.ofInt(top_height-scroll_y,0);  //设置一个从100到1000的值回调
        //监听动画执行的进度
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                //监听过程中这个方法会不断被调用,里面的值animatedValue 就是100-1000之间的值,代表了动画执行的进度
                int animatedValue = (Integer) animator.getAnimatedValue();
                linearlayout_top.setPadding(0,-animatedValue,0,0);
            }
        });
        animator.setDuration(300);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                linear_group_buttom.setVisibility(View.GONE);  //底部三个导航栏
                viewpager.setVisibility(View.GONE);
                isbuttom=false;
                mScrollView.setMode(PullToRefreshBase.Mode.BOTH); //两个都能拉
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();
    }


    private void showButtom() {
        ValueAnimator animator = ValueAnimator.ofInt(0,top_height-scroll_y);  //设置一个从100到1000的值回调
        //监听动画执行的进度
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                //监听过程中这个方法会不断被调用,里面的值animatedValue 就是100-1000之间的值,代表了动画执行的进度
                int animatedValue = (Integer) animator.getAnimatedValue();
                linearlayout_top.setPadding(0,-animatedValue,0,0); //顶部下拉
                s.smoothScrollTo(0,0);
            }
        });
        animator.setDuration(300);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                linearlayout_top.setVisibility(View.GONE);  //整个顶部布局隐藏
                linear_group_top.setVisibility(View.VISIBLE); //顶部三个导航栏显示出来
                linear_group_buttom.setVisibility(View.GONE);  //底部三个导航栏隐藏
                s.smoothScrollTo(0,0);
                isbuttom=true;
                if (currentItem!=2){ //如果是位于项目介绍或者安全保障
                    mScrollView.setMode(PullToRefreshBase.Mode.PULL_FROM_START); //只支持下拉
                }else {
                    mScrollView.setMode(PullToRefreshBase.Mode.BOTH); //两个都能拉
                }

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();


    }
//
//    class MyFragmentPagerAdapter extends FragmentPagerAdapter {
//        private List<Fragment> fragments;
//        public FragmentManager fm;
//
//        public MyFragmentPagerAdapter(FragmentManager fm) {
//            super(fm);
//        }
//
//        public MyFragmentPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
//            super(fm);
//            this.fm = fm;
//            this.fragments = fragments;
//        }
//
//        @Override
//        public Fragment getItem(int arg0) {
//            if (arg0 >= fragments.size()) {
//                return null;
//            }
//            return fragments.get(arg0);
//        }
//
//
//
//        @Override
//        public int getItemPosition(Object object) {
//            if (fragments.contains(object)) {
//                return POSITION_UNCHANGED;
//            }
//            return POSITION_NONE;
//        }
//        @Override
//        public void destroyItem(ViewGroup container, int position, Object object) {
//            Fragment fragment = fragments.get(position);
//            fm.beginTransaction().hide(fragment).commitAllowingStateLoss();
//        }
//
//        @Override
//        public int getCount() {
//            return fragments.size();
//        }
//
//        @Override
//        public Fragment instantiateItem(ViewGroup container, int position) {
//            Fragment fragment = (Fragment) super.instantiateItem(container,
//                    position);
//            fm.beginTransaction().show(fragment).commitAllowingStateLoss();
//            return fragment;
//        }
//
//    }

    class MyFragmentPagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> fragments;

        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public MyFragmentPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int arg0) {
            if (arg0 >= fragments.size()) {
                return null;
            }
            return fragments.get(arg0);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public int getItemPosition(Object object) {
            if (fragments.contains(object)) {
                return POSITION_UNCHANGED;
            }
            return POSITION_NONE;
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            super.destroyItem(container, position, object);

        }
    }



    public static void start(Context context, String borrowId) {
        if (TextUtils.isEmpty(borrowId)) {
            return;
        }
        Intent i = new Intent(context, ProductContentAct3.class);
        i.putExtra(BaseParam.QIAN_PRODUCT_BORROWID, borrowId);
        context.startActivity(i);
    }
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


    private class MyHandler extends Handler {

        private DecimalFormat df;

        @SuppressLint("NewApi")
        public void handleMessage(Message paramMessage) {


            Bundle localBundle1 = paramMessage.getData();
            ArrayList<Parcelable> product_R = localBundle1.getParcelableArrayList(BaseParam.QIAN_REQUEAT_PRODUCT_CONTENT);
            if (null != progressDialog && progressDialog.isShowing()) {// 隐藏加载框
                ProductContentAct3.this.progressDialog.dismiss();
            }
            if (null != product_R) {
                Map<String, String> map = (Map<String, String>) product_R.get(0);
                String resultCode = map.get("resultCode");
                if (resultCode.equals("1")) {
                    String phoneList = map.get("phoneList");
                    if (phoneList != null) {

                        String[] phone = phoneList.substring(1, phoneList.length() - 1).split(",");
                        String prize = "";
                        for (int i = 0; i < phone.length; i++) {
                            if (phone[i].length() == 13) {
                                prize = prize + phone[i].substring(8, 12) + " ";
                            } else {
                                prize = prize + "";
                            }
                        }
                    } else {
                    }
                    act_title = map.get(BaseParam.QIAN_PRODUCT_ACT_TITLE);
                    act_url = map.get(BaseParam.QIAN_PRODUCT_ACT_URL);
                    franchiseeId = map.get(BaseParam.QIAN_PRODUCT_FRANCHISEEID);
                    title = map.get(BaseParam.QIAN_PRODUCT_NAME);

                    tv_name.setText(title);
                    flowCount = map.get(BaseParam.QIAN_PRODUCT_FLOWCOUNT);// 还可以申购的份数
                    flowMoney = map.get(BaseParam.QIAN_PRODUCT_FLOWMONEY);// 每份申购的金额



                    type = map.get(BaseParam.QIAN_PRODUCT_TYPE);
                    isday = map.get(BaseParam.QIAN_PRODUCT_ISDAY);
                    timeLimitDay = Integer.parseInt(map.get(BaseParam.QIAN_PRODUCT_TIMELIMITDAY));
                    timeLimit = Integer.parseInt(map.get(BaseParam.QIAN_PRODUCT_TIMELIMIT));
                    apr = map.get(BaseParam.QIAN_PRODUCT_APR);//本质上是nomal apir
                    rateapr = map.get("apr"); //11.5
                    extraAwardApr = map.get("extraAwardApr");//1.5
                    interestDay = map.get("interestDay");  //计算利息的天数
                    account = Integer.parseInt(map.get(BaseParam.QIAN_PRODUCT_ACCOUNT));
                    tv_allmoney.setText(new DecimalFormat("#,###").format(account)+"元");
                    double accountYes1 = Double.parseDouble(map.get(BaseParam.QIAN_PRODUCT_ACCOUNTYES));
                    accountYes = (int) accountYes1;
                    duan = (int) (accountYes / 100);

                    percent = (int) (accountYes1 * 100 / account); //已经购买的百分比

                    if (progress_width==0){  //进度条的宽度还不确定,所以需要去算一次
                        progress_background.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                            @Override
                            public void onGlobalLayout() {
                                //一般用完立即移除，因为只要该view的宽高改变都会再引起回调该方法
                                progress_background.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                                //获取到高度
                                progress_width = progress_background.getWidth();

                                text_value=percent*progress_width/100;


                                    ValueAnimator animator= ValueAnimator.ofInt(0,text_value);
                                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                        @Override
                                        public void onAnimationUpdate(ValueAnimator animation) {
                                            int value= (int) animation.getAnimatedValue();
                                            product_content_mProgressBar.setTranslationX(value);

                                        }
                                    });

                                    animator.setDuration(500);
                                    animator.start();


                                ValueAnimator animator2= ValueAnimator.ofInt(0,percent);
                                animator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                    @Override
                                    public void onAnimationUpdate(ValueAnimator animation) {
                                        int value= (int) animation.getAnimatedValue();
                                        iv_progress_width=progress_width*value/100;
                                        ViewGroup.LayoutParams para = iv_progress.getLayoutParams();
                                        para.width = iv_progress_width;
                                        iv_progress.setLayoutParams(para); //设置一下图片的宽
                                    }
                                });
                                animator2.setDuration(500);
                                animator2.start();


                            }
                        });
                    }




                    lastRepayTime = map.get(BaseParam.QIAN_PRODUCT_LASTREPAYTIME); //还款日期
                    if (!TextUtils.isEmpty(map.get("interestStartTime"))){
                        tv_interestStartTime.setText(map.get("interestStartTime"));
                    }
                    if (!TextUtils.isEmpty(map.get("interestEndTime"))){
                        tv_interestEndTime.setText(map.get("interestEndTime"));
                    }
                    if (!TextUtils.isEmpty(map.get("validTime"))){
                        tv_validTime.setText(map.get("validTime")+"天");
                    }
                    balance = account - accountYes1;
                    df = new DecimalFormat("0.00");
                    allApr=Double.parseDouble(map.get(BaseParam.QIAN_PRODUCT_APR))+Double.parseDouble(map.get(BaseParam.QIAN_PRODUCT_EXTRAAWARDAPR));
                    product_apr1.setText(df.format(allApr));
                    product_apr.setText(df.format(Double.parseDouble(map.get(BaseParam.QIAN_PRODUCT_APR))));
                    mBrType = map.get(BaseParam.QIAN_PRODUCT_BR_TYPE);
                    lowestAccount = map.get(BaseParam.QIAN_PRODUCT_LOWESTACCOUNT);
                    isAdvanceRepay = map.get(BaseParam.QIAN_PRODUCT_IS_ADVANCEREPAY);

                    //支持提前还款
                    if (isAdvanceRepay.equals("1")) {
                        repayment_text.setText("到期一次性还本付息，支持提现还款");
                        repayment_image.setVisibility(View.VISIBLE);
                        repayment.setClickable(true);
                    } else {
                        repayment_text.setText("到期一次性还本付息");
                        repayment_image.setVisibility(View.GONE);
                        repayment.setClickable(false);
                    }
                    out_time.setText(AppTool.getMsgTwoDateDistance1(map.get("secVerifyTime"))); //发布时间
                    /**
                     *  三个红包分别被领取的时间
                     */


                    if (!"0".equals(map.get(BaseParam.QIAN_PRODUCT_EXTRAAWARDAPR))) { //有额外加息
                        lin_product_extraApr.setVisibility(View.VISIBLE);
                        product_extraApr.setText(df.format(Double.parseDouble(map.get(BaseParam.QIAN_PRODUCT_EXTRAAWARDAPR))));
                        rl_apr1.setClickable(true);
                    } else {
                        lin_product_extraApr.setVisibility(View.GONE);
                        rl_apr1.setClickable(false);

                    }

                    String productLimitTime = "";

                    // 钱包包产品,现在已经没有钱包包产品了不用管
                    if (TextUtils.equals(map.get(BaseParam.QIAN_PRODUCT_BR_TYPE), "3")) {
                        productLimitTime = ProductContentAct3.this.getString(R.string.product_list_apr_baobao_format);
                        product_limit_time.setText(Html.fromHtml(productLimitTime)+"天");

                    } else {// 非钱贝勒产品
                        if (map.get(BaseParam.QIAN_PRODUCT_ISDAY).equals("1")) {// 天标
                            productLimitTime = map.get(BaseParam.QIAN_PRODUCT_TIMELIMITDAY);
                        } else {
                            productLimitTime = map.get(BaseParam.QIAN_PRODUCT_TIMELIMIT);
                        }
                        product_limit_time.setText( productLimitTime+"天");
                    }


                    borrowType = map.get(BaseParam.QIAN_PRODUCT_BORROWTYPE);
                    style = map.get(BaseParam.QIAN_PRODUCT_STYLE);
                    productStatus = map.get(BaseParam.QIAN_PRODUCT_PRODUCTSTATUS);
                    isNewHand = Integer.parseInt(map.get(BaseParam.QIAN_PRODUCT_ISNEWHAND));

                    product_tender_btn.setVisibility(View.VISIBLE);
                    countDown.setVisibility(View.GONE);

                    if (map.get(BaseParam.QIAN_PRODUCT_ISNEWHAND).equals("1")) {  //如果是新手标的话
                        product_tender_btn.setText("仅限新手投资");

                        // 额外奖励
                        mProductIsNewHand = true;
                    } else {
                        product_tender_btn.setText("立即投资");
                    }
                    if (isRestart){  //是从onRestart方法调用来的
                        String showStatus= map.get("showStatus"); //是否是新手标
                        String fastestTender=map.get("fastestTender");//首投人号码
                        String sendFlag=map.get("sendFlag"); //是否满标
                        String lastTender=map.get("lastTender"); //尾投人号码
                        String largestTenderUser=map.get("largestTenderUser"); //最高投资人号码
                        String largestTenderSum=map.get("largestTenderSum"); //最高投资人累计投资金额
                        String firstRedPacket=map.get("firstRedPacket");
                        String highestRedPacket=map.get("highestRedPacket");
                        String lastRedPacket=map.get("lastRedPacket");
//                        touziHistoryFragment=new com.rd.qnz.product.fragment3.TouziHistoryFragment(ProductContentAct3.this,viewpager); //投资记录
                        touziHistoryFragment=new TouziHistoryFragment(borrowId,ProductContentAct3.this,
                                showStatus,fastestTender,sendFlag,lastTender,largestTenderUser,largestTenderSum
                                ,firstRedPacket,highestRedPacket,lastRedPacket); //投资记录

                    }else {  //项目详情页第一次打开
                        /**
                         * 1.6.0改变
                         */
                        String showStatus= map.get("showStatus"); //是否是新手标
                        String fastestTender=map.get("fastestTender");//首投人号码
                        String sendFlag=map.get("sendFlag"); //是否满标
                        String lastTender=map.get("lastTender"); //尾投人号码
                        String largestTenderUser=map.get("largestTenderUser"); //最高投资人号码
                        String largestTenderSum=map.get("largestTenderSum"); //最高投资人累计投资金额
                        String firstRedPacket=map.get("firstRedPacket");
                        String highestRedPacket=map.get("highestRedPacket");
                        String lastRedPacket=map.get("lastRedPacket");

                        knowProjectFragment=new KnowProjectFragment(borrowId,"putong",ProductContentAct3.this) ;  //项目详情/
                        saveFragmentForProduct = new SaveFragmentForProduct(ProductContentAct3.this);/* 安全保障 */

                        touziHistoryFragment=new TouziHistoryFragment(borrowId,ProductContentAct3.this,
                                showStatus,fastestTender,sendFlag,lastTender,largestTenderUser,largestTenderSum
                                ,firstRedPacket,highestRedPacket,lastRedPacket); //投资记录



//                        knowProjectFragment=new KnowProjectFragment(ProductContentAct3.this,viewpager) ;  //项目详情/
//                        saveFragmentForProduct = new com.rd.qnz.product.fragment3.SaveFragmentForProduct(ProductContentAct3.this,viewpager);/* 安全保障 */

//                        touziHistoryFragment=new com.rd.qnz.product.fragment3.TouziHistoryFragment(ProductContentAct3.this,viewpager); //投资记录



                        fragments.add(knowProjectFragment);
                        fragments.add(saveFragmentForProduct);
                        fragments.add(touziHistoryFragment);

                         initViewpager();
                    }





                    DecimalFormat df = new DecimalFormat("#,###");
                    String sykt=  df.format(balance);

                    product_total_money.setText( sykt + "元");
                    my_day_money.setText(lowestAccount + "元");


                    if (productStatus.equals("1")) {//1：申购
                        if (account != accountYes) { //如果总金额不等于已购买金额
                            product_content_mProgressBar.setVisibility(View.VISIBLE);
                            product_total_money.setText(sykt + "元");
                            product_content_mProgressBar.setText(percent + "%");// 设置最大的值 92%



                            SharedPreferences preferences = getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_USER, Context.MODE_PRIVATE);
                            oauthToken = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN, "");
                            realStatus = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_REALSTATUS, "");

                            if (oauthToken.equals("") || oauthToken == null) {
                                product_tender_btn.setText("立即投资");
                            } else {
                                if (realStatus.equals("0") || realStatus == null) {
                                    product_tender_btn.setText("立即投资");
                                }
                            }
                        } else {
                            product_content_mProgressBar.setVisibility(View.GONE);
                            product_total_money.setText("0");
                            product_tender_btn.setText("已售罄");
                            tv_allmoney.setText("0");
                        product_tender_btn.setBackgroundResource(R.color.buy_btn);
                        product_tender_btn.setClickable(false);

                    }
                } else if (productStatus.equals("2")) {//2:已售完 (在app端实际上不会有售罄这个标记)
                        product_total_money.setText("0");
                        tv_allmoney.setText("0");

                        product_content_mProgressBar.setVisibility(View.VISIBLE);
                        product_content_mProgressBar.setText(percent+"%");// 设置最大的值
                        product_tender_btn.setText("已售罄");
                        product_tender_btn.setBackgroundResource(R.color.buy_btn);
                        product_tender_btn.setClickable(false);
                        if (map.get("lastTender") != null && map.get("lastTender").length() == 11) {
                        }


                    } else if (productStatus.equals("3")) {//3:还款中
                        product_content_mProgressBar.setVisibility(View.VISIBLE);
                        product_content_mProgressBar.setText(percent+"%");// 设置最大的值
                        String rate = "";
                        if (style.equals("1")) {
                            rate = AppTool.denge(account, Double.parseDouble(apr), timeLimit);
                        } else {
                            rate = AppTool.rateReceipts(account, Float.parseFloat(apr), isday, timeLimitDay, timeLimit);
                        }
                        product_tender_btn.setText("已售罄");
                        product_tender_btn.setBackgroundResource(R.color.buy_btn);
                        product_tender_btn.setClickable(false);
                        if (map.get("lastTender") != null && map.get("lastTender").length() == 11) {
                        }
                    } else if (productStatus.equals("4")) {//4:已还款(测试版有这个环境,正式版没有)
                        product_tender_btn.setText("已售罄");
                        product_tender_btn.setClickable(false);
                        product_content_mProgressBar.setVisibility(View.GONE);
                        String rate = "";
                        if (style.equals("1")) {
                            rate = AppTool.denge(account, Double.parseDouble(apr), timeLimit);
                        } else {
                            rate = AppTool.rateReceipts(account, Float.parseFloat(apr), isday, timeLimitDay, timeLimit);
                        }
                        product_tender_btn.setBackgroundResource(R.color.buy_btn);
                        if (map.get("lastTender") != null && map.get("lastTender").length() == 11) {
                        }
                    } else if (productStatus.equals("0")) {//0:预售
                        product_content_mProgressBar.setVisibility(View.VISIBLE);
                        product_content_mProgressBar.setText(percent + "%");
                        product_total_money.setText(sykt+"元");

                        countDownTime = map.get("countDownTime");
                        if (Integer.valueOf(countDownTime) > 0) {

                            product_tender_btn.setVisibility(View.GONE);
                            countDown.setVisibility(View.VISIBLE);
                            long time = Long.parseLong(countDownTime) / 1000 + 2;
                            long days = time / (60 * 60 * 24);
                            long hours = time / (60 * 60) - days * 24;
                            long minutes = time / 60 - days * 60 * 24 - hours * 60;
                            long mill = time - days * (60 * 60 * 24) - hours * (60 * 60) - minutes * 60;
                            mDay = days;
                            mHour = hours;
                            mMin = minutes;
                            mSecond = mill;// 天 ,小时,分钟,秒
                            if (friststart == 1) {
                                startRun();
                                friststart = 2;
                            }
                        } else if (Integer.valueOf(countDownTime) <= 0) {
                            product_tender_btn.setVisibility(View.GONE);
                            countDown.setVisibility(View.GONE);
                            progressDialog = dia.getLoginDialog(ProductContentAct3.this, "刷新中，请稍后..");
                            progressDialog.show();
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Thread.sleep(3000);
                                        Message message = Message.obtain();
                                        message.what = 10;
                                        gitHandler.sendMessage(message);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();
                        }
                    } else {
                        if (map.get("errorCode").equals("TOKEN_NOT_EXIST")) {
                            startActivity(new Intent(ProductContentAct3.this, Login.class));
                        } else if (map.get("errorCode").equals("TOKEN_EXPIRED")) {
                            startActivity(new Intent(ProductContentAct3.this, Login.class));
                        } else {
                            showToast(Check.checkReturn(map.get("errorCode")));
                        }
                    }
                }
                mScrollView.onRefreshComplete();
                super.handleMessage(paramMessage);
            }
        }
    }

    private void initViewpager() {
        viewpager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), fragments));
        viewpager.setCurrentItem(0);
        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        product_radiobutton1_top.setChecked(true);
                        product_radiobutton1_buttom.setChecked(true);
                        currentItem=0;
                        mScrollView.setMode(PullToRefreshBase.Mode.PULL_FROM_START); //只支持下拉
                        s.smoothScrollTo(0,0);

//                        s.smoothScrollBy(0,0);
                        break;
                    case 1:
                        product_radiobutton2_top.setChecked(true);
                        product_radiobutton2_buttom.setChecked(true);
                        currentItem=1;
                        mScrollView.setMode(PullToRefreshBase.Mode.PULL_FROM_START); //只支持下拉
                        s.smoothScrollBy(0,0);
                        s.smoothScrollTo(0,0);
                        break;
                    case 2:
                        product_radiobutton3_top.setChecked(true);
                        product_radiobutton3_buttom.setChecked(true);
                        currentItem=2;
                        mScrollView.setMode(PullToRefreshBase.Mode.BOTH);
                        mScrollView.getRefreshableView().smoothScrollBy(0,0);
                        s.smoothScrollTo(0,0);
                        break;

                }
            }
            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                cursor_view_top.setXY(arg0, arg1);
//                cursor_view_buttom.setXY(arg0,arg1);
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                s.smoothScrollTo(0,0);
            }
        });

        product_radiogroup_buttom.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() { //底部radiogroup

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.product_radiobutton1_buttom:
                        viewpager.setCurrentItem(0);
                        currentItem=0;
                        product_radiobutton1_buttom.setChecked(true);
                        s.smoothScrollTo(0,0);
                        break;
                    case R.id.product_radiobutton2_buttom:
                        viewpager.setCurrentItem(1);
                        currentItem=1;
                        product_radiobutton2_buttom.setChecked(true);
                        s.smoothScrollTo(0,0);
                        break;
                    case R.id.product_radiobutton3_buttom:
                        viewpager.setCurrentItem(2);
                        product_radiobutton3_buttom.setChecked(true);
                        currentItem=2;
                        s.smoothScrollTo(0,0);
                        break;
                }
            }
        });

        product_radiogroup_top.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.product_radiobutton1_top:
                        viewpager.setCurrentItem(0);
                        product_radiobutton1_top.setChecked(true);
                        currentItem=0;
                        s.smoothScrollTo(0,0);
                        break;
                    case  R.id.product_radiobutton2_top:
                        viewpager.setCurrentItem(1);
                        product_radiobutton2_top.setChecked(true);
                        currentItem=1;
                        s.smoothScrollTo(0,0);
                        break;
                    case  R.id.product_radiobutton3_top:
                        viewpager.setCurrentItem(2);
                        product_radiobutton3_top.setChecked(true);
                        currentItem=2;
                        s.smoothScrollTo(0,0);
                        break;
                }
            }
        });
    }


    private BroadcastReceiver myReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action=intent.getAction();
            switch (action){
                case "NewRealAct":
                    startDataRequest();
                    break;
                case "web":
                     finish();
                    break;
            }

        }
    };


    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        preferences = getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_USER, Context.MODE_PRIVATE);
        mAuthoToken = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN, "");
    }

    @Override  //从其他界面返回回来需要重新请求一次数据
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG,"ProductContentAct3 onRestart()被调用");
        isRestart=true;
        startDataRequest();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG,"ProductContentAct3 onStop()被调用");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"ProductContentAct3 onDestroy()被调用");
        if (myReceiver != null) {
            try{
                unregisterReceiver(myReceiver);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        if (null != progressDialog && progressDialog.isShowing()) {// 隐藏加载框
            ProductContentAct3.this.progressDialog.dismiss();
            progressDialog=null;
        }
    }



    /**
     * 14.	产品详情
     */
    private void startDataRequest() {

        if (Check.hasInternet(ProductContentAct3.this)) {
            ProductContentAct3.this.initArray();
            ProductContentAct3.this.param.add(BaseParam.QIAN_PRODUCT_BORROWID);
            ProductContentAct3.this.value.add(borrowId);

            ProductContentAct3.this.param.add(BaseParam.URL_QIAN_API_APPID);
            ProductContentAct3.this.value.add(myApp.appId);
            ProductContentAct3.this.param.add(BaseParam.URL_QIAN_API_SERVICE);
            ProductContentAct3.this.value.add("productDetail");
            ProductContentAct3.this.param.add(BaseParam.URL_QIAN_API_SIGNTYPE);
            ProductContentAct3.this.value.add(myApp.signType);

            String[] array = new String[]{
                    BaseParam.QIAN_PRODUCT_BORROWID + "=" + borrowId,
                    BaseParam.URL_QIAN_API_APPID + "=" + myApp.appId,
                    BaseParam.URL_QIAN_API_SERVICE + "=productDetail",
                    BaseParam.URL_QIAN_API_SIGNTYPE + "=" + myApp.signType};

            String sign = apiModel.sortStringArray(array);
            ProductContentAct3.this.param.add(BaseParam.URL_QIAN_API_SIGN);
            ProductContentAct3.this.value.add(sign);
            ProductContentAct3.this.progressDialog = ProductContentAct3.this.dia.getLoginDialog(ProductContentAct3.this, "正在获取数据..");
            progressDialog.setCancelable(true);
            ProductContentAct3.this.progressDialog.show();
            new Thread(new JsonRequeatThreadProductContent(
                    ProductContentAct3.this,
                    myApp,
                    ProductContentAct3.this.myHandler,
                    ProductContentAct3.this.param,
                    ProductContentAct3.this.value)
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_apr1:  //总利息布局
                rl_apr2.setVisibility(View.VISIBLE);
                rl_apr1.setVisibility(View.GONE);
                break;
            case R.id.rl_apr2:
                rl_apr1.setVisibility(View.VISIBLE);
                rl_apr2.setVisibility(View.GONE);
                break;
            case R.id.product_tender_btn:  //点击了立即投资
                if (MineShow.fastClick()) {
                    if (mAuthoToken.equals("") || mAuthoToken == null) {//未登录
                        startActivity(new Intent(ProductContentAct3.this, Login.class));
                        return;
                    } else {
                        if (realStatus.equals("0") || realStatus == null) {//未实名认证
                            startActivity(new Intent(ProductContentAct3.this, NewRealAct.class));
                            return;
                        }
                    }
                    if (account == accountYes) {//余额为0（标总额=已投金额）
                        return;
                    }
                    if (TextUtils.equals(Profile.getUserPayPassWordStatus(), "0")) {//未设置交易密码
                        Intent intent = new Intent(ProductContentAct3.this, ForgetPasswordAct.class);
                        startActivity(intent);
                        return;
                    }

                    if (mProductIsNewHand && TextUtils.equals(Profile.getUserIsNewHandStatus(), "0")) {//新手产品
                        showToast("该产品为新手标您无法投资");
                    } else {
                        ProductContentPurchaseAct.start(
                                ProductContentAct3.this,
                                borrowId,//产品ID（标id）
                                productStatus, //产品状态：0：预售 ，1：申购，2:已售完，3:还款中,4:已完结
                                title, //标名
                                type, //类型（102,103，110,112）其中（102,103借款类，110，112债券类，其中110是按照份数）
                                flowCount, //份数
                                flowMoney,//份金额
                                balance, //余额（标总额-已投金额）
                                isday,//是否是天标(1天标 0月标)
                                timeLimitDay + "", //借款期限(天标)
                                timeLimit + "",//借款期限(月)
                                lowestAccount,//最低投资金额
                                lastRepayTime, //最后待收时间
                                style,//还款方式（1;等额本息，2;一次性到期还款，3;每月还息到期还本）
                                apr, //基本几率
                                rateapr, //总利率
                                extraAwardApr, //额外加息
                                interestDay, //天数
                                mBrType//品具体类型 1新手标 2 普通标 3 钱贝勒
                        );
                    }
                }
                break;
            /* 点击了还款方式  支持提前还款 */
            case R.id.repayment:
                if (MineShow.fastClick()) {
                    Intent repayment = new Intent(ProductContentAct3.this, LastRepayTimeActivity.class);
                    repayment.putExtra("lastRepayTime", lastRepayTime);
                    startActivity(repayment);
                }
                break;



            case R.id.relativeLayout2:  //点击了额外奖励
                if (MineShow.fastClick()) {
                    Intent intent = new Intent(ProductContentAct3.this, WebViewAct.class);
                    intent.putExtra("web_url", act_url);
                    intent.putExtra("title", "额外奖励");
                    startActivity(intent);
                }
                break;

        }
    }

    /**
     * 开启倒计时
     */
    private void startRun() {
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
                    startDataRequest();
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

    private Handler gitHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 10) {
                startDataRequest();
            }
        }
    };






    /**
     * 保存位图到本地
     * @param bitmap
     * @param path 本地路径
     * @return void
     */
    public void SavaImage(Bitmap bitmap, String dir,String path){
        File file=new File(dir);
        FileOutputStream fileOutputStream=null;
        //文件夹不存在，则创建它
        if(!file.exists()){
            file.mkdir();
        }
        try {
            fileOutputStream=new FileOutputStream(path);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100,fileOutputStream);
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示本地图片
     */
    public  void show(ImageView imageView,String path){
        File f=new File(path);
        if (!f.exists()){
            return;
        }
        Bitmap bitmap= BitmapFactory.decodeFile(path);
        imageView.setImageBitmap(bitmap);
    }

}
