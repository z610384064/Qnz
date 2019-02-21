package com.rd.qnz.custom;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.support.multidex.MultiDex;
import android.util.Log;
import android.widget.TabHost;


import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.cookie.store.PersistentCookieStore;
import com.lzy.okgo.request.PostRequest;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.qiyukf.unicorn.api.SavePowerConfig;
import com.qiyukf.unicorn.api.StatusBarNotificationConfig;
import com.qiyukf.unicorn.api.Unicorn;
import com.qiyukf.unicorn.api.YSFOptions;
import com.rd.qnz.R;
import com.rd.qnz.bean.BaseBean;
import com.rd.qnz.bean.RefreshBean;
import com.rd.qnz.http.HttpUtils;
import com.rd.qnz.http.okgo.callback.JsonCallback;
import com.rd.qnz.qiyu.DemoCache;
import com.rd.qnz.qiyu.UILImageLoader;
import com.rd.qnz.qiyu.Utils;
import com.rd.qnz.tools.BaseParam;
import com.rd.qnz.util.MyVolley;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;

import cn.jpush.android.api.JPushInterface;
import okhttp3.Call;
import okhttp3.Response;

public class MyApplication extends Application {

    private String TAG="MyApplication";
    public int tabHostId = 0;
    public TabHost tabHost;
    public String QIAN_VERSIONNAME = "";
    public String PLATFORM_ID = "-1";

    public static String appId = "20150710100373165482";
    String appKey = "5b63b810f1f46f52e6b44111347591fb";   //七鱼的appkey

    public String service = "login";
    public String signType = "MD5";

    public static String JSESSIONID = null;
    private static MyApplication mInstance;
    public int homeCount = 0;
    public int isApplication = 0;// 1 表示正在运行 2.表示正在执行
    public int time = 0;
    public int isSeccess = 0;
    public boolean isReg = false;// true 注册跳转 false 不是注册跳转
    public String redPacketAmount = "";// 红包 金额
    public String redPacketOpen = "";// 红包开关
    public String redPacketType = "";// 红包类型

    public boolean isFloatingWindow = true;// 可以出现悬浮窗口为true,不可以为false。我在启动页会设为true,出现完了当即设为false;
    public boolean isAnzhuang = false; // 首次安装 直接跳转到登录页面
    public final String ACTION_NAME = "消息公告";
    public String latestDate = "";// 消息公告的时间
    public boolean homepage = false;//
    public boolean isLock = false;// 首次进来


    public static final String IMG_SAVE_PATH =
            Environment.getExternalStorageDirectory() + File.separator + "QNZ"
                    + File.separator + "imgs";
    public static final String PORTRAIT_FILE_NAME = "qnz_user_portrait.jpg";

    public boolean isfirst = false;// 是第一次打开应用
    public Bitmap bt_head;
    public String head_url;
    /**
     * 1.3.0 申购 银行卡 返回
     *
     * @return
     */
    public boolean real_back = false;//
    public String real_name = "";// 姓名
    public String bank_name = "";// 银行
    public String uniqNo = "";// 银行卡的唯一标识
    public String hiddenCardNo = "";// 尾号
    public String bankId = "";// 银行卡id
    public boolean isMyGongGao = true;// 每次启动程序 检测公告

    private String refreshToken;
    private String oauthToken;
    public static MyApplication getInstance() {
        if (mInstance == null) {
            mInstance = new MyApplication();
        }
        return mInstance;
    }

    public Context context;

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();

        //打开调试Log;
//        GrowingIO.startWithConfiguration(this, new Configuration()
//                .useID()
//                .trackAllFragments()
//                .setChannel(BaseParam.getChannelCode(this))
//                .setDebugMode(true));


        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);


        MultiDex.install(this);
        Log.d(TAG,"MyApplication的oncreate()调用了");
        mInstance = this;
        isApplication = 1;
        MyVolley.init(this);
        File path = new File(IMG_SAVE_PATH);
        if (!path.exists()) {
            path.mkdirs();
        }

        //保护
//        MyUncaugthExceptionHandler handler = MyUncaugthExceptionHandler.getInstance();
//        handler.init(getApplicationContext());

        Unicorn.init(this, appKey, options(), new UILImageLoader());

        if (inMainProcess(this)) {
            DemoCache.context = getApplicationContext();
            ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(this));
        }



        /**
         * okhttp
         * */
        //---------这里给出的是示例代码,告诉你可以这么传,实际使用的时候,根据需要传,不需要就不传-------------//
        // HttpHeaders headers = new HttpHeaders();
        //  headers.put("commonHeaderKey1", "commonHeaderValue1");    //header不支持中文
        //  headers.put("commonHeaderKey2", "commonHeaderValue2");
        // HttpParams params = new HttpParams();
        // params.put("commonParamsKey1", "commonParamsValue1");     //param支持中文,直接传,不要自己编码
        //  params.put("commonParamsKey2", "这里支持中文参数");
        //-----------------------------------------------------------------------------------//
        //必须调用初始化
        OkGo.init(this);
