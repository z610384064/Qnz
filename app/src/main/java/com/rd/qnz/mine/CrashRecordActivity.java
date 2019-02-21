package com.rd.qnz.mine;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lzy.okgo.request.PostRequest;
import com.rd.qnz.BaseActivity;
import com.rd.qnz.R;
import com.rd.qnz.bean.BaseBean;
import com.rd.qnz.bean.CrashRecordBean;
import com.rd.qnz.custom.MineShow;
import com.rd.qnz.custom.MyApplication;
import com.rd.qnz.dialog.CustomProgressDialog;
import com.rd.qnz.dialog.GetDialog;
import com.rd.qnz.http.HttpUtils;
import com.rd.qnz.http.okgo.callback.JsonCallback;
import com.rd.qnz.login.Login;
import com.rd.qnz.tools.AppTool;
import com.rd.qnz.tools.BaseParam;
import com.rd.qnz.tools.Check;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 提现记录
 *
 * @author Evonne
 */
public class CrashRecordActivity extends BaseActivity {
    private ImageView mBackBtn;
    private PullToRefreshListView mListView;
    private int pernum = 10;// 每页显示个数
    private int currentPage = 1;// 当前页
    private MyAdapter mAdapter;
    private CustomProgressDialog mProgressDialog;
    private LinearLayout no_crash;
    private ImageView no_crash_img;

    private List<CrashRecordBean.CashDetailBean> mDataList=new ArrayList<>();
    public static void start(Context context) {
        Intent i = new Intent(context, CrashRecordActivity.class);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_account_balancelist2);
        initView();
    }

    private void initView() {
        no_crash = (LinearLayout) findViewById(R.id.no_crash);
        no_crash_img = (ImageView) findViewById(R.id.no_crash_img);
        no_crash_img.setImageResource(R.drawable.nocrash);
        mBackBtn = (ImageView) findViewById(R.id.actionbar_side_left_iconfont);
        mBackBtn.setVisibility(View.VISIBLE);
        mBackBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mListView = (PullToRefreshListView) findViewById(R.id.listview);
        TextView tv = (TextView) findViewById(R.id.actionbar_side_name);
        tv.setText("提现记录");

        mAdapter = new MyAdapter(this);
        mListView.setAdapter(mAdapter);
        mListView.setMode(Mode.BOTH);
        mListView.autoRefresh();
        mListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                refreshView.getLoadingLayoutProxy().setPullLabel("释放财运刷新");
                refreshView.getLoadingLayoutProxy().setRefreshingLabel("财运刷新中...");
                refreshView.getLoadingLayoutProxy().setReleaseLabel("释放财运刷新");
                currentPage = 1;
                getData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                refreshView.getLoadingLayoutProxy(false, true).setPullLabel("加载更多");
                refreshView.getLoadingLayoutProxy(false, true).setRefreshingLabel("正在获取数据...");
                refreshView.getLoadingLayoutProxy(false, true).setReleaseLabel("松开获取更多数据");
                ++currentPage;
                getData();
            }
        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (MineShow.fastClick()) {  //两次点击的间隔没有小于10秒
                   Intent i=new Intent(CrashRecordActivity.this,CrashRecordDetails.class);
                    i.putExtra("CashDetailBean",mDataList.get(position-1));
                    startActivity(i);
                }
            }
        });

    }

    private void getData() {

        MyApplication myApp = MyApplication.getInstance();
        SharedPreferences preferences = getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_USER, Context.MODE_PRIVATE);
        String oauthToken = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN, "");

        String[] str={BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN,oauthToken,
                BaseParam.QIAN_PERNUM,pernum+"",
                BaseParam.QIAN_CURRENTPAGE,currentPage + "",
                BaseParam.URL_QIAN_API_APPID,myApp.appId,
                BaseParam.URL_QIAN_API_SERVICE,"withdrawRecord",
                BaseParam.URL_QIAN_API_SIGNTYPE,myApp.signType,
                "new",1 + ""
        };
        if (mAdapter.getCount() == 0 || mAdapter == null) {
            GetDialog dia = new GetDialog();
            mProgressDialog = dia.getLoginDialog(CrashRecordActivity.this, "正在获取数据..");
            mProgressDialog.show();
        }

        PostRequest request=HttpUtils.getRequest(BaseParam.URL_QIAN_CASH_RECORD,CrashRecordActivity.this,str);
        request.execute(new JsonCallback<BaseBean<CrashRecordBean>>() {
            @Override
            public void onSuccess(BaseBean<CrashRecordBean> crashRecordBeanBaseBean, Call call, Response response) {
                if (null != mProgressDialog && mProgressDialog.isShowing()) { // 隐藏加载框
                    mProgressDialog.dismiss();
                }
                if (null != crashRecordBeanBaseBean) {
                    String resultCode=crashRecordBeanBaseBean.resultCode;
                    if (resultCode.equals("1")){
                        //验证码
                      List<CrashRecordBean.CashDetailBean> cashDetailBeen= crashRecordBeanBaseBean.resultData.getCashDetail();
                        if (mAdapter != null) {
                            if (currentPage > 1) {
                                mAdapter.addData(cashDetailBeen);
                            } else {
                                mAdapter.setData(cashDetailBeen);
                            }
                        }
                        if (mAdapter.getCount() > 0) {
                            no_crash.setVisibility(View.GONE);
                            mListView.setVisibility(View.VISIBLE);
                        } else {
                            no_crash.setVisibility(View.VISIBLE);
                            mListView.setVisibility(View.GONE);
                        }
                    }else {
                        MyApplication myApp = MyApplication.getInstance();
                        String errorCode =crashRecordBeanBaseBean.errorCode;
                        if (errorCode.equals("TOKEN_NOT_EXIST")) {
                            showToast("身份证验证不存在,请重新登录");
                            startActivity(new Intent(CrashRecordActivity.this, Login.class));
                            myApp.tabHostId = 0;
                            myApp.tabHost.setCurrentTab(myApp.tabHostId);
                        } else if (errorCode.equals("TOKEN_EXPIRED")) {
                            startActivity(new Intent(CrashRecordActivity.this, Login.class));
                            myApp.tabHostId = 0;
                            myApp.tabHost.setCurrentTab(myApp.tabHostId);
                        } else if (TextUtils.equals(errorCode, "no")) {
                            if (currentPage == 1) {
                                no_crash.setVisibility(View.VISIBLE);
                                mListView.setVisibility(View.GONE);
                            } else {
                                showToast("没有数据可获取");
                            }
                        } else {
                            showToast(Check.checkReturn(errorCode));
                        }
                    }

                }
                mListView.onRefreshComplete();


            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                if (null != mProgressDialog && mProgressDialog.isShowing()) { // 隐藏加载框
                    mProgressDialog.dismiss();
                }
                MineShow.toastShow("连接服务器异常,请稍后再试",CrashRecordActivity.this);
            }
        });

