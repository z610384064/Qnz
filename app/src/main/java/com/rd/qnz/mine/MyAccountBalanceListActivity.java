package com.rd.qnz.mine;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
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
import com.rd.qnz.custom.MineShow;
import com.rd.qnz.custom.MyApplication;
import com.rd.qnz.custom.Profile;
import com.rd.qnz.dialog.CustomProgressDialog;
import com.rd.qnz.dialog.GetDialog;
import com.rd.qnz.tools.AppTool;
import com.rd.qnz.tools.BaseParam;
import com.rd.qnz.tools.Check;
import com.rd.qnz.tools.webservice.jsonRequeatThreadGetBalanceRecordList;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 可用余额
 *
 * @author Evonne
 */
public class MyAccountBalanceListActivity extends BaseActivity implements OnClickListener {
    @BindView(R.id.image_account_question)
    ImageView imageAccountQuestion;
    private PullToRefreshListView mListView;
    private int pernum = 10;// 每页显示个数
    private int currentPage = 1;// 当前页
    private MyAdapter mAdapter;
    private CustomProgressDialog mProgressDialog;
    private ImageView mBackBtn;
    private boolean mIsCanScrollToBottom = true;
    private LinearLayout mNoRecordView;
    private ImageView no_crash_img;

    /**
     * 1.6.0
     *
     * @param context
     */
    private TextView tv_balance;
    private TextView tv_disposeCash;
    private String currentMonthHasCash; //已提现次数
    private Double balance; //可用余额
    private Double disposeCash;//提现中金额
    private Button btn_tixian, btn_recharge;
    DecimalFormat df = new DecimalFormat("0.00");

    public static void start(Context context) {
        Intent i = new Intent(context, MyAccountBalanceListActivity.class);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_account_balancelist);
        ButterKnife.bind(this);

        Intent i = getIntent();
        currentMonthHasCash = i.getStringExtra("currentMonthHasCash");
        balance = i.getDoubleExtra("balance", 0);
        disposeCash = i.getDoubleExtra("disposeCash", 0);
        initView();
        mIsCanScrollToBottom = true;

