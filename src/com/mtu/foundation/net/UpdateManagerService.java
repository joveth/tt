package com.mtu.foundation.net;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpStatus;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.widget.RemoteViews;

import com.mtu.foundation.MainActivity;
import com.mtu.foundation.R;
import com.mtu.foundation.bean.UpdateBean;
import com.mtu.foundation.db.BeanPropEnum;
import com.mtu.foundation.db.DBHelper;
import com.mtu.foundation.net.httpjersey.Callback;
import com.mtu.foundation.net.httpjersey.NetworkHandler;
import com.mtu.foundation.net.httpjersey.TransResp;
import com.mtu.foundation.util.CommonUtil;
import com.mtu.foundation.util.Constants;
import com.mtu.foundation.util.FileUtil;

/**
 * Created by jov on 2015/1/30.
 */
public class UpdateManagerService {
	private Context context;
	private int versioncode;
	private String downUrl, versionName, newVersionName;
	private static final int DOWN_OK = 21;
	private static final int DOWN_ERROR = 20;
	private File updateFile;
	private Intent updateIntent;
	private NotificationManager notificationManager;
	private Notification notification;
	private PendingIntent pendingIntent;
	private int notification_id = 0;
	private static final int TIMEOUT = 30 * 1000;// 超时

	public UpdateManagerService(Context context) {
		this.context = context;
	}

	public void checkVersion() {
		if (!CommonUtil.isNetWorkConnected(context)) {
			return;
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				getAppVersionNameOr();
			}
		}).start();
	}

	public String getVersionName() {
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			versionName = pi.versionName;
			versioncode = pi.versionCode;
			Log.d("versionName", versionName);
			Log.d("versioncode", versioncode + "");
			if (versionName == null || versionName.length() <= 0) {
				return "";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return versionName;
	}

	private HTMLParser parser;

	public void getAppVersionNameOr() {
		getVersionName();
		if (CommonUtil.isEmpty(versionName)) {
			return;
		}
		NetworkHandler networkHandler = NetworkHandler.getInstance();
		networkHandler.post(Constants.URI_UPDATE, null, 30,
				new Callback<TransResp>() {
					@Override
					public void callback(TransResp transResp) {
						if (transResp.getRetcode() == HttpStatus.SC_OK) {
							String ret = transResp.getRetjson();
							Log.d("ret", ret + "");
							if (!CommonUtil.isEmpty(ret)) {
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
										if (versionName
												.compareToIgnoreCase(bean
														.getVersion()) < 0) {
											DBHelper dao = DBHelper
													.getInstance(context);
											dao.saveVersion(bean.getVersion());
											Log.d("newVersion",
													bean.getVersion() + "");
											doUpdate(bean.getDownloadurl(),
													bean.getRemark(),
													bean.getVersion());
										}
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						} else {
							Log.d("更新服务", "无法更新");
						}
					}
				});
	}

	public void doUpdate(String url, String updateContent, String newVersionName) {
		downUrl = url;
		this.newVersionName = newVersionName;
		if (CommonUtil.isEmpty(downUrl)) {
			return;
		}
		showDialog(updateContent);
	}

	private void downLoadThread() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				updateFile = FileUtil.getUpdateFile(newVersionName);
				long downloadSize = downloadUpdateFile(downUrl, updateFile);
				Message message = new Message();
				if (downloadSize > 0) {
					// 下载成功
					message.what = DOWN_OK;
					handler.sendMessage(message);
				} else {
					message.what = DOWN_ERROR;
					handler.sendMessage(message);
				}
			}
		}).start();
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DOWN_OK:
				// 下载完成，点击安装
				if (updateFile != null) {
					Uri uri = Uri.fromFile(updateFile);
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.setDataAndType(uri,
							"application/vnd.android.package-archive");
					pendingIntent = PendingIntent.getActivity(context, 0,
							intent, 0);
					notification.tickerText = context
							.getString(R.string.app_name) + "下载完成，点击安装";
					notification.setLatestEventInfo(context,
							context.getString(R.string.app_name), "下载完成，点击安装",
							pendingIntent);
					notificationManager.notify(notification_id, notification);
					context.startActivity(intent);
					// 如果不加上这句的话在apk安装完成之后点击单开会崩溃
					android.os.Process.killProcess(android.os.Process.myPid());
					break;
				}
			case DOWN_ERROR:
				notification.setLatestEventInfo(context,
						context.getString(R.string.app_name), "下载失败",
						pendingIntent);
				break;
			default:
				break;
			}
		}
	};

	private void showDialog(String msg) {
		// 发现新版本，提示用户更新
		AlertDialog.Builder alert = new AlertDialog.Builder(context);
		if (CommonUtil.isEmpty(msg)) {
			msg = "新版本更新";
		}
		alert.setTitle("发现新的版本")
				.setMessage(Html.fromHtml(msg))
				.setPositiveButton("现在更新",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								createNotification();
								downLoadThread();
							}
						})
				.setNegativeButton("下次再说",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						}).setCancelable(false);
		alert.create().show();
	}

	private RemoteViews contentView;

	private void createNotification() {
		notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		notification = new Notification();
		notification.icon = R.drawable.ic_launcher;
		notification.tickerText = context.getString(R.string.app_name) + "更新";
		contentView = new RemoteViews(context.getPackageName(),
				R.layout.notification_update);
		contentView.setTextViewText(R.id.notificationTitle, "正在下载");
		contentView.setTextViewText(R.id.notificationPercent, "0%");
		contentView.setProgressBar(R.id.notificationProgress, 100, 0, false);
		notification.contentView = contentView;
		updateIntent = new Intent(context, MainActivity.class);
		updateIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		pendingIntent = PendingIntent.getActivity(context, 0, updateIntent, 0);
		notification.contentIntent = pendingIntent;
		notificationManager.notify(notification_id, notification);
	}

	private long downloadUpdateFile(String down_url, File file) {
		if (file == null) {
			return 0;
		}
		int down_step = 5;// 提示step
		int totalSize;// 文件总大小
		int downloadCount = 0;// 已经下载好的大小
		int updateCount = 0;// 已经上传的文件大小
		InputStream inputStream;
		OutputStream outputStream;
		URL url;
		try {
			url = new URL(down_url);
			HttpURLConnection httpURLConnection = (HttpURLConnection) url
					.openConnection();
			httpURLConnection.setConnectTimeout(TIMEOUT);
			httpURLConnection.setReadTimeout(TIMEOUT);
			// 获取下载文件的size
			totalSize = httpURLConnection.getContentLength();
			if (httpURLConnection.getResponseCode() != HttpStatus.SC_OK) {
				httpURLConnection.disconnect();
				return 0;
			}
			inputStream = httpURLConnection.getInputStream();
			outputStream = new FileOutputStream(file, false);// 文件存在则覆盖掉
			byte buffer[] = new byte[1024];
			int readsize = 0;
			while ((readsize = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, readsize);
				downloadCount += readsize;// 时时获取下载到的大小
				if (updateCount == 0
						|| (downloadCount * 100 / totalSize - down_step) >= updateCount) {
					updateCount += down_step;
					contentView.setTextViewText(R.id.notificationPercent,
							updateCount + "%");
					contentView.setProgressBar(R.id.notificationProgress, 100,
							updateCount, false);
					// show_view
					notificationManager.notify(notification_id, notification);
				}
			}
			if (httpURLConnection != null) {
				httpURLConnection.disconnect();
			}
			inputStream.close();
			outputStream.close();
			return downloadCount;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
}
