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

/**
 * 我的收益
 * Created by Evonne on 2016/11/29.
 */
public class Income extends BaseActivity {

    private PieChart mChart;


    /**
     *  我的收益
     */
    private Double received;                //已收基础利息
    private Double waitInterestNormal;    //待收基础利息
    private Double useRedpacketSum;         //已用红包
    private Double dsjx=100.27;  //待收额外利息
    private Double ysjx=300.26;  //已收额外利息


    private Double sum;     //每个界面的总额
    private TextView first_text, second_text, third_text, fourth_text,
            income_money,
            income_name;
    private TextView five_text ;//待收加息描述/待收加息文本
    private TextView six_text ;//已收加息描述/已收加息文本


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.income);
            received = getIntent().getDoubleExtra("interestNormal", 0); //已收基础利息
            waitInterestNormal=getIntent().getDoubleExtra("waitInterestNormal",0); //待收基础利息
            useRedpacketSum = getIntent().getDoubleExtra("useRedpacketSum", 0); //已用红包

            dsjx=getIntent().getDoubleExtra("waitInterestExtra",0); //待收额外利息
            ysjx=getIntent().getDoubleExtra("interestExtra",0); //已收额外利息
            sum = received + waitInterestNormal + useRedpacketSum+dsjx+ysjx;


        initBar();
        intView();   //做好文本的初始化
        mChart = (PieChart) findViewById(R.id.spread_pie_chart);  //绘画用的圆
        WindowManager wm = getWindowManager();
        Display d = wm.getDefaultDisplay();
        ViewGroup.LayoutParams l = mChart.getLayoutParams();
        l.width = d.getWidth() / 5 * 3;  //圆的宽度占屏幕宽度的五分之三
        l.height = d.getWidth() / 5 * 3; //圆的高度占屏幕宽度的五分之三


            income_name.setText("我的收益（元）");
            income_money.setText(formatMoney(sum));
            if (sum != 0) {
                PieData mPieData = getPieData2(5, 100, (float) (received / sum), (float) (waitInterestNormal / sum), (float) (useRedpacketSum / sum), (float) (dsjx / sum),(float) (ysjx / sum));//已收、待收、红包
                showChart(mChart, mPieData);
            } else {
                PieData mPieData = getPieData(4, 100, 0, 0, 0, 0);//已收、待收、红包
                showChart(mChart, mPieData);
            }

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

            actionbar_side_name.setText("我的收益");

    }

    public void intView() {




        income_money = (TextView) findViewById(R.id.income_money);
        income_name = (TextView) findViewById(R.id.income_name);

        first_text = (TextView) findViewById(R.id.first_text);
        second_text = (TextView) findViewById(R.id.second_text);
        third_text = (TextView) findViewById(R.id.third_text);
        fourth_text = (TextView) findViewById(R.id.fourth_text);


        five_text= (TextView) findViewById(R.id.five_text);

        six_text= (TextView) findViewById(R.id.six_text);





            first_text.setText(formatMoney(received));//已收基础利息
            second_text.setText(formatMoney(waitInterestNormal));//待收利息
            third_text.setText(formatMoney(useRedpacketSum));//已用红包
            five_text.setText(formatMoney(dsjx));
            six_text.setText(formatMoney(ysjx));

    }

    private String formatMoney(Double money) {
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(money);
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

    /**
     * @param count 分成4部分
     * @param range
     *
     */                          //   4       100
    private PieData getPieData(int count, float range, float a, float b, float c, float d) {

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

        if ((a > -0.000001 && a < +0.000001) && (b > -0.000001 && b < +0.000001) && (c > -0.000001 && c < +0.000001) && (d > -0.000001 && d < +0.000001)) {
            yValues.add(new Entry(100, 0));
            yValues.add(new Entry(0, 1));
            yValues.add(new Entry(0, 2));
            yValues.add(new Entry(0, 3));
        } else {
            float quarterly1 = a;
            float quarterly2 = b;
            float quarterly3 = c;
            float quarterly4 = d;
            yValues.add(new Entry(quarterly1, 0));
            yValues.add(new Entry(quarterly2, 1));
            yValues.add(new Entry(quarterly3, 2));
            yValues.add(new Entry(quarterly4, 3));
        }
        //y轴的集合
        PieDataSet pieDataSet = new PieDataSet(yValues, "");
        pieDataSet.setSliceSpace(0f); //设置个饼状图之间的距离
        ArrayList<Integer> colors = new ArrayList<Integer>();
        // 饼图颜色
        colors.add(Color.rgb(255, 192, 0));//黄色
        colors.add(Color.rgb(1, 209, 177));//绿色
        colors.add(Color.rgb(68, 154, 228));//蓝色
        colors.add(Color.rgb(255, 123, 17));//橙色
        pieDataSet.setColors(colors);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float px = 2 * (metrics.densityDpi / 160f);
        pieDataSet.setSelectionShift(px);
        PieData pieData = new PieData(xValues, pieDataSet);
        return pieData;
    }

    /**
     * @param count 分成5部分
     * @param range
     *
     */                          //   4       100
    private PieData getPieData2(int count, float range, float a, float b, float c, float d,float e) {

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

        if ((a > -0.000001 && a < +0.000001) && (b > -0.000001 && b < +0.000001) && (c > -0.000001 && c < +0.000001) && (d > -0.000001 && d < +0.000001) && (e > -0.000001 && e < +0.000001)) {
            yValues.add(new Entry(100, 0));
            yValues.add(new Entry(0, 1));
            yValues.add(new Entry(0, 2));
            yValues.add(new Entry(0, 3));
            yValues.add(new Entry(0, 4));
        } else {
            float quarterly1 = a;
            float quarterly2 = b;
            float quarterly3 = c;
            float quarterly4 = d;
            float quarterly5 = e;
            yValues.add(new Entry(quarterly1, 0));
            yValues.add(new Entry(quarterly2, 1));
            yValues.add(new Entry(quarterly3, 2));
            yValues.add(new Entry(quarterly4, 3));
            yValues.add(new Entry(quarterly5, 4));
        }
        //y轴的集合
        PieDataSet pieDataSet = new PieDataSet(yValues, "");
        pieDataSet.setSliceSpace(0f); //设置个饼状图之间的距离
        ArrayList<Integer> colors = new ArrayList<Integer>();
        // 饼图颜色
        colors.add(Color.rgb(255, 192, 0));//黄色
        colors.add(Color.rgb(1, 209, 177));//绿色
        colors.add(Color.rgb(68, 154, 228));//蓝色
        colors.add(Color.rgb(255, 123, 17));//橙色
        colors.add(Color.rgb(255, 0, 0));//红色
        pieDataSet.setColors(colors);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float px = 2 * (metrics.densityDpi / 160f);
        pieDataSet.setSelectionShift(px);
        PieData pieData = new PieData(xValues, pieDataSet);
        return pieData;
    }
}

