package com.rd.qnz.mine;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.orhanobut.logger.Logger;
import com.rd.qnz.BaseActivity;
import com.rd.qnz.R;
import com.rd.qnz.adapter.MyPaymentListAdapter2;
import com.rd.qnz.calendar.ADCircleCalendarView;
import com.rd.qnz.calendar.CalendarInfo;
import com.rd.qnz.calendar.MonthView;
import com.rd.qnz.custom.APIModel;
import com.rd.qnz.custom.MineShow;
import com.rd.qnz.custom.MyApplication;
import com.rd.qnz.dialog.CustomProgressDialog;
import com.rd.qnz.dialog.GetDialog;
import com.rd.qnz.entity.RecordItem;
import com.rd.qnz.entity.TimeBean;
import com.rd.qnz.tools.BaseParam;
import com.rd.qnz.tools.Check;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 回款日历
 *
 * @author Evonne by 2016/12/15
 */
public class MyPaymentListAct extends BaseActivity implements ADCircleCalendarView.OnCouponChangeListener {

    private static final String TAG = "我的回款记录";
    private List<RecordItem> dataList;
    private ListView listview;
    private MyPaymentListAdapter2 adapter;
    private GetDialog dia;
    private CustomProgressDialog progressDialog = null;
    private String oauthToken = "";
    private TextView my_tender_text, my_tender_time;
    private ADCircleCalendarView circleCalendarView;
    private LinearLayout my_tender_linear;
    private RelativeLayout no_note, tender_top_bar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mypayment_list);
        this.dia = new GetDialog();
        SharedPreferences preferences = getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_USER, Context.MODE_PRIVATE);
        oauthToken = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN, "");
        initBar();
        initView();
    }

    private void initBar() {
        RelativeLayout main_top_relativeLayout = (RelativeLayout) findViewById(R.id.main_top_relativeLayout);
        main_top_relativeLayout.setBackgroundColor(getResources().getColor(R.color.app_text_org));
        ImageView actionbar_side_left_iconfont = (ImageView) findViewById(R.id.actionbar_side_left_iconfont);
        actionbar_side_left_iconfont.setVisibility(View.VISIBLE);
        actionbar_side_left_iconfont.setImageResource(R.drawable.main_topleft_w);
        actionbar_side_left_iconfont.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                finish();
            }
        });
        TextView actionbar_side_name = (TextView) findViewById(R.id.actionbar_side_name);
        my_tender_text = (TextView) findViewById(R.id.my_tender_text);
        my_tender_time = (TextView) findViewById(R.id.my_tender_time);
        actionbar_side_name.setText("回款日历");
        actionbar_side_name.setTextColor(Color.WHITE);
        my_tender_text.setText("回款金额/回款至");

    }

    private void initView() {
        no_note = (RelativeLayout) findViewById(R.id.no_note); //没有数据时对应的布局
        my_tender_linear = (LinearLayout) findViewById(R.id.my_tender_linear); //有数据时对应的布局
        tender_top_bar = (RelativeLayout) findViewById(R.id.tender_top_bar);  //项目/回款时间(只是用来显示或者隐藏的)
        listview = (ListView) findViewById(R.id.my_tender_list);

        circleCalendarView = (ADCircleCalendarView) findViewById(R.id.circleMonthView);
        List<CalendarInfo> list_info = new ArrayList<CalendarInfo>();
        list_info.add(new CalendarInfo(0, 0, 0, " "));
        circleCalendarView.setCalendarInfos(list_info);

        dataList = new ArrayList<RecordItem>();
        adapter = new MyPaymentListAdapter2(MyPaymentListAct.this, dataList);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                if (MineShow.fastClick()) {
                    Intent intent = new Intent(MyPaymentListAct.this, MyuseMoneyDetailAct.class);
                    intent.putExtra("tenderId", dataList.get(arg2).getTenderId());
                    intent.putExtra("oauthToken", oauthToken);
                    startActivity(intent);
                }
            }
        });
        getDay(System.currentTimeMillis() + "");   //先默认请求一下当前接口
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = formatter.format(curDate);
        getData(date2TimeStamp(str));


        circleCalendarView.setDateClick(new MonthView.IDateClick() {   //点击哪一个就根据这个日期去请求
            @Override
            public void onClickOnDate(int year, int month, int day) {
                getData(date2TimeStamp(year + "-" + month + "-" + day));
            }
        });
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG); // 统计页面
        MobclickAgent.onResume(this); // 统计时长
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(TAG); // 保证 onPageEnd 在onPause 之前调用,因为 onPause
        // // 中会保存信息
        MobclickAgent.onPause(this);
    }
    /**
     * 日期格式字符串转换成时间戳
     *
     * @return
     */
    public static String date2TimeStamp(String date_str) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            return String.valueOf(sdf.parse(date_str).getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    /*
     请求投资记录端口
     */
    private void getData(String date) {
        if (Check.hasInternet(MyPaymentListAct.this)) {
            JSONObject param = new JSONObject();
            MyApplication myApp = MyApplication.getInstance();
            String sign = null;
            try {
                param.put(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN, oauthToken);
                param.put("time", date);
                param.put(BaseParam.URL_QIAN_API_APPID, myApp.appId);
                param.put(BaseParam.URL_QIAN_API_SERVICE, "productList1xV4");
                param.put(BaseParam.URL_QIAN_API_SIGNTYPE, myApp.signType);

                String[] array = new String[]{
                        BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN + "=" + oauthToken + "",
                        "time=" + date,
                        BaseParam.URL_QIAN_API_APPID + "=" + myApp.appId,
                        BaseParam.URL_QIAN_API_SERVICE + "=productList1xV4",
                        BaseParam.URL_QIAN_API_SIGNTYPE + "=" + myApp.signType};

                APIModel apiModel = new APIModel();
                sign = apiModel.sortStringArray(array);
                param.put(BaseParam.URL_QIAN_API_SIGN, sign);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            String url = BaseParam.URL_QIAN_MY_RETURNRECORD2
                    + "?oauthToken=" + oauthToken
                    + "&" + "time=" + date
                    + "&" + "appId=" + myApp.appId
                    + "&" + "service=" + "productList1xV4"
                    + "&" + "signType=" + myApp.signType
                    + "&" + "sign=" + sign;

            OkGo.post(url)
                    .tag(this)
                    .upJson(param.toString())
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(String s, Call call, Response response) {
                            Log.i("回款日列表", "------");
                            Logger.json(s.toString());
                            if (null != progressDialog && progressDialog.isShowing()) {// 隐藏加载框
                                progressDialog.dismiss();
                            }
                            dataList.clear();
                            try {
                                JSONObject object = new JSONObject(s);
                                int errorcode = object.getInt("resultCode");
                                if (errorcode == 1) {
                                    JSONObject oj1 = new JSONObject(Check.jsonGetStringAnalysis(object, "resultData"));
                                    JSONArray jsonArray = oj1.getJSONArray("list");
                                    if (jsonArray.length() > 0) {
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            JSONObject obj = jsonArray.getJSONObject(i);
                                            RecordItem item = new RecordItem();
                                            item.setBorrowId(Check.jsonGetStringAnalysis(obj, BaseParam.QIAN_MY_REPAY_BORROWID));
                                            item.setBorrowName(Check.jsonGetStringAnalysis(obj, BaseParam.QIAN_MY_REPAY_BORROWNAME));
                                            item.setCaptial(Check.jsonGetStringAnalysis(obj, BaseParam.QIAN_MY_REPAY_CAPTIAL));
                                            item.setInterest(Check.jsonGetStringAnalysis(obj, BaseParam.QIAN_MY_REPAY_INTEREST));
                                            item.setRepaymenTime(Check.jsonGetStringAnalysis(obj, BaseParam.QIAN_MY_REPAY_REPAYMENTIME));
                                            item.setRepaymentAccount(Check.jsonGetStringAnalysis(obj, BaseParam.QIAN_MY_REPAY_REPAYMENTACCOUNT));
                                            item.setTenderId(Check.jsonGetStringAnalysis(obj, BaseParam.QIAN_MY_REPAY_TENDERID));
                                            item.setTenderTime(Check.jsonGetStringAnalysis(obj, BaseParam.QIAN_MY_REPAY_TENDERTIME));
                                            item.setIsbin(Check.jsonGetStringAnalysis(obj, BaseParam.QIAN_MY_REPAY_ISBIN));
                                            item.setBankName(Check.jsonGetStringAnalysis(obj, BaseParam.QIAN_MY_REPAY_BANKNAME));
                                            item.setHiddenCardNo(Check.jsonGetStringAnalysis(obj, BaseParam.QIAN_MY_REPAY_HIDDENCARDNO));
                                            item.setBackPlace(Check.jsonGetStringAnalysis(obj, BaseParam.QIAN_MY_REPAY_BACKPLACE));
                                            dataList.add(item);
                                            Log.i("回款日列表", obj.toString() + "");

                                        }
                                    }
                                    Log.i("回款日列表", dataList.size() + "");
                                    adapter.notifyDataSetChanged();
                                    if (dataList.size() == 0) {
                                        no_note.setVisibility(View.VISIBLE);
                                        my_tender_linear.setVisibility(View.GONE);
                                        tender_top_bar.setVisibility(View.GONE);
                                    } else {
                                        no_note.setVisibility(View.GONE);
                                        my_tender_linear.setVisibility(View.VISIBLE);
                                        tender_top_bar.setVisibility(View.VISIBLE);
                                    }
                                }
                            } catch (JSONException e) {
                                if (null != progressDialog && progressDialog.isShowing()) {// 隐藏加载框
                                    progressDialog.dismiss();
                                    showToast("回款日列表");
                                }
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(Call call, Response response, Exception e) {
                            super.onError(call, response, e);
                            Log.i("回款日列表", e.toString());
                            if (null != progressDialog && progressDialog.isShowing()) {// 隐藏加载框
                                progressDialog.dismiss();
                                showToast("获取数据失败");
                            }
                        }
                    });
        } else {
            showToast("请检查网络连接是否正常");
        }
    }

    /**
     *  请求	回款日端接口
     * @param time
     */
    private void getDay(String time) {
        if (Check.hasInternet(MyPaymentListAct.this)) {
            JSONObject param = new JSONObject();
            MyApplication myApp = MyApplication.getInstance();
            String sign = null;
            try {
                param.put(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN, oauthToken);
                param.put("time", time);
                param.put(BaseParam.URL_QIAN_API_APPID, myApp.appId);
                param.put(BaseParam.URL_QIAN_API_SERVICE, "returnRecordTime");
                param.put(BaseParam.URL_QIAN_API_SIGNTYPE, myApp.signType);

                String[] array = new String[]{
                        BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN + "=" + oauthToken + "",
                        "time" + "=" + time + "",
                        BaseParam.URL_QIAN_API_APPID + "=" + myApp.appId,
                        BaseParam.URL_QIAN_API_SERVICE + "=returnRecordTime",
                        BaseParam.URL_QIAN_API_SIGNTYPE + "=" + myApp.signType};

                APIModel apiModel = new APIModel();
                sign = apiModel.sortStringArray(array);
                param.put(BaseParam.URL_QIAN_API_SIGN, sign);

                progressDialog = dia.getLoginDialog(MyPaymentListAct.this, "正在获取数据..");
                progressDialog.show();

            } catch (JSONException e) {
                e.printStackTrace();
            }
            String url = BaseParam.URL_QIAN_PAYMENTDATE
                    + "?oauthToken=" + oauthToken
                    + "&" + "time=" + time
                    + "&" + "appId=" + myApp.appId
                    + "&" + "service=" + "returnRecordTime"
                    + "&" + "signType=" + myApp.signType
                    + "&" + "sign=" + sign;

            OkGo.post(url)
                    .tag(this)
                    .upJson(param.toString())
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(String s, Call call, Response response) {
                            Log.i("回款日", "------");
                            Logger.json(s.toString());
                            if (null != progressDialog && progressDialog.isShowing()) {// 隐藏加载框
                                progressDialog.dismiss();
                            }
                            Gson gson = new Gson();
                            TimeBean timeBean = gson.fromJson(s, TimeBean.class);
                            if (timeBean.getResultCode() == 1) {
                                TimeBean.ResultDataBean resultData = timeBean.getResultData();
                                List<String> listt = resultData.getTimeList();
                                List<CalendarInfo> list = new ArrayList<CalendarInfo>();
                                if (listt.size() > 0) {
                                    for (int i = 0; i < listt.size(); i++) {
                                        String year = new SimpleDateFormat("yyyy").format(new Date(Long.valueOf(listt.get(i))));
                                        String month = new SimpleDateFormat("MM").format(new Date(Long.valueOf(listt.get(i))));
                                        String day = new SimpleDateFormat("dd").format(new Date(Long.valueOf(listt.get(i))));
                                        list.add(new CalendarInfo(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day), " "));
                                        circleCalendarView.setCalendarInfos(list);
                                    }
                                }
                            }
                        }

                        @Override
                        public void onError(Call call, Response response, Exception e) {
                            super.onError(call, response, e);
                            Log.i("回款日", e.toString());
                            if (null != progressDialog && progressDialog.isShowing()) {// 隐藏加载框
                                progressDialog.dismiss();
                                showToast("获取数据失败");
                            }
                        }
                    });
        } else {
            showToast("请检查网络连接是否正常");
        }
    }

    /**
     *  当点击另一个月的时候会触发该方法,比如说我点击2017年1月 会传入2017-1-01
     * @param date
     */
    public void OnCouponChange(String date) {
        getDay(date2TimeStamp(date));

    }

}
