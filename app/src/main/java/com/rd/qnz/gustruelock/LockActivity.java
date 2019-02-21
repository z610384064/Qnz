package com.rd.qnz.gustruelock;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v4.os.CancellationSignal;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.rd.qnz.R;
import com.rd.qnz.custom.MyApplication;
import com.rd.qnz.fingerprint.CryptoObjectHelper;
import com.rd.qnz.fingerprint.MyAuthCallback;
import com.rd.qnz.gustruelock.LockPatternView.Cell;
import com.rd.qnz.gustruelock.LockPatternView.DisplayMode;
import com.rd.qnz.login.Login;
import com.rd.qnz.tools.BaseParam;
import com.zhy.autolayout.AutoLayoutActivity;

import java.util.List;


/**
 * 解锁界面
 */
public class LockActivity extends AutoLayoutActivity implements LockPatternView.OnPatternListener {

    private static final String TAG = "LockActivity";
    private List<Cell> lockPattern;
    private LockPatternView lockPatternView;
    private TextView forget;
    private TextView other, fingerprint;
    private TextView count;
    private TextView name;
    private TextView forget_center;
    private int size = 5;
    private MyApplication myApp;
    private FingerprintManagerCompat fingerprintManager = null;
    private MyAuthCallback myAuthCallback = null;
    private Handler handler = null;

    private TextView message;
    private Dialog dialog;
    private boolean fingerconfirm;

    public static final int MSG_AUTH_SUCCESS = 100;
    public static final int MSG_AUTH_FAILED = 101;
    public static final int MSG_AUTH_ERROR = 102;
    public static final int MSG_AUTH_HELP = 103;

    private CancellationSignal cancellationSignal = null;

