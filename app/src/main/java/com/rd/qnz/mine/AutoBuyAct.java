
package com.rd.qnz.mine;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.lzy.okgo.request.PostRequest;
import com.rd.qnz.BaseActivity;
import com.rd.qnz.R;
import com.rd.qnz.WebViewAct;
import com.rd.qnz.bean.AutoInterestBean;
import com.rd.qnz.bean.BaseBean;
import com.rd.qnz.custom.APIModel;
import com.rd.qnz.custom.MyApplication;
import com.rd.qnz.http.HttpUtils;
import com.rd.qnz.http.SSLSocketFactoryEx;
import com.rd.qnz.http.okgo.callback.JsonCallback;
import com.rd.qnz.tools.BaseParam;
import com.rd.qnz.tools.Check;
import com.rd.qnz.tools.ChildClickableLinearLayout;
import com.wheel.widget.OnWheelChangedListener;
import com.wheel.widget.WheelView;
import com.wheel.widget.adapters.ArrayWheelAdapter;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

import static com.rd.qnz.custom.MyApplication.appId;
import static com.rd.qnz.tools.BaseParam.URL_QIAN_API_APPID;


/**
 *  自动投资界面
 */
public class AutoBuyAct extends BaseActivity implements View.OnClickListener ,CompoundButton.OnCheckedChangeListener, OnWheelChangedListener
,View.OnFocusChangeListener{
    private MyApplication myApp;
    private String oauthToken;
    private HttpClient httpClient;
    private HttpPost post;


    private ChildClickableLinearLayout ll_select;  //控制标选择的总开关
    private Button change; //修改设置
    private boolean ischange=false;  //修改设置的开关
    /**
     *  投资期限
     */
    private RelativeLayout rl_time_select;// 期限选择
    private TextView  tv_time_descript,tv_maxtime_select,tv_center,tv_mintime_select;     //期限选择文本描述,最高期限,最低期限
    private  WheelView  wheelView_min_time;  //投标期限最低值
    private  WheelView  wheelView_max_time;   //投标期限最高值
    private  Button   btn_confirm;   //确认按钮
    private String[]  min_times={"不限","30天","60天","90天","120天","150天","180天"};
    private String[]  max_times={"不限","30天","60天","90天","120天","150天","180天"};
    private String min_time="不限";  //显示在自动投资界面和弹框界面的值 自身就带天
    private String max_time="不限";
    String server_min_time="";  //传给服务器的时候用
    String server_max_time="";  //传给服务器的时候用

    private String w_mintime="不限"; //滑轮当前对应的值
    private String w_maxtime="不限"; //滑轮当前对应的值

    private int max=0;  //最高期限对应的数字  只是在比较两个月的大小的时候用以下
    private int min=0;  //最低期限对应的数据
    /**
     * 利率选择
     *
     */
    private RelativeLayout rl_interest_select; //利率选择
    private TextView tv_mininterest_select,tv_interest_descript; //利率,利率选择文本描述,百分号
    private WheelView wheelView_min_interest; //最低投资利率
    private String[]  min_interests={"不限","1%","2%","3%","4%","5%","6%","7%","8%","9%","10%","11%","12%","13%","14%","15%","16%","17%","18%","19%","20%"};
    private String[]  server_interests={"0","1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20"};
    private String min_interest="不限"; //显示时候的值
    private String server_interest="0"; //传给服务器的利率
    private String w_min_interest="不限"; //滑轮当前对应的利率值
    /**
     * 最高投资金额
     */
    private TextView tv_max_money;
    private EditText et_buy_money;   //最高投资金额
    /**
     * 是否使用红包
     */
    private TextView tv_isred;
    private ToggleButton tb_red;
    private RelativeLayout rl_red;
    /**
     * 确认对话框
     */

    private LinearLayout layout;
    private Dialog dialog;

    private Button btn_auto_open_confirm;
    private TextView tv_min_time,tv_max_time,tv_lilv,tv_money,tv_red_status;
    private AutoInterestBean.AutoDataBean autodatabean; //自动投资返回的数据

    private ToggleButton tb_zdtz; //自动投资开关,是否使用红包开关
    private boolean tb_isopen=false; //当前是否开启自动投标的标记

    private LinearLayout rl_change;
    private Button no_change; //暂不修改
    private Button save_change;//保存修改
    private LinearLayout activity_auto_buy; //整个屏幕对应的布局
    private RelativeLayout rl_shoushi; //自动投资开关的布局
    private ImageView imageview; //自动投标界面的图片
    /**
     * 点击修改设置之前保存的一些变量值(让它点击暂不修改的时候可以还原回来)
     * @param savedInstanceState
     */
    private String last_interest="";
    private String last_min_time="";
    private String last_max_time="";
    private String last_money="";
    private String TAG="AutoBuyAct";
    private boolean isRedpacket=false; //是否使用红包开关
    private boolean last_isredpacket=false; //上一次使用红包的状态

    /**
     *  自动图片顶部图片
     * @param savedInstanceState
     */
    // TODO: 2017/3/22 0022

    private static final int SUCCESS = 1;
    private static final int FALL = 2;

    private String DIR_PATH;
    private String IMG_PATH;
    private SharedPreferences sp;
    private String last; //之前的缓存
    private String Last_Modified; //新的缓存

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_buy);
        myApp = (MyApplication) getApplication();
        DIR_PATH=getFilesDir().getAbsolutePath()+File.separator+"Test";
        IMG_PATH=DIR_PATH+File.separator+"auto.png";
        initBar();
        initView();

        sp=getSharedPreferences("start",MODE_PRIVATE);
        last=sp.getString("Auto_Last_Modified","");
        /**
         * 把传入的数据显示到界面上
         */
        startQueryInterestData();

    }



    private void initBar() {
        ImageView actionbar_side_left_iconfont = (ImageView) findViewById(R.id.actionbar_side_left_iconfont);
        actionbar_side_left_iconfont.setVisibility(View.VISIBLE);
        actionbar_side_left_iconfont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                finish();
            }
        });
        TextView actionbar_side_name = (TextView) findViewById(R.id.actionbar_side_name);
            actionbar_side_name.setText("自动投资");
        LinearLayout linearLayout_use = (LinearLayout) findViewById(R.id.linearLayout_use);
        linearLayout_use.setVisibility(View.VISIBLE);
        linearLayout_use.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 2017/4/11 0011  拿到h5的网页,显示规则
                WebViewAct.start(AutoBuyAct.this, "自动投资使用规则", BaseParam.URL_QIAN_AUTOFORWARD);
            }
        });

    }
    private void initView() {
        rl_shoushi= (RelativeLayout) findViewById(R.id.rl_shoushi);
        ll_select= (ChildClickableLinearLayout) findViewById(R.id.ll_select);
        tv_max_money= (TextView) findViewById(R.id.tv_max_money);
        et_buy_money= (EditText) findViewById(R.id.et_buy_money);
        et_buy_money.setOnClickListener(this);
        rl_shoushi.setOnClickListener(this);
        et_buy_money.setInputType(InputType.TYPE_CLASS_NUMBER); //只能输入数字

        tv_interest_descript= (TextView) findViewById(R.id.tv_interest_descript); //利率描述
        tv_mininterest_select= (TextView) findViewById(R.id.tv_mininterest_select);   //利率
        activity_auto_buy= (LinearLayout) findViewById(R.id.activity_auto_buy);
        activity_auto_buy.setOnClickListener(this);

        tv_isred= (TextView) findViewById(R.id.tv_isred);
        rl_red= (RelativeLayout) findViewById(R.id.rl_red);
        rl_red.setOnClickListener(this);

        rl_interest_select= (RelativeLayout) findViewById(R.id.rl_interest_select);
        tv_time_descript= (TextView) findViewById(R.id.tv_time_descript);

        tv_maxtime_select= (TextView) findViewById(R.id.tv_maxtime_select); //最高期限
        tv_center= (TextView) findViewById(R.id.tv_center);
        tv_mintime_select= (TextView) findViewById(R.id.tv_mintime_select); //最低期限

        rl_change= (LinearLayout) findViewById(R.id.rl_change);
        no_change= (Button) findViewById(R.id.no_change);  //暂不保存
        save_change= (Button) findViewById(R.id.save_change);//保存设置
        no_change.setOnClickListener(this);
        save_change.setOnClickListener(this);
        rl_change.setVisibility(View.GONE);

        rl_time_select= (RelativeLayout) findViewById(R.id.rl_time_select);
        tb_zdtz= (ToggleButton) findViewById(R.id.tb_zdtz);
        tb_zdtz.setOnCheckedChangeListener(this);
        tb_red= (ToggleButton) findViewById(R.id.tb_red);
        tb_red.setOnCheckedChangeListener(this);
        change= (Button) findViewById(R.id.change);

        change.setOnClickListener(this);
        rl_time_select.setOnClickListener(this);
        rl_interest_select.setOnClickListener(this);

        ll_select.setChildClickable(false); //一开始让子类控件都不可点击

        imageview= (ImageView) findViewById(R.id.imageview);
        imageview.setOnClickListener(this);
        int width = getResources().getDisplayMetrics().widthPixels; //得到手机屏幕的宽
        int height = width * 22 / 75;
        ViewGroup.LayoutParams para = imageview.getLayoutParams();
        para.height = height;
        imageview.setLayoutParams(para); //设置一下控件的宽高


        MyThread myThread=new MyThread(); //开启线程准备下载图片
        myThread.start();

        setColorWhite();
        SharedPreferences preferences = getSharedPreferences(BaseParam.QIAN_SHAREDPREFERENCES_USER, Context.MODE_PRIVATE);
        oauthToken = preferences.getString(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN, "");
        et_buy_money.setOnFocusChangeListener(this);



                et_buy_money.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String money=et_buy_money.getText().toString();

                if (!money.equals("")){
                    int money1=Integer.parseInt(et_buy_money.getText().toString());

                    if (money1>100000||money.length()>6){
                        et_buy_money.setText(100000+"");
                    }
                }

            }
        });


    }
    /**
     * 产品列表中间的缓存图片,这是之前的线程代码
     */


    class MyThread extends Thread {
        @Override
        public void run() {

            sp = getSharedPreferences("start", MODE_PRIVATE);
            String last_url=sp.getString("Auto_last_url",""); //上次访问接口的时间

            File f = new File(IMG_PATH);
            if (f.exists()&&!last_url.isEmpty()) { //文件存在,时间戳也存在,那么就判断时间戳是否大于24小时
                long now=System.currentTimeMillis()/1000; //毫秒
                long last = Long.parseLong(last_url);
                boolean isOverDay=isOverDay(last,now);
                if (isOverDay){  //已经超过一天了,就再去访问接口,没超过就用原图
                    getBitMapFromUrl();
                }else {  //没超过,直接用原图
                    Message message=Message.obtain();
                    message.what=4;
                    newhandler.sendMessage(message);
                }
            } else {  //文件如果不存在,那么不用管别的,直接去网络上下载图片
                getBitMapFromUrl();



            }
        }
    }

    public void  getBitMapFromUrl(){
        URL url = null;
        InputStream inStream = null;
        HttpURLConnection conn = null;
        try {

            url = new URL(BaseParam.URL_QIAN_AUTOINTERESTIMAGE);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5 * 1000);
            inStream = conn.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(inStream);
            if (bitmap != null) {  //去显示图片
                Message message = Message.obtain();
                message.obj = bitmap;
                message.what = 3;
                newhandler.sendMessage(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public boolean isOverDay(long last_url,long now){  //是否超过一天
        if (now<last_url){
            return false;
        }
        else if ((now-last_url)>(60*60*24)){
            return true;
        }else {
            return false;
        }
    }
    /**
     * 新的handler ,现在正在使用
     */
    Handler newhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                //加载网络成功进行UI的更新,处理得到的图片资源
                case SUCCESS:         //不执行
                    //通过message，拿到字节数组
                    byte[] Picture = (byte[]) msg.obj;
                    //使用BitmapFactory工厂，把字节数组转化为bitmap
                    Bitmap bitmap = BitmapFactory.decodeByteArray(Picture, 0, Picture.length);
                    //通过imageview，设置图片
                    imageview.setImageBitmap(bitmap);

                    Bitmap bitmap2 = BitmapFactory.decodeResource(getResources(), R.drawable.product_pic);
                    int bwidth = bitmap2.getWidth();
                    int bHeight = bitmap2.getHeight();
                    int width = getResources().getDisplayMetrics().widthPixels;
                    Log.e("====", bwidth + " " + bHeight + " " + width);

                    int height = width * bHeight / bwidth;
                    ViewGroup.LayoutParams para = imageview.getLayoutParams();
                    para.height = height;
                    imageview.setLayoutParams(para);


                    break;
                //当加载网络失败执行的逻辑代码
                case FALL:
                    showToast("网络出现了问题");
                    break;
                case  3: //代表图片是从网络加载来的,还需要保存到本地
                    bitmap= (Bitmap) msg.obj;
                    imageview.setImageBitmap(bitmap);
                    SavaImage(bitmap,DIR_PATH);
                    sp=getSharedPreferences("start",MODE_PRIVATE);
                    SharedPreferences.Editor editor=sp.edit();
                    editor.putString("AutoLast_Modified",Last_Modified); //把新的缓存存入本地
                    long now=System.currentTimeMillis()/1000;
                    editor.putString("Auto_last_url",now+""); //把上次访问接口的时间保持到本地
                    editor.commit();
                    break;
                case 4:  //代表图片是本地缓存的
                    show();
                    break;
            }
        }
    };

    /**
     * 显示本地图片
     */
    public  void show(){
        File f=new File(IMG_PATH);
        if (!f.exists()){
            return;
        }
        Bitmap bitmap= BitmapFactory.decodeFile(IMG_PATH);
        imageview.setImageBitmap(bitmap);
    }
    /**
     * 保存位图到本地
     * @param bitmap
     * @param path 本地路径
     * @return void
     */
    public void SavaImage(Bitmap bitmap, String path){
        File file=new File(path);
        FileOutputStream fileOutputStream=null;
        //文件夹不存在，则创建它
        if(!file.exists()){
            file.mkdir();
        }
        try {
            fileOutputStream=new FileOutputStream(IMG_PATH);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100,fileOutputStream);
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()){
            case R.id.et_buy_money:
                if(hasFocus) {
                    et_buy_money.setText("");  //一旦它得到焦点,就把文本清空,并弹出键盘


                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(et_buy_money,InputMethodManager.SHOW_FORCED);
                }
                else {  //一旦它失去焦点,就关闭键盘
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(et_buy_money.getWindowToken(), 0) ;

                    if (!et_buy_money.getText().toString().equals("")){ //失去焦点的时候如果文本不是""
                        int money=Integer.parseInt(et_buy_money.getText().toString());
                        if (money<100){
                            et_buy_money.setText(100+"");
                        }
                    }else { //失去焦点的时候如果文本是空,那么就显示上次投标的金额
                        et_buy_money.setText(last_money);
                    }
                    et_buy_money.setFocusable(false); //动态让它失去焦点
                }
                break;
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.change: //修改设置
                    rl_change.setVisibility(View.VISIBLE);

                    ll_select.setChildClickable(true); //让标的选项可以点击
                    setColorBlank();
                break;
            case R.id.no_change:  //暂不修改 把原先的数据都改回来,界面也改回来,发给服务器的数据也要改回来,然后重新改变布局

                et_buy_money.setText(last_money);
                if (last_interest.equals("0")){  //最低利率
                    min_interest="不限";
                    server_interest="0";
                }else {
                    min_interest=last_interest+"%";
                    server_interest=last_interest;
                }
                tv_mininterest_select.setText(min_interest);


                if (last_min_time.equals("0")){  //最低期限
                    min_time="不限";
                    server_min_time="0";
                }else {
                    min_time=last_min_time+"天";
                    server_min_time=last_min_time;
                }
                tv_mintime_select.setText(min_time);


                if (last_max_time.equals("0")){  //最高期限
                    max_time="不限";
                    server_max_time="0";
                }else {
                    max_time=last_max_time+"天";
                    server_max_time=last_max_time;
                }
                tv_maxtime_select.setText(max_time);

                rl_change.setVisibility(View.GONE); //把按钮隐藏,并失去焦点
                ll_select.setChildClickable(false);
                tb_red.setChecked(last_isredpacket);
                setColorWhite();
                et_buy_money.setFocusable(false);

                break;
            case R.id.save_change: //保存设置
                et_buy_money.setFocusable(false);
                if (tb_isopen) { //当前自动投标是打开状态,那就是保持修改
                    // TODO: 2017/4/13 0013  在这里判断当前自动投标的开关
                    LayoutInflater inflaterDl = LayoutInflater.from(AutoBuyAct.this);
                    layout = (LinearLayout) inflaterDl.inflate(R.layout.dialog_auto_save, null);
                    dialog = new AlertDialog.Builder(AutoBuyAct.this).create();

                    dialog.show();
                    dialog.getWindow().setContentView(layout);

                    btn_auto_open_confirm = (Button) layout.findViewById(R.id.btn_auto_open_confirm);
                    tv_min_time= (TextView) layout.findViewById(R.id.tv_min_time);
                    tv_max_time= (TextView) layout.findViewById(R.id.tv_max_time);
                    tv_lilv= (TextView) layout.findViewById(R.id.tv_lilv);
                    tv_money= (TextView) layout.findViewById(R.id.tv_money);
                    tv_red_status= (TextView) layout.findViewById(R.id.tv_red_status);
                    tv_min_time.setText(min_time);
                    tv_max_time.setText(max_time);
                    tv_lilv.setText(min_interest);
                    if(min_interest.equals("不限")){
                        tv_lilv.setText("不限");
                    }
                    if (isRedpacket){
                        tv_red_status.setText("开启");
                    }else {
                        tv_red_status.setText("关闭");
                    }
                    last_isredpacket=isRedpacket;
                    tv_money.setText(et_buy_money.getText().toString());
                    btn_auto_open_confirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) { // TODO: 2017/4/13 0013   发数据到接口
                            dialog.dismiss();
                            rl_change.setVisibility(View.GONE);
                            openAutoBuy();

                            ll_select.setChildClickable(false);
                            setColorWhite();
                            et_buy_money.setFocusable(false);
                        }
                    });

                }else {//当前是关闭状态,变成确定开启
                    // TODO: 2017/4/13 0013  在这里判断当前自动投标的开关
                    LayoutInflater inflaterDl = LayoutInflater.from(AutoBuyAct.this);
                    layout = (LinearLayout) inflaterDl.inflate(R.layout.dialog_auto_confirm, null);
                    dialog = new AlertDialog.Builder(AutoBuyAct.this).create();


                    dialog.show();

                    dialog.getWindow().setContentView(layout);


                    btn_auto_open_confirm = (Button) layout.findViewById(R.id.btn_auto_open_confirm);
                    tv_min_time= (TextView) layout.findViewById(R.id.tv_min_time);
                    tv_max_time= (TextView) layout.findViewById(R.id.tv_max_time);
                    tv_lilv= (TextView) layout.findViewById(R.id.tv_lilv);
                    tv_money= (TextView) layout.findViewById(R.id.tv_money);
                    tv_red_status= (TextView) layout.findViewById(R.id.tv_red_status);
                    tv_min_time.setText(min_time);
                    tv_max_time.setText(max_time);
                    tv_lilv.setText(min_interest);
                    if(min_interest.equals("不限")){
                        tv_lilv.setText("不限");
                    }
                    if (isRedpacket){
                        tv_red_status.setText("开启");
                    }else {
                        tv_red_status.setText("关闭");
                    }

                    tv_money.setText(et_buy_money.getText().toString());
                    btn_auto_open_confirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) { // TODO: 2017/4/13 0013   发数据到接口
                            dialog.dismiss();
                            rl_change.setVisibility(View.GONE);
                            openAutoBuy();

                            ll_select.setChildClickable(false);
                            setColorWhite();
                            et_buy_money.setFocusable(false);
                        }
                    });

                }

                break;
            case R.id.et_buy_money:  //点击输入框

                et_buy_money.setFocusable(true);//设置输入框可聚集
                et_buy_money.setFocusableInTouchMode(true);//设置触摸聚焦
                et_buy_money.requestFocus();//请求焦点
                et_buy_money.findFocus();//获取焦点
                et_buy_money.setCursorVisible(true); //得到光标
                break;

            case R.id.rl_time_select: //期限选择


                et_buy_money.setFocusable(false);

