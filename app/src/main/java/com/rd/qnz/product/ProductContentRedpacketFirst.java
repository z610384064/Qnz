package com.rd.qnz.product;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.rd.qnz.BaseActivity;
import com.rd.qnz.R;
import com.rd.qnz.WebViewAct;
import com.rd.qnz.custom.APIModel;
import com.rd.qnz.custom.Utility;
import com.rd.qnz.tools.BaseParam;
import com.umeng.analytics.MobclickAgent;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 首次用户使用红包页面
 * 规则不同：新人只能一次使用一个红包
 *
 * @author Evonne
 */
public class ProductContentRedpacketFirst extends BaseActivity {

    private static final String TAG = "新人使用红包";
    APIModel apiModel = new APIModel();
    private ProductContentRedpacketAdapter listAdapter, listAdapter2;
    private ListView redpacket_listview, redpacket_listview2; //可用红包,不可用红包
    List<Map<String, String>> list1, list2;
    private List<Map<String, String>> redpacket_select;
    private int buy_money = 0, timeLimitDay;
    private int number = 0;//红包选中个数
    private TextView red_count;
    private int select_count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage_product_list_content_redpacket);
        buy_money = getIntent().getIntExtra("buy_money", 0);
        timeLimitDay = getIntent().getIntExtra("timeLimitDay", 0);//标的使用期限
        list1 = (List<Map<String, String>>) getIntent().getSerializableExtra("list1");
        list2 = (List<Map<String, String>>) getIntent().getSerializableExtra("list2");
        redpacket_select = (List<Map<String, String>>) getIntent().getSerializableExtra("redpacket_select");
        setSwipeBackEnable(false); //禁止滑动删除
        initBar();
        initView();
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG); //统计页面
        MobclickAgent.onResume(this);  //统计时长
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(TAG); // 保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息
        MobclickAgent.onPause(this);
    }

    //导航栏
    private void initBar() {
        ImageView actionbar_side_left_iconfont = (ImageView) findViewById(R.id.actionbar_side_left_iconfont);
        actionbar_side_left_iconfont.setVisibility(View.VISIBLE);
        actionbar_side_left_iconfont.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                finish();
            }
        });

        //标题
        TextView actionbar_side_name = (TextView) findViewById(R.id.actionbar_side_name);
        actionbar_side_name.setVisibility(View.VISIBLE);
        actionbar_side_name.setText("使用红包");

    }

    private void initView() {
        findViewById(R.id.sure_btn).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable("redpacket_select", (Serializable) redpacket_select);
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        red_count = (TextView) findViewById(R.id.red_count);
        red_count.setText("0" + "");
        redpacket_listview = (ListView) findViewById(R.id.redpacket_listview);
        redpacket_listview2 = (ListView) findViewById(R.id.redpacket_listview2);
        listAdapter = new ProductContentRedpacketAdapter(this, list1, buy_money, timeLimitDay);
        listAdapter2 = new ProductContentRedpacketAdapter(this, list2, buy_money, timeLimitDay);

        redpacket_listview.setAdapter(listAdapter);
        Utility.setListViewHeightBasedOnChildren(redpacket_listview);
        redpacket_listview2.setAdapter(listAdapter2);
        Utility.setListViewHeightBasedOnChildren(redpacket_listview2);

        redpacket_listview.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                // TODO Auto-generated method stub
                select_count = 0;

                /* 点击列表项 若选中状态情况下改为未选中，反之 */
                if (list1.get(arg2).get("status").equals("0")) {
                    list1.get(arg2).put("status", "1");
                } else {
                    list1.get(arg2).put("status", "0");
                }
                number = 0;
                /* 点击列表项 遍历红包list，把选中的红包的金额相加 */
                for (int i = 0; i < list1.size(); i++) {
                    if (list1.get(i).get("status").equals("1")) {
                        select_count = select_count + Integer.parseInt(list1.get(i).get("redPacketAmount"));
                        Log.i("---select_count---", "---" + select_count);
                        number++;
                    }
                }
                if (number > 1) {
                    showToast("首次投资红包不可叠加使用！");
                    for (int i = 0; i < list1.size(); i++) {
                        list1.get(i).put("status", "0");
                        redpacket_select.get(i).put("status", "0");
                    }
                    list1.get(arg2).put("status", "1"); //把当前选中的状态改为1
                    red_count.setText(list1.get(arg2).get("redPacketAmount"));  //已选择多少元

                    for (int i = 0; i < redpacket_select.size(); i++) {
                        if (redpacket_select.get(i).get("redpacketId").equals(list1.get(arg2).get("redpacketId"))) {
                            redpacket_select.get(i).put("status", list1.get(arg2).get("status")); //把整个列表的状态也改成1
                        }
                    }
                } else { //次数没有大于一 等于1
                    if (timeLimitDay >= Integer.valueOf(list1.get(arg2).get("timeLimit"))) {
                        if (buy_money >= Double.valueOf(list1.get(arg2).get("dayLimit"))) {
                            //可用的红包
                            if ((buy_money) < Integer.valueOf(list1.get(arg2).get("dayLimit"))) {
                                showToast("勾选的红包金额超过上限咯！");
                                list1.get(arg2).put("status", "0");
                            } else {
                                red_count.setText(select_count + "");//已选择多少元
                            }
                        } else {//投资金额未达到红包不可用
                            showToast("投资金额未达到，此红包不能使用");
                            list1.get(arg2).put("status", "0");
                        }
                    } else {//投资期限未达到红包不可用
                        showToast("投资期限未达到，此红包不能使用");
                        list1.get(arg2).put("status", "0");
                    }
                }

                listAdapter.notifyDataSetChanged(list1);
                for (int i = 0; i < redpacket_select.size(); i++) {
                    if (redpacket_select.get(i).get("redpacketId").equals(list1.get(arg2).get("redpacketId"))) {
                        redpacket_select.get(i).put("status", list1.get(arg2).get("status"));
                    }
                }
            }
        });

        findViewById(R.id.text_hbgz).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                WebViewAct.start(ProductContentRedpacketFirst.this, "红包使用规则", BaseParam.URL_QIAN_FORWARD);
            }
        });
        initRed();
    }

    private void initRed() {
        select_count = 0;
        number = 0;
        for (int i = 0; i < redpacket_select.size(); i++) {
            if (redpacket_select.get(i).get("status").equals("1")) {
                select_count = select_count + Integer.parseInt(redpacket_select.get(i).get("redPacketAmount"));
                number++;
            }
        }
        red_count.setText(select_count + "");
        Log.e("redpacket_select444", redpacket_select.toString());
        listAdapter.notifyDataSetChanged();
    }

}
