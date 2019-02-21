package com.rd.qnz.mine;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
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
import com.rd.qnz.login.Login;
import com.rd.qnz.tools.BaseParam;
import com.rd.qnz.tools.Check;
import com.rd.qnz.tools.webservice.JsonRequeatThreadGetHistoryTender;
import com.rd.qnz.tools.webservice.JsonRequeatThreadMyRepaymentGaiList;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 投资回款记录
 *
 * @author Evonne
 */
public class MyTenderRecordListAct extends BaseActivity {

    private static final String TAG = "投资回款记录";
    private PullToRefreshListView my_tender_list;

    private MyTenderRecordListAdapter listAdapter;
    private ArrayList<String> param = null;
    private ArrayList<String> value = null;
    private GetDialog dia;
    private MyHandler myHandler;
    private CustomProgressDialog progressDialog = null;
    private MyApplication myApp;
    private LinearLayout no_note;
    private ImageView no_note_img;
    private LinearLayout my_tender_linear;

    APIModel apiModel = new APIModel();

    private boolean isFlag = true;// true为投资记录 false 为回款计划
    private TextView my_tender_text;
    private TextView my_tender_time;

    private int pernum = 12;// 每页显示个数
    private int currentPage = 1;

    private String oauthToken = "";
    private String status = "5";// 投资与回款的类型(1:投资中 2:待收款 3:已收款 4:所有投资)钱内助暂时用到的状态为4和2
    List<Map<String, String>> list = new ArrayList<Map<String, String>>();

    private String type = "2";// 状态1:已回款,2:待回款

    private int yi_currentPage = 1;
    private int dai_currentPage = 1;

    List<Map<String, String>> list1 = new ArrayList<Map<String, String>>();// 1:已回款list
    List<Map<String, String>> list2 = new ArrayList<Map<String, String>>();// 2:待回款

    private boolean mDingQiIsHaveData = false;//定期是否有数据


    private Context context;

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