//                Toast.makeText(this, "期限选择", Toast.LENGTH_SHORT).show();
                LayoutInflater inflaterDl = LayoutInflater.from(AutoBuyAct.this);
                layout = (LinearLayout) inflaterDl.inflate(R.layout.dialog_time, null);
                dialog = new AlertDialog.Builder(AutoBuyAct.this).create();
                dialog.show();
                     Window window = dialog.getWindow();
                    window.setGravity(Gravity.BOTTOM);  //此处可以设置dialog显示的位置
                    window.setContentView(layout);
                window.getDecorView().setPadding(0, 0, 0, 0);
                WindowManager.LayoutParams lp = window.getAttributes();

                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

                window.setAttributes(lp);

                wheelView_min_time = (WheelView) layout.findViewById(R.id.id_min_month); //最低期限
                wheelView_max_time = (WheelView) layout.findViewById(R.id.id_max_month);   //最高期限

                TextView btn_confirm = (TextView) layout.findViewById(R.id.btn_confirm);
                TextView btn_cancel = (TextView) layout.findViewById(R.id.btn_cancel);
                setTimeListener();
                setTimeData();

                w_mintime="不限"; //滑轮当前对应的值,刚打开肯定是不限,只有当点击确认键,我们才可以认为显示在界面上的值和传给服务器的值改变了
                w_maxtime="不限";
                btn_confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //点击确定才能把滑轮的值赋给显示值
                        min_time=w_mintime;
                        max_time=w_maxtime;
