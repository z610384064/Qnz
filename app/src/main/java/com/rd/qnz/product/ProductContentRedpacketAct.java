package com.rd.qnz.product;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rd.qnz.BaseActivity;
import com.rd.qnz.R;
import com.rd.qnz.WebViewAct;
import com.rd.qnz.custom.APIModel;
import com.rd.qnz.custom.MyApplication;
import com.rd.qnz.custom.Utility;
import com.rd.qnz.tools.BaseParam;
import com.umeng.analytics.MobclickAgent;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 使用红包页面
 *
 * @author Evonne
 */
public class ProductContentRedpacketAct extends BaseActivity {

    private static final String TAG = "使用红包";

    APIModel apiModel = new APIModel();

    private ProductContentRedpacketAdapter listAdapter, listAdapter2;

    private ListView redpacket_listview, redpacket_listview2;
    List<Map<String, String>> list1, list2;

    private List<Map<String, String>> redpacket_select;

    private MyApplication myApp;

    private int buy_money = 0, timeLimitDay;
    private int number = 0;//红包选中个数
    List<Map<String, String>> red_number;//红包数的集合
    private String timeLimit = "", aprLimit = "";//全局的红包期限和抵扣比例

    private LinearLayout text_hbgz; //红包规则

    private TextView red_count;//hongbao
    private int count_red;//zongbao
    private int ke_red;
    private int select_count = 0;
    private int select_limit = 0;//判断是否满足红包的使用金额

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage_product_list_content_redpacket);

        myApp = (MyApplication) getApplication();
        buy_money = getIntent().getIntExtra("buy_money", 0);
        timeLimitDay = getIntent().getIntExtra("timeLimitDay", 0);//标的使用期限
        list1 = (List<Map<String, String>>) getIntent().getSerializableExtra("list1");
        list2 = (List<Map<String, String>>) getIntent().getSerializableExtra("list2");
        redpacket_select = (List<Map<String, String>>) getIntent().getSerializableExtra("redpacket_select");
        Log.e("redpacket_select333", redpacket_select.toString());
        setSwipeBackEnable(false); //禁止滑动删除

        text_hbgz= (LinearLayout) findViewById(R.id.text_hbgz);
        text_hbgz.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                WebViewAct.start(ProductContentRedpacketAct.this, "红包使用规则", BaseParam.URL_QIAN_FORWARD);
            }
        });

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

        TextView actionbar_side_name = (TextView) findViewById(R.id.actionbar_side_name);
        actionbar_side_name.setText("使用红包");

    }

    /*public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }*/

    private void initView() {


        findViewById(R.id.sure_btn).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //返回总权限
                Intent intent = new Intent();
                Bundle bundle = new Bundle();

                bundle.putSerializable("redpacket_select", (Serializable) redpacket_select);
                // intent.putExtra("redpacket_select", (Serializable) redpacket_select);
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        red_count = (TextView) findViewById(R.id.red_count);
        RelativeLayout item_relativeLayout = (RelativeLayout) findViewById(R.id.item_relativeLayout);//不可用红包

        count_red = (int) (buy_money * 0.02);
        //int red = Red_count();
        //View headerView = getLayoutInflater().inflate(R.layout.homepage_product_redpacket_item2, null);
        red_count.setText("0" + "");
        redpacket_listview = (ListView) findViewById(R.id.redpacket_listview);
        redpacket_listview2 = (ListView) findViewById(R.id.redpacket_listview2);
        //redpacket_listview.addHeaderView(headerView);
        listAdapter = new ProductContentRedpacketAdapter(this, list1, buy_money, timeLimitDay);
        listAdapter2 = new ProductContentRedpacketAdapter(this, list2, buy_money, timeLimitDay);

        if (list2.size() == 0) {
            item_relativeLayout.setVisibility(View.INVISIBLE);
        } else {
            item_relativeLayout.setVisibility(View.VISIBLE);
        }

        redpacket_listview.setAdapter(listAdapter);
        Utility.setListViewHeightBasedOnChildren(redpacket_listview);
        redpacket_listview2.setAdapter(listAdapter2);
        Utility.setListViewHeightBasedOnChildren(redpacket_listview2);

        redpacket_listview.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                // TODO Auto-generated method stub
                select_count = 0;
                select_limit = 0;
                /* 点击列表项 若选中状态情况下改为未选中，反之 */
                if (list1.get(arg2).get("status").equals("0")) {

                    list1.get(arg2).put("status", "1");
                } else {
                    list1.get(arg2).put("status", "0");
                }
                number = 0;
                /* 点击列表项 遍历红包list，把选中的红包的金额相加 得到选中的红包所需投资的金额*/
                for (int i = 0; i < list1.size(); i++) {
                    if (list1.get(i).get("status").equals("1")) {
                        select_count = select_count + Integer.parseInt(list1.get(i).get("redPacketAmount"));//所有被选中的红包的优惠金额
                        Log.i("---select_count---", "---" + select_count);
                        select_limit = select_limit + Integer.parseInt(list1.get(i).get("dayLimit")); //所有被选中的红包需要投资的金额
                        number = i + 1;
                    }
                }
                if (timeLimitDay >= Integer.valueOf(list1.get(arg2).get("timeLimit"))) { //如果这个标的期限大于红包的期限
                    if (buy_money >= Double.valueOf(list1.get(arg2).get("dayLimit"))) { //购买金额大于使用这个红包需要投资的金额
                        //可用的红包
                        select_limit = select_limit - (Integer.valueOf(list1.get(arg2).get("dayLimit")));
                        Log.i("(还剩多少满足金额)======11111", (buy_money - select_limit) + "=========");
                        if ((buy_money - select_limit) < Integer.valueOf(list1.get(arg2).get("dayLimit"))) {
                            showToast("勾选的红包金额超过上限咯！");
                            list1.get(arg2).put("status", "0");
                        } else {
                            red_count.setText(select_count + "");
                            //money_need.setText("还剩" + (buy_money - select_limit - (Integer.valueOf(list1.get(arg2).get("dayLimit")))) + "元可作为红包起用金");
                        }
                    } else {//投资金额未达到红包不可用
                        showToast("投资金额未达到，此红包不能使用");
                        list1.get(arg2).put("status", "0");
                    }
                } else {//投资期限未达到红包不可用
                    showToast("投资期限未达到，此红包不能使用");
                    list1.get(arg2).put("status", "0");
                }
                listAdapter.notifyDataSetChanged(list1);
                Log.i("(还剩多少满足金额)======22222", (buy_money - select_limit) + "=========");


                for (int i = 0; i < redpacket_select.size(); i++) { //把被选中的红包状态改为1
                    if (redpacket_select.get(i).get("redpacketId").equals(list1.get(arg2).get("redpacketId"))) {

                        redpacket_select.get(i).put("status", list1.get(arg2).get("status"));
                    }
                }

            }
        });

        initRed();
    }

    private void initRed() {
        select_count = 0;
        select_limit = 0;
        for (int i = 0; i < redpacket_select.size(); i++) {
            if (redpacket_select.get(i).get("status").equals("1")) {
                select_count = select_count + Integer.parseInt(redpacket_select.get(i).get("redPacketAmount"));
                select_limit = select_limit + (Integer.valueOf(redpacket_select.get(i).get("dayLimit")));
            }
        }
        red_count.setText(select_count + "");
        Log.e("redpacket_select444", redpacket_select.toString());
        listAdapter.notifyDataSetChanged();
    }

}
