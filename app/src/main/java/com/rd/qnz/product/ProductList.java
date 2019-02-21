package com.rd.qnz.product;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lzy.okgo.request.PostRequest;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.orhanobut.logger.Logger;
import com.rd.qnz.R;
import com.rd.qnz.bean.BaseBean;
import com.rd.qnz.bean.CellProduct;
import com.rd.qnz.bean.ProductBean;
import com.rd.qnz.custom.KeyPatternActivity2;
import com.rd.qnz.custom.MineShow;
import com.rd.qnz.custom.MyApplication;
import com.rd.qnz.http.HttpUtils;
import com.rd.qnz.http.okgo.callback.JsonCallback;
import com.rd.qnz.tools.BaseParam;
import com.rd.qnz.tools.Check;
import com.rd.qnz.util.StatusBarCompat;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Evonne on 2016.10.14.
 * 导航栏理财界面-投资标列 1.6.0新布局(已适配)
 */

public class ProductList extends KeyPatternActivity2 implements View.OnClickListener {
    private static final String TAG = "ProductList";
    private PullToRefreshListView listview;  //PullToRefreshListView 内部藏着一个listView
    private RelativeLayout click_to_reload;
    private com.rd.qnz.adapter.ProductListAdapter2 adapter2;
    private int currentPage = 1;
    private int pernum = 10;  //每页产品个数


    private ImageView product_pic;
    private View headerView;
    private List<ProductBean.ProductListBean> productListBeens; //所有产品集合


    // TODO: 2017/3/22 0022
    private SharedPreferences sp;

