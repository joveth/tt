package com.mtu.foundation;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.mtu.foundation.db.DBHelper;
import com.mtu.foundation.db.RecordBean;
import com.mtu.foundation.net.httpjersey.Callback;
import com.mtu.foundation.net.httpjersey.NetworkHandler;
import com.mtu.foundation.net.httpjersey.TransResp;
import com.mtu.foundation.util.CommonUtil;
import com.mtu.foundation.util.Constants;
import com.mtu.foundation.view.CustomProgressDialog;

public class MessageActivity extends BaseActivity {
	private EditText vName, vTel, vEmail, vSubject, vContent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message);
		initView();
	}

	private void initView() {
		leftBtn = findViewById(R.id.left_btn);
		leftBtn.setOnClickListener(this);
		leftBtn.setVisibility(View.VISIBLE);
		title = (TextView) findViewById(R.id.top_title);
		title.setText("我要留言");
		rightBtn = (TextView) findViewById(R.id.right_btn);
		rightBtn.setText("提交");
		rightBtn.setOnClickListener(this);

		vName = (EditText) findViewById(R.id.msg_name_txt);
		vTel = (EditText) findViewById(R.id.msg_tel_txt);
		vEmail = (EditText) findViewById(R.id.msg_email_txt);
		vSubject = (EditText) findViewById(R.id.msg_subject_txt);
		vContent = (EditText) findViewById(R.id.message_content);

		initViewData();
	}

	private void initViewData() {
		if (dbHelper == null) {
			dbHelper = DBHelper.getInstance(this);
		}
		RecordBean bean = dbHelper.getLastUser();
		if (bean != null) {
			vName.setText(bean.getUsername());
			vTel.setText(bean.getCellphone());
			vEmail.setText(bean.getEmail());
		}
	}

	@Override
	public void onClick(View arg0) {
		if (arg0 == leftBtn) {
			finish();
		}
		if (arg0 == rightBtn) {
			checkInput();
			return;
		}
	}

	private void checkInput() {
		if (!CommonUtil.isNetWorkConnected(this)) {
			showSimpleMessageDialog("没有网络!");
			return;
		}

		if (CommonUtil.isEmpty(vName.getText().toString())) {
			vName.setError("请输入姓名");
			vName.requestFocus();
			return;
		}
		if (CommonUtil.isEmpty(vTel.getText().toString())) {
			vTel.setError("请输入联系电话");
			vTel.requestFocus();
			return;
		}
		if (CommonUtil.isEmpty(vEmail.getText().toString())) {
			vEmail.setError("请输入邮箱");
			vEmail.requestFocus();
			return;
		}
		if (CommonUtil.isEmpty(vSubject.getText().toString())) {
			vSubject.setError("请输入主题");
			vSubject.requestFocus();
			return;
		}
		if (CommonUtil.isEmpty(vContent.getText().toString())) {
			vContent.setError("请填写留言内容");
			vContent.requestFocus();
			return;
		}
		if (progressDialog == null) {
			progressDialog = new CustomProgressDialog(this, "正在提交", false);
		}
		progressDialog.show();

		if (networkHandler == null) {
			networkHandler = NetworkHandler.getInstance();
		}

		List<NameValuePair> paramspost = new ArrayList<NameValuePair>();
		paramspost.add(new BasicNameValuePair("field_username[und][0][value]",
				vName.getText().toString()));
		paramspost.add(new BasicNameValuePair(
				"field_contactinfo[und][0][value]", vTel.getText().toString()));
		paramspost.add(new BasicNameValuePair("field_email[und][0][value]",
				vEmail.getText().toString()));
		paramspost.add(new BasicNameValuePair("title", vSubject.getText()
				.toString()));
		paramspost.add(new BasicNameValuePair("field_plaintext[und][0][value]",
				vContent.getText().toString()));
		paramspost.add(new BasicNameValuePair("form_id", "liu_yan_node_form"));
		networkHandler.post(Constants.URI_MESSAGE, paramspost, 30,
				new Callback<TransResp>() {

					@Override
					public void callback(TransResp t) {
						if (t.getRetcode() == HttpStatus.SC_OK) {
							Log.d("ret", t.getRetjson());
							progressDialog.dismiss();
							showMsgDialogWithCallback("感谢您的留言,我们将根据您的留言尽快与您联系！");
						} else {
							progressDialog.dismiss();
							showSimpleMessageDialog("提交失败了");
						}
					}
				});

	}

}
