package com.mtu.foundation;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.apache.http.HttpStatus;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.mtu.foundation.bean.DonateBean;
import com.mtu.foundation.bean.HistoryItemBean;
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

public class DonateActivity extends BaseActivity {
	private View vItemLay, vGendarLay, vAlumiLay, vAnonymousLay, vNextStep,
			vRecordBtn, vShowFlag, vUserInforLay, vOtherInforLay;
	private String item, amount, comment, username, gender, is_alumni, email,
			tel, cellphone, address, postcode, company, is_anonymous, paytype;
	private NetworkHandler networkHandler;
	private String[] itemListStr, gendarStr = { "先生", "女士" }, alumniStr = {
			"是", "否" };
	private AlertDialog.Builder itemDialog, gendarDialog, alumniDialog,
			anonymousDialog;
	private DonateBean donateBean;
	private EditText vAmountTxt, vCommentTxt, vUsernameTxt, vEmailTxt, vTelTxt,
			vCellphoneTxt, vAddressTxt, vPostcodeTxt, vCompanyTxt;
	private TextView vItemTxt, vgendarTxt, vAlumniTxt, vAnonymousTxt;
	private double price;
	private Handler cacheHandler;
	private DBHelper dbHelper;
	private ImageView vShowImg;
	private RecordBean bean;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dbHelper = DBHelper.getInstance(this);
		String fastFlag = dbHelper
				.getValueByKey(BeanPropEnum.CommonProp.fastDonateFlag
						.toString());
		bean = dbHelper.getLastUser();
		if (!CommonUtil.isEmpty(fastFlag) && "1".equals(fastFlag)) {
			if (bean != null && !CommonUtil.isEmpty(bean.getAmount())
					&& !CommonUtil.isEmpty(bean.getProject())
					&& !CommonUtil.isEmpty(bean.getUsername())
					&& !CommonUtil.isEmpty(bean.getEmail())
					&& !CommonUtil.isEmpty(bean.getCellphone())) {
				item = bean.getProject();
				amount = bean.getAmount();
				username = bean.getUsername();
				gender = bean.getGender() == null ? gendarStr[0] : bean
						.getGender();
				is_alumni = bean.getIs_alumni() == null ? alumniStr[0] : bean
						.getIs_alumni();
				email = bean.getEmail();
				cellphone = bean.getCellphone();
				is_anonymous = bean.getIs_anonymous() == null ? alumniStr[1]
						: bean.getIs_anonymous();
				toPay();
			}
		}
		setContentView(R.layout.activity_donate);
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

	}

	private void initView() {
		leftBtn = findViewById(R.id.left_btn);
		leftBtn.setOnClickListener(this);
		leftBtn.setVisibility(View.VISIBLE);
		title = (TextView) findViewById(R.id.top_title);
		title.setText("我要捐赠");
		rightBtn = (TextView) findViewById(R.id.right_btn);
		rightBtn.setText("下一步");
		rightBtn.setOnClickListener(this);

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
		vCommentTxt = (EditText) findViewById(R.id.item_comment_txt);
		vUsernameTxt = (EditText) findViewById(R.id.item_username_txt);
		vEmailTxt = (EditText) findViewById(R.id.item_email_txt);
		vTelTxt = (EditText) findViewById(R.id.item_tel_txt);
		vCellphoneTxt = (EditText) findViewById(R.id.item_cellphone_txt);
		vAddressTxt = (EditText) findViewById(R.id.item_address_txt);
		vPostcodeTxt = (EditText) findViewById(R.id.item_postcode_txt);
		vCompanyTxt = (EditText) findViewById(R.id.item_company_txt);

		vShowFlag = findViewById(R.id.show_flag);
		vShowFlag.setOnClickListener(this);
		vShowImg = (ImageView) findViewById(R.id.show_flag_img);
		vShowImg.setOnClickListener(this);

		vUserInforLay = findViewById(R.id.user_infor_lay);
		vOtherInforLay = findViewById(R.id.other_infor_lay);
		vRecordBtn = findViewById(R.id.record_btn);
		vRecordBtn.setOnClickListener(this);
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

		Log.d("bean", bean == null ? "" : bean.toString());
		if (bean != null && !CommonUtil.isEmpty(bean.getUsername())
				&& !CommonUtil.isEmpty(bean.getEmail())
				&& !CommonUtil.isEmpty(bean.getCellphone())) {
			vUserInforLay.setVisibility(View.GONE);
			vOtherInforLay.setVisibility(View.GONE);
			vShowFlag.setVisibility(View.VISIBLE);
			vShowImg.setImageResource(R.drawable.iconfont_unselected);
			vUsernameTxt.setText(bean.getUsername());
			vEmailTxt.setText(bean.getEmail());

			vCellphoneTxt.setText(bean.getCellphone());
			vAddressTxt.setText(bean.getAddress());
			vPostcodeTxt.setText(bean.getPostcode());
			vCompanyTxt.setText(bean.getCompany());
			if (!CommonUtil.isEmpty(bean.getGender())) {
				vgendarTxt.setText(bean.getGender());
			}
			if (!CommonUtil.isEmpty(bean.getIs_alumni())) {
				vAlumniTxt.setText(bean.getIs_alumni());
			}
			if (!CommonUtil.isEmpty(bean.getIs_anonymous())) {
				vAnonymousTxt.setText(bean.getIs_anonymous());
			}
			vRecordBtn.setVisibility(View.VISIBLE);
			showFlag = false;
		} else {
			vUserInforLay.setVisibility(View.VISIBLE);
			vOtherInforLay.setVisibility(View.VISIBLE);
			vShowFlag.setVisibility(View.GONE);
			vShowImg.setImageResource(R.drawable.iconfont_unselected);
			vRecordBtn.setVisibility(View.GONE);
			showFlag = false;
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
		if (vNextStep == arg0 || rightBtn == arg0) {
			checkInput();
			return;
		}
		if (vShowFlag == arg0 || vShowImg == arg0) {
			switchFlag();
			return;
		}
		if (vRecordBtn == arg0) {
			Intent intent = new Intent(this, RecordsActivity.class);
			startActivity(intent);
			return;
		}

	}

	private boolean showFlag = false;

	private void switchFlag() {
		if (showFlag) {
			vUserInforLay.setVisibility(View.GONE);
			vOtherInforLay.setVisibility(View.GONE);
			vShowImg.setImageResource(R.drawable.iconfont_unselected);
		} else {
			vUserInforLay.setVisibility(View.VISIBLE);
			vOtherInforLay.setVisibility(View.VISIBLE);
			vShowImg.setImageResource(R.drawable.iconfont_pwdselected);
		}
		showFlag = !showFlag;
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

		cellphone = vCellphoneTxt.getText().toString();
		if (CommonUtil.isEmpty(cellphone)) {
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
		toPay();

	}

	private void saveHistory() {
		HistoryItemBean bean = new HistoryItemBean();
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
		try {
			String date = format.format(calendar.getTime());
			bean.setDate(date);
			bean.setDay(date.substring(8, 10));
			bean.setImgId(String.valueOf(R.drawable.red_circle));
			double price = Double.parseDouble(amount);
			String temp = "捐赠了一笔，积小流，成江河，善于回报。";
			if (price > 10000 && price < 50000) {
				temp = "很大方的捐赠了一笔，积小流，成江河，敢于回报，母校因你而自豪。";
			} else if (price >= 50000) {
				temp = "非常大方的捐赠了一笔，此时此刻，母校的各位学子，除了感谢，我想再不会有其他。";
			}

			bean.setContent(bean.getDay() + "日，为" + item + temp);
			dbHelper.saveHistory(bean);
		} catch (Exception e) {
		}
	}

	private void toPay() {
		Intent intent = new Intent(this, PayRouteActivity.class);
		intent.putExtra("item", item);
		intent.putExtra("amount", amount);
		intent.putExtra("comment", comment);
		intent.putExtra("username", username);
		intent.putExtra("gender", gender);
		intent.putExtra("is_alumni", is_alumni);
		intent.putExtra("email", email);
		intent.putExtra("tel", cellphone);
		intent.putExtra("cellphone", cellphone);
		intent.putExtra("address", address);
		intent.putExtra("postcode", postcode);
		intent.putExtra("company", company);
		intent.putExtra("is_anonymous", is_anonymous);
		this.startActivity(intent);
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
				if (bean != null && !temp.contains(bean.getProject())) {
					item = itemListStr[0];
					if (dbHelper != null) {
						dbHelper.saveOrUpdateKeyMap(
								BeanPropEnum.RecordProp.project.toString(),
								item);
					}
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

	@Override
	public void onResume() {
		initViewData();
		super.onResume();
	}

}
