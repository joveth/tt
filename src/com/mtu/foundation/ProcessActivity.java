package com.mtu.foundation;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ProcessActivity extends BaseActivity {
	private View vNormal, vByWeb, vByPhone;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_process);
		initView();
	}

	private void initView() {
		leftBtn = findViewById(R.id.left_btn);
		leftBtn.setOnClickListener(this);
		leftBtn.setVisibility(View.VISIBLE);
		title = (TextView) findViewById(R.id.top_title);
		title.setText("捐赠流程");
		vNormal = findViewById(R.id.donate_by_normal);
		vNormal.setOnClickListener(this);
		vByWeb = findViewById(R.id.donate_by_web);
		vByWeb.setOnClickListener(this);
		vByPhone = findViewById(R.id.donate_by_phone);
		vByPhone.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		if (arg0 == leftBtn) {
			finish();
			return;
		}
		if (arg0 == vNormal) {
			switchTo(ImageContentActivity.class);
			return;
		}
		if (arg0 == vByWeb) {
			Bundle bundle = new Bundle();
			bundle.putBoolean("typeflag", true);
			switchTo(ImageContentActivity.class, bundle);
			return;
		}
		if (arg0 == vByPhone) {
			new AlertDialog.Builder(this)
					.setMessage("请返回到主界面,切换到捐赠选项卡,填写好信息后,选择下一步,选择付款方式,进行付款!")
					.setTitle(null).setCancelable(false)
					.setNegativeButton("确定", null).show();
			return;
		}
	}
}
