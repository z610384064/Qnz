package com.rd.qnz.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rd.qnz.R;
import com.rd.qnz.bean.Recording;
import com.rd.qnz.tools.AppTool;

import java.text.DecimalFormat;
import java.util.List;

/**
 * 投资中
 */
public class RecordingListAdapter extends BaseAdapter {

    private List<Recording> list;
    private int type;//1已回款2待回款
    DecimalFormat df = new DecimalFormat("0.00");
    private Context context;

    public RecordingListAdapter(Context context, List<Recording> list) {
        this.context = context;
        this.list = list;
    }

    public RecordingListAdapter(Context context, List<Recording> list, int type) {
        this.context = context;
        this.list = list;
        this.type = type;
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.my_tenderrecord_item_gai, null);

            holder.product_name = (TextView) convertView.findViewById(R.id.product_name);
            holder.product_time = (TextView) convertView.findViewById(R.id.product_time);
            holder.product_money = (TextView) convertView.findViewById(R.id.product_money);
            holder.product_rate = (TextView) convertView.findViewById(R.id.product_rate);
            holder.product_return_time=(TextView)convertView.findViewById(R.id.product_return_time);
            holder.product_status = (ImageView) convertView.findViewById(R.id.product_status);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (list.size() > 0) {
            holder.product_status.setVisibility(View.GONE);
            holder.product_name.setText(list.get(position).getBorrowName());
            holder.product_time.setText(AppTool.getMsgTwoDateDistance_miao(list.get(position).getAddtime()));//投资时间
            holder.product_rate.setText("¥ "+df.format(Math.abs(Double.parseDouble(list.get(position).getInterest()))));//收益

            holder.product_money.setText("¥ "+df.format(Double.parseDouble(list.get(position).getMoney())));  //本金
            holder.product_return_time.setText(AppTool.getMsgTwoDateDistance1(list.get(position).getRepayTime())); //回款日期

        }
        return convertView;
    }

    private class ViewHolder {
        TextView product_name;
        TextView product_time;
        TextView product_money;
        TextView product_rate;
        ImageView product_status;
        TextView product_return_time; //回款时间
    }


}
