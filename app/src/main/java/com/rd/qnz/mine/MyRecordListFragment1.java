package com.rd.qnz.mine;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.orhanobut.logger.Logger;
import com.rd.qnz.R;
import com.rd.qnz.adapter.RecordingListAdapter;
import com.rd.qnz.bean.Recording;
import com.rd.qnz.custom.APIModel;
import com.rd.qnz.custom.MineShow;
import com.rd.qnz.custom.MyApplication;
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
 * Created by Evonne on 2016.11.21.
 * 投资中
 */

public class MyRecordListFragment1 extends Fragment implements View.OnClickListener {

    private View mView;
    private List<Recording> dataList;
    private PullToRefreshListView listview;
    private RecordingListAdapter adapter;
    private int currentPage = 1;// 当前页
    private int pernum = 10;// 每页显示个数
    private String oauthToken = "";
    private LinearLayout no_note;
    private ImageView no_note_img;
    private Button no_note_btn; //没有投资信息的时候显示的按钮
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = getActivity().getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_USER, Context.MODE_PRIVATE);
        oauthToken = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN, "");
        dataList = new ArrayList<>();
        adapter = new RecordingListAdapter(getActivity(), dataList, 2);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_list, null);

        listview = (PullToRefreshListView) mView.findViewById(R.id.product_list);
        no_note = (LinearLayout) mView.findViewById(R.id.no_note);//无数据
        no_note_img = (ImageView) mView.findViewById(R.id.no_note_img);
        no_note_btn= (Button) mView.findViewById(R.id.no_note_btn);
        no_note_btn.setOnClickListener(this);

        listview.setMode(Mode.BOTH);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) { //跳到投资概况界面
                if (MineShow.fastClick()) {
                    Intent intent = new Intent(getActivity(), MyuseMoneyDetailAct.class);
                    intent.putExtra("tenderId", dataList.get(arg2 - 1).getTenderId());
                    intent.putExtra("oauthToken", oauthToken);
                    startActivityForResult(intent,1);

                }
            }
        });

        listview.autoRefresh();  //刷新一下数据
        listview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
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
        return mView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 1:
                if (resultCode==1){
                    getActivity().finish();
                }
                break;
        }
    }

    private void getData() {
        JSONObject param = new JSONObject();
        MyApplication myApp = MyApplication.getInstance();
        String sign = null;
        try {
            param.put(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN, oauthToken);
            param.put(BaseParam.QIAN_PERNUM, pernum);
            param.put(BaseParam.QIAN_CURRENTPAGE, currentPage);
            param.put("type", "2");//1已回款2待回款
            param.put(BaseParam.URL_QIAN_API_APPID, myApp.appId);
            param.put(BaseParam.URL_QIAN_API_SERVICE, "productList1xV4");
            param.put(BaseParam.URL_QIAN_API_SIGNTYPE, myApp.signType);

            String[] array = new String[]{
                    BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN + "=" + oauthToken + "",
                    BaseParam.QIAN_PERNUM + "=" + pernum + "",
                    BaseParam.QIAN_CURRENTPAGE + "=" + currentPage + "",
                    "type=2",
                    BaseParam.URL_QIAN_API_APPID + "=" + myApp.appId,
                    BaseParam.URL_QIAN_API_SERVICE + "=productList1xV4",
                    BaseParam.URL_QIAN_API_SIGNTYPE + "=" + myApp.signType};

            APIModel apiModel = new APIModel();
            sign = apiModel.sortStringArray(array);
            param.put(BaseParam.URL_QIAN_API_SIGN, sign);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = BaseParam.URL_QIAN_MYTENDERDOING
                + "?oauthToken=" + oauthToken
                + "&" + "pernum=" + pernum
                + "&" + "currentPage=" + currentPage
                + "&" + "type=" + "2"
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
                        Log.i("待回款", "------");
                        Logger.json(s.toString());
                        if (currentPage == 1) {
                            dataList.clear();
                        }
                        try {
                            JSONObject object = new JSONObject(s);
                            int errorcode = object.getInt("resultCode");
                            if (errorcode == 1) {
                                JSONObject oj1 = new JSONObject(Check.jsonGetStringAnalysis(object, "resultData"));
                                JSONArray jsonArray = oj1.getJSONArray("list");
                                if (jsonArray.length() > 0) {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject obj = jsonArray.getJSONObject(i);
                                        Recording item = new Recording();
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
                                        item.setMoney(Check.jsonGetStringAnalysis(obj,"money"));
                                        item.setAddtime(Check.jsonGetStringAnalysis(obj, "addtime"));
                                        item.setRepayTime(Check.jsonGetStringAnalysis(obj,"repayTime"));
                                        dataList.add(item);
                                        Log.i("待回款dataList", obj.toString() + "");
                                    }
                                }
                                if (jsonArray.length() == 0) {
                                    if (currentPage == 1) {
                                        listview.setVisibility(View.GONE);
                                        no_note.setVisibility(View.VISIBLE);
                                        no_note_img.setImageResource(R.drawable.notender);
                                    } else {
                                        MineShow.toastShow("没有数据可获取", getActivity());
                                    }
                                }
                                adapter.notifyDataSetChanged();
                            }
                        } catch (JSONException e) {
                            MineShow.toastShow("获取数据失败", getActivity());
                            e.printStackTrace();
                        } finally {
                            listview.onRefreshComplete();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        Log.i("待回款", e.toString());
                        listview.onRefreshComplete();
                        MineShow.toastShow("获取数据失败", getActivity());
                    }
                });
    }

    @Override
    public void onClick(View v) {
        MyApplication myApp=MyApplication.getInstance();
        myApp.tabHostId = 1;
        myApp.tabHost.setCurrentTab(myApp.tabHostId);   //跳转到投资界面
        getActivity().finish();
    }
}
