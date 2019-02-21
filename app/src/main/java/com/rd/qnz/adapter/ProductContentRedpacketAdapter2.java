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
 * 可用红包,不可用红包对应的adapter(已适配)
 */
public class ProductContentRedpacketAdapter2 extends BaseAdapter {

    private List<Map<String, String>> list;

    private TreeSet mSeparatorsSet = new TreeSet();
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_SEPARATOR = 1;
    private LayoutInflater mInflater;

    private Context context;
    private int buy_money = 0;
    private int timeLimitDay = 0;//选中的标的使用期限

    public ProductContentRedpacketAdapter2(Context context, List<Map<String, String>> list, int buy_money, int timeLimitDay) {
        this.context = context;
        this.list = list;
        this.buy_money = buy_money;
        this.timeLimitDay = timeLimitDay;
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


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();

            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.homepage_product_redpacket_item2, null);

            holder.repad_relativeLayout = (RelativeLayout) convertView.findViewById(R.id.repad_relativeLayout);
            holder.redpacket_money = (TextView) convertView.findViewById(R.id.redpacket_money);

            holder.redpacket_title = (TextView) convertView.findViewById(R.id.redpacket_title);
            holder.redpacket_dayLimit = (TextView) convertView.findViewById(R.id.redpacket_dayLimit);
            holder.activity_red = (TextView) convertView.findViewById(R.id.activity_red);
            holder.redpacket_timeLimit = (TextView) convertView.findViewById(R.id.redpacket_timeLimit);
            holder.redpacket_icon = (ImageView) convertView.findViewById(R.id.redpacket_icon);
            holder.iv_redusestatus= (ImageView) convertView.findViewById(R.id.iv_redusestatus);
            convertView.setTag(holder);
            AutoUtils.autoSize(convertView);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (list.size() > 0) {
            holder.redpacket_money.setText(list.get(position).get("redPacketAmount"));

            holder.redpacket_title.setText("• " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(Long.valueOf(list.get(position).get("validDate")))) + "过期");

            holder.redpacket_dayLimit.setText("• 金额≥" + Integer.valueOf(list.get(position).get("dayLimit")) + "元");

            holder.activity_red.setText(list.get(position).get("redPacketType"));

            if (list.get(position).get("timeLimit").equals("0")) {
                holder.redpacket_timeLimit.setText("• 不限产品期限,");
            } else {
                holder.redpacket_timeLimit.setText("• 期限≥" + list.get(position).get("timeLimit") + "天,");
            }



            if (timeLimitDay < Integer.parseInt(list.get(position).get("timeLimit"))
                    || buy_money < Double.valueOf(list.get(position).get("dayLimit"))) { //不能选择的红包

                holder.redpacket_icon.setVisibility(View.INVISIBLE);
                holder.repad_relativeLayout.setBackgroundResource(R.drawable.redpacked_redgray); //把红包的图换成灰色的
                holder.iv_redusestatus.setBackgroundResource(R.drawable.bukeyong);
                holder.redpacket_money.setTextColor(0xff969696);

            } else { //可用选择的红包

                if (list.get(position).get("status").equals("1")) {  //红包被选中,那么就显示选中的图片
                    holder.redpacket_icon.setVisibility(View.VISIBLE);
                } else {
                    holder.redpacket_icon.setVisibility(View.INVISIBLE);
                }
                holder.repad_relativeLayout.setBackgroundResource(R.drawable.redpacked_red);
                holder.iv_redusestatus.setBackgroundResource(R.drawable.keshiyong);
                holder.redpacket_money.setTextColor(0xffff6e19);
            }
        }
        return convertView;
    }

    private class ViewHolder {
        TextView redpacket_money;  //红包金额


        TextView redpacket_title; //到期时间
        ImageView redpacket_icon;  //打钩图片
        TextView redpacket_timeLimit; //投资期限
        RelativeLayout repad_relativeLayout; //整个背景图
        TextView activity_red; //左侧红包名称
        TextView redpacket_dayLimit; //投资金额期限
        ImageView iv_redusestatus; //红包右下角是否可使用标识
    }

}
