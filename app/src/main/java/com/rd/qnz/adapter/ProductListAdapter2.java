package com.rd.qnz.adapter;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.rd.qnz.R;
import com.rd.qnz.bean.ProductBean;
import com.rd.qnz.util.MathUtil;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.List;

/**
 * 新框架对应的adapter(进行AutoLayout适配)
 */
public class ProductListAdapter2 extends BaseAdapter {

    private List<ProductBean.ProductListBean> list;
    private int type;//区分新手标(1)和普通标(2)

    private Context context;
    private int windos_width,progress_width,value; //屏幕宽度,进度条宽度,文本移动的距离


    /**
     *  新框架对应的构造方法
     * @param context
     * @param list
     */

    public ProductListAdapter2(Context context, List<ProductBean.ProductListBean> list, int windos_width) {
    this.context = context;
    this.list = list;
    this.windos_width=windos_width;
}

    public ProductListAdapter2(){

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @SuppressLint({"NewApi", "ResourceAsColor"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // TODO Auto-generated method stub
        String productStatus = list.get(position).getProductStatus()+"";
        final ViewHolder holder;
        if (convertView == null) {

            convertView = LayoutInflater.from(context).inflate(R.layout.homepage_product_list_item2, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
            AutoUtils.autoSize(convertView);
        } else {
            holder = (ViewHolder) convertView.getTag();

        }

        if (position==0){

        }


        if (list.size() > 0) {

            String isNewHand=list.get(position).getIsNewHand()+"";
            //判断是否是新手
            if (isNewHand.equals("1")) {  //如果是新手,就显示新手图标,不显示抢红包
                holder.product_list_xin_img.setVisibility(View.VISIBLE);
                holder.product_list_xin_img.setBackgroundResource(R.drawable.product_new); //新手

            } else {
                holder.product_list_xin_img.setVisibility(View.GONE);
            }

            // 判断是否是活动标
            if (list.get(position).getName().indexOf("(") != -1) {   //如果出现了(
                holder.product_list_active_img.setVisibility(View.VISIBLE);  //显示活动图标
                holder.product_title.setText(list.get(position).getName().substring(0, list.get(position).getName().indexOf("(")));
                holder.product_list_active_img.setText(list.get(position).getName().substring(list.get(position).getName().indexOf("(") + 1, list.get(position).getName().indexOf(")")));
                holder.product_list_active_img.setBackgroundResource(R.drawable.product_active);  //活动图标变红色
            } else {
                holder.product_list_active_img.setVisibility(View.GONE);
                holder.product_title.setText(list.get(position).getName());
            }

             //判断是否加息
            Double extraAwardApr=Double.parseDouble(list.get(position).getExtraAwardApr());
            if (extraAwardApr!=0) {  //如果extraAwardApr=1 代表要额外加息
                holder.product_list_extraApr.setVisibility(View.VISIBLE);
                holder.homepage_jia_tv.setVisibility(View.VISIBLE);  //+号
                holder.product_list_extraApr.setText(MathUtil.formatRate(list.get(position).getExtraAwardApr()) + "%");
                holder.product_list_jiaxi_img.setVisibility(View.VISIBLE);
                holder.product_list_jiaxi_img.setBackgroundResource(R.drawable.product_jiaxi);
            } else {  //代表不是加息标,图标不显示
                holder.product_list_extraApr.setVisibility(View.GONE);
                holder.homepage_jia_tv.setVisibility(View.INVISIBLE);
                holder.product_list_jiaxi_img.setVisibility(View.GONE);
            }

            //已购金额/总金额 算出百分比来移动进度条,
            final int baifen = (int) (Float.parseFloat((list.get(position).getAccountYes())) * 100
                    / Float.parseFloat((list.get(position).getAccount())));

            holder.product_rate.setText(MathUtil.formatRate(list.get(position).getNormalApr()));

            if (productStatus.equals("0")) {    // 预发布

                holder.product_list_yu_img.setVisibility(View.VISIBLE); //显示预售

                holder.line_shengyu.setVisibility(View.VISIBLE);  //显示剩余多少元可投
                holder.line_shouqin.setVisibility(View.GONE);    //隐藏售罄这一列
                holder.lin_progress.setVisibility(View.VISIBLE); //显示进度条

                ValueAnimator animator2= ValueAnimator.ofInt(0,baifen);  //进行进度条移动
                animator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int value= (int) animation.getAnimatedValue();
                        holder.progress.setProgress(value);
                    }
                });
                animator2.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        holder.progress_ismove=true;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                holder.tv_progress_value.setText(baifen+"%");
                animator2.setDuration(200);
                animator2.start();


                /**
                 * 1.6.0 年化利率变红
                 */
                holder.product_rate.setTextColor(context.getResources().getColor(R.color.home_text_org)); //年化利率全变红
                holder.tv_first_baifen.setTextColor(context.getResources().getColor(R.color.home_text_org)); //百分号变红
                holder.homepage_jia_tv.setTextColor(context.getResources().getColor(R.color.home_text_org)); //加息的+号变红
                holder.product_list_extraApr.setTextColor(context.getResources().getColor(R.color.home_text_org));//加息数值变红
                //标题后面的图标
                holder.product_list_yu_img.setBackgroundResource(R.drawable.product_yu); //预售标颜色变蓝
                holder.product_list_jiaxi_img.setBackgroundResource(R.drawable.product_jiaxi);//加息标变红
                holder.product_list_active_img.setBackgroundResource(R.drawable.product_active);//活动标变红
                holder.product_list_xin_img.setBackgroundResource(R.drawable.product_new); //新手标变蓝


            } else if (productStatus.equals("1")) {  // 可申购

                holder.product_list_yu_img.setVisibility(View.GONE); //不显示预售标
                holder.line_shengyu.setVisibility(View.VISIBLE);  //显示剩余多少元可投
                holder.line_shouqin.setVisibility(View.GONE);    //隐藏售罄这一列
                holder.lin_progress.setVisibility(View.VISIBLE); //显示进度条

                ValueAnimator animator2= ValueAnimator.ofInt(0,baifen);  //进行进度条移动
                animator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int value= (int) animation.getAnimatedValue();
                        holder.progress.setProgress(value);
                    }
                });
                animator2.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        holder.progress_ismove=true;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                holder.tv_progress_value.setText(baifen+"%");
                animator2.setDuration(200);
                animator2.start();


