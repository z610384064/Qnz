package com.rd.qnz.gustruelock;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.os.CancellationSignal;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.widget.TextView;

import com.rd.qnz.R;
import com.rd.qnz.custom.MyApplication;
import com.rd.qnz.gustruelock.LockPatternView.Cell;
import com.rd.qnz.gustruelock.LockPatternView.DisplayMode;
import com.rd.qnz.login.Login;
import com.rd.qnz.tools.BaseParam;

import java.util.List;


/**
 * 重新设置手势密码 ,验证成功,去设置手势密码界面
 */
public class CheckLockActivity extends Activity implements LockPatternView.OnPatternListener {

    private static final String TAG = "LockActivity";
    private List<Cell> lockPattern;
    private LockPatternView lockPatternView;
    private TextView count;
    private int size = 5;
    private MyApplication myApp;

    private CancellationSignal cancellationSignal = null;


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
        myApp = (MyApplication) getApplication();
        lockPattern = LockPatternView.stringToPattern(patternString);
        setContentView(R.layout.activity_check_lock);


        lockPatternView = (LockPatternView) findViewById(R.id.lock_pattern);  //九宫格
        count = (TextView) findViewById(R.id.count); //请使用原手势密码验证
        lockPatternView.setOnPatternListener(this);




    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (cancellationSignal != null) {
            cancellationSignal.cancel();
        }
    }





    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {



        return super.onKeyDown(keyCode, event);
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

        if (pattern.equals(lockPattern)) {  //解锁成功
            myApp.time = (int) (System.currentTimeMillis() / 1000);
            myApp.isSeccess = 0;
            Intent i=new Intent(CheckLockActivity.this,LockSetupActivity.class); //重新设置手势密码
            startActivity(i);
            finish();
        } else {
            lockPatternView.setDisplayMode(DisplayMode.Wrong);
            if (pattern.size() < LockPatternView.MIN_LOCK_PATTERN_SIZE) {   //画的个数小鱼四个
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
                Context ctx = CheckLockActivity.this;
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
                startActivity(new Intent(CheckLockActivity.this, Login.class));
                finish();
            }
        }

    }

    private static String replaceChar(String temp) {
        return temp.replace(temp.substring(3, 7), "****");
    }

}
