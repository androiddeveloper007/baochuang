package com.szbc.front.addcar.sortedlistview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.szbc.android.R;
import com.szbc.model.CarConfiguration;

import java.util.List;

/*车配置适配器*/
public class CarConfAdapter extends BaseAdapter {
	private List<CarConfiguration> list;
	private Context mContext;
	public CarConfAdapter(Context mContext, List<CarConfiguration> list){
		this.mContext = mContext;
		this.list = list;
	}
	@Override
	public int getCount() {
		return this.list.size();
	}
	@Override
	public Object getItem(int position) {
		return list.get(position);
	}
	@Override
	public long getItemId(int position) {
		return position;
	}
	public void update(List<CarConfiguration> list){
		this.list = list;
		notifyDataSetChanged();
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		final CarConfiguration mContent = list.get(position);
		if (convertView== null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.sorted_car_conf_item, null);
			viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.title_conf);
			convertView.setTag(viewHolder);
		}else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.tvTitle.setText(mContent.getYearstyleName());
		return convertView;
	}
	final static class ViewHolder{
		TextView tvTitle;
	}
}