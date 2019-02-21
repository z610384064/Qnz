package com.rd.qnz.homepage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.rd.qnz.R;
import com.rd.qnz.custom.MyApplication;
import com.rd.qnz.tools.AppTool;
import com.rd.qnz.tools.BaseParam;
import com.tencent.connect.share.QQShare;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

/*
分享窗口
 */
public class SharePopupWindow extends Activity implements OnClickListener, IWXAPIEventHandler {
    private LinearLayout share_tab0, share_tab1, share_tab_qq;

    private final String WX_PACKAGE_NAME = "com.tencent.mm";

    // APP_ID 替换为你的应用从官方网站申请到的合法appId
    // public static final String APP_ID = "wxd930ea5d5a258f4f";
    public static final String APP_ID = "wx24bba7e8d4231785";
    // IWXAPI 是第三方app和微信通信的openapi接口
    private IWXAPI api;
    private String title = "";
    private String web_url = "";
    private String type = "";//1 表示 首页轮播图、  2 表示活动中心 跳转

    private String fileName = "";
    private String imageUrl = "";

     // TODO: 2017/3/9 0009
    private String intro=""; //标题
    private String duce=""; //内容
    private ImageView mImageView;

    private static Tencent mTencent;
    private int shareType = QQShare.SHARE_TO_QQ_TYPE_DEFAULT;
    public static final String QQ_APP_ID = "1104761586";


    public static final String KEY_SHARE_TYPE = "key_share_type";
    public static final int SHARE_CLIENT = 1;
    public static final int SHARE_ALL_IN_ONE = 2;

    private Bitmap mShareBitmap;//分享出去的bitmap

    private int mShareType = SHARE_ALL_IN_ONE;

    private boolean mIsShareApp;
    private String mShare;
    private String mServiceShare;
    private String share_description;

    /**
     * @param context
     * @param title
     * @param url      分享出去的链接
     * @param type
     * @param fileName
     * @param imageUrl 图片地址
     */
    public static void start(Context context, String title, String url, String type, String fileName, String imageUrl) {
        Intent intent = new Intent(context, SharePopupWindow.class);
        intent.putExtra("title", title);
        intent.putExtra("web_url", url);
        intent.putExtra("type", type);
        intent.putExtra("fileName", fileName);
        intent.putExtra("imageUrl", imageUrl);
        context.startActivity(intent);

    }

    /**
     * 我在产品详情页分享app
     * @param context
     * @param isShareApp  true
     * @param share         2
     * @param serviceShare  null
     * @param title        钱内助
     *///点击分享给好友  跳转过来  CommunityAct.this         true,            "2",          "",                  ""
    public static void start(Context context, boolean isShareApp, String share, String serviceShare, String title) {
        Intent intent = new Intent(context, SharePopupWindow.class);
        intent.putExtra("isShareApp", isShareApp);
        intent.putExtra("share", share);
        intent.putExtra("serviceShare", serviceShare);
        intent.putExtra("title", title);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Transparent);
        setContentView(R.layout.dialog_layout_share);
        Intent intent = getIntent();
        mIsShareApp = intent.getBooleanExtra("isShareApp", false);
        imageUrl = intent.getStringExtra("imageUrl");
        Log.d("pepe", "传过来的imageUrl:" + imageUrl);
        title = intent.getStringExtra("title");
        Log.d("pepe", "传过来的title:" + title);
        web_url = intent.getStringExtra("web_url");
        Log.d("pepe", "传过来的web_url:" + web_url);
        type = intent.getStringExtra("type");
        Log.d("pepe", "传过来的type:" + type);
        fileName = intent.getStringExtra("fileName");
        // TODO: 2017/3/9 0009
        intro=intent.getStringExtra("intro");
        duce=intent.getStringExtra("duce");


