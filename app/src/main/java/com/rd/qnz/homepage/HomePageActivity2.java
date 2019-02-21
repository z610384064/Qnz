package com.rd.qnz.homepage;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.rd.qnz.R;
import com.rd.qnz.WebBannerViewNeedAccesTokenActivity;
import com.rd.qnz.WebViewAct;
import com.rd.qnz.bean.HomepageButtomItemBean;
import com.rd.qnz.custom.APIModel;
import com.rd.qnz.custom.KeyPatternActivity;
import com.rd.qnz.custom.MineShow;
import com.rd.qnz.custom.MyApplication;
import com.rd.qnz.dialog.CustomProgressDialog;
import com.rd.qnz.dialog.GetDialog;
import com.rd.qnz.dialog.PopupAdDialog;
import com.rd.qnz.entity.FloatingWindowBean;
import com.rd.qnz.entity.HomepageBannerItemBean;
import com.rd.qnz.entity.HomepageBean;
import com.rd.qnz.entity.ProductItemBean;
import com.rd.qnz.gustruelock.LockActivity;
import com.rd.qnz.login.Login;
import com.rd.qnz.mine.NewRealAct;
import com.rd.qnz.mine.RedAndJiaXiH5;
import com.rd.qnz.product.ProductContentAct;
import com.rd.qnz.product.ProductContentPurchaseAct;
import com.rd.qnz.tools.AppTool;
import com.rd.qnz.tools.BaseParam;
import com.rd.qnz.tools.Check;
import com.rd.qnz.tools.Utils;
import com.rd.qnz.tools.webservice.JsonRequeatThreadClickActivity;
import com.rd.qnz.tools.webservice.JsonRequeatThreadHomePageGaiList;
import com.rd.qnz.util.MathUtil;
import com.rd.qnz.util.MyVolley;
import com.rd.qnz.view.ImageFileCache;
import com.rd.qnz.view.ImageGetFromHttp;
import com.rd.qnz.view.ImageMemoryCache;
import com.rd.qnz.webview.ShareWebViewActivity;
import com.rd.qnz.xutils.Installation;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

import static com.rd.qnz.custom.Profile.TAG_USER_STATUE_NAME;

/**
 * 1.6.0版本的首页(按设计图适配的)
 *
 * @author Evonne
 */

public class HomePageActivity2 extends KeyPatternActivity implements OnClickListener {

    private static final String TAG ="HomePageActivity2" ;
    private FloatingWindowBean floatingWindowBean;

    private List<View> mViewList = null;  //首页banner底部圆点的集合
    private ViewPager mViewPage = null;
    private MyPagerAdapter adapter = null;
    private LinearLayout mCustomSpace = null;
    private String mUserId= "";
    private GetDialog mGetDialog;
    private CustomProgressDialog mProgressDialog = null;
    private String mAauthToken;
    private MyApplication myApp;

    private MyHandler myHandler;
    private TextView mLimitTime, home_product_name;
    private HomepageBean mHomepageBean;
    private String mRealStatus;
    private PullToRefreshScrollView mScrollView;
    private boolean isfresh = false;


    private RelativeLayout click_to_reload;
    public  com.android.volley.toolbox.ImageLoader imageLoader;
    private int again = 0;
    private String VersionAndroid = "", androidUrl = "";//版本号,url;
    private String android_action = "", android_content = "";//是否需要强制,`信息;
    private String up_data;
    private String imei;


    private String newHandStatus; //是否是新手 1:是 0:否



    /**
     * 1.6.0
     * @param context
     */
    private RelativeLayout rl_message; //消息公告布局
    private ImageView more_messageremind_btn; //小红点

    private SharedPreferences sp;
    private String latestDate;
    private String oauthToken;


    private ArrayList<String> param = null;
    private ArrayList<String> value = null;
    APIModel apiModel = new APIModel();
    private Dialog update_dialog;
    /**
     * 底部浮动广告
     */
    ArrayList<HomepageButtomItemBean> homepageButtomItemBeen;
    private boolean isYaoQing=false;
    private LinearLayout ll_guanggao; //底部广告
    private RelativeLayout rl_data,rl_safe,rl_new,rl_yaoqing;//运营数据,安全保障,新手保障,邀请有礼
    private ImageView iv_buttom;

    public static void start(Context context) {
        Intent i = new Intent();
        i.setClass(context, HomePageActivity2.class);
        context.startActivity(i);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);



        imageLoader = MyVolley.getImageLoader();
        MobclickAgent.setDebugMode(true);
        imei = AppTool.imeiSave(HomePageActivity2.this);
        imei = imei + "-qian-" + Installation.id(HomePageActivity2.this);

        //获取sp_user.xml的数据
        sp = getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_USER, Context.MODE_PRIVATE);
        latestDate = sp.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_LATESTDATE_XIAOXI, "");

        //注册动态广播,接收注册界面发来的数据
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction("register");
        myIntentFilter.addAction("login");
        myIntentFilter.addAction("loginout");
        myIntentFilter.addAction("new");
        registerReceiver(myReceiver,myIntentFilter);

