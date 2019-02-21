package com.rd.qnz.mine;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.rd.qnz.BaseActivity;
import com.rd.qnz.R;
import com.rd.qnz.view.RedCursorView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Evonne on 2016.11.21.
 * 我的投资
 */

public class MyRecordList extends BaseActivity {

    private RadioGroup mRadioGroup;
    private RadioButton radioButton1, radioButton2;
    private ViewPager mViewPager;
    private Fragment fragment1, fragment2;
    private RedCursorView cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewpager_list);
        initBar();
        initView();
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
        TextView actionbar_side_name = (TextView) findViewById(R.id.actionbar_side_name);  //我的投资
        actionbar_side_name.setText("我的投资");
    }

    private void initView() {

        radioButton1 = (RadioButton) findViewById(R.id.product_radiobutton1);
        radioButton2 = (RadioButton) findViewById(R.id.product_radiobutton2);
        mRadioGroup = (RadioGroup) findViewById(R.id.product_radiogroup);
        mViewPager = (ViewPager) findViewById(R.id.product_viewpager);
        cursor = (RedCursorView) findViewById(R.id.cursor_view);
        radioButton1.setText("投资中");
        radioButton2.setText("已回款");
        List<Fragment> fragments = new ArrayList<Fragment>();
        fragment1 = new MyRecordListFragment1();/* 投资中 */
        fragment2 = new MyRecordListFragment2();/* 已回款 */
        fragments.add(fragment1);
        fragments.add(fragment2);
        mViewPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), fragments));
        mViewPager.setCurrentItem(0);

        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.product_radiobutton1:
                        mViewPager.setCurrentItem(0);
                        break;
                    case R.id.product_radiobutton2:
                        mViewPager.setCurrentItem(1);
                        break;
                }
            }
        });

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int arg0) {
                switch (arg0) {
                    case 0:
                        radioButton1.setChecked(true);
                        break;
                    case 1:
                        radioButton2.setChecked(true);
                        break;
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub
                cursor.setXY(arg0, arg1);
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub
            }
        });

    }

    class MyFragmentPagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> fragments;

        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public MyFragmentPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int arg0) {
            if (arg0 >= fragments.size()) {
                return null;
            }
            return fragments.get(arg0);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public int getItemPosition(Object object) {
            if (fragments.contains(object)) {
                return POSITION_UNCHANGED;
            }
            return POSITION_NONE;
        }
    }
}

