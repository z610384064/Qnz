package com.rd.qnz.product;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.rd.qnz.BaseActivity;
import com.rd.qnz.R;
import com.rd.qnz.custom.APIModel;
import com.rd.qnz.custom.MyApplication;
import com.rd.qnz.dialog.CustomProgressDialog;
import com.rd.qnz.dialog.GetDialog;
import com.rd.qnz.tools.BaseParam;
import com.rd.qnz.tools.Check;
import com.rd.qnz.tools.webservice.JsonRequeatThreadProductMoreList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 项目投资记录
 *
 * @author Evonne
 */

@SuppressLint("SetJavaScriptEnabled")
public class ProductRecordAct extends BaseActivity {

    private PullToRefreshListView product_record_list;
    private ProductMoreListAdapter listAdapter;

    private ArrayList<String> param = null;
    private ArrayList<String> value = null;

    private GetDialog dia;
    private MyHandler myHandler;
    private CustomProgressDialog progressDialog = null;
    private MyApplication myApp;

    APIModel apiModel = new APIModel();



    private int pernum = 12;// 每页显示个数

    private int currentPage = 1;// 当前页

    private String borrowId = "";

    List<Map<String, String>> list = new ArrayList<Map<String, String>>();

    /**
     * 1.6.0
     * @param savedInstanceState
     */