//
        init();   //判断一下用户是否登录,是否设置了手势锁,指纹锁,去解下锁
        initView(); //find控件,做一下新手专享,还有下拉刷新的代码

        getupdata();   //发一个请求到服务器,看需不需要更新app
        startDataRequest();     //调用首页的接口(判断lastdata跟本地文件保存的是不是一样)


    }






    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
    }

    /**
     * 跳转到注册界面的时候,登录界面没有被关闭(让用户在注册界面点击左上角能够退回来),在注册完成之后发送广播到登录界面,
     * 把登录界面给finish
     */
    private BroadcastReceiver myReceiver = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
            String action=intent.getAction();
            if (action.equals("register")){
            showLookRed();
        }else if (action.equals("login")){
            startDataRequest();

                sp = getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_USER, Context.MODE_PRIVATE);
                oauthToken=sp.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN, "");
                if (isYaoQing==true){
                    if (oauthToken.isEmpty()){
                        return;
                    }else {
                        Intent i=new Intent(HomePageActivity2.this, ShareWebViewActivity.class);
                        startActivity(i);
                    }
                }

        }else if (action.equals("loginout")){
            startDataRequest();
        }else if (action.equals("new")){
            startDataRequest();
        }

        }

    };


    private void initArray() {
        this.param = null;
        this.value = null;
        this.param = new ArrayList();
        this.value = new ArrayList();
        System.gc();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myReceiver != null) {
            try{
                unregisterReceiver(myReceiver);
            }catch (Exception e){
                e.printStackTrace();
            }

        }

    }

    /**
     * 每次打开应用从sp文件里面得到当前账号数据,   //如果不是第一次进入app了 并且已经登录并设置了手势密码,就进入解锁界面
     */
    private void init() {
        //从sp_user.xml文件里面获取数据
        SharedPreferences preferences = getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_USER, MODE_PRIVATE);
        mUserId = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_USERID, ""); //userId

            //得到手势密码
        String patternString = getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_LOCK, MODE_PRIVATE).getString(BaseParam.QIAN_SHAREDPREFERENCES_LOCKKEY, null);
        this.mGetDialog = new GetDialog();
        mAauthToken = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN, ""); //oauthToken 判断是否登录
        up_data = preferences.getString("up_data", "");
        mRealStatus = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_REALSTATUS, "");  //realStatus
        myApp = (MyApplication) getApplication();
        myApp.context = this;
        myApp.homepage = true;


        //这里只是改下图标,实际点击效果到时候再判断
        SharedPreferences sp = MyApplication.getInstance().getSharedPreferences(TAG_USER_STATUE_NAME, MODE_PRIVATE);
        newHandStatus = sp.getString(BaseParam.QIAN_USER_STATUS_INFO_NEWHAND_STATUS, "");  //默认0代表不是新手

  

        //如果手势密码不为空,用户也已经登录了,手势密码也是开启的状态,就去解个锁
            if (!TextUtils.isEmpty(patternString) && !TextUtils.isEmpty(mAauthToken) &&!myApp.isLock) {
            Intent intent = new Intent(this, LockActivity.class);
            startActivity(intent);
            myApp.homeCount = 1;
        }else if (!TextUtils.isEmpty(mAauthToken) ){ //用户已经登陆了,但是没有设置手势密码,或者手势密码没开启,那啥也不干
        }else {
            myApp.isLock = false;
//
        }
        if (myApp.isfirst&&TextUtils.isEmpty(mAauthToken)){ //是第一次打开应用,展示领取红包的弹窗
            showLQRed();
            myApp.isfirst=false;
        }
        myHandler = new MyHandler();
    }


    /**
     * 只有在首页第一次被初始化的时候调用一次
     */
    private void initView() {

        iv_buttom= (ImageView) findViewById(R.id.iv_buttom);
        rl_data= (RelativeLayout) findViewById(R.id.rl_data);
        rl_data.setOnClickListener(this);
        rl_safe= (RelativeLayout) findViewById(R.id.rl_safe);
        rl_safe.setOnClickListener(this);
        rl_new= (RelativeLayout) findViewById(R.id.rl_new); //新手福利
        rl_new.setOnClickListener(this);
        rl_yaoqing= (RelativeLayout) findViewById(R.id.rl_yaoqing);
        rl_yaoqing.setOnClickListener(this);
        ll_guanggao= (LinearLayout) findViewById(R.id.ll_guanggao);
        ll_guanggao.setOnClickListener(this);
        rl_message= (RelativeLayout) findViewById(R.id.rl_message);
        rl_message.setOnClickListener(this);
        more_messageremind_btn= (ImageView) findViewById(R.id.more_messageremind_btn);

        click_to_reload = (RelativeLayout) findViewById(R.id.click_to_reload); //当你当前网络状态不好的时候,它会显示出来,你点击这个布局重新加载数据
        click_to_reload.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startDataRequest();   //调用首页的接口
            }
        });

        mViewPage = (ViewPager) findViewById(R.id.homepage_viewpager);
        mCustomSpace = (LinearLayout) findViewById(R.id.homepage_banner_bottom_point);
        findViewById(R.id.homepage_first_product_progress_lay).setOnClickListener(this);
        mProgressDialog = new CustomProgressDialog(this);
        mLimitTime = (TextView) findViewById(R.id.homepage_first_product_time_limit); //homepage_first_product_time_limit
        home_product_name = (TextView) findViewById(R.id.homepage_first_product_name);


        mScrollView = (PullToRefreshScrollView) findViewById(R.id.scrollVeiw);
        mScrollView.setRefreshing(false);
        mScrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ScrollView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                // TODO Auto-generated method stub
                //刷新的最近时间
                refreshView.getLoadingLayoutProxy().setLoadingDrawable(getResources().getDrawable(R.drawable.default_ptr_rotate));
                refreshView.getLoadingLayoutProxy().setPullLabel("释放财运刷新");
                refreshView.getLoadingLayoutProxy().setRefreshingLabel("财运刷新中...");
                refreshView.getLoadingLayoutProxy().setReleaseLabel("释放财运刷新");
                startDataRequest();   //调用首页接口
                isfresh = true;
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
                mScrollView.onRefreshComplete();
            }
        });
        /**
         * 1.6.0
         */




    }

    private void initViewData(HomepageBean homepageBean) {

        ImageView xin_img = (ImageView) findViewById(R.id.product_list_xin_img); //新手图标
        TextView earningsRate = (TextView) findViewById(R.id.homepage_first_product_earnings_rate); //年化收益率
        Button homepageGotoBuyBtn = (Button) findViewById(R.id.homepage_goto_buy);  //立即投资
        homepageGotoBuyBtn.setOnClickListener(this);
        //判断产品是否为空,用户是否是新手`
        if (homepageBean.getProduct() != null && homepageBean.getProduct().getIsNewHand() != null) {
            if (TextUtils.equals(homepageBean.getProduct().getIsNewHand(), "1")) {  //是新手
                xin_img.setVisibility(View.VISIBLE);
                xin_img.setImageResource(R.drawable.new_hand);
            } else {
                xin_img.setVisibility(View.GONE);
            }
        }
        ProductItemBean productItemBean = homepageBean.getProduct();
        if (productItemBean != null && productItemBean.getAccount() != null && !productItemBean.getAccount().equals("")) {
            double all = Double.parseDouble(productItemBean.getAccount());//标总额
            double buyYes = Double.parseDouble(productItemBean.getAccountYes());//已投金额
            int progress = (int) (buyYes * 100 / all);

        }

        earningsRate.setText(MathUtil.formatRate(productItemBean.getApr()));

        String limitTimeStr = "";
        if (TextUtils.equals(productItemBean.getIsday(), ProductItemBean.IS_DAY_YES)) {
            limitTimeStr = productItemBean.getTimeLimitDay() + "天";
        } else {
            limitTimeStr =  productItemBean.getTimeLimit() + "个月";
        }
        if (!TextUtils.isEmpty(limitTimeStr)) {
            mLimitTime.setText(limitTimeStr);
        }

        home_product_name.setText(mHomepageBean.getProduct().getName());
        String productStatus = productItemBean.getProductStatus();
        TextView productBuyIntro = (TextView) findViewById(R.id.homepage_first_product_bottom_intro); //剩余可投金额

        /**
         * 首页按钮修改
         */
        if (TextUtils.equals(productStatus, ProductItemBean.PRODUCT_STATUS_PRESELL)) {// 预售
            xin_img.setVisibility(View.VISIBLE);
            xin_img.setImageResource(R.drawable.fore_show);
            String saleTimeDesStr = productItemBean.getPreSaleTimeDes().replace("_", " ");
//            homepageGotoBuyBtn.setText(R.string.show_detail);
        } else if (TextUtils.equals(productStatus, ProductItemBean.PRODUCT_STATUS_BUYING)) {// 热卖中
//            homepageGotoBuyBtn.setText(R.string.goto_buy);
        } else if (TextUtils.equals(productStatus, ProductItemBean.PRODUCT_STATUS_BUY_OVER)) {// 卖完了
//            homepageGotoBuyBtn.setText(R.string.show_detail);
        } else if (TextUtils.equals(productStatus, ProductItemBean.PRODUCT_STATUS_REPAYMENT)) {// 还款中
//            homepageGotoBuyBtn.setText(R.string.show_detail);
        } else if (TextUtils.equals(productStatus, ProductItemBean.PRODUCT_STATUS_OVER)) {// 已完结
//            homepageGotoBuyBtn.setText(R.string.show_detail);
        }
        productBuyIntro.setText(Integer.parseInt(productItemBean.getAccount())-Integer.parseInt(productItemBean.getAccountYes())+"元");
    }

    public static double round(Double v, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        BigDecimal b = null == v ? new BigDecimal("0.0") : new BigDecimal(Double.toString(v));
        BigDecimal one = new BigDecimal("1");
        return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 获得浮动广告
     */
    private void startClickActivityRequest() {
        ArrayList<String> param = new ArrayList<String>();
        ArrayList<String> value = new ArrayList<String>();
        if (Check.hasInternet(this)) {  /* 判断网络 */

            click_to_reload.setVisibility(View.GONE);
            param.add(BaseParam.URL_QIAN_API_APPID);
            value.add(myApp.appId);
            param.add(BaseParam.URL_QIAN_API_SERVICE);
            value.add("clickActivity");
            param.add(BaseParam.URL_QIAN_API_SIGNTYPE);
            value.add(myApp.signType);
            param.add(BaseParam.IDFA);
            value.add(BaseParam.DEVICE_ID);
            param.add("activity_id");
            value.add(floatingWindowBean.getId() + "");
            String[] array = new String[]{BaseParam.URL_QIAN_API_APPID + "=" + myApp.appId,
                    BaseParam.URL_QIAN_API_SERVICE + "=clickActivity",
                    BaseParam.URL_QIAN_API_SIGNTYPE + "=" + myApp.signType,
                    BaseParam.IDFA + "=" + BaseParam.DEVICE_ID,
                    "activity_id" + "=" + floatingWindowBean.getId()};
            String sign = apiModel.sortStringArray(array);/* 获取签名(参数为键值对拼接数组) */
            param.add(BaseParam.URL_QIAN_API_SIGN);
            value.add(sign);

            new Thread(new JsonRequeatThreadClickActivity(
                    HomePageActivity2.this, myApp,
                    HomePageActivity2.this.myHandler,
                    param,
                    value)
            ).start();


        } else {
            MineShow.toastShow("请检查网络连接是否正常", this);
            click_to_reload.setVisibility(View.VISIBLE);
        }
    }


    /**
     * 单纯的一个领取红包
     */
    public void showLQRed(){
        LayoutInflater inflaterDl = LayoutInflater.from(HomePageActivity2.this);
        RelativeLayout layout = (RelativeLayout) inflaterDl.inflate(R.layout.toast_redpag_zwc, null);
        final Dialog dialog = new AlertDialog.Builder(HomePageActivity2.this).create();
        dialog.show();
        dialog.getWindow().setContentView(layout);
        ImageView close = (ImageView) layout.findViewById(R.id.close);

        TextView red_raiders = (TextView) layout.findViewById(R.id.red_raiders); //红包攻略

        //点击领取,跳转到登录注册界面
        red_raiders.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(HomePageActivity2.this,Login.class);
                startActivity(i);
                dialog.dismiss();
            }
        });

        close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
    /**
     *  左边一个查看,右边一个联系客服
     */
    public void showLookRed(){
        //弹出红包界面
        LayoutInflater inflaterDl = LayoutInflater.from(HomePageActivity2.this);
        RelativeLayout layout = (RelativeLayout) inflaterDl.inflate(R.layout.toast_redpag_registed, null);
        final Dialog dialog = new AlertDialog.Builder(HomePageActivity2.this).create();
        dialog.show();
        dialog.getWindow().setContentView(layout);
        ImageView close = (ImageView) layout.findViewById(R.id.close);

        TextView red_raiders = (TextView) layout.findViewById(R.id.red_raiders); //红包查看
        TextView customer_service_hotline = (TextView) layout.findViewById(R.id.customer_service_hotline);  //客服热线

        //点击查看红包,跳转到红包界面
        red_raiders.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//
                if (MineShow.fastClick()) {
                    Intent redAndAward = new Intent(HomePageActivity2.this, RedAndJiaXiH5.class);
                    startActivity(redAndAward);
                    dialog.dismiss();
                }
            }
        });
        //点击客服热线,打电话应用
        customer_service_hotline.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                /**
                 * 在点击电话图标的时候
                 * Intent.ACTION_CALL此方法是直接询问且拨通电话
                 * Intent.ACTION_DIAL此方法是携带电话号码跳转到拨号页面
                 * */
                Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "400-000-9810"));//call
                callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(callIntent);
            }
        });
        close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }



    private void startFloatingWindow() {
        if (myApp.isFloatingWindow) {
            showPopupWindow();
            myApp.isFloatingWindow = false;
        }
    }

    /**
     * 联系客服
     */
    public  void call(){
            Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "400-000-9810"));//call
            callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(callIntent);
        }



    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.rl_safe:
                Intent intent=new Intent(HomePageActivity2.this,WebViewAct.class);
                intent.putExtra("web_url", BaseParam.URL_QIAN_HOME_SECURITY);
                intent.putExtra("title", "安全保障");
                startActivity(intent);
                break;
            case R.id.rl_new:
                intent=new Intent(HomePageActivity2.this,WebBannerViewNeedAccesTokenActivity.class);
                intent.putExtra("web_url", BaseParam.URL_QIAN_HOME_NEW);
                intent.putExtra("fileName", "新手福利");
                startActivity(intent);
            break;
            case R.id.rl_data: //运营数据
                 intent = new Intent(HomePageActivity2.this, WebViewAct.class);
                intent.putExtra("web_url", BaseParam.URL_QIAN_HOME_DATA);
                intent.putExtra("title", "运营数据");
                startActivity(intent);

                break;
            case R.id.rl_yaoqing: //邀请有礼
                if (MineShow.fastClick()) {
                    if (Check.hasInternet(this)){
                        sp = getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_USER, Context.MODE_PRIVATE);
                        oauthToken=sp.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN, "");
                        if (TextUtils.isEmpty(oauthToken)){
                            isYaoQing=true;
                            Intent i3=new Intent(HomePageActivity2.this, Login.class);
                            startActivity(i3);

                        }else {
                            intent=new Intent(HomePageActivity2.this, ShareWebViewActivity.class);
                            startActivity(intent);
                        }
                    }else {
                        MineShow.toastShow("请检查网络连接是否正常", this);
                    }

                }

                break;
            case R.id.ll_guanggao: //底部广告
                if (MineShow.fastClick()) {
                    Intent about = new Intent(HomePageActivity2.this, WebViewAct.class);
                    about.putExtra("web_url", BaseParam.URL_QIAN + "forward.html?returnUrl=aboutus");
                    about.putExtra("title", "关于钱内助");
                    startActivity(about);
                }
                break;
            case R.id.rl_message: //消息公告
                sp = getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_USER, Context.MODE_PRIVATE);
                Editor editor = sp.edit();
                editor.putString(BaseParam.QIAN_SHAREDPREFERENCES_USER_LATESTDATE_XIAOXI, myApp.latestDate);
                editor.commit();
                more_messageremind_btn.setBackgroundResource(R.drawable.home_message_default);

                Intent i=new Intent(HomePageActivity2.this, AnnouncementAct.class);
                startActivity(i);
                break;
            /* 立即投资 */
            case R.id.homepage_goto_buy:
                if (MineShow.fastClick()) {
                    if (Check.hasInternet(this)) {
                        click_to_reload.setVisibility(View.GONE);  //网络不好的时候显示的布局
                        Button btn = (Button) v;
                        if (mHomepageBean == null) {
                            return;
                        }
                        boolean isNewHandProduct = TextUtils.equals(mHomepageBean.getProduct().getIsNewHand(), "1");// 是否是新手标
                        boolean isUserIsNewHand = TextUtils.equals(mHomepageBean.getLoginstatus(), "0");// 0:是否是未登录或者是新手 1:已登录
                        //是新手标,然后用户处于未登录或者新手状态
                        if (TextUtils.equals(btn.getText().toString(), getString(R.string.show_detail)) || (isNewHandProduct && isUserIsNewHand)) {
                            String borrowId = "";
                            if (mHomepageBean != null && mHomepageBean.getProduct() != null) {
                                borrowId = mHomepageBean.getProduct().getBorrowId();
                                 intent = new Intent(HomePageActivity2.this, ProductContentAct.class);
                                intent.putExtra(BaseParam.QIAN_PRODUCT_BORROWID, borrowId);
                                startActivity(intent);
                            }
                        } else if (isNewHandProduct) {  // 产品是新手标
                            if (isUserIsNewHand) {// 未登录或者是新手
                                gotoBuy(mHomepageBean.getProduct());
                            } else {// 已登录且不是新手了
                                String borrowId = "";
                                if (mHomepageBean != null && mHomepageBean.getProduct() != null
                                        && !TextUtils.isEmpty(mHomepageBean.getProduct().getBorrowId())) {
                                    borrowId = mHomepageBean.getProduct().getBorrowId();
                                     intent = new Intent(HomePageActivity2.this, ProductContentAct.class);
                                    intent.putExtra(BaseParam.QIAN_PRODUCT_BORROWID, borrowId);
                                    startActivity(intent);
                                }
                            }
                        } else {
                            gotoBuy(mHomepageBean.getProduct());
                        }
                    } else {
                        MineShow.toastShow("请检查网络连接是否正常", this);
                        click_to_reload.setVisibility(View.VISIBLE);
                    }
                }
                break;
            case R.id.homepage_first_product_progress_lay://点击整个标,进入标详情
                if (MineShow.fastClick()) {
                    if (Check.hasInternet(this)) {
                        click_to_reload.setVisibility(View.GONE);
                        if (mHomepageBean != null && mHomepageBean.getProduct() != null
                                && !TextUtils.isEmpty(mHomepageBean.getProduct().getBorrowId())) {
                             intent = new Intent(HomePageActivity2.this, ProductContentAct.class);
                            intent.putExtra(BaseParam.QIAN_PRODUCT_BORROWID, mHomepageBean.getProduct().getBorrowId());
                            startActivity(intent);
                        }
                    } else {
                        MineShow.toastShow("请检查网络连接是否正常", this);
                        click_to_reload.setVisibility(View.VISIBLE);
                    }
                }
                break;



            default:
                break;
        }
    }

    private void gotoBuy(final ProductItemBean product) {
        SharedPreferences preferences = getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_USER, MODE_PRIVATE);
        mAauthToken = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN, "");
        if (mAauthToken.equals("") || mAauthToken == null) {//如果mAauthToken为空或者为null说明没有登录
            startActivity(new Intent(HomePageActivity2.this, Login.class));
            return;
        } else {
            if (mRealStatus.equals("0") || mRealStatus == null) {//如果mRealStatus为0或者为null说明没有实名认证
                startActivity(new Intent(HomePageActivity2.this, NewRealAct.class));
                return;
            }
        }
        double mAccount = Double.parseDouble(product.getAccount());//标总额
        double mAccountYes = Double.parseDouble(product.getAccountYes());//已投金额
        final double mBalance = mAccount - mAccountYes;//余额
        if (mBalance <= 0) {
            return;
        }

        JSONObject param = new JSONObject();
        MyApplication myApp = MyApplication.getInstance();
        String sign = null;
        try {
            param.put(BaseParam.QIAN_PRODUCT_BORROWID, product.getBorrowId());
            param.put(BaseParam.URL_QIAN_API_APPID, myApp.appId);
            param.put(BaseParam.URL_QIAN_API_SERVICE, "productDetail");
            param.put(BaseParam.URL_QIAN_API_SIGNTYPE, myApp.signType);

            String[] array = new String[]{BaseParam.QIAN_PRODUCT_BORROWID + "=" + product.getBorrowId(),
                    BaseParam.URL_QIAN_API_APPID + "=" + myApp.appId,
                    BaseParam.URL_QIAN_API_SERVICE + "=productDetail",
                    BaseParam.URL_QIAN_API_SIGNTYPE + "=" + myApp.signType};

            sign = apiModel.sortStringArray(array);
            param.put(BaseParam.URL_QIAN_API_SIGN, sign);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = BaseParam.URL_QIAN_GET_PRODUCT_CONTENT
                + "?" + BaseParam.QIAN_PRODUCT_BORROWID + "=" + product.getBorrowId()
                + "&" + "appId=" + myApp.appId
                + "&" + "service=" + "productDetail"
                + "&" + "signType=" + myApp.signType
                + "&" + "sign=" + sign;

        OkGo.post(url)
                .tag(this)
                .upJson(param.toString())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        Log.i("产品详情获取天数", "-------");
//                        Logger.json(s.toString());

                        try {
                            JSONObject object = new JSONObject(s);
                            int errorcode = object.getInt("resultCode");
                            if (errorcode == 1) {
                                JSONObject oj1 = new JSONObject(Check.jsonGetStringAnalysis(object, "resultData"));
                                if (Check.jsonGetStringAnalysis(oj1, "interestDay") != null) {

                                    ProductContentPurchaseAct.start(
                                            HomePageActivity2.this,
                                            product.getBorrowId(),
                                            product.getStatus(),
                                            product.getName(),
                                            product.getType(),
                                            product.getFlowCount(),
                                            product.getFlowMoney(),
                                            mBalance,
                                            product.getIsday(),
                                            product.getTimeLimitDay(),
                                            product.getTimeLimit(),
                                            product.getLowestAccount(),
                                            product.getLastRepayTime(),
                                            product.getStyle(),
                                            product.getNormalApr(),
                                            product.getRateapr(),
                                            product.getExtraAwardApr(),
                                            Check.jsonGetStringAnalysis(oj1, "interestDay"),
                                            product.getBrType()
                                    );
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } finally {

                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                    }
                });

    }

    /**
     * 调用首页的接口
     */
    private void startDataRequest() {
        if (Check.hasInternet(this)) {
            click_to_reload.setVisibility(View.GONE);
            ArrayList<String> param = new ArrayList<String>();
            ArrayList<String> value = new ArrayList<String>();
            param.add(BaseParam.URL_QIAN_API_APPID);
            value.add(myApp.appId);
            param.add(BaseParam.URL_QIAN_API_SERVICE);
            value.add("home1xV4");
            param.add(BaseParam.URL_QIAN_API_SIGNTYPE);
            value.add(myApp.signType);
            param.add(BaseParam.IDFA);
            value.add(BaseParam.DEVICE_ID);
            String[] array;
            SharedPreferences preferences = getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_USER, MODE_PRIVATE);
            String oauthToken = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN, "");
                if (!TextUtils.isEmpty(oauthToken)) {
                param.add(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN);
                value.add(oauthToken);
                array = new String[]{BaseParam.URL_QIAN_API_APPID + "=" + myApp.appId,
                        BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN + "=" + oauthToken,
                        BaseParam.URL_QIAN_API_SERVICE + "=home1xV4",
                        BaseParam.URL_QIAN_API_SIGNTYPE + "=" + myApp.signType,
                        BaseParam.IDFA + "=" + BaseParam.DEVICE_ID};
            } else {
                array = new String[]{BaseParam.URL_QIAN_API_APPID + "=" + myApp.appId,
                        BaseParam.URL_QIAN_API_SERVICE + "=home1xV4",
                        BaseParam.URL_QIAN_API_SIGNTYPE + "=" + myApp.signType,
                        BaseParam.IDFA + "=" + BaseParam.DEVICE_ID};
            }

            String sign = apiModel.sortStringArray(array);
            param.add(BaseParam.URL_QIAN_API_SIGN);
            value.add(sign);

            mProgressDialog = HomePageActivity2.this.mGetDialog.getLoginDialog(HomePageActivity2.this, "正在获取数据..");
            mProgressDialog.setCancelable(true);
            mProgressDialog.show();

            new Thread(new JsonRequeatThreadHomePageGaiList(
                    this,
                    myApp,
                    this.myHandler,
                    param,
                    value)
            ).start();


        } else {
            MineShow.toastShow("请检查网络连接是否正常", this);
            click_to_reload.setVisibility(View.VISIBLE);
        }
    }



    private final class MyPagerAdapter extends PagerAdapter {
        List<ImageView> imageViewList = new ArrayList<ImageView>();  //banner图片的集合
        List<HomepageBannerItemBean> bannerList = new ArrayList<HomepageBannerItemBean>();
        int width;

        public MyPagerAdapter(Context context) {
            width = getWindowManager().getDefaultDisplay().getWidth();  //得到屏幕宽度
        }

        public void setData(List<HomepageBannerItemBean> bannerList, List<ImageView> imageViewList) {
            this.bannerList = bannerList;
            this.imageViewList = imageViewList;
            notifyDataSetChanged();
        }

        public String getLocationUrl(int position) {
            return bannerList.get(position).getLocationUrl();
        }

        public String getBannerUrl(int position) {
            return bannerList.get(position).getUrl();
        }

        public String getBannerFileName(int position) {
            return bannerList.get(position).getFileName();
        }
        public String getBannerIntro(int position) {
            return bannerList.get(position).getIntro();
        }
        public String getBannerDuce(int position) {
            return bannerList.get(position).getDuce();
        }

        @Override   //页面总个数
        public int getCount() {
            // TODO Auto-generated method stub
            return imageViewList.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            // TODO Auto-generated method stub
            ( container).removeView(imageViewList.get(position));
        }

        public void notifyDataSetChanged() {
            // TODO Auto-generated method stub
            super.notifyDataSetChanged();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            if (position >= bannerList.size()) {
                return null;
            }
            ImageView temp = imageViewList.get(position);

            temp.setScaleType(ImageView.ScaleType.FIT_XY);
            RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            temp.setLayoutParams(param);
            ImageLoader.getInstance().displayImage( bannerList.get(position).getFullShowUrl(),temp);
//            BitmapUtils.getInstence().display(temp, bannerList.get(position).getFullShowUrl()); //使用xutils 进行图片展示
            (container).addView(temp);
            return temp;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            // TODO Auto-generated method stub
            return arg0 == arg1;
        }


    }



    private class MyHandler extends Handler {
        public void handleMessage(Message paramMessage) {
            Bundle localBundle1 = paramMessage.getData();
            String home_list = localBundle1.getString(BaseParam.URL_REQUEAT_MY_HOMEPAGE);
            String start_page = localBundle1.getString(BaseParam.QIAN_REQUEAT_GETSTARTPAGE);//启动页图片
            if (null != mProgressDialog && mProgressDialog.isShowing()) {// 隐藏加载框
                HomePageActivity2.this.mProgressDialog.dismiss();
            }
            if (null != home_list) {
                //这里只是改下图标,实际点击效果到时候再判断
                SharedPreferences sp = MyApplication.getInstance().getSharedPreferences(TAG_USER_STATUE_NAME, MODE_PRIVATE);
                newHandStatus = sp.getString(BaseParam.QIAN_USER_STATUS_INFO_NEWHAND_STATUS, "");  //默认0代表不是新手
                SharedPreferences sp1 = MyApplication.getInstance().getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_USER, MODE_PRIVATE);
                String oa=sp1.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN, "");
                        if (TextUtils.isEmpty(oa)){ //如果还未登录
                        }else {
                            if (newHandStatus.equals("0")){  //已经登录了,并且是非新手,显示自动投标的图片
                            }else {  //登录了是新手,显示新手图标
                            }

                        }

                initWidget(); //初始化viewpager
                JsonList(home_list);
            }
            if (null != start_page) {
                JsonStartPage(start_page);
            }
            mScrollView.onRefreshComplete();
            super.handleMessage(paramMessage);
        }
    }

    /**
     * 弹出太子快跑,女神福利这些弹窗
     */
    @SuppressLint("NewApi")
    private void showPopupWindow() {
        final String floating_url = floatingWindowBean.getLocationUrl();
        final String intro=floatingWindowBean.getIntro();
        final String duce=floatingWindowBean.getDuce();

        final PopupAdDialog updateWebDialog = new PopupAdDialog(HomePageActivity2.this, imageLoader, floatingWindowBean.getFullShowUrl());
        updateWebDialog.setOnImgClickListener(new PopupAdDialog.OnImgClickListener() {
            @Override
            public void onImgClick() {
                startClickActivityRequest();
                Intent intent=new Intent(HomePageActivity2.this,WebBannerViewNeedAccesTokenActivity.class);
                intent.putExtra("web_url", floating_url);
                intent.putExtra("type", "");
                intent.putExtra("title", "");
                intent.putExtra("message", "");
                intent.putExtra("imageUrl", "");
                if (!TextUtils.isEmpty(intro)&&!TextUtils.isEmpty(duce)){
                    intent.putExtra("intro", intro);
                    intent.putExtra("duce", duce);
                }else if (!TextUtils.isEmpty(intro)){
                    intent.putExtra("intro", intro);
                }else {
                    intent.putExtra("duce", duce);
                }
                startActivity(intent);
                updateWebDialog.dismiss();
            }
        });
        updateWebDialog.show();
    }
    /**
     * 初始化viewpager
     */
    private void initWidget() {

        mViewList = new ArrayList<View>();

        adapter = new MyPagerAdapter(this);
        mViewPage.setAdapter(adapter);
        // 设置一个监听器，当ViewPager中的页面改变时调用
        mViewPage.setOnPageChangeListener(new MyPageChangeListener());
    }

    /* 当ViewPager中页面的状态发生改变时调用 */
    private class MyPageChangeListener implements ViewPager.OnPageChangeListener {

        private int historyPosition = 0;
        //当某个界面被选中的时候调用
        public void onPageSelected(int position) {
            mViewList.get(historyPosition).setBackgroundResource(R.drawable.feature_point);
            mViewList.get(position).setBackgroundResource(R.drawable.feature_point_cur); //当前被选中的变亮
            historyPosition = position;
        }

        public void onPageScrollStateChanged(int arg0) {
        }

        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }
    }

    /**
     *   主要来说就是从后台查询有没有启动页,如果没有的话就把本地sp文件里面的startPageUrl字段清空
     *   如果服务端有返回值得话,就把返回的值跟本地的值作对比,如果两个值不一样就删除以前的sp字段,并把图片删了,存入现在的字段和图片
     * @param result
     */
    private void JsonStartPage(String result) {
        if (result.equals("unusual")) {
            return;
        }
        try {
            JSONObject oj = new JSONObject(result);
            if (Check.jsonGetStringAnalysis(oj, "resultCode").equals("1")) {
                JSONObject oj1 = new JSONObject(Check.jsonGetStringAnalysis(oj, "resultData"));
                SharedPreferences sp = this.getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_USER, MODE_PRIVATE); //sp_user
                // 存入数据
                Editor editor = sp.edit();
              //  如果从网络上返回的startPageUrl等于空,那么就把sp文件里面的startPageUrl清空
                if (Check.jsonGetStringAnalysis(oj1, BaseParam.QIAN_SHAREDPREFERENCES_USER_GETSTARTPAGE) == null
                        || "".equals(Check.jsonGetStringAnalysis(oj1, BaseParam.QIAN_SHAREDPREFERENCES_USER_GETSTARTPAGE))) {
                    //startPageUrl  存到sp里面
                    editor.putString(BaseParam.QIAN_SHAREDPREFERENCES_USER_GETSTARTPAGE, "");
                } else {
                    String newUrl = BaseParam.URL_QIAN + "/"
                            + Check.jsonGetStringAnalysis(oj1, BaseParam.QIAN_SHAREDPREFERENCES_USER_GETSTARTPAGE);
                    String oldUrl = sp.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_GETSTARTPAGE, "");
                    if ((!TextUtils.isEmpty(newUrl)) && !TextUtils.equals(oldUrl, newUrl)) {
                        editor.putString(BaseParam.QIAN_SHAREDPREFERENCES_USER_GETSTARTPAGE, newUrl);
                        delBitmap(oldUrl);// 删除老照片
                        if (getBitmapFromCache(newUrl) == null) {// 缓存没有就下载
                            new DownLoadStartPage().execute(newUrl);
                        }
                    }
                }
                editor.commit();
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void delBitmap(final String url) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ImageFileCache.getInstance().delBitmapFromCache(url);
            }
        }).start();
    }

    private void saveBitmap(final Bitmap bitmap, final String url) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ImageFileCache.getInstance().saveBitmap(bitmap, url);
            }
        }).start();
    }

    public Bitmap getBitmapFromCache(String url) {
        // 从内存缓存中获取图片
        Bitmap result = ImageMemoryCache.getInstance().getBitmapFromCache(url);
        if (result == null) {
            // 文件缓存中获取
            result = ImageFileCache.getInstance().getImage(url);
            if (result == null) {
                return null;
            }
        }
        return result;
    }

    private void JsonList(String result) {
        if (result.equals("unusual")) {
            MineShow.toastShow("连接服务器异常", HomePageActivity2.this);
            return;
        }
        try {
            JSONObject oj = new JSONObject(result);
            if (Check.jsonGetStringAnalysis(oj, "resultCode").equals("1")) {
                JSONObject oj1 = new JSONObject(Check.jsonGetStringAnalysis(oj, "resultData"));
                Gson gson = new Gson();
                HomepageBean homepageBean = gson.fromJson(oj1.toString(), HomepageBean.class);
                // 消息公告       sp_user.xml
                SharedPreferences preferences = getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_USER, MODE_PRIVATE);
                String latestDate = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_LATESTDATE_XIAOXI, "");
                myApp.latestDate = homepageBean.getLatestDate();

                String appUpdate = Check.jsonGetStringAnalysis(oj1, "appUpdate");//没用到这个数据

                String activityTopShow = Check.jsonGetStringAnalysis(oj1, "activityTopShow");//没用到这个数据
                if (activityTopShow != null) {   //弹出一个广告(太子快跑)
                    JSONObject oj2 = new JSONObject(activityTopShow);
                    Gson gson2 = new Gson();
                    floatingWindowBean = gson2.fromJson(oj2.toString(), FloatingWindowBean.class);
                    startFloatingWindow();
                }
                //如果服务器返回的latestDate和本地的latestDate不一样,显示小红点
                if (!TextUtils.equals(latestDate, myApp.latestDate)) {
                    more_messageremind_btn.setBackgroundResource(R.drawable.home_message_select);
                }
                initTopViewPage(homepageBean.getBannerList());
                mHomepageBean = homepageBean;
                initViewData(mHomepageBean);

                JSONArray bannerList = oj1.getJSONArray("bannerList");
                if (isfresh) {  //如果已经刷新成功了,这个数据还是为0,代表并没有数据
                    if (bannerList.length() == 0) {
                        MineShow.toastShow("没有数据可获取", HomePageActivity2.this);
                        return;
                    }
                }
                homepageButtomItemBeen= homepageBean.getBottomGG();
                if (homepageButtomItemBeen!=null&&homepageButtomItemBeen.size()>0){
                    iv_buttom.setVisibility(View.VISIBLE);
                    ImageLoader.getInstance().displayImage(homepageButtomItemBeen.get(0).getUrl(), iv_buttom, new ImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String s, View view) {

                        }

                        @Override
                        public void onLoadingFailed(String s, View view, FailReason failReason) {
                            iv_buttom.setVisibility(View.GONE);
                        }

                        @Override
                        public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                            iv_buttom.setVisibility(View.VISIBLE);
                            iv_buttom.setImageBitmap(bitmap);
                        }

                        @Override
                        public void onLoadingCancelled(String s, View view) {

                        }
                    });
