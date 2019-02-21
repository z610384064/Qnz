package com.rd.qnz;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

import com.rd.qnz.community.ActiveAct;
import com.rd.qnz.custom.MyApplication;
import com.rd.qnz.homepage.HomePageActivity2;
import com.rd.qnz.mine.MineAct;
import com.rd.qnz.product.ProductList;
import com.rd.qnz.tools.AppTool;

import java.lang.reflect.Field;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 主界面的tab
 *
 * @author Evonne
 */
@SuppressWarnings("deprecation")
public class MainTabAct extends TabActivity {

    private MyApplication myApp;
    private Intent mHomepageIntent, mProductListIntent,mActivityIntent, mMineIntent;

    private LinearLayout mBut0, mBut1,mBut2,mBut3;

    private ImageView mIv0, mIv1, mIv2,mIv3;
    private TextView mTv0, mTv1, mTv2,mTv3;
    private Typeface iconfont = null;
    private String regact = "";    //用来判断是否是手势解锁后进来的

    /**
     * 1.6.0 登录界面
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_tab);  //2代表适配完的

        //在android4.4的设备上设置状态栏透明
        highApiEffects();


        myApp = (MyApplication) getApplication();
        regact = "";
        regact = getIntent().getStringExtra("account");
        if (regact != null) {
            Log.e("regact---regact", regact);
        }

        myApp.context = MainTabAct.this;
        iconfont = Typeface.createFromAsset(getAssets(), "iconfont.ttf");   //一个字体文件,实际上没有用到

        prepareIntent();   //设置一下三个跳转的intent
        setupIntent();

        prepareView();   //做一下findViewById ,进行一下regact判断,看是要进首页还是进钱袋界面
        registerBoradcastReceiver();          //在这里面动态注册了一个广播,不过接收到广播以后并没有做什么处理,所以可以默认没用
        AppTool.getUserStatusInfoRequest();  //获取一下用户的状态信息,把数据存入到sp

    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void highApiEffects() {
        getWindow().getDecorView().setFitsSystemWindows(true);
        //透明状态栏 @顶部
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //透明导航栏 @底部 这一句不要加，目的是防止沉浸式状态栏和部分底部自带虚拟按键的手机（比如华为）发生冲突，注释掉就好了
//         getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterMyBoradcastReceiver();
    }

    //这个
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
        if(action.equals("MainTabAct")){
                myApp.tabHostId=3;
                myApp.tabHost.setCurrentTab(myApp.tabHostId);   //这个是跳转界面的方法

            }
        }

    };

    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction("MainTabAct"); //从登录界面返回回来的数据
        // 注册广播
        registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    public void unRegisterMyBoradcastReceiver() {
        if (mBroadcastReceiver != null) {
            try{
                unregisterReceiver(mBroadcastReceiver);
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    private void prepareIntent() {
        /* 导航的四个标签:  首页 产品列表 我的钱袋 更多 */
        mHomepageIntent = new Intent(this, HomePageActivity2.class);
        mProductListIntent = new Intent(this, ProductList.class);

