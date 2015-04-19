package com.mtu.foundation;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mtu.foundation.adapter.FramePagerAdaper;
import com.mtu.foundation.frame.DonateFrame;
import com.mtu.foundation.frame.NewsFrame;
import com.mtu.foundation.frame.OthersFrame;
import com.mtu.foundation.util.ExitAppUtil;
import com.mtu.foundation.view.MainViewPager;

public class MainActivity extends FragmentActivity {
	private MainViewPager mainViewPager;
	private List<Fragment> pagerFragments;
	private View vNews, vDonate, vOthers;
	protected TextView title;
	private List<ImageView> iconImgs;
	private List<TextView> iconTxts;
	private List<String> titles;
	private TextView newsTxt, donateTxt, othersTxt;
	private ImageView newsImg, donateImg, othersImg;
	private int currentItem = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ExitAppUtil.add(this);
		setContentView(R.layout.activity_main);
		initView();
	}

	private void initView() {
		title = (TextView) findViewById(R.id.top_title);
		title.setText("基金会");
		mainViewPager = (MainViewPager) findViewById(R.id.tabpager);
		mainViewPager.setCanScroll(false);
		vNews = findViewById(R.id.news_lay);
		vDonate = findViewById(R.id.donate_lay);
		vOthers = findViewById(R.id.others_lay);
		MenuOnClickListener homeMenuOnClickListener = new MenuOnClickListener(0);
		MenuOnClickListener msgMenuOnClickListener = new MenuOnClickListener(1);
		MenuOnClickListener payMenuOnClickListener = new MenuOnClickListener(2);
		vNews.setOnClickListener(homeMenuOnClickListener);
		vDonate.setOnClickListener(msgMenuOnClickListener);
		vOthers.setOnClickListener(payMenuOnClickListener);
		titles = new ArrayList<String>();
		titles.add("新闻公告");
		titles.add("爱心捐赠");
		titles.add("捐赠名单");
		newsImg = (ImageView) findViewById(R.id.news_img);
		donateImg = (ImageView) findViewById(R.id.donate_img);
		donateImg.setSelected(true);
		othersImg = (ImageView) findViewById(R.id.others_img);
		iconImgs = new ArrayList<ImageView>();
		iconImgs.add(newsImg);
		iconImgs.add(donateImg);
		iconImgs.add(othersImg);

		newsTxt = (TextView) findViewById(R.id.news_txt);

		donateTxt = (TextView) findViewById(R.id.donate_txt);
		donateTxt.setSelected(true);
		othersTxt = (TextView) findViewById(R.id.others_txt);
		iconTxts = new ArrayList<TextView>();
		iconTxts.add(newsTxt);
		iconTxts.add(donateTxt);
		iconTxts.add(othersTxt);

		initOthers();
	}

	private void initOthers() {
		pagerFragments = new ArrayList<Fragment>();
		NewsFrame newsFrame = new NewsFrame();
		DonateFrame donateFrame = new DonateFrame();
		OthersFrame paymentFrame = new OthersFrame();
		pagerFragments.add(newsFrame);
		pagerFragments.add(donateFrame);
		pagerFragments.add(paymentFrame);
		FramePagerAdaper adapter = new FramePagerAdaper(
				this.getSupportFragmentManager(), mainViewPager, pagerFragments);
		adapter.setOnExtraPageChangeListener(new FramePagerAdaper.OnExtraPageChangeListener() {
			public void onExtraPageSelected(int i) {
				switchItem(i);
				currentItem = i;
			}
		});
		mainViewPager.setCurrentItem(1);
	}

	private void switchItem(int item) {
		title.setText(titles.get(item));
		for (int i = 0; i < iconImgs.size(); i++) {
			if (i == item) {
				iconImgs.get(i).setSelected(true);
				iconTxts.get(i).setSelected(true);
			} else {
				iconImgs.get(i).setSelected(false);
				iconTxts.get(i).setSelected(false);
			}
		}
	}

	public class MenuOnClickListener implements View.OnClickListener {
		private int index = 0;

		public MenuOnClickListener(int i) {
			index = i;
		}

		public void onClick(View v) {
			mainViewPager.setCurrentItem(index, false);
		}
	}

	private long exitTime = 0;

	public void backTo() {
		if ((System.currentTimeMillis() - exitTime) > 2000) {
			Toast.makeText(getApplicationContext(), "再按一次退出程序",
					Toast.LENGTH_SHORT).show();
			exitTime = System.currentTimeMillis();
		} else {
			ExitAppUtil.exit();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			backTo();
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_MENU
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if (currentItem != 1) {
				mainViewPager.setCurrentItem(1, false);
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	protected void hiddenInput() {
		View view = getWindow().peekDecorView();
		if (view != null) {
			InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			View v = getCurrentFocus();
			if (isShouldHideInput(v, ev)) {
				hiddenInput();
			}
			return super.dispatchTouchEvent(ev);
		}
		if (getWindow().superDispatchTouchEvent(ev)) {
			return true;
		}
		return onTouchEvent(ev);
	}

	public boolean isShouldHideInput(View v, MotionEvent event) {
		if (v != null && (v instanceof EditText)) {
			int[] leftTop = { 0, 0 };
			// 获取输入框当前的location位置
			v.getLocationInWindow(leftTop);
			int left = leftTop[0];
			int top = leftTop[1];
			int bottom = top + v.getHeight();
			int right = left + v.getWidth();
			if (event.getX() > left && event.getX() < right
					&& event.getY() > top && event.getY() < bottom) {
				// 点击的是输入框区域，保留点击EditText的事件
				return false;
			} else {
				return true;
			}
		}
		return false;
	}
}
