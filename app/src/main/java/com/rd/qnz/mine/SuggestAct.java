package com.rd.qnz.mine;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.rd.qnz.BaseActivity;
import com.rd.qnz.R;
import com.rd.qnz.custom.APIModel;
import com.rd.qnz.custom.MyApplication;
import com.rd.qnz.dialog.CustomProgressDialog;
import com.rd.qnz.dialog.GetDialog;
import com.rd.qnz.tools.BaseParam;
import com.rd.qnz.tools.Check;
import com.rd.qnz.tools.webservice.JsonRequeatThreadMoreSuggest;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * 意见反馈
 *
 * @author Evonne
 */
public class SuggestAct extends BaseActivity {

    private static final String TAG = "意见反馈";

    private ArrayList<String> param = null;
    private ArrayList<String> value = null;

    private GetDialog dia;
    private MyHandler myHandler;
    private CustomProgressDialog progressDialog = null;
    private MyApplication myApp;

    private Button more_suggest_commit;
    private TextView more_suggest_count;
    private EditText more_suggest_content;
    private String opinion;
    APIModel apiModel = new APIModel();

    private SharedPreferences preferences;
    private String user_phone;

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG); // 统计页面
        MobclickAgent.onResume(this); // 统计时长
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(TAG);
        MobclickAgent.onPause(this);
    }

    private class MyHandler extends Handler {
        public void handleMessage(Message paramMessage) {
            Bundle localBundle1 = paramMessage.getData();
            String result = localBundle1.getString(BaseParam.URL_REQUEAT_MORE_FEEDBACK);
            if (null != progressDialog && progressDialog.isShowing()) {// 隐藏加载框
                SuggestAct.this.progressDialog.dismiss();
            }
            if (null != result) {
                Log.i("意见反馈MoreSuggestAct---结果", result);
                if (result.equals("unusual")) {
                    showToast("网络不稳定，请重试");
                    return;
                }
                try {
                    JSONObject oj = new JSONObject(result);
                    if (Check.jsonGetStringAnalysis(oj, "resultCode").equals("1")) {
                        showToast("反馈成功");
                        finish();
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    showToast("网络故障，请重试");
                    e.printStackTrace();
                }
            }
            super.handleMessage(paramMessage);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.community_suggest);
        this.dia = new GetDialog();
        myApp = (MyApplication) getApplication();
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
        actionbar_side_name.setText("意见反馈");
    }

    public void intView() {
        preferences = getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_USER, Context.MODE_PRIVATE);
        user_phone = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_PHONE, "");

        more_suggest_count = (TextView) findViewById(R.id.more_suggest_count);
        more_suggest_content = (EditText) findViewById(R.id.more_suggest_content);
        more_suggest_commit = (Button) findViewById(R.id.more_suggest_commit);
        more_suggest_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                opinion = more_suggest_content.getText().toString().trim();
                if (opinion.length() <= 150) {
                    more_suggest_count.setText(opinion.length() + "/150");
                } else {
                    more_suggest_content.setText(opinion.substring(0, opinion.length() - 1));
                    more_suggest_content.setSelection(more_suggest_content.length());// 调整光标到最后一行Android:autoText
                    // 自动拼写帮助
                    opinion = more_suggest_content.getText().toString().trim();
                    showToast("字数已经超过");
                }
                if (opinion.length() > 0) {
                    more_suggest_commit.setBackgroundResource(R.drawable.button_org_big);
                    more_suggest_commit.setClickable(true);
                } else {
                    more_suggest_commit.setBackgroundResource(R.drawable.button_org_grauly);
                    more_suggest_commit.setClickable(false);
                }
            }

        });
        more_suggest_commit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                opinion = more_suggest_content.getText().toString().trim();
                if (opinion.length() == 0) {
                    showToast("反馈建议不能为空");
                    return;
                }
                if (opinion.length() > 150) {
                    showToast("字数已经超过");
                } else {
                    try {
                        opinion = URLEncoder.encode(opinion, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    startDataRequest();
                    Log.i("pepe", 1 + "");
                }
            }
        });
    }

    /**
     * 意见反馈
     */
    private void startDataRequest() {
        if (Check.hasInternet(SuggestAct.this)) {
            Log.i("pepe", 1 + "");
            SuggestAct.this.initArray();
            SuggestAct.this.param.add("opinion");
            SuggestAct.this.value.add(opinion);
            param.add("systemType");
            value.add(BaseParam.systemType);// android 版本
            param.add(BaseParam.QIAN_SHAREDPREFERENCES_USER_PHONE);
            value.add(user_phone);
            param.add("version");
            String ver = (BaseParam.getVersionName(SuggestAct.this) + " build " + BaseParam.systemType);
            value.add(ver);
            Log.i("pepe", "version:" + ver);
            SuggestAct.this.param.add(BaseParam.URL_QIAN_API_APPID);
            SuggestAct.this.value.add(myApp.appId);
            SuggestAct.this.param.add(BaseParam.URL_QIAN_API_SERVICE);
            SuggestAct.this.value.add("feedback");
            SuggestAct.this.param.add(BaseParam.URL_QIAN_API_SIGNTYPE);
            SuggestAct.this.value.add(myApp.signType);
            String[] array = new String[]{
                    "opinion=" + opinion,
                    "systemType=" + BaseParam.systemType,
                    BaseParam.QIAN_SHAREDPREFERENCES_USER_PHONE + "=" + user_phone,
                    "version=" + ver,
                    BaseParam.URL_QIAN_API_APPID + "=" + myApp.appId,
                    BaseParam.URL_QIAN_API_SERVICE + "=feedback",
                    BaseParam.URL_QIAN_API_SIGNTYPE + "=" + myApp.signType};
            Log.i("pepe", array[2]);
            String sign = apiModel.sortStringArray(array);
            SuggestAct.this.param.add(BaseParam.URL_QIAN_API_SIGN);
            SuggestAct.this.value.add(sign);
            Log.i("pepe", "sign:" + sign);
            SuggestAct.this.progressDialog = SuggestAct.this.dia.getLoginDialog(SuggestAct.this, "正在获取数据..");
            SuggestAct.this.progressDialog.show();
            Log.i("pepe", 2 + "");
            new Thread(new JsonRequeatThreadMoreSuggest(
                    SuggestAct.this,
                    myApp, SuggestAct.this.myHandler,
                    SuggestAct.this.param,
                    SuggestAct.this.value)
            ).start();
        } else {
            showToast("请检查网络连接是否正常");
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
