package com.rd.qnz.product;

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
import android.widget.TextView;

public class ProductMoreListAdapter extends BaseAdapter{
	
	private List<Map<String, String>> list;
	
	private Context context;
	DecimalFormat df = new DecimalFormat("0.00"); 
	
	public ProductMoreListAdapter(Context context,List<Map<String, String>> list){
		this.context = context;
		this.list = list;
	}

	public void notifyDataSetChanged(List<Map<String, String>> list){
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
		if(convertView==null){
			holder = new ViewHolder();
			LayoutInflater mInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(R.layout.homepage_product_more_item, null);
			holder.product_more_people = (TextView) convertView.findViewById(R.id.product_more_people);
			holder.product_more_time = (TextView) convertView.findViewById(R.id.product_more_time);
			holder.product_more_money = (TextView) convertView.findViewById(R.id.product_more_money);
			
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		if(list.size()>0){		
			holder.product_more_people.setText(list.get(position).get(BaseParam.QIAN_PRODUCT_USERSHOWNAME));
			System.out.println("product_more_time = "+list.get(position).get(BaseParam.QIAN_PRODUCT_ADDTIME));
			System.out.println("product_more_time = "+AppTool.getMsgTwoDateDistance(list.get(position).get(BaseParam.QIAN_PRODUCT_ADDTIME)));
			holder.product_more_time.setText(AppTool.getMsgTwoDateDistance(list.get(position).get(BaseParam.QIAN_PRODUCT_ADDTIME)));
			
			holder.product_more_money.setText(df.format(Double.parseDouble(list.get(position).get(BaseParam.QIAN_PRODUCT_ACCOUNT))));
		}
		return convertView;
	}

	private class ViewHolder{
		TextView product_more_people;
		TextView product_more_time;
		TextView product_more_money;
	}
	
}
