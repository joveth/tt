package com.mtu.foundation;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.TextView;

import com.mtu.foundation.adapter.HistoryItemAdapter;
import com.mtu.foundation.bean.HistoryItemBean;
import com.mtu.foundation.db.DBHelper;

public class HistoryActivity extends BaseActivity implements OnScrollListener {
	private ListView listview;
	private HistoryItemAdapter adapter;
	private List<HistoryItemBean> list;
	private int pageNo = 1;
	private int totalPage = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history);
		initData();
		initView();
	}

	private void initData() {
		list = new ArrayList<HistoryItemBean>();
		if (dbHelper == null) {
			dbHelper = DBHelper.getInstance(this);
		}
		int total = dbHelper.getHistorysCnt();
		if (total == 0) {
			showMsgDialogWithCallback("没有更多记录了，下次再来看看吧！");
			return;
		}
		Log.d("total", total + "");
		totalPage = total % 10 == 0 ? total / 10 : (total / 10 + 1);
		list.addAll(dbHelper.getHistorys(pageNo));
	}

	private void initView() {
		leftBtn = findViewById(R.id.left_btn);
		leftBtn.setOnClickListener(this);
		leftBtn.setVisibility(View.VISIBLE);
		title = (TextView) findViewById(R.id.top_title);
		title.setText("我的足迹");
		listview = (ListView) findViewById(R.id.listview);
		adapter = new HistoryItemAdapter(this, list);
		View header = View.inflate(this, R.layout.history_header, null);
		listview.addHeaderView(header);
		listview.setAdapter(adapter);
		listview.setOnScrollListener(this);
		// initViewData();
	}

	@Override
	public void onClick(View arg0) {
		if (arg0 == leftBtn) {
			finish();
		}
	}

	private int firstItem;
	private int lastItem;
	private boolean isLast;

	@Override
	public void onScroll(AbsListView arg0, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		firstItem = firstVisibleItem;
		lastItem = totalItemCount - 1;
		Log.d("firstVisibleItem,visibleItemCount", firstVisibleItem + ","
				+ visibleItemCount);
		if (firstVisibleItem + visibleItemCount == totalItemCount) {
			isLast = true;
		} else {
			isLast = false;
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView arg0, int scrollState) {
		Log.d("isLast", isLast + "");
		Log.d("pageNo", pageNo + "");

		if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
			if (isLast && pageNo < totalPage) {
				pageNo++;
				list.addAll(dbHelper.getHistorys(pageNo));
				adapter.notifyDataSetChanged();
				return;
			}
		}
	}

}