        web_url = AppTool.addShareIdAtLastUrl(web_url);
        Log.d("pepe", "AppTool之后的web_url:" + web_url);
        if (mIsShareApp) {    //(从产品页过来的话)分享的是app,type=2
            mShare = intent.getStringExtra("share");
            mServiceShare = intent.getStringExtra("serviceShare");
            if (mShare.equals("1")) {
                share_description = "钱内助";
            } else {
                share_description = BaseParam.QIAN_SHARE; //献给最不平凡的你，即使没有好几个零的存款
            }
        } else {
            if ("1".equals(type)) {  //目前没有1这个的
                fileName = title + web_url;
            } else if ("2".equals(type)) { //如果是从活动中心过来,那么文本内容是 文本+url
//                fileName = fileName + web_url;
                fileName =BaseParam.QIAN_BANNER_INTRO;  //活动火爆进行中
            } else if ("3".equals(type)) { //如果是首页banner,那么文本一定是 活动火爆进行中，钱内助邀您一起参加！
                share_description = fileName;

                //不改动message
            } else {   //从首页广告弹窗进来的
                fileName = BaseParam.QIAN_SHARE;
            }
        }
        mImageView = (ImageView) findViewById(R.id.test_image);
        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        api = WXAPIFactory.createWXAPI(SharePopupWindow.this, APP_ID, true);
//		api.handleIntent(getIntent(), SharePopupWindow.this);
        api.registerApp(APP_ID);

        share_tab0 = (LinearLayout) findViewById(R.id.share_tab0);  //微信
        share_tab1 = (LinearLayout) findViewById(R.id.share_tab1);  //朋友圈
        share_tab_qq = (LinearLayout) findViewById(R.id.share_tab_qq); //qq
        share_tab_qq.setOnClickListener(this);
        share_tab0.setOnClickListener(this);
        share_tab1.setOnClickListener(this);
        initTentQQ();
        initImageBitmap();
    }

    private void initTentQQ() {
        // 其中APP_ID是分配给第三方应用的appid，类型为String。
        mTencent = Tencent.createInstance(QQ_APP_ID, MyApplication.getInstance().getApplicationContext());
    }

    /**
     * 如果是从分享给好友界面跳过来的话,这个方法直接return
     */
    private void initImageBitmap() {
        if (TextUtils.isEmpty(imageUrl)) {
            return;
        }
        BitmapUtils.getInstence().display(mImageView, imageUrl, new BitmapLoadCallBack<View>() {
            @Override
            public void onLoadCompleted(View container, String uri, Bitmap bitmap, BitmapDisplayConfig config, BitmapLoadFrom from) {
                mShareBitmap = bitmap;
            }

            @Override
            public void onLoadFailed(View container, String uri, Drawable drawable) {
                Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.icon);
                mShareBitmap = thumb;
            }
        });
    }


    // 实现onTouchEvent触屏函数但点击屏幕时销毁本Activity
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();
        return true;
    }

    /**
     * 微信分享 （这里仅提供一个分享网页的示例，其它请参看官网示例代码）
     *
     * @param flag(0:分享到微信好友，1：分享到微信朋友圈)
     */
    private void wechatShare(final int flag) {

        //这里把服务端返回给我们的图替换一张自己工程里的图片资源
        final Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.fengxiang);
          gotoShare(thumb, flag);