        mActivityIntent=new Intent(this, ActiveAct.class);
        mMineIntent = new Intent(this, MineAct.class);
    }

    private void setupIntent() {
        myApp.tabHost = getTabHost();

        if (regact != null && regact.equals("yes")) {
            try {
                Field idcurrent = myApp.tabHost.getClass().getDeclaredField("mCurrentTab");
                idcurrent.setAccessible(true);
                idcurrent.setInt(myApp.tabHost, -2);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        myApp.tabHost.setup();

        TabSpec recommend_tab = myApp.tabHost.newTabSpec("首页").setIndicator("首页").setContent(mHomepageIntent);
        TabSpec list_tab = myApp.tabHost.newTabSpec("理财").setIndicator("理财").setContent(mProductListIntent);
        TabSpec activity_tab = myApp.tabHost.newTabSpec("活动").setIndicator("活动").setContent(mActivityIntent);
        TabSpec product_tab = myApp.tabHost.newTabSpec("钱袋").setIndicator("钱袋").setContent(mMineIntent);

        myApp.tabHost.addTab(recommend_tab);
        myApp.tabHost.addTab(list_tab);
        myApp.tabHost.addTab(activity_tab);
        myApp.tabHost.addTab(product_tab);

        int tadid = 2;
        try {
            Field idcurrent = myApp.tabHost.getClass().getDeclaredField("mCurrentTab");
            idcurrent.setAccessible(true);
            if (tadid == 0) {
                idcurrent.setInt(myApp.tabHost, 1);
            } else {
                idcurrent.setInt(myApp.tabHost, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        myApp.tabHost.setOnTabChangedListener(new OnTabChangeListener() {
            @Override
            public void onTabChanged(String arg0) {

                switch (myApp.tabHost.getCurrentTab()) {
                    case 0: //首页
                        tianchongtext(mIv0, R.drawable.homepager_select);
                        tianchongColors(mTv0, R.color.tab_color);
                        tianchongtext(mIv1, R.drawable.product_nomal);
                        tianchongColors(mTv1, R.color.tenderr_tex);
                        tianchongtext(mIv2, R.drawable.active_nomal);
                        tianchongColors(mTv2, R.color.tenderr_tex);
                        tianchongtext(mIv3, R.drawable.mine_nomal);
                        tianchongColors(mTv3, R.color.tenderr_tex);
                        break;
                    case 1:  //理财

                        tianchongtext(mIv0, R.drawable.homepager_nomal);
                        tianchongColors(mTv0, R.color.tenderr_tex);
                        tianchongtext(mIv1, R.drawable.product_select);
                        tianchongColors(mTv1, R.color.tab_color);
                        tianchongtext(mIv2, R.drawable.active_nomal);
                        tianchongColors(mTv2, R.color.tenderr_tex);
                        tianchongtext(mIv3, R.drawable.mine_nomal);
                        tianchongColors(mTv3, R.color.tenderr_tex);
                        break;
                    case 2:
                        tianchongtext(mIv0, R.drawable.homepager_nomal);
                        tianchongColors(mTv0, R.color.tenderr_tex);
                        tianchongtext(mIv1, R.drawable.product_nomal);
                        tianchongColors(mTv1, R.color.tenderr_tex);
                        tianchongtext(mIv2, R.drawable.active_select);
                        tianchongColors(mTv2, R.color.tab_color);
                        tianchongtext(mIv3, R.drawable.mine_nomal);
                        tianchongColors(mTv3, R.color.tenderr_tex);
                        break;
                    case 3:
                        tianchongtext(mIv0, R.drawable.homepager_nomal);
                        tianchongColors(mTv0, R.color.tenderr_tex);
                        tianchongtext(mIv1, R.drawable.product_nomal);
                        tianchongColors(mTv1, R.color.tenderr_tex);
                        tianchongtext(mIv2, R.drawable.active_nomal);
                        tianchongColors(mTv2, R.color.tenderr_tex);
                        tianchongtext(mIv3, R.drawable.mine_select);
                        tianchongColors(mTv3, R.color.tab_color);
                        break;

                    default:
                        break;
                }
            }
        });
    }

    private void prepareView() {
        mBut0 = (LinearLayout) findViewById(R.id.main_tab0);
        mIv0 = (ImageView) findViewById(R.id.main_tab0_iv);
        mTv0 = (TextView) findViewById(R.id.main_tab0_tv);

        mBut1 = (LinearLayout) findViewById(R.id.main_tab1);
        mIv1 = (ImageView) findViewById(R.id.main_tab1_iv);
        mTv1 = (TextView) findViewById(R.id.main_tab1_tv);

        mBut2= (LinearLayout) findViewById(R.id.main_tab2); //底部活动界面
        mIv2 = (ImageView) findViewById(R.id.main_tab2_iv);
        mTv2 = (TextView) findViewById(R.id.main_tab2_tv);

        mBut3 = (LinearLayout) findViewById(R.id.main_tab3); //底部钱袋界面
        mIv3 = (ImageView) findViewById(R.id.main_tab3_iv);
        mTv3 = (TextView) findViewById(R.id.main_tab3_tv);


        tianchongtext(mIv0, R.drawable.homepager_select);
        tianchongColors(mTv0, R.color.tab_color);

        if (regact != null && regact.equals("yes")) {  //如果是从注册界面过来的直接进入我的钱袋界面
            myApp.tabHostId = 3;
            myApp.tabHost.setCurrentTab(myApp.tabHostId);
        } else {
            myApp.tabHost.setCurrentTab(0); //如果是从其他方式来的首页,直接进入首页界面
        }

        MyListener myListener = new MyListener();
        mBut0.setOnClickListener(myListener);
        mBut1.setOnClickListener(myListener);
        mBut2.setOnClickListener(myListener);
        mBut3.setOnClickListener(myListener);
    }

    private class MyListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.main_tab0:
                    myApp.tabHostId = 0;
//                    SystemBarTintManager tintManager = new SystemBarTintManager(MainTabAct.this);
//                    setTranslucentStatus(false);
//                    tintManager.setStatusBarTintEnabled(false);
                    //设置状态栏颜色
//                    tintManager.setStatusBarTintResource(Color.TRANSPARENT);
                    break;
                case R.id.main_tab1:
                    myApp.tabHostId = 1;
//                       tintManager = new SystemBarTintManager(MainTabAct.this);
//                    setTranslucentStatus(false);
//                    tintManager.setStatusBarTintEnabled(false);
                    break;
                case R.id.main_tab2: //点击了活动界面
                    myApp.tabHostId = 2;
//                     tintManager = new SystemBarTintManager(MainTabAct.this);
//                    setTranslucentStatus(false);
//                    tintManager.setStatusBarTintEnabled(false);
                    break;
                case R.id.main_tab3:  //点击了我的钱袋

                        myApp.tabHostId = 3;
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                        setTranslucentStatus(true);
//                    }
//
//                     tintManager = new SystemBarTintManager(MainTabAct.this);
//                    tintManager.setStatusBarTintEnabled(true);
//                    //设置状态栏颜色
//                    tintManager.setStatusBarTintResource(R.color.redpacket_money);

                    break;

            }
            myApp.tabHost.setCurrentTab(myApp.tabHostId);   //这个是跳转界面的方法
        }
    }
    @TargetApi(19)  //改变状态栏颜色
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }
    //顶部内容顶到状态栏上
    private void fullScreen(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //5.x开始需要把颜色设置透明，否则导航栏会呈现系统默认的浅灰色
                Window window = activity.getWindow();
                View decorView = window.getDecorView();
                //两个 flag 要结合使用，表示让应用的主体内容占用系统状态栏的空间
                int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
                decorView.setSystemUiVisibility(option);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.TRANSPARENT);
                //导航栏颜色也可以正常设置
//                window.setNavigationBarColor(Color.TRANSPARENT);
            } else {
                Window window = activity.getWindow();
                WindowManager.LayoutParams attributes = window.getAttributes();
                int flagTranslucentStatus = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
                int flagTranslucentNavigation = WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
                attributes.flags |= flagTranslucentStatus;
//                attributes.flags |= flagTranslucentNavigation;
                window.setAttributes(attributes);
            }
        }
    }

    private void tianchongColors(TextView textView, int clolors) {
        textView.setTextColor(getResources().getColor(clolors));
    }

    private void tianchongtext(ImageView imageviews, int fonts) {
        imageviews.setBackgroundResource(fonts);
    }

    /**
     * // * 菜单、返回键响应 //
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitBy2Click(); // 调用双击退出函数
        }
        return false;
    }

    /**
     * 双击退出函数
     */
    private static Boolean isExit1 = false;

    private void exitBy2Click() {
        Timer tExit = null;
        if (isExit1 == false) {
            isExit1 = true; // 准备退出
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit1 = false; // 取消退出
                }
            }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务

        } else {
            finish();
            System.exit(0);
        }
    }

}
