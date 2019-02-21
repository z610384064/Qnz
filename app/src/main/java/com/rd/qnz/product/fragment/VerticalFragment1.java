package com.rd.qnz.product.fragment;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.CustScrollView;
import com.handmark.pulltorefresh.library.ProductContent_PullToRefreshScrollView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.lzy.okgo.request.PostRequest;
import com.rd.qnz.R;
import com.rd.qnz.Test;
import com.rd.qnz.bean.BaseBean;
import com.rd.qnz.bean.ProductContentBean;
import com.rd.qnz.custom.APIModel;
import com.rd.qnz.custom.MineShow;
import com.rd.qnz.custom.MyApplication;
import com.rd.qnz.dialog.CustomProgressDialog;
import com.rd.qnz.dialog.GetDialog;
import com.rd.qnz.http.HttpUtils;
import com.rd.qnz.http.okgo.callback.JsonCallback;
import com.rd.qnz.login.Login;
import com.rd.qnz.product.LastRepayTimeActivity;
import com.rd.qnz.tools.AppTool;
import com.rd.qnz.tools.BaseParam;
import com.rd.qnz.tools.Check;
import com.rd.qnz.util.MathUtil;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

import static com.rd.qnz.custom.MyApplication.appId;
import static com.rd.qnz.qiyu.Utils.runOnUiThread;

/**
 *  2017/9/26 0026.
 */

public class VerticalFragment1 extends Fragment implements View.OnClickListener{
    private Context context;
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


    private String isAdvanceRepay;//是否支持提前还款  1支持 0 不
    private String productStatus = "";
    private SharedPreferences preferences;
    private String mAuthoToken;
    private String mRealStatus;
    private String countDownTime;

    private TextView product_total_money;// 剩余可投
    private TextView my_day_money;//起投金额
    private RelativeLayout normal_standard;



    private RelativeLayout repayment;
    private TextView repayment_text,out_time;
    private ImageView repayment_image;

    private ProductContent_PullToRefreshScrollView mScrollView;





    private TextView product_apr, qian_rate, bank_rate,
            product_extraApr, product_limit_time;
    private EditText qian_edit;
    private String mBrType;
    private RelativeLayout lin_product_extraApr;


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


    private int percent;
    private int j = 0;

    private long mDay;
    private long mHour;
    private long mMin;
    private long mSecond;// 天 ,小时,分钟,秒

    // 倒计时
    private TextView daysTv, hoursTv, minutesTv, secondsTv;
    private boolean isRun = true;



    private int friststart = 1;

    private static final int SUCCESS = 1;
    private static final int FALL = 2;


    //顶部事先隐藏的布局

    /**
     *  还款日
     * @param savedInstanceState
     */
    private TextView product_apr1;  //总利率
    private double allApr; //总利息
    private LinearLayout rl_apr1;
    private RelativeLayout rl_apr2;
    private TextView tv_interestStartTime,tv_interestEndTime,tv_validTime;//计息时间,回款日,募集时间
    private TextView tv_name; //项目名称
    private TextView tv_allmoney; //项目总额
    private TextView product_content_mProgressBar; //进度条的值
    private ImageView iv_progress; //进度条对应的图片
    private int iv_progress_width;
    private int progress_width=0; //进度条宽度
    private int text_value;
    private ImageView progress_background;
    private boolean isRestart=false; //项目调用了onRestart方法


    private String borrowId;
    private View view;
   private  Test parentActivity;

