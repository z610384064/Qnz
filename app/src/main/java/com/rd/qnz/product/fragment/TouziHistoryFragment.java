package com.rd.qnz.product.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.rd.qnz.R;
import com.rd.qnz.custom.APIModel;
import com.rd.qnz.custom.MineShow;
import com.rd.qnz.custom.MyApplication;
import com.rd.qnz.dialog.CustomProgressDialog;
import com.rd.qnz.dialog.GetDialog;
import com.rd.qnz.product.ProductMoreListAdapter;
import com.rd.qnz.tools.BaseParam;
import com.rd.qnz.tools.Check;
import com.rd.qnz.tools.CustomViewpager;
import com.rd.qnz.tools.MyListView;
import com.rd.qnz.tools.webservice.JsonRequeatThreadProductMoreList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.rd.qnz.qiyu.Utils.runOnUiThread;

/**
 * Created by Administrator on 2017/5/9 0009.
 */

public class TouziHistoryFragment extends Fragment {


    private ProductMoreListAdapter listAdapter;

    private ArrayList<String> param = null;
    private ArrayList<String> value = null;

    private GetDialog dia;
    private MyHandler myHandler;
    private CustomProgressDialog progressDialog = null;
    private MyApplication myApp;

    APIModel apiModel = new APIModel();

    private LinearLayout product_more_linear;

    private LinearLayout no_note;

    private int pernum = 12;// 每页显示个数

    private int currentPage = 1;// 当前页

    private String borrowId = "";

