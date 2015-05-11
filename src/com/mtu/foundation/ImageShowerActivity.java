package com.mtu.foundation;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.mtu.foundation.net.HttpGetThread;
import com.mtu.foundation.net.ThreadPoolUtils;
import com.mtu.foundation.util.CommonUtil;
import com.mtu.foundation.util.Constants;
import com.mtu.foundation.util.FileUtiles;
import com.mtu.foundation.view.CustomProgressDialog;

public class ImageShowerActivity extends BaseActivity {
	private ImageView vImg;
	private String imgSrc;
	private Handler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_imageshower);
		Intent intent = getIntent();
		if (intent == null) {
			finish();
			Toast.makeText(this, "加载失败了", Toast.LENGTH_SHORT).show();
			return;
		}
		vImg = (ImageView) findViewById(R.id.image_shower);
		int imgOnly = intent.getIntExtra("imgResid", -1);
		if (imgOnly != -1) {
			vImg.setImageResource(imgOnly);
			return;
		}
		imgSrc = intent.getStringExtra("image");
		if (CommonUtil.isEmpty(imgSrc)) {
			finish();
			Toast.makeText(this, "加载失败了", Toast.LENGTH_SHORT).show();
			return;
		}
		if (progressDialog == null) {
			progressDialog = new CustomProgressDialog(this, "正在加载...", true);
			progressDialog.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface arg0) {
					finish();
					arg0.dismiss();
				}
			});
		}
		initOther();
		
		if (FileUtiles.isHasImage(imgSrc)) {
			vImg.setImageBitmap(BitmapFactory.decodeFile(FileUtiles
					.getImagePath(imgSrc)));
			progressDialog.dismiss();
		} else {
			getImage();
		}
	}

	private void getImage() {
		if (!CommonUtil.isNetWorkConnected(this)) {
			progressDialog.dismiss();
			finish();
			Toast.makeText(this, "加载失败了", Toast.LENGTH_SHORT).show();
			return;
		}
		ThreadPoolUtils.execute(new HttpGetThread(imgSrc, handler));
	}

	private void initOther() {
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				if (msg.what == Constants.RESULT_OK) {
					vImg.setImageBitmap(BitmapFactory.decodeFile(FileUtiles
							.getImagePath(imgSrc)));
					progressDialog.dismiss();
				} else {
					progressDialog.dismiss();
					finish();
					Toast.makeText(ImageShowerActivity.this, "加载失败了",
							Toast.LENGTH_SHORT).show();
				}
			}
		};
	}

	@Override
	public void onClick(View arg0) {
		finish();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		finish();
		return true;
	}
}
