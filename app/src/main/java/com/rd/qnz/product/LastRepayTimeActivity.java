package com.rd.qnz.product;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.rd.qnz.BaseActivity;
import com.rd.qnz.R;

/**
 * 提前还款
 * Created by Evonne on 2016/9/12.
 */
public class LastRepayTimeActivity extends BaseActivity {

    private String lastRepayTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_lastrepaytime);
        initBar();

//        lastRepayTime = getIntent().getStringExtra("lastRepayTime");
//        TextView repayTime = (TextView) findViewById(R.id.lastRepayTime);
//        repayTime.setText("本项目默认还款日：" + new SimpleDateFormat("yyyy-MM-dd").format(new Date(Long.valueOf(lastRepayTime))));
    }

    private void initBar() {
        ImageView actionbar_side_left_iconfont = (ImageView) findViewById(R.id.actionbar_side_left_iconfont);
        actionbar_side_left_iconfont.setVisibility(View.VISIBLE);
        actionbar_side_left_iconfont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                finish();
            }
        });
        TextView actionbar_side_name = (TextView) findViewById(R.id.actionbar_side_name);
        actionbar_side_name.setText("提前还款");
    }

}
