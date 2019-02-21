package com.rd.qnz.mine.setting;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.orhanobut.logger.Logger;
import com.rd.qnz.BaseActivity;
import com.rd.qnz.R;
import com.rd.qnz.WebViewAct;
import com.rd.qnz.custom.APIModel;
import com.rd.qnz.custom.MineShow;
import com.rd.qnz.custom.MyApplication;
import com.rd.qnz.custom.Profile;
import com.rd.qnz.dialog.CustomProgressDialog;
import com.rd.qnz.dialog.GetDialog;
import com.rd.qnz.homepage.UpdateAppManager;
import com.rd.qnz.mine.NewRealAct;
import com.rd.qnz.mine.UpdateLoginPasswordAct;
import com.rd.qnz.mine.UpdatePasswordAct;
import com.rd.qnz.mine.UpdateShouShiPasswordAct;
import com.rd.qnz.tools.AppTool;
import com.rd.qnz.tools.BaseParam;
import com.rd.qnz.tools.Check;
import com.rd.qnz.xutils.Installation;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import okhttp3.Call;
import okhttp3.Response;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

import static com.rd.qnz.custom.Profile.TAG_USER_STATUE_NAME;


/**
 * 1.6.0 设置界面,之前的更多界面
 *
 * @author Evonne
 */
@RuntimePermissions
public class SettingAct extends BaseActivity implements IWXAPIEventHandler, OnClickListener {

    private static final String TAG = "社区";

    private MyApplication myApp;
    private String latestDate;
    private SharedPreferences preferences;
    private String oauthToken, user_phone, user_id, realStatus;
    private CustomProgressDialog progressDialog = null;
    private GetDialog dia;

