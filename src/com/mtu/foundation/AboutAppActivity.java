package com.mtu.foundation;

import java.io.File;

import org.apache.http.HttpStatus;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.mtu.foundation.bean.UpdateBean;
import com.mtu.foundation.db.DBHelper;
import com.mtu.foundation.net.HTMLParser;
import com.mtu.foundation.net.UpdateManagerService;
import com.mtu.foundation.net.httpjersey.Callback;
import com.mtu.foundation.net.httpjersey.NetworkHandler;
import com.mtu.foundation.net.httpjersey.TransResp;
import com.mtu.foundation.util.CommonUtil;
import com.mtu.foundation.util.Constants;
import com.mtu.foundation.util.FileUtil;
import com.mtu.foundation.view.CustomProgressDialog;

public class AboutAppActivity extends BaseActivity {
	private View vClean, vCheck;
	private TextView vVersion, vCheckVer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about_app);
		initView();
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
		getVersion();
		vVersion.setText("ver " + version);
		vCheckVer = (TextView) findViewById(R.id.check_up_ver);
		dbHelper = DBHelper.getInstance(this);
		String remoteVersion = dbHelper.getVersion();
		if (!CommonUtil.isEmpty(remoteVersion) && !CommonUtil.isEmpty(version)) {
			if (version.compareToIgnoreCase(remoteVersion) < 0) {
				vCheckVer.setText("新版本 " + remoteVersion);
				Drawable drawable = getResources().getDrawable(
						R.drawable.iconfont_hint);
				drawable.setBounds(0, 0, drawable.getMinimumWidth(),
						drawable.getMinimumHeight());
				vCheckVer.setCompoundDrawables(drawable, null, null, null);
			}
		}
	}

	private String version;

	private void getVersion() {
		PackageManager manager = this.getPackageManager();
		PackageInfo info = null;
		try {
			info = manager.getPackageInfo(this.getPackageName(), 0);
			if (info != null) {
				version = info.versionName;
			}
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
			Log.d("check_version", "版本号获取失败");
		}
		Log.d("version", version + "");
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
			checkVersion();
			return;
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

	private boolean cancelFlag = false;
	private HTMLParser parser;

	private void checkVersion() {
		if (!CommonUtil.isNetWorkConnected(this)) {
			showSimpleMessageDialog("网络无法连接");
			return;
		}
		cancelFlag = false;
		if (progressDialog == null) {
			progressDialog = new CustomProgressDialog(this, "正在检查...", false);
			progressDialog
					.setOnCancelListener(new DialogInterface.OnCancelListener() {
						@Override
						public void onCancel(DialogInterface dialogInterface) {
							dialogInterface.dismiss();
							cancelFlag = true;
						}
					});
		}
		progressDialog.setMsg("正在检查...");
		progressDialog.show();
		if (networkHandler == null) {
			networkHandler = NetworkHandler.getInstance();
		}
		getVersion();
		networkHandler.post(Constants.URI_UPDATE, null, 30,
				new Callback<TransResp>() {
					@Override
					public void callback(TransResp transResp) {
						if (cancelFlag) {
							return;
						}
						if (transResp.getRetcode() == HttpStatus.SC_OK) {
							String ret = transResp.getRetjson();
							Log.d("ret", ret);
							if (CommonUtil.isEmpty(ret)) {
								progressDialog.dismiss();
								showSimpleMessageDialog("请求失败了");
								return;
							}
							try {
								if (parser == null) {
									parser = new HTMLParser(ret);
								} else {
									parser.setHTMLStr(ret);
								}
								UpdateBean bean = parser.getVersion();
								if (bean != null
										&& !CommonUtil.isEmpty(bean
												.getVersion())
										&& !CommonUtil.isEmpty(bean
												.getDownloadurl())) {
									DBHelper dao = DBHelper
											.getInstance(AboutAppActivity.this);
									dao.saveVersion(bean.getVersion());
									if (String.valueOf(version)
											.compareToIgnoreCase(
													bean.getVersion()) < 0) {
										UpdateManagerService service = new UpdateManagerService(
												AboutAppActivity.this);
										progressDialog.dismiss();
										vCheckVer.setText("新版本"
												+ bean.getVersion());
										Drawable drawable = getResources()
												.getDrawable(
														R.drawable.iconfont_hint);
										drawable.setBounds(0, 0,
												drawable.getMinimumWidth(),
												drawable.getMinimumHeight());
										Drawable subdrawable = getResources()
												.getDrawable(
														R.drawable.iconfont_submenu);
										subdrawable.setBounds(0, 0,
												subdrawable.getMinimumWidth(),
												subdrawable.getMinimumHeight());
										vCheckVer.setCompoundDrawables(
												drawable, null, null, null);
										service.doUpdate(bean.getDownloadurl(),
												bean.getRemark(),
												bean.getVersion());

									} else {
										progressDialog.dismiss();
										vCheckVer.setText("已是最新版");
										vCheckVer.setCompoundDrawables(null,
												null, null, null);
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
								progressDialog.dismiss();
								showSimpleMessageDialog("请求失败了");
								return;
							}
						} else {
							progressDialog.dismiss();
							showSimpleMessageDialog("请求失败了，返回"
									+ transResp.getRetcode());
						}
					}
				});
	}
}
