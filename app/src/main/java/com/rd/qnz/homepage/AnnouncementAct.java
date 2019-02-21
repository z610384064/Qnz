package com.rd.qnz.homepage;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.rd.qnz.BaseActivity;
import com.rd.qnz.R;
import com.rd.qnz.WebViewAct;
import com.rd.qnz.adapter.AnnouncementListAdapter;
import com.rd.qnz.custom.APIModel;
import com.rd.qnz.custom.MineShow;
import com.rd.qnz.custom.MyApplication;
import com.rd.qnz.dialog.CustomProgressDialog;
import com.rd.qnz.dialog.GetDialog;
import com.rd.qnz.tools.BaseParam;
import com.rd.qnz.tools.Check;
import com.rd.qnz.tools.webservice.JsonRequeatThreadMoreAnnouncementList;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 更多-消息公告
 *
 * @author Evonne
 */
public class AnnouncementAct extends BaseActivity {

    private static final String TAG = "消息公告";

    private PullToRefreshListView more_announcement_list;

    private AnnouncementListAdapter listAdapter;
    private List<Map<String, String>> list = new ArrayList<Map<String, String>>();
    private int pernum = 15;
    private int currentPage = 1;

    private ArrayList<String> param = null;
    private ArrayList<String> value = null;

    private MyApplication myApp;
    private GetDialog dia;
    private MyHandler myHandler;
    private CustomProgressDialog progressDialog = null;
    APIModel apiModel = new APIModel();

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG); // 统计页面
        MobclickAgent.onResume(this); // 统计时长
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(TAG); // 保证onPageEnd在onPause之前调用,因为onPause中会保存信息
        MobclickAgent.onPause(this);
    }

    private class MyHandler extends Handler {
        public void handleMessage(Message paramMessage) {
            Bundle localBundle1 = paramMessage.getData();
            String red_list = localBundle1.getString(BaseParam.URL_REQUEAT_MORE_NOTICE);
            if (null != progressDialog && progressDialog.isShowing()) {// 隐藏加载框
                AnnouncementAct.this.progressDialog.dismiss();
            }
            if (null != red_list) {
                JsonList(red_list);
            }
            more_announcement_list.onRefreshComplete();
            super.handleMessage(paramMessage);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.community_announcement);
        myApp = (MyApplication) getApplication();
        this.dia = new GetDialog();
        this.myHandler = new MyHandler();
        initBar();
        intView();
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
        actionbar_side_name.setText("消息中心");
    }

    public void intView() {
        more_announcement_list = (PullToRefreshListView) findViewById(R.id.more_announcement_list);
        more_announcement_list.setMode(Mode.BOTH);
        listAdapter = new AnnouncementListAdapter(AnnouncementAct.this, list);
        more_announcement_list.setAdapter(listAdapter);
        more_announcement_list.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                if (MineShow.fastClick()) {
                    Intent intent = new Intent(AnnouncementAct.this, WebViewAct.class);
                    intent.putExtra("web_url", startWebViewRequest(list.get(arg2 - 1).get(BaseParam.QIAN_MORE_NOTICE_ID)));
                    intent.putExtra("title", "公告详情");
                    startActivity(intent);
                }
            }
        });
        more_announcement_list.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                refreshView.getLoadingLayoutProxy().setPullLabel("释放财运刷新");
                refreshView.getLoadingLayoutProxy().setRefreshingLabel("财运刷新中...");
                refreshView.getLoadingLayoutProxy().setReleaseLabel("释放财运刷新");
                currentPage = 1;
                startDataRequest();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                refreshView.getLoadingLayoutProxy().setPullLabel("更多");
                refreshView.getLoadingLayoutProxy().setRefreshingLabel("正在获取数据...");
                refreshView.getLoadingLayoutProxy().setReleaseLabel("松开获取更多数据");
                ++currentPage;
                startDataRequest();
            }
        });
        startDataRequest();
    }

    private String startWebViewRequest(String noticeId) {

        String[] array = new String[]{
                "noticeId=" + noticeId,
                BaseParam.URL_QIAN_API_APPID + "=" + myApp.appId,
                BaseParam.URL_QIAN_API_SERVICE + "=noticeDetail",
                BaseParam.URL_QIAN_API_SIGNTYPE + "=" + myApp.signType};
        String sign = apiModel.sortStringArray(array);
        String url = BaseParam.URL_QIAN_NOTICEDETAIL + "?noticeId=" + noticeId + "&"
                + BaseParam.URL_QIAN_API_APPID + "=" + myApp.appId + "&"
                + BaseParam.URL_QIAN_API_SERVICE + "=noticeDetail&"
                + BaseParam.URL_QIAN_API_SIGNTYPE + "=" + myApp.signType + "&"
                + BaseParam.URL_QIAN_API_SIGN + "=" + sign;
        Log.i("消息公告webview---url == ", url);
        return url;
    }

    private void startDataRequest() {
        if (Check.hasInternet(AnnouncementAct.this)) {
            AnnouncementAct.this.initArray();
            AnnouncementAct.this.param.add("pernum");
            AnnouncementAct.this.value.add(pernum + "");
            AnnouncementAct.this.param.add("currentPage");
            AnnouncementAct.this.value.add(currentPage + "");
            AnnouncementAct.this.param.add(BaseParam.URL_QIAN_API_APPID);
            AnnouncementAct.this.value.add(myApp.appId);
            AnnouncementAct.this.param.add(BaseParam.URL_QIAN_API_SERVICE);
            AnnouncementAct.this.value.add("notice");
            AnnouncementAct.this.param.add(BaseParam.URL_QIAN_API_SIGNTYPE);
            AnnouncementAct.this.value.add(myApp.signType);
            String[] array = new String[]{"pernum=" + pernum,
                    "currentPage=" + currentPage,
                    BaseParam.URL_QIAN_API_APPID + "=" + myApp.appId,
                    BaseParam.URL_QIAN_API_SERVICE + "=notice",
                    BaseParam.URL_QIAN_API_SIGNTYPE + "=" + myApp.signType};

            String sign = apiModel.sortStringArray(array);
            AnnouncementAct.this.param.add(BaseParam.URL_QIAN_API_SIGN);
            AnnouncementAct.this.value.add(sign);
            AnnouncementAct.this.progressDialog = AnnouncementAct.this.dia.getLoginDialog(AnnouncementAct.this, "正在获取数据..");
            AnnouncementAct.this.progressDialog.show();
            new Thread(new JsonRequeatThreadMoreAnnouncementList(
                    AnnouncementAct.this, myApp,
                    AnnouncementAct.this.myHandler,
                    AnnouncementAct.this.param,
                    AnnouncementAct.this.value)
            ).start();

        } else {
            showToast("请检查网络连接是否正常");
        }

    }

    private void JsonList(String result) {
        Log.i("消息公告 === ", result);
        if (result.equals("unusual")) {
            showToast("连接服务器异常");
            return;
        }
        if (currentPage == 1) {
            list.clear();
        }
        try {
            JSONObject oj = new JSONObject(result);
            if (Check.jsonGetStringAnalysis(oj, "resultCode").equals("1")) {
                JSONObject oj1 = new JSONObject(Check.jsonGetStringAnalysis(oj, "resultData"));

                JSONArray active_list = oj1.getJSONArray("list");

                if (active_list.length() == 0) {
                    showToast("没有数据可获取");
                    return;
                }
                for (int i = 0; i < active_list.length(); i++) {
                    JSONObject product = active_list.getJSONObject(i);
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("resultCode", "1");
                    map.put(BaseParam.QIAN_MORE_NOTICE_ADDTIME, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_MORE_NOTICE_ADDTIME));
                    map.put(BaseParam.QIAN_MORE_NOTICE_ID, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_MORE_NOTICE_ID));
                    map.put(BaseParam.QIAN_MORE_NOTICE_NAME, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_MORE_NOTICE_NAME));
                    map.put(BaseParam.QIAN_MORE_NOTICE_CONTENT, Check.jsonGetStringAnalysis(product, BaseParam.QIAN_MORE_NOTICE_CONTENT));
                    list.add(map);
                }
                listAdapter.notifyDataSetChanged(list);

            } else {
                showToast(Check.checkReturn(Check.jsonGetStringAnalysis(oj, "resultCode")));
                return;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return;
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
