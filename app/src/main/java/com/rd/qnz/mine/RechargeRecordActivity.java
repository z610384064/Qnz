package com.rd.qnz.mine;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.orhanobut.logger.Logger;
import com.rd.qnz.BaseActivity;
import com.rd.qnz.R;
import com.rd.qnz.custom.APIModel;
import com.rd.qnz.custom.MyApplication;
import com.rd.qnz.dialog.CustomProgressDialog;
import com.rd.qnz.dialog.GetDialog;
import com.rd.qnz.entity.RechargeItem;
import com.rd.qnz.tools.BaseParam;
import com.rd.qnz.tools.Check;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 点击充值进入账户充值页面,点击右上角充值记录
 *
 * @author Evonne
 */
public class RechargeRecordActivity extends BaseActivity {
    private ImageView mBackBtn;
    private PullToRefreshListView mListView;
    private int pernum = 10;// 每页显示个数
    private int currentPage = 1;// 当前页
    private RechargeListAdapter mAdapter;
    private CustomProgressDialog mProgressDialog;
    private LinearLayout no_crash;
    private ImageView no_crash_img;
    private List<RechargeItem> dataList;

    public static void start(Context context) {
        Intent i = new Intent(context, RechargeRecordActivity.class);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_account_balancelist3);
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
        tv.setText("充值记录");
        dataList = new ArrayList<RechargeItem>();
        mAdapter = new RechargeListAdapter(RechargeRecordActivity.this, dataList);
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
    }

    private void getData() {//充值记录
        if (Check.hasInternet(RechargeRecordActivity.this)) {
            JSONObject param = new JSONObject();
            MyApplication myApp = MyApplication.getInstance();
            String sign = null;
            SharedPreferences preferences = getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_USER, Context.MODE_PRIVATE);
            String oauthToken = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN, "");
            try {
                param.put(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN, oauthToken);
                param.put(BaseParam.URL_QIAN_API_APPID, myApp.appId);
                param.put(BaseParam.URL_QIAN_API_SERVICE, "myAccount");
                param.put(BaseParam.URL_QIAN_API_SIGNTYPE, myApp.signType);
                param.put(BaseParam.QIAN_CURRENTPAGE, currentPage);
                param.put(BaseParam.QIAN_PERNUM, 10);

                String[] array = new String[]{
                        BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN + "=" + oauthToken,
                        BaseParam.QIAN_PERNUM + "=" + pernum + "",
                        BaseParam.QIAN_CURRENTPAGE + "=" + currentPage + "",
                        BaseParam.URL_QIAN_API_APPID + "=" + myApp.appId,
                        BaseParam.URL_QIAN_API_SERVICE + "=myAccount",
                        BaseParam.URL_QIAN_API_SIGNTYPE + "=" + myApp.signType};
                APIModel apiModel = new APIModel();
                sign = apiModel.sortStringArray(array);
                param.put(BaseParam.URL_QIAN_API_SIGN, sign);

                GetDialog dia = new GetDialog();
                mProgressDialog = dia.getLoginDialog(RechargeRecordActivity.this, "正在获取数据..");
                mProgressDialog.show();

            } catch (JSONException e) {
                e.printStackTrace();
            }
            String url = BaseParam.URL_QIAN_RECHARGERECORD
                    + "?oauthToken=" + oauthToken
                    + "&" + "appId=" + myApp.appId
                    + "&" + "service=" + "myAccount"
                    + "&" + "signType=" + myApp.signType
                    + "&" + "currentPage=" + currentPage
                    + "&" + "pernum=" + 10
                    + "&" + "sign=" + sign;

            OkGo.post(url)
                    .tag(this)
                    .upJson(param.toString())
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(String s, Call call, Response response) {
                            Log.i("充值记录", "----------");
                            Logger.json(s.toString());
                            if (null != mProgressDialog && mProgressDialog.isShowing()) {// 隐藏加载框
                                mProgressDialog.dismiss();
                            }
                            if (currentPage == 1) {
                                dataList.clear();
                            }
                            try {
                                JSONObject object = new JSONObject(s);
                                int errorcode = object.getInt("resultCode");
                                if (errorcode == 1) {
                                    JSONObject oj1 = new JSONObject(Check.jsonGetStringAnalysis(object, "resultData"));
                                    JSONArray jsonArray = oj1.getJSONArray("result");
                                    if (jsonArray.length() > 0) {
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            JSONObject obj = jsonArray.getJSONObject(i);
                                            RechargeItem recharge = new RechargeItem();
                                            recharge.setAddTime(obj.getString("addTime"));
                                            recharge.setBankName(obj.getString("bankName"));
                                            recharge.setCardNo(obj.getString("cardNo"));
                                            recharge.setGoodsName(obj.getString("goodsName"));
                                            recharge.setStatus(obj.getString("status"));
                                            recharge.setTradeMoney(obj.getString("tradeMoney"));
                                            recharge.setTradeType(obj.getString("tradeType"));
                                            dataList.add(recharge);
                                        }
                                    }
                                    if (jsonArray.length() == 0) {
                                        if (currentPage == 1) {
                                            mListView.setVisibility(View.GONE);
                                            no_crash.setVisibility(View.VISIBLE);
                                            no_crash_img.setImageResource(R.drawable.nocrash);
                                        } else {
                                            showToast("没有数据可获取");
                                        }
                                    }
                                    mAdapter.notifyDataSetChanged();
                                }
                            } catch (JSONException e) {
                                if (null != mProgressDialog && mProgressDialog.isShowing()) {// 隐藏加载框
                                    mProgressDialog.dismiss();
                                    showToast("获取数据失败");
                                }
                                e.printStackTrace();
                            } finally {
                                mListView.onRefreshComplete();
                            }
                        }

                        @Override
                        public void onError(Call call, Response response, Exception e) {
                            super.onError(call, response, e);
                            Log.i("充值记录", e.toString());
                            mListView.onRefreshComplete();
                            if (null != mProgressDialog && mProgressDialog.isShowing()) {// 隐藏加载框
                                mProgressDialog.dismiss();
                                showToast("获取数据失败");
                            }
                        }
                    });

        } else {
            showToast("请检查网络连接是否正常");
        }
    }

}
