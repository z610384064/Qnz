package com.rd.qnz.product;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rd.qnz.R;
import com.rd.qnz.adapter.ProductContentRedpacketAdapter2;
import com.rd.qnz.custom.Utility;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static com.rd.qnz.qiyu.Utils.runOnUiThread;

/**
 * Created by Administrator on 2017/4/17 0017.
 */

/**
 * 投资确认-我的红包
 */
public class MyRedListFragment extends Fragment implements View.OnClickListener {
    private  View view;
    private Context context;
    private  List<Map<String, String>> redpacket_select;
    private int buy_money = 0, timeLimitDay;
    List<Map<String, String>> redlist1, redlist2; //可用红包,不可用红包
    private ListView redpacket_listview,redpacket_listview2;
    private TextView sure_btn; //确认按钮
//    private ProductContentRedpacketAdapter listAdapter, listAdapter2;
    private ProductContentRedpacketAdapter2 listAdapter, listAdapter2;
    private TextView red_count; //已选择xx元
    private int count_red;//zongbao

    private int select_count = 0;
    private int select_limit = 0;//判断是否满足红包的使用金额
    private int number = 0;//红包选中个数
    private String isnew; //是否是新手

    public MyRedListFragment(){

    }
    public MyRedListFragment(Context context, List<Map<String, String>> list, List<Map<String, String>> redlist1, List<Map<String, String>> redlist2,int buy_money, int timeLimitDay,String isnew){
        this.redlist1=redlist1;
        this.redlist2=redlist2;
        this.context = context;
        this.redpacket_select = list;
        this.buy_money = buy_money;
        this.timeLimitDay = timeLimitDay;
        this.isnew=isnew;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        listAdapter = new ProductContentRedpacketAdapter(context, redlist1, buy_money, timeLimitDay);
//        listAdapter2 = new ProductContentRedpacketAdapter(context, redlist2, buy_money, timeLimitDay);
        listAdapter = new ProductContentRedpacketAdapter2(context, redlist1, buy_money, timeLimitDay);
        listAdapter2 = new ProductContentRedpacketAdapter2(context, redlist2, buy_money, timeLimitDay);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_redpacket, null);
        redpacket_listview= (ListView) view.findViewById(R.id.redpacket_listview);
        redpacket_listview2= (ListView) view.findViewById(R.id.redpacket_listview2);
        redpacket_listview.setAdapter(listAdapter);
        Utility.setListViewHeightBasedOnChildren(redpacket_listview);
        redpacket_listview2.setAdapter(listAdapter2);
        Utility.setListViewHeightBasedOnChildren(redpacket_listview2);

        sure_btn= (TextView) view.findViewById(R.id.sure_btn);
        sure_btn.setOnClickListener(this);
        red_count= (TextView) view.findViewById(R.id.red_count);
        count_red = (int) (buy_money * 0.02);

        red_count.setText("0" + "");

        RelativeLayout item_relativeLayout = (RelativeLayout)view.findViewById(R.id.item_relativeLayout);
        if (redlist2.size() == 0) {
            item_relativeLayout.setVisibility(View.INVISIBLE);
        } else {
            item_relativeLayout.setVisibility(View.VISIBLE);
        }