    private String showStatus;
    private String fastestTender;
    private String sendFlag;
    private String lastTender;
    private String largestTenderUser;
    private String largestTenderSum;
    private String firstRedPacket;
    private String highestRedPacket;
    private String lastRedPacket;
    private View headview;
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage_product_record);
        this.dia = new GetDialog();
        myApp = (MyApplication) getApplication();
        this.myHandler = new MyHandler();
        borrowId = getIntent().getStringExtra(BaseParam.QIAN_PRODUCT_BORROWID);
        showStatus = getIntent().getStringExtra("showStatus");
        fastestTender = getIntent().getStringExtra("fastestTender");
        sendFlag = getIntent().getStringExtra("sendFlag");
        lastTender = getIntent().getStringExtra("lastTender");
        largestTenderUser = getIntent().getStringExtra("largestTenderUser");
        largestTenderSum = getIntent().getStringExtra("largestTenderSum");
        firstRedPacket = getIntent().getStringExtra("firstRedPacket");
        highestRedPacket = getIntent().getStringExtra("highestRedPacket");
        lastRedPacket = getIntent().getStringExtra("lastRedPacket");
        initBar();
        initView();

    }

    private void initBar() {
        // 返回按钮
        ImageView actionbar_side_left_iconfont = (ImageView) findViewById(R.id.actionbar_side_left_iconfont);
        actionbar_side_left_iconfont.setVisibility(View.VISIBLE);
        actionbar_side_left_iconfont.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                finish();
            }
        });

        TextView actionbar_side_name = (TextView) findViewById(R.id.actionbar_side_name);
        actionbar_side_name.setText("投资记录");

    }
    private LinearLayout line_hongbao,product_more_linear,no_note;
    private TextView tv_first_phone, tv_shoutoudescribe,tv_max_phone,tv_leijidescribe,tv_finally_phone,tv_weitoudescribe;
    private ImageView iv_red1,iv_red2,iv_red3;
    private void initView() {
//        headview=View.inflate(this,R.layout.touzi_head,null);

        line_hongbao= (LinearLayout) findViewById(R.id.line_hongbao);


//        tv_first_phone= (TextView) headview.findViewById(R.id.tv_first_phone);
//        tv_shoutoudescribe= (TextView)headview. findViewById(R.id.tv_shoutoudescribe);
//        tv_max_phone= (TextView) headview.findViewById(R.id.tv_max_phone);
//        tv_leijidescribe= (TextView)headview. findViewById(R.id.tv_leijidescribe);
//        tv_finally_phone= (TextView) headview.findViewById(R.id.tv_finally_phone);
//        tv_weitoudescribe= (TextView) headview.findViewById(R.id.tv_weitoudescribe);
//        iv_red1= (ImageView) headview.findViewById(R.id.iv_red1);
//        iv_red2= (ImageView) headview.findViewById(R.id.iv_red2);
//        iv_red3= (ImageView) headview.findViewById(R.id.iv_red3);

        tv_first_phone= (TextView) findViewById(R.id.tv_first_phone);
        tv_shoutoudescribe= (TextView)findViewById(R.id.tv_shoutoudescribe);
        tv_max_phone= (TextView) findViewById(R.id.tv_max_phone);
        tv_leijidescribe= (TextView) findViewById(R.id.tv_leijidescribe);
        tv_finally_phone= (TextView) findViewById(R.id.tv_finally_phone);
        tv_weitoudescribe= (TextView) findViewById(R.id.tv_weitoudescribe);
        iv_red1= (ImageView) findViewById(R.id.iv_red1);
        iv_red2= (ImageView) findViewById(R.id.iv_red2);
        iv_red3= (ImageView) findViewById(R.id.iv_red3);

        no_note = (LinearLayout) findViewById(R.id.no_note);
        product_more_linear = (LinearLayout) findViewById(R.id.product_more_linear);
        product_record_list = (PullToRefreshListView) findViewById(R.id.product_more_list);

//        product_record_list.getRefreshableView().addHeaderView(headview);
        product_record_list.setMode(Mode.BOTH);
        listAdapter = new ProductMoreListAdapter(ProductRecordAct.this, list);

        product_record_list.setAdapter(listAdapter);
        product_record_list.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

            }
        });
        startDataRequest();
        product_record_list.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {

                    @Override
                    public void onPullDownToRefresh(
                            PullToRefreshBase<ListView> refreshView) {

                        refreshView.getLoadingLayoutProxy().setPullLabel("释放财运刷新");
                        refreshView.getLoadingLayoutProxy().setRefreshingLabel("财运刷新中...");
                        refreshView.getLoadingLayoutProxy().setReleaseLabel("释放财运刷新");
                        currentPage = 1;
                        startDataRequest();
                    }

                    @Override
                    public void onPullUpToRefresh(
                            PullToRefreshBase<ListView> refreshView) {
                        refreshView.getLoadingLayoutProxy().setPullLabel("加载更多");
                        refreshView.getLoadingLayoutProxy().setRefreshingLabel("正在获取数据...");
                        refreshView.getLoadingLayoutProxy().setReleaseLabel("松开获取更多数据");
                        ++currentPage;
                        startDataRequest();

                    }
                });
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
    }

    private class MyHandler extends Handler {
        public void handleMessage(Message paramMessage) {
            Bundle localBundle1 = paramMessage.getData();
            ArrayList<Parcelable> product_Rlist = localBundle1.getParcelableArrayList(BaseParam.QIAN_REQUEAT_PRODUCT_MORE_LIST);
            if (null != progressDialog && progressDialog.isShowing()) {// 隐藏加载框
                ProductRecordAct.this.progressDialog.dismiss();
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
                    // toastShow("获取成功！！！");

                } else {

                    if (map.get("errorCode").equals("no")) { //代表没有数据
                        if (list1.size() == 0) {  //就算是没数据 也不会是0  有1  errorCode =no resultCode=0
                            product_more_linear.setVisibility(View.GONE);
                            no_note.setVisibility(View.VISIBLE);
                        } else {
                            if (currentPage==1){
                                product_more_linear.setVisibility(View.GONE);
                                no_note.setVisibility(View.VISIBLE);
                            }else {
                                showToast("没有数据可获取");
                            }

                        }

                    } else {
                        product_more_linear.setVisibility(View.GONE);
                        no_note.setVisibility(View.VISIBLE);
                        showToast(Check.checkReturn(map.get("errorCode")));
                    }
                }
            }

            product_record_list.onRefreshComplete();
            super.handleMessage(paramMessage);
        }
    }
    private void startDataRequest() {
        if (Check.hasInternet(ProductRecordAct.this)) {
            ProductRecordAct.this.initArray();
            ProductRecordAct.this.param.add(BaseParam.QIAN_PERNUM);
            ProductRecordAct.this.value.add(pernum + "");
            ProductRecordAct.this.param.add(BaseParam.QIAN_CURRENTPAGE);
            ProductRecordAct.this.value.add(currentPage + "");
            ProductRecordAct.this.param.add(BaseParam.QIAN_PRODUCT_BORROWID);
            ProductRecordAct.this.value.add(borrowId + "");

            ProductRecordAct.this.param.add(BaseParam.URL_QIAN_API_APPID);
            ProductRecordAct.this.value.add(myApp.appId);
            ProductRecordAct.this.param.add(BaseParam.URL_QIAN_API_SERVICE);
            ProductRecordAct.this.value.add("tenderRecord");
            ProductRecordAct.this.param.add(BaseParam.URL_QIAN_API_SIGNTYPE);
            ProductRecordAct.this.value.add(myApp.signType);

            String[] array = new String[]{
                    BaseParam.QIAN_PERNUM + "=" + pernum + "",
                    BaseParam.QIAN_CURRENTPAGE + "=" + currentPage + "",
                    BaseParam.QIAN_PRODUCT_BORROWID + "=" + borrowId + "",
                    BaseParam.URL_QIAN_API_APPID + "=" + myApp.appId,
                    BaseParam.URL_QIAN_API_SERVICE + "=tenderRecord",
                    BaseParam.URL_QIAN_API_SIGNTYPE + "=" + myApp.signType};

            String sign = apiModel.sortStringArray(array);
            ProductRecordAct.this.param.add(BaseParam.URL_QIAN_API_SIGN);
            ProductRecordAct.this.value.add(sign);
            ProductRecordAct.this.progressDialog = ProductRecordAct.this.dia
                    .getLoginDialog(ProductRecordAct.this, "正在获取数据..");
            ProductRecordAct.this.progressDialog.show();
            new Thread(new JsonRequeatThreadProductMoreList(
                    ProductRecordAct.this,
                    myApp,
                    ProductRecordAct.this.myHandler,
                    ProductRecordAct.this.param,
                    ProductRecordAct.this.value)
            ).start();

        } else {
            showToast("请检查网络连接是否正常");
        }

    }

    private String startWebViewRequest() {
        String network = isWifi(ProductRecordAct.this);
        String[] array = new String[]{
                BaseParam.QIAN_PRODUCT_BORROWID + "=" + borrowId + "",
                "networkType=" + network,
                BaseParam.URL_QIAN_API_APPID + "=" + myApp.appId,
                BaseParam.URL_QIAN_API_SERVICE + "=projectDetail",
                BaseParam.URL_QIAN_API_SIGNTYPE + "=" + myApp.signType};

        String sign = apiModel.sortStringArray(array);

        String url = BaseParam.URL_QIAN_PROJECTDETAIL + "?"
                + BaseParam.QIAN_PRODUCT_BORROWID + "=" + borrowId + "&" + "networkType=" + network + "&"
                + BaseParam.URL_QIAN_API_APPID + "=" + myApp.appId + "&"
                + BaseParam.URL_QIAN_API_SERVICE + "=projectDetail&"
                + BaseParam.URL_QIAN_API_SIGNTYPE + "=" + myApp.signType + "&"
                + BaseParam.URL_QIAN_API_SIGN + "=" + sign;
        System.out.println("webview = " + url);
        return url;
        //
    }

    private static String isWifi(Context mContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return "1";
        }
        return "0";
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
}
