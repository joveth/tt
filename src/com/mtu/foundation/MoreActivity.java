package com.mtu.foundation;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;

import com.mtu.foundation.adapter.MoreItemAdapter;
import com.mtu.foundation.bean.MoreItemBean;
import com.mtu.foundation.util.CommonUtil;

public class MoreActivity extends BaseActivity {
	private GridView gridView;
	private List<MoreItemBean> items;
	private MoreItemAdapter moreItemAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_more);
		initView();
		initViewData();
	}

	private void initView() {
		leftBtn = findViewById(R.id.left_btn);
		leftBtn.setOnClickListener(this);
		leftBtn.setVisibility(View.VISIBLE);
		title = (TextView) findViewById(R.id.top_title);
		title.setText("更多功能");

		gridView = (GridView) findViewById(R.id.gridlay);
	}

	private void initViewData() {
		items = new ArrayList<MoreItemBean>();

		MoreItemBean item = new MoreItemBean();
		item.setImgId(R.drawable.iconfont_kuaijie_red);
		item.setText("快捷捐赠");
		item.setDisplay(true);
		item.setProcessActivity("com.mtu.foundation.FastDonateActivity");
		items.add(item);

		MoreItemBean item2 = new MoreItemBean();
		item2.setImgId(R.drawable.iconfont_zuji_blue);
		item2.setText("我的足迹");
		item2.setDisplay(true);
		item2.setProcessActivity("com.mtu.foundation.HistoryActivity");
		items.add(item2);

		MoreItemBean item1 = new MoreItemBean();
		item1.setDisplay(false);
		items.add(item1);

		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		int width = metrics.widthPixels;
		int itemWidth = (width) / 3;
		// gridView.setColumnWidth(itemWidth);
		moreItemAdapter = new MoreItemAdapter(this, items, itemWidth);
		gridView.setAdapter(moreItemAdapter);
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				String process = items.get(arg2).getProcessActivity();
				if (!CommonUtil.isEmpty(process)) {
					try {
						Class clazz = Class.forName(process);
						if (clazz != null) {
							Intent intent = new Intent(MoreActivity.this, clazz);
							startActivity(intent);
						}
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	@Override
	public void onClick(View arg0) {
		if (arg0 == leftBtn) {
			finish();
		}
	}

}
