package com.mtu.foundation.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mtu.foundation.R;
import com.mtu.foundation.bean.ThankBean;
import com.mtu.foundation.util.CommonUtil;

public class ThanksItemAdapter extends BaseAdapter {
	private List<ThankBean> list;
	private Context context;

	public ThanksItemAdapter(Context context, List<ThankBean> items) {
		this.context = context;
		this.list = items;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int i) {
		return list.get(i);
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	@Override
	public View getView(int i, View view, ViewGroup viewGroup) {

		final Holder hold;
		if (view == null) {
			hold = new Holder();
			view = View.inflate(context, R.layout.thanks_item, null);
			hold.itemName = (TextView) view.findViewById(R.id.item_name);
			hold.itemAmount = (TextView) view.findViewById(R.id.item_money);
			hold.itemProject = (TextView) view.findViewById(R.id.item_project);
			hold.itemDate = (TextView) view.findViewById(R.id.item_date);
			hold.lay = view.findViewById(R.id.item_lay);
			view.setTag(hold);
		} else {
			hold = (Holder) view.getTag();
		}
		if (!CommonUtil.isEmpty(list.get(i).getDate())) {
			hold.itemDate.setText(list.get(i).getDate());
		} else {
			hold.itemDate.setText(null);
		}
		hold.itemName.setText(list.get(i).getUserName());
		hold.itemAmount.setText(list.get(i).getAmount());
		hold.itemProject.setText(list.get(i).getProjectName());
		return view;
	}

	static class Holder {
		TextView itemName;
		TextView itemAmount;
		TextView itemProject;
		TextView itemDate;
		View lay;
	}
}
