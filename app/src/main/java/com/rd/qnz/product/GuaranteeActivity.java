package com.rd.qnz.product;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rd.qnz.BaseActivity;
import com.rd.qnz.R;

/**
 * 安全保障
 * Created by Evonne on 2016/9/12.
 */
public class GuaranteeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guarantee);
        initBar();
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
        actionbar_side_name.setText("安全保障");
    }
}
