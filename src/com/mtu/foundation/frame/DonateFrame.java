package com.mtu.foundation.frame;

import java.util.List;

import org.apache.http.HttpStatus;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.mtu.foundation.PayRouteActivity;
import com.mtu.foundation.R;
import com.mtu.foundation.bean.DonateBean;
import com.mtu.foundation.net.HTMLParser;
import com.mtu.foundation.net.ThreadPoolUtils;
import com.mtu.foundation.net.httpjersey.Callback;
import com.mtu.foundation.net.httpjersey.NetworkHandler;
import com.mtu.foundation.net.httpjersey.TransResp;
import com.mtu.foundation.util.CommonUtil;
import com.mtu.foundation.util.Constants;
import com.mtu.foundation.util.FileOperRunnable;
import com.mtu.foundation.util.FileUtil;

public class DonateFrame extends Fragment implements OnClickListener {
	private View view, vItemLay, vGendarLay, vAlumiLay, vAnonymousLay,
			vNextStep;
	private Context context;
	private String item, amount, comment, username, gender, is_alumni, email,
			tel, cellphone, address, postcode, company, is_anonymous, paytype;
	private NetworkHandler networkHandler;
	private String[] itemListStr, gendarStr = { "先生", "女士" }, alumniStr = {
			"是", "否" };
	private AlertDialog.Builder simpleMsgDialog, itemDialog, gendarDialog,
			alumniDialog, anonymousDialog;
	private DonateBean donateBean;
	private EditText vAmountTxt, vCommentTxt, vUsernameTxt, vEmailTxt, vTelTxt,
			vCellphoneTxt, vAddressTxt, vPostcodeTxt, vCompanyTxt;
	private TextView vItemTxt, vgendarTxt, vAlumniTxt, vAnonymousTxt;
	private double price;
	private Handler cacheHandler;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.tab_donate, container, false);
		context = view.getContext();
		initData();
		initView();
		initOther();
		ThreadPoolUtils.execute(new FileOperRunnable(FileUtil
				.getCacheFile(Constants.CACHE_DONATE), true, false, null,
				cacheHandler));
		return view;
	}

	private void initData() {
		gender = gendarStr[0];
		is_alumni = alumniStr[0];
		is_anonymous = alumniStr[1];
		networkHandler = NetworkHandler.getInstance();
	}

	private void initView() {
		// vItemLay, vGendarLay, vAlumiLay, vAnonymousLay;
		vNextStep = view.findViewById(R.id.next_step);
		vNextStep.setOnClickListener(this);
		vNextStep.setSelected(true);
		vNextStep.setClickable(false);
		vItemLay = view.findViewById(R.id.item_project_lay);
		vItemLay.setOnClickListener(this);
		vGendarLay = view.findViewById(R.id.item_gender_lay);
		vGendarLay.setOnClickListener(this);
		vAlumiLay = view.findViewById(R.id.item_alumni_lay);
		vAlumiLay.setOnClickListener(this);
		vAnonymousLay = view.findViewById(R.id.item_anonymous_lay);
		vAnonymousLay.setOnClickListener(this);

		vItemTxt = (TextView) view.findViewById(R.id.item_project_txt);
		vgendarTxt = (TextView) view.findViewById(R.id.item_gender_txt);
		vAlumniTxt = (TextView) view.findViewById(R.id.item_alumni_txt);
		vAnonymousTxt = (TextView) view.findViewById(R.id.item_anonymous_txt);

		vAmountTxt = (EditText) view.findViewById(R.id.item_amount_txt);
		vAmountTxt.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i,
					int i2, int i3) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i2,
					int i3) {
				if (CommonUtil.isEmpty(vAmountTxt.getText().toString())) {
					vNextStep.setSelected(true);
					vNextStep.setClickable(false);
				} else {
					String temp = vAmountTxt.getText().toString();
					price = 0;
					try {
						price = Double.parseDouble(temp);
					} catch (Exception e) {
						if (temp.startsWith(".")) {
							vAmountTxt.setText(null);
							vAmountTxt.setError("请输入合法的数字");
							vAmountTxt.requestFocus();
							return;
						}
					}
					if (price < 0.01 || price > 1000) {
						vNextStep.setSelected(true);
						vNextStep.setClickable(false);
					} else {
						vNextStep.setSelected(false);
						vNextStep.setClickable(true);
						int dot = temp.indexOf(".");
						if (dot >= 0) {
							int len = temp.substring(dot + 1).length();
							if (len > 2) {
								vAmountTxt.setText(temp.substring(0, dot + 3));
								vAmountTxt.setSelection(vAmountTxt.getText()
										.length());
								return;
							}
						}
					}
				}
			}

			@Override
			public void afterTextChanged(Editable editable) {

			}
		});
		vCommentTxt = (EditText) view.findViewById(R.id.item_comment_txt);
		vUsernameTxt = (EditText) view.findViewById(R.id.item_username_txt);
		vEmailTxt = (EditText) view.findViewById(R.id.item_email_txt);
		vTelTxt = (EditText) view.findViewById(R.id.item_tel_txt);
		vCellphoneTxt = (EditText) view.findViewById(R.id.item_cellphone_txt);
		vAddressTxt = (EditText) view.findViewById(R.id.item_address_txt);
		vPostcodeTxt = (EditText) view.findViewById(R.id.item_postcode_txt);
		vCompanyTxt = (EditText) view.findViewById(R.id.item_company_txt);

	}

	private void initOther() {
		cacheHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case Constants.READ_RESULT_OK:
					String cacheData = (String) msg.obj;
					if (!CommonUtil.isEmpty(cacheData)) {
						tranceData(false, cacheData);
					} else {
						getData(false);
					}
					break;
				}
			}
		};
	}

	@Override
	public void onClick(View arg0) {
		if (arg0 == vItemLay) {
			showItemDialog();
			return;
		}
		if (arg0 == vGendarLay) {
			showGendarDialog();
			return;
		}
		if (arg0 == vAlumiLay) {
			showAlumniDialog();
			return;
		}
		if (arg0 == vAnonymousLay) {
			showAnonymousDialog();
			return;
		}
		if (vNextStep == arg0) {
			checkInput();
			return;
		}

	}

	private void checkInput() {
		amount = vAmountTxt.getText().toString();
		if (CommonUtil.isEmpty(amount)) {
			vAmountTxt.setError("请输入金额");
			vAmountTxt.requestFocus();
			return;
		}

		if (CommonUtil.isEmpty(item)) {
			showItemDialog();
			return;
		}
		comment = vCommentTxt.getText().toString();
		username = vUsernameTxt.getText().toString();
		if (CommonUtil.isEmpty(username)) {
			vUsernameTxt.setError("请输入您的姓名");
			vUsernameTxt.requestFocus();
			return;
		}
		if (CommonUtil.isEmpty(gender)) {
			showGendarDialog();
			return;
		}
		if (CommonUtil.isEmpty(is_alumni)) {
			showAlumniDialog();
			return;
		}
		email = vEmailTxt.getText().toString();
		if (CommonUtil.isEmpty(email)) {
			vEmailTxt.setError("请输入您的邮箱");
			vEmailTxt.requestFocus();
			return;
		}
		tel = vTelTxt.getText().toString();
		if (CommonUtil.isEmpty(tel)) {
			vTelTxt.setError("请输入联系电话");
			vTelTxt.requestFocus();
			return;
		}

		cellphone = vCellphoneTxt.getText().toString();
		if (CommonUtil.isEmpty(tel)) {
			vCellphoneTxt.setError("请输入移动电话");
			vCellphoneTxt.requestFocus();
			return;
		}
		if (CommonUtil.isEmpty(is_anonymous)) {
			showAnonymousDialog();
			return;
		}
		address = vAddressTxt.getText().toString();
		postcode = vPostcodeTxt.getText().toString();
		company = vCompanyTxt.getText().toString();
		Intent intent = new Intent(context, PayRouteActivity.class);
		intent.putExtra("item", item);
		intent.putExtra("amount", amount);
		intent.putExtra("comment", comment);
		intent.putExtra("username", username);
		intent.putExtra("gender", gender);
		intent.putExtra("is_alumni", is_alumni);
		intent.putExtra("email", email);
		intent.putExtra("tel", tel);
		intent.putExtra("cellphone", cellphone);
		intent.putExtra("address", address);
		intent.putExtra("postcode", postcode);
		intent.putExtra("company", company);
		intent.putExtra("is_anonymous", is_anonymous);
		context.startActivity(intent);

	}

	private HTMLParser parser;

	private void getData(final boolean showFlag) {
		networkHandler.get(Constants.URI_DONATE, null, 30,
				new Callback<TransResp>() {
					@Override
					public void callback(TransResp tr) {
						if (tr.getRetcode() == HttpStatus.SC_OK) {
							tranceData(showFlag, tr.getRetjson());
						} else {
							if (showFlag) {
								showSimpleMessageDialog("加载失败了");
							}
						}
					}
				});
	}

	private void tranceData(boolean showFlag, String html) {
		if (parser == null) {
			parser = new HTMLParser(html);
		} else {
			parser.setHTMLStr(html);
		}
		donateBean = parser.getInitDonateBean();
		if (donateBean != null) {
			List<String> temp = donateBean.getItemList();
			if (temp != null && temp.size() > 0) {
				itemListStr = new String[temp.size()];
				for (int i = 0; i < temp.size(); i++) {
					itemListStr[i] = temp.get(i);
				}
				item = itemListStr[0];
				vItemTxt.setText(item);
				if (showFlag) {
					showItemDialog();
				}
			}
		}
	}

	private void showItemDialog() {
		if (itemDialog == null) {
			itemDialog = new AlertDialog.Builder(context).setTitle("请选择捐赠项目")
					.setNegativeButton("取消", null);
		}
		if (itemListStr == null || itemListStr.length == 0) {
			getData(true);
			return;
		}
		itemDialog.setItems(itemListStr, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				item = itemListStr[which];
				vItemTxt.setText(item);
				dialog.dismiss();
			}
		});
		itemDialog.show();
	}

	private void showAnonymousDialog() {
		if (anonymousDialog == null) {
			anonymousDialog = new AlertDialog.Builder(context)
					.setTitle("是否匿名？")
					.setItems(alumniStr, new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							is_anonymous = alumniStr[arg1];
							vAnonymousTxt.setText(is_anonymous);
							arg0.dismiss();

						}
					}).setNegativeButton("取消", null);
		}
		anonymousDialog.show();
	}

	private void showAlumniDialog() {
		if (alumniDialog == null) {
			alumniDialog = new AlertDialog.Builder(context).setTitle("是否校友？")
					.setItems(alumniStr, new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							is_alumni = alumniStr[arg1];
							vAlumniTxt.setText(is_alumni);
							arg0.dismiss();
						}
					}).setNegativeButton("取消", null);
		}
		alumniDialog.show();
	}

	private void showGendarDialog() {
		if (gendarDialog == null) {
			gendarDialog = new AlertDialog.Builder(context).setTitle("请选择称谓")
					.setItems(gendarStr, new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							gender = gendarStr[arg1];
							vgendarTxt.setText(gender);
							arg0.dismiss();
						}
					}).setNegativeButton("取消", null);
		}
		gendarDialog.show();
	}

	private void showSimpleMessageDialog(String msg) {

		if (simpleMsgDialog == null) {
			simpleMsgDialog = new AlertDialog.Builder(context).setTitle(null)
					.setNegativeButton("确定", null).setCancelable(false)
					.setMessage(msg);
		} else {
			simpleMsgDialog.setMessage(msg);
		}
		simpleMsgDialog.show();
	}

}