//                        Toast.makeText(AutoBuyAct.this, "选中的期限最小值:"+min_time+"  选中的期限最大值:"+max_time, Toast.LENGTH_SHORT).show();
                        tv_maxtime_select.setText(max_time);
                        tv_mintime_select.setText(min_time);

                        if (max_time.equals("不限")&&min_time.equals("不限")){
                            server_min_time="0";
                            server_max_time="0";

                        }else if (max_time.equals("不限")){  //最大期限是无限,最小期限不是无限
                            server_max_time="0";
                            server_min_time=min_time.replaceAll("天","");

                        }else if(w_mintime.equals("不限")){
                            server_min_time="0";
                            server_max_time=max_time.replaceAll("天","");
                        }
                        else  {  //两个都没有不限
                            String max_day=max_time.replaceAll("天","");
                            String min_day=min_time.replaceAll("天","");

                            max=Integer.parseInt(max_day);
                            min=Integer.parseInt(min_day);
                            server_max_time=max+"";//最大值和最小值相同
                            server_min_time=min+"";
                            if (max<min){  //当最大期限的值小于最小期限时候,把最大期限改为无限
                                server_max_time="0";//最大期限该我不限
                                server_min_time=min+"";
                                max_time="不限";
                                tv_maxtime_select.setText(max_time);
                                tv_mintime_select.setText(min_time);


                            }
                        }
                        dialog.dismiss();
                    }
                });
                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                break;
            case R.id.rl_interest_select: //利率选择


                et_buy_money.setFocusable(false);



                LayoutInflater  inflaterD1 = LayoutInflater.from(AutoBuyAct.this);
                layout = (LinearLayout) inflaterD1.inflate(R.layout.dialog_interest, null);
                dialog = new AlertDialog.Builder(AutoBuyAct.this).create();
                dialog.show();

                window = dialog.getWindow();
                window.setGravity(Gravity.BOTTOM);  //此处可以设置dialog显示的位置
                window.setContentView(layout);
                window.getDecorView().setPadding(0, 0, 0, 0);
                lp = window.getAttributes();

                lp.width = WindowManager.LayoutParams.MATCH_PARENT;

                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

                window.setAttributes(lp);

                wheelView_min_interest= (WheelView) layout.findViewById(R.id.wheelView_min_interest);
                TextView tv_confirm = (TextView) layout.findViewById(R.id.tv_confirm);
                TextView tv_cancel= (TextView) layout.findViewById(R.id.tv_cancel);

                setInterestListener();
                setInterestData();
                w_min_interest="不限";
                tv_confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        min_interest=w_min_interest;
                        tv_mininterest_select.setText(min_interest);
                        if (min_interest.equals("不限")){
                            server_interest="0";
                        }else {
                            server_interest=getInterest(min_interest);
                        }
                        dialog.dismiss();
                    }
                });
                tv_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                break;
            case R.id.activity_auto_buy:  //点击整个大屏幕
                et_buy_money.setFocusable(false);
