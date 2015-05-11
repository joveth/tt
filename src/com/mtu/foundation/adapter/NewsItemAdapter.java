package com.mtu.foundation.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mtu.foundation.R;
import com.mtu.foundation.WebViewActivity;
import com.mtu.foundation.bean.NewsBean;
import com.mtu.foundation.util.CommonUtil;
import com.mtu.foundation.util.FileUtiles;

public class NewsItemAdapter extends BaseAdapter {
	private List<NewsBean> list;
	private Context context;

	public NewsItemAdapter(Context context, List<NewsBean> items) {
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
		final NewsBean bean = list.get(i);
		if (view == null) {
			hold = new Holder();
			view = View.inflate(context, R.layout.news_item, null);
			hold.itemTitle = (TextView) view.findViewById(R.id.item_title);
			hold.itemDate = (TextView) view.findViewById(R.id.item_date);
			hold.itemImg = (ImageView) view.findViewById(R.id.item_img);
			hold.lay = view.findViewById(R.id.item_lay);
			view.setTag(hold);
		} else {
			hold = (Holder) view.getTag();
		}
		if (!CommonUtil.isEmpty(bean.getDate())) {
			hold.itemDate.setText(bean.getDate());
		} else {
			hold.itemDate.setText(null);
		}
		if (CommonUtil.isEmpty(bean.getImg())) {
			hold.itemImg.setVisibility(View.GONE);
		} else {
			hold.itemImg.setVisibility(View.VISIBLE);
			if (FileUtiles.isHasImage(bean.getImg())) {
				hold.itemImg.setImageBitmap(BitmapFactory.decodeFile(FileUtiles
						.getImagePath(bean.getImg())));
			}
		}
		hold.itemTitle.setText(bean.getTitle());
		hold.lay.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(context, WebViewActivity.class);
				intent.putExtra("newsurl", bean.getUrl());
				intent.putExtra("title", bean.getTitle());
				context.startActivity(intent);
			}
		});
		return view;
	}

	static class Holder {
		TextView itemTitle, itemDate;;
		ImageView itemImg;
		View lay;
	}

}
