package com.mtu.foundation;

import java.util.List;

import org.apache.http.HttpStatus;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.mtu.foundation.bean.DonateBean;
import com.mtu.foundation.db.BeanPropEnum;
import com.mtu.foundation.db.DBHelper;
import com.mtu.foundation.db.RecordBean;
import com.mtu.foundation.net.HTMLParser;
import com.mtu.foundation.net.ThreadPoolUtils;
import com.mtu.foundation.net.httpjersey.Callback;
import com.mtu.foundation.net.httpjersey.NetworkHandler;
import com.mtu.foundation.net.httpjersey.TransResp;
import com.mtu.foundation.util.CommonUtil;
import com.mtu.foundation.util.Constants;
import com.mtu.foundation.util.FileOperRunnable;
import com.mtu.foundation.util.FileUtil;

public class FastDonateActivity extends BaseActivity {
	private View vItemLay, vGendarLay, vAlumiLay, vAnonymousLay, vNextStep,
			vOpenLay, vContentLay, vCheckImg;
	private String item, amount, comment, username, gender, is_alumni, email,
			tel, cellphone, address, postcode, company, is_anonymous, paytype;
	private NetworkHandler networkHandler;
	private String[] itemListStr, gendarStr = { "先生", "女士" }, alumniStr = {
			"是", "否" };
	private AlertDialog.Builder itemDialog, gendarDialog, alumniDialog,
			anonymousDialog;
	private DonateBean donateBean;
	private EditText vAmountTxt, vUsernameTxt, vEmailTxt, vCellphoneTxt;
	private TextView vItemTxt, vgendarTxt, vAlumniTxt, vAnonymousTxt,
			vCheckTxt;
	private double price;
	private Handler cacheHandler;
	private DBHelper dbHelper;
	private RecordBean bean;
	private boolean checkFlag;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fastdonate);
		dbHelper = DBHelper.getInstance(this);
		initData();
		initView();
		initOther();
		ThreadPoolUtils.execute(new FileOperRunnable(FileUtil
				.getCacheFile(Constants.CACHE_DONATE), true, false, null,
				cacheHandler));
	}

	private void initData() {
		gender = gendarStr[0];
		is_alumni = alumniStr[0];
		is_anonymous = alumniStr[1];
		networkHandler = NetworkHandler.getInstance();
		bean = dbHelper.getLastUser();
	}

	private void initView() {

		leftBtn = findViewById(R.id.left_btn);
		leftBtn.setOnClickListener(this);
		leftBtn.setVisibility(View.VISIBLE);
		title = (TextView) findViewById(R.id.top_title);
		title.setText("快捷捐赠");
		vOpenLay = findViewById(R.id.open_lay);
		vContentLay = findViewById(R.id.content_lay);
		vCheckImg = findViewById(R.id.check_img);
		vCheckImg.setOnClickListener(this);
		vCheckTxt = (TextView) findViewById(R.id.check_txt);
		vCheckTxt.setOnClickListener(this);
		String fastFlag = dbHelper
				.getValueByKey(BeanPropEnum.CommonProp.fastDonateFlag
						.toString());
		if (!CommonUtil.isEmpty(fastFlag) && "1".equals(fastFlag)) {
			vCheckImg.setSelected(true);
			vOpenLay.setVisibility(View.VISIBLE);
			vCheckTxt.setText("已开启");
			checkFlag = true;
			vContentLay.setVisibility(View.GONE);
		} else if (bean != null && !CommonUtil.isEmpty(bean.getAmount())
				&& !CommonUtil.isEmpty(bean.getProject())
				&& !CommonUtil.isEmpty(bean.getUsername())
				&& !CommonUtil.isEmpty(bean.getEmail())
				&& !CommonUtil.isEmpty(bean.getCellphone())) {
			vCheckTxt.setText("未开启");
			checkFlag = false;
			vOpenLay.setVisibility(View.VISIBLE);
			vContentLay.setVisibility(View.GONE);
		} else {
			checkFlag = false;
			vOpenLay.setVisibility(View.GONE);
			vContentLay.setVisibility(View.VISIBLE);
		}

		vNextStep = findViewById(R.id.next_step);
		vNextStep.setOnClickListener(this);
		vItemLay = findViewById(R.id.item_project_lay);
		vItemLay.setOnClickListener(this);
		vGendarLay = findViewById(R.id.item_gender_lay);
		vGendarLay.setOnClickListener(this);
		vAlumiLay = findViewById(R.id.item_alumni_lay);
		vAlumiLay.setOnClickListener(this);
		vAnonymousLay = findViewById(R.id.item_anonymous_lay);
		vAnonymousLay.setOnClickListener(this);

		vItemTxt = (TextView) findViewById(R.id.item_project_txt);
		vgendarTxt = (TextView) findViewById(R.id.item_gender_txt);
		vAlumniTxt = (TextView) findViewById(R.id.item_alumni_txt);
		vAnonymousTxt = (TextView) findViewById(R.id.item_anonymous_txt);

		vAmountTxt = (EditText) findViewById(R.id.item_amount_txt);
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

			@Override
			public void afterTextChanged(Editable editable) {

			}
		});
		vUsernameTxt = (EditText) findViewById(R.id.item_username_txt);
		vEmailTxt = (EditText) findViewById(R.id.item_email_txt);
		vCellphoneTxt = (EditText) findViewById(R.id.item_cellphone_txt);

		initViewData();
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

	private void initViewData() {

		if (bean != null && !CommonUtil.isEmpty(bean.getUsername())
				&& !CommonUtil.isEmpty(bean.getEmail())
				&& !CommonUtil.isEmpty(bean.getCellphone())
				&& !CommonUtil.isEmpty(bean.getTel())) {
			vUsernameTxt.setText(bean.getUsername());
			vEmailTxt.setText(bean.getEmail());
			vCellphoneTxt.setText(bean.getCellphone());
			if (!CommonUtil.isEmpty(bean.getGender())) {
				vgendarTxt.setText(bean.getGender());
			}
			if (!CommonUtil.isEmpty(bean.getIs_alumni())) {
				vAlumniTxt.setText(bean.getIs_alumni());
			}
			if (!CommonUtil.isEmpty(bean.getIs_anonymous())) {
				vAnonymousTxt.setText(bean.getIs_anonymous());
			}
		} else {

		}
	}

	@Override
	public void onClick(View arg0) {
		if (arg0 == leftBtn) {
			finish();
			return;
		}
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
		if (arg0 == vCheckImg || arg0 == vCheckTxt) {
			if (checkFlag) {
				showMsgDialogWithTwo("确定关闭快捷捐赠吗？");
				return;
			} else {
				doChange(false);
			}
			return;
		}
		if (vNextStep == arg0 || rightBtn == arg0) {
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

		cellphone = vCellphoneTxt.getText().toString();
		if (CommonUtil.isEmpty(cellphone)) {
			vCellphoneTxt.setError("请输入移动电话");
			vCellphoneTxt.requestFocus();
			return;
		}
		tel = cellphone;
		if (CommonUtil.isEmpty(is_anonymous)) {
			showAnonymousDialog();
			return;
		}
		saveData();

	}

	private void saveData() {
		if (dbHelper == null) {
			dbHelper = DBHelper.getInstance(this);
		}

		RecordBean bean = new RecordBean();
		bean.setAddress(address);
		bean.setAmount(amount);
		bean.setCellphone(cellphone);
		bean.setComment(comment);
		bean.setCompany(company);

		bean.setEmail(email);
		bean.setGender(gender);
		bean.setIs_alumni(is_alumni);
		bean.setIs_anonymous(is_anonymous);
		bean.setPaytype(paytype);
		bean.setPostcode(postcode);
		bean.setProject(item);
		bean.setTel(tel);
		bean.setUsername(username);
		dbHelper.saveLastRecord(bean);
		dbHelper.saveOrUpdateKeyMap(
				BeanPropEnum.CommonProp.fastDonateFlag.toString(), "1");

		showMsgDialogWithCallback("您已开启快捷捐赠功能是否现在捐赠一次？");
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
			itemDialog = new AlertDialog.Builder(this).setTitle("请选择捐赠项目")
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
			anonymousDialog = new AlertDialog.Builder(this)
					.setTitle(null)
					.setMessage("是否匿名捐赠？")
					.setNegativeButton("否",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									vAnonymousTxt.setText("否");
									is_anonymous = "否";
									arg0.dismiss();
								}
							})
					.setPositiveButton("是",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									vAnonymousTxt.setText("是");
									is_anonymous = "是";
									arg0.dismiss();
								}
							});
		}
		anonymousDialog.show();
	}

	private void showAlumniDialog() {
		if (alumniDialog == null) {
			alumniDialog = new AlertDialog.Builder(this)
					.setTitle(null)
					.setMessage("是否校友？")
					.setNegativeButton("否",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									vAlumniTxt.setText("否");
									is_alumni = "否";
									arg0.dismiss();
								}
							})
					.setPositiveButton("是",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									vAlumniTxt.setText("是");
									is_alumni = "是";
									arg0.dismiss();
								}
							});
		}
		alumniDialog.show();
	}

	private void showGendarDialog() {
		if (gendarDialog == null) {
			gendarDialog = new AlertDialog.Builder(this).setTitle("请选择称谓")
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

	protected void showMsgDialogWithCallback(String msg) {
		new AlertDialog.Builder(this)
				.setTitle(null)
				.setPositiveButton("现在捐赠",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(
									DialogInterface dialogInterface, int i) {
								dialogInterface.dismiss();
								switchTo(DonateActivity.class);
								finish();
							}
						})
				.setNegativeButton("稍后再说",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								arg0.dismiss();
								finish();
							}
						}).setCancelable(false).setMessage(msg).show();
	}

	protected void showMsgDialogWithTwo(String msg) {
		new AlertDialog.Builder(this).setTitle(null)
				.setNegativeButton("取消", null)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						doChange(true);
						arg0.dismiss();
					}
				}).setCancelable(false).setMessage(msg).show();
	}

	private void doChange(boolean flag) {
		if (flag) {
			dbHelper.saveOrUpdateKeyMap(
					BeanPropEnum.CommonProp.fastDonateFlag.toString(), "0");
			vCheckImg.setSelected(false);
			vCheckTxt.setText("未开启");
			checkFlag = false;
		} else {
			dbHelper.saveOrUpdateKeyMap(
					BeanPropEnum.CommonProp.fastDonateFlag.toString(), "1");
			vCheckImg.setSelected(true);
			vCheckTxt.setText("已开启");
			checkFlag = true;
		}
	}
}
