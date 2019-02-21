package com.rd.qnz.mine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.okgo.request.PostRequest;
import com.rd.qnz.R;
import com.rd.qnz.bean.BaseBean;
import com.rd.qnz.bean.CePing;
import com.rd.qnz.bean.EvenInter;
import com.rd.qnz.bean.Last;
import com.rd.qnz.bean.Next;
import com.rd.qnz.custom.MyApplication;
import com.rd.qnz.http.HttpUtils;
import com.rd.qnz.http.okgo.callback.JsonCallback;
import com.rd.qnz.mine.fragment.CePing1Fragment;
import com.rd.qnz.mine.fragment.CePing2Fragment;
import com.rd.qnz.mine.fragment.CePing3Fragment;
import com.rd.qnz.mine.fragment.CePing4Fragment;
import com.rd.qnz.mine.fragment.CePing5Fragment;
import com.rd.qnz.mine.fragment.CePing6Fragment;
import com.rd.qnz.mine.fragment.CePing7Fragment;
import com.rd.qnz.tools.BaseParam;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

import static com.rd.qnz.custom.MyApplication.appId;


public class FengxianTestAct extends FragmentActivity {
    private FrameLayout framelayout;
    private int zongfeng; //总分
    private int last_fenshu; //上一次的分数
    private List<Integer> list;
    private int yeshu=0; //当前页,默认是第一页
    private MyApplication myApp;
    private Fragment Ceping1Fragment,Ceping2Fragment,Ceping3Fragment,Ceping4Fragment,Ceping5Fragment,Ceping6Fragment,Ceping7Fragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fengxian_test);
        EventBus.getDefault().register(this);//在当前界面注册一个订阅者
        framelayout= (FrameLayout) findViewById(R.id.framelayout);
        initBar();
        initView();


    }

    private void initView() {
        FragmentTransaction transaction= getSupportFragmentManager().beginTransaction();
        Ceping1Fragment=new CePing1Fragment(this);
        Ceping2Fragment=new CePing2Fragment(this);
        Ceping3Fragment=new CePing3Fragment(this);
        Ceping4Fragment=new CePing4Fragment(this);
        Ceping5Fragment=new CePing5Fragment(this);
        Ceping6Fragment=new CePing6Fragment(this);
        Ceping7Fragment=new CePing7Fragment(this);
        transaction.add(R.id.framelayout,Ceping1Fragment).show(Ceping1Fragment);
        transaction.add(R.id.framelayout,Ceping2Fragment).hide(Ceping2Fragment);
        transaction.add(R.id.framelayout,Ceping3Fragment).hide(Ceping3Fragment);
        transaction.add(R.id.framelayout,Ceping4Fragment).hide(Ceping4Fragment);
        transaction.add(R.id.framelayout,Ceping5Fragment).hide(Ceping5Fragment);
        transaction.add(R.id.framelayout,Ceping6Fragment).hide(Ceping6Fragment);
        transaction.add(R.id.framelayout,Ceping7Fragment).hide(Ceping7Fragment);
        transaction.commit();
        list=new ArrayList<>();
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
        actionbar_side_name.setText("风险测评");
    }
    @Subscribe          //订阅事件FirstEvent
    public  void onEventMainThread(EvenInter event){
        if (event instanceof Next) { //下一页,那么需要加分数
            Next next= (Next) event;
            zongfeng=zongfeng+next.getFenshu();
            list.add(next.getFenshu());
            yeshu++;
           if (next.getIssend()){
                startDataRequest();
           }else {
               qiehuanFragment();
           }

        }else if (event instanceof Last){ //上一页,需要减分
            yeshu--;
            zongfeng=zongfeng-list.get(yeshu);
            qiehuanFragment();
        }
    }
    public void qiehuanFragment(){
         switch (yeshu){
             case 0:
                 FragmentTransaction transaction= getSupportFragmentManager().beginTransaction();
                 transaction.show(Ceping1Fragment);
                 transaction.hide(Ceping2Fragment);
                 transaction.hide(Ceping3Fragment);
                 transaction.hide(Ceping4Fragment);
                 transaction.hide(Ceping5Fragment);
                 transaction.hide(Ceping6Fragment);
                 transaction.hide(Ceping7Fragment);
                 transaction.commit();
                 break;
             case 1:
                  transaction= getSupportFragmentManager().beginTransaction();
                 transaction.show(Ceping2Fragment);
                 transaction.hide(Ceping1Fragment);
                 transaction.hide(Ceping3Fragment);
                 transaction.hide(Ceping4Fragment);
                 transaction.hide(Ceping5Fragment);
                 transaction.hide(Ceping6Fragment);
                 transaction.hide(Ceping7Fragment);
                 transaction.commit();
                 break;
             case 2:
                 transaction= getSupportFragmentManager().beginTransaction();
                 transaction.show(Ceping3Fragment);
                 transaction.hide(Ceping1Fragment);
                 transaction.hide(Ceping2Fragment);
                 transaction.hide(Ceping4Fragment);
                 transaction.hide(Ceping5Fragment);
                 transaction.hide(Ceping6Fragment);
                 transaction.hide(Ceping7Fragment);
                 transaction.commit();
                 break;
             case 3:
                  transaction= getSupportFragmentManager().beginTransaction();
                 transaction.show(Ceping4Fragment);
                 transaction.hide(Ceping1Fragment);
                 transaction.hide(Ceping3Fragment);
                 transaction.hide(Ceping2Fragment);
                 transaction.hide(Ceping5Fragment);
                 transaction.hide(Ceping6Fragment);
                 transaction.hide(Ceping7Fragment);
                 transaction.commit();
                 break;
             case 4:
                  transaction= getSupportFragmentManager().beginTransaction();
                 transaction.show(Ceping5Fragment);
                 transaction.hide(Ceping1Fragment);
                 transaction.hide(Ceping3Fragment);
                 transaction.hide(Ceping4Fragment);
                 transaction.hide(Ceping2Fragment);
                 transaction.hide(Ceping6Fragment);
                 transaction.hide(Ceping7Fragment);
                 transaction.commit();
                 break;
             case 5:
                  transaction= getSupportFragmentManager().beginTransaction();
                 transaction.show(Ceping6Fragment);
                 transaction.hide(Ceping1Fragment);
                 transaction.hide(Ceping3Fragment);
                 transaction.hide(Ceping4Fragment);
                 transaction.hide(Ceping5Fragment);
                 transaction.hide(Ceping2Fragment);
                 transaction.hide(Ceping7Fragment);
                 transaction.commit();
                 break;
             case 6:
                  transaction= getSupportFragmentManager().beginTransaction();
                 transaction.show(Ceping7Fragment);
                 transaction.hide(Ceping1Fragment);
                 transaction.hide(Ceping3Fragment);
                 transaction.hide(Ceping4Fragment);
                 transaction.hide(Ceping5Fragment);
                 transaction.hide(Ceping6Fragment);
                 transaction.hide(Ceping2Fragment);
                 transaction.commit();
                 break;
         }

    }
        public void startDataRequest(){
            SharedPreferences sp=getSharedPreferences("sp_user",MODE_PRIVATE);
            String oauthToken=sp.getString("oauthToken","");
            myApp = (MyApplication) getApplication();
            String str[]=new String[]{       //构建parames参数
                    "score",zongfeng+"",
                    BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN  , oauthToken,
                    "appId",appId,
                    BaseParam.URL_QIAN_API_SERVICE ,"riskResult",
                    BaseParam.URL_QIAN_API_SIGNTYPE , myApp.signType,
            };
            PostRequest request= HttpUtils.getRequest(BaseParam.URL_SEND_SCORE,this,str);
            request.execute(new JsonCallback<BaseBean<CePing>>() {

                @Override
                public void onSuccess(BaseBean<CePing> cePingBaseBean, Call call, Response response) {
                    //成功
                    Intent i=new Intent(FengxianTestAct.this,CePingResultAct.class);
                    i.putExtra("score",zongfeng);
                    startActivity(i);
                    finish();
                    
                }

                @Override
                public void onError(Call call, Response response, Exception e) {

                    Toast.makeText(FengxianTestAct.this, "网络错误,请稍后再试", Toast.LENGTH_SHORT).show();
                }
            });
        }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);//取消注册

    }

}
