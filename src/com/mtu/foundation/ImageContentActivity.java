package com.mtu.foundation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ImageContentActivity extends BaseActivity {
	private ImageView vImg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_content);
		initView();
	}

	private void initView() {
		Intent intent = getIntent();
		if (intent == null) {
			intent = new Intent();
		}
		boolean flag = intent.getBooleanExtra("typeflag", false);
		leftBtn = findViewById(R.id.left_btn);
		leftBtn.setOnClickListener(this);
		leftBtn.setVisibility(View.VISIBLE);
		title = (TextView) findViewById(R.id.top_title);
		vImg = (ImageView) findViewById(R.id.img_content);
		title.setText("一般捐赠流程");
		if (flag) {
			title.setText("在线捐赠流程");
			vImg.setImageResource(R.drawable.online);
		}

	}

	@Override
	public void onClick(View arg0) {
		if (arg0 == leftBtn) {
			finish();
		}
	}

}