//                    BitmapUtils.getInstence().display(iv_buttom,homepageButtomItemBeen.get(0).getUrl());

                }else {
                    iv_buttom.setVisibility(View.GONE);
                }


            } else {
                MineShow.toastShow("连接服务器异常", HomePageActivity2.this);
                return;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            MineShow.toastShow(BaseParam.ERRORCODE_CHECKFWQ, HomePageActivity2.this);
            return;
        }
    }

    /**
     * 初始化ViewPager
     * @param bannerList
     */
    private void initTopViewPage(List<HomepageBannerItemBean> bannerList) {
        int width;
        List<ImageView> imageViewList = new ArrayList<ImageView>();
        mViewList.clear();
        mCustomSpace.removeAllViews(); //viewpager底下的线性布局
        width = getWindowManager().getDefaultDisplay().getWidth();
        if (bannerList == null) {
            return;
        }
        //GrowingIO埋点,统计banner
        ArrayList<String> bannerDescriptions=new ArrayList<>();

        for (int i = 0; i < bannerList.size(); i++) { //写一个放imageView的集合,一开始放一些空的imageview对象
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(new LayoutParams(width, width / 4));
            imageView.setOnClickListener(new ClickListener(i));    //给每个图片设置了点击事件
            imageViewList.add(imageView);
            View view = new View(this);
            LayoutParams layoutParams = new LayoutParams(28, 28);
            view.setLayoutParams(layoutParams);
            view.setBackgroundResource(R.drawable.feature_point);
            mCustomSpace.addView(view);
            mViewList.add(view);
            mViewList.get(0).setBackgroundResource(R.drawable.feature_point_cur);
            bannerDescriptions.add(bannerList.get(i).getIntro());
        }
//        GrowingIO.getInstance().trackBanner(mViewPage, bannerDescriptions);
        Log.i("bannerList.size()", bannerList.size() + "");
        adapter.setData(bannerList, imageViewList);
        mBannerThreadState = false;
        startScrollBanner();  //让viewPager每隔五秒换一次
    }

    private final class DownLoadStartPage extends AsyncTask<String, Void, Bitmap> {
        private String imageUrl;

        @Override
        protected Bitmap doInBackground(String... params) {
            // TODO Auto-generated method stub
            try {
                imageUrl = params[0];
                Bitmap bitmap = getBitmap(params[0]);
                return bitmap;
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            saveBitmap(result, imageUrl);// 保存下载的图片
        }
    }

    public Bitmap getBitmap(String url) {
        // 从内存缓存中获取图片
        ImageMemoryCache memoryCache = ImageMemoryCache.getInstance();
        Bitmap result = memoryCache.getBitmapFromCache(url);
        ImageFileCache fileCache = ImageFileCache.getInstance();
        if (result == null) {
            // 文件缓存中获取
            result = fileCache.getImage(url);
            if (result == null) {
                // 从网络获取
                result = ImageGetFromHttp.downloadBitmap(url);
                if (result != null) {
                    fileCache.saveBitmap(result, url);
                    memoryCache.addBitmapToCache(url, result);
                }
            } else {
                // 添加到内存缓存
                memoryCache.addBitmapToCache(url, result);
            }
        }
        return result;
    }

    /**
     * viewPager里面图片对应的点击事件
     */
    private final class ClickListener implements OnClickListener {
        private int mPosition;

        public ClickListener(int mPosition) {
            this.mPosition = mPosition;
        }

        @Override
        public void onClick(View v) {
            if (v instanceof ImageView) {
                if (MineShow.fastClick()) {

                    String intro=adapter.getBannerIntro(mPosition);
                    String duce=adapter.getBannerDuce(mPosition);
                    if (TextUtils.isEmpty(intro)&&TextUtils.isEmpty(duce)){ //标题和内容都为空
                        Intent intent=new Intent(HomePageActivity2.this,WebBannerViewNeedAccesTokenActivity.class);
                        intent.putExtra("web_url", BaseParam.URL_QIAN + adapter.getLocationUrl(mPosition));
                        intent.putExtra("type", "3");
                        intent.putExtra("fileName", adapter.getBannerFileName(mPosition));
                        intent.putExtra("message", BaseParam.QIAN_BANNER_INTRO);
                        intent.putExtra("imageUrl", Utils.getAbsoluteUrlPath(adapter.getBannerUrl(mPosition)));
                        startActivity(intent);
                    }else  if (TextUtils.isEmpty(intro)){  //标题为空
                        Intent intent=new Intent(HomePageActivity2.this,WebBannerViewNeedAccesTokenActivity.class);
                        intent.putExtra("web_url", BaseParam.URL_QIAN + adapter.getLocationUrl(mPosition));
                        intent.putExtra("type", "3");
                        intent.putExtra("fileName", adapter.getBannerFileName(mPosition));
                        intent.putExtra("message",  BaseParam.QIAN_BANNER_INTRO);
                        intent.putExtra("imageUrl",  Utils.getAbsoluteUrlPath(adapter.getBannerUrl(mPosition)));
                        intent.putExtra("duce",duce);
                        startActivity(intent);
                    }else if (TextUtils.isEmpty(duce)){ //内容为空
                        Intent intent=new Intent(HomePageActivity2.this,WebBannerViewNeedAccesTokenActivity.class);
                        intent.putExtra("web_url", BaseParam.URL_QIAN + adapter.getLocationUrl(mPosition));
                        intent.putExtra("type", "3");
                        intent.putExtra("fileName", adapter.getBannerFileName(mPosition));
                        intent.putExtra("message",  BaseParam.QIAN_BANNER_INTRO);
                        intent.putExtra("imageUrl",  Utils.getAbsoluteUrlPath(adapter.getBannerUrl(mPosition)));
                        intent.putExtra("intro",intro);
                        startActivity(intent);
                    }else { //两者都不为空
                        Intent intent=new Intent(HomePageActivity2.this,WebBannerViewNeedAccesTokenActivity.class);
                        intent.putExtra("web_url", BaseParam.URL_QIAN + adapter.getLocationUrl(mPosition));
                        intent.putExtra("type", "3");
                        intent.putExtra("fileName", adapter.getBannerFileName(mPosition));//服务端返回来的数据都是"",后面的界面也没有接收
                        intent.putExtra("message",  BaseParam.QIAN_BANNER_INTRO);  //没接收
                        intent.putExtra("imageUrl",  Utils.getAbsoluteUrlPath(adapter.getBannerUrl(mPosition)));
                        intent.putExtra("intro",intro);  //标题
                        intent.putExtra("duce",duce);    //分享出去的文本介绍
                        startActivity(intent);
                    }

                }
            } else {
                return;
            }
        }
    }

    private boolean mBannerThreadState = false;
    private boolean mThreadIsDo = true;
    private Thread mBannerThread;
    private Thread mMoveThread;

    /**
     * 让顶部的banner 定时滑动
     */
    private void startScrollBanner() {
        if (mBannerThread == null) {
            mBannerThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (mThreadIsDo) {
                        try {
                            mBannerThreadState = true;
                            Message msg = new Message();
                            msg.what = 1000;
                            bannerHandler.sendMessage(msg);
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    mBannerThreadState = false;
                }
            });

        }
        if ((!mBannerThread.isAlive()) && !mBannerThreadState) {
            mBannerThread.start();
        }
    }



    private boolean mThreadIsFrist = true;
    private Handler bannerHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1000) {
                if (mThreadIsFrist) {
                    mViewPage.setCurrentItem(0, true);
                    mThreadIsFrist = false;
                } else {
                    int position = mViewPage.getCurrentItem();
                    int nextPosition = position + 1;
                    if (nextPosition >= adapter.getCount()) {
                        nextPosition = 0;
                    }
                    mViewPage.setCurrentItem(nextPosition, true);
                }
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences preferences = getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_USER, MODE_PRIVATE);
        mAauthToken = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN, "");
        isfresh = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        isYaoQing=false;
        SharedPreferences preferences = getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_USER, MODE_PRIVATE);
        mAauthToken = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN, "");
        if (again == 1) {
            if (null == update_dialog || !update_dialog.isShowing()) {// 隐藏加载框
                update_force(androidUrl, VersionAndroid, android_content, android_action);
            }
        }
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

    /* 可选择更新 */
    private void update(String url, String name, String str, String type) {
        again = 0;
        showUpdateDialog(url, name, HomePageActivity2.this, str, type);
    }