    List<Map<String, String>> list = new ArrayList<Map<String, String>>();
    private Context context;
    public MyListView listview;
    private CustomViewpager viewpager;
    /**
     * 1.6.0
     */
    private LinearLayout line_hongbao; //投资奖励界面
    private TextView tv_first_phone,tv_shoutoudescribe,tv_max_phone,tv_leijidescribe,tv_finally_phone,tv_weitoudescribe;
    private ImageView iv_red1,iv_red2,iv_red3;
    String showStatus;//是否是新手 1:非新手 2:新手
    String fastestTender; //首投号码
    String sendFlag;  //1:代表满标
    String lastTender;  //尾投号码
    String largestTenderUser; //最高投资人号码
    String largestTenderSum; //最高投资人累计投资金额
    String firstRedPacket;
    String highestRedPacket;
    String lastRedPacket;
    /**
     *  1.6.0 红包领取状态移到投资记录里面
     * @param savedInstanceState
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    public TouziHistoryFragment(){}
    public TouziHistoryFragment(String borrowId, Context context, CustomViewpager viewpager, String showStatus,
                                String fastestTender, String sendFlag, String lastTender, String largestTenderUser,
                                String largestTenderSum, String firstRedPacket, String highestRedPacket, String lastRedPacket){
        this.borrowId=borrowId;
        this.context=context;
        this.viewpager=viewpager;
        this.showStatus=showStatus;
        this.fastestTender=fastestTender;
        this.sendFlag=sendFlag;
        this.lastTender=lastTender;
        this.largestTenderUser=largestTenderUser;
        this.largestTenderSum=largestTenderSum;
        this.firstRedPacket=firstRedPacket;
        this.highestRedPacket=highestRedPacket;
        this.lastRedPacket=lastRedPacket;
    }

    public TouziHistoryFragment(String borrowId, Context context, String showStatus,
                                String fastestTender, String sendFlag, String lastTender, String largestTenderUser,
                                String largestTenderSum, String firstRedPacket, String highestRedPacket, String lastRedPacket){
        this.borrowId=borrowId;
        this.context=context;
        this.showStatus=showStatus;
        this.fastestTender=fastestTender;
        this.sendFlag=sendFlag;
        this.lastTender=lastTender;
        this.largestTenderUser=largestTenderUser;
        this.largestTenderSum=largestTenderSum;
        this.firstRedPacket=firstRedPacket;
        this.highestRedPacket=highestRedPacket;
        this.lastRedPacket=lastRedPacket;
    }



    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        View view=View.inflate(context,R.layout.homepage_product_record2,null);

        this.dia = new GetDialog();
        myApp = (MyApplication)getActivity().getApplication();
        this.myHandler = new MyHandler();

        line_hongbao= (LinearLayout) view.findViewById(R.id.line_hongbao);
        product_more_linear = (LinearLayout) view.findViewById(R.id.product_more_linear);
        no_note = (LinearLayout) view.findViewById(R.id.no_note);
        tv_first_phone= (TextView) view.findViewById(R.id.tv_first_phone);
        tv_shoutoudescribe= (TextView) view.findViewById(R.id.tv_shoutoudescribe);
        tv_max_phone= (TextView) view.findViewById(R.id.tv_max_phone);
        tv_leijidescribe= (TextView) view.findViewById(R.id.tv_leijidescribe);
        tv_finally_phone= (TextView) view.findViewById(R.id.tv_finally_phone);
        tv_weitoudescribe= (TextView) view.findViewById(R.id.tv_weitoudescribe);
        iv_red1= (ImageView) view.findViewById(R.id.iv_red1);
        iv_red2= (ImageView) view.findViewById(R.id.iv_red2);
        iv_red3= (ImageView) view.findViewById(R.id.iv_red3);

        listAdapter = new ProductMoreListAdapter(context, list);

        listview= (MyListView) view.findViewById(R.id.listview);

        listview.setAdapter(listAdapter);
        setListViewHeightBasedOnChildren(listview); //重新设置高度

        IntentFilter intentFilter=new IntentFilter("productcontent"); //从产品详情页发送来的广播
        context.registerReceiver(broadcastReceiver,intentFilter);

        if (showStatus != null && showStatus.equals("1")) {  //不是新手标,会有许多奖励

            if (fastestTender != null && fastestTender.length() == 11) { //如果首投奖励已经有人了
                tv_first_phone.setText(fastestTender.replace(fastestTender.substring(3, 7), "****")+"");

                iv_red1.setImageResource(R.drawable.red1);
                tv_shoutoudescribe.setText("获得"+firstRedPacket+"元首投红包");
            }else {

                iv_red1.setImageResource(R.drawable.red1);
                tv_first_phone.setText("第一笔");
            }

            if (sendFlag.equals("1")) {  //满标(全部售完了)
                if (lastTender != null && lastTender.length() == 11) { //如果有尾投人号码
                    tv_finally_phone.setText(lastTender.replace(lastTender.substring(3, 7), "****")+"");
                    tv_weitoudescribe.setVisibility(View.VISIBLE);
                    iv_red3.setImageResource(R.drawable.red3);

                    tv_weitoudescribe.setText("获得"+lastRedPacket+"元尾投红包");
                }
                //最高投资人

                if (largestTenderUser!= null && largestTenderUser.length() == 11) {  //最高投资用户不为空
                    tv_max_phone.setText(largestTenderUser.replace(largestTenderUser.substring(3, 7), "****")+"");


                    String newlargestTenderSum = largestTenderSum.substring(0,largestTenderSum.indexOf("."));
                    tv_leijidescribe.setText("累计:"+newlargestTenderSum+"元");

                    tv_leijidescribe.setText("获得"+highestRedPacket+"元红包");

                    iv_red2.setImageResource(R.drawable.red2);


                }
            } else {   //还没满,还可以继续买
            iv_red3.setImageResource(R.drawable.red3);
            iv_red2.setImageResource(R.drawable.red2);

                tv_finally_phone.setText("最后一笔");

                if (largestTenderUser!= null && largestTenderUser.length() == 11) {  //有最高投资金额
                    tv_max_phone.setText("" + largestTenderUser.replace(largestTenderUser.substring(3, 7), "****"));
                    String newlargestTenderSum = largestTenderSum.substring(0,largestTenderSum.indexOf("."));
                    tv_leijidescribe.setText("累计:"+newlargestTenderSum+"元");

                }else { //没有最高投资金额,说明一个人都没投资
                    tv_max_phone.setText("最高金额");
                    tv_leijidescribe.setText(highestRedPacket+"元红包待领取");
                }


            }
        } else if (showStatus!= null &&showStatus.equals("2")) {  //新app,新手标是没有这些奖励的
            line_hongbao.setVisibility(View.GONE);

        }

        startDataRequest();
//        viewpager.setObjectForPosition(view,2);
        return view;
    }
    BroadcastReceiver broadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
             String action= intent.getAction();
            if (action.equals("productcontent")){
                ++currentPage;
                startDataRequest();
            }
        }
    };



    private void startDataRequest() {
        if (Check.hasInternet(getActivity())) {
           initArray();
           param.add(BaseParam.QIAN_PERNUM);
            value.add(pernum + "");
            param.add(BaseParam.QIAN_CURRENTPAGE);
            value.add(currentPage + "");
            param.add(BaseParam.QIAN_PRODUCT_BORROWID);
            value.add(borrowId + "");

           param.add(BaseParam.URL_QIAN_API_APPID);
           value.add(myApp.appId);
           param.add(BaseParam.URL_QIAN_API_SERVICE);
            value.add("tenderRecord");
            param.add(BaseParam.URL_QIAN_API_SIGNTYPE);
           value.add(myApp.signType);

            String[] array = new String[]{
                    BaseParam.QIAN_PERNUM + "=" + pernum + "",
                    BaseParam.QIAN_CURRENTPAGE + "=" + currentPage + "",
                    BaseParam.QIAN_PRODUCT_BORROWID + "=" + borrowId + "",
                    BaseParam.URL_QIAN_API_APPID + "=" + myApp.appId,
                    BaseParam.URL_QIAN_API_SERVICE + "=tenderRecord",
                    BaseParam.URL_QIAN_API_SIGNTYPE + "=" + myApp.signType};

            String sign = apiModel.sortStringArray(array);
            param.add(BaseParam.URL_QIAN_API_SIGN);
            value.add(sign);
            progressDialog = dia
                    .getLoginDialog(getActivity(), "正在获取数据..");
          progressDialog.show();
            new Thread(new JsonRequeatThreadProductMoreList(
                   context,
                    myApp,
                   myHandler,
                   param,
                    value)
            ).start();

        } else {
            showToast("请检查网络连接是否正常");
        }

    }


    public void setListViewHeightBasedOnChildren(ListView listView) {

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

        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));


        listView.setLayoutParams(params);
    }



    private class MyHandler extends Handler {
        public void handleMessage(Message paramMessage) {
            Bundle localBundle1 = paramMessage.getData();
            ArrayList<Parcelable> product_Rlist = localBundle1.getParcelableArrayList(BaseParam.QIAN_REQUEAT_PRODUCT_MORE_LIST);
            if (null != progressDialog && progressDialog.isShowing()) {// 隐藏加载框
              progressDialog.dismiss();
            }

            if (null != product_Rlist) {
                List<Map<String, String>> list1 = (List<Map<String, String>>) product_Rlist.get(0);

                Map<String, String> map = list1.get(0);
                String resultCode = map.get("resultCode");
                System.out.println("resultCode = " + resultCode);
                if (resultCode.equals("1")) {
                    // 服务器已经发送验证码
                    initData(list1);
                    listAdapter.notifyDataSetChanged(list);

                } else {

                    if (map.get("errorCode").equals("no")) { //投资记录已经没有了
                        if (list.size() == 0) {
                            product_more_linear.setVisibility(View.GONE);
                            no_note.setVisibility(View.VISIBLE);
                        } else {
                             MineShow.toastShow("没有更多数据了",context);
                        }

                    } else {
                        product_more_linear.setVisibility(View.GONE);
                        no_note.setVisibility(View.VISIBLE);
                        showToast(Check.checkReturn(map.get("errorCode")));
                    }
                }
            }

//            product_record_list.onRefreshComplete();
            super.handleMessage(paramMessage);
        }
    }
    /**
     * 初始化适配器的数据源
     *
     * @param paramList
     */
    private void initData(List<Map<String, String>> paramList) {
        System.out.println("paramList = " + paramList);
        if (paramList == null) {
            return;
        }

        if (currentPage == 1) {
            list.clear();
        }
        for (int i = 0; i < paramList.size(); i++) {
            Map<String, String> map = new HashMap<String, String>();
            map.put(BaseParam.QIAN_PRODUCT_ACCOUNT, paramList.get(i).get(BaseParam.QIAN_PRODUCT_ACCOUNT));
            map.put(BaseParam.QIAN_PRODUCT_ADDTIME, paramList.get(i).get(BaseParam.QIAN_PRODUCT_ADDTIME));
            map.put(BaseParam.QIAN_PRODUCT_BORROWID, paramList.get(i).get(BaseParam.QIAN_PRODUCT_BORROWID));
            map.put(BaseParam.QIAN_PRODUCT_BORROWNAME, paramList.get(i).get(BaseParam.QIAN_PRODUCT_BORROWNAME));
            map.put(BaseParam.QIAN_PRODUCT_BORROWSTATUS, paramList.get(i).get(BaseParam.QIAN_PRODUCT_BORROWSTATUS));
            map.put(BaseParam.QIAN_PRODUCT_BORROWTYPE, paramList.get(i).get(BaseParam.QIAN_PRODUCT_BORROWTYPE));
            map.put(BaseParam.QIAN_PRODUCT_BORROWUSERID, paramList.get(i).get(BaseParam.QIAN_PRODUCT_BORROWUSERID));
            map.put(BaseParam.QIAN_PRODUCT_FRANCHISEENAME, paramList.get(i).get(BaseParam.QIAN_PRODUCT_FRANCHISEENAME));
            map.put(BaseParam.QIAN_PRODUCT_HASCOLLECTEDPERIOD, paramList.get(i).get(BaseParam.QIAN_PRODUCT_HASCOLLECTEDPERIOD));
            map.put(BaseParam.QIAN_PRODUCT_ISEXPERIENCE, paramList.get(i).get(BaseParam.QIAN_PRODUCT_ISEXPERIENCE));
            map.put(BaseParam.QIAN_PRODUCT_MONEY, paramList.get(i).get(BaseParam.QIAN_PRODUCT_MONEY));
            map.put(BaseParam.QIAN_PRODUCT_TENDERID, paramList.get(i).get(BaseParam.QIAN_PRODUCT_TENDERID));
            map.put(BaseParam.QIAN_PRODUCT_TENDERSTATUS, paramList.get(i).get(BaseParam.QIAN_PRODUCT_TENDERSTATUS));
            map.put(BaseParam.QIAN_PRODUCT_TENDERTYPE, paramList.get(i).get(BaseParam.QIAN_PRODUCT_TENDERTYPE));
            map.put(BaseParam.QIAN_PRODUCT_TOTALPERIOD, paramList.get(i).get(BaseParam.QIAN_PRODUCT_TOTALPERIOD));
            map.put(BaseParam.QIAN_PRODUCT_USERID, paramList.get(i).get(BaseParam.QIAN_PRODUCT_USERID));
            map.put(BaseParam.QIAN_PRODUCT_USERSHOWNAME, paramList.get(i).get(BaseParam.QIAN_PRODUCT_USERSHOWNAME));

            list.add(map);
        }
    }

