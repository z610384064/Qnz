package com.rd.qnz.mine;

import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.rd.qnz.BaseActivity;
import com.rd.qnz.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 可用余额,详情页
 */
public class MyAccountBalanceDetails extends BaseActivity implements View.OnClickListener{

    @BindView(R.id.actionbar_side_left_iconfont)
    ImageView actionbarSideLeftIconfont;
    @BindView(R.id.actionbar_side_name)
    TextView actionbarSideName;




    @BindView(R.id.spread_pie_chart)
    PieChart spreadPieChart;
    @BindView(R.id.textView_account_money)
    TextView textViewAccountMoney;
    @BindView(R.id.first_text)
    TextView firstText;
    @BindView(R.id.second_text)
    TextView secondText;
    @BindView(R.id.third_text)
    TextView thirdText;

    private Double balance;
    DecimalFormat df = new DecimalFormat("0.00");
    private Double sum;     //每个界面的总额
    private Double ts_balance;
    private Double ty_balance;
    private Double zt_balance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account_balance_details);
        ButterKnife.bind(this);
        balance = getIntent().getDoubleExtra("balance", 0);
        ts_balance= balance;
        ty_balance= getIntent().getDoubleExtra("ty_balance", 0);
        zt_balance= getIntent().getDoubleExtra("zt_balance", 0);
        sum=ts_balance+ty_balance+zt_balance;
        initBar();
        initView();
    }
    private void initBar() {
        actionbarSideLeftIconfont.setVisibility(View.VISIBLE);
        actionbarSideLeftIconfont.setOnClickListener(this);
        actionbarSideName.setText("可用余额");
    }
    private void initView() {
        textViewAccountMoney.setText(df.format(balance));
        // TODO: 2017/10/9 0009  得到可提现金额,体验金收益,在途金额,显示一下


        WindowManager wm = getWindowManager();
        Display d = wm.getDefaultDisplay();
        ViewGroup.LayoutParams l = spreadPieChart.getLayoutParams();
        l.width = d.getWidth() / 5 * 3;  //圆的宽度占屏幕宽度的五分之三
        l.height = d.getWidth() / 5 * 3; //圆的高度占屏幕宽度的五分之三


            PieData mPieData = getPieData(3, 100,  (float) (ts_balance / sum), (float) (ty_balance / sum), (float) (zt_balance / sum));//已收、待收、红包
            showChart(spreadPieChart, mPieData);

    }
    /**
     * @param count 分成4部分
     * @param range
     *
     */
    private PieData getPieData(int count, float range, float a, float b, float c) {

        ArrayList<String> xValues = new ArrayList<String>();  //xVals用来表示每个饼块上的内容
        for (int i = 0; i < count; i++) {
            xValues.add("");
        }
        ArrayList<Entry> yValues = new ArrayList<Entry>();  //yVals用来表示封装每个饼块的实际数据

        /**
         * 饼图数据
         * 将一个饼形图分成四部分,四部分的数值比例为a:b:c:d
         * 所以a代表的百分比就是a%
         */

        if ((a > -0.000001 && a < +0.000001) && (b > -0.000001 && b < +0.000001) && (c > -0.000001 && c < +0.000001)) {
            yValues.add(new Entry(100, 0));
            yValues.add(new Entry(0, 1));
            yValues.add(new Entry(0, 2));
            yValues.add(new Entry(0, 3));
        } else {
            float quarterly1 = a;
            float quarterly2 = b;
            float quarterly3 = c;

            yValues.add(new Entry(quarterly1, 0));
            yValues.add(new Entry(quarterly2, 1));
            yValues.add(new Entry(quarterly3, 2));
        }
        //y轴的集合
        PieDataSet pieDataSet = new PieDataSet(yValues, "");
        pieDataSet.setSliceSpace(0f); //设置个饼状图之间的距离
        ArrayList<Integer> colors = new ArrayList<Integer>();
        // 饼图颜色
        colors.add(Color.rgb(255, 192, 0));//黄色
        colors.add(Color.rgb(1, 209, 177));//绿色
        colors.add(Color.rgb(68, 154, 228));//蓝色

        pieDataSet.setColors(colors);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float px = 2 * (metrics.densityDpi / 160f);
        pieDataSet.setSelectionShift(px);
        PieData pieData = new PieData(xValues, pieDataSet);
        return pieData;
    }
    private void showChart(PieChart pieChart, PieData pieData) {
        Legend legend = pieChart.getLegend();//设置比例图
        legend.setEnabled(false);
        pieChart.setDrawSliceText(false);
        pieChart.setDescription("");
        pieChart.setHoleRadius(88f);  //半径
        pieChart.setDrawCenterText(false);  //饼状图中间可以添加文字
        pieChart.setDrawHoleEnabled(true);//空心
        pieChart.setRotationAngle(90); // 初始旋转角度
        pieChart.setRotationEnabled(true); // 可以手动旋转
        pieChart.setUsePercentValues(false);  //显示成百分比
        pieData.setValueFormatter(new PercentFormatter());
        pieData.setValueTextSize(0);
        pieChart.setCenterTextSize(18);
        pieChart.setData(pieData);//设置数据
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.actionbar_side_left_iconfont:
                finish();
                break;
        }
    }
}