//      以下设置的所有参数是全局参数,同样的参数可以在请求的时候再设置一遍,那么对于该请求来讲,请求中的参数会覆盖全局参数
//      好处是全局参数统一,特定请求可以特别定制参数
        try {
            //以下都不是必须的，根据需要自行选择,一般来说只需要 debug,缓存相关,cookie相关的 就可以了
            OkGo.getInstance()
//                  打开该调试开关,打印级别INFO,并不是异常,是为了显眼,不需要就不要加入该行
//                  最后的true表示是否打印okgo的内部异常，一般打开方便调试错误
                    .debug("OkGo", Level.INFO, true)
//                  如果使用默认的 60秒,以下三行也不需要传
                    .setConnectTimeout(OkGo.DEFAULT_MILLISECONDS)  //全局的连接超时时间
                    .setReadTimeOut(OkGo.DEFAULT_MILLISECONDS)     //全局的读取超时时间
                    .setWriteTimeOut(OkGo.DEFAULT_MILLISECONDS)    //全局的写入超时时间
//                  可以全局统一设置缓存模式,默认是不使用缓存,可以不传,具体其他模式看 github 介绍 https://github.com/jeasonlzy/
                    .setCacheMode(CacheMode.NO_CACHE)
//                  可以全局统一设置缓存时间,默认永不过期,具体使用方法看 github 介绍
                    .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)
//                  可以全局统一设置超时重连次数,默认为三次,那么最差的情况会请求4次(一次原始请求,三次重连请求),不需要可以设置为0
                    .setRetryCount(3)
//                  如果不想让框架管理cookie（或者叫session的保持）,以下不需要
//                  .setCookieStore(new MemoryCookieStore())            //cookie使用内存缓存（app退出后，cookie消失）
                    .setCookieStore(new PersistentCookieStore())        //cookie持久化存储，如果cookie不过期，则一直有效
//                  可以设置https的证书,以下几种方案根据需要自己设置
                    .setCertificates();                                //方法一：信任所有证书,不安全有风险
