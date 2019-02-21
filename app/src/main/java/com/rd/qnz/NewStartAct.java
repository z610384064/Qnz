package com.rd.qnz;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.qiyukf.nimlib.sdk.NimIntent;
import com.qiyukf.unicorn.api.ConsultSource;
import com.qiyukf.unicorn.api.ProductDetail;
import com.qiyukf.unicorn.api.Unicorn;
import com.rd.qnz.custom.APIModel;
import com.rd.qnz.custom.MyApplication;
import com.rd.qnz.tools.AppTool;
import com.rd.qnz.tools.BaseParam;
import com.rd.qnz.tools.Check;
import com.rd.qnz.tools.OnViewChangeListener;
import com.rd.qnz.tools.webservice.JsonRequestThreadDefault;
import com.rd.qnz.xutils.Installation;

import java.util.ArrayList;

import cn.jpush.android.api.JPushInterface;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

import static com.rd.qnz.custom.MyApplication.appId;

/**
 * 引导页(5个页面一个个向左滑动)
 *
 * @author Evonne
 */

@RuntimePermissions
public class NewStartAct extends Activity implements OnViewChangeListener {

    private final String TAG = "NewStartAct";

    private MyScrollLayout mScrollLayout;
    private ImageView[] imgs;
    private int count;
    private int currentItem;

