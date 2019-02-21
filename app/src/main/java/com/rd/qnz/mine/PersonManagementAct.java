package com.rd.qnz.mine;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.rd.qnz.BaseActivity;
import com.rd.qnz.R;
import com.rd.qnz.custom.APIModel;
import com.rd.qnz.custom.MineShow;
import com.rd.qnz.custom.MyApplication;
import com.rd.qnz.dialog.CustomProgressDialog;
import com.rd.qnz.dialog.GetDialog;
import com.rd.qnz.gustruelock.LockSetupActivity;
import com.rd.qnz.login.Login;
import com.rd.qnz.tools.AppTool;
import com.rd.qnz.tools.BaseParam;
import com.rd.qnz.tools.Check;
import com.rd.qnz.tools.webservice.JsonRequeatThreadRefresh;
import com.rd.qnz.view.CircularNetworkImageView;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * 个人中心界面
 *
 * @author Evonne
 */

public class PersonManagementAct extends BaseActivity implements OnClickListener  {


    private static final String TAG = "账户中心";
    private RelativeLayout more_address;//收货地址
    private RelativeLayout safe_real_btn;// 实名认证
    private Dialog dialog;
    private TextView real_smrz;



    private String realStatus;
    private String phoneStatus;
    private String payPwdFlag;
    private String user_id;
    private PullToRefreshScrollView pullrefresh;
    APIModel apiModel = new APIModel();

    private ArrayList<String> param = null;
    private ArrayList<String> value = null;
    private GetDialog dia;
    private MyHandler myHandler;
    private CustomProgressDialog progressDialog = null;
    private MyApplication myApp;
    private String phone_num, name = "";
    private CircularNetworkImageView iv_head;
    private String avayar_url, cardId;
    private boolean modified;
    private SharedPreferences preferences;

    private String oauthToken;
    private int newReal = 0;//返回的实名认证确认

    private boolean isphone=true; //是拍照还是从相册选取

    /**
     * 1.5.1 新增风险测评
     * @param savedInstanceState
     */
    private RelativeLayout rl_ceping;
    private int score;  //测评分数

    /**
     * 1.6.0身份证
     * @param savedInstanceState
     */