    private CryptoObjectHelper cryptoObjectHelper;
    private ImageView mine_info_portrait_iv;
    /**
     * 1.6.0新增用户头像
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //先判断一下lock.xml文件里面的lock_key字段,看有没有设置手势密码

        SharedPreferences preferences = getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_LOCK, MODE_PRIVATE);
        String patternString = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_LOCKKEY, null);

        if (patternString == null||patternString.equals("")) {
            finish();
            return;
        }

        IntentFilter intentFilter=new IntentFilter("login");
        registerReceiver(myBroadcastReceiver,intentFilter);

        myApp = (MyApplication) getApplication();
        lockPattern = LockPatternView.stringToPattern(patternString);
        setContentView(R.layout.activity_lock);
        mine_info_portrait_iv= (ImageView) findViewById(R.id.mine_info_portrait_iv);
        mine_info_portrait_iv.setImageResource(R.drawable.person);

        if (myApp.bt_head!=null){
            mine_info_portrait_iv.setImageBitmap(myApp.bt_head);
        }else  if (!TextUtils.isEmpty(myApp.head_url))
        {

            ImageLoader.getInstance().displayImage(myApp.head_url, mine_info_portrait_iv, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String s, View view) {

                }

                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {
                    myApp.bt_head=null;
                    mine_info_portrait_iv.setImageResource(R.drawable.person);
                }

                @Override
                public void onLoadingComplete(String s, View view, Bitmap bitmap) {

                    myApp.bt_head=bitmap;
                    mine_info_portrait_iv.setImageBitmap(bitmap);
                }

                @Override
                public void onLoadingCancelled(String s, View view) {

                }
            });


        }

        lockPatternView = (LockPatternView) findViewById(R.id.lock_pattern);
        count = (TextView) findViewById(R.id.count); //请输入解锁手势(随着输入内容的变化而变化)
        lockPatternView.setOnPatternListener(this);

        forget = (TextView) findViewById(R.id.forget);  //忘记密码
        other = (TextView) findViewById(R.id.other);  //其它账号登录
        fingerprint = (TextView) findViewById(R.id.fingerprint); //指纹解锁
        name = (TextView) findViewById(R.id.name);  //手机号
        forget_center= (TextView) findViewById(R.id.forget_center); //位于中间的 忘记密码

        //sp_user.xml
        SharedPreferences sp = LockActivity.this.getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_USER, MODE_PRIVATE);
        String phone = sp.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_PHONE, "钱内助欢迎你!");
        String userName = sp.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_USERNAME, "钱内助欢迎你!");


        if (phone.equals("")) {
            name.setText(userName);
        } else {
            name.setText(replaceChar(phone));
        }

        forget.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
//                showDialog("忘记密码,你需要重新登录");
                Intent i= new Intent(LockActivity.this, Login.class);
                i.putExtra("forget",true);
                startActivity(i);
                myApp.homepage = false;
                finish();
            }
        });
        forget_center.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
//                showDialog("忘记密码,你需要重新登录");
                Intent i= new Intent(LockActivity.this, Login.class);
                i.putExtra("forget",true);
                startActivity(i);
                myApp.homepage = false;
                finish();
            }
        });

        other.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                showDialog("切换账号,你需要重新登录");
            }
        });


        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                Log.d(TAG, "msg: " + msg.what + " ,arg1: " + msg.arg1);
                switch (msg.what) {
                    case MSG_AUTH_SUCCESS:
                        message.setText(R.string.fingerprint_success);
                        dialog.dismiss();
                        myApp.time = (int) (System.currentTimeMillis() / 1000);
                        myApp.isSeccess = 0;
                        finish();
                        cancellationSignal = null;
                        break;
                    case MSG_AUTH_FAILED:
                        message.setText(R.string.fingerprint_not_recognized);
                        cancellationSignal = null;
                        break;
                    case MODE_PRIVATE:
                        break;
                    case MSG_AUTH_ERROR:
                        handleErrorCode(msg.arg1);
                        break;
                    case MSG_AUTH_HELP:
                        handleHelpCode(msg.arg1);
                        break;
                }
            }
        };

        fingerprintManager = FingerprintManagerCompat.from(this);   //指纹识别管理类
        fingerconfirm=sp.getBoolean("fingerconfirm",false); //指纹开关是开着还是关闭的

            if (!fingerprintManager.isHardwareDetected()) {//没有指纹传感器
                forget.setVisibility(View.GONE);
                fingerprint.setVisibility(View.GONE);
                forget_center.setVisibility(View.VISIBLE);
            } else if (!fingerprintManager.hasEnrolledFingerprints()) {//没有指纹登记
                forget.setVisibility(View.GONE);
                fingerprint.setVisibility(View.GONE);
                forget_center.setVisibility(View.VISIBLE);
            }else  if (!fingerconfirm){
                //指纹开关没打开,就什么都不做
            }
            else {
                try {
                    myAuthCallback = new MyAuthCallback(handler);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.e("@@@222", "去指纹解锁");

                //弹出一个对话框,让用户去指纹识别
                LayoutInflater inflaterDl = LayoutInflater.from(LockActivity.this);
                LinearLayout layout = (LinearLayout) inflaterDl.inflate(R.layout.dialog_fingerprint_layout, null);
                dialog = new android.app.AlertDialog.Builder(LockActivity.this).create();
                dialog.show();
                dialog.getWindow().setContentView(layout);
                dialog.setCanceledOnTouchOutside(false);
                dialog.setCancelable(false);
                message = (TextView) layout.findViewById(R.id.message);
                TextView cancel = (TextView) layout.findViewById(R.id.negativeButton);

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                try {
                    cryptoObjectHelper = new CryptoObjectHelper();
                    if (cancellationSignal == null) {
                        cancellationSignal = new CancellationSignal();
                    }
                    fingerprintManager.authenticate(cryptoObjectHelper.buildCryptoObject(), 0,
                            cancellationSignal, myAuthCallback, null);
                    // set button state.
                    Log.e("@@@", "@@@");
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(LockActivity.this, "指纹初始化失败！再试一次!", Toast.LENGTH_SHORT).show();
                }
            }





        fingerprint.setOnClickListener(new OnClickListener() {  //点击指纹解锁这个文本
            @Override
            public void onClick(View v) {
                if (!fingerprintManager.isHardwareDetected()) {//没有指纹传感器
                    // no fingerprint sensor is detected, show dialog to tell user.
                    LayoutInflater inflaterDl = LayoutInflater.from(LockActivity.this);
                    LinearLayout layout = (LinearLayout) inflaterDl.inflate(R.layout.dialog_fingerprint_warning, null);
                    dialog = new android.app.AlertDialog.Builder(LockActivity.this).create();
                    dialog.show();
                    dialog.getWindow().setContentView(layout);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.setCancelable(false);
                    TextView message= (TextView) layout.findViewById(R.id.message);
                    message.setText(R.string.no_sensor_dialog_title);
                    Button cancel = (Button) layout.findViewById(R.id.warning);
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                } else if (!fingerprintManager.hasEnrolledFingerprints()) {//没有指纹登记
                    // no fingerprint image has been enrolled.
                    LayoutInflater inflaterDl = LayoutInflater.from(LockActivity.this);
                    LinearLayout layout = (LinearLayout) inflaterDl.inflate(R.layout.dialog_fingerprint_warning, null);
                    dialog = new android.app.AlertDialog.Builder(LockActivity.this).create();
                    dialog.show();
                    dialog.getWindow().setContentView(layout);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.setCancelable(false);
                    TextView message= (TextView) layout.findViewById(R.id.message);
                    message.setText(R.string.no_fingerprint_enrolled_dialog_title);
                    Button cancel = (Button) layout.findViewById(R.id.warning);
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                } else {
                    try {
                        myAuthCallback = new MyAuthCallback(handler);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Log.e("@@@", "去指纹解锁");

                    SharedPreferences sp = getApplication().getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_USER, MODE_PRIVATE);
                    Editor editor = sp.edit();//存入数据
                    editor.putBoolean("fingerconfirm", true);  //打开指纹解锁的开关
                    editor.commit();

                    LayoutInflater inflaterDl = LayoutInflater.from(LockActivity.this);
                    LinearLayout layout = (LinearLayout) inflaterDl.inflate(R.layout.dialog_fingerprint_layout, null);
                    dialog = new android.app.AlertDialog.Builder(LockActivity.this).create();
                    dialog.show();
                    dialog.getWindow().setContentView(layout);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.setCancelable(false);
                    message = (TextView) layout.findViewById(R.id.message);
                    TextView cancel = (TextView) layout.findViewById(R.id.negativeButton);

                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            SharedPreferences sp = getApplication().getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_USER, MODE_PRIVATE);
                            Editor editor = sp.edit();//存入数据
                            editor.putBoolean("fingerconfirm", false);
                            editor.commit();
                        }
                    });

                    // start fingerprint auth here.
                    try {
                        cryptoObjectHelper = new CryptoObjectHelper();
                        if (cancellationSignal == null) {
                            cancellationSignal = new CancellationSignal();
                        }
                        fingerprintManager.authenticate(cryptoObjectHelper.buildCryptoObject(), 0,
                                cancellationSignal, myAuthCallback, null);
                        // set button state.
                        // mStartBtn.setEnabled(false);
                        // mCancelBtn.setEnabled(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(LockActivity.this, "指纹初始化失败！再试一次!", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

    }

    BroadcastReceiver myBroadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action=intent.getAction();
            if (action.equals("login")){ //从解锁界面跳到登录界面,等待登录成功发来广播关闭解锁界面
                LockActivity.this.finish();
           }
        }
    };
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myBroadcastReceiver!=null){
            try{
                unregisterReceiver(myBroadcastReceiver);
                myBroadcastReceiver=null;
            }catch (Exception e){
                e.printStackTrace();
            }

        }
        if (cancellationSignal != null) {
            cancellationSignal.cancel();
        }
    }

    private void handleHelpCode(int code) {
        switch (code) {
            case FingerprintManager.FINGERPRINT_ACQUIRED_GOOD:
                message.setText(R.string.AcquiredGood_warning);
                break;
            case FingerprintManager.FINGERPRINT_ACQUIRED_IMAGER_DIRTY:
                message.setText(R.string.AcquiredImageDirty_warning);
                break;
            case FingerprintManager.FINGERPRINT_ACQUIRED_INSUFFICIENT:
                message.setText(R.string.AcquiredInsufficient_warning);
                break;
            case FingerprintManager.FINGERPRINT_ACQUIRED_PARTIAL:
                message.setText(R.string.AcquiredPartial_warning);
                break;
            case FingerprintManager.FINGERPRINT_ACQUIRED_TOO_FAST:
                message.setText(R.string.AcquiredTooFast_warning);
                break;
            case FingerprintManager.FINGERPRINT_ACQUIRED_TOO_SLOW:
                message.setText(R.string.AcquiredToSlow_warning);
                break;
        }
    }

    private void handleErrorCode(int code) {
        switch (code) {
            case FingerprintManager.FINGERPRINT_ERROR_CANCELED:
                message.setText(R.string.ErrorCanceled_warning);
                break;
            case FingerprintManager.FINGERPRINT_ERROR_HW_UNAVAILABLE:
                message.setText(R.string.ErrorHwUnavailable_warning);
                break;
            /*case FingerprintManager.FINGERPRINT_ERROR_LOCKOUT:
                setResultInfo(R.string.ErrorLockout_warning);
                break;*/
            case FingerprintManager.FINGERPRINT_ERROR_NO_SPACE:
                message.setText(R.string.ErrorNoSpace_warning);
                break;
            case FingerprintManager.FINGERPRINT_ERROR_TIMEOUT:
                message.setText(R.string.ErrorTimeout_warning);
                break;
            case FingerprintManager.FINGERPRINT_ERROR_UNABLE_TO_PROCESS:
                message.setText(R.string.ErrorUnableToProcess_warning);
                break;
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        // disable back key
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private void showDialog(String content) {

        LayoutInflater inflaterDl = LayoutInflater.from(LockActivity.this);
        RelativeLayout layout = (RelativeLayout) inflaterDl.inflate(R.layout.my_dialog_lock, null);
        final Dialog dialog = new AlertDialog.Builder(LockActivity.this).create();
        dialog.show();
        dialog.getWindow().setContentView(layout);
        TextView title = (TextView) dialog.findViewById(R.id.title);
        title.setText(content);
        Button confirm = (Button) dialog.findViewById(R.id.confirm);
        Button cancel = (Button) dialog.findViewById(R.id.cancel);
        confirm.setOnClickListener(new OnClickListener() {  //点击确认按钮,跳到登录界面去重新登录
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                startActivity(new Intent(LockActivity.this, Login.class));
                myApp.homepage = false;
                dialog.dismiss();
                finish();
            }
        });
        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onPatternStart() {
        Log.d(TAG, "onPatternStart");
    }