//        if (Check.hasInternet(CrashRecordActivity.this)) {
//            MyApplication myApp = MyApplication.getInstance();
//            SharedPreferences preferences = getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_USER, Context.MODE_PRIVATE);
//            String oauthToken = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN, "");
//            APIModel apiModel = new APIModel();
//            ArrayList<String> param = new ArrayList<String>();
//            ArrayList<String> value = new ArrayList<String>();
//            param.add(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN);
//            value.add(oauthToken);
//            param.add(BaseParam.QIAN_PERNUM);
//            value.add(pernum + "");
//            param.add(BaseParam.QIAN_CURRENTPAGE);
//            value.add(currentPage + "");
//            param.add(BaseParam.URL_QIAN_API_APPID);
//            value.add(myApp.appId);
//            param.add(BaseParam.URL_QIAN_API_SERVICE);
//            value.add("withdrawRecord");
//            param.add(BaseParam.URL_QIAN_API_SIGNTYPE);
//            value.add(myApp.signType);
//            param.add("new");
//            value.add(1 + "");
//
//            String[] array = new String[]{
//                    BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN + "=" + oauthToken,
//                    BaseParam.QIAN_PERNUM + "=" + pernum + "",
//                    BaseParam.QIAN_CURRENTPAGE + "=" + currentPage + "",
//                    BaseParam.URL_QIAN_API_APPID + "=" + myApp.appId,
//                    BaseParam.URL_QIAN_API_SERVICE + "=withdrawRecord",
//                    BaseParam.URL_QIAN_API_SIGNTYPE + "=" + myApp.signType,
//                    "new" + "=" + 1 + ""};
//            String sign = apiModel.sortStringArray(array);
//            param.add(BaseParam.URL_QIAN_API_SIGN);
//            value.add(sign);
//            if (mAdapter.getCount() == 0 || mAdapter == null) {
//                GetDialog dia = new GetDialog();
//                mProgressDialog = dia.getLoginDialog(CrashRecordActivity.this, "正在获取数据..");
//                mProgressDialog.show();
//            }
//
//            new Thread(new jsonRequestThreadGetCashRecordList(
//                    CrashRecordActivity.this,
//                    myApp,
//                    CrashRecordActivity.this.myHandler,
//                    param,
//                    value)
//            ).start();
//        } else {
//            showToast("请检查网络连接是否正常");
//        }
    }

