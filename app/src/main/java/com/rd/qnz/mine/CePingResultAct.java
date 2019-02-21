package com.rd.qnz.mine;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.rd.qnz.R;
import com.rd.qnz.custom.MyApplication;

public class CePingResultAct extends Activity implements View.OnClickListener{
    private TextView tv_dengji,tv_descibe;
    private Button btn_touzi,btn_ceping;
    private int score;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ce_ping_result);
         score= getIntent().getIntExtra("score",0);

        tv_dengji= (TextView) findViewById(R.id.tv_dengji);
        tv_descibe= (TextView) findViewById(R.id.tv_descibe);
        btn_touzi= (Button) findViewById(R.id.btn_touzi);
        btn_ceping= (Button) findViewById(R.id.btn_ceping);
        btn_touzi.setOnClickListener(this);
        btn_ceping.setOnClickListener(this);
        if (score<=14){
            tv_dengji.setText("保守型");
            tv_descibe.setText("结果分析：您往往很在意资产的流动性,不希望看到本金收到损失的发生. ");
        }else if (score>=15&&score<=21){
            tv_dengji.setText("稳健型");
            tv_descibe.setText("结果分析：您对风险有一定的认识,希望在保证资本稳定性下获得增值收入.");
        }else if (score>=22&&score<=28){
            tv_descibe.setText("结果分析：您的投资行为较为活跃，能在投资操作中接受一定的风险性。");
            tv_dengji.setText("激进型");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_touzi:
                MyApplication myApp=MyApplication.getInstance();
                myApp.tabHostId = 1;
                myApp.tabHost.setCurrentTab(myApp.tabHostId);   //跳转到投资界面
                setResult(2);
                finish();
                break;
            case R.id.btn_ceping:
                Intent i=new Intent(CePingResultAct.this,FengxianTestAct.class);
                startActivity(i);
                finish();
                break;
        }
    }
}