    private class MyHandler extends Handler {
        public void handleMessage(Message paramMessage) {
            Bundle localBundle1 = paramMessage.getData();
            ArrayList<Parcelable> product_Rlist = localBundle1
                    .getParcelableArrayList(BaseParam.QIAN_REQUEAT_MY_TENDERRECORD);//定期理财
            ArrayList<Parcelable> repay_Rlist = localBundle1
                    .getParcelableArrayList(BaseParam.QIAN_REQUEAT_MY_RETURNRECORD);//回款记录

            if (null != progressDialog && progressDialog.isShowing()) {// 隐藏加载框
                MyTenderRecordListAct.this.progressDialog.dismiss();
            }
            if (null != product_Rlist) {//定期理财
                @SuppressWarnings("unchecked")
                List<Map<String, String>> r_list = (List<Map<String, String>>) product_Rlist.get(0);

                Map<String, String> map = r_list.get(0);
                String resultCode = map.get("resultCode");
                if (resultCode.equals("1")) {
                    initData(r_list);
                    if (isFlag) {
                        listAdapter.notifyDataSetChanged(list, isFlag, type);
                    }
                } else {
                    String errorCode = map.get("errorCode");
                    if (map.get("errorCode").equals("no_note")) {
                        if (list.size() == 0) {
                            mDingQiIsHaveData = false;
                        } else {
                            showToast("没有数据可获取");
                        }
                    } else if (errorCode.equals("TOKEN_NOT_EXIST")) {
                        showToast("身份证验证不存在,请重新登录");
                        startActivity(new Intent(MyTenderRecordListAct.this, Login.class));
                    } else if (errorCode.equals("TOKEN_EXPIRED")) {
                        showToast("身份证验证不存在,请重新登录");
                        startActivity(new Intent(MyTenderRecordListAct.this, Login.class));
                    } else {
                        my_tender_linear.setVisibility(View.GONE);
                        no_note.setVisibility(View.VISIBLE);
                        if (isFlag) {//投资记录
                            no_note_img.setImageResource(R.drawable.norecord);
                        } else {
                            no_note_img.setImageResource(R.drawable.notender);
                        }
                        showToast(errorCode);
                    }
                    if (listAdapter.getCount() > 0) {
                        mDingQiIsHaveData = true;
                    } else {
                        mDingQiIsHaveData = false;
                    }
                    initNoRecordView();
                }
            }

            if (null != repay_Rlist) {// 回款记录
                List<Map<String, String>> r_list = (List<Map<String, String>>) repay_Rlist.get(0);
                Map<String, String> map = r_list.get(0);
                String resultCode = map.get("resultCode");

                if (resultCode.equals("1")) {
                    my_tender_linear.setVisibility(View.VISIBLE);
                    no_note.setVisibility(View.GONE);
                    initData(r_list);
                    if (type.equals("1")) {
                        my_tender_linear.setVisibility(View.VISIBLE);
                        no_note.setVisibility(View.GONE);
                        listAdapter.notifyDataSetChanged(list1, isFlag, type);
                    } else {
                        listAdapter.notifyDataSetChanged(list2, isFlag, type);
                    }

                } else {
                    String errorCode = map.get("errorCode");
                    if (TextUtils.equals(errorCode, "no_note")) {
                        if (type.equals("1")) {
                            if (list1.size() == 0) {
                                my_tender_linear.setVisibility(View.GONE);
                                no_note.setVisibility(View.VISIBLE);
                                if (isFlag) {//投资记录
                                    no_note_img.setImageResource(R.drawable.norecord);
                                } else {
                                    no_note_img.setImageResource(R.drawable.notender);
                                }
                            } else {
                                showToast("没有数据可获取");
                            }
                        } else {
                            if (list2.size() == 0) {
                                my_tender_linear.setVisibility(View.GONE);
                                no_note.setVisibility(View.VISIBLE);
                                if (isFlag) {//投资记录
                                    no_note_img.setImageResource(R.drawable.norecord);
                                } else {
                                    no_note_img.setImageResource(R.drawable.notender);
                                }
                            } else {
                                showToast("没有数据可获取");
                            }
                        }
                    } else if (errorCode.equals("TOKEN_NOT_EXIST")) {
                        showToast("身份证验证不存在,请重新登录");
                        startActivity(new Intent(MyTenderRecordListAct.this, Login.class));
                    } else if (errorCode.equals("TOKEN_EXPIRED")) {
                        showToast("身份证验证不存在,请重新登录");
                        startActivity(new Intent(MyTenderRecordListAct.this, Login.class));
                    } else {
                        my_tender_linear.setVisibility(View.GONE);
                        no_note.setVisibility(View.VISIBLE);
                        if (isFlag) {//投资记录
                            no_note_img.setImageResource(R.drawable.norecord);
                        } else {
                            no_note_img.setImageResource(R.drawable.notender);
                        }
                        showToast(errorCode);
                    }
                }
            }

            my_tender_list.onRefreshComplete();
            super.handleMessage(paramMessage);
        }
    }

