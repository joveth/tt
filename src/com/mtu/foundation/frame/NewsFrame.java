package com.mtu.foundation.frame;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpStatus;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.mtu.foundation.R;
import com.mtu.foundation.adapter.NewsItemAdapter;
import com.mtu.foundation.bean.NewsBean;
import com.mtu.foundation.net.HTMLParser;
import com.mtu.foundation.net.ThreadPoolUtils;
import com.mtu.foundation.net.httpjersey.Callback;
import com.mtu.foundation.net.httpjersey.NetworkHandler;
import com.mtu.foundation.net.httpjersey.TransResp;
import com.mtu.foundation.util.CommonUtil;
import com.mtu.foundation.util.Constants;
import com.mtu.foundation.util.FileOperRunnable;
import com.mtu.foundation.util.FileUtil;
import com.mtu.foundation.view.PullDownView;

public class NewsFrame extends Fragment implements
		PullDownView.OnPullDownListener {
	private View view;
	private Context context;
	private NetworkHandler networkHandler;
	private ListView listView;
	private List<NewsBean> list;
	private NewsItemAdapter adapter;
	private PullDownView pullDownView;
	private Handler cacheHandler;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.tab_news, container, false);
		context = view.getContext();
		initView();
		initOther();
		networkHandler = NetworkHandler.getInstance();
		ThreadPoolUtils.execute(new FileOperRunnable(FileUtil
				.getCacheFile(Constants.CACHE_NEWS), true, false, null,
				cacheHandler));
		return view;
	}

	private void initView() {
		pullDownView = (PullDownView) view.findViewById(R.id.news_listview);
		listView = pullDownView.getListView();
		list = new ArrayList<NewsBean>();
		adapter = new NewsItemAdapter(context, list);
		listView.setAdapter(adapter);
		pullDownView.setOnPullDownListener(this);
		pullDownView.enableAutoFetchMore(true, 3);
		pullDownView.setShowFooter();
		pullDownView.setShowHeader();
		pullDownView.notifyDidMore();
	}

	private void initOther() {
		cacheHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case Constants.READ_RESULT_OK:
					try {
						String cacheData = (String) msg.obj;
						if (!CommonUtil.isEmpty(cacheData)) {
							tranceData(cacheData);
						} else {
							getData();
						}
					} finally {
						pullDownView.refreshComplete();
						pullDownView.notifyDidMore();
					}
					break;
				}
			}
		};
	}

	private HTMLParser parser;
	private int PULL_STATE = 1;// 1 下拉，2上拉
	private int page = 0, totalPage = 0;

	private void getData() {
		networkHandler.get(Constants.URI_NEWS + "?page=" + page, null, 30,
				new Callback<TransResp>() {
					@Override
					public void callback(TransResp tr) {
						try {
							if (tr.getRetcode() == HttpStatus.SC_OK) {
								tranceData(tr.getRetjson());
							}
						} finally {
							pullDownView.refreshComplete();
							pullDownView.notifyDidMore();
						}
					}
				});

	}

	private void tranceData(String html) {
		if (parser == null) {
			parser = new HTMLParser(html);
		} else {
			parser.setHTMLStr(html);
		}
		List<NewsBean> tempList = parser.getNews();
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
	}

	@Override
	public void onRefresh() {
		if (!CommonUtil.isNetWorkConnected(context)) {
			pullDownView.refreshComplete();
			return;
		}
		PULL_STATE = 1;
		page = 0;
		getData();
	}

	@Override
	public void onMore() {
		if (!CommonUtil.isNetWorkConnected(context)) {
			pullDownView.notifyDidMore();
			return;
		}
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