    private void initArray() {
        this.param = null;
        this.value = null;
        this.param = new ArrayList();
        this.value = new ArrayList();
        System.gc();
    }

    Toast mToast;
    TextView toast_tv;

    public void showToast(final String text) {
        if (!TextUtils.isEmpty(text)) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (mToast == null) {
//						mToast = Toast.makeText(BaseActivity.this, text, Toast.LENGTH_SHORT);
                        mToast = new Toast(context);
                        View tview = LayoutInflater.from(context).inflate(R.layout.layout_toast, null);
                        mToast.setView(tview);
                        toast_tv = (TextView) tview.findViewById(R.id.toast_text);
                    } else {
//						mToast.setText(text);
                    }
                    toast_tv.setText(text);
                    mToast.show();
                }
            });
        }
    }

    public void showToast(final int resId) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (mToast == null) {
//					mToast = Toast.makeText(BaseActivity.this, resId, Toast.LENGTH_SHORT);
                    mToast = new Toast(context);
                    View tview = LayoutInflater.from(context).inflate(R.layout.layout_toast, null);
                    mToast.setView(tview);
                    toast_tv = (TextView) tview.findViewById(R.id.toast_text);
                } else {
//					mToast.setText(resId);
                }
                toast_tv.setText(resId);
                mToast.show();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (broadcastReceiver!=null){
            try{
                context.unregisterReceiver(broadcastReceiver);
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (broadcastReceiver!=null){
            try{
                context.unregisterReceiver(broadcastReceiver);
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }
}
