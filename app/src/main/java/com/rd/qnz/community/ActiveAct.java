package com.rd.qnz.community;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.rd.qnz.BaseActivity;
import com.rd.qnz.R;
import com.rd.qnz.WebBannerViewNeedAccesTokenActivity;
import com.rd.qnz.adapter.ActiveListAdapter;
import com.rd.qnz.custom.APIModel;
import com.rd.qnz.custom.MineShow;
import com.rd.qnz.custom.MyApplication;
import com.rd.qnz.dialog.CustomProgressDialog;
import com.rd.qnz.dialog.GetDialog;
import com.rd.qnz.login.Login;
import com.rd.qnz.tools.BaseParam;
import com.rd.qnz.tools.Check;
import com.rd.qnz.tools.webservice.JsonRequeatThreadMoreActiveList;
import com.rd.qnz.util.StatusBarCompat;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.rd.qnz.tools.Check.jsonGetStringAnalysis;

/**
 * 活动中心(已适配)
 *
 * @author Evonne
 */
public class ActiveAct extends BaseActivity {
    private static final String TAG = "活动中心";

    private ArrayList<String> param = null;
    private ArrayList<String> value = null;

    private GetDialog dia;
    private MyHandler myHandler;
    private CustomProgressDialog progressDialog = null;
    private MyApplication myApp;
    List<Map<String, String>> list = new ArrayList<>(); //正在进行的活动
    List<Map<String, String>> list2 = new ArrayList<>(); //已结束的活动

    APIModel apiModel = new APIModel();

    private int currentPage = 1;
    private String type = "2";// 1为红包 2为活动
    private int pernum = 15;

    private PullToRefreshListView my_active_list;
    private ActiveListAdapter listAdapter;
    private String userId = "";


