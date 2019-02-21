package com.rd.qnz.mine;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rd.qnz.BaseActivity;
import com.rd.qnz.NewStartAct;
import com.rd.qnz.R;

/**
 * 1.6.0联系客服
 */
public class ServerAct extends BaseActivity implements View.OnClickListener{
    private RelativeLayout rl_online,rl_phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);
        setSwipeBackEnable(false);
        initBar();
        rl_online= (RelativeLayout) findViewById(R.id.rl_online);
        rl_phone= (RelativeLayout) findViewById(R.id.rl_phone);
        rl_online.setOnClickListener(this);
        rl_phone.setOnClickListener(this);

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
        actionbar_side_name.setText("联系客服");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_online: //
                NewStartAct.consultService(ServerAct.this, null, null, null);
                break;
            case R.id.rl_phone:
                Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "400-000-9810"));
                callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(callIntent);
                break;
        }
    }
}