    @Override
    protected void onRestart() {
        // TODO Auto-generated method stub
        super.onRestart();
        Log.e("投资回款记录", "onRestart");
        if (isFlag) {
            if (currentPage == 1) {
                list.clear();
            }
        } else {
            if (type.equals("1")) {
                yi_currentPage = 1;
                list1.clear();
            } else {
                dai_currentPage = 1;
                list2.clear();
            }
        }
        startDataRequest();
        list.clear();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_tenderrecord_list_gai);
        context = MyTenderRecordListAct.this;
        this.dia = new GetDialog();
        myApp = (MyApplication) getApplication();
        this.myHandler = new MyHandler();
        SharedPreferences preferences = getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_USER, Context.MODE_PRIVATE);
        oauthToken = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN, "");
        isFlag = getIntent().getBooleanExtra("isFlag", true);
        initBar();
        initView();
        startDataRequest();
    }

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

        my_tender_text = (TextView) findViewById(R.id.my_tender_text);
        my_tender_time = (TextView) findViewById(R.id.my_tender_time);

        if (isFlag) {
            actionbar_side_name.setText("投资记录");

            my_tender_time.setText("项目/投资时间");
            my_tender_text.setText("投资/收益");
            status = "5";
        } else {
            actionbar_side_name.setText("回款记录");

            my_tender_time.setText("项目/回款时间");
            my_tender_text.setText("回款金额/回款至");
            status = "2";
        }

    }

    private void initView() {
        no_note = (LinearLayout) findViewById(R.id.no_note);//无数据
        no_note_img = (ImageView) findViewById(R.id.no_note_img);
        my_tender_linear = (LinearLayout) findViewById(R.id.my_tender_linear);//PullToRefreshListView的布局
        my_tender_list = (PullToRefreshListView) findViewById(R.id.my_tender_list);
        my_tender_list.setMode(Mode.BOTH);
        listAdapter = new MyTenderRecordListAdapter(MyTenderRecordListAct.this, list, isFlag);

        my_tender_list.setAdapter(listAdapter);
        my_tender_list.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                if (isFlag) {//投资记录
                    Intent intent = new Intent(MyTenderRecordListAct.this, MyuseMoneyDetailAct.class);
                    intent.putExtra("tenderId", list.get(arg2 - 1).get(BaseParam.QIAN_USEMONEY_TENDERID));
                    intent.putExtra("oauthToken", oauthToken);
                    startActivity(intent);
                } else {//回款记录
                    Intent intent = new Intent(MyTenderRecordListAct.this, MyuseMoneyDetailAct.class);
                    if (type.equals("1")) {
                        intent.putExtra("tenderId", list1.get(arg2 - 1).get(BaseParam.QIAN_USEMONEY_TENDERID));
                    } else {
                        intent.putExtra("tenderId", list2.get(arg2 - 1).get(BaseParam.QIAN_USEMONEY_TENDERID));
                    }
                    intent.putExtra("oauthToken", oauthToken);
                    startActivity(intent);
                }
            }
        });

        my_tender_list.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

                refreshView.getLoadingLayoutProxy().setPullLabel("释放财运刷新");
                refreshView.getLoadingLayoutProxy().setRefreshingLabel("财运刷新中...");
                refreshView.getLoadingLayoutProxy().setReleaseLabel("释放财运刷新");

                if (isFlag) {
                    currentPage = 1;
                } else {
                    if (type.equals("1")) {
                        yi_currentPage = 1;
                    } else {
                        dai_currentPage = 1;
                    }
                }
                startDataRequest();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                refreshView.getLoadingLayoutProxy().setPullLabel("加载更多");
                refreshView.getLoadingLayoutProxy().setRefreshingLabel("正在获取数据...");
                refreshView.getLoadingLayoutProxy().setReleaseLabel("松开获取更多数据");

                if (isFlag) {
                    ++currentPage;
                } else {
                    if (type.equals("1")) {
                        ++yi_currentPage;
                    } else {
                        ++dai_currentPage;
                    }
                }
                startDataRequest();

            }
        });
    }

    private void initNoRecordView() {
        if (isFlag) {
            if (mDingQiIsHaveData) {
                no_note.setVisibility(View.GONE);
                my_tender_linear.setVisibility(View.VISIBLE);
            } else {
                no_note.setVisibility(View.VISIBLE);
                if (isFlag) {//投资记录
                    no_note_img.setImageResource(R.drawable.norecord);
                } else {
                    no_note_img.setImageResource(R.drawable.notender);
                }
                my_tender_linear.setVisibility(View.GONE);
            }
        }

    }

    private void startDataRequest() {
        if (Check.hasInternet(MyTenderRecordListAct.this)) {
            if (isFlag) {
                MyTenderRecordListAct.this.initArray();
                MyTenderRecordListAct.this.param.add(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN);
                MyTenderRecordListAct.this.value.add(oauthToken);
                MyTenderRecordListAct.this.param.add("status");
                MyTenderRecordListAct.this.value.add(status);
                MyTenderRecordListAct.this.param.add(BaseParam.QIAN_PERNUM);
                MyTenderRecordListAct.this.value.add(pernum + "");
                MyTenderRecordListAct.this.param.add(BaseParam.QIAN_CURRENTPAGE);
                MyTenderRecordListAct.this.value.add(currentPage + "");
                MyTenderRecordListAct.this.param.add(BaseParam.URL_QIAN_API_APPID);
                MyTenderRecordListAct.this.value.add(myApp.appId);
                MyTenderRecordListAct.this.param.add(BaseParam.URL_QIAN_API_SERVICE);
                MyTenderRecordListAct.this.value.add("myTenderRecord");
                MyTenderRecordListAct.this.param.add(BaseParam.URL_QIAN_API_SIGNTYPE);
                MyTenderRecordListAct.this.value.add(myApp.signType);
                String[] array = new String[]{
                        BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN + "=" + oauthToken + "",
                        "status=" + status + "",
                        BaseParam.QIAN_CURRENTPAGE + "=" + currentPage + "",
                        BaseParam.QIAN_PERNUM + "=" + pernum + "",
                        BaseParam.URL_QIAN_API_APPID + "=" + myApp.appId,
                        BaseParam.URL_QIAN_API_SERVICE + "=myTenderRecord",
                        BaseParam.URL_QIAN_API_SIGNTYPE + "=" + myApp.signType};
                String sign = apiModel.sortStringArray(array);
                MyTenderRecordListAct.this.param.add(BaseParam.URL_QIAN_API_SIGN);
                MyTenderRecordListAct.this.value.add(sign);

                progressDialog = dia.getLoginDialog(MyTenderRecordListAct.this, "正在获取数据..");
                progressDialog.show();

                new Thread(new JsonRequeatThreadGetHistoryTender(
                        MyTenderRecordListAct.this, myApp,
                        MyTenderRecordListAct.this.myHandler,
                        MyTenderRecordListAct.this.param,
                        MyTenderRecordListAct.this.value)
                ).start();
            } else {
                MyTenderRecordListAct.this.initArray();
                MyTenderRecordListAct.this.param.add(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN);
                MyTenderRecordListAct.this.value.add(oauthToken);
                MyTenderRecordListAct.this.param.add(BaseParam.QIAN_PERNUM);
                MyTenderRecordListAct.this.value.add(pernum + "");
                MyTenderRecordListAct.this.param.add(BaseParam.QIAN_CURRENTPAGE);
                if (type.equals("1")) {
                    MyTenderRecordListAct.this.value.add(yi_currentPage + "");
                } else {
                    MyTenderRecordListAct.this.value.add(dai_currentPage + "");
                }
                MyTenderRecordListAct.this.param.add("type");
                MyTenderRecordListAct.this.value.add(type);
                MyTenderRecordListAct.this.param.add(BaseParam.URL_QIAN_API_APPID);
                MyTenderRecordListAct.this.value.add(myApp.appId);
                MyTenderRecordListAct.this.param.add(BaseParam.URL_QIAN_API_SERVICE);
                MyTenderRecordListAct.this.value.add("returnRecord");
                MyTenderRecordListAct.this.param.add(BaseParam.URL_QIAN_API_SIGNTYPE);
                MyTenderRecordListAct.this.value.add(myApp.signType);
                String[] array;
                if (type.equals("1")) {
                    array = new String[]{
                            BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN + "=" + oauthToken + "",
                            BaseParam.QIAN_CURRENTPAGE + "=" + yi_currentPage + "",
                            "type=" + type,
                            BaseParam.QIAN_PERNUM + "=" + pernum + "",
                            BaseParam.URL_QIAN_API_APPID + "=" + myApp.appId,
                            BaseParam.URL_QIAN_API_SERVICE + "=returnRecord",
                            BaseParam.URL_QIAN_API_SIGNTYPE + "=" + myApp.signType};
                } else {
                    array = new String[]{
                            BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN + "=" + oauthToken + "",
                            BaseParam.QIAN_CURRENTPAGE + "=" + dai_currentPage + "",
                            "type=" + type,
                            BaseParam.QIAN_PERNUM + "=" + pernum + "",
                            BaseParam.URL_QIAN_API_APPID + "=" + myApp.appId,
                            BaseParam.URL_QIAN_API_SERVICE + "=returnRecord",
                            BaseParam.URL_QIAN_API_SIGNTYPE + "=" + myApp.signType};
                }

                String sign = apiModel.sortStringArray(array);
                MyTenderRecordListAct.this.param.add(BaseParam.URL_QIAN_API_SIGN);
                MyTenderRecordListAct.this.value.add(sign);

                progressDialog = dia.getLoginDialog(MyTenderRecordListAct.this, "正在获取数据..");
                progressDialog.show();

                new Thread(new JsonRequeatThreadMyRepaymentGaiList(
                        MyTenderRecordListAct.this, myApp,
                        MyTenderRecordListAct.this.myHandler,
                        MyTenderRecordListAct.this.param,
                        MyTenderRecordListAct.this.value)
                ).start();
            }
        } else {
            showToast("请检查网络连接是否正常");
        }
    }

    /**
     * 初始化适配器的数据源
     *
     * @param paramList
     */
    private void initData(List<Map<String, String>> paramList) {
        if (paramList == null || paramList.size() == 0) {
            return;
        }
        if (isFlag) {
            if (currentPage == 1) {
                list.clear();
            }
        } else {
            if (type.equals("1")) {
                if (yi_currentPage == 1) {
                    list1.clear();
                }
            } else {
                if (dai_currentPage == 1) {
                    list2.clear();
                }
            }
        }

        for (int i = 0; i < paramList.size(); i++) {
            if (isFlag) {
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
                map.put(BaseParam.QIAN_PRODUCT_INTEREST, paramList.get(i).get(BaseParam.QIAN_PRODUCT_INTEREST));
                list.add(map);
            } else {
                Map<String, String> map = new HashMap<String, String>();
                map.put(BaseParam.QIAN_MY_REPAY_BORROWID, paramList.get(i).get(BaseParam.QIAN_MY_REPAY_BORROWID));
                map.put(BaseParam.QIAN_MY_REPAY_BORROWNAME, paramList.get(i).get(BaseParam.QIAN_MY_REPAY_BORROWNAME));
                map.put(BaseParam.QIAN_MY_REPAY_CAPTIAL, paramList.get(i).get(BaseParam.QIAN_MY_REPAY_CAPTIAL));
                map.put(BaseParam.QIAN_MY_REPAY_INTEREST, paramList.get(i).get(BaseParam.QIAN_MY_REPAY_INTEREST));
                map.put(BaseParam.QIAN_MY_REPAY_REPAYMENTIME, paramList.get(i).get(BaseParam.QIAN_MY_REPAY_REPAYMENTIME));
                map.put(BaseParam.QIAN_MY_REPAY_REPAYMENTACCOUNT, paramList.get(i).get(BaseParam.QIAN_MY_REPAY_REPAYMENTACCOUNT));
                map.put(BaseParam.QIAN_MY_REPAY_TENDERID, paramList.get(i).get(BaseParam.QIAN_MY_REPAY_TENDERID));
                map.put(BaseParam.QIAN_MY_REPAY_TENDERTIME, paramList.get(i).get(BaseParam.QIAN_MY_REPAY_TENDERTIME));
                map.put(BaseParam.QIAN_MY_REPAY_ISBIN, paramList.get(i).get(BaseParam.QIAN_MY_REPAY_ISBIN));
                map.put(BaseParam.QIAN_MY_REPAY_BANKNAME, paramList.get(i).get(BaseParam.QIAN_MY_REPAY_BANKNAME));
                map.put(BaseParam.QIAN_MY_REPAY_HIDDENCARDNO, paramList.get(i).get(BaseParam.QIAN_MY_REPAY_HIDDENCARDNO));
                map.put(BaseParam.QIAN_MY_REPAY_BACKPLACE, paramList.get(i).get(BaseParam.QIAN_MY_REPAY_BACKPLACE));

                if (type.equals("1")) {
                    list1.add(map);
                } else {
                    list2.add(map);
                }
            }
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
