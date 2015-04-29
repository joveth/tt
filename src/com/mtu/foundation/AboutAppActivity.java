package com.mtu.foundation;

import java.io.File;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.mtu.foundation.util.FileUtil;
import com.mtu.foundation.view.CustomProgressDialog;

public class AboutAppActivity extends BaseActivity {
	private View vClean, vCheck;
	private TextView vVersion;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about_app);
		initView();
		initViewData();
	}

	private void initView() {
		leftBtn = findViewById(R.id.left_btn);
		leftBtn.setOnClickListener(this);
		leftBtn.setVisibility(View.VISIBLE);
		title = (TextView) findViewById(R.id.top_title);
		title.setText("关于APP");
		vClean = findViewById(R.id.clean_cache_lay);
		vClean.setOnClickListener(this);
		vCheck = findViewById(R.id.check_update);
		vCheck.setOnClickListener(this);
		vVersion = (TextView) findViewById(R.id.version);
	}

	private void initViewData() {
		try {
			PackageManager manager = this.getPackageManager();
			PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
			String version = info.versionName;
			vVersion.setText("ver " + version);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View arg0) {
		if (arg0 == leftBtn) {
			finish();
		}
		if (arg0 == vClean) {
			new AlertDialog.Builder(this)
					.setTitle(null)
					.setNegativeButton("取消", null)
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(
										DialogInterface dialogInterface, int i) {
									dialogInterface.dismiss();
									clean();
								}
							}).setCancelable(false).setMessage("确定要清除缓存吗？")
					.show();

		}
		if (arg0 == vCheck) {
			
		}
	}

	private void clean() {
		if (progressDialog == null) {
			progressDialog = new CustomProgressDialog(this, "正在清理...", false);
		}
		progressDialog.setMsg("正在清理...");
		progressDialog.show();
		File imgFile = FileUtil.getImgRoot();
		if (imgFile != null && imgFile.exists()) {
			for (File temp : imgFile.listFiles()) {
				temp.delete();
			}
		}
		File cacheFile = FileUtil.getCacheRoot();
		if (cacheFile != null && cacheFile.exists()) {
			for (File temp : cacheFile.listFiles()) {
				temp.delete();
			}
		}
		progressDialog.dismiss();
		vClean.setSelected(true);
		vClean.setEnabled(false);
		Toast.makeText(this, "清理完成!", Toast.LENGTH_SHORT).show();
	}
}