//    Handler myHandler = new Handler() {
//        public void handleMessage(android.os.Message msg) {
//            Bundle localBundle1 = msg.getData();
//            ArrayList<Parcelable> product_Rlist = localBundle1.getParcelableArrayList(BaseParam.URL_QIAN_CASH_RECORD);
//            if (null != mProgressDialog && mProgressDialog.isShowing()) { // 隐藏加载框
//                mProgressDialog.dismiss();
//            }
//            if (null != product_Rlist) {
//                List<Map<String, String>> list1 = (List<Map<String, String>>) product_Rlist.get(0);
//                Map<String, String> map = list1.get(0);
//                String resultCode = map.get("resultCode");
//                if (resultCode.equals("1")) {
//                    //验证码fa
//                    if (mAdapter != null) {
//                        if (currentPage > 1) {
//                            mAdapter.addData(list1);
//                        } else {
//                            mAdapter.setData(list1);
//                        }
//                    }
//                    if (mAdapter.getCount() > 0) {
//                        no_crash.setVisibility(View.GONE);
//                        mListView.setVisibility(View.VISIBLE);
//                    } else {
//                        no_crash.setVisibility(View.VISIBLE);
//                        mListView.setVisibility(View.GONE);
//                    }
//                } else {
//                    MyApplication myApp = MyApplication.getInstance();
//                    String errorCode = map.get("errorCode");
//                    if (errorCode.equals("TOKEN_NOT_EXIST")) {
//                        showToast("身份证验证不存在,请重新登录");
//                        startActivity(new Intent(CrashRecordActivity.this, Login.class));
//                        myApp.tabHostId = 0;
//                        myApp.tabHost.setCurrentTab(myApp.tabHostId);
//                    } else if (errorCode.equals("TOKEN_EXPIRED")) {
//                        startActivity(new Intent(CrashRecordActivity.this, Login.class));
//                        myApp.tabHostId = 0;
//                        myApp.tabHost.setCurrentTab(myApp.tabHostId);
//                    } else if (TextUtils.equals(errorCode, "no")) {
//                        if (currentPage == 1) {
//                            no_crash.setVisibility(View.VISIBLE);
//                            mListView.setVisibility(View.GONE);
//                        } else {
//                            showToast("没有数据可获取");
//                        }
//                    } else {
//                        showToast(Check.checkReturn(map.get("errorCode")));
//                    }
//                }
//            }
//            mListView.onRefreshComplete();
//        }
//    };

    public class MyAdapter extends BaseAdapter {
        private Context mContext;


//        private List<Map<String, String>> mDataList = new ArrayList<Map<String, String>>();

        public MyAdapter(Context context) {
            mContext = context;
        }
        public void setData( List<CrashRecordBean.CashDetailBean> list) {
            mDataList.clear();
            mDataList.addAll(list);
            notifyDataSetChanged();
        }
        public void addData( List<CrashRecordBean.CashDetailBean> list) {
            mDataList.addAll(list);
            notifyDataSetChanged();
        }
//
//        public void setData(List<Map<String, String>> list) {
//            this.mDataList.clear();
//            this.mDataList = list;
//            notifyDataSetChanged();
//        }

//        public void addData(List<Map<String, String>> list) {
//            this.mDataList.addAll(list);
//            notifyDataSetChanged();
//        }

        @Override
        public int getCount() {
            return mDataList.size();
        }

        @Override
        public Object getItem(int position) {
            return mDataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder mHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_cash_record, null);
                mHolder = new Holder(convertView);
                convertView.setTag(mHolder);
            } else {
                mHolder = (Holder) convertView.getTag();
            }
            CrashRecordBean.CashDetailBean cashDetailBean = mDataList.get(position);
            String bankName = getString(R.string.balance_withdraw_bank_card_name_format,cashDetailBean.getBankName() , cashDetailBean.getBankNo());
            mHolder.name.setText(bankName);
            // mHolder.status.setText(map.get(BaseParam.QIAN_CASH_RECORD_STATUS_DESC));
            String status =cashDetailBean.getStatus();

                if (TextUtils.equals(status, "1")) {//提现成功
                    mHolder.status.setText("提现成功");
                    mHolder.status.setTextColor(mContext.getResources().getColor(R.color.account_balance2));
                } else if (TextUtils.equals(status, "2")) {//处理中
                    mHolder.status.setText("提现处理中");
                    mHolder.status.setTextColor(mContext.getResources().getColor(R.color.account_balance3));
                } else if (TextUtils.equals(status, "3")) {//提现失败
                    mHolder.status.setText("提现失败");
                    mHolder.status.setTextColor(mContext.getResources().getColor(R.color.account_balance1));
                }



            double money = 0;
            String itemMoney = cashDetailBean.getMoney();
            if (!TextUtils.isEmpty(itemMoney)) {
                money = Double.parseDouble(itemMoney);
            }
            DecimalFormat df = new DecimalFormat("0.00");
            mHolder.thisMoney.setText(df.format(money));
            mHolder.time.setText(AppTool.getMsgTwoDateDistance(cashDetailBean.getAddtime()+""));
            return convertView;
        }

        class Holder {
            TextView name, time, thisMoney, status;

            public Holder(View contentView) {
                name = (TextView) contentView.findViewById(R.id.cash_record_item_bank_name);
                time = (TextView) contentView.findViewById(R.id.cash_record_item_time);
                thisMoney = (TextView) contentView.findViewById(R.id.cash_record_item_money);
                status = (TextView) contentView.findViewById(R.id.cash_record_item_status);
            }
        }
    }

}