//                activity_auto_buy.setFocusable(true);
//                activity_auto_buy.setFocusableInTouchMode(true);
//                activity_auto_buy.setOnTouchListener(new View.OnTouchListener() {
//                    @Override
//                    public boolean onTouch(View v, MotionEvent event) {
//                        activity_auto_buy.setFocusable(true);
//                        activity_auto_buy.setFocusableInTouchMode(true);
//                        activity_auto_buy.requestFocus();
//                        activity_auto_buy.findFocus();//获取焦点
//                        et_buy_money.setCursorVisible(false); //得到光标
//                        View v1 = getCurrentFocus();
//                        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
//                        imm.hideSoftInputFromWindow(v1.getWindowToken(), 0);
//                        return false;
//                    }
//                });
                break;
            case R.id.imageview: //点击图片
                et_buy_money.setFocusable(false);

            break;
            case R.id.rl_shoushi: //自动投资布局
                et_buy_money.setFocusable(false);

                break;
            case R.id.rl_red:
                et_buy_money.setFocusable(false);
                break;
        }
    }



    /**
     * 自动投标的开关
     * @param buttonView
     * @param isChecked
     */

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.tb_zdtz:  //自动投标开关
                if (!buttonView.isPressed()){ //如果不是通过按钮触发的这个方法,而是通过调用setChecked(),我们不做操作
                    return;
                }
                //让它点击的时候不改变状态栏
                tb_zdtz.setChecked(!isChecked);

                if (tb_isopen){ //当前自动投标是打开状态,就关闭自动投标

                    LayoutInflater inflaterDl = LayoutInflater.from(AutoBuyAct.this);
                    layout = (LinearLayout) inflaterDl.inflate(R.layout.dialog_auto_close, null);
                    dialog = new AlertDialog.Builder(AutoBuyAct.this).create();
                    dialog.show();
                    dialog.getWindow().setContentView(layout);


                    Button negativeButton = (Button) layout.findViewById(R.id.negativeButton);

                    Button closeButton= (Button) layout.findViewById(R.id.closeButton);


                    negativeButton.setOnClickListener(new View.OnClickListener() { //再用用看
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    closeButton.setOnClickListener(new View.OnClickListener() { //立即关闭
                        @Override
                        public void onClick(View v) {   // TODO: 2017/4/13 0013   发数据到接口
                            dialog.dismiss();
                            closeAuto();
                        }
                    });

                }else {   //打开自动投标
                    LayoutInflater inflaterDl = LayoutInflater.from(AutoBuyAct.this);
                    layout = (LinearLayout) inflaterDl.inflate(R.layout.dialog_auto_confirm, null);
                    dialog = new AlertDialog.Builder(AutoBuyAct.this).create();
                    dialog.show();
                    dialog.getWindow().setContentView(layout);


                    btn_auto_open_confirm = (Button) layout.findViewById(R.id.btn_auto_open_confirm);
                    tv_min_time= (TextView) layout.findViewById(R.id.tv_min_time);
                    tv_max_time= (TextView) layout.findViewById(R.id.tv_max_time);
                    tv_lilv= (TextView) layout.findViewById(R.id.tv_lilv);
                    tv_money= (TextView) layout.findViewById(R.id.tv_money);
                    tv_red_status= (TextView)layout.findViewById(R.id.tv_red_status);
                    tv_min_time.setText(min_time);
                    tv_max_time.setText(max_time);
                    tv_lilv.setText(min_interest);
                    if(min_interest.equals("不限")){
                        tv_lilv.setText("不限");
                    }
                    if (isRedpacket){
                        tv_red_status.setText("开启");
                    }else {
                        tv_red_status.setText("关闭");
                    }
                    last_isredpacket=isRedpacket;
                    tv_money.setText(et_buy_money.getText().toString());
                    btn_auto_open_confirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) { // TODO: 2017/4/13 0013   发数据到接口
                            dialog.dismiss();
                            int money=Integer.parseInt(et_buy_money.getText().toString());
                            if (money<100){
                                Toast.makeText(myApp, "每次最高投资金额不能小于100", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            openAutoBuy();
                        }
                    });
                }
                break;
            case R.id.tb_red:
                if (isChecked){ //当前是开启状态
                    isRedpacket=true;
                }else {
                    isRedpacket=false;
                }
                break;
        }

    }

    /**
     * 设置期限监听
     */
    private void setTimeListener() {
        wheelView_min_time.addChangingListener(this);
        wheelView_max_time.addChangingListener(this);
    }

    /**
     * 设置期限的数据
     */
    private void setTimeData() {
        wheelView_min_time.setViewAdapter(new ArrayWheelAdapter<>(AutoBuyAct.this,min_times));
        wheelView_max_time.setViewAdapter(new ArrayWheelAdapter<>(AutoBuyAct.this,max_times));
        // 设置可见条目数量
        wheelView_min_time.setVisibleItems(6);
        wheelView_max_time.setVisibleItems(6);

    }


    /**
     * 设置利率监听
     */
    private void setInterestListener() {
        wheelView_min_interest.addChangingListener(this);
    }
    /**
     * 设置;利率数据
     */
    private void setInterestData() {
        wheelView_min_interest.setViewAdapter(new ArrayWheelAdapter<>(AutoBuyAct.this,min_interests));
        // 设置可见条目数量
        wheelView_min_interest.setVisibleItems(6);
    }

    /**
     * 滚动条状态修改
     * @param wheel the wheel view whose state has changed
     * @param oldValue the old value of current item
     * @param newValue the new value of current item
     */
    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue) {
        int current=wheel.getCurrentItem();
        if (wheel==wheelView_min_time){
//            min_time=min_times[current];
            w_mintime=min_times[current];
        }else if (wheel==wheelView_max_time){
//            max_time=max_times[current];
            w_maxtime=max_times[current];
        }else if (wheel==wheelView_min_interest){
            w_min_interest=min_interests[current];

        }
    }




    public void openAutoBuy(){
        httpClient =  SSLSocketFactoryEx.getNewHttpClient();
        post = new HttpPost(BaseParam.URL_QIAN_AUTOINTEREST);

        new Thread(){
            @Override
            public void run() {
                try {
                    List<NameValuePair> pairs=new ArrayList<>();
                    NameValuePair pair1 = new BasicNameValuePair(BaseParam.URL_QIAN_API_APPID,myApp.appId);  //键值对对象
                    pairs.add(pair1);
                    NameValuePair pair2 = new BasicNameValuePair(BaseParam.URL_QIAN_API_SERVICE ,"autoInvest");  //键值对对象
                    pairs.add(pair2);
                    NameValuePair pair3 = new BasicNameValuePair(BaseParam.URL_QIAN_API_SIGNTYPE,myApp.signType);  //键值对对象
                    pairs.add(pair3);
                    NameValuePair pair4 = new BasicNameValuePair(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN,oauthToken);  //键值对对象
                    pairs.add(pair4);
                    NameValuePair pair5 = new BasicNameValuePair("actionType","add");  //键值对对象
                    pairs.add(pair5);
                    NameValuePair pair6 = new BasicNameValuePair("way","1");  //键值对对象
                    pairs.add(pair6);
                    NameValuePair pair7 = new BasicNameValuePair("fixedMoney",et_buy_money.getText().toString());  //最高投资金额
                    pairs.add(pair7);

                        NameValuePair pair8 = new BasicNameValuePair("timeS",server_min_time);   //最低期限
                        pairs.add(pair8);
                        NameValuePair pair9 = new BasicNameValuePair("timeE",server_max_time); //最高期限
                        pairs.add(pair9);

                    NameValuePair pair10 = new BasicNameValuePair("aprS",server_interest);  //利率最低值
                    pairs.add(pair10);
                    NameValuePair pair11= new BasicNameValuePair("aprE","20");  //利率最高值 默认20
                    pairs.add(pair11);
                    NameValuePair pair12= new BasicNameValuePair("isRedpacket",isRedpacket+"");  //是否使用红包
                    pairs.add(pair12);
                    String[] array = new String[]{URL_QIAN_API_APPID + "=" + myApp.appId,
                            BaseParam.URL_QIAN_API_SERVICE + "=autoInvest",
                            BaseParam.URL_QIAN_API_SIGNTYPE + "=" + myApp.signType,
                            BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN + "=" + oauthToken,
                            "actionType"+ "=" +"add",
                            "way"+"="+"1",
                            "fixedMoney"+"="+et_buy_money.getText().toString(),
                            "timeS"+"="+server_min_time,
                            "timeE"+"="+server_max_time,
                            "aprS"+"="+server_interest,
                            "aprE"+"="+20,
                            "isRedpacket"+"="+isRedpacket,
                    };

                    APIModel apiModel = new APIModel();
                    String sign = apiModel.sortStringArray(array);/* 获取签名(参数为键值对拼接数组) */
                    NameValuePair pair13 = new BasicNameValuePair("sign",sign);  //键值对对象
                    pairs.add(pair13);


                    HttpEntity entity = new UrlEncodedFormEntity(pairs, HTTP.UTF_8);
                    post.setEntity(entity);
                    HttpResponse response= httpClient.execute(post);
                    int code= response.getStatusLine().getStatusCode();
                    if (code==200){
                        entity = response.getEntity();
                        String data = EntityUtils.toString(entity);
                        Log.d("TestActivity",data);
                        JSONObject object=new JSONObject(data);
                        if (Check.jsonGetStringAnalysis(object, "resultCode").equals("1")){
                            Message message=Message.obtain();
                            message.what=1;
                            myhandler.sendMessage(message);

                        }else if (Check.jsonGetStringAnalysis(object, "resultCode").equals("0")){
                            String msg=Check.jsonGetStringAnalysis(object, "resultMsg");
                            Message message=Message.obtain();
                            message.what=3;
                            message.obj=msg;
                            myhandler.sendMessage(message);

                        }
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
    public void closeAuto(){
        httpClient = SSLSocketFactoryEx.getNewHttpClient(); //可用通过所有的证书
//        httpClient = new DefaultHttpClient();
        post = new HttpPost(BaseParam.URL_QIAN_AUTOINTEREST);

        new Thread(){
            @Override
            public void run() {
                try {
                    List<NameValuePair> pairs=new ArrayList<>();
                    NameValuePair pair1 = new BasicNameValuePair(BaseParam.URL_QIAN_API_APPID,myApp.appId);  //键值对对象
                    pairs.add(pair1);
                    NameValuePair pair2 = new BasicNameValuePair(BaseParam.URL_QIAN_API_SERVICE ,"autoInvest");  //键值对对象
                    pairs.add(pair2);
                    NameValuePair pair3 = new BasicNameValuePair(BaseParam.URL_QIAN_API_SIGNTYPE,myApp.signType);  //键值对对象
                    pairs.add(pair3);
                    NameValuePair pair4 = new BasicNameValuePair(BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN,oauthToken);  //键值对对象
                    pairs.add(pair4);
                    NameValuePair pair5 = new BasicNameValuePair("actionType","clo");  //键值对对象
                    pairs.add(pair5);


                    String[] array = new String[]{URL_QIAN_API_APPID + "=" + myApp.appId,
                            BaseParam.URL_QIAN_API_SERVICE + "=autoInvest",
                            BaseParam.URL_QIAN_API_SIGNTYPE + "=" + myApp.signType,
                            BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN + "=" + oauthToken,
                            "actionType"+ "=" +"clo",
                    };

                    APIModel apiModel = new APIModel();
                    String sign = apiModel.sortStringArray(array);/* 获取签名(参数为键值对拼接数组) */
                    NameValuePair pair6 = new BasicNameValuePair("sign",sign);  //键值对对象
                    pairs.add(pair6);

                    HttpEntity entity = new UrlEncodedFormEntity(pairs, HTTP.UTF_8);
                    post.setEntity(entity);
                    HttpResponse response= httpClient.execute(post);
                    int code= response.getStatusLine().getStatusCode();
                    if (code==200){
                        entity = response.getEntity();
                        String data = EntityUtils.toString(entity);
                        Log.d("TestActivity",data);
                        JSONObject object=new JSONObject(data);
                        if (Check.jsonGetStringAnalysis(object, "resultCode").equals("1")){
                            Message message=Message.obtain();
                            message.what=2;
                            myhandler.sendMessage(message);

                        }
                    }else {
                        Message message=Message.obtain();
                        message.what=3;
                        message.obj="网络异常,请稍后再试";
                        myhandler.sendMessage(message);
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 请求自动投标的数据
     */
    private void startQueryInterestData() {
        String str[]=new String[]{
                "actionType","query",
                URL_QIAN_API_APPID,appId,
                BaseParam.URL_QIAN_API_SERVICE ,"autoInvest",
                BaseParam.URL_QIAN_API_SIGNTYPE , myApp.signType,
                BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN,oauthToken
        };
        PostRequest request= HttpUtils.getRequest(BaseParam.URL_QIAN_AUTOINTEREST,AutoBuyAct.this,str);
        request.execute(new JsonCallback<BaseBean<AutoInterestBean>>() {
            @Override
            public void onSuccess(BaseBean<AutoInterestBean> autoInterestBean, Call call, Response response) {
//                Toast.makeText(myApp, "获取自动投资的数据成功", Toast.LENGTH_LONG).show();
                autodatabean=autoInterestBean.resultData.getAutoData();
                String open=autodatabean.getIsOpen();
                 String isAdvance =autodatabean.getIsAdvance();
                Log.d(TAG,"autodatabean:"+autodatabean.toString());
                if (open.equals("0")){ //如果自动投标是关闭的
                    tb_zdtz.setChecked(false);
                    tb_isopen=false;
                }else {
                    tb_zdtz.setChecked(true);
                    tb_isopen=true;
                }

                if (isAdvance.equals("0")){ //用户之前是不使用红包
                    tb_red.setChecked(false);
                    last_isredpacket=false;
                }else {
                    tb_red.setChecked(true);
                    last_isredpacket=true;
                }

                String timeStart=autodatabean.getTimeStart();  //服务端返回的期限最小值
                String timeEnd=autodatabean.getTimeEnd();       //期限最大值
                String fixedMoney=autodatabean.getTenderAccount();//服务端返回的金额
                String aprStart=autodatabean.getAprStart();  //服务端返回的最低利率
                last_interest=aprStart;
                if (fixedMoney.equals("0")){
                    last_money="100000";
                }else {
                    last_money=fixedMoney;
                }

                last_min_time=timeStart;

                last_max_time=timeEnd;


                if (timeStart.equals("0")){  //标最低期限
                    tv_mintime_select.setText("不限");
                    min_time="不限";
                    server_min_time="0";
                }else {
                    tv_mintime_select.setText(timeStart+"天");
                    min_time=timeStart+"天";
                    server_min_time=timeStart;
                }
                if (timeEnd.equals("0")){  // 标最高期限
                    tv_maxtime_select.setText("不限");
                    max_time="不限";
                    server_max_time="0";
                }else {
                    tv_maxtime_select.setText(timeEnd+"天");
                    max_time=timeEnd+"天";
                    server_max_time=timeEnd;
                }
                if (!fixedMoney.equals("0")){ //如果服务器返回的投资金额为0说明用户没有设置过,那么就使用原先的布局
                    et_buy_money.setText(fixedMoney);
                }

                if (aprStart.equals("0")){ //标的利率
                    tv_mininterest_select.setText("不限");
                    min_interest="不限";
                    server_interest="0";
                }else {
                    server_interest=aprStart;
                    min_interest=aprStart+"%";
                    tv_mininterest_select.setText(min_interest);
                }


            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                Toast.makeText(myApp, "获取自动投资的数据失败", Toast.LENGTH_LONG).show();
            }
        });

    }

    private Handler myhandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:  //自动投资开启成功
                    tb_isopen=true;
                    tb_zdtz.setChecked(true);

                    last_money=et_buy_money.getText().toString();
                    last_interest=server_interest;
                    last_min_time=server_min_time;
                    last_max_time=server_max_time;
                    break;
                case 2:
                    tb_isopen=false;
                    tb_zdtz.setChecked(false);

                    break;
                case 3:
                    String message= (String) msg.obj;
                    showToast(message);
                    break;
            }
        }
    };

    public void  setColorWhite(){
        tv_max_money.setTextColor(Color.parseColor("#b1b1b1"));
        et_buy_money.setTextColor(Color.parseColor("#b1b1b1"));
        tv_isred.setTextColor(Color.parseColor("#b1b1b1"));
        tv_interest_descript.setTextColor(Color.parseColor("#b1b1b1"));
        tv_mininterest_select.setTextColor(Color.parseColor("#b1b1b1"));
        tv_time_descript.setTextColor(Color.parseColor("#b1b1b1"));
        tv_mintime_select.setTextColor(Color.parseColor("#b1b1b1"));
        tv_maxtime_select.setTextColor(Color.parseColor("#b1b1b1"));
        tv_center.setTextColor(Color.parseColor("#b1b1b1"));
        tb_red.setAlpha(0.5f);

    }
    public void  setColorBlank(){
        tv_max_money.setTextColor(Color.BLACK);
        et_buy_money.setTextColor(Color.BLACK);
        tv_isred.setTextColor(Color.BLACK);
        tv_interest_descript.setTextColor(Color.BLACK);
        tv_mininterest_select.setTextColor(Color.BLACK);
        tv_time_descript.setTextColor(Color.BLACK);
        tv_mintime_select.setTextColor(Color.BLACK);
        tv_maxtime_select.setTextColor(Color.BLACK);
        tv_center.setTextColor(Color.BLACK);
        tb_red.setAlpha(1.0f);
    }
    public String getInterest(String s){
       int length1= min_interests.length;
        int y=0;
        for (int i=0;i<length1;i++){
            if (s==min_interests[i]){
                y=i;
            }
        }
        return server_interests[y];
    }


}
