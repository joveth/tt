package com.mtu.foundation;

import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.mtu.foundation.adapter.RecordItemAdapter;
import com.mtu.foundation.db.DBHelper;
import com.mtu.foundation.db.RecordBean;

public class RecordsActivity extends BaseActivity {
	private ListView listView;
	private List<RecordBean> list;
	private RecordItemAdapter adapter;
	private DBHelper dbHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_records);
		initData();
		initView();
	}

	private void initData() {
		dbHelper = DBHelper.getInstance(this);
		list = dbHelper.getRecords();
	}

	private void initView() {
		leftBtn = findViewById(R.id.left_btn);
		leftBtn.setOnClickListener(this);
		leftBtn.setVisibility(View.VISIBLE);
		title = (TextView) findViewById(R.id.top_title);
		title.setText("我的捐赠记录");
		rightBtn = (TextView) findViewById(R.id.right_btn);
		rightBtn.setText("清空");
		rightBtn.setOnClickListener(this);

		listView = (ListView) findViewById(R.id.listview);
		adapter = new RecordItemAdapter(this, list);
		listView.setAdapter(adapter);
		if (list == null || list.size() == 0) {
			showMsgDialogWithCallback("亲，您还没有捐赠记录");
			return;
		}
	}

	protected void showMsgDialogWithCallback(String msg) {
		new AlertDialog.Builder(this)
				.setTitle(null)
				.setPositiveButton("现在捐赠",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(
									DialogInterface dialogInterface, int i) {
								dialogInterface.dismiss();
								switchTo(DonateActivity.class);
								finish();
							}
						})
				.setNegativeButton("稍后再说",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								arg0.dismiss();
								finish();
							}
						}).setCancelable(false).setMessage(msg).show();
	}

	@Override
	public void onClick(View arg0) {
		if (arg0 == leftBtn) {
			finish();
			return;
		}
		if (arg0 == rightBtn) {
			doDel();
			return;
		}
	}

	private void doDel() {
		new AlertDialog.Builder(this).setTitle(null)
				.setNegativeButton("取消", null)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						dialogInterface.dismiss();
						dbHelper.deleteAllRecord();
						list.clear();
						adapter.notifyDataSetChanged();
					}
				}).setCancelable(false).setMessage("确定要全部清空吗？").show();
	}

}
