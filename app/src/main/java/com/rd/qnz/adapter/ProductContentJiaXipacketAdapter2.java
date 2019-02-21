package com.rd.qnz.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rd.qnz.R;
import com.zhy.autolayout.utils.AutoUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 * 1.6.0可用加息劵,已适配
 */
public class ProductContentJiaXipacketAdapter2 extends BaseAdapter {

    private List<Map<String, String>> list;

    private TreeSet mSeparatorsSet = new TreeSet();
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_SEPARATOR = 1;
    private LayoutInflater mInflater;

    private Context context;
    private int buy_money = 0; //购买金额
    private int timeLimitDay = 0;//选中的标的使用期限
    private int amountMin; //最低投资金额
    private int amountMax; //最高投资金额
    private int timeMin;  //最低投资期限
    private int timeMax;//最高投资期限
    private int days; //加息时长
    private float  extraAwardApr ;//额外加息数字
    public ProductContentJiaXipacketAdapter2(Context context, List<Map<String, String>> list, int buy_money, int timeLimitDay, float extraAwardApr) {
        this.context = context;
        this.list = list;
        this.buy_money = buy_money;
        this.timeLimitDay = timeLimitDay;
        this.extraAwardApr=extraAwardApr;
    }

    public void notifyDataSetChanged(List<Map<String, String>> list) {
        this.list = list;
        super.notifyDataSetChanged();
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
        return position;
    }

