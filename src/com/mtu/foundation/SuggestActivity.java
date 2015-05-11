package com.mtu.foundation;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.mtu.foundation.mail.MailSender;
import com.mtu.foundation.util.CommonUtil;

public class SuggestActivity extends BaseActivity {
	private EditText vSuggestTxt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_suggest);
		initView();
	}

	private void initView() {
		leftBtn = findViewById(R.id.left_btn);
		leftBtn.setOnClickListener(this);
		leftBtn.setVisibility(View.VISIBLE);
		title = (TextView) findViewById(R.id.top_title);
		title.setText("意见反馈");
		rightBtn = (TextView) findViewById(R.id.right_btn);
		rightBtn.setText("提交");
		rightBtn.setOnClickListener(this);
		vSuggestTxt = (EditText) findViewById(R.id.suggestion);

	}

	@Override
	public void onClick(View arg0) {
		if (arg0 == leftBtn) {
			finish();
			return;
		}
		if (arg0 == rightBtn) {
			doSend();
			return;
		}
	}

	private void doSend() {
		if (!CommonUtil.isNetWorkConnected(this)) {
			showSimpleMessageDialog("亲，没有网络哦！");
			return;
		}
		String suggest = vSuggestTxt.getText().toString();
		if (CommonUtil.isEmpty(suggest)) {
			showSimpleMessageDialog("亲，您还没有填写内容哦！");
			return;
		}
		try {
			TelephonyManager mTm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
			String mtype = android.os.Build.MODEL; // 手机型号
			String mtyb = android.os.Build.BRAND;// 手机品牌
			String numer = mTm.getLine1Number(); // 手机号码，有的可得，有的不可得
			SenderRunnable senderRunnable = new SenderRunnable(
					"funny_ba@163.com", "funny_ba@163");
			senderRunnable.setMail("海大基金会安卓APP", suggest + "\n\n\n\n 手机型号："
					+ mtype + "，手机品牌：" + mtyb + "，手机号码：" + numer,
					"funny_ba@163.com", "");
			new Thread(senderRunnable).start();
		} catch (Exception e) {

		}
		showMsgDialogWithCallback("感谢您的意见或建议，我们将根据您提出的问题，对本应用进行改进，后续功能期待您的参与。");
	}

	protected void showMsgDialogWithCallback(String msg) {
		new AlertDialog.Builder(this).setTitle(null)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						dialogInterface.dismiss();
						finish();
					}
				}).setCancelable(false).setMessage(msg).show();
	}

}

class SenderRunnable implements Runnable {
	private String user;
	private String password;
	private String subject;
	private String body;
	private String receiver;
	private MailSender sender;
	private String attachment;

	public SenderRunnable(String user, String password) {
		this.user = user;
		this.password = password;
		sender = new MailSender(user, this.password);
		String mailhost = user.substring(user.lastIndexOf("@") + 1,
				user.lastIndexOf("."));
		if (!mailhost.equals("gmail")) {
			mailhost = "smtp." + mailhost + ".com";
			sender.setMailhost(mailhost);
		}
	}

	public void setMail(String subject, String body, String receiver,
			String attachment) {
		this.subject = subject;
		this.body = body;
		this.receiver = receiver;
		this.attachment = attachment;
	}

	public void run() {
		try {
			sender.sendMail(subject, body, user, receiver, attachment);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