    public VerticalFragment1(){}
    public VerticalFragment1(Context context,String borrowId){
        this.context=context;
        this.borrowId=borrowId;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         view=View.inflate(context,R.layout.fragment1,null);

        IntentFilter intentFilter=new IntentFilter("NewRealAct");
        intentFilter.addAction("web");
        context.registerReceiver(myReceiver,intentFilter);


        this.dia = new GetDialog();
        myApp = (MyApplication) getActivity().getApplication();
//        this.myHandler = new MyHandler();
        preferences =context. getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_USER, Context.MODE_PRIVATE); //sp_user
        mAuthoToken = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN, "");
        mRealStatus = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_REALSTATUS, ""); //是否实名认证
        initView();
        startDataRequest();
        return view;
    }


    /**
     * 做控件的声明
     */
    private void initView() {
        progress_background= (ImageView)view. findViewById(R.id.progress_background);
        tv_name= (TextView) view.findViewById(R.id.tv_name); //项目名称
        tv_interestStartTime= (TextView) view.findViewById(R.id.tv_interestStartTime); //满标次日
        tv_interestEndTime= (TextView) view.findViewById(R.id.tv_interestEndTime); //回款时间
        tv_validTime= (TextView) view.findViewById(R.id.tv_validTime); //募集时间

        rl_apr1= (LinearLayout) view.findViewById(R.id.rl_apr1);
        rl_apr2= (RelativeLayout)view. findViewById(R.id.rl_apr2);
        rl_apr1.setOnClickListener(this);
        rl_apr2.setOnClickListener(this);
        tv_allmoney= (TextView) view.findViewById(R.id.tv_allmoney);
        product_apr = (TextView) view.findViewById(R.id.product_apr);// 总利率
        lin_product_extraApr = (RelativeLayout)  view.findViewById(R.id.lin_product_extraApr);
        product_apr1= (TextView) view.findViewById(R.id.product_apr1);
        product_extraApr = (TextView) view.findViewById(R.id.product_extraApr);
        product_limit_time = (TextView) view.findViewById(R.id.product_limit_time);

        product_content_mProgressBar = (TextView)view. findViewById(R.id.product_content_mProgressBar);

        daysTv = (TextView)  view.findViewById(R.id.days_tv);
        hoursTv = (TextView)  view.findViewById(R.id.hours_tv);
        minutesTv = (TextView) view. findViewById(R.id.minutes_tv);
        secondsTv = (TextView)  view.findViewById(R.id.seconds_tv);


        product_total_money = (TextView) view. findViewById(R.id.product_total_money);
        my_day_money = (TextView)  view.findViewById(R.id.my_day_money);

        repayment = (RelativeLayout)  view.findViewById(R.id.repayment);
        repayment.setOnClickListener(this);

        out_time = (TextView)  view.findViewById(R.id.out_time);
        repayment_text = (TextView)  view.findViewById(R.id.repayment_text);
        repayment_image = (ImageView)  view.findViewById(R.id.repayment_image);
        iv_progress= (ImageView)  view.findViewById(R.id.iv_progress);



        // 投资有奖

        //了解项目

        //安全保障
//        mScrollView = (ProductContent_PullToRefreshScrollView) view. findViewById(R.id.scrollVeiw);
//        mScrollView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
//        //下拉加载
//        mScrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ScrollView>() {
//
//            @Override
//            public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
//
//                // TODO Auto-generated method stub
//                refreshView.getLoadingLayoutProxy().setLoadingDrawable(getResources().getDrawable(R.drawable.default_ptr_rotate));
//                refreshView.getLoadingLayoutProxy().setPullLabel("下拉加载更多");
//                refreshView.getLoadingLayoutProxy().setRefreshingLabel("加载中...");
//                refreshView.getLoadingLayoutProxy().setReleaseLabel("下拉加载更多");
//                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel("  ");
//                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel("");
//                    startDataRequest();
//
//
//            }
//
//            @Override   //上拉加载
//            public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {  //上拉加载更多
//
////                    refreshView.getLoadingLayoutProxy().setLoadingDrawable(getResources().getDrawable(R.drawable.default_ptr_rotate));
////                    refreshView.getLoadingLayoutProxy().setPullLabel("上拉加载更多");
////                    refreshView.getLoadingLayoutProxy().setRefreshingLabel("加载中...");
////                    refreshView.getLoadingLayoutProxy().setReleaseLabel("上拉加载更多");
//                    mScrollView.onRefreshComplete();
//
//
//            }
//        });



    }

    /**
     * 14.	产品详情
     */
    public void startDataRequest() {

        progressDialog = dia.getLoginDialog(getActivity(), "正在获取数据..");
        progressDialog.setCancelable(true);
        progressDialog.show();
        String str[] = new String[]{
                BaseParam.QIAN_MY_REPAY_BORROWID, borrowId,
                "appId", appId,
                BaseParam.URL_QIAN_API_SERVICE, "productDetail",
                BaseParam.URL_QIAN_API_SIGNTYPE, myApp.signType,
        };
        PostRequest request = HttpUtils.getRequest(BaseParam.URL_QIAN_GET_PRODUCT_CONTENT, context, str);
        request.execute(new JsonCallback<BaseBean<ProductContentBean>>() {
            @Override
            public void onSuccess(BaseBean<ProductContentBean> productContentBeanBaseBean, Call call, Response response) {

                if (null != progressDialog && progressDialog.isShowing()) { // 隐藏加载框
                    progressDialog.dismiss();
                }

                ProductContentBean productContentBean = productContentBeanBaseBean.resultData;
                ProductContentBean.ProductBean productBean = productContentBean.getProduct();
                String resultCode = productContentBeanBaseBean.resultCode;
                if (!TextUtils.isEmpty(resultCode) && "1".equals(resultCode)) {

                    tv_name.setText(productBean.getName() + "");

                    flowCount = productBean.getFlowCount();// 还可以申购的份数
                    flowMoney = productBean.getFlowMoney(); // 每份申购的金额


                    type = productBean.getType();

                    isday = productBean.getIsday();

                    timeLimitDay = productBean.getTimeLimitDay();

                    timeLimit = productBean.getTimeLimit();
                    apr = productBean.getNormalApr();
                    rateapr = productBean.getApr();
                    extraAwardApr = productBean.getExtraAwardApr(); //1.5

                    interestDay = productContentBean.getInterestDay();

                    double accountYes1 = Double.parseDouble(productBean.getAccountYes());
                    accountYes = (int) accountYes1;
                    duan = (int) (accountYes / 100);
                    account = Integer.parseInt(productBean.getAccount());
                    tv_allmoney.setText(new DecimalFormat("#,###").format(account) + "元");
                    percent = (int) (accountYes1 * 100 / account); //已经购买的百分比
                    if (progress_width == 0) {  //进度条的宽度还不确定,所以需要去算一次
                        progress_background.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                            @Override
                            public void onGlobalLayout() {
                                //一般用完立即移除，因为只要该view的宽高改变都会再引起回调该方法
                                progress_background.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                                //获取到高度
                                progress_width = progress_background.getWidth();

                                text_value = percent * progress_width / 100;


                                ValueAnimator animator = ValueAnimator.ofInt(0, text_value);
                                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                    @Override
                                    public void onAnimationUpdate(ValueAnimator animation) {
                                        int value = (int) animation.getAnimatedValue();
                                        product_content_mProgressBar.setTranslationX(value);

                                    }
                                });

                                animator.setDuration(500);
                                animator.start();


                                ValueAnimator animator2 = ValueAnimator.ofInt(0, percent);
                                animator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                    @Override
                                    public void onAnimationUpdate(ValueAnimator animation) {
                                        int value = (int) animation.getAnimatedValue();
                                        iv_progress_width = progress_width * value / 100;
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

                    lastRepayTime = productBean.getLastRepayTime();  //还款日期
                    String interestStartTime = productBean.getInterestStartTime();
                    String interestEndTime = productBean.getInterestEndTime();
                    String validTime = productBean.getValidTime();
                    if (!TextUtils.isEmpty(interestStartTime)) {
                        tv_interestStartTime.setText(interestStartTime);
                    }
                    if (!TextUtils.isEmpty(interestEndTime)) {
                        tv_interestEndTime.setText(interestEndTime);
                    }
                    if (!TextUtils.isEmpty(validTime)) {
                        tv_validTime.setText(validTime + "天");
                    }
                    balance = account - accountYes1;

                    // 基础年化收益率
                    BigDecimal baseApr = new BigDecimal(productBean.getNormalApr()).setScale(2, BigDecimal.ROUND_HALF_UP);
                    // 额外年化收益率
                    BigDecimal extraApr = new BigDecimal(productBean.getExtraAwardApr()).setScale(2,BigDecimal.ROUND_HALF_UP);
                    // 总年化收益率 = 基础年化收益率+额外年化收益率
                    BigDecimal allApr = baseApr.add(extraApr);

                    // 填充数据
                    product_apr1.setText(MathUtil.formatRate(allApr.doubleValue() + ""));
                    product_apr.setText(MathUtil.formatRate(baseApr.doubleValue() + ""));


                    mBrType = productBean.getProductType();

                    lowestAccount = productBean.getLowestAccount();
                    isAdvanceRepay = productBean.getIsAdvanceRepay();


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
                    out_time.setText(AppTool.getMsgTwoDateDistance1(productBean.getSecVerifyTime())); //标的发布时间
                    /**
                     *  三个红包分别被领取的时间
                     */
                    if (!"0".equals(extraAwardApr)) { //有额外加息
                        lin_product_extraApr.setVisibility(View.VISIBLE);
                        product_extraApr.setText(MathUtil.formatRate(extraApr.doubleValue() + ""));
                        rl_apr1.setClickable(true);
                    } else {
                        lin_product_extraApr.setVisibility(View.GONE);
                        rl_apr1.setClickable(false);

                    }

                    String productLimitTime = "";

                    // 钱包包产品,现在已经没有钱包包产品了不用管
                    if (TextUtils.equals(mBrType, "3")) {
                        productLimitTime = getString(R.string.product_list_apr_baobao_format);
                        product_limit_time.setText(Html.fromHtml(productLimitTime) + "天");

                    } else {  // 非钱贝勒产品
                        if (isday.equals("1")) {// 天标
                            productLimitTime = productBean.getTimeLimitDay() + "";
                        } else {
                            productLimitTime = productBean.getTimeLimit() + "";
                        }
                        product_limit_time.setText(productLimitTime + "天"); //标的期限
                    }
                    borrowType = productBean.getBorrowType();
                    style = productBean.getStyle();
                    productStatus = productBean.getProductStatus();
                    isNewHand = productBean.getIsNewHand();
                    parentActivity = (Test) getActivity();
                    parentActivity.setButtomVisible(View.VISIBLE);
                    parentActivity.setCoudownButtomVisible(View.GONE);


                    if (isNewHand == 1) {  //如果是新手标的话
                        parentActivity.setButtomText("仅限新手投资");
                        // 额外奖励
                        mProductIsNewHand = true;
                    } else {
                        parentActivity.setButtomText("立即投资");
                    }


                    DecimalFormat df = new DecimalFormat("#,###");
                    String sykt = df.format(balance);

                    product_total_money.setText(sykt + "元");
                    my_day_money.setText(lowestAccount + "元");


                    if (productStatus.equals("1")) {  //1：申购
                        if (account != accountYes) { //如果总金额不等于已购买金额
                            product_content_mProgressBar.setVisibility(View.VISIBLE);
                            product_total_money.setText(sykt + "元");
                            product_content_mProgressBar.setText(percent + "%");// 设置最大的值 92%


                            SharedPreferences preferences = context.getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_USER, Context.MODE_PRIVATE);
                            oauthToken = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN, "");
                            realStatus = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_REALSTATUS, "");

                            if (oauthToken.equals("") || oauthToken == null) {
                                parentActivity.setButtomText("立即投资");
                            } else {
                                if (realStatus.equals("0") || realStatus == null) {
                                    parentActivity.setButtomText("立即投资");
                                }
                            }
                        } else {
                            product_content_mProgressBar.setVisibility(View.GONE);
                            product_total_money.setText("0");

                            tv_allmoney.setText("0");
                            parentActivity.setButtomText("已售罄");
                            parentActivity.setButtomBackground(R.color.buy_btn);
                            parentActivity.setButtomClickble(false);
                        }
                    } else if (productStatus.equals("2")) {  //2:已售完 (在app端实际上不会有售罄这个标记)
                        product_total_money.setText("0");
                        tv_allmoney.setText("0");

                        product_content_mProgressBar.setVisibility(View.VISIBLE);
                        product_content_mProgressBar.setText(percent + "%");// 设置最大的值
                        parentActivity.setButtomText("已售罄");
                        parentActivity.setButtomBackground(R.color.buy_btn);
                        parentActivity.setButtomClickble(false);


                    } else if (productStatus.equals("3")) {  //3:还款中
                        product_content_mProgressBar.setVisibility(View.VISIBLE);
                        product_content_mProgressBar.setText(percent + "%");// 设置最大的值
                        String rate = "";
                        if (style.equals("1")) {
                            rate = AppTool.denge(account, Double.parseDouble(apr), timeLimit);
                        } else {
                            rate = AppTool.rateReceipts(account, Float.parseFloat(apr), isday, timeLimitDay, timeLimit);
                        }
                        parentActivity.setButtomText("已售罄");
                        parentActivity.setButtomBackground(R.color.buy_btn);
                        parentActivity.setButtomClickble(false);

                    } else if (productStatus.equals("4")) {  //4:已还款(测试版有这个环境,正式版没有)
                        parentActivity.setButtomText("已售罄");
                        parentActivity.setButtomClickble(false);

                        product_content_mProgressBar.setVisibility(View.GONE);
                        String rate = "";
                        if (style.equals("1")) {
                            rate = AppTool.denge(account, Double.parseDouble(apr), timeLimit);
                        } else {
                            rate = AppTool.rateReceipts(account, Float.parseFloat(apr), isday, timeLimitDay, timeLimit);
                        }
                        parentActivity.setButtomBackground(R.color.buy_btn);

                    } else if (productStatus.equals("0")) {   //0:预售
                        product_content_mProgressBar.setVisibility(View.VISIBLE);
                        product_content_mProgressBar.setText(percent + "%");
                        product_total_money.setText(sykt + "元");

                        countDownTime = productBean.getCountDownTime();
                        if (Integer.valueOf(countDownTime) > 0) {
                            parentActivity.setButtomVisible(View.GONE);
                            parentActivity.setCoudownButtomVisible(View.VISIBLE);
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
                                parentActivity.startRun();
                                friststart = 2;
                            }
                        } else if (Integer.valueOf(countDownTime) <= 0) {
                            parentActivity.setButtomVisible(View.GONE);
                            parentActivity.setCoudownButtomVisible(View.GONE);
                            progressDialog = dia.getLoginDialog(getActivity(), "刷新中，请稍后..");
                            progressDialog.show();
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Thread.sleep(3000);
                                        Message message = Message.obtain();
                                        message.what = 10;
                                        parentActivity.gitHandler.sendMessage(message);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();
                        } else {
                            String errorCode = productContentBeanBaseBean.errorCode;
                            if (!TextUtils.isEmpty(errorCode)) {
                                if (errorCode.equals("TOKEN_NOT_EXIST")) {
                                    startActivity(new Intent(getActivity(), Login.class));
                                } else if (errorCode.equals("TOKEN_EXPIRED")) {
                                    startActivity(new Intent(getActivity(), Login.class));
                                } else {
                                    showToast(Check.checkReturn(errorCode));
                                }
                            } else {
                                showToast("网络错误,请重新再试");
                            }
                        }

                    }
                   parentActivity.setData2(productContentBean,balance);

                } else {
                    String errorCode = productContentBeanBaseBean.errorCode;
                    if (!TextUtils.isEmpty(errorCode)) {
                        if (errorCode.equals("TOKEN_NOT_EXIST")) {
                            startActivity(new Intent(getActivity(), Login.class));
                        } else if (errorCode.equals("TOKEN_EXPIRED")) {
                            startActivity(new Intent(getActivity(), Login.class));
                        } else {
                            showToast(Check.checkReturn(errorCode));
                        }
                    } else {
                        showToast("网络错误,请重新再试");
                    }
                }
//                mScrollView.onRefreshComplete();

            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                MineShow.toastShow("网络错误,请稍后再试",context);
//                mScrollView.onRefreshComplete();
            }
        });
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

            /* 点击了还款方式  支持提前还款 */
            case R.id.repayment:
                if (MineShow.fastClick()) {
                    Intent repayment = new Intent(context, LastRepayTimeActivity.class);
                    repayment.putExtra("lastRepayTime", lastRepayTime);
                    startActivity(repayment);
                }
                break;

        }
    }

    private class MyHandler extends Handler {

        private DecimalFormat df;

        @SuppressLint("NewApi")
        public void handleMessage(Message paramMessage) {


            Bundle localBundle1 = paramMessage.getData();
            ArrayList<Parcelable> product_R = localBundle1.getParcelableArrayList(BaseParam.QIAN_REQUEAT_PRODUCT_CONTENT);
            if (null != progressDialog && progressDialog.isShowing()) {// 隐藏加载框
                progressDialog.dismiss();
            }
            if (null != product_R) {
                Map<String, String> map = (Map<String, String>) product_R.get(0);
                String resultCode = map.get("resultCode");
                if (resultCode.equals("1")) {



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
                        productLimitTime = getString(R.string.product_list_apr_baobao_format);
                        product_limit_time.setText(Html.fromHtml(productLimitTime)+"天");

                    } else {  // 非钱贝勒产品
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
                    parentActivity= (Test) getActivity();
                    parentActivity.setButtomVisible(View.VISIBLE);
                    parentActivity.setCoudownButtomVisible(View.GONE);

                    if (map.get(BaseParam.QIAN_PRODUCT_ISNEWHAND).equals("1")) {  //如果是新手标的话
                        parentActivity.setButtomText("仅限新手投资");

                        // 额外奖励
                        mProductIsNewHand = true;
                    } else {
                        parentActivity.setButtomText("立即投资");
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

//                        knowProjectFragment=new KnowProjectFragment(borrowId,"putong",ProductContentAct4.this) ;  //项目详情/
//                        saveFragmentForProduct = new SaveFragmentForProduct(ProductContentAct4.this);/* 安全保障 */
//
//                        touziHistoryFragment=new TouziHistoryFragment(borrowId,ProductContentAct4.this,
//                                showStatus,fastestTender,sendFlag,lastTender,largestTenderUser,largestTenderSum
//                                ,firstRedPacket,highestRedPacket,lastRedPacket); //投资记录







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



                            SharedPreferences preferences = context.getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_USER, Context.MODE_PRIVATE);
                            oauthToken = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN, "");
                            realStatus = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_REALSTATUS, "");

                            if (oauthToken.equals("") || oauthToken == null) {
                                parentActivity.setButtomText("立即投资");
                            } else {
                                if (realStatus.equals("0") || realStatus == null) {
                                    parentActivity.setButtomText("立即投资");
                                }
                            }
                        } else {
                            product_content_mProgressBar.setVisibility(View.GONE);
                            product_total_money.setText("0");

                            tv_allmoney.setText("0");
                            parentActivity.setButtomText("已售罄");
                            parentActivity.setButtomBackground(R.color.buy_btn);
                            parentActivity.setButtomClickble(false);
                        }
                    } else if (productStatus.equals("2")) {//2:已售完 (在app端实际上不会有售罄这个标记)
                        product_total_money.setText("0");
                        tv_allmoney.setText("0");

                        product_content_mProgressBar.setVisibility(View.VISIBLE);
                        product_content_mProgressBar.setText(percent+"%");// 设置最大的值
                        parentActivity.setButtomText("已售罄");
                        parentActivity.setButtomBackground(R.color.buy_btn);
                        parentActivity.setButtomClickble(false);
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
                        parentActivity.setButtomText("已售罄");
                        parentActivity.setButtomBackground(R.color.buy_btn);
                        parentActivity.setButtomClickble(false);
                        if (map.get("lastTender") != null && map.get("lastTender").length() == 11) {
                        }
                    } else if (productStatus.equals("4")) {//4:已还款(测试版有这个环境,正式版没有)
                        parentActivity.setButtomText("已售罄");
                        parentActivity.setButtomClickble(false);

                        product_content_mProgressBar.setVisibility(View.GONE);
                        String rate = "";
                        if (style.equals("1")) {
                            rate = AppTool.denge(account, Double.parseDouble(apr), timeLimit);
                        } else {
                            rate = AppTool.rateReceipts(account, Float.parseFloat(apr), isday, timeLimitDay, timeLimit);
                        }
                        parentActivity.setButtomBackground(R.color.buy_btn);
                        if (map.get("lastTender") != null && map.get("lastTender").length() == 11) {
                        }
                    } else if (productStatus.equals("0")) {//0:预售
                        product_content_mProgressBar.setVisibility(View.VISIBLE);
                        product_content_mProgressBar.setText(percent + "%");
                        product_total_money.setText(sykt+"元");

                        countDownTime = map.get("countDownTime");
                        if (Integer.valueOf(countDownTime) > 0) {
                            parentActivity.setButtomVisible(View.GONE);
                            parentActivity.setCoudownButtomVisible(View.VISIBLE);
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
                            parentActivity.setButtomVisible(View.GONE);
                            parentActivity.setCoudownButtomVisible(View.GONE);
                            progressDialog = dia.getLoginDialog(getActivity(), "刷新中，请稍后..");
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
                            startActivity(new Intent(getActivity(), Login.class));
                        } else if (map.get("errorCode").equals("TOKEN_EXPIRED")) {
                            startActivity(new Intent(getActivity(), Login.class));
                        } else {
                            showToast(Check.checkReturn(map.get("errorCode")));
                        }
                    }
                }
                super.handleMessage(paramMessage);
            }
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
    private BroadcastReceiver myReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action=intent.getAction();
            switch (action){
                case "NewRealAct":
                    startDataRequest();
                    break;
                case "web":
                    getActivity().finish();
                    break;
            }

        }
    };


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
                    parentActivity.setCoudownButtomVisible(View.GONE);
                    parentActivity.setButtomVisible(View.VISIBLE);
                    startDataRequest();
                    if (mAuthoToken.equals("") || mAuthoToken == null) {
                        parentActivity.setButtomText("立即投资");
                    } else {
                        if (mRealStatus.equals("0") || mRealStatus == null) {
                            parentActivity.setButtomText("立即投资");
                        } else {
                            if (isNewHand == 1) {
                                parentActivity.setButtomText("仅限新手投资");
                                mProductIsNewHand = true;
                            } else {
                                parentActivity.setButtomText("立即投资");
                            }
                        }
                    }
                }
            }
        }
    };

    Toast mToast;
    TextView toast_tv;

    public void showToast(final String text) {
        if (!TextUtils.isEmpty(text)) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (mToast == null) {
                        mToast = new Toast(context);
                        View tview = LayoutInflater.from(context).inflate(R.layout.layout_toast, null);
                        mToast.setView(tview);
                        toast_tv = (TextView) tview.findViewById(R.id.toast_text);
                    } else {
                    }
                    toast_tv.setText(text);
                    mToast.show();
                }
            });
        }
    }
}