    /**
     *  1.6.0
     * 新增尾部局
     */
    private RelativeLayout main_top_relativeLayout;
    private View footView;
    private TextView tv_footview;
    private List<Integer> sell_out=new ArrayList<>(); //已售罄
    MyApplication myApp;
    private RelativeLayout rl_top; //顶部透明的view

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_list);
        myApp = MyApplication.getInstance();
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction("login");
        intentFilter.addAction("loginout");
        registerReceiver(myBroadcastReceiver,intentFilter);
        sp=getSharedPreferences("start",MODE_PRIVATE);
        rl_top= (RelativeLayout) findViewById(R.id.rl_top);
        rl_top.setBackgroundColor(getResources().getColor(R.color.xilie_text));
        ViewGroup.LayoutParams params=rl_top.getLayoutParams();
        params.height = StatusBarCompat.getStatusBarHeight(this);
        rl_top.setLayoutParams(params);

        initBar();
        initView();
    }


    private BroadcastReceiver myBroadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
         String action=intent.getAction();
            switch (action){
                case "login":
                    getData1();
                    break;
                case "loginout":
                    getData1();
                    break;
            }
        }
    };

    private void initBar() {
        TextView actionbar_side_name = (TextView) findViewById(R.id.actionbar_side_name);
        actionbar_side_name.setText("理财");
    }



    private void initView() {
        main_top_relativeLayout= (RelativeLayout) findViewById(R.id.main_top_relativeLayout);
        main_top_relativeLayout.setOnClickListener(ProductList.this);
        productListBeens=new ArrayList<>();
        int windos_width = getWindowManager().getDefaultDisplay().getWidth();
        adapter2 = new com.rd.qnz.adapter.ProductListAdapter2(this,productListBeens,windos_width);

        listview = (PullToRefreshListView) findViewById(R.id.product_list);
        click_to_reload = (RelativeLayout) findViewById(R.id.click_to_reload);  //没有网络时对应的布局
        footView=View.inflate(ProductList.this,R.layout.productlist_footview,null);
        /**
         * 头部广告
         */
        headerView = getLayoutInflater().inflate(R.layout.product_pic, null);  //头布局
        product_pic = (ImageView) headerView.findViewById(R.id.product_pic);  //金鸡报喜图片



        click_to_reload.setOnClickListener(new View.OnClickListener() {  //没网络的时候点击一下去加载网络
            @Override
            public void onClick(View v) {
                getData1();  //重新获取产品列表数据
            }
        });
        listview.setMode(PullToRefreshBase.Mode.BOTH); //两边都能刷新


        int width = getResources().getDisplayMetrics().widthPixels; //得到手机屏幕的宽
        int height = width * 22 / 75;
        ViewGroup.LayoutParams para = product_pic.getLayoutParams();
        para.height = height;
        product_pic.setLayoutParams(para); //设置一下图片的宽高


        ImageLoader.getInstance().displayImage(BaseParam.URL_QIAN_DOWNLOADIMAGE, product_pic, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {

            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {
                product_pic.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {

                product_pic.setVisibility(View.VISIBLE);
                product_pic.setImageBitmap(bitmap);
            }

            @Override
            public void onLoadingCancelled(String s, View view) {

            }
        });



        listview.getRefreshableView().addHeaderView(headerView, null, false);  //添加头布局



        listview.setAdapter(adapter2);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                if (MineShow.fastClick()) {  //两次点击的间隔没有小于10秒
                    if (Check.hasInternet(ProductList.this)) {
                        click_to_reload.setVisibility(View.GONE);
                        Intent intent = new Intent(ProductList.this, ProductContentAct.class);  //跳转到产品详情页
                        intent.putExtra(BaseParam.QIAN_PRODUCT_BORROWID, productListBeens.get(arg2 - 2).getBorrowId());
                        startActivity(intent);
                    } else {
                        showToast("请检查网络连接是否正常");
                        click_to_reload.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        listview.autoRefresh();  //打开产品页面,数据主动刷新一次

        listview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {  //下拉刷新
                refreshView.getLoadingLayoutProxy().setPullLabel("释放财运刷新");
                refreshView.getLoadingLayoutProxy().setRefreshingLabel("财运刷新中...");
                refreshView.getLoadingLayoutProxy().setReleaseLabel("释放财运刷新");
                currentPage = 1;
                getData1();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                refreshView.getLoadingLayoutProxy(false, true).setPullLabel("加载更多");
                refreshView.getLoadingLayoutProxy(false, true).setRefreshingLabel("正在获取数据...");
                refreshView.getLoadingLayoutProxy(false, true).setReleaseLabel("松开获取更多数据");
                ++currentPage;
                getData1();
            }
        });
    }

    /**
     * 访问产品列表接口
     */
    public void getData1(){
        click_to_reload.setVisibility(View.GONE);
        listview.setVisibility(View.VISIBLE);

        sp =getSharedPreferences("sp_user",MODE_PRIVATE);
        String oauthToken=sp.getString("oauthToken","");
        String[] str;
        if (!TextUtils.isEmpty(oauthToken)){
            str = new String[]{BaseParam.QIAN_PERNUM ,pernum + "",
                    BaseParam.QIAN_CURRENTPAGE , currentPage + "",
                    BaseParam.QIAN_PRODUCTTYPE ,"1",
                    BaseParam.URL_QIAN_API_APPID ,myApp.appId,
                    BaseParam.QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN ,oauthToken,
                    BaseParam.URL_QIAN_API_SERVICE,"productList1xV4",
                    BaseParam.URL_QIAN_API_SIGNTYPE , myApp.signType};
            }else {
                str = new String[]{BaseParam.QIAN_PERNUM ,pernum + "",
                        BaseParam.QIAN_CURRENTPAGE , currentPage + "",
                        BaseParam.QIAN_PRODUCTTYPE ,"1",
                        BaseParam.URL_QIAN_API_APPID ,myApp.appId,
                        BaseParam.URL_QIAN_API_SERVICE,"productList1xV4",
                        BaseParam.URL_QIAN_API_SIGNTYPE , myApp.signType};
            }

        PostRequest request= HttpUtils.getRequest(BaseParam.URL_QIAN_PRODUCT_LIST,ProductList.this,str);
        request.execute(new JsonCallback<BaseBean<ProductBean>>() {
            @Override
            public void onSuccess(BaseBean<ProductBean> productBeanBaseBean, Call call, Response response) {
                Log.i("列表信息", "-------");
                Logger.json(productBeanBaseBean.resultCode.toString());
                if (currentPage == 1) {  //如果当前界面是第一页,就把原来的集合清空
                    productListBeens.clear();
                    sell_out.clear();
                    listview.setMode(PullToRefreshBase.Mode.BOTH); //两边都能拉
                    listview.getRefreshableView().removeFooterView(footView);  //添加头布局
                }

                String resultCode = productBeanBaseBean.resultCode;
                if (resultCode.equals("1")) {
                    List<ProductBean.ProductListBean> productListBeen=productBeanBaseBean.resultData.getProductList();
                    for (int i=0;i<productListBeen.size();i++){
                      String productStatus=productListBeen.get(i).getProductStatus();
                        if (productStatus.equals("0")||productStatus.equals("1")){
                            productListBeens.add(productListBeen.get(i));
                        }else {    //这个项目是已售罄的项目

                            sell_out.add(sell_out.size());
                            if (sell_out.size()>30){  //大于30了,不能再显示了
                                tv_footview= (TextView) footView.findViewById(R.id.tv_footview);
                                String[] str;
                                str = new String[]{
                                        BaseParam.URL_QIAN_API_APPID ,myApp.appId,
                                        BaseParam.URL_QIAN_API_SERVICE,"productDetail",
                                        BaseParam.URL_QIAN_API_SIGNTYPE , myApp.signType};
                                PostRequest request= HttpUtils.getRequest(BaseParam.URL_QIAN_SHOUQIN,ProductList.this,str);
                                request.execute(new JsonCallback<BaseBean<CellProduct>>() {
                                    @Override
                                    public void onSuccess(BaseBean<CellProduct> cellProductBaseBean, Call call, Response response) {
                                        tv_footview.setText("已成功募集项目"+cellProductBaseBean.resultData.getSolenum()+"个,已回款项目"+cellProductBaseBean.resultData.getRepaynum()+"个");
                                        listview.getRefreshableView().addFooterView(footView, null, false);  //添加头布局
                                        listview.setMode(PullToRefreshBase.Mode.PULL_FROM_START);//只能下拉刷新

                                    }

                                    @Override
                                    public void onError(Call call, Response response, Exception e) {  //获取数据失败了,什么都不做
                                        super.onError(call, response, e);
                                    }
                                });
                                break;
                            }else { //已售罄项目还没有大于50,还可以继续添加
                                productListBeens.add(productListBeen.get(i));
                            }

                        }
                    }


                    adapter2.notifyDataSetChanged();
                    listview.onRefreshComplete();

                }
            }
            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                Log.e("列表信息", e.toString());
                listview.onRefreshComplete();
                showToast("获取数据失败");
                click_to_reload.setVisibility(View.VISIBLE);
                productListBeens.clear();
                listview.setVisibility(View.GONE);
            }
        });
    }





    @Override
    protected void onRestart() {
        super.onRestart();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myBroadcastReceiver != null) {
            try{
                unregisterReceiver(myBroadcastReceiver);
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.main_top_relativeLayout:
               listview.getRefreshableView().smoothScrollToPosition(0);
                break;
        }
    }

    /**
     * 产品列表中间的缓存图片,这是之前的线程代码
     */




}

