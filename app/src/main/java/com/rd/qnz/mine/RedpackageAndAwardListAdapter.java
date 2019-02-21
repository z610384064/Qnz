package com.rd.qnz.mine;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rd.qnz.R;
import com.rd.qnz.entity.AwardItem;
import com.rd.qnz.tools.AppTool;

import java.util.List;

public class RedpackageAndAwardListAdapter extends BaseAdapter {

    private List<AwardItem> list;
    private int type;//1红包2奖励
    private Context context;

    public RedpackageAndAwardListAdapter(Context context, List<AwardItem> list) {
        this.context = context;
        this.list = list;
    }

    public RedpackageAndAwardListAdapter(Context context, List<AwardItem> list, int type) {
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
            convertView = mInflater.inflate(R.layout.award_item, null);
            holder.award_name = (TextView) convertView.findViewById(R.id.award_name);
            holder.award_time = (TextView) convertView.findViewById(R.id.award_time);
            holder.award_money = (TextView) convertView.findViewById(R.id.award_money);
            holder.award_addr = (TextView) convertView.findViewById(R.id.award_addr);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (list.size() > 0) {
            final AwardItem award = list.get(position);
            holder.award_name.setText(award.getTitle());
            holder.award_time.setText(AppTool.getMsgTwoDateDistance(award.getAddtime()));
            holder.award_money.setText(award.getMoney());
            holder.award_addr.setText(award.getAddr());
        }
        return convertView;
    }

    private class ViewHolder {
        TextView award_name;
        TextView award_time;
        TextView award_money;
        TextView award_addr;
    }


}
