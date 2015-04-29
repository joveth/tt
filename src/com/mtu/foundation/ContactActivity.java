package com.mtu.foundation;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ContactActivity extends BaseActivity {
	private View vMsg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact);
		initView();
	}

	private void initView() {
		leftBtn = findViewById(R.id.left_btn);
		leftBtn.setOnClickListener(this);
		leftBtn.setVisibility(View.VISIBLE);
		title = (TextView) findViewById(R.id.top_title);
		title.setText("联系我们");
		vMsg = findViewById(R.id.level_msg);
		vMsg.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		if (arg0 == leftBtn) {
			finish();
		}
		if (arg0 == vMsg) {
			switchTo(MessageActivity.class);
		}
	}

}