    @Override
    public void onPatternCleared() {
        Log.d(TAG, "onPatternCleared");
    }

    @Override
    public void onPatternCellAdded(List<Cell> pattern) {
        Log.d(TAG, "onPatternCellAdded");
        Log.e(TAG, LockPatternView.patternToString(pattern));
        // Toast.makeText(this, LockPatternView.patternToString(pattern),
        // Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPatternDetected(List<Cell> pattern) {
        Log.i(TAG, "onPatternDetected---图案检测");

        if (pattern.equals(lockPattern)) {
            myApp.time = (int) (System.currentTimeMillis() / 1000);
            myApp.isSeccess = 0;
            finish();
        } else {
            lockPatternView.setDisplayMode(DisplayMode.Wrong);
            if (pattern.size() < LockPatternView.MIN_LOCK_PATTERN_SIZE) {
                count.setText(R.string.lockpattern_recording_incorrect_too_short);
                lockPatternView.clearPattern();
                lockPatternView.enableInput();
                lockPatternView.setDisplayMode(DisplayMode.Wrong);
                return;
            } else {
                size--;
                count.setText("密码不正确,你还以尝试" + size + "次");
                lockPatternView.clearPattern();
                lockPatternView.enableInput();
            }
            if (size <= 0) {
                Context ctx = LockActivity.this;
                SharedPreferences sp = ctx.getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_USER, MODE_PRIVATE);
                //存入数据
                Editor editor = sp.edit();
                editor.putString(BaseParam.QIAN_SHAREDPREFERENCES_USER_EXPIRES, "");
                editor.putString(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN, "");
//			    editor.putString(BaseParam.QIAN_SHAREDPREFERENCES_USER_USERMONEY, "");
                editor.putString(BaseParam.QIAN_SHAREDPREFERENCES_USER_REALSTATUS, "");
                editor.putString(BaseParam.QIAN_SHAREDPREFERENCES_USER_PHONESTATUS, "");
                editor.putString(BaseParam.QIAN_SHAREDPREFERENCES_USER_EMAILSTATUS, "");
                editor.putString(BaseParam.QIAN_SHAREDPREFERENCES_USER_REFRESHTOKEN, "");
                editor.putString(BaseParam.QIAN_SHAREDPREFERENCES_USER_BANKSTATUS, "");
                editor.putString(BaseParam.QIAN_SHAREDPREFERENCES_USER_PAYPWDFLAG, "");
                editor.putString(BaseParam.QIAN_SHAREDPREFERENCES_USER_USERID, "");
                editor.putString(BaseParam.QIAN_SHAREDPREFERENCES_USER_REALNAME, "");
                editor.commit();
                startActivity(new Intent(LockActivity.this, Login.class));
                finish();
            }
//            Toast.makeText(this, R.string.lockpattern_error, Toast.LENGTH_LONG).show();
        }

    }

    private static String replaceChar(String temp) {
        return temp.replace(temp.substring(3, 7), "****");
    }

}