        getData();
    }

    public void initView() {
        imageAccountQuestion.setOnClickListener(this);
        mNoRecordView = (LinearLayout) findViewById(R.id.no_crash);
        no_crash_img = (ImageView) findViewById(R.id.no_crash_img);
        no_crash_img.setImageResource(R.drawable.nobalance);
        mBackBtn = (ImageView) findViewById(R.id.actionbar_side_left_iconfont);
        mBackBtn.setVisibility(View.VISIBLE);
        mListView = (PullToRefreshListView) findViewById(R.id.listview);
        mListView.setMode(Mode.BOTH);
        tv_balance = (TextView) findViewById(R.id.tv_balance);
        tv_balance.setText(df.format(balance));
        tv_disposeCash = (TextView) findViewById(R.id.tv_disposeCash);
        tv_disposeCash.setText(df.format(disposeCash));
        TextView tv = (TextView) findViewById(R.id.actionbar_side_name);
        tv.setText("可用余额");

        mBackBtn.setOnClickListener(this);
        btn_tixian = (Button) findViewById(R.id.btn_tixian);
        btn_recharge = (Button) findViewById(R.id.btn_recharge);
        btn_recharge.setOnClickListener(this);
        btn_tixian.setOnClickListener(this);
        mAdapter = new MyAdapter(this);
        mListView.setAdapter(mAdapter);
        mListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                refreshView.getLoadingLayoutProxy().setPullLabel("释放财运刷新");
                refreshView.getLoadingLayoutProxy().setRefreshingLabel("财运刷新中...");
                refreshView.getLoadingLayoutProxy().setReleaseLabel("释放财运刷新");
                currentPage = 1;
                mIsCanScrollToBottom = true;
                getData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                refreshView.getLoadingLayoutProxy(false, true).setPullLabel("加载更多");
                refreshView.getLoadingLayoutProxy(false, true).setRefreshingLabel("正在获取数据...");
                refreshView.getLoadingLayoutProxy(false, true).setReleaseLabel("松开获取更多数据");
                if (mIsCanScrollToBottom) {
                    ++currentPage;
                    getData();
                }
            }
        });
    }


    private void getData() {
        if (Check.hasInternet(MyAccountBalanceListActivity.this)) {
            MyApplication myApp = MyApplication.getInstance();
            SharedPreferences preferences = getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_USER, Context.MODE_PRIVATE);
            String oauthToken = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN, "");
            APIModel apiModel = new APIModel();
            ArrayList<String> param = new ArrayList<String>();
            ArrayList<String> value = new ArrayList<String>();
            param.add(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN);
            value.add(oauthToken);
            param.add(BaseParam.QIAN_PERNUM);
            value.add(pernum + "");
            param.add(BaseParam.QIAN_CURRENTPAGE);
            value.add(currentPage + "");
            param.add(BaseParam.URL_QIAN_API_APPID);
            value.add(myApp.appId);
            param.add(BaseParam.URL_QIAN_API_SERVICE);
            value.add("balanceRecord");
            param.add(BaseParam.URL_QIAN_API_SIGNTYPE);
            value.add(myApp.signType);

            String[] array = new String[]{
                    BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN + "=" + oauthToken,
                    BaseParam.QIAN_PERNUM + "=" + pernum + "",
                    BaseParam.QIAN_CURRENTPAGE + "=" + currentPage + "",
                    BaseParam.URL_QIAN_API_APPID + "=" + myApp.appId,
                    BaseParam.URL_QIAN_API_SERVICE + "=balanceRecord",
                    BaseParam.URL_QIAN_API_SIGNTYPE + "=" + myApp.signType};
            String sign = apiModel.sortStringArray(array);
            param.add(BaseParam.URL_QIAN_API_SIGN);
            value.add(sign);
            if (mAdapter.getCount() == 0 || mAdapter == null) {
                GetDialog dia = new GetDialog();
                mProgressDialog = dia.getLoginDialog(MyAccountBalanceListActivity.this, "正在获取数据..");
                mProgressDialog.show();
            }

            new Thread(new jsonRequeatThreadGetBalanceRecordList(
                    MyAccountBalanceListActivity.this,
                    myApp,
                    MyAccountBalanceListActivity.this.myHandler,
                    param,
                    value)
            ).start();

        } else {
            showToast("请检查网络连接是否正常");
        }
    }

    @Override
    public void onClick(View v) {
        int vId = v.getId();
        switch (vId) {
            case R.id.image_account_question:
                Intent i=new Intent(MyAccountBalanceListActivity.this,MyAccountBalanceDetails.class);
                i.putExtra("balance",balance); //可用余额
                startActivity(i);
                break;
            case R.id.actionbar_side_left_iconfont:
                finish();
                break;
            case R.id.btn_recharge: //充值
                if (MineShow.fastClick()) {
                    if (TextUtils.equals(Profile.getUserRealNameStatus(), "0")) {//未实名认证
                        NewRealAct.start(MyAccountBalanceListActivity.this);
                        MineShow.toastShow("请先实名认证", MyAccountBalanceListActivity.this);
                    } else {
                        if (TextUtils.equals(Profile.getUserPayPassWordStatus(), "0")) {//未设置交易密码
                            startActivity(new Intent(MyAccountBalanceListActivity.this, ForgetPasswordAct.class));
                            MineShow.toastShow("请先设置交易密码", MyAccountBalanceListActivity.this);
                        } else {
                            if (TextUtils.equals(Profile.getUserBankCardStatus(), "0")) {//未绑卡
                                AddYiBankAct.start(MyAccountBalanceListActivity.this);
                                MineShow.toastShow("请先绑定一张银行卡", MyAccountBalanceListActivity.this);
                            } else {
                                startActivityForResult(new Intent(MyAccountBalanceListActivity.this, Recharge.class), RECHARGE_SUCCESS);
                            }
                        }
                    }
                }
                break;
            case R.id.btn_tixian:
                if (MineShow.fastClick()) {
                    if (TextUtils.equals(Profile.getUserRealNameStatus(), "0")) {//未实名认证
                        NewRealAct.start(MyAccountBalanceListActivity.this);
                        MineShow.toastShow("请先实名认证", MyAccountBalanceListActivity.this);
                    } else {
                        if (TextUtils.equals(Profile.getUserPayPassWordStatus(), "0")) {//未设置交易密码
                            startActivity(new Intent(MyAccountBalanceListActivity.this, ForgetPasswordAct.class));
                            MineShow.toastShow("请先设置交易密码", MyAccountBalanceListActivity.this);
                        } else {
                            if (TextUtils.equals(Profile.getUserBankCardStatus(), "0")) {//未绑卡
                                AddYiBankAct.start(MyAccountBalanceListActivity.this);
                                MineShow.toastShow("请先绑定一张银行卡", MyAccountBalanceListActivity.this);
                            } else {
                                 i = new Intent(MyAccountBalanceListActivity.this, MyBalanceWithdrawCash.class);
                                i.putExtra("currentMonthHasCash", currentMonthHasCash);
                                startActivityForResult(i, 2);
                            }
                        }
                    }
                }
                break;
            default:
                break;
        }
    }

    private static final int RECHARGE_SUCCESS = 0x22;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {

                case RECHARGE_SUCCESS:  //从充值界面返回的数据,并且充值成功了,关闭当前界面
                    finish();
                case 2:  //从提现界面返回的数据
                    finish();
                    break;
            }
        }
    }


    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            Bundle localBundle1 = msg.getData();
            ArrayList<Parcelable> product_Rlist = localBundle1.getParcelableArrayList(BaseParam.URL_QIAN_BALANCE_RECORD);
            if (null != mProgressDialog && mProgressDialog.isShowing()) {// 隐藏加载框
                mProgressDialog.dismiss();
            }

            if (null != product_Rlist) {
                List<Map<String, String>> list1 = (List<Map<String, String>>) product_Rlist.get(0);
                Map<String, String> map = list1.get(0);
                String resultCode = map.get("resultCode");
                if (resultCode.equals("1")) {
                    if (mAdapter != null) {
                        if (currentPage > 1) {
                            mAdapter.addData(list1);
                        } else {
                            mAdapter.setData(list1);
                        }
                    }
                    if (list1.size() <= 0) {
                        mIsCanScrollToBottom = false;
                    }

                } else {
                    String errorCode = map.get("errorCode");
                    if (TextUtils.equals(errorCode, "no")) { //网络成功,但是没有数据
                        if (list1.size() == 0) {
                            mNoRecordView.setVisibility(View.VISIBLE);
                            mListView.setVisibility(View.GONE);
                        } else {  //没有更多数据了
                            if (currentPage == 1) {
                                mNoRecordView.setVisibility(View.VISIBLE);
                                mListView.setVisibility(View.GONE);
                            } else {
                                showToast("没有数据可获取");
                            }
//                            showToast("没有数据可获取");
//                            mNoRecordView.setVisibility(View.VISIBLE);
//                            mListView.setVisibility(View.GONE);
                        }
                    } else {
                        showToast(Check.checkReturn(map.get("errorCode")));
                    }
                }
            }
            mListView.onRefreshComplete();
        }
    };

    public class MyAdapter extends BaseAdapter {
        private Context mContext;
        private List<Map<String, String>> mDataList = new ArrayList<Map<String, String>>();

        public MyAdapter(Context context) {
            mContext = context;
        }

        public void setData(List<Map<String, String>> list) {
            this.mDataList.clear();
            this.mDataList = list;
            notifyDataSetChanged();
        }

        public void addData(List<Map<String, String>> list) {
            this.mDataList.addAll(list);
            notifyDataSetChanged();
        }

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
                convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_account_balance_record, null);
                mHolder = new Holder(convertView);
                convertView.setTag(mHolder);
            } else {
                mHolder = (Holder) convertView.getTag();
            }
            Map<String, String> map = mDataList.get(position);
            mHolder.name.setText(map.get(BaseParam.QIAN_BALANCE_RECORD_TITLE));
//            mHolder.balance.setText("余额: " + map.get(BaseParam.QIAN_BALANCE_RECORD_LATEST_BALANCE));
            if (TextUtils.equals(map.get(BaseParam.QIAN_BALANCE_RECORD_TYPE), "2")) {//收入
                mHolder.thisMoney.setTextColor(mContext.getResources().getColor(R.color.account_balance3));//增加
            } else {
                mHolder.thisMoney.setTextColor(mContext.getResources().getColor(R.color.account_balance2));//减少
            }
            mHolder.thisMoney.setText(map.get(BaseParam.QIAN_BALANCE_RECORD_MONEY));
            mHolder.time.setText(AppTool.getMsgTwoDateDistance(map.get(BaseParam.QIAN_BALANCE_RECORD_DATA)));
            return convertView;
        }

        class Holder {
            TextView name, time, thisMoney, balance;

            public Holder(View contentView) {
                name = (TextView) contentView.findViewById(R.id.account_balance_record_item_name);
                time = (TextView) contentView.findViewById(R.id.account_balance_record_item_time);
                thisMoney = (TextView) contentView.findViewById(R.id.account_balance_record_item_this_money);
            }
        }
    }

}
