package com.rd.qnz.gustruelock;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rd.qnz.MainTabAct;
import com.rd.qnz.R;
import com.rd.qnz.custom.MyApplication;
import com.rd.qnz.gustruelock.LockPatternView.Cell;
import com.rd.qnz.gustruelock.LockPatternView.DisplayMode;
import com.rd.qnz.tools.BaseParam;
import com.zhy.autolayout.AutoLayoutActivity;

import java.util.ArrayList;
import java.util.List;

/**
 *  1.6.0 新版本设置手势密码
 */
public class LockSetupActivity extends AutoLayoutActivity implements LockPatternView.OnPatternListener, OnClickListener {

    private static final String TAG = "LockSetupActivity";
    private LockPatternView lockPatternView;

    private static final int STEP_1 = 1; // 开始
    private static final int STEP_2 = 2; // 第一次设置手势完成
    private static final int STEP_3 = 3; // 按下继续按钮
    private static final int STEP_4 = 4; // 第二次设置手势完成
    private FingerprintManagerCompat fingerprintManager = null;

    private MyApplication myApp;

    private int step;
    private String reg;//是否是协议过来的

    private List<Cell> choosePattern;

    private boolean confirm = false;
    private TextView hzssmmTextView, backTextView;
    private Button resetTextView;
    private ImageView image1, image2, image3, image4, image5, image6, image7, image8, image9;
    private ImageView close; //关闭图标
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//设置标题不显示
        setContentView(R.layout.activity_lock_setup);
        initBar();
        reg = "";
        reg = getIntent().getStringExtra("reg");  //我从登录界面过来的,这个值为null

        myApp = (MyApplication) getApplication();

        lockPatternView = (LockPatternView) findViewById(R.id.lock_pattern);
        lockPatternView.setOnPatternListener(this);
        image1 = (ImageView) findViewById(R.id.imageView1);
        image2 = (ImageView) findViewById(R.id.imageView2);
        image3 = (ImageView) findViewById(R.id.imageView3);
        image4 = (ImageView) findViewById(R.id.imageView4);
        image5 = (ImageView) findViewById(R.id.imageView5);
        image6 = (ImageView) findViewById(R.id.imageView6);
        image7 = (ImageView) findViewById(R.id.imageView7);
        image8 = (ImageView) findViewById(R.id.imageView8);
        image9 = (ImageView) findViewById(R.id.imageView9);

        hzssmmTextView = (TextView) findViewById(R.id.hzssmmTextView);  //手势密码将在你开启程序时启动(至少连接4个点,请重试 )(确认手势)