    private String VersionAndroid = "", androidUrl = "";//版本号,url;
    private String android_action = "", android_content = "";//是否需要强制,信息;
    private String imei;
    private RelativeLayout user_exit;
    /**
     * 1.6.0
     * @param savedInstanceState
     */
    private RelativeLayout my_management_login_password,my_management_shoushi_password,my_management_my_change_pay_password_list;
    private String payPwdFlag;
    private TextView tv_jiaoyi;//请先认证
    private TextView tv_shoushi; //手势密码 开/关
    private String lock_key;   //sp文件里面保存的手势密码
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.community);
        myApp = (MyApplication) getApplication();


        this.dia = new GetDialog();
        imei = AppTool.imeiSave(SettingAct.this);
        imei = imei + "-qian-" + Installation.id(SettingAct.this);
        SharedPreferences preferences = getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_USER, Context.MODE_PRIVATE);
        payPwdFlag = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_PAYPWDFLAG, "");


        initBar();
        intView();

    }
    //在标题栏显示左边的图标,设置点击事件,改变中间的文字
    private void initBar() {
        ImageView actionbar_side_left_iconfont = (ImageView) findViewById(R.id.actionbar_side_left_iconfont);
        actionbar_side_left_iconfont.setVisibility(View.VISIBLE);
        actionbar_side_left_iconfont.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                finish();
            }
        });
        TextView actionbar_side_name = (TextView) findViewById(R.id.actionbar_side_name);
        actionbar_side_name.setText("设置");
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG); //统计页面
        MobclickAgent.onResume(this);  //统计时长
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(TAG); // 保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息
        MobclickAgent.onPause(this);
    }



    @Override
    protected void onRestart() {
        super.onRestart();
        intView();
        payPwdFlag = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_PAYPWDFLAG, "");
        Log.i(TAG, "onRestart");
    }

    public void intView() {
        preferences = getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_USER, Context.MODE_PRIVATE);
        latestDate = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_LATESTDATE_XIAOXI, "");
        oauthToken = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN, "");
        realStatus = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_REALSTATUS, "");
        user_phone = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_PHONE, "");
        user_id = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_USERID, "");
        tv_shoushi= (TextView) findViewById(R.id.tv_shoushi);
        preferences=getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_LOCK, Context.MODE_PRIVATE);
        lock_key=preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_LOCKKEY,""); //是否设置了手势密码
         /* 管理登录密码 */
        my_management_login_password = (RelativeLayout) findViewById(R.id.my_management_login_password);
        my_management_login_password.setOnClickListener(this);

        /* 管理手势密码*/
        my_management_shoushi_password = (RelativeLayout) findViewById(R.id.my_management_shoushi_password);


        my_management_shoushi_password.setOnClickListener(this);
        user_exit= (RelativeLayout) findViewById(R.id.user_exit);
        user_exit.setOnClickListener(this);
        /* 管理交易密码 */
        my_management_my_change_pay_password_list = (RelativeLayout) findViewById(R.id.my_management_my_change_pay_password_list);
        my_management_my_change_pay_password_list.setOnClickListener(this);

        if (TextUtils.isEmpty(lock_key)){  //手势密码为空,隐藏下面两行
            tv_shoushi.setText("关");
        }else {  //手势密码不为空,判断是否支持指纹 再做界面显示
            tv_shoushi.setText("开");
        }
        tv_jiaoyi= (TextView) findViewById(R.id.tv_jiaoyi);

        if (TextUtils.equals(Profile.getUserRealNameStatus(), "0")) {   //未实名认证 去实名认证
            tv_jiaoyi.setVisibility(View.VISIBLE);
        } else {  //已经实名认证 设置交易密码
            tv_jiaoyi.setVisibility(View.GONE);
            }


        TextView tv_updata = (TextView) findViewById(R.id.tv_updata);
        tv_updata.setText("V" + BaseParam.getVersionName(SettingAct.this));
        findViewById(R.id.tel).setOnClickListener(this);
        findViewById(R.id.qr_code).setOnClickListener(this);
        findViewById(R.id.more_about).setOnClickListener(this);
        findViewById(R.id.more_update).setOnClickListener(this);



    }


    // 微信发送请求到第三方应用时，会回调到该方法
    @Override
    public void onReq(BaseReq req) {
        // TODO
    }

    // 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
    @Override
    public void onResp(BaseResp resp) {
        String result = "";
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                result = "发送成功";
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                result = "取消";
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                result = "发送失败";
                break;
            default:
                result = "出现异常";
                break;
        }
        MineShow.toastShow(result, this);
    }

    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_exit: //退出登录
                logout();
                break;
            case R.id.qr_code:  /* 复制微信公众号二维码 跳到微信 */
                if (MineShow.fastClick()) {
                    LayoutInflater inflaterDl1 = LayoutInflater.from(SettingAct.this);
                    LinearLayout linearlayout = (LinearLayout) inflaterDl1.inflate(R.layout.dialog_code_layout, null);
                    final Dialog jump = new AlertDialog.Builder(SettingAct.this).create();
                    jump.show();
                    jump.getWindow().setContentView(linearlayout);
                    jump.setCanceledOnTouchOutside(true);
                    TextView qr_code = (TextView) linearlayout.findViewById(R.id.code_tv);
                    qr_code.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (Build.VERSION.SDK_INT > 11) {  //用手机复制文字
                                ClipboardManager c = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                                c.setPrimaryClip(ClipData.newPlainText(null, "qianneizhu"));
                            } else {
                                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                cm.setText("qianneizhu");
                            }
                            // 通过包名获取要跳转的app
                            final Intent tencent = getPackageManager().getLaunchIntentForPackage("com.tencent.mm");
                            if (tencent != null) {
                                progressDialog = dia.getLoginDialog(SettingAct.this, "已复制微信号，正在跳转..");
                                jump.dismiss();
                                progressDialog.show();
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            Thread.sleep(2000);
                                            startActivity(tencent);
                                            if (null != progressDialog && progressDialog.isShowing()) {
                                                progressDialog.dismiss();
                                            }
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).start();
                            } else {
                                showToast("未安装微信");
                            }
                        }
                    });
                }
                break;

            case R.id.my_management_login_password:  //管理登录密码
                if (MineShow.fastClick()) {
                /* 管理登录密码 */
                    Intent login_password = new Intent(SettingAct.this, UpdateLoginPasswordAct.class);
                    startActivity(login_password);
                }
                break;

            case R.id.my_management_shoushi_password: //管理手势密码
                if (MineShow.fastClick()) {
                    Intent i1 = new Intent(SettingAct.this, UpdateShouShiPasswordAct.class);
                    startActivityForResult(i1,5);
                }
                break;
            case R.id.my_management_my_change_pay_password_list:  //管理交易密码
                if (MineShow.fastClick()) {
                /* 管理交易密码 */
                    if (TextUtils.equals(Profile.getUserRealNameStatus(), "0")) {   //未实名认证 去实名认证
                        tv_jiaoyi.setVisibility(View.VISIBLE);
                        Intent intent = new Intent(SettingAct.this, NewRealAct.class);
                        startActivityForResult(intent, 1001);
                    } else {//已经实名认证 设置交易密码
                        tv_jiaoyi.setVisibility(View.GONE);
                        if (payPwdFlag.equals("0")) {
                        } else {
                            Intent pay_password = new Intent(SettingAct.this, UpdatePasswordAct.class);
                            pay_password.putExtra("login", "2");
                            startActivity(pay_password);
                        }
                    }
                }
                break;
            /* 电话 */
            case R.id.tel:
                LayoutInflater inflaterDl = LayoutInflater.from(SettingAct.this);
                LinearLayout layout = (LinearLayout) inflaterDl.inflate(R.layout.dialog_normal_layout, null);
                final Dialog dialog = new AlertDialog.Builder(SettingAct.this).create();
                dialog.show();
                dialog.getWindow().setContentView(layout);
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
                        Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "400-000-9810"));//call
                        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(callIntent);
                    }
                });
                break;

            /* 关于我们 */
            case R.id.more_about:
                if (MineShow.fastClick()) {
                    Intent about = new Intent(SettingAct.this, WebViewAct.class);
                    about.putExtra("web_url", BaseParam.URL_QIAN + "forward.html?returnUrl=aboutus");
                    about.putExtra("title", "关于钱内助");
                    startActivity(about);
                }
                break;
            // 检查更新
            case R.id.more_update:
                if (MineShow.fastClick()) {
                    getupdata();
                }
                break;

            default:
                break;
        }
    }
    /* 安全退出 */
    private void logout() {
        LayoutInflater inflaterDl = LayoutInflater.from(SettingAct.this);
        LinearLayout layout = (LinearLayout) inflaterDl.inflate(R.layout.dialog_logout_layout, null);
        final Dialog dialog = new AlertDialog.Builder(SettingAct.this).create();
        dialog.show();
        dialog.getWindow().setContentView(layout);
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
                loginOut();
            }
        });
    }

    private void loginOut() {
        Context ctx = SettingAct.this;
        SharedPreferences sp = ctx.getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_USER, MODE_PRIVATE);
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
        editor.putString(BaseParam.QIAN_SHAREDPREFERENCES_USER_PHONE, "");
        editor.putString(BaseParam.QIAN_MY_AVAYARURL, "");
        editor.putString("cardId", "");
        editor.putString("cardExist", "");
        editor.putBoolean("fingerconfirm", false);
        Profile.setUserShare("");
        editor.commit();


        SharedPreferences sp1 = getSharedPreferences(TAG_USER_STATUE_NAME, MODE_PRIVATE);
        sp1.edit().putString(BaseParam.QIAN_USER_STATUS_INFO_NEWHAND_STATUS,"").commit(); //清空是否是新手


        sendBroadcast(new Intent("loginout"));

        myApp.tabHostId = 0;
        myApp.bt_head=null; //清除缓存中的图片
        myApp.tabHost.setCurrentTab(myApp.tabHostId);
        finish();
    }
    private void update(String url, String name, String str, String type) {
//        UpdateAppManager updateManager = new UpdateAppManager(this);
//        updateManager.showUpdateDialog(url, name, CommunityAct.this, str, type);
        showUpdateDialog(url, name, SettingAct.this, str, type);
    }

    private String filename=""; //文件名 1.4.0
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
                    SharedPreferences sp = context.getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_USER, context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("up_data", filename);
                    editor.commit();
                }
            });
        }

        Button update = (Button) layout.findViewById(R.id.update);
        update.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {


                update_dialog.dismiss();

                if (Build.VERSION.SDK_INT >= 23) { //设备号是6.0及以上 动态判断权限
                    requestPermission();
                }else {
                    //弹框去更新
                    File downloadPath = new File(FILE_PATH);
                    if (downloadPath.exists()) {
                        //删除路径下的文件
                        File[] childFiles = downloadPath.listFiles();
                        for (int i = 0; i < childFiles.length; i++) {
                            childFiles[i].delete();
                        }
                    }
                    UpdateAppManager updateManager = new UpdateAppManager(SettingAct.this);
                    updateManager.showDownloadDialog(filename,url);


                }

            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        SettingActPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);

    }
    @TargetApi(Build.VERSION_CODES.M)
    private void requestPermission() {
        //申请权限
        SettingActPermissionsDispatcher.onPermitWithCheck(this);
    }
    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void onPermit() {
        File downloadPath = new File(FILE_PATH);
        if (downloadPath.exists()) {
            //删除路径下的文件
            File[] childFiles = downloadPath.listFiles();
            for (int i = 0; i < childFiles.length; i++) {
                childFiles[i].delete();
            }
        }
        UpdateAppManager updateManager = new UpdateAppManager(SettingAct.this);
        updateManager.showDownloadDialog(filename,download_url);

    }



    @OnShowRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void onShowIntroduce(final PermissionRequest request) {
                      request.proceed();
                        //再次执行请求
//        new android.support.v7.app.AlertDialog.Builder(this)
//                .setMessage("更新应用的话需要存储权限,亲 给个权限吧!")
//                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        //再次执行请求
//
//                    }
//                })
//                .show();


    }

    @OnPermissionDenied(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void onDenied() {
        showToast("获取权限失败,无法更新app");
//        Toast.makeText(myApp, , Toast.LENGTH_SHORT).show();
    }

    @OnNeverAskAgain(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void onNeverAskAgain() {
        LayoutInflater inflaterDl = LayoutInflater.from(SettingAct.this);
        LinearLayout layout = (LinearLayout) inflaterDl.inflate(R.layout.dialog_updateperssion_layout, null);
        final Dialog dialog = new AlertDialog.Builder(SettingAct.this).create();
        dialog.show();
        dialog.getWindow().setContentView(layout);

        Window dialogWindow = dialog.getWindow();
        WindowManager m = dialogWindow.getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高度
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        p.width = (int) (d.getWidth() * 0.7); // 宽度设置为屏幕的0.7，根据实际情况调整
        p.height = (int) (0.8*p.width); // 高度设置为屏幕的0.56，根据实际情况调整
        dialogWindow.setAttributes(p);



        dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==1){
            int permissionCheck= ContextCompat.checkSelfPermission(SettingAct.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permissionCheck!= PackageManager.PERMISSION_GRANTED){  //没拿到权限,啥也不做

            }else { //拿到权限了,去更新
                File downloadPath = new File(FILE_PATH);
                if (downloadPath.exists()) {
                    //删除路径下的文件
                    File[] childFiles = downloadPath.listFiles();
                    for (int i = 0; i < childFiles.length; i++) {
                        childFiles[i].delete();
                    }
                }
                UpdateAppManager updateManager = new UpdateAppManager(SettingAct.this);
                updateManager.showDownloadDialog(filename,download_url);
            }
//            requestPermission();  重新去请求权限
        }
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
                        Logger.json(s.toString());
                        try {
                            JSONObject object = new JSONObject(s);
                            int errorcode = object.getInt("resultCode");
                            if (errorcode == 1) {
                                JSONObject oj1 = new JSONObject(Check.jsonGetStringAnalysis(object, "resultData"));
                                JSONObject appVersion = new JSONObject(Check.jsonGetStringAnalysis(oj1, "appVersion"));

                                android_action = appVersion.getString("action");
                                android_content = appVersion.getString("content");
                                androidUrl = appVersion.getString("url");
                                VersionAndroid = appVersion.getString("version");
                                filename=VersionAndroid;
                                download_url=androidUrl;

                                int  server_version=Integer.parseInt(VersionAndroid.replace(".",""));
                                int  now_version=Integer.parseInt(getVersionCode().replace(".",""));
                                if (now_version!=server_version&&now_version<server_version){
                                    if (android_action != null && android_action.equals("2")) {
                                        update(androidUrl, VersionAndroid, android_content, android_action);
                                    }
                                }else {
                                    showToast("已经是最新版本！");
                                }

//                                if (!VersionAndroid.equals(getVersionCode())) {
//                                    if (android_action != null && android_action.equals("2")) {
//                                        update(androidUrl, VersionAndroid, android_content, android_action);
//                                    }
//                                } else {
//                                    showToast("已经是最新版本！");
//                                }
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
