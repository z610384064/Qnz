package com.rd.qnz;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;
import com.lzy.okgo.request.PostRequest;
import com.rd.qnz.bean.BaseBean;
import com.rd.qnz.bean.StartPageBean;
import com.rd.qnz.custom.MineShow;
import com.rd.qnz.custom.MyApplication;
import com.rd.qnz.http.HttpUtils;
import com.rd.qnz.http.okgo.callback.JsonCallback;
import com.rd.qnz.tools.BaseParam;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Response;


/**
 * 启动页
 *
 * @author Evonne
 */


public class LoginingAct extends Activity {

    @BindView(R.id.imview_top)
    ImageView imviewTop;
    @BindView(R.id.tv_skip)
    TextView tvSkip;
    private MyApplication myApp;
    private String refreshToken;
    private String oauthToken;
    // 线程是否运行判断变量

    /**
     * 1.6.0新版本倒计时
     */
    private MyCountDownTimer mCDT;
    private Timer mTimer;
    private StartPageBean startPageBean;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logining);
        ButterKnife.bind(this);
        MobclickAgent.setDebugMode(true);   //打开debug模式
        MobclickAgent.openActivityDurationTrack(false);    //来禁止默认的Activity页面统计方式。

        myApp = (MyApplication) getApplication();
        //读取sp_user.xml文件
        SharedPreferences preferences = getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_USER, Context.MODE_PRIVATE);
        oauthToken = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN, "");
        refreshToken = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_REFRESHTOKEN, "");






        tvSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mTimer != null) {
                    mTimer.cancel();
                }
                Intent intent = new Intent(LoginingAct.this, MainTabAct.class);
                startActivity(intent);
                finish();
            }
        });


        startPageRequest();


    }


    private void startPageRequest() {

        HashMap<String,String> map=new HashMap<>();
        map.put(  BaseParam.URL_QIAN_API_APPID, myApp.appId);
        map.put(  BaseParam.URL_QIAN_API_SERVICE, "getStartPage");
        map.put(  BaseParam.URL_QIAN_API_SIGNTYPE,myApp.signType);
        PostRequest request1=  HttpUtils.getRequestSign(BaseParam.URL_QIAN_GETSTARTPAGE, LoginingAct.this, map);

        String[] str = new String[]{
                BaseParam.URL_QIAN_API_APPID, myApp.appId,
                BaseParam.URL_QIAN_API_SERVICE, "getStartPage",
                BaseParam.URL_QIAN_API_SIGNTYPE, myApp.signType};
        PostRequest request = HttpUtils.getRequest(BaseParam.URL_QIAN_GETSTARTPAGE, LoginingAct.this, str);
        request.execute(new JsonCallback<BaseBean<StartPageBean>>() {
            @Override
            public void onSuccess(BaseBean<StartPageBean> startPageBeanBaseBean, Call call, Response response) {
                if (startPageBeanBaseBean.resultCode.equals("1")) {
                    startPageBean = startPageBeanBaseBean.resultData;
                    BitmapUtils.getInstence().display(imviewTop, BaseParam.URL_QIAN + startPageBean.getStartPageUrl()); //使用xutils 进行图片展示
                    imviewTop.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mTimer != null) {
                                mTimer.cancel();
                            }
                            Intent intent = new Intent(LoginingAct.this, MainTabAct.class);
                            startActivity(intent);
                            Intent intent2 = new Intent(LoginingAct.this, WebBannerViewNeedAccesTokenActivity.class);
                            intent2.putExtra("web_url", BaseParam.URL_QIAN + startPageBean.getLocationUrl());
                            intent2.putExtra("type", "3");
                            intent2.putExtra("message", BaseParam.QIAN_BANNER_INTRO);
                            startActivity(intent2);

                            finish();
                        }
                    });
                    try {
                        Integer time = Integer.parseInt(startPageBean.getCountdownTime()) * 1000;
                        mTimer = new Timer();
                        mTimer.schedule(new MyTimerTask(), time);  //等待5秒才运行MyTimerTask里面的方法
                        mCDT = new MyCountDownTimer(time, 1000);  //总共有5秒,每一秒调用一次onTick方法,修改界面的textview
                        mCDT.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                        mTimer = new Timer();
                        mTimer.schedule(new MyTimerTask(), 5000);  //等待5秒才运行MyTimerTask里面的方法
                        mCDT = new MyCountDownTimer(5000, 1000);  //总共有5秒,每一秒调用一次onTick方法,修改界面的textview
                        mCDT.start();
                    }


                } else {
                    MineShow.toastShow("请检查您的网络", LoginingAct.this);
                }
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                mTimer = new Timer();
                mTimer.schedule(new MyTimerTask(), 5000);  //等待5秒才运行MyTimerTask里面的方法
                mCDT = new MyCountDownTimer(5000, 1000);  //总共有5秒,每一秒调用一次onTick方法,修改界面的textview
                mCDT.start();
            }
        });
    }


    /**
     * 4秒后调用该方法
     */
    class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            Intent intent = new Intent(LoginingAct.this, MainTabAct.class);
            startActivity(intent);
            finish();

        }
    }

    class MyCountDownTimer extends CountDownTimer {

        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long l) {
            tvSkip.setVisibility(View.VISIBLE);
            tvSkip.setText("跳过" + l / 1000 + "秒");
        }

        @Override
        public void onFinish() {

        }
    }


}