    private RelativeLayout rl_card; //身份证一栏布局
    private TextView tv_name; //姓名
    private TextView tv_cardId; //身份证号码
    private RelativeLayout rl_bank;
    private TextView tv_bank,tv_bankname,tv_bankcard;
    private RelativeLayout rl_person;//个人信息
    private String userName,realName,birthday,sex;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.person_manager);
        phone_num = getIntent().getStringExtra("phone");
        score=getIntent().getIntExtra("score",0);
        avayar_url=getIntent().getStringExtra("avatarUrl");
        this.dia = new GetDialog();
        myApp = (MyApplication) getApplication();
        this.myHandler = new MyHandler();

        //sp_user.xml
        preferences = getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_USER, Context.MODE_PRIVATE);
        oauthToken = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN, "");
        realStatus = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_REALSTATUS, "");
        phoneStatus = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_PHONESTATUS, "");
        payPwdFlag = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_PAYPWDFLAG, "");
        user_id = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_USERID, ""); //我的user_id :424
        cardId = preferences.getString("cardId", ""); //身份证



        initBar();
        intView();
    }
    private void initBar() {
        ImageView actionbar_side_left_iconfont = (ImageView) findViewById(R.id.actionbar_side_left_iconfont);
        actionbar_side_left_iconfont.setVisibility(View.VISIBLE);
        actionbar_side_left_iconfont.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (modified) {
                    setResult(RESULT_OK);
                }
                finish();
            }
        });
        TextView actionbar_side_name = (TextView) findViewById(R.id.actionbar_side_name);
        actionbar_side_name.setText("个人中心");
    }

    public void intView() {
        rl_person= (RelativeLayout) findViewById(R.id.rl_person);
        rl_person.setOnClickListener(this);

        rl_bank= (RelativeLayout) findViewById(R.id.rl_bank);
        tv_bank= (TextView) findViewById(R.id.tv_bank);
        tv_bankname= (TextView) findViewById(R.id.tv_bankname);
        tv_bankcard= (TextView) findViewById(R.id.tv_bankcard);

        rl_card= (RelativeLayout) findViewById(R.id.rl_card);
        tv_name= (TextView) findViewById(R.id.tv_name);
        tv_cardId= (TextView) findViewById(R.id.tv_cardId);

        iv_head = (CircularNetworkImageView) findViewById(R.id.mine_info_portrait_iv); //头像
        iv_head.setOnClickListener(this);
        Log.e("fff", avayar_url); //https://testqnz.qianneizhu.com//data/avatar/424_avatar_big.jpg

        ImageLoader.getInstance().displayImage(avayar_url, iv_head, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {

            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {
                iv_head.setImageResource(R.drawable.person);
            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                iv_head.setImageBitmap(bitmap);
            }

            @Override
            public void onLoadingCancelled(String s, View view) {

            }
        });


        pullrefresh = (PullToRefreshScrollView) findViewById(R.id.pullrefresh);

        pullrefresh.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        pullrefresh.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ScrollView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                // TODO Auto-generated method stub
                refreshView.getLoadingLayoutProxy().setPullLabel("释放财运刷新");
                refreshView.getLoadingLayoutProxy().setRefreshingLabel("财运刷新中...");
                refreshView.getLoadingLayoutProxy().setReleaseLabel("释放财运刷新");
                startDataRequest();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                // TODO Auto-generated method stub
                refreshView.getLoadingLayoutProxy().setPullLabel("加载更多");
                refreshView.getLoadingLayoutProxy().setRefreshingLabel("正在获取数据...");
                refreshView.getLoadingLayoutProxy().setReleaseLabel("松开获取更多数据");
                pullrefresh.onRefreshComplete();
            }
        });


        real_smrz = (TextView) findViewById(R.id.real_smrz);

        Log.i("realStatus =(1为已认证) ", realStatus);
        if (realStatus.equals("1")) {
            real_smrz.setText("已认证");
        } else {
            real_smrz.setText("未认证");
        }

        TextView phone = (TextView) findViewById(R.id.phone);
        if (phone_num.length() == 11) {
            phone.setText(phone_num.replace(phone_num.substring(3, 7), "****"));
        }

        findViewById(R.id.my_management_my_bank_list).setOnClickListener(this);//我的银行卡

        startDataRequest();

        /* 风险测评*/
        rl_ceping= (RelativeLayout) findViewById(R.id.rl_ceping);
        rl_ceping.setOnClickListener(this);
        /* 实名认证 */
        safe_real_btn = (RelativeLayout) findViewById(R.id.safe_real_btn);
        safe_real_btn.setOnClickListener(this);

        /* 收货地址 */
        more_address = (RelativeLayout) findViewById(R.id.more_address);
        more_address.setOnClickListener(this);

    }
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG); // 统计页面
        MobclickAgent.onResume(this); // 统计时长
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(TAG); // 保证 onPageEnd 在onPause 之前调用,因为 onPause
        MobclickAgent.onPause(this);// 中会保存信息
    }



    private class MyHandler extends Handler {
        public void handleMessage(Message paramMessage) {
            Bundle localBundle1 = paramMessage.getData();
            String json = localBundle1.getString(BaseParam.URL_REQUEAT_MY_GETLATESTSTATUS);  //21.	账户管理
            String checkLoginPwd = localBundle1.getString(BaseParam.URL_REQUEAT_CHECKLOGINPWD);
            if (null != progressDialog && progressDialog.isShowing()) {// 隐藏加载框
                PersonManagementAct.this.progressDialog.dismiss();
            }
            if (null != json) {
                JsonList(json);  //把数据存入sp
        }
            if (null != checkLoginPwd) {
                checkLogin(checkLoginPwd);
            }
            pullrefresh.onRefreshComplete();
            super.handleMessage(paramMessage);
        }
    }

    //验证登录密码
    private void checkLogin(String result) {

        if (result.equals("unusual")) {
            showToast("连接服务器异常");
            return;
        }
        try {
            JSONObject oj = new JSONObject(result);
            if (Check.jsonGetStringAnalysis(oj, "resultCode").equals("1")) {
                JSONObject oj1 = new JSONObject(Check.jsonGetStringAnalysis(oj, "resultData"));
                dialog.dismiss();
                Intent intent = new Intent(PersonManagementAct.this, LockSetupActivity.class);
                startActivity(intent);
            } else {
                if (Check.jsonGetStringAnalysis(oj, "errorCode").equals("TOKEN_NOT_EXIST")) {
                    startActivity(new Intent(PersonManagementAct.this, Login.class));
                } else if (Check.jsonGetStringAnalysis(oj, "errorCode").equals("TOKEN_EXPIRED")) {
                    startActivity(new Intent(PersonManagementAct.this, Login.class));
                } else {
                    showToast(Check.jsonGetStringAnalysis(oj, "errorCode"));
                }
                dialog.dismiss();
                return;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            showToast(BaseParam.ERRORCODE_CHECKFWQ);
            return;
        }
    }

    /**
     * 把从	账户管理-刷新安全中心接口返回回来的数据存入本地sp文件里面
     * @param result
     */
    private void JsonList(String result) {
        if (result.equals("unusual")) {
            showToast("连接服务器异常");
            return;
        }
        try {
            JSONObject oj = new JSONObject(result);
            if (Check.jsonGetStringAnalysis(oj, "resultCode").equals("1")) {
                JSONObject oj1 = new JSONObject(Check.jsonGetStringAnalysis(oj, "resultData"));

                SharedPreferences sp = PersonManagementAct.this.getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_USER, MODE_PRIVATE);
                // 存入数据
                Editor editor = sp.edit();
                editor.putString(BaseParam.QIAN_SHAREDPREFERENCES_USER_EMAILSTATUS,
                        Check.jsonGetStringAnalysis(oj1, "emailStatus"));
                editor.putString(BaseParam.QIAN_SHAREDPREFERENCES_USER_REALSTATUS,
                        Check.jsonGetStringAnalysis(oj1, BaseParam.QIAN_SHAREDPREFERENCES_USER_REALSTATUS));
                editor.putString(BaseParam.QIAN_SHAREDPREFERENCES_USER_PHONESTATUS,
                        Check.jsonGetStringAnalysis(oj1, BaseParam.QIAN_SHAREDPREFERENCES_USER_PHONESTATUS));


                String cardId = Check.jsonGetStringAnalysis(oj1, "cardId");
                sex=Check.jsonGetStringAnalysis(oj1, "sex");
                birthday=Check.jsonGetStringAnalysis(oj1, "birthday");
                if (Check.jsonGetStringAnalysis(oj1, BaseParam.QIAN_SHAREDPREFERENCES_USER_REALSTATUS).equals("1")) {
                    name = Check.jsonGetStringAnalysis(oj1, "realName");
                    real_smrz.setVisibility(View.GONE);
                    rl_card.setVisibility(View.VISIBLE);
                    tv_name.setText(name);
//                    身份证号,前三位,后四位
                    String cardid=cardId.substring(0,3);
                    for (int i=3;i<cardId.length()-4;i++){
                        cardid=cardid+"*";
                    }
                    cardid=cardid+cardId.substring(cardId.length()-4,cardId.length());
                    tv_cardId.setText(cardid);


                } else {  //还没有实名认证
                    name ="q"+Check.jsonGetStringAnalysis(oj1, "phone");
                    real_smrz.setText("未认证");
                }
                editor.commit();
                String bankCardNo=Check.jsonGetStringAnalysis(oj1, "bankCardNo");
                if (TextUtils.isEmpty(bankCardNo)){ //判断银行卡号是否为空
                    tv_bank.setVisibility(View.VISIBLE);
                    rl_bank.setVisibility(View.GONE);
                }else {  //银行卡号 前3位加后四位
                    tv_bank.setVisibility(View.GONE);
                    rl_bank.setVisibility(View.VISIBLE);
                    tv_bankname.setText(Check.jsonGetStringAnalysis(oj1, "bankShortName"));
                    String cardno=bankCardNo.substring(0,3);
                    for (int i=3;i<bankCardNo.length()-4;i++){
                        cardno=cardno+"*";
                    }
                    cardno=cardno+bankCardNo.substring(bankCardNo.length()-4,bankCardNo.length());
                    tv_bankcard.setText(cardno);
                }


            } else {
                if (Check.jsonGetStringAnalysis(oj, "errorCode").equals("TOKEN_NOT_EXIST")) {
                    startActivity(new Intent(PersonManagementAct.this, Login.class));
                } else if (Check.jsonGetStringAnalysis(oj, "errorCode").equals("TOKEN_EXPIRED")) {
                    startActivity(new Intent(PersonManagementAct.this, Login.class));
                } else {
                    showToast(Check.checkReturn(Check.jsonGetStringAnalysis(oj, "errorCode")));
                }
                return;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            showToast(BaseParam.ERRORCODE_CHECKFWQ);
            return;
        }

    }



    @Override
    protected void onRestart() {
        // TODO Auto-generated method stub
        super.onRestart();
        Log.e("账户中心", "onRestart");
        SharedPreferences preferences = getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_USER, Context.MODE_PRIVATE);
        oauthToken = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN, "");
        realStatus = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_REALSTATUS, "");
        phoneStatus = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_PHONESTATUS, "");
        payPwdFlag = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_PAYPWDFLAG, "");



        startDataRequest();
        AppTool.getUserStatusInfoRequest();
    }


    /**
     * 走刷新安全中心 这个接口 /api/member/getLatestStatus.html
     */
    private void startDataRequest() {
        if (Check.hasInternet(PersonManagementAct.this)) {
            PersonManagementAct.this.initArray();
            PersonManagementAct.this.param.add(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN);
            PersonManagementAct.this.value.add(oauthToken);
            PersonManagementAct.this.param.add(BaseParam.URL_QIAN_API_APPID);
            PersonManagementAct.this.value.add(myApp.appId);
            PersonManagementAct.this.param.add(BaseParam.URL_QIAN_API_SERVICE);
            PersonManagementAct.this.value.add("getLatestStatus");
            PersonManagementAct.this.param.add(BaseParam.URL_QIAN_API_SIGNTYPE);
            PersonManagementAct.this.value.add(myApp.signType);

            String[] array = new String[]{
                    BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN + "=" + oauthToken,
                    BaseParam.URL_QIAN_API_APPID + "=" + myApp.appId,
                    BaseParam.URL_QIAN_API_SERVICE + "=getLatestStatus",
                    BaseParam.URL_QIAN_API_SIGNTYPE + "=" + myApp.signType};

            String sign = apiModel.sortStringArray(array);
            PersonManagementAct.this.param.add(BaseParam.URL_QIAN_API_SIGN);
            PersonManagementAct.this.value.add(sign);
            PersonManagementAct.this.progressDialog = PersonManagementAct.this.dia.getLoginDialog(PersonManagementAct.this, "正在获取数据..");
            PersonManagementAct.this.progressDialog.show();
            new Thread(new JsonRequeatThreadRefresh(
                    PersonManagementAct.this,
                    myApp,
                    PersonManagementAct.this.myHandler,
                    PersonManagementAct.this.param,
                    PersonManagementAct.this.value)
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

    private static final int MODIFY_REQUEST_CODE = 0x15;


    /**
     * 从个人中心界面,头像更换成功返回时调用
     */
    private void initData() {
        File file = new File(MyApplication.IMG_SAVE_PATH, MyApplication.PORTRAIT_FILE_NAME);
        if (file.exists()) {
            iv_head.setImageBitmap(BitmapFactory.decodeFile(String.valueOf(file)));
        } else {
            iv_head.setImageResource(R.drawable.person);
        }

        long time = System.currentTimeMillis();
        final Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(time);
        int apm = mCalendar.get(Calendar.AM_PM);

    }

    @Override
    public void onClick(View v) {
        int vId = v.getId();
        switch (vId) {
            case R.id.rl_person: //个人信息
            Intent i=new Intent(PersonManagementAct.this,PersonInfomationAct.class);
                i.putExtra("phone_num",phone_num);
                i.putExtra("avayar_url",avayar_url);
                i.putExtra("birthday",birthday);
                i.putExtra("sex",sex);
                i.putExtra("name",name);
                startActivityForResult(i, MODIFY_REQUEST_CODE);
                break;
            case R.id.rl_ceping:  //判断是否曾经测评,是的话弹框,否则直接跳转到测评界面
                if (score==0){  //直接去测评界面
                     i=new Intent(PersonManagementAct.this,FengxianTestAct.class);
                     startActivity(i);

                }else {
                     i=new Intent(PersonManagementAct.this,CePingResultAct.class);
                     i.putExtra("score",score);
                     startActivityForResult(i,2);
                }


                break;
            case R.id.my_management_my_bank_list:  //我的银行卡
                /* 我的银行卡 原生的 */
                if (MineShow.fastClick()) {
                    MyBankCardAct.start(PersonManagementAct.this);
                }
                break;
            case R.id.safe_real_btn:  //实名认证
                /* 实名认证 */
                if (MineShow.fastClick()) {
                    if (realStatus.equals("0")) {
                        if (phoneStatus.equals("1")) {
                            Intent intent = new Intent(PersonManagementAct.this, NewRealAct.class);
                            startActivityForResult(intent, 1001);
                        }
                    } else {
                        showToast("已认证");
                    }
                }
                break;

            case R.id.more_address://收货地址
                if (MineShow.fastClick()) {
                    startActivity(new Intent(PersonManagementAct.this, AddressAct.class));
                }
                break;

            default:
                break;
        }
    }




    private String path = "";
    private String picname = "";



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
            switch (requestCode) {


                case 1001:
                    /* 取得返回的权限 */
                    if (resultCode == Activity.RESULT_OK) {
                        Bundle bundle = data.getExtras(); //接收数据
                        newReal = bundle.getInt("finish");
                        Log.e("newReal", newReal + "");
                    }

                    break;
                case MODIFY_REQUEST_CODE:
                         if (resultCode == Activity.RESULT_OK) { //那边头像更换成功
                                initData();
                            }
                            break;
                case 2:

                        finish();
                    break;

            }


    }

    private void savePortrait(Bitmap bitmap) {
        final File imgcache = new File(MyApplication.IMG_SAVE_PATH, MyApplication.PORTRAIT_FILE_NAME);
        if (imgcache.exists()) {
            imgcache.delete();
        }
        try {
            imgcache.createNewFile();
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(imgcache));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
            bos.flush();
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        uploadImage(imgcache);
    }

    public void uploadImage(File file) {

        Log.e("uploadImage", "头像上传");
        JSONObject param = new JSONObject();
        String sign = null;
        try {
            param.put(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN, oauthToken);
            param.put(BaseParam.URL_QIAN_API_APPID, myApp.appId);
            param.put(BaseParam.URL_QIAN_API_SERVICE, "uploadAvatar");
            param.put(BaseParam.URL_QIAN_API_SIGNTYPE, myApp.signType);
            String[] array = new String[]{
                    BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN + "=" + oauthToken,
                    BaseParam.URL_QIAN_API_APPID + "=" + myApp.appId,
                    BaseParam.URL_QIAN_API_SERVICE + "=uploadAvatar",
                    BaseParam.URL_QIAN_API_SIGNTYPE + "=" + myApp.signType};
            sign = apiModel.sortStringArray(array);
            param.put(BaseParam.URL_QIAN_API_SIGN, sign);
            progressDialog = dia.getLoginDialog(PersonManagementAct.this, "正在上传头像..");
            progressDialog.show();

        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = BaseParam.URL_QIAN_AVAYARURL
                + "?appId=" + myApp.appId
                + "&" + "service=" + "uploadAvatar"
                + "&" + "signType=" + myApp.signType
                + "&" + "sign=" + sign
                + "&" + "oauthToken=" + oauthToken;

        OkGo.post(url)
                .tag(this)
                .params("avatarFile", file)   // 可以添加文件上传
                        //  .connTimeOut()
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, okhttp3.Call call, okhttp3.Response response) {
                        if (null != progressDialog && progressDialog.isShowing()) {// 隐藏加载框
                            progressDialog.dismiss();
                        }
                        Log.i("头像上传返回", s);
                        try {
                            JSONObject object = new JSONObject(s);
                            int errorcode = object.getInt("resultCode");
                            if (errorcode == 1) {
                                showToast("头像上传成功");
                                modified = true;
                                Log.e("头像", avayar_url);
                            } else {
                                showToast("头像上传失败");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(okhttp3.Call call, okhttp3.Response response, Exception e) {
                        super.onError(call, response, e);
                    }

                    @Override
                    public void upProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                        //这里回调上传进度(该回调在主线程,可以直接更新ui)
                    }
                });
    }

    @Override
    public void onBackPressed() {
        if (modified) {
            setResult(RESULT_OK);
        }
        super.onBackPressed();
    }





    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != progressDialog && progressDialog.isShowing()) {// 隐藏加载框
            PersonManagementAct.this.progressDialog.dismiss();
            progressDialog=null;
        }
    }
}
