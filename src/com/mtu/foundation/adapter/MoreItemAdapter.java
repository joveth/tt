package com.mtu.foundation.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mtu.foundation.R;
import com.mtu.foundation.bean.MoreItemBean;
import com.mtu.foundation.util.CommonUtil;

/**
 * Created by jov on 2015/1/29.
 */
public class MoreItemAdapter extends BaseAdapter {
	private List<MoreItemBean> list;
	private int itemWidth;
	private Context context;
	private int H = 300;

	public MoreItemAdapter(Context context, List<MoreItemBean> items,
			int itemWidth) {
		this.context = context;
		this.list = items;
		this.itemWidth = itemWidth;
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
		return 0;
	}

	@Override
	public View getView(int i, View view, ViewGroup viewGroup) {
		final Holder hold;
		if (view == null) {
			hold = new Holder();
			view = View.inflate(context, R.layout.more_item, null);
			hold.iconImg = (ImageView) view.findViewById(R.id.item_img);
			hold.itemName = (TextView) view.findViewById(R.id.item_txt);
			hold.lay = view.findViewById(R.id.item_lay);

			view.setTag(hold);
		} else {
			hold = (Holder) view.getTag();
		}
		AbsListView.LayoutParams param = new AbsListView.LayoutParams(
				itemWidth, LayoutParams.WRAP_CONTENT);
		view.setLayoutParams(param);
		if (list.get(i).isDisplay()) {
			hold.iconImg.setImageResource(list.get(i).getImgId());
			hold.itemName.setText(list.get(i).getText());
			
		}
		return view;
	}

	static class Holder {
		ImageView iconImg;
		TextView itemName;
		View lay;
	}
}
