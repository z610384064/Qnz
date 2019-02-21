package com.rd.qnz.mine;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rd.qnz.R;
import com.rd.qnz.entity.AwardItem;
import com.rd.qnz.entity.RechargeItem;
import com.rd.qnz.tools.AppTool;

import java.text.DecimalFormat;
import java.util.List;

public class RechargeListAdapter extends BaseAdapter {

    private List<RechargeItem> list;
    private Context context;

    public RechargeListAdapter(Context context, List<RechargeItem> list) {
        this.context = context;
        this.list = list;
    }

    public void notifyDataSetChanged(List<RechargeItem> list) {
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
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.list_item_cash_record, null);
            holder.name = (TextView) convertView.findViewById(R.id.cash_record_item_bank_name);
            holder.time = (TextView) convertView.findViewById(R.id.cash_record_item_time);
            holder.thisMoney = (TextView) convertView.findViewById(R.id.cash_record_item_money);
            holder.status = (TextView) convertView.findViewById(R.id.cash_record_item_status);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (list.size() > 0) {
            String bankName = "";
            if (list.get(position).getTradeType().equals("105")) {
                bankName = "一毛钱银行卡校验";
            } else if (list.get(position).getTradeType().equals("115")) {
                bankName = "app充值";
            } else if (list.get(position).getTradeType().equals("108")) {
                bankName = "pc端充值";
            } else if (list.get(position).getTradeType().equals("119")) {
                bankName = "线下充值";
            }
            //  String bankName = list.get(position).getBankName() + "（尾号" + list.get(position).getCardNo() + "）";
            holder.name.setText(bankName);
            String status = list.get(position).getStatus();
            if (TextUtils.equals(status, "200")) {
                holder.status.setText("充值成功");
                holder.status.setTextColor(context.getResources().getColor(R.color.account_balance2));
            } else {
                holder.status.setText("订单未完成");
                holder.status.setTextColor(context.getResources().getColor(R.color.app_color));
            }

            double money = 0;
            String itemMoney = list.get(position).getTradeMoney();
            if (!TextUtils.isEmpty(itemMoney)) {
                money = Double.parseDouble(itemMoney);
            }
            DecimalFormat df = new DecimalFormat("0.00");
            holder.thisMoney.setText(df.format(money));
            holder.time.setText(list.get(position).getAddTime());
//            holder.time.setText(AppTool.getMsgTwoDateDistance(list.get(position).getAddTime()));
        }
        return convertView;
    }

    private class ViewHolder {
        TextView name, time, thisMoney, status;
    }

}
