package com.rd.qnz.mine;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import com.rd.qnz.R;
import com.rd.qnz.tools.AppTool;
import com.rd.qnz.tools.BaseParam;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyTenderRecordListAdapter extends BaseAdapter {

    private List<Map<String, String>> list;

    private Context context;
    private boolean isFlag = true;
    private String type = "1";
    DecimalFormat df = new DecimalFormat("0.00");

    public MyTenderRecordListAdapter(Context context, List<Map<String, String>> list, boolean isFlag) {
        this.context = context;
        this.list = list;
        this.isFlag = isFlag;
    }

    public void notifyDataSetChanged(List<Map<String, String>> list, boolean isFlag, String type) {
        this.list = list;
        this.isFlag = isFlag;
        this.type = type;
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

            holder.product_status = (ImageView) convertView.findViewById(R.id.product_status);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (list.size() > 0) {
            if (isFlag) {
//				holder.product_rate.setVisibility(View.VISIBLE);
                holder.product_name.setText(list.get(position).get(BaseParam.QIAN_PRODUCT_BORROWNAME));
                holder.product_time.setText(AppTool.getMsgTwoDateDistance(list.get(position).get(BaseParam.QIAN_PRODUCT_ADDTIME)));

                if (list.get(position).get(BaseParam.QIAN_PRODUCT_BORROWSTATUS).equals("8")) {
                    holder.product_status.setBackgroundResource(R.drawable.tender_icon);
//					holder.product_status.setText("      已还款");
//					holder.product_status.setTextColor(context.getResources().getColor(R.color.black));
                } else {
                    holder.product_status.setBackgroundResource(R.drawable.repay_icon);
//					holder.product_status.setText("      还款中");
//					holder.product_status.setTextColor(context.getResources().getColor(R.color.yollew));
                }

                holder.product_money.setText(df.format(Double.parseDouble(list.get(position).get(BaseParam.QIAN_PRODUCT_ACCOUNT))));
//				holder.product_rate.setTextColor(context.getResources().getColor(R.color.green_true));
                holder.product_rate.setText("+" + df.format(Double.parseDouble(list.get(position).get(BaseParam.QIAN_PRODUCT_INTEREST))));
            } else {
                holder.product_status.setVisibility(View.GONE);
                holder.product_name.setText(list.get(position).get(BaseParam.QIAN_MY_REPAY_BORROWNAME));
                holder.product_time.setText(AppTool.getMsgTwoDateDistance1(list.get(position).get(BaseParam.QIAN_MY_REPAY_REPAYMENTIME)));
                holder.product_money.setText(df.format(Double.parseDouble(list.get(position).get(BaseParam.QIAN_MY_REPAY_REPAYMENTACCOUNT))));
//				holder.product_money.setTextColor(context.getResources().getColor(R.color.green_true));
                holder.product_rate.setText(list.get(position).get(BaseParam.QIAN_MY_REPAY_BACKPLACE));
            }

        }
        return convertView;
    }

    private class ViewHolder {
        TextView product_name;
        TextView product_time;
        TextView product_money;
        TextView product_rate;

        ImageView product_status;
    }

}
