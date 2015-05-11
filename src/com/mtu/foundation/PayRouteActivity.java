package com.mtu.foundation;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.mtu.foundation.bean.HistoryItemBean;
import com.mtu.foundation.db.BeanPropEnum;
import com.mtu.foundation.db.DBHelper;
import com.mtu.foundation.db.RecordBean;
import com.mtu.foundation.net.httpjersey.NetworkHandler;
import com.mtu.foundation.util.CommonUtil;
import com.mtu.foundation.util.Constants;
import com.mtu.foundation.util.alipay.AliPayUtil;
import com.mtu.foundation.util.alipay.PayResult;
import com.mtu.foundation.view.CustomProgressDialog;

public class PayRouteActivity extends BaseActivity {
	private NetworkHandler networkHandler;
	private String item, amount, comment, username, gender, is_alumni, email,
			tel, cellphone, address, postcode, company, is_anonymous,
			paytype = "alipay", bank, cardName;

	private TextView textView, vTypeName, vItem;
	private View vNextStep, vZhiFuBao, vZhiFuBaoImg, vZhiFuBaoWeb,
			vZhiFuBaoWebImg, vCardImg, vRechargeType;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_payroute);
		leftBtn = findViewById(R.id.left_btn);
		leftBtn.setOnClickListener(this);
		leftBtn.setVisibility(View.VISIBLE);
		title = (TextView) findViewById(R.id.top_title);
		title.setText("确认付款");
		textView = (TextView) findViewById(R.id.donate_amount);
		vZhiFuBao = findViewById(R.id.recharge_zhifubao);
		vZhiFuBao.setOnClickListener(this);
		vZhiFuBaoImg = findViewById(R.id.recharge_zhifubao_img);

		vZhiFuBaoWeb = findViewById(R.id.recharge_type_zhifubao_web);
		vZhiFuBaoWeb.setOnClickListener(this);
		vZhiFuBaoWebImg = findViewById(R.id.recharge_zhifubao_web_img);
		vCardImg = findViewById(R.id.recharge_card_img);
		vRechargeType = findViewById(R.id.recharge_type_lay);
		vRechargeType.setOnClickListener(this);
		vNextStep = findViewById(R.id.next_step);
		vNextStep.setOnClickListener(this);
		vTypeName = (TextView) findViewById(R.id.charge_type_name);
		vItem = (TextView) findViewById(R.id.donate_item);
		initData();
	}

	private void initData() {
		networkHandler = NetworkHandler.getInstance();
		Intent intent = getIntent();
		if (intent == null) {
			finish();
			return;
		}
		item = intent.getStringExtra("item");
		amount = intent.getStringExtra("amount");
		textView.setText(CommonUtil.decialStrFormat(amount) + " 元");
		comment = intent.getStringExtra("comment");
		username = intent.getStringExtra("username");
		gender = intent.getStringExtra("gender");
		is_alumni = intent.getStringExtra("is_alumni");
		email = intent.getStringExtra("email");
		tel = intent.getStringExtra("tel");
		cellphone = intent.getStringExtra("cellphone");
		address = intent.getStringExtra("address");
		postcode = intent.getStringExtra("postcode");
		company = intent.getStringExtra("company");
		is_anonymous = intent.getStringExtra("is_anonymous");
		vItem.setText(item);
	}

	private void toPay() {
		Intent intent = new Intent(this, ToPayActivity.class);
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
		intent.putExtra("paytype", paytype);
		if (!CommonUtil.isEmpty(bank)) {
			intent.putExtra("bank", bank);
		}
		startActivityForResult(intent, Constants.REQUEST_CODE_110);
	}

	private int choice = 2;

	@Override
	public void onClick(View arg0) {
		if (arg0 == leftBtn) {
			finish();
			return;
		}
		if (arg0 == vNextStep) {
			// toPay();
			doNextStep();
			return;
		}
		if (arg0 == vRechargeType) {
			Intent intent = new Intent(this, RechargeTypeActivity.class);
			if (!CommonUtil.isEmpty(bank)) {
				intent.putExtra("checkedcard", bank);
			}
			startActivityForResult(intent, Constants.REQUEST_CODE);
			return;
		}
		if (arg0 == vZhiFuBao) {
			vZhiFuBaoImg.setVisibility(View.VISIBLE);
			vZhiFuBaoWebImg.setVisibility(View.INVISIBLE);
			vCardImg.setVisibility(View.INVISIBLE);
			choice = 2;
			return;
		}
		if (arg0 == vZhiFuBaoWeb) {
			vZhiFuBaoImg.setVisibility(View.INVISIBLE);
			vZhiFuBaoWebImg.setVisibility(View.VISIBLE);
			vCardImg.setVisibility(View.INVISIBLE);
			paytype = "alipay";
			choice = 3;
			return;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == Constants.REQUEST_CODE) {
			if (resultCode == Constants.RESULT_OK) {
				vZhiFuBaoImg.setVisibility(View.INVISIBLE);
				vZhiFuBaoWebImg.setVisibility(View.INVISIBLE);
				vCardImg.setVisibility(View.VISIBLE);

				cardName = data.getStringExtra("cardName");
				bank = data.getStringExtra("cardkey");
				if (!CommonUtil.isEmpty(bank)) {
					vTypeName.setText(cardName);
					paytype = "bank";
				}
			} else {
				if (!CommonUtil.isEmpty(bank)) {
					paytype = "bank";
					vZhiFuBaoImg.setVisibility(View.INVISIBLE);
					vZhiFuBaoWebImg.setVisibility(View.INVISIBLE);
					vCardImg.setVisibility(View.VISIBLE);
				}
			}
		} else if (requestCode == Constants.REQUEST_CODE_110) {
			if (resultCode == Constants.RESULT_OK) {
				saveData();
				saveHistory();
				showMsgDialogWithCallback("感谢您对学校的捐赠！");
			}
		}
	}

	private void saveData() {
		if (dbHelper == null) {
			dbHelper = DBHelper.getInstance(this);
		}
		RecordBean bean = new RecordBean();
		bean.setAddress(address);
		bean.setAmount(amount);
		bean.setBank(bank);
		bean.setCardName(cardName);
		bean.setCellphone(cellphone);
		bean.setComment(comment);
		bean.setCompany(company);
		bean.setDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
				.format(Calendar.getInstance().getTime()));
		bean.setEmail(email);
		bean.setGender(gender);
		bean.setIs_alumni(is_alumni);
		bean.setIs_anonymous(is_anonymous);
		bean.setPaytype(paytype);
		bean.setPostcode(postcode);
		bean.setProject(item);
		bean.setTel(tel);
		bean.setUsername(username);
		dbHelper.saveRecords(bean);
		String fastDonateFlag = dbHelper
				.getValueByKey(BeanPropEnum.CommonProp.fastDonateFlag
						.toString());
		if (CommonUtil.isEmpty(fastDonateFlag) && "1".equals(fastDonateFlag)) {
			bean.setAmount(null);
		}
		dbHelper.saveLastRecord(bean);
	}

	private String refNo = "";

	private void doNextStep() {
		refNo = AliPayUtil.getOutTradeNo();
		if (choice == 1) {
			// doCardPay();
		} else if (choice == 3) {
			doAliWapPay();
		} else {
			doAliClientPay();
		}
	}

	private void doAliClientPay() {
		if (progressDialog == null) {
			progressDialog = CustomProgressDialog.show(this, "正在加载...", false);
		}
		if (!progressDialog.isShowing()) {
			progressDialog.setMsg("正在加载...");
			progressDialog.show();
		}
		AliPayUtil aliPayUtil = new AliPayUtil(this, mHandler);
		// TODO refno
		// aliPayUtil.pay("手机基金会捐赠",username+ "为"+item+"捐赠", "0.01", refNo);
		aliPayUtil.pay("测试", username + "测试内容", "0.01", refNo);
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Constants.ALI_SDK_PAY_FLAG: {
				if (progressDialog != null && progressDialog.isShowing()) {
					progressDialog.dismiss();
				}
				PayResult payResult = new PayResult((String) msg.obj);
				// 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
				String resultInfo = payResult.getResult();
				String resultStatus = payResult.getResultStatus();
				// 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
				if (TextUtils.equals(resultStatus, "9000")) {
					// TODO OK
					saveData();
					saveHistory();
					showMsgDialogWithCallback("感谢您对学校的捐赠！");
				} else {
					// 判断resultStatus 为非“9000”则代表可能支付失败
					// “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
					if (TextUtils.equals(resultStatus, "8000")) {
						Toast.makeText(PayRouteActivity.this, "支付结果确认中",
								Toast.LENGTH_SHORT).show();

					} else {
						// 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
						Toast.makeText(PayRouteActivity.this, "支付未完成",
								Toast.LENGTH_SHORT).show();

					}
				}
				break;
			}
			default:
				break;
			}
		}
	};

	private void doAliWapPay() {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
		Intent intent = new Intent(this, AliPayWapActivity.class);
		// TODO get refno
		intent.putExtra("refno", refNo);
		startActivity(intent);
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
			String temp = "捐赠了" + amount + "元，积小流，成江河，善于回报。";
			if (price >= 10000 && price < 50000) {
				temp = "很大方的捐赠了" + amount + "元，积小流，成江河，敢于回报，母校因你而自豪。";
			} else if (price >= 50000) {
				temp = "非常大方的捐赠了" + amount + "元，此时此刻，母校的各位学子，除了感谢，我想再不会有其他。";
			}

			bean.setContent(bean.getDay() + "日，为" + item + temp);
			dbHelper.saveHistory(bean);
		} catch (Exception e) {
		}
	}
}