    public void addSeparatorItem(final String item) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(item, item);
        list.add(map);
        // save separator position
        mSeparatorsSet.add(list.size() - 1);
        notifyDataSetChanged();
    }


    @Override
    public int getItemViewType(int position) {

        return mSeparatorsSet.contains(position) ? TYPE_SEPARATOR : TYPE_ITEM;
    }


    @Override  //这个界面只负责显示数据,什么点击事件都在MyJiaXiFragment里面做
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();

            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.homepage_product_jiaxipacket_item2, null);


            holder.jiaxi_relativeLayout = (RelativeLayout) convertView.findViewById(R.id.jiaxi_relativeLayout); //整个布局
            holder.tv_jiaxi_name = (TextView) convertView.findViewById(R.id.tv_jiaxi_name);  //加息劵名称
            holder.tv_jiaxi_apr= (TextView) convertView.findViewById(R.id.tv_jiaxi_apr);  //加息比例


            holder.tv_jiaxi_timeLimit = (TextView) convertView.findViewById(R.id.tv_jiaxi_timeLimit);//期限大于天使用
            holder.tv_jiaxi_moneyLimit = (TextView) convertView.findViewById(R.id.tv_jiaxi_moneyLimit);  //金额限制
            holder.tv_jiaxi_days= (TextView) convertView.findViewById(R.id.tv_jiaxi_days); //加息时间


            holder.tv_jiaxi_validDate = (TextView) convertView.findViewById(R.id.tv_jiaxi_validDate); //到期时间

            holder.iv_jiaxi_icon = (ImageView) convertView.findViewById(R.id.iv_jiaxi_icon); //加息打钩的图片
            holder.iv_redusestatus= (ImageView) convertView.findViewById(R.id.iv_redusestatus); //加息劵是否可用状态图
            convertView.setTag(holder);
            AutoUtils.autoSize(convertView);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (list.size() > 0) {
            holder.tv_jiaxi_moneyLimit.setText(list.get(position).get("redPacketAmount"));


            holder.tv_jiaxi_name.setText(list.get(position).get("name"));
            holder.tv_jiaxi_apr.setText(+ Double.valueOf(list.get(position).get("apr")) +"%");//加息比例
            timeMin=Integer.valueOf(list.get(position).get("timeMin"));
            timeMax= Integer.valueOf(list.get(position).get("timeMax"));
            amountMin=  Integer.valueOf(list.get(position).get("amountMin"));
            amountMax= Integer.valueOf(list.get(position).get("amountMax"));

            if (timeMax==0&&timeMin==0){
                holder.tv_jiaxi_timeLimit.setText("• 期限:无限制");
            }else if (timeMin==0){ //最低期限为0 ,最高期限不为0
                holder.tv_jiaxi_timeLimit.setText("• 期限:≤"+timeMax+"天");
            }else if (timeMax==0){ //最高期限为0,最低期限不为0
                holder.tv_jiaxi_timeLimit.setText("• 期限≥"+timeMin+"天");
            }else {  //两个都不为0
                holder.tv_jiaxi_timeLimit.setText("• 期限:"+timeMin+"天-"+timeMax+"天");
            }
            //金额的话 两边都要判断

            if (amountMax==0&&amountMin==0){
                holder.tv_jiaxi_moneyLimit.setText("• 金额:" +"无限制");
            }else if (amountMin==0){  //开始判断
                holder.tv_jiaxi_moneyLimit.setText("• 金额≤ "+amountMax);
            }else if (amountMax==0){
                holder.tv_jiaxi_moneyLimit.setText("• 金额≥ "+amountMin);
            }else {
                holder.tv_jiaxi_moneyLimit.setText("• 金额: "+amountMin+"-"+amountMax);
            }


            days=Integer.valueOf(list.get(position).get("days"));
            if (days==0){
                holder.tv_jiaxi_days.setVisibility(View.GONE);
            }else {
                holder.tv_jiaxi_days.setVisibility(View.VISIBLE);
                holder.tv_jiaxi_days.setText("• 加息时长:"+days+"天");
            }

            holder.tv_jiaxi_validDate.setText("• " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ").format(new Date(Long.valueOf(list.get(position).get("validDate")))) + "过期");

            /**
             *   判断加息劵是否可用
             */
        if (extraAwardApr!=0){  //是加息标,那么加息劵肯定不可用
            holder.iv_jiaxi_icon.setVisibility(View.INVISIBLE);
            holder.jiaxi_relativeLayout.setBackgroundResource(R.drawable.redpacked_redgray); //把红包的图换成灰色的
            holder.iv_redusestatus.setBackgroundResource(R.drawable.bukeyong);
            holder.tv_jiaxi_apr.setTextColor(0xff969696);

        }else {
            if (timeMax == 0 && timeMin == 0) { //代表没有时间限制,那就判断金额

                if (amountMin==0&&amountMax==0){ //代表金额也没有限制

                    if (list.get(position).get("status").equals("1")) {
                        holder.iv_jiaxi_icon.setVisibility(View.VISIBLE);
                    } else {
                          holder.iv_jiaxi_icon.setVisibility(View.INVISIBLE);
                    }
                    holder.jiaxi_relativeLayout.setBackgroundResource(R.drawable.redpacked_red);
                    holder.tv_jiaxi_apr.setTextColor(0xffff6e19);

                    holder.iv_redusestatus.setBackgroundResource(R.drawable.keshiyong);

                }else if(amountMin==0){ //金额最小值为0,最大值不为0  金额<= 1000
                    if (buy_money <= amountMax){ //这张加息劵可用
                        if (list.get(position).get("status").equals("1")) {
                            holder.iv_jiaxi_icon.setVisibility(View.VISIBLE);
                        } else {
                            holder.iv_jiaxi_icon.setVisibility(View.INVISIBLE);
                        }
                        holder.jiaxi_relativeLayout.setBackgroundResource(R.drawable.redpacked_red);
                        holder.tv_jiaxi_apr.setTextColor(0xffff6e19);

                        holder.iv_redusestatus.setBackgroundResource(R.drawable.keshiyong);
                    }else {
                        holder.iv_jiaxi_icon.setVisibility(View.INVISIBLE);
                        holder.jiaxi_relativeLayout.setBackgroundResource(R.drawable.redpacked_redgray); //把红包的图换成灰色的
                        holder.tv_jiaxi_apr.setTextColor(0xff969696);

                        holder.iv_redusestatus.setBackgroundResource(R.drawable.bukeyong);
                    }
                }else if (amountMax==0){ //金额最大值为0,最小值不为0  金额>=1000
                    if (buy_money >= amountMin){ //这张加息劵可用
                        if (list.get(position).get("status").equals("1")) {
                            holder.iv_jiaxi_icon.setVisibility(View.VISIBLE);
                        } else {
                            holder.iv_jiaxi_icon.setVisibility(View.INVISIBLE);
                        }
                        holder.jiaxi_relativeLayout.setBackgroundResource(R.drawable.redpacked_red);
                        holder.tv_jiaxi_apr.setTextColor(0xffff6e19);

                        holder.iv_redusestatus.setBackgroundResource(R.drawable.keshiyong);
                    }else {
                        holder.iv_jiaxi_icon.setVisibility(View.INVISIBLE);
                        holder.jiaxi_relativeLayout.setBackgroundResource(R.drawable.redpacked_redgray); //把红包的图换成灰色的
                        holder.tv_jiaxi_apr.setTextColor(0xff969696);

                        holder.iv_redusestatus.setBackgroundResource(R.drawable.bukeyong);
                    }
                }else {  //最大值和最小值都不为0
                    if (buy_money>=amountMin&&buy_money<=amountMax ){ //这张加息劵可用
                        if (list.get(position).get("status").equals("1")) {
                            holder.iv_jiaxi_icon.setVisibility(View.VISIBLE);
                        } else {
                            holder.iv_jiaxi_icon.setVisibility(View.INVISIBLE);
                        }
                        holder.jiaxi_relativeLayout.setBackgroundResource(R.drawable.redpacked_red);
                        holder.tv_jiaxi_apr.setTextColor(0xffff6e19);

                        holder.iv_redusestatus.setBackgroundResource(R.drawable.keshiyong);
                    }else {
                        holder.iv_jiaxi_icon.setVisibility(View.INVISIBLE);
                        holder.jiaxi_relativeLayout.setBackgroundResource(R.drawable.redpacked_redgray); //把红包的图换成灰色的
                        holder.tv_jiaxi_apr.setTextColor(0xff969696);

                        holder.iv_redusestatus.setBackgroundResource(R.drawable.bukeyong);
                    }
                }

            } else if (timeMax == 0) {  //加息劵的最大值为0那么就只需要判断最低期限就行了,最小值不为0,判断最小值
                if (timeLimitDay >= timeMin) {  //标的期限大于加息劵最小期限,判断金额

                    if (amountMin==0&&amountMax==0){ //代表金额也没有限制
                        if (list.get(position).get("status").equals("1")) {
                            holder.iv_jiaxi_icon.setVisibility(View.VISIBLE);
                        } else {
                            holder.iv_jiaxi_icon.setVisibility(View.INVISIBLE);
                        }
                        holder.jiaxi_relativeLayout.setBackgroundResource(R.drawable.redpacked_red);
                         holder.iv_redusestatus.setBackgroundResource(R.drawable.keshiyong);
                        holder.tv_jiaxi_apr.setTextColor(0xffff6e19);

                    }else if(amountMin==0){ //金额最小值为0,最大值不为0  金额<= 1000
                        if (buy_money <= amountMax){ //这张加息劵可用
                            if (list.get(position).get("status").equals("1")) {
                                holder.iv_jiaxi_icon.setVisibility(View.VISIBLE);
                            } else {
                                holder.iv_jiaxi_icon.setVisibility(View.INVISIBLE);
                            }
                            holder.jiaxi_relativeLayout.setBackgroundResource(R.drawable.redpacked_red);
                            holder.tv_jiaxi_apr.setTextColor(0xffff6e19);

                            holder.iv_redusestatus.setBackgroundResource(R.drawable.keshiyong);
                        }else {
                            holder.iv_jiaxi_icon.setVisibility(View.INVISIBLE);
                            holder.jiaxi_relativeLayout.setBackgroundResource(R.drawable.redpacked_redgray); //把红包的图换成灰色的
                            holder.tv_jiaxi_apr.setTextColor(0xff969696);

                            holder.iv_redusestatus.setBackgroundResource(R.drawable.bukeyong);
                        }
                    }else if (amountMax==0){ //金额最大值为0,最小值不为0  金额>=1000
                        if (buy_money >= amountMin){ //这张加息劵可用
                       if (list.get(position).get("status").equals("1")) {
                            holder.iv_jiaxi_icon.setVisibility(View.VISIBLE);
                        } else {
                            holder.iv_jiaxi_icon.setVisibility(View.INVISIBLE);
                        }
                            holder.jiaxi_relativeLayout.setBackgroundResource(R.drawable.redpacked_red);
                            holder.iv_redusestatus.setBackgroundResource(R.drawable.keshiyong);
                            holder.tv_jiaxi_apr.setTextColor(0xffff6e19);

                        }else {
                            holder.iv_jiaxi_icon.setVisibility(View.INVISIBLE);
                            holder.jiaxi_relativeLayout.setBackgroundResource(R.drawable.redpacked_redgray); //把红包的图换成灰色的
                            holder.iv_redusestatus.setBackgroundResource(R.drawable.bukeyong);
                            holder.tv_jiaxi_apr.setTextColor(0xff969696);

                        }
                    }else {  //最大值和最小值都不为0
                        if (buy_money>=amountMin&&buy_money<=amountMax ){ //这张加息劵可用
                            if (list.get(position).get("status").equals("1")) {
                                holder.iv_jiaxi_icon.setVisibility(View.VISIBLE);
                            } else {
                                holder.iv_jiaxi_icon.setVisibility(View.INVISIBLE);
                            }
                            holder.jiaxi_relativeLayout.setBackgroundResource(R.drawable.redpacked_red);
                            holder.iv_redusestatus.setBackgroundResource(R.drawable.keshiyong);
                            holder.tv_jiaxi_apr.setTextColor(0xffff6e19);

                        }else {
                            holder.iv_jiaxi_icon.setVisibility(View.INVISIBLE);
                            holder.jiaxi_relativeLayout.setBackgroundResource(R.drawable.redpacked_redgray); //把红包的图换成灰色的
                            holder.iv_redusestatus.setBackgroundResource(R.drawable.bukeyong);
                            holder.tv_jiaxi_apr.setTextColor(0xff969696);

                        }
                    }


                } else {
                    holder.iv_jiaxi_icon.setVisibility(View.INVISIBLE);
                    holder.jiaxi_relativeLayout.setBackgroundResource(R.drawable.redpacked_redgray); //把红包的图换成灰色的
                    holder.iv_redusestatus.setBackgroundResource(R.drawable.bukeyong);
                    holder.tv_jiaxi_apr.setTextColor(0xff969696);

                }

            } else if (timeMin == 0) {  //加息劵最低期限是0,那么只需要判断最高期限就行了
                if (timeLimitDay <= timeMax) { //期限没问题,判断金额

                    if (amountMin==0&&amountMax==0){ //代表金额也没有限制
                        if (list.get(position).get("status").equals("1")) {
                            holder.iv_jiaxi_icon.setVisibility(View.VISIBLE);
                        } else {
                            holder.iv_jiaxi_icon.setVisibility(View.INVISIBLE);
                        }
                        holder.jiaxi_relativeLayout.setBackgroundResource(R.drawable.redpacked_red);
                        holder.iv_redusestatus.setBackgroundResource(R.drawable.keshiyong);
                        holder.tv_jiaxi_apr.setTextColor(0xffff6e19);

                    }else if(amountMin==0){ //金额最小值为0,最大值不为0  金额<= 1000
                        if (buy_money <= amountMax){ //这张加息劵可用
                            if (list.get(position).get("status").equals("1")) {
                                holder.iv_jiaxi_icon.setVisibility(View.VISIBLE);
                            } else {
                                holder.iv_jiaxi_icon.setVisibility(View.INVISIBLE);
                            }
                            holder.jiaxi_relativeLayout.setBackgroundResource(R.drawable.redpacked_red);
                            holder.iv_redusestatus.setBackgroundResource(R.drawable.keshiyong);
                            holder.tv_jiaxi_apr.setTextColor(0xffff6e19);

                        }else {
                            holder.iv_jiaxi_icon.setVisibility(View.INVISIBLE);
                            holder.jiaxi_relativeLayout.setBackgroundResource(R.drawable.redpacked_redgray); //把红包的图换成灰色的
                            holder.iv_redusestatus.setBackgroundResource(R.drawable.bukeyong);
                            holder.tv_jiaxi_apr.setTextColor(0xff969696);

                        }
                    }else if (amountMax==0){ //金额最大值为0,最小值不为0  金额>=1000
                        if (buy_money >= amountMin){ //这张加息劵可用
                            if (list.get(position).get("status").equals("1")) {
                                holder.iv_jiaxi_icon.setVisibility(View.VISIBLE);
                            } else {
                                holder.iv_jiaxi_icon.setVisibility(View.INVISIBLE);
                            }
                            holder.jiaxi_relativeLayout.setBackgroundResource(R.drawable.redpacked_red);
                            holder.iv_redusestatus.setBackgroundResource(R.drawable.keshiyong);
                            holder.tv_jiaxi_apr.setTextColor(0xffff6e19);

                        }else {
                            holder.iv_jiaxi_icon.setVisibility(View.INVISIBLE);
                            holder.jiaxi_relativeLayout.setBackgroundResource(R.drawable.redpacked_redgray); //把红包的图换成灰色的
                            holder.iv_redusestatus.setBackgroundResource(R.drawable.bukeyong);
                            holder.tv_jiaxi_apr.setTextColor(0xff969696);

                        }
                    }else {  //最大值和最小值都不为0
                        if (buy_money>=amountMin&&buy_money<=amountMax ){ //这张加息劵可用
                            if (list.get(position).get("status").equals("1")) {
                                holder.iv_jiaxi_icon.setVisibility(View.VISIBLE);
                            } else {
                                holder.iv_jiaxi_icon.setVisibility(View.INVISIBLE);
                            }
                            holder.jiaxi_relativeLayout.setBackgroundResource(R.drawable.redpacked_red);
                            holder.iv_redusestatus.setBackgroundResource(R.drawable.keshiyong);
                            holder.tv_jiaxi_apr.setTextColor(0xffff6e19);

                        }else {
                            holder.iv_jiaxi_icon.setVisibility(View.INVISIBLE);
                            holder.jiaxi_relativeLayout.setBackgroundResource(R.drawable.redpacked_redgray); //把红包的图换成灰色的
                            holder.iv_redusestatus.setBackgroundResource(R.drawable.bukeyong);
                            holder.tv_jiaxi_apr.setTextColor(0xff969696);

                        }
                    }

                } else {
                    holder.iv_jiaxi_icon.setVisibility(View.INVISIBLE);
                    holder.jiaxi_relativeLayout.setBackgroundResource(R.drawable.redpacked_redgray); //把红包的图换成灰色的
                    holder.iv_redusestatus.setBackgroundResource(R.drawable.bukeyong);
                    holder.tv_jiaxi_apr.setTextColor(0xff969696);

                }
            } else { //加息劵最大和最小期限都不为0
                if (timeLimitDay >= timeMin && timeLimitDay <= timeMax) {

                    if (amountMin==0&&amountMax==0){ //代表金额也没有限制
                        if (list.get(position).get("status").equals("1")) {
                            holder.iv_jiaxi_icon.setVisibility(View.VISIBLE);
                        } else {
                            holder.iv_jiaxi_icon.setVisibility(View.INVISIBLE);
                        }
                        holder.jiaxi_relativeLayout.setBackgroundResource(R.drawable.redpacked_red);
                        holder.iv_redusestatus.setBackgroundResource(R.drawable.keshiyong);
                        holder.tv_jiaxi_apr.setTextColor(0xffff6e19);
                    }else if(amountMin==0){ //金额最小值为0,最大值不为0  金额<= 1000
                        if (buy_money <= amountMax){ //这张加息劵可用
                            if (list.get(position).get("status").equals("1")) {
                                holder.iv_jiaxi_icon.setVisibility(View.VISIBLE);
                            } else {
                                holder.iv_jiaxi_icon.setVisibility(View.INVISIBLE);
                            }
                            holder.jiaxi_relativeLayout.setBackgroundResource(R.drawable.redpacked_red);
                            holder.iv_redusestatus.setBackgroundResource(R.drawable.keshiyong);
                            holder.tv_jiaxi_apr.setTextColor(0xffff6e19);
                        }else {
                            holder.iv_jiaxi_icon.setVisibility(View.INVISIBLE);
                            holder.jiaxi_relativeLayout.setBackgroundResource(R.drawable.redpacked_redgray); //把红包的图换成灰色的
                            holder.iv_redusestatus.setBackgroundResource(R.drawable.bukeyong);
                            holder.tv_jiaxi_apr.setTextColor(0xff969696);
                        }
                    }else if (amountMax==0){ //金额最大值为0,最小值不为0  金额>=1000
                        if (buy_money >= amountMin){ //这张加息劵可用
                            if (list.get(position).get("status").equals("1")) {
                                holder.iv_jiaxi_icon.setVisibility(View.VISIBLE);
                            } else {
                                holder.iv_jiaxi_icon.setVisibility(View.INVISIBLE);
                            }
                            holder.jiaxi_relativeLayout.setBackgroundResource(R.drawable.redpacked_red);
                            holder.iv_redusestatus.setBackgroundResource(R.drawable.keshiyong);
                            holder.tv_jiaxi_apr.setTextColor(0xffff6e19);
                        }else {
                            holder.iv_jiaxi_icon.setVisibility(View.INVISIBLE);
                            holder.jiaxi_relativeLayout.setBackgroundResource(R.drawable.redpacked_redgray); //把红包的图换成灰色的
                            holder.iv_redusestatus.setBackgroundResource(R.drawable.bukeyong);
                            holder.tv_jiaxi_apr.setTextColor(0xff969696);
                        }
                    }else {  //最大值和最小值都不为0
                        if (buy_money>=amountMin&&buy_money<=amountMax ){ //这张加息劵可用
                            if (list.get(position).get("status").equals("1")) {
                                holder.iv_jiaxi_icon.setVisibility(View.VISIBLE);
                            } else {
                                holder.iv_jiaxi_icon.setVisibility(View.INVISIBLE);
                            }
                            holder.jiaxi_relativeLayout.setBackgroundResource(R.drawable.redpacked_red);
                            holder.iv_redusestatus.setBackgroundResource(R.drawable.keshiyong);
                            holder.tv_jiaxi_apr.setTextColor(0xffff6e19);
                        }else {
                            holder.jiaxi_relativeLayout.setBackgroundResource(R.drawable.redpacked_redgray); //把红包的图换成灰色的
                            holder.iv_redusestatus.setBackgroundResource(R.drawable.bukeyong);
                            holder.tv_jiaxi_apr.setTextColor(0xff969696);
                        }
                    }

                } else {
                    holder.jiaxi_relativeLayout.setBackgroundResource(R.drawable.redpacked_redgray); //把红包的图换成灰色的
                    holder.iv_redusestatus.setBackgroundResource(R.drawable.bukeyong);
                    holder.tv_jiaxi_apr.setTextColor(0xff969696);
                }
            }

            }

        }
        return convertView;
    }

    private class ViewHolder {
        RelativeLayout jiaxi_relativeLayout; //整个布局
        TextView tv_jiaxi_name; //加息劵名称

        TextView tv_jiaxi_apr; //加息比例

        TextView tv_jiaxi_timeLimit;//期限
        TextView tv_jiaxi_moneyLimit; //金额限制
        TextView tv_jiaxi_days; //加息时间
        TextView tv_jiaxi_validDate; //到期时间
        ImageView     iv_jiaxi_icon; //加息是否被选中图标
        ImageView iv_redusestatus; //加息劵使用情况


    }

}
