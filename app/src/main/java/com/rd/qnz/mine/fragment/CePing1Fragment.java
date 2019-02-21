package com.rd.qnz.mine.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.rd.qnz.R;
import com.rd.qnz.bean.Next;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Administrator on 2017/5/27 0027.
 */

public class CePing1Fragment extends Fragment implements View.OnClickListener {
    private Context context;
    private LinearLayout rl1,rl2,rl3,rl4;
    private ImageView  iv_select1,iv_select2,iv_select3,iv_select4;
    public CePing1Fragment(){}
    public CePing1Fragment(Context context){
        this.context=context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=View.inflate(context, R.layout.fragment_ceping1,null);
        rl1= (LinearLayout) view.findViewById(R.id.rl1);
        rl2= (LinearLayout) view.findViewById(R.id.rl2);
        rl3= (LinearLayout) view.findViewById(R.id.rl3);
        rl4= (LinearLayout) view.findViewById(R.id.rl4);
        iv_select1= (ImageView) view.findViewById(R.id.iv_select1);
        iv_select2= (ImageView) view.findViewById(R.id.iv_select2);
        iv_select3= (ImageView) view.findViewById(R.id.iv_select3);
        iv_select4= (ImageView) view.findViewById(R.id.iv_select4);
        rl1.setOnClickListener(this);
        rl2.setOnClickListener(this);
        rl3.setOnClickListener(this);
        rl4.setOnClickListener(this);
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
                EventBus.getDefault().post(new Next(1,false));
                break;
            case R.id.rl2:
                iv_select2.setImageResource(R.drawable.ceping_confim);
                EventBus.getDefault().post(new Next(2,false));
                break;
            case R.id.rl3:
                iv_select3.setImageResource(R.drawable.ceping_confim);
                EventBus.getDefault().post(new Next(3,false));
                break;
            case R.id.rl4:
                iv_select4.setImageResource(R.drawable.ceping_confim);
                EventBus.getDefault().post(new Next(4,false));
                break;
        }
    }
}
