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
import com.rd.qnz.adapter.ProductContentJiaXipacketAdapter2;
import com.rd.qnz.custom.Utility;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import static com.rd.qnz.qiyu.Utils.runOnUiThread;

/**
 * Created by Administrator on 2017/4/17 0017.
 */

public class MyJiaXiFragment extends Fragment implements View.OnClickListener{
    private  View view;
    private Context context;

    /**
     * 底部按钮
     */

    private TextView tv_jiaxi_count; //加息份额 xx%
    private TextView sure_btn; //确认按钮



    private float jiaxi_count = 0;  //加息比例值


    private int buy_money = 0, timeLimitDay;  //从申购界面返回的投资金额,标期限

    List<Map<String, String>> jiaxi_list1, jiaxi_list2; //可用加息劵,不可用加息劵的集合
    private  List<Map<String, String>> jiaxi_select; //所有加息劵的集合
    private ListView redpacket_listview,redpacket_listview2; //可用加息劵列表,不可用加息劵的listview
//    private ProductContentJiaXipacketAdapter listAdapter, listAdapter2;\
    private ProductContentJiaXipacketAdapter2 listAdapter, listAdapter2;
    private  int number=0; //被选中的加息劵的个数
    private float  extraAwardApr ;//额外加息数字
    private TextView repad_textview2; //不可使用的加息劵
    public MyJiaXiFragment(Context context, List<Map<String, String>> jiaxi_select, List<Map<String, String>> jiaxi_list1, List<Map<String, String>> jiaxi_list2, int buy_money, int timeLimitDay,float  extraAwardApr){
            this.jiaxi_list1=jiaxi_list1;
            this.jiaxi_list2=jiaxi_list2;
            this.context = context;
            this.jiaxi_select = jiaxi_select;
            this.buy_money = buy_money;
            this.timeLimitDay = timeLimitDay;
            this.extraAwardApr=extraAwardApr;
    }
    public MyJiaXiFragment(){};
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        listAdapter = new ProductContentJiaXipacketAdapter(context, jiaxi_list1, buy_money, timeLimitDay,extraAwardApr);
//        listAdapter2 = new ProductContentJiaXipacketAdapter(context, jiaxi_list2, buy_money, timeLimitDay,extraAwardApr);
        listAdapter = new ProductContentJiaXipacketAdapter2(context, jiaxi_list1, buy_money, timeLimitDay,extraAwardApr);
        listAdapter2 = new ProductContentJiaXipacketAdapter2(context, jiaxi_list2, buy_money, timeLimitDay,extraAwardApr);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_jiaxi, null);
        redpacket_listview= (ListView) view.findViewById(R.id.redpacket_listview);
        redpacket_listview2= (ListView) view.findViewById(R.id.redpacket_listview2);
        redpacket_listview.setAdapter(listAdapter);
        Utility.setListViewHeightBasedOnChildren(redpacket_listview);
        redpacket_listview2.setAdapter(listAdapter2);
        Utility.setListViewHeightBasedOnChildren(redpacket_listview2);

        repad_textview2= (TextView) view.findViewById(R.id.repad_textview2);
        sure_btn= (TextView) view.findViewById(R.id.sure_btn);
        sure_btn.setOnClickListener(this);
        tv_jiaxi_count= (TextView) view.findViewById(R.id.tv_jiaxi_count);


        tv_jiaxi_count.setText("0" + "");

        RelativeLayout item_relativeLayout = (RelativeLayout)view.findViewById(R.id.item_relativeLayout);
        if (extraAwardApr!=0){ //是加息标,那么加息劵肯定不能使用了
            if (jiaxi_list1.size()==0){
                repad_textview2.setText("加息标不可使用加息劵");
            }


        }else { //不是加息标,可以使用加息劵
            if (jiaxi_list2.size() == 0) {  //如果没有不可用的加息劵
                item_relativeLayout.setVisibility(View.INVISIBLE);
            } else {
                item_relativeLayout.setVisibility(View.VISIBLE);
            }
        }



        redpacket_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                // TODO Auto-generated method stub
                jiaxi_count = 0; //加息金额

                /* 点击列表项 若选中状态情况下改为未选中，反之 */
                if (jiaxi_list1.get(arg2).get("status").equals("0")) {
                    jiaxi_list1.get(arg2).put("status", "1");
                } else {
                    jiaxi_list1.get(arg2).put("status", "0");
                }
                number = 0;
                /* 点击列表项 遍历加息劵list，把选中的红包的金额相加 */
                for (int i = 0; i < jiaxi_list1.size(); i++) {
                    if (jiaxi_list1.get(i).get("status").equals("1")) {
                        jiaxi_count =Float.parseFloat(jiaxi_list1.get(i).get("apr"));
                        Log.i("---select_count---", "---" + jiaxi_count);
                        number++;
                    }
                }
                if (number > 1) {
//                    showToast("加息劵不可叠加使用！");
                    int jiaxi_list1_length=jiaxi_list1.size();
                    for (int i = 0; i <jiaxi_list1_length ; i++) {
                        jiaxi_list1.get(i).put("status", "0");

                    }
                    int select_length=jiaxi_select.size();
                    for (int i=0;i<select_length;i++){
                        jiaxi_select.get(i).put("status", "0");
                    }
                    jiaxi_list1.get(arg2).put("status", "1");
                    tv_jiaxi_count.setText(jiaxi_list1.get(arg2).get("apr"));

                    for (int i = 0; i < jiaxi_select.size(); i++) {
                        if (jiaxi_select.get(i).get("userCouponId").equals(jiaxi_list1.get(arg2).get("userCouponId"))) { //加息劵id
                            jiaxi_select.get(i).put("status", jiaxi_list1.get(arg2).get("status"));
                        }
                    }
                } else {

                    tv_jiaxi_count.setText(jiaxi_count + "");
                }
                listAdapter.notifyDataSetChanged(jiaxi_list1);
                for (int i = 0; i < jiaxi_select.size(); i++) {
                    if (jiaxi_select.get(i).get("userCouponId").equals(jiaxi_list1.get(arg2).get("userCouponId"))) {
                        jiaxi_select.get(i).put("status", jiaxi_list1.get(arg2).get("status"));
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

                bundle.putSerializable("jiaxi_select", (Serializable) jiaxi_select);
                intent.putExtras(bundle);
                getActivity().setResult(2, intent);
                getActivity().finish();
                break;

        }
    }

    private void initRed() {
        jiaxi_count = 0;

//        for (int i = 0; i < jiaxi_select.size(); i++) {
//            if (jiaxi_select.get(i).get("status").equals("1")) {
//                jiaxi_count =Float.parseFloat(jiaxi_select.get(i).get("apr"));
//            }
//        }
        for (int i = 0; i < jiaxi_list1.size(); i++) {
            if (jiaxi_list1.get(i).get("status").equals("1")) {
                jiaxi_count =Float.parseFloat(jiaxi_list1.get(i).get("apr"));
            }
        }
        tv_jiaxi_count.setText(jiaxi_count + "");
        Log.e("redpacket_select444", jiaxi_select.toString());
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
