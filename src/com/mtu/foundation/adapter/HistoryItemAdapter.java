package com.mtu.foundation.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mtu.foundation.R;
import com.mtu.foundation.bean.HistoryItemBean;
import com.mtu.foundation.util.CommonUtil;

public class HistoryItemAdapter extends BaseAdapter {
	private List<HistoryItemBean> list;
	private Context context;

	public HistoryItemAdapter(Context context, List<HistoryItemBean> items) {
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
			view = View.inflate(context, R.layout.history_item, null);
			hold.itemDay = (TextView) view.findViewById(R.id.item_day);
			hold.itemDate = (TextView) view.findViewById(R.id.item_date);
			hold.itemContent = (TextView) view.findViewById(R.id.item_content);
			hold.itemImg = (ImageView) view.findViewById(R.id.item_img);
			hold.lay = view.findViewById(R.id.item_lay);
			view.setTag(hold);
		} else {
			hold = (Holder) view.getTag();
		}
		hold.itemDay.setText(list.get(i).getDay());
		hold.itemDate.setText(list.get(i).getDate());
		hold.itemContent.setText(list.get(i).getContent());
		String imgid = list.get(i).getImgId();
		if (!CommonUtil.isEmpty(imgid)) {
			try {
				int id = Integer.parseInt(imgid);
				hold.itemImg.setImageResource(id);
			} catch (Exception e) {
			}
		}
		return view;
	}

	static class Holder {
		TextView itemDay, itemDate, itemContent;
		ImageView itemImg;
		View lay;
	}

}