        step = STEP_1;
        updateView();
    }
    private void initBar() {
        ImageView actionbar_side_left_iconfont= (ImageView) findViewById(R.id.actionbar_side_left_iconfont);
        actionbar_side_left_iconfont.setVisibility(View.VISIBLE);
        actionbar_side_left_iconfont.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView actionbar_side_name = (TextView) findViewById(R.id.actionbar_side_name);
        actionbar_side_name.setText("设置手势密码");
    }


    private void updateView() {
        switch (step) {
            case STEP_1:  //开始
                hzssmmTextView.setText("绘制解锁图案");
                choosePattern = null;
                confirm = false;
                lockPatternView.clearPattern();  //清空手势密码
                lockPatternView.enableInput();   //允许输入
                image1.setBackgroundResource(R.drawable.gesture_default);
                image2.setBackgroundResource(R.drawable.gesture_default);
                image3.setBackgroundResource(R.drawable.gesture_default);
                image4.setBackgroundResource(R.drawable.gesture_default);
                image5.setBackgroundResource(R.drawable.gesture_default);
                image6.setBackgroundResource(R.drawable.gesture_default);
                image7.setBackgroundResource(R.drawable.gesture_default);
                image8.setBackgroundResource(R.drawable.gesture_default);
                image9.setBackgroundResource(R.drawable.gesture_default);
                break;
            case STEP_2:   // 第一次设置手势完成
                //for循环绘制上面的九宫格图标
                for (int i = 0; i < choosePattern.size(); i++) {
                    if (choosePattern.get(i).getRow() == 0) {
                        if (choosePattern.get(i).getColumn() == 0) {
                            image1.setBackgroundResource(R.drawable.gesture_select);
                            continue;
                        } else if (choosePattern.get(i).getColumn() == 1) {
                            image2.setBackgroundResource(R.drawable.gesture_select);
                            continue;
                        } else if (choosePattern.get(i).getColumn() == 2) {
                            image3.setBackgroundResource(R.drawable.gesture_select);
                            continue;
                        }
                    } else if (choosePattern.get(i).getRow() == 1) {
                        if (choosePattern.get(i).getColumn() == 0) {
                            image4.setBackgroundResource(R.drawable.gesture_select);
                            continue;
                        } else if (choosePattern.get(i).getColumn() == 1) {
                            image5.setBackgroundResource(R.drawable.gesture_select);
                            continue;
                        } else if (choosePattern.get(i).getColumn() == 2) {
                            image6.setBackgroundResource(R.drawable.gesture_select);
                            continue;
                        }
                    } else if (choosePattern.get(i).getRow() == 2) {
                        if (choosePattern.get(i).getColumn() == 0) {
                            image7.setBackgroundResource(R.drawable.gesture_select);
                            continue;
                        } else if (choosePattern.get(i).getColumn() == 1) {
                            image8.setBackgroundResource(R.drawable.gesture_select);
                            continue;
                        } else if (choosePattern.get(i).getColumn() == 2) {
                            image9.setBackgroundResource(R.drawable.gesture_select);
                            continue;
                        }
                    }
                }

                getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_LOCK, MODE_PRIVATE).edit().putString(BaseParam.QIAN_SHAREDPREFERENCES_LOCKKEY, null).commit();
                hzssmmTextView.setText("请再次绘制解锁图案");

                lockPatternView.clearPattern();
                lockPatternView.enableInput();

                break;
            case STEP_3:   //按下继续按钮
                lockPatternView.clearPattern();
                lockPatternView.enableInput();
                break;
            case STEP_4:  // 第二次设置手势完成
                if (confirm) {     //如果两次手势一样,那么就把手势密码保存到sp里面
                    SharedPreferences preferences = getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_LOCK, MODE_PRIVATE);

                    SharedPreferences.Editor edit =preferences.edit();
                    edit.putString(BaseParam.QIAN_SHAREDPREFERENCES_LOCKKEY, LockPatternView.patternToString(choosePattern));
                    edit.commit();
                    setResult(RESULT_OK);


                    fingerprintManager = FingerprintManagerCompat.from(this);
                    if (!fingerprintManager.isHardwareDetected()) {//没有指纹传感器
                        getinit();
                        finish();
                        lockPatternView.disableInput();  //禁止输入
                    } else if (!fingerprintManager.hasEnrolledFingerprints()) {//手机还没有录入指纹
                        getinit();
                        finish();
                        lockPatternView.disableInput();
                    } else {   //弹出一个对话框,确认是否开启你的指纹锁
                        LayoutInflater inflaterDl = LayoutInflater.from(LockSetupActivity.this);
                        LinearLayout layout = (LinearLayout) inflaterDl.inflate(R.layout.dialog_fingerprint_confirm, null);
                        final Dialog dialog = new android.app.AlertDialog.Builder(LockSetupActivity.this).create();
                        dialog.show();
                        dialog.getWindow().setContentView(layout);
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.setCancelable(false);

                        Button cancel = (Button) layout.findViewById(R.id.no);
                        Button sure = (Button) layout.findViewById(R.id.yes);
                        //sp_user.xml
                        SharedPreferences sp = getApplication().getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_USER, MODE_PRIVATE);
                        final SharedPreferences.Editor editor = sp.edit();//存入数据

                        cancel.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                editor.putBoolean("fingerconfirm", false);
                                editor.commit();
                                Log.e("fingerconfirm11", " false");
                                getinit();
                                // finger_confirm = false;
                                finish();
                                lockPatternView.disableInput();
                            }
                        });
                        sure.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                editor.putBoolean("fingerconfirm", true);
                                editor.commit();
                                Log.e("fingerconfirm12", " true");
                                getinit();

                                finish();
                                lockPatternView.disableInput();
                            }
                        });

                    }


                } else {
                    hzssmmTextView.setText("与上次绘制不一致，请重新绘制");
                    lockPatternView.setDisplayMode(DisplayMode.Wrong);
                    lockPatternView.clearPattern();
                    lockPatternView.enableInput();
                    step = STEP_2;

                }

                break;

            default:
                break;
        }
    }

    private void getinit(){
        Log.e("走这里","走四方");
//        Intent i=new Intent("lock_set");
//        sendBroadcast(i);  //发送广播到手势密码管理界面
        if (!myApp.homepage) {
            myApp.isLock = true; //把它设置成true,这样进来的时候主界面就不需要去解锁了
            Intent intent = new Intent(this, MainTabAct.class);
            Bundle mBundle = new Bundle();
            if (reg != null) {
                mBundle.putString("account", "yes");
            } else {
                mBundle.putString("account", "no");
            }
            intent.putExtras(mBundle);
            startActivity(intent);//手势密码设置成功
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {


            default:
                break;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onPatternStart() {
        hzssmmTextView.setText("完成后松开手指");
//        Toast.makeText(myApp, "点击了屏幕", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPatternCleared() {
        //Log.d(TAG, "onPatternCleared");
//        Toast.makeText(myApp, "onPatternStart", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPatternCellAdded(List<Cell> pattern) { //有一个圆点被点击到就调用
        //Log.d(TAG, "onPatternCellAdded");
//        Toast.makeText(myApp, "onPatternCellAdded", Toast.LENGTH_SHORT).show();
    }

    /**
     *  每次滑动->松开就会调用这个方法
     * @param pattern The pattern.
     */
    @Override
    public void onPatternDetected(List<Cell> pattern) {


        if (pattern.size() < LockPatternView.MIN_LOCK_PATTERN_SIZE) {
            hzssmmTextView.setText(R.string.lockpattern_recording_incorrect_too_short);

            lockPatternView.clearPattern();
            lockPatternView.setDisplayMode(DisplayMode.Wrong);
            return;
        }

        if (choosePattern == null) {
            choosePattern = new ArrayList<Cell>(pattern);
            step = STEP_2;
            updateView();
            return;
        }

        if (choosePattern.equals(pattern)) {
            confirm = true;
        } else {
            confirm = false;
        }
        step = STEP_4;
        updateView();

    }


}