private String filename="";
    private String download_url=""; //文件下载url
        /* 可更新的弹窗 */
        private static final String FILE_PATH =
                Environment.getExternalStorageDirectory() + File.separator + "QianNeiZhu"
                        + File.separator + "update";
    public void showUpdateDialog(final String url, final String filename, final Context context, String str, String type) {


        LayoutInflater inflaterDl = LayoutInflater.from(context);
        RelativeLayout layout = (RelativeLayout) inflaterDl.inflate(R.layout.dialog_update_layout, null);
        final Dialog update_dialog = new AlertDialog.Builder(context).create();

        update_dialog.show();
        update_dialog.getWindow().setContentView(layout);



        TextView message = (TextView) layout.findViewById(R.id.message);
        message.setText(str);
        ImageView close = (ImageView) layout.findViewById(R.id.close);
        if (type.equals("1")) {
            close.setVisibility(View.GONE);
            update_dialog.setCanceledOnTouchOutside(false);
            update_dialog.setCancelable(false);
        } else {
            close.setVisibility(View.VISIBLE);
            close.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {   //点击了更新底下的关闭图标,把数据存入up_data字段
                    update_dialog.dismiss();
                    SharedPreferences sp = context.getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_USER, MODE_PRIVATE);
                    Editor editor = sp.edit();
                    editor.putString("up_data", filename);
                    editor.commit();
                }
            });
        }

        Button update = (Button) layout.findViewById(R.id.update);
        update.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                // TODO: 2017/4/1 0001  判断是否有存储权限
                      update_dialog.dismiss();

                    if (Build.VERSION.SDK_INT >= 23) { //设备号是6.0及以上 动态判断权限
//                           requestPermission();
                        int permissionCheck= ContextCompat.checkSelfPermission(HomePageActivity2.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                        if (permissionCheck!= PackageManager.PERMISSION_GRANTED){ //如果这个权限还没有被授予,那么就去请求这个权限

                                //没拿到权限的话直接弹个框让用户给权限,否则gg
                            LayoutInflater inflaterDl = LayoutInflater.from(HomePageActivity2.this);
                            LinearLayout layout = (LinearLayout) inflaterDl.inflate(R.layout.dialog_updateperssion_layout, null);
                            final Dialog dialog = new AlertDialog.Builder(HomePageActivity2.this).create();
                            dialog.show();
                            dialog.getWindow().setContentView(layout);
                            dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失


                            Window dialogWindow = dialog.getWindow();
                            WindowManager m = dialogWindow.getWindowManager();
                            Display d = m.getDefaultDisplay(); // 获取屏幕宽、高度
                            WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值

                            p.width = (int) (d.getWidth() * 0.7); // 宽度设置为屏幕的0.65，根据实际情况调整
                            p.height = (int) (0.8*p.width); // 高度设置为屏幕的0.6，根据实际情况调整
                            dialogWindow.setAttributes(p);


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
                                    Intent localIntent = new Intent();

                                    localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                                    localIntent.setData(Uri.fromParts("package", getPackageName(), null));
                                    startActivityForResult(localIntent,1);

                                }
                            });

                        }else {
                            File downloadPath = new File(FILE_PATH);
                            if (downloadPath.exists()) {
                                //删除路径下的文件
                                File[] childFiles = downloadPath.listFiles();
                                for (int i = 0; i < childFiles.length; i++) {
                                    childFiles[i].delete();
                                }
                            }
                            UpdateAppManager updateManager = new UpdateAppManager(HomePageActivity2.this);
                            updateManager.showDownloadDialog(filename,download_url);
                        }

                    }else { //设备低于6.0 直接更新
                        //弹框去更新
                        File downloadPath = new File(FILE_PATH);
                        if (downloadPath.exists()) {
                            //删除路径下的文件
                            File[] childFiles = downloadPath.listFiles();
                            for (int i = 0; i < childFiles.length; i++) {
                                childFiles[i].delete();
                            }
                        }
                        UpdateAppManager updateManager = new UpdateAppManager(HomePageActivity2.this);
                        updateManager.showDownloadDialog(filename,download_url);


                    }

            }
        });
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            int permissionCheck = ContextCompat.checkSelfPermission(HomePageActivity2.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) { //如果这个权限还没有被授予,那么不做处理

            }else {
                File downloadPath = new File(FILE_PATH);
                if (downloadPath.exists()) {
                    //删除路径下的文件
                    File[] childFiles = downloadPath.listFiles();
                    for (int i = 0; i < childFiles.length; i++) {
                        childFiles[i].delete();
                    }
                }
                UpdateAppManager updateManager = new UpdateAppManager(HomePageActivity2.this);
                updateManager.showDownloadDialog(filename,download_url);
            }
        }
    }
    /**
     * 显示下载对话框
     */





    /* 强制更新 */
    private void update_force(String url, String name, String str, String type) {
        again = 1;
        UpdateAppManager updateManager = new UpdateAppManager(this);
        updateManager.showUpdateDialog(url, name, HomePageActivity2.this, str, type);
    }

    private String getVersionCode() {
        String version = "";
        try {
            // 获取packagemanager的实例
            PackageManager packageManager = getPackageManager();
            // getPackageName()是你当前类的包名，0代表是获取版本信息
            PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(), 0);
            version = packInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return version;
    }

    /**
     *  更新app接口
     */
    private void getupdata() {

        JSONObject param = new JSONObject();
        MyApplication myApp = MyApplication.getInstance();
        String sign = null;
        try {
            param.put(BaseParam.IDFA, imei);
            param.put("type", 3);
            param.put("version", getVersionCode());
            param.put(BaseParam.URL_QIAN_API_APPID, myApp.appId);
            param.put(BaseParam.URL_QIAN_API_SERVICE, "version");
            param.put(BaseParam.URL_QIAN_API_SIGNTYPE, myApp.signType);

            String[] array = new String[]{
                    BaseParam.IDFA + "=" + imei,
                    "type=3",
                    "version=" + getVersionCode(),
                    BaseParam.URL_QIAN_API_APPID + "=" + myApp.appId,
                    BaseParam.URL_QIAN_API_SERVICE + "=version",
                    BaseParam.URL_QIAN_API_SIGNTYPE + "=" + myApp.signType};

            APIModel apiModel = new APIModel();
            sign = apiModel.sortStringArray(array);
            param.put(BaseParam.URL_QIAN_API_SIGN, sign);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = BaseParam.URL_QIAN_UPDATA
                + "?idfa=" + imei
                + "&" + "type=" + 3
                + "&" + "version=" + getVersionCode()
                + "&" + "appId=" + myApp.appId
                + "&" + "service=" + "version"
                + "&" + "signType=" + myApp.signType
                + "&" + "sign=" + sign;

        OkGo.post(url)
                .tag(this)
                .upJson(param.toString())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        Log.i("更新", "-------");

                        try {
                            JSONObject object = new JSONObject(s);
                            int errorcode = object.getInt("resultCode");
                            if (errorcode == 1) {
                                JSONObject oj1 = new JSONObject(Check.jsonGetStringAnalysis(object, "resultData"));
                                JSONObject appVersion = new JSONObject(Check.jsonGetStringAnalysis(oj1, "appVersion"));

                                android_action = appVersion.getString("action"); //1 强制  ,2 不强制
                                android_content = appVersion.getString("content");
                                androidUrl = appVersion.getString("url"); //下载app的url
                                VersionAndroid = appVersion.getString("version"); //服务端返回的版本  1.2.9
                                filename=VersionAndroid;
                                download_url=androidUrl;

                                /**
                                 *  修改更新逻辑,以前是只判断Version是否一致,现在还要判断两个Version的大小
                                 */
                            int  server_version=Integer.parseInt(VersionAndroid.replace(".",""));
                            int  now_version=Integer.parseInt(getVersionCode().replace(".",""));
                                if (now_version!=server_version&&now_version<server_version){
                                    if (android_action != null && android_action.equals("1")) { //强制更新
                                        update_force(androidUrl, VersionAndroid, android_content, android_action);
                                    } else if (android_action != null && android_action.equals("2")) {  //不强制更新
                                        if (!up_data.equals(VersionAndroid)) { //本地保存的版本号跟服务端返回的版本号不一致
                                            update(androidUrl, VersionAndroid, android_content, android_action); //可选更新
                                        }
                                    }
                                }


                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        Log.e("更新", e.toString());
                    }
                });
    }


}