        redpacket_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                if (isnew.equals("1")){ //是新手
                    // TODO Auto-generated method stub
                    select_count = 0;

                /* 点击列表项 若选中状态情况下改为未选中，反之 */
                    if (redlist1.get(arg2).get("status").equals("0")) {
                        redlist1.get(arg2).put("status", "1");
                    } else {
                        redlist1.get(arg2).put("status", "0");
                    }
                    number = 0;
                /* 点击列表项 遍历红包list，把选中的红包的金额相加 */
                    for (int i = 0; i < redlist1.size(); i++) {
                        if (redlist1.get(i).get("status").equals("1")) {
                            select_count = select_count + Integer.parseInt(redlist1.get(i).get("redPacketAmount"));
                            Log.i("---select_count---", "---" + select_count);
                            number++;
                        }
                    }
                    if (number > 1) {
                        for (int i = 0; i < redlist1.size(); i++) {
                            redlist1.get(i).put("status", "0");
                            redpacket_select.get(i).put("status", "0");
                        }
                        redlist1.get(arg2).put("status", "1"); //把当前选中的状态改为1
                        red_count.setText(redlist1.get(arg2).get("redPacketAmount"));  //已选择多少元

                        for (int i = 0; i < redpacket_select.size(); i++) {
                            if (redpacket_select.get(i).get("redpacketId").equals(redlist1.get(arg2).get("redpacketId"))) {
                                redpacket_select.get(i).put("status", redlist1.get(arg2).get("status")); //把整个列表的状态也改成1
                            }
                        }
                    } else { //次数没有大于一 等于1
                        if (timeLimitDay >= Integer.valueOf(redlist1.get(arg2).get("timeLimit"))) {
                            if (buy_money >= Double.valueOf(redlist1.get(arg2).get("dayLimit"))) {
                                //可用的红包
                                if ((buy_money) < Integer.valueOf(redlist1.get(arg2).get("dayLimit"))) {
                                    showToast("勾选的红包金额超过上限咯！");
                                    redlist1.get(arg2).put("status", "0");
                                } else {
                                    red_count.setText(select_count + "");//已选择多少元
                                }
                            } else {//投资金额未达到红包不可用
                                showToast("投资金额未达到，此红包不能使用");
                                redlist1.get(arg2).put("status", "0");
                            }
                        } else {//投资期限未达到红包不可用
                            showToast("投资期限未达到，此红包不能使用");
                            redlist1.get(arg2).put("status", "0");
                        }
                    }

                    listAdapter.notifyDataSetChanged(redlist1);
                    for (int i = 0; i < redpacket_select.size(); i++) {
                        if (redpacket_select.get(i).get("redpacketId").equals(redlist1.get(arg2).get("redpacketId"))) {
                            redpacket_select.get(i).put("status", redlist1.get(arg2).get("status"));
                        }
                    }

                }else {  //不是新手
                    // TODO Auto-generated method stub
                    select_count = 0;
                    select_limit = 0;
                /* 点击列表项 若选中状态情况下改为未选中，反之 */
                    if (redlist1.get(arg2).get("status").equals("0")) {

                        redlist1.get(arg2).put("status", "1");
                    } else {
                        redlist1.get(arg2).put("status", "0");
                    }
                    number = 0;
                /* 点击列表项 遍历红包list，把选中的红包的金额相加 得到选中的红包所需投资的金额*/
                    for (int i = 0; i < redlist1.size(); i++) {
                        if (redlist1.get(i).get("status").equals("1")) {
                            select_count = select_count + Integer.parseInt(redlist1.get(i).get("redPacketAmount"));//所有被选中的红包的优惠金额
                            Log.i("---select_count---", "---" + select_count);
                            select_limit = select_limit + Integer.parseInt(redlist1.get(i).get("dayLimit")); //所有被选中的红包需要投资的金额
                            number = i + 1;
                        }
                    }
                    if (timeLimitDay >= Integer.valueOf(redlist1.get(arg2).get("timeLimit"))) { //如果这个标的期限大于红包的期限
                        if (buy_money >= Double.valueOf(redlist1.get(arg2).get("dayLimit"))) { //购买金额大于使用这个红包需要投资的金额
                            //可用的红包
                            select_limit = select_limit - (Integer.valueOf(redlist1.get(arg2).get("dayLimit")));
                            Log.i("(还剩多少满足金额)======11111", (buy_money - select_limit) + "=========");
                            if ((buy_money - select_limit) < Integer.valueOf(redlist1.get(arg2).get("dayLimit"))) {
                                showToast("勾选的红包金额超过上限咯！");
                                redlist1.get(arg2).put("status", "0");
                            } else {
                                red_count.setText(select_count + "");
                                //money_need.setText("还剩" + (buy_money - select_limit - (Integer.valueOf(list1.get(arg2).get("dayLimit")))) + "元可作为红包起用金");
                            }
                        } else {//投资金额未达到红包不可用
                            showToast("投资金额未达到，此红包不能使用");
                            redlist1.get(arg2).put("status", "0");
                        }
                    } else {//投资期限未达到红包不可用
                        showToast("投资期限未达到，此红包不能使用");
                        redlist1.get(arg2).put("status", "0");
                    }
                    listAdapter.notifyDataSetChanged(redlist1);
                    Log.i("(还剩多少满足金额)======22222", (buy_money - select_limit) + "=========");


                    for (int i = 0; i < redpacket_select.size(); i++) { //把被选中的红包状态改为1
                        if (redpacket_select.get(i).get("redpacketId").equals(redlist1.get(arg2).get("redpacketId"))) {

                            redpacket_select.get(i).put("status", redlist1.get(arg2).get("status"));
                        }
                    }
                }


            }
        });

        initRed();
        return view;

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.sure_btn:
                //返回总权限
                Intent intent = new Intent();
                Bundle bundle = new Bundle();

                bundle.putSerializable("redpacket_select", (Serializable) redpacket_select);
                intent.putExtras(bundle);
                getActivity().setResult(RESULT_OK, intent);
                getActivity().finish();
                break;

        }
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

    Toast mToast;
    TextView toast_tv;
    public void showToast(final String text) {
        if (!TextUtils.isEmpty(text)) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (mToast == null) {
                        mToast = new Toast(context);
                        View tview = LayoutInflater.from(context).inflate(R.layout.layout_toast, null);
                        mToast.setView(tview);
                        toast_tv = (TextView) tview.findViewById(R.id.toast_text);
                    } else {

                    }
                    toast_tv.setText(text);
                    mToast.show();
                }
            });
        }
    }
}
