package com.rd.qnz.mine;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.rd.qnz.BaseActivity;
import com.rd.qnz.R;
import com.rd.qnz.gustruelock.CheckLockActivity;
import com.rd.qnz.gustruelock.CheckLockActivity2;
import com.rd.qnz.gustruelock.LockSetupActivity;
import com.rd.qnz.tools.BaseParam;

/**
 * 1.6.0管理手势密码界面
 */


public class UpdateShouShiPasswordAct extends BaseActivity implements View.OnClickListener,CompoundButton.OnCheckedChangeListener {
    private RelativeLayout change_password ;//修改手势密码
    private RelativeLayout finger_off; //指纹锁对应的相对布局
    private ToggleButton tb_zws; //开启指纹锁
    private ToggleButton tb_ssmm; //手势密码(开启关闭)

    private TextView actionbar_side_name; //标题
    private ImageView actionbar_side_left_iconfont; //向左的图标
    private SharedPreferences  preferences;
    private String lock_key;   //sp文件里面保存的手势密码
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myhome_management_update_zhiwen);
        initView();

    }

    private void initView() {

        change_password= (RelativeLayout) findViewById(R.id.change_password); //修改手势密码
        finger_off= (RelativeLayout) findViewById(R.id.finger_off);   //开启关闭指纹锁
        tb_zws= (ToggleButton) findViewById(R.id.tb_zws);  //指纹锁开关
        tb_ssmm= (ToggleButton) findViewById(R.id.tb_ssmm);//手势密码开关

        actionbar_side_left_iconfont= (ImageView) findViewById(R.id.actionbar_side_left_iconfont);
        actionbar_side_left_iconfont.setVisibility(View.VISIBLE);
        actionbar_side_left_iconfont.setOnClickListener(this);

        actionbar_side_name= (TextView) findViewById(R.id.actionbar_side_name);
        actionbar_side_name.setText("管理手势密码");

        tb_ssmm.setOnCheckedChangeListener(this);
        change_password.setOnClickListener(this);
        tb_zws.setOnCheckedChangeListener(this);// 添加监听事件
            refreshView();

    }
    public void refreshView(){

        preferences=getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_LOCK, Context.MODE_PRIVATE);
        lock_key=preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_LOCKKEY,""); //是否设置了手势密码

        SharedPreferences     sp = getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_USER, MODE_PRIVATE);
        tb_zws.setChecked(sp.getBoolean("fingerconfirm", false)); //指纹锁是开启还是关闭

        if (TextUtils.isEmpty(lock_key)){  //手势密码为空,隐藏下面两行
            change_password.setVisibility(View.GONE);
            finger_off.setVisibility(View.GONE);
            tb_ssmm.setChecked(false);
        }else {  //手势密码不为空,判断是否支持指纹 再做界面显示
            change_password.setVisibility(View.VISIBLE);
            //判断是否需要指纹,不需要的话就隐藏这一行
            FingerprintManagerCompat fingerprintManager = FingerprintManagerCompat.from(this);
            if (!fingerprintManager.isHardwareDetected()) {//不支持指纹硬件
                finger_off.setVisibility(View.GONE);
            } else if (!fingerprintManager.hasEnrolledFingerprints()) {//手机没有录入指纹登记
                finger_off.setVisibility(View.GONE);
            } else {
                finger_off.setVisibility(View.VISIBLE);
            }
            tb_ssmm.setChecked(true);
        }



    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i("UpdateShouShi"," onRestart()调用");
//        Toast.makeText(this, "UpdateShouShiPasswordAct  onRestart()调用", Toast.LENGTH_SHORT).show();
        refreshView();

    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.change_password:   //修改手势密码
//                Toast.makeText(UpdateShouShiPasswordAct.this, "点击了修改手势密码", Toast.LENGTH_LONG).show();
                //先去验证原来的手势密码,然后进行新的手势密码设置
                Intent i=new Intent(UpdateShouShiPasswordAct.this, CheckLockActivity.class);
                startActivityForResult(i,0);

                break;
            case R.id.actionbar_side_left_iconfont:  //点击左上角图标
                setResult(RESULT_OK);
                finish();
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        preferences=getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_LOCK, Context.MODE_PRIVATE);
        lock_key=preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_LOCKKEY,""); //是否设置了手势密码

        switch (buttonView.getId()){
            case R.id.tb_ssmm:
                if (isChecked) {  //开启手势密码,去创建手势密码
                    if (!TextUtils.isEmpty(lock_key)){
                        return;
                    }
                    Intent i=new Intent(UpdateShouShiPasswordAct.this, LockSetupActivity.class);
                    startActivity(i);
                } else {  //关闭手势密码,先去解锁
                    if (TextUtils.isEmpty(lock_key)){  //如果手势密码为空
                        return;
                    }else {
                        Intent i=new Intent(UpdateShouShiPasswordAct.this, CheckLockActivity2.class);
                        startActivityForResult(i,1);
                    }

                }


                break;

            case R.id.tb_zws: //开启关闭指纹锁
                SharedPreferences     sp = getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_USER, MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();//存入数据
                if (isChecked) {
                    editor.putBoolean("fingerconfirm", true);
                } else {
                    editor.putBoolean("fingerconfirm", false);
                }
                editor.commit();
            break;


    }
}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode){
            case 0: //从设置手势密码界面返回来的
                SharedPreferences   sp = getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_USER, MODE_PRIVATE);
                tb_zws.setChecked(sp.getBoolean("fingerconfirm", false)); //指纹锁是开启还是关闭
                break;
            case 1: //关闭手势密码的回调,之后清空sp里面的手势密码,隐藏下面两行,再退出
                switch (resultCode){
                    case 0: //没有进行原手势密码的校验
                        break;
                    case 1: //校验成功,清除手势密码,隐藏下面两行,并关闭页面
                        preferences=getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_LOCK, Context.MODE_PRIVATE);
                        preferences.edit().putString(BaseParam.QIAN_SHAREDPREFERENCES_LOCKKEY,"").commit(); //清除手势密码
                        change_password.setVisibility(View.GONE);
                        finger_off.setVisibility(View.GONE);
                        finish();
                        break;
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
