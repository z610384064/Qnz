package com.rd.qnz.product.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rd.qnz.R;

/**
 * 项目介绍,安全保障,投资记录2017/9/26 0026.
 */

public class VerticalFragment2 extends Fragment {
    private Context context;
    private ViewPager viewpager;
    public VerticalFragment2(){}
    public VerticalFragment2(Context context){
        this.context=context;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=View.inflate(context,R.layout.fragment2,null);
        viewpager= (ViewPager) view.findViewById(R.id.viewpager);
        return view;
    }

    public void  initData(){

    }
}