//        之前的代码
//        if (mShareBitmap == null) {
//            gotoShare(mShareBitmap, flag);
//        } else {
//            BitmapUtils.getInstence().display(mImageView, imageUrl, new BitmapLoadCallBack<View>() {
//                @Override
//                public void onLoadCompleted(View container, String uri, Bitmap bitmap, BitmapDisplayConfig config, BitmapLoadFrom from) {
//                    gotoShare(thumb, flag);
//                }
//
//                @Override
//                public void onLoadFailed(View container, String uri, Drawable drawable) {
//                    gotoShare(thumb, flag);
//                }
//            });
//        }

    }

    /**
      * 分享一个网页(活动)
      * @param bitmap
      * @param flag
      */
    private void gotoShare(Bitmap bitmap, int flag) {
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = web_url;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        if (TextUtils.isEmpty(title)) {
//            msg.title = "钱内助";
            msg.title = "活动中心";
        } else {
            msg.title = title;
        }
        if (!TextUtils.isEmpty(intro)){
            msg.title=intro;
        }

        msg.description = fileName;
        if (!TextUtils.isEmpty(duce)){
            msg.description = duce;
        }
        msg.setThumbImage(bitmap);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = flag == 0 ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;  //微信会话:朋友圈
        api.sendReq(req);
    }

    /**
     * 微信分享 （这里仅提供一个分享网页的示例，其它请参看官网示例代码）
     *
     * @param flag(0:分享到微信好友，1：分享到微信朋友圈)
     */
    private void wechatShareApp(int flag) {
        WXWebpageObject webpage = new WXWebpageObject();
//        webpage.webpageUrl="https://testqnz.qianneizhu.com//api/trade/projectDetail.html?borrowId=412&networkType=1&appId=20150710100373165482&service=projectDetail&signType=MD5&sign=ab35f6a2a068c6607fa324ee4ab7e2da";

        webpage.webpageUrl = BaseParam.QIAN_WEB_DOWNLOAD;  //app下载地址 其他用户在打开的时候也是这个网址
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = "钱内助";
        msg.description = share_description;
        //这里替换一张自己工程里的图片资源
        Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.icon);
        msg.setThumbImage(thumb);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = flag == 0 ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;  //0代表分享到聊天界面,1代表分享到朋友圈
        api.sendReq(req);
    }

    /**
     * 微信分享 （这里仅提供一个分享网页的示例，其它请参看官网示例代码）
     *
     * @param flag(0:分享到微信好友，1：分享到微信朋友圈)
     */
    private void wechatShare1App(int flag) {
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = BaseParam.QIAN_WEB_DOWNLOAD;
        ;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = share_description;
        msg.description = share_description;
        //这里替换一张自己工程里的图片资源
        Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.icon);
        msg.setThumbImage(thumb);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = flag == 0 ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;
        api.sendReq(req);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.share_tab0:    //微信分享(app)
                if (mIsShareApp) {  //是分享app
                    wechatShareApp(0);
                } else {
                    wechatShare(0);
                }
                break;
            case R.id.share_tab1:    //朋友圈分享(app)
                if (mIsShareApp) {
                    wechatShare1App(1);
                } else {
                    wechatShare(1);
                }
                break;
            case R.id.share_tab_qq:
                if (mIsShareApp) {   //分享给qq好友
                    doShareToQQ("钱内助", BaseParam.QIAN_WEB_DOWNLOAD, share_description, BaseParam.QIAN_ICON_WEB_PATH);
                } else {

                        doShareToQQ("钱内助", web_url, fileName, BaseParam.QIAN_ICON_WEB_PATH);


                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onReq(BaseReq arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onResp(BaseResp arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        finish();
    }


    private Handler mHandler = new Handler();

    private void doShareToQQ(String title, String url, String fileName, String imageUrl) {
        final Bundle params = new Bundle();
        if (shareType != QQShare.SHARE_TO_QQ_TYPE_IMAGE) {


            if (!TextUtils.isEmpty(intro)){  //标题
                params.putString(QQShare.SHARE_TO_QQ_TITLE, intro);
            }else if (!TextUtils.isEmpty(title)) {
            params.putString(QQShare.SHARE_TO_QQ_TITLE, title);
                }
            if (!TextUtils.isEmpty(duce)) {  //描述不为空
                params.putString(QQShare.SHARE_TO_QQ_SUMMARY, duce);
            } else if (!TextUtils.isEmpty(fileName)) {
                params.putString(QQShare.SHARE_TO_QQ_SUMMARY, fileName);
           }

            if (!TextUtils.isEmpty(url)) {
                params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, url);
            } else {
                params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, BaseParam.QIAN_WEB_DOWNLOAD);
            }

            if (TextUtils.isEmpty(imageUrl)) {
                params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, BaseParam.QIAN_ICON_WEB_PATH);
            } else {
                params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, imageUrl);
            }

        }

        // QQ分享要在主线程做
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                if (null != SharePopupWindow.mTencent) {
                    SharePopupWindow.mTencent.shareToQQ(SharePopupWindow.this, params, qqShareListener);
                }
            }
        });
    }

    /**
     * qq
     */
    IUiListener qqShareListener = new IUiListener() {
        @Override
        public void onCancel() {
            if (shareType != QQShare.SHARE_TO_QQ_TYPE_IMAGE) {
                Toast.makeText(SharePopupWindow.this, R.string.weibosdk_demo_toast_share_canceled, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onComplete(Object response) {
            Toast.makeText(SharePopupWindow.this, R.string.weibosdk_demo_toast_share_success, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onError(UiError e) {
            Toast.makeText(SharePopupWindow.this, R.string.weibosdk_demo_toast_share_failed, Toast.LENGTH_LONG).show();
        }
    };


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

}
