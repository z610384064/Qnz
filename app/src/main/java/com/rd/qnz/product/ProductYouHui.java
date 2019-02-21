package com.rd.qnz.product;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.rd.qnz.BaseActivity;
import com.rd.qnz.R;
import com.rd.qnz.view.RedCursorView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 红包界面
 */
public class ProductYouHui extends BaseActivity {
    private RadioGroup mRadioGroup;
    private RadioButton radioButton1, radioButton2;
    private ViewPager mViewPager;
    private Fragment fragment1, fragment2;
    private RedCursorView cursor;
    private String isnew;
    private int buy_money = 0, timeLimitDay; //购买金额,标的期限
    List<Map<String, String>> redlist1, redlist2; //可用红包,不可用红包
    private  List<Map<String, String>> redpacket_select; //当前所有红包的集合

    List<Map<String, String>> jiaxi_list1, jiaxi_list2; //可用加息劵,不可用加息劵
    private  List<Map<String, String>> jiaxi_select; //当前所有红包的集合
    private float  extraAwardApr ;//额外加息数字
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_you_hui);

        isnew=getIntent().getStringExtra("isnew");
        buy_money = getIntent().getIntExtra("buy_money", 0);  //购买金额
        timeLimitDay = getIntent().getIntExtra("timeLimitDay", 0);//标的使用期限
        redlist1 = (List<Map<String, String>>) getIntent().getSerializableExtra("list1");  //可用红包
        redlist2 = (List<Map<String, String>>) getIntent().getSerializableExtra("list2");   //不可用红包
        redpacket_select = (List<Map<String, String>>) getIntent().getSerializableExtra("redpacket_select"); //所有红包

        jiaxi_list1 = (List<Map<String, String>>) getIntent().getSerializableExtra("jiaxi_list1");  //可用加息集合
        jiaxi_list2 = (List<Map<String, String>>) getIntent().getSerializableExtra("jiaxi_list2");   //不可用加息集合
        jiaxi_select = (List<Map<String, String>>) getIntent().getSerializableExtra("jiaxi_select"); //所有加息集合
        extraAwardApr=getIntent().getFloatExtra("extraAwardApr",0);
        Log.e("redpacket_select333", redpacket_select.toString());

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

        TextView actionbar_side_name = (TextView) findViewById(R.id.actionbar_side_name);
        actionbar_side_name.setText("我的优惠");

        TextView text_use= (TextView) findViewById(R.id.text_use);
        text_use.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ProductYouHui.this, "点击了使用规则", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initView() {

        radioButton1 = (RadioButton) findViewById(R.id.product_radiobutton1);
        radioButton2 = (RadioButton) findViewById(R.id.product_radiobutton2);
        mRadioGroup = (RadioGroup) findViewById(R.id.product_radiogroup);
        mViewPager = (ViewPager) findViewById(R.id.product_viewpager);
        cursor = (RedCursorView) findViewById(R.id.cursor_view);
        radioButton1.setText("我的红包");
        radioButton2.setText("加息劵");
        List<Fragment> fragments = new ArrayList<Fragment>();

        fragment1 =new MyRedListFragment(this,redpacket_select,redlist1,redlist2,buy_money,timeLimitDay,isnew);/* 红包 */
        fragment2 = new MyJiaXiFragment(this,jiaxi_select,jiaxi_list1,jiaxi_list2,buy_money,timeLimitDay,extraAwardApr);/* 加息劵 */
        fragments.add(fragment1);
        fragments.add(fragment2);
        mViewPager.setAdapter(new ProductYouHui.MyFragmentPagerAdapter(getSupportFragmentManager(), fragments));
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