                // TODO: 2017/3/21 0021

                /**
                 * 1.6.0 年化利率变红
                 */
                holder.product_rate.setTextColor(context.getResources().getColor(R.color.home_text_org)); //年化利率全变红
                holder.tv_first_baifen.setTextColor(context.getResources().getColor(R.color.home_text_org)); //百分号变红
                holder.homepage_jia_tv.setTextColor(context.getResources().getColor(R.color.home_text_org)); //加息的+号变红
                holder.product_list_extraApr.setTextColor(context.getResources().getColor(R.color.home_text_org));//加息数值变红
                //标题后面的图标
                holder.product_list_yu_img.setBackgroundResource(R.drawable.product_yu); //预售标颜色变蓝
                holder.product_list_jiaxi_img.setBackgroundResource(R.drawable.product_jiaxi);//加息标变红
                holder.product_list_active_img.setBackgroundResource(R.drawable.product_active);//活动标变红

            } else if (productStatus.equals("2")) {  // 已售完

                holder.line_shengyu.setVisibility(View.GONE); //隐藏剩余
                holder.line_shouqin.setVisibility(View.VISIBLE); //显示售罄
                holder.lin_progress.setVisibility(View.GONE); ///隐藏进度条
                holder.product_list_yu_img.setVisibility(View.GONE); //隐藏预售

                // TODO: 2017/3/21 0021

                holder.product_list_jiaxi_img.setBackgroundResource(R.drawable.product_hui); //加息变灰
                holder.product_list_active_img.setBackgroundResource(R.drawable.product_hui);//活动变灰
                holder.product_list_xin_img.setBackgroundResource(R.drawable.product_hui); //新手变灰

                /**
                 * 1.6.0 年化利率变灰
                 */
                holder.product_rate.setTextColor(context.getResources().getColor(R.color.home_text));
                holder.tv_first_baifen.setTextColor(context.getResources().getColor(R.color.home_text));
                holder.homepage_jia_tv.setTextColor(context.getResources().getColor(R.color.home_text));
                holder.product_list_extraApr.setTextColor(context.getResources().getColor(R.color.home_text));
            } else if (productStatus.equals("3")) {   // 还款中

                holder.line_shengyu.setVisibility(View.GONE);
                holder.line_shouqin.setVisibility(View.VISIBLE);
                holder.lin_progress.setVisibility(View.GONE);
                holder.product_list_yu_img.setVisibility(View.GONE); //隐藏预告

                // TODO: 2017/3/21 0021
                holder.product_list_jiaxi_img.setBackgroundResource(R.drawable.product_hui); //加息变灰
                holder.product_list_active_img.setBackgroundResource(R.drawable.product_hui); //活动变灰
                holder.product_list_xin_img.setBackgroundResource(R.drawable.product_hui); //新手变灰

                /**
                 * 1.6.0 年化利率变灰
                 */
                holder.product_rate.setTextColor(context.getResources().getColor(R.color.home_text));
                holder.tv_first_baifen.setTextColor(context.getResources().getColor(R.color.home_text));
                holder.homepage_jia_tv.setTextColor(context.getResources().getColor(R.color.home_text));
                holder.product_list_extraApr.setTextColor(context.getResources().getColor(R.color.home_text));
            } else if (productStatus.equals("4")) {    // 已完结
                holder.line_shengyu.setVisibility(View.GONE);
                holder.line_shouqin.setVisibility(View.VISIBLE);

                holder.lin_progress.setVisibility(View.GONE);
                holder.product_list_yu_img.setVisibility(View.GONE);
                // TODO: 2017/3/21 0021
                holder.product_list_xin_img.setBackgroundResource(R.drawable.product_hui); //新手变灰
                holder.product_list_jiaxi_img.setBackgroundResource(R.drawable.product_hui); //加息变灰
                holder.product_list_active_img.setBackgroundResource(R.drawable.product_hui); //活动变灰

                /**
                 * 1.6.0 年化利率变灰
                 */
                holder.product_rate.setTextColor(context.getResources().getColor(R.color.home_text));
                holder.tv_first_baifen.setTextColor(context.getResources().getColor(R.color.home_text));
                holder.homepage_jia_tv.setTextColor(context.getResources().getColor(R.color.home_text));
                holder.product_list_extraApr.setTextColor(context.getResources().getColor(R.color.home_text));
            }



            if (list.get(position).getIsday().equals("1")) {  // 天标
                holder.product_date.setText(list.get(position).getTimeLimitDay());
                holder.product_day.setText("天");
            } else {
                holder.product_date.setText(list.get(position).getTimeLimit());
                holder.product_day.setText("月");
            }

            holder.product_time.setText((Integer.parseInt(list.get(position).getAccount()) - Integer.parseInt(list.get(position).getAccountYes())) + "");
        }

        return convertView;
    }

    private class ViewHolder {
        TextView product_title;//标题
        TextView product_list_extraApr;//年化收益后面的数字+（小字）百分号   +7.2%
        TextView product_rate;//年化收益后面的数字 7.2%
        TextView product_date;//期限
        TextView product_day;//期限(天、月)
        TextView homepage_jia_tv;//年化收益后面的数字 +字

        private TextView product_time;//投资总额
        private TextView product_list_yu_img;//预告图标
        private TextView product_list_xin_img;//新手图标
        private TextView product_list_jiaxi_img;//加息图标
        private TextView product_list_active_img;//活动图标

        /**
         *  1.6.0
         *  进度条
         */
        private LinearLayout line_shengyu; //剩余可投的布局,在售罄的情况下隐藏掉
        private LinearLayout line_shouqin; //剩余可投的布局,在售罄的情况下隐藏掉

        private Boolean progress_ismove=false;
        private LinearLayout lin_progress; //进度条布局
        private TextView tv_progress_value; //进度条后面的文字
        private ProgressBar progress;
        private TextView tv_first_baifen; //年化后面的百分号
        public ViewHolder(View convertView) {
            product_list_extraApr = (TextView) convertView.findViewById(R.id.product_list_extraApr);
            product_title = (TextView) convertView.findViewById(R.id.product_title);
            homepage_jia_tv = (TextView) convertView.findViewById(R.id.homepage_jia_tv);
            product_rate = (TextView) convertView.findViewById(R.id.product_rate);
            product_date = (TextView) convertView.findViewById(R.id.product_list_limit_time);
            product_day = (TextView) convertView.findViewById(R.id.product_day);
            product_time = (TextView) convertView.findViewById(R.id.product_time);
            product_list_yu_img = (TextView) convertView.findViewById(R.id.product_list_yu_img);
            product_list_xin_img = (TextView) convertView.findViewById(R.id.product_list_xin_img);
            product_list_jiaxi_img = (TextView) convertView.findViewById(R.id.product_list_jiaxi_img);
            product_list_active_img = (TextView) convertView.findViewById(R.id.product_list_active_img);
            tv_progress_value= (TextView) convertView.findViewById(R.id.tv_progress_value);
            progress= (ProgressBar) convertView.findViewById(R.id.progress);
            lin_progress= (LinearLayout) convertView.findViewById(R.id.lin_progress);
            line_shengyu= (LinearLayout) convertView.findViewById(R.id.line_shengyu);
            line_shouqin= (LinearLayout) convertView.findViewById(R.id.line_shouqin);
            tv_first_baifen= (TextView) convertView.findViewById(R.id.tv_first_baifen);
        }
    }

}