    private LinearLayout pointLLayout;
    private MyApplication myApp;
    SharedPreferences sp;
    private String imei;
    private ArrayList<String> param = null;
    private ArrayList<String> value = null;
    APIModel apiModel = new APIModel();
    private Context context = null;
    private String key;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "NewStartAct的oncreate()调用了");
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_start);

        myApp = (MyApplication) getApplication();
        context = NewStartAct.this;

        try {
            if (Build.VERSION.SDK_INT >= 23) { //设备号是6.0及以上 动态判断权限
                requestPermission();
            } else {
                //获取手机DEVICE_ID
                TelephonyManager tm = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
                try {
                    BaseParam.DEVICE_ID = tm.getDeviceId();
                } catch (Exception e) {
                    e.printStackTrace();
                }


                imei = AppTool.imeiSave(NewStartAct.this);   //得到手机的imei号
                imei = imei + "-qian-" + Installation.id(NewStartAct.this);   //加了一串随机数
                next();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 读取设备状态权限申请
     */
    public void next() {

        sp = NewStartAct.this.getSharedPreferences("start", MODE_PRIVATE);

        //不是第一次打开,并且本地保存的版本号跟当前应用的版本号一致 (如果更新了新界面就需要走引导界面)直接跳到LoginingAct 里面是一张引导图,由暗到明3秒后进入首页 MainTabAct
        if (sp.getString("istart", "").equals("yes") && sp.getString("versionName", "").equals(BaseParam.getVersionName(NewStartAct.this))) {
            myApp.isAnzhuang = false;
            Intent intent = new Intent(NewStartAct.this, LoginingAct.class);
            NewStartAct.this.startActivity(intent);
            NewStartAct.this.finish();

        } else {
            myApp.isAnzhuang = true;     //是第一次打开
        }


        key = "0A9B3180020C876F34C29C928876CB48";

        if (sp.getString("activate", "").equals("yes")) {
        } else {
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("activate", "yes");
            editor.commit();
        }
        startDataRequest();  //推广接口
        initView();
        parseIntent();
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        NewStartActPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void requestPermission() {
        //申请权限
        NewStartActPermissionsDispatcher.onPermitWithCheck(this);
    }

    @NeedsPermission({Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void onPermit() {
        imei = AppTool.imeiSave(NewStartAct.this);   //得到手机的imei号
        imei = imei + "-qian-" + Installation.id(NewStartAct.this);   //加了一串随机数

        TelephonyManager tm = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
        try {
            BaseParam.DEVICE_ID = tm.getDeviceId();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i(TAG, "成功 imei:" + imei);
        Log.i(TAG, "成功 BaseParam.DEVICE_ID:" + BaseParam.DEVICE_ID);
        next();
    }


    @OnShowRationale({Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void onShowIntroduce(final PermissionRequest request) {
        request.proceed();
    }

    @OnPermissionDenied({Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void onDenied() {
//        Toast.makeText(this, "权限获取失败", Toast.LENGTH_SHORT).show();


        TelephonyManager tm = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
        try {
            BaseParam.DEVICE_ID = tm.getDeviceId();
        } catch (Exception e) {
            e.printStackTrace();
        }


        imei = AppTool.imeiSave(NewStartAct.this);   //得到手机的imei号
        imei = imei + "-qian-" + Installation.id(NewStartAct.this);   //加了一串随机数
        Log.i(TAG, "失败:imei:" + imei);
        Log.i(TAG, "失败:BaseParam.DEVICE_IDi:" + BaseParam.DEVICE_ID);
        next();
    }

    @OnNeverAskAgain({Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void onNeverAskAgain() {  //用户点击了不再询问

        LayoutInflater inflaterDl = LayoutInflater.from(NewStartAct.this);
        LinearLayout layout = (LinearLayout) inflaterDl.inflate(R.layout.dialog_phoneperssion_layout, null);
        final Dialog dialog = new AlertDialog.Builder(NewStartAct.this).create();
        dialog.show();
        dialog.getWindow().setContentView(layout);
        dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失

        Window dialogWindow = dialog.getWindow();
        WindowManager m = dialogWindow.getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高度
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        p.width = (int) (d.getWidth() * 0.7); // 宽度设置为屏幕的0.65，根据实际情况调整
        p.height = (int) (p.width * 0.8); // 高度设置为屏幕的0.6，根据实际情况调整

        dialogWindow.setAttributes(p);

        Button cancel = (Button) layout.findViewById(R.id.negativeButton);
        Button sure = (Button) layout.findViewById(R.id.positiveButton);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                TelephonyManager tm = (TelephonyManager) NewStartAct.this.getSystemService(TELEPHONY_SERVICE);
                try {
                    BaseParam.DEVICE_ID = tm.getDeviceId();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                imei = AppTool.imeiSave(NewStartAct.this);   //得到手机的imei号
                imei = imei + "-qian-" + Installation.id(NewStartAct.this);   //加了一串随机数
                Log.i(TAG, "失败:imei:" + imei);
                Log.i(TAG, "失败:BaseParam.DEVICE_IDi:" + BaseParam.DEVICE_ID);

                next();
            }
        });
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent localIntent = new Intent();

                localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                localIntent.setData(Uri.fromParts("package", getPackageName(), null));
                startActivityForResult(localIntent, 1);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            requestPermission();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
    }

    @SuppressWarnings("deprecation")
    private void initView() {
        mScrollLayout = (MyScrollLayout) findViewById(R.id.ScrollLayout);
        pointLLayout = (LinearLayout) findViewById(R.id.llayout);
        RelativeLayout app5 = (RelativeLayout) findViewById(R.id.app5);
        app5.setOnClickListener(onClick);
        count = mScrollLayout.getChildCount();
        imgs = new ImageView[count];
        for (int i = 0; i < count; i++) {
            imgs[i] = (ImageView) pointLLayout.getChildAt(i);
            imgs[i].setEnabled(true);
            imgs[i].setTag(i);
        }
        currentItem = 0;
        imgs[currentItem].setEnabled(false);
        mScrollLayout.SetOnViewChangeListener(this);
    }

    private View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.app5:
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("istart", "yes");
                    editor.putString("versionName", BaseParam.getVersionName(NewStartAct.this));
                    editor.commit();
                    Intent intent = new Intent(NewStartAct.this, MainTabAct.class);
                    myApp.isfirst = true; //是第一次进来
                    NewStartAct.this.startActivity(intent);
                    NewStartAct.this.finish();
                    break;
            }
        }
    };

    public boolean dispatchKeyEvent(KeyEvent event) {
        if ((event.getAction() == KeyEvent.ACTION_DOWN) && (event.getKeyCode() == KeyEvent.KEYCODE_BACK)) {
            this.finish();
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void OnViewChange(int position) {
        setcurrentPoint(position);
    }

    private void setcurrentPoint(int position) {
        if (position < 0 || position > count - 1 || currentItem == position) {
            return;
        }
        imgs[currentItem].setEnabled(true);
        imgs[position].setEnabled(false);
        currentItem = position;
    }

    /**
     * 推广接口 使用的是imei
     */
    private void startDataRequest() {
        Log.e("NewStartAct", "走了推广接口");
        NewStartAct.this.initArray();
        NewStartAct.this.param.add(BaseParam.URL_QIAN_API_APPID);   //appId
        NewStartAct.this.value.add(appId);
        NewStartAct.this.param.add(BaseParam.URL_QIAN_API_SERVICE);  //service
        NewStartAct.this.value.add("promotionStat");
        NewStartAct.this.param.add(BaseParam.URL_QIAN_API_SIGNTYPE); //signType
        NewStartAct.this.value.add(myApp.signType);
        NewStartAct.this.param.add("source");                       //source
        NewStartAct.this.value.add(BaseParam.getChannelCode(context));
        NewStartAct.this.param.add("idfa");                         //idfa
        NewStartAct.this.value.add(imei);

        Log.e("source--source", BaseParam.getChannelCode(context));


        //appId service、signType、Sign
        String[] array = new String[]{
                BaseParam.URL_QIAN_API_APPID + "=" + appId,
                BaseParam.URL_QIAN_API_SERVICE + "=promotionStat",
                BaseParam.URL_QIAN_API_SIGNTYPE + "=" + myApp.signType,
                "source=" + BaseParam.getChannelCode(context),
                "idfa=" + imei};

        String sign = apiModel.sortStringArray(array);
        NewStartAct.this.param.add(BaseParam.URL_QIAN_API_SIGN);
        NewStartAct.this.value.add(sign);
        new Thread(new JsonRequestThreadDefault(
                NewStartAct.this,
                myApp,
                NewStartAct.this.param,
                NewStartAct.this.value)
        ).start();


    }

    private void initArray() {
        this.param = null;
        this.value = null;
        this.param = new ArrayList();
        this.value = new ArrayList();
        System.gc();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        parseIntent();
    }

    public static void consultService(final Context context, String uri, String title, ProductDetail productDetail) {
        if (!Unicorn.isServiceAvailable()) {
            // 当前客服服务不可用
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            if (!Check.isNetworkAvailable(context)) {
                // 当前无网络
                dialog.setMessage("网络状况不佳，请重试。");
                dialog.setPositiveButton("确定", null);
            }
            return;
        }

        // 启动聊天界面
        ConsultSource source = new ConsultSource(uri, title, null);
        source.productDetail = productDetail;
        Unicorn.openServiceActivity(context, staffName(), source);
    }

    private static String staffName() {
        return "钱内助客服";
    }

    /**
     * FC不保存状态，防止重启后fragment重复添加
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    }

    private void parseIntent() {
        Intent intent = getIntent();
        if (intent.hasExtra(NimIntent.EXTRA_NOTIFY_CONTENT)) {
            consultService(this, null, null, null);
            // 最好将intent清掉，以免从堆栈恢复时又打开客服窗口
            setIntent(new Intent());
        }
    }


}
