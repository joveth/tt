package com.mtu.foundation.frame;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpStatus;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.mtu.foundation.R;
import com.mtu.foundation.adapter.ThanksItemAdapter;
import com.mtu.foundation.bean.ThankBean;
import com.mtu.foundation.net.HTMLParser;
import com.mtu.foundation.net.httpjersey.Callback;
import com.mtu.foundation.net.httpjersey.NetworkHandler;
import com.mtu.foundation.net.httpjersey.TransResp;
import com.mtu.foundation.util.CommonUtil;
import com.mtu.foundation.util.Constants;
import com.mtu.foundation.view.PullDownView;

public class OthersFrame extends Fragment implements
		PullDownView.OnPullDownListener {
	private View view;
	private Context context;
	private NetworkHandler networkHandler;
	private ListView listView;
	private List<ThankBean> list;
	private ThanksItemAdapter adapter;
	private PullDownView pullDownView;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.tab_others, container, false);
		context = view.getContext();
		initView();
		networkHandler = NetworkHandler.getInstance();
		getData();
		return view;
	}

	private void initView() {
		pullDownView = (PullDownView) view.findViewById(R.id.thanks_listview);
		listView = pullDownView.getListView();
		list = new ArrayList<ThankBean>();
		adapter = new ThanksItemAdapter(context, list);
		listView.setAdapter(adapter);
		pullDownView.setOnPullDownListener(this);
		pullDownView.enableAutoFetchMore(true, 3);
		pullDownView.setShowFooter();
		pullDownView.setShowHeader();
		pullDownView.notifyDidMore();
	}

	private HTMLParser parser;
	private int PULL_STATE = 1;// 1 下拉，2上拉
	private int page = 0, totalPage = 0;

	private void getData() {
		networkHandler.get(Constants.URI_THANKS + "?page=" + page, null, 30,
				new Callback<TransResp>() {
					@Override
					public void callback(TransResp tr) {
						if (tr.getRetcode() == HttpStatus.SC_OK) {
							if (parser == null) {
								parser = new HTMLParser(tr.getRetjson());
							} else {
								parser.setHTMLStr(tr.getRetjson());
							}
							List<ThankBean> tempList = parser.getThanks();
							if (PULL_STATE == 1) {
								list.clear();
								String last = parser.getLastPager();
								Log.d("last_pager", last + "");
								if (!CommonUtil.isEmpty(last)) {
									try {
										totalPage = Integer.parseInt(last);
										Log.d("totalPage", totalPage + "");
									} catch (Exception e) {

									}
								}
							}
							list.addAll(tempList);
							if (totalPage == 0) {
								String last = parser.getLastPager();
								if (!CommonUtil.isEmpty(last)) {
									try {
										totalPage = Integer.parseInt(last);
									} catch (Exception e) {
									}
								}
							}
							adapter.notifyDataSetChanged();
							pullDownView.refreshComplete();
							pullDownView.notifyDidMore();
						}
					}
				});

	}

	@Override
	public void onRefresh() {
		PULL_STATE = 1;
		page = 0;
		getData();
	}

	@Override
	public void onMore() {
		PULL_STATE = 2;
		Log.d("total", totalPage + "");
		if (page > totalPage) {
			pullDownView.notifyDidMore();
			return;
		}
		page++;
		getData();
	}
}