//                  .setCertificates(new SafeTrustManager())            //方法二：自定义信任规则，校验服务端证书
//                  .setCertificates(getAssets().open("srca.cer"))      //方法三：使用预埋证书，校验服务端证书（自签名证书）
//                  方法四：使用bks证书和密码管理客户端证书（双向认证），使用预埋证书，校验服务端证书（自签名证书）
//                  .setCertificates(getAssets().open("xxx.bks"), "123456", getAssets().open("yyy.cer"))//
//                  配置https的域名匹配规则，详细看demo的初始化介绍，不需要就不要加入，使用不当会导致https握手失败
//                  .setHostnameVerifier(new SafeHostnameVerifier())
//                  可以添加全局拦截器，不需要就不要加入，错误写法直接导致任何回调不执行
//                  .addInterceptor(new Interceptor() {
//                    @Override
//                    public Response intercept(Chain chain) throws IOException {
//                        return chain.proceed(chain.request());
//                    }
//                })
//                  这两行同上，不需要就不要加入
            //  .addCommonHeaders(headers)  //设置全局公共头
            // .addCommonParams(params)   //设置全局公共参数


            SharedPreferences preferences = getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_USER, Context.MODE_PRIVATE);
            oauthToken = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN, "");
            refreshToken = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_REFRESHTOKEN, "");

            if (oauthToken.equals("") || oauthToken == null) {  //如果以前没有登录过,睡眠3秒跳到MainTabAct

            } else {  //已经登录了,走刷新token
                startRefreshRequest();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* 刷新token */
    private void startRefreshRequest() {

        String str[] = new String[]{
                BaseParam.QIAN_SHAREDPREFERENCES_USER_REFRESHTOKEN, refreshToken,
                "appId", appId,
                BaseParam.URL_QIAN_API_SERVICE, "refreshToken",
                BaseParam.URL_QIAN_API_SIGNTYPE, signType,
        };
        PostRequest request = HttpUtils.getRequest(BaseParam.URL_QIAN_REFRESHTOKEN, MyApplication.this, str);
        request.execute(new JsonCallback<BaseBean<RefreshBean>>() {
            @Override
            public void onSuccess(BaseBean<RefreshBean> refreshBeanBaseBean, Call call, Response response) {
                if (null != refreshBeanBaseBean) {
                    RefreshBean resultData = refreshBeanBaseBean.resultData;
                    if (refreshBeanBaseBean.resultCode.equals("1")) {
                        final String avayar_url = BaseParam.URL_QIAN + resultData.getUserIcon();
                        head_url = avayar_url;


                        Context ctx = MyApplication.this;
                        SharedPreferences sp = ctx.getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_USER, MODE_PRIVATE);
                        // 存入数据
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString(BaseParam.QIAN_SHAREDPREFERENCES_USER_EXPIRES, resultData.getExpires());
                        editor.putString(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN, resultData.getOauthToken());
                        editor.putString(BaseParam.QIAN_SHAREDPREFERENCES_USER_REALSTATUS, resultData.getRealStatus() + "");
                        editor.putString(BaseParam.QIAN_SHAREDPREFERENCES_USER_PHONESTATUS, resultData.getPhoneStatus() + "");
                        editor.putString(BaseParam.QIAN_SHAREDPREFERENCES_USER_EMAILSTATUS, resultData.getEmailStatus() + "");
                        editor.putString(BaseParam.QIAN_SHAREDPREFERENCES_USER_REFRESHTOKEN, resultData.getRefreshToken());
                        editor.putString(BaseParam.QIAN_SHAREDPREFERENCES_USER_BANKSTATUS, resultData.getBankStatus() + "");
                        editor.putString(BaseParam.QIAN_SHAREDPREFERENCES_USER_PAYPWDFLAG, resultData.getPayPwdFlag() + "");
                        editor.putString(BaseParam.QIAN_SHAREDPREFERENCES_USER_USERID, resultData.getUserId() + "");
                        editor.putString(BaseParam.QIAN_SHAREDPREFERENCES_USER_REALNAME, resultData.getRealName());
                        editor.putString(BaseParam.QIAN_SHAREDPREFERENCES_USER_PHONE, resultData.getPhone());
                        editor.putInt(BaseParam.QIAN_SHAREDPREFERENCES_USER_JYCOUNT, 0);
                        editor.putLong(BaseParam.QIAN_SHAREDPREFERENCES_USER_JYTIME, System.currentTimeMillis() / 1000);
                        editor.commit();


                    } else {

                    }
                }
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                SharedPreferences sp = getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_USER, MODE_PRIVATE);
                //存入数据
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
                editor.putString(BaseParam.QIAN_SHAREDPREFERENCES_USER_ISRED, "");
                editor.putString(BaseParam.QIAN_SHAREDPREFERENCES_USER_REALNAME, "");
                editor.putString(BaseParam.QIAN_SHAREDPREFERENCES_USER_PHONE, "");
                editor.putString(BaseParam.QIAN_SHAREDPREFERENCES_USER_USERNAME, "");
                editor.commit();

            }
        });
    }


    // 如果返回值为null，则全部使用默认参数。
    private YSFOptions options() {
        YSFOptions options = new YSFOptions();
        options.statusBarNotificationConfig = new StatusBarNotificationConfig();
        options.statusBarNotificationConfig.notificationSmallIconId = R.drawable.icon;
        options.savePowerConfig = new SavePowerConfig();
        return options;
    }

    public static boolean inMainProcess(Context context) {
        String packageName = context.getPackageName();
        String processName = Utils.getProcessName(context);
        return packageName.equals(processName);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

    }

    @Override
    public void onLowMemory() {
        // TODO Auto-generated method stub
        super.onLowMemory();

    }

    @Override
    public void onConfigurationChanged(android.content.res.Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.i("", "onConfigurationChanged=");
    }


    /**
     *  以下的两个方法在AppTool里面有用到,主要是用来获取用户状态
     * @return
     */
    public Handler getBaseHandler() {
        return mAppHandler;
    }

    private Handler mAppHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {//
            Bundle localBundle1 = msg.getData();
            ArrayList<Parcelable> mUserInfoStatus = localBundle1
                    .getParcelableArrayList(BaseParam.URL_QIAN_USER_STATUS_INFORMATION);// 获取用户信息
            if (null != mUserInfoStatus) {// 充值 获取和连连支付相关的数据
                Map<String, String> map = (Map<String, String>) mUserInfoStatus.get(0);
                initUserStatus(map);
            }
        }

        ;
    };

    private void initUserStatus(Map<String, String> map) {
        if (map != null) {
            Profile.setUserRealNameStatus(map.get(BaseParam.QIAN_USER_STATUS_INFO_REAL_NAME));
            Profile.setUserBankCardStatus(map.get(BaseParam.QIAN_USER_STATUS_INFO_BANK_CARD));
            Profile.setUserNeedPopStatus(map.get(BaseParam.QIAN_USER_STATUS_INFO_NEED_POP));
            Profile.setUserPayPassWordStatus(map.get(BaseParam.QIAN_USER_STATUS_INFO_PAY_PWD));
            Profile.setUserIsNewHandStatus(map.get(BaseParam.QIAN_USER_STATUS_INFO_NEWHAND_STATUS));
        }
    }

}