    private RelativeLayout rl_top; //顶部透明的view
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.community_active);
        setSwipeBackEnable(false);//不许侧滑
        this.dia = new GetDialog();
        myApp = (MyApplication) getApplication();
        this.myHandler = new MyHandler();
        SharedPreferences preferences = getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_USER, Context.MODE_PRIVATE);
        userId = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_USERID, "");
        rl_top= (RelativeLayout) findViewById(R.id.rl_top);
        ViewGroup.LayoutParams params=rl_top.getLayoutParams();
        rl_top.setBackgroundColor(getResources().getColor(R.color.xilie_text));
        params.height = StatusBarCompat.getStatusBarHeight(this);
        rl_top.setLayoutParams(params);
        initBar();
        intView();
    }

    private void initBar() {

        TextView actionbar_side_name = (TextView) findViewById(R.id.actionbar_side_name);
        actionbar_side_name.setText("活动中心");
    }

    public void intView() {
        int width = getWindowManager().getDefaultDisplay().getWidth();
        my_active_list = (PullToRefreshListView) findViewById(R.id.my_active_list);
        my_active_list.setMode(Mode.BOTH);
        listAdapter = new ActiveListAdapter(ActiveAct.this, list,list2, width);
        my_active_list.setAdapter(listAdapter);
        my_active_list.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {  //点击第一个, arg2=1
                if (MineShow.fastClick()) {
                    if (list.size()==0&&list2.size()==0){  //没有活动,点击没反应

                    }else if (list.size()>0&&list2.size()==0){ //全是正在进行的活动,没有已结束的活动
                        String webUrl = BaseParam.URL_QIAN + list.get(arg2 - 1).get(BaseParam.QIAN_MORE_ACTIVE_LOCATIONURL);
                        Log.e("webUrl--webUrl", webUrl);
                        String fileName = list.get(arg2 - 1).get(BaseParam.QIAN_MORE_ACTIVE_FILENAME);
                        String intro = list.get(arg2 - 1).get(BaseParam.QIAN_MORE_ACTIVE_INTRO); //标题是一定存在的
                        String duce = list.get(arg2 - 1).get(BaseParam.DUCE);  //描述有可能为空

                        WebBannerViewNeedAccesTokenActivity.start(ActiveAct.this, webUrl, fileName, "2", intro, BaseParam.QIAN_ICON_WEB_PATH,duce);

                    }else if (list2.size()>0&&list.size()==0){//没有正在进行的活动,全是已结束的活动
                        String webUrl = BaseParam.URL_QIAN + list2.get(arg2 -2).get(BaseParam.QIAN_MORE_ACTIVE_LOCATIONURL);
                        Log.e("webUrl--webUrl", webUrl);
                        String fileName = list2.get(arg2 - 2).get(BaseParam.QIAN_MORE_ACTIVE_FILENAME);
                        String intro = list2.get(arg2 - 2).get(BaseParam.QIAN_MORE_ACTIVE_INTRO); //标题是一定存在的
                        String duce = list2.get(arg2 - 2).get(BaseParam.DUCE);  //描述有可能为空

                        WebBannerViewNeedAccesTokenActivity.start(ActiveAct.this, webUrl, fileName, "2", intro, BaseParam.QIAN_ICON_WEB_PATH,duce);
                    }else { //两种活动都存在
                        if (arg2<list.size()+1){ //点击了正在进行的活动
                            String webUrl = BaseParam.URL_QIAN + list.get(arg2 - 1).get(BaseParam.QIAN_MORE_ACTIVE_LOCATIONURL);
                            Log.e("webUrl--webUrl", webUrl);
                            String fileName = list.get(arg2 - 1).get(BaseParam.QIAN_MORE_ACTIVE_FILENAME);
                            String intro = list.get(arg2 - 1).get(BaseParam.QIAN_MORE_ACTIVE_INTRO); //标题是一定存在的
                            String duce = list.get(arg2 - 1).get(BaseParam.DUCE);  //描述有可能为空
                            WebBannerViewNeedAccesTokenActivity.start(ActiveAct.this, webUrl, fileName, "2", intro, BaseParam.QIAN_ICON_WEB_PATH,duce);
                        }else if (arg2>list.size()+1){
                            Log.i("arg2",arg2+"");
                            String webUrl = BaseParam.URL_QIAN + list2.get(arg2 -2-list.size()).get(BaseParam.QIAN_MORE_ACTIVE_LOCATIONURL);
                            Log.e("webUrl--webUrl", webUrl);
                            String fileName = list2.get(arg2 -2-list.size()).get(BaseParam.QIAN_MORE_ACTIVE_FILENAME);
                            String intro = list2.get(arg2 - 2-list.size()).get(BaseParam.QIAN_MORE_ACTIVE_INTRO); //标题是一定存在的
                            String duce = list2.get(arg2 - 2-list.size()).get(BaseParam.DUCE);  //描述有可能为空

                            WebBannerViewNeedAccesTokenActivity.start(ActiveAct.this, webUrl, fileName, "2", intro, BaseParam.QIAN_ICON_WEB_PATH,duce);
                        }
                    }

                }
            }

        });
        my_active_list.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
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
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG); // 统计页面
        MobclickAgent.onResume(this); // 统计时长
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(TAG); // 保证 onPageEnd 在onPause 之前调用,因为 onPause
        // 中会保存信息
        MobclickAgent.onPause(this);
    }

    private class MyHandler extends Handler {
        public void handleMessage(Message paramMessage) {
            Bundle localBundle1 = paramMessage.getData();
            String red_list = localBundle1.getString(BaseParam.QIAN_REQUEAT_MOREACTIVITY);
            if (null != progressDialog && progressDialog.isShowing()) {// 隐藏加载框
                ActiveAct.this.progressDialog.dismiss();
            }
            if (null != red_list) {
                JsonList(red_list);
            }
            my_active_list.onRefreshComplete();
            super.handleMessage(paramMessage);
        }
    }
    private void startDataRequest() {
        if (Check.hasInternet(ActiveAct.this)) {
            initArray();
            param.add("pernum");
            value.add("15");
            param.add("currentPage");
            value.add(currentPage + "");
            param.add(BaseParam.URL_QIAN_API_APPID);
            value.add(myApp.appId);
            param.add(BaseParam.URL_QIAN_API_SERVICE);
            value.add("activity");
            param.add(BaseParam.URL_QIAN_API_SIGNTYPE);
            value.add(myApp.signType);
            String[] array = new String[]{
                    "pernum=" + pernum,
                    "currentPage=" + currentPage,
                    BaseParam.URL_QIAN_API_APPID + "=" + myApp.appId,
                    BaseParam.URL_QIAN_API_SERVICE + "=activity",
                    BaseParam.URL_QIAN_API_SIGNTYPE + "=" + myApp.signType};

            String sign = apiModel.sortStringArray(array);
            param.add(BaseParam.URL_QIAN_API_SIGN);
            value.add(sign);
            progressDialog = dia.getLoginDialog(ActiveAct.this, "正在获取数据..");
            progressDialog.show();
            new Thread(new JsonRequeatThreadMoreActiveList(
                    ActiveAct.this,
                    myApp,
                    myHandler,
                    param,
                    value,
                    type)
            ).start();
        } else {
            showToast("请检查网络连接是否正常");
        }
    }

    private void JsonList(String result) {

        if (result.equals("unusual")) {
            showToast("连接服务器异常");
            return;
        }
        if (currentPage == 1) {
            list.clear();
        }
        try {
            JSONObject oj = new JSONObject(result);
            if (jsonGetStringAnalysis(oj, "resultCode").equals("1")) {
                JSONObject oj1 = new JSONObject(jsonGetStringAnalysis(oj, "resultData"));

                JSONArray active_list = oj1.getJSONArray("activityList");
                for (int i = 0; i < active_list.length(); i++) {
                    JSONObject active = active_list.getJSONObject(i);
                    Map<String, String> map = new HashMap<>();
                    map.put("resultCode", "1");
                    map.put(BaseParam.QIAN_MORE_ACTIVE_ADDTIME, jsonGetStringAnalysis(active, BaseParam.QIAN_MORE_ACTIVE_ADDTIME));
                    map.put(BaseParam.QIAN_MORE_ACTIVE_COLOR, jsonGetStringAnalysis(active, BaseParam.QIAN_MORE_ACTIVE_COLOR));
                    map.put(BaseParam.QIAN_MORE_ACTIVE_FILENAME, jsonGetStringAnalysis(active, BaseParam.QIAN_MORE_ACTIVE_FILENAME));
                    map.put(BaseParam.QIAN_MORE_ACTIVE_FILETYPENAME, jsonGetStringAnalysis(active, BaseParam.QIAN_MORE_ACTIVE_FILETYPENAME));
                    map.put(BaseParam.QIAN_MORE_ACTIVE_FRANCHISEEID, jsonGetStringAnalysis(active, BaseParam.QIAN_MORE_ACTIVE_FRANCHISEEID));
                    map.put(BaseParam.QIAN_MORE_ACTIVE_ID, jsonGetStringAnalysis(active, BaseParam.QIAN_MORE_ACTIVE_ID));
                    map.put(BaseParam.QIAN_MORE_ACTIVE_INTRO, jsonGetStringAnalysis(active, BaseParam.QIAN_MORE_ACTIVE_INTRO));
                    map.put(BaseParam.DUCE, jsonGetStringAnalysis(active, BaseParam.DUCE));
                    map.put(BaseParam.QIAN_MORE_ACTIVE_LOCATIONURL, jsonGetStringAnalysis(active, BaseParam.QIAN_MORE_ACTIVE_LOCATIONURL));
                    map.put(BaseParam.QIAN_MORE_ACTIVE_URL, jsonGetStringAnalysis(active, BaseParam.QIAN_MORE_ACTIVE_URL));
                    String endFlg =Check.jsonGetStringAnalysis(active, BaseParam.QIAN_REQUEAT_ENDFLG);
                    map.put(BaseParam.QIAN_REQUEAT_ENDFLG, endFlg);

                    if ("0".equals(endFlg)){
                        list2.add(map);
                    }else if ("1".equals(endFlg)){
                        list.add(map);
                    }else if (TextUtils.isEmpty(endFlg)){
                        list.add(map);
                    }


                }
                listAdapter.notifyDataSetChanged(list,list2, type);
            } else {
                if (jsonGetStringAnalysis(oj, "errorCode").equals("TOKEN_NOT_EXIST")) {
                    startActivity(new Intent(ActiveAct.this, Login.class));
                } else if (jsonGetStringAnalysis(oj, "errorCode").equals("TOKEN_EXPIRED")) {
                    startActivity(new Intent(ActiveAct.this, Login.class));
                } else {
                    showToast(Check.checkReturn(jsonGetStringAnalysis(oj, "errorCode")));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initArray() {
        this.param = null;
        this.value = null;
        this.param = new ArrayList();
        this.value = new ArrayList();
        System.gc();
    }
    private long exitTime = 0;
    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - exitTime > 2000) {
            showToast("再按一次退出程序");
            exitTime = System.currentTimeMillis();
        } else {
            finish();
            System.exit(0);
        }
    }
}
