package com.rd.qnz.mine.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rd.qnz.R;
import com.rd.qnz.bean.Last;
import com.rd.qnz.bean.Next;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Administrator on 2017/5/27 0027.
 */

public class CePing7Fragment extends Fragment implements View.OnClickListener {
    private Context context;
    private LinearLayout rl1,rl2,rl3,rl4;
    private ImageView  iv_select1,iv_select2,iv_select3,iv_select4;
    private TextView tv_last;
    private Button btn_send;
    private int fenshu;
    public CePing7Fragment(){}
    public CePing7Fragment(Context context){
        this.context=context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=View.inflate(context, R.layout.fragment_ceping7,null);
        rl1= (LinearLayout) view.findViewById(R.id.rl1);
        rl2= (LinearLayout) view.findViewById(R.id.rl2);
        rl3= (LinearLayout) view.findViewById(R.id.rl3);
        rl4= (LinearLayout) view.findViewById(R.id.rl4);
        iv_select1= (ImageView) view.findViewById(R.id.iv_select1);
        iv_select2= (ImageView) view.findViewById(R.id.iv_select2);
        iv_select3= (ImageView) view.findViewById(R.id.iv_select3);
        iv_select4= (ImageView) view.findViewById(R.id.iv_select4);
        btn_send= (Button) view.findViewById(R.id.btn_send);
        btn_send.setOnClickListener(this);
        rl1.setOnClickListener(this);
        rl2.setOnClickListener(this);
        rl3.setOnClickListener(this);
        rl4.setOnClickListener(this);
        tv_last= (TextView) view.findViewById(R.id.tv_last);
        tv_last.setOnClickListener(this);
        btn_send.setClickable(false);
        btn_send.setBackgroundResource(R.drawable.button_org_grauly);
        return view;
    }

    @Override
    public void onClick(View v) {
        iv_select1.setImageResource(R.drawable.ceping_default);
        iv_select2.setImageResource(R.drawable.ceping_default);
        iv_select3.setImageResource(R.drawable.ceping_default);
        iv_select4.setImageResource(R.drawable.ceping_default);
        switch (v.getId()){
            case R.id.rl1:
                iv_select1.setImageResource(R.drawable.ceping_confim);
                fenshu=1;
                btn_send.setClickable(true);
                btn_send.setBackgroundResource(R.drawable.button_org_big); //红色
                break;
            case R.id.rl2:
                iv_select2.setImageResource(R.drawable.ceping_confim);
                fenshu=2;
                btn_send.setClickable(true);
                btn_send.setBackgroundResource(R.drawable.button_org_big); //红色
                break;
            case R.id.rl3:
                iv_select3.setImageResource(R.drawable.ceping_confim);
                fenshu=3;
                btn_send.setClickable(true);
                btn_send.setBackgroundResource(R.drawable.button_org_big); //红色
                break;
            case R.id.rl4:
                iv_select4.setImageResource(R.drawable.ceping_confim);
                fenshu=4;
                btn_send.setClickable(true);
                btn_send.setBackgroundResource(R.drawable.button_org_big); //红色
                break;
            case R.id.btn_send:

                EventBus.getDefault().post(new Next(fenshu,true));

                break;
            case R.id.tv_last:
                EventBus.getDefault().post(new Last());
                btn_send.setClickable(false);
                btn_send.setBackgroundResource(R.drawable.button_org_grauly);
                break;
        }
    }
}
