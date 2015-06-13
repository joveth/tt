package com.mtu.foundation;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.chinamworld.electronicpayment.IRemoteService;
import com.chinamworld.electronicpayment.json.BOCPAYUtil;
import com.mtu.foundation.bean.HistoryItemBean;
import com.mtu.foundation.db.BeanPropEnum;
import com.mtu.foundation.db.DBHelper;
import com.mtu.foundation.db.RecordBean;
import com.mtu.foundation.net.httpjersey.NetworkHandler;
import com.mtu.foundation.util.CommonUtil;
import com.mtu.foundation.util.Constants;
import com.mtu.foundation.util.alipay.AliPayUtil;
import com.mtu.foundation.util.alipay.PayResult;
import com.mtu.foundation.util.alipay.util.UtilDate;
import com.mtu.foundation.view.CustomProgressDialog;

public class PayRouteActivity extends BaseActivity {
	private NetworkHandler networkHandler;
	private String item, amount, comment, username, gender, is_alumni, email,
			tel, cellphone, address, postcode, company, is_anonymous,
			paytype = "alipay", bank, cardName;

	private TextView textView, vTypeName, vItem;
	private View vNextStep, vZhiFuBao, vZhiFuBaoImg, vZhiFuBaoWeb,
			vZhiFuBaoWebImg, vCardImg, vRechargeType, vZyImg, vZy;

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
		vZy = findViewById(R.id.recharge_type_zy);
		vZy.setOnClickListener(this);
		vZyImg = findViewById(R.id.recharge_zy_img);
		bindService(new Intent(IRemoteService.class.getName()),
				serviceConnection, Context.BIND_AUTO_CREATE);
		initData();
	}

	// 服务，服务名固定不变

	IRemoteService mIRemoteService;

	// 处理支付结果Handler

	private Handler payHandle = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			super.handleMessage(msg);

			// 处理支付结果，刷新页面

			Toast.makeText(PayRouteActivity.this, "支付结果：  " + msg.obj,

			Toast.LENGTH_LONG).show();

		}

	};

	private ServiceConnection serviceConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {

			Log.d("info", "---- onServiceDisconnected-- ");

		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {

			mIRemoteService = IRemoteService.Stub.asInterface(service);

		}

	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#finish()
	 */
	protected void onDestroy() {
		super.onDestroy();
		unbindService(serviceConnection);

	};

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
			vZyImg.setVisibility(View.INVISIBLE);
			choice = 2;
			return;
		}
		if (arg0 == vZhiFuBaoWeb) {
			vZhiFuBaoImg.setVisibility(View.INVISIBLE);
			vZhiFuBaoWebImg.setVisibility(View.VISIBLE);
			vCardImg.setVisibility(View.INVISIBLE);
			vZyImg.setVisibility(View.INVISIBLE);
			paytype = "alipay";
			choice = 3;
			return;
		}
		if (arg0 == vZy) {
			vZyImg.setVisibility(View.VISIBLE);
			vZhiFuBaoImg.setVisibility(View.INVISIBLE);
			vZhiFuBaoWebImg.setVisibility(View.INVISIBLE);
			vCardImg.setVisibility(View.INVISIBLE);
			choice = 4;
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
				vZyImg.setVisibility(View.INVISIBLE);
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
					vZyImg.setVisibility(View.INVISIBLE);
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
		} else if (choice == 4) {
			doZyPay();
		} else {
			doAliClientPay();
		}
	}

	private void doZyPay() {
		startAPK();
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

	/**
	 * 
	 * 
	 * 
	 * @param packageName
	 * 
	 *            订单信息
	 */

	public void startAPK() {

		try {
			if(!BOCPAYUtil.getInstanse().aboutMapQuery(PayRouteActivity.this)) {
				return;
			}
			// 封装订单信息
			//封装订单信息

			/*final String businessResult = "{\"result\":{\"custTranId\":\""

						+ custTranId

						+ "\", \"tranType\":\""

						+ "01"

						+ "\", \"merchantNo\":\""

						+ merchantNum

						+ "\", \"orderNo\":\""

						+ orderNo

						+ "\", \"curCode\":\""

						+ curCode

						+ "\",\"orderAmount\":\""

						+ orderAmount

						+ "\", \"orderTime\":\"20130321132413\", \"orderNote\":\""

						+ orderNote

						+ "\", \"orderUrl\":\"http://22.11.101.182:8087/SimMerchant/showPara.do\",  \"mNormalData\":\"{c=123, d=456}\", \"signature\":\"xx\"},\"payType\"=\"0\"}";*/

			/*{"result":{"custTranId":"10000779855655a50066508119", "tranType":"01", "merchantNo":

			 * "104110082201632", "orderNo":"201311281714159672", "curCode":"001",

			 * "orderAmount":"200.00", "orderTime":"20131125142425","orderNote":"短信标题", 

			 * "orderUrl":"http://test.lashoupay.com/lashoupay/wapPayCallBackAction.action?bankid=109$$$206$$$10062",

			 * "signature":"MIIEFQYJKoZIhvcNAQcCoIIEBjCCBAICAQExCzAJBgUrDgMCGgUAMAsGCSqGSIb3DQEHAaCCAw8wggMLMIIB86ADAgECAgUQEQOUMDANBgkqhkiG9w0BAQUFADAhMQswCQYDVQQGEwJDTjESMBAGA1UEChMJQ0ZDQSBPQ0ExMB4XDTEzMDkxMjAxMTY1MVoXDTE1MDkxMjAxMTY1MVowZDELMAkGA1UEBhMCY24xEjAQBgNVBAoTCUNGQ0EgT0NBMTEMMAoGA1UECxMDQk9DMRgwFgYDVQQLEw9Pcmdhbml6YXRpb25hbDIxGTAXBgNVBAMTEDk1NTY2U1owMDAwMDA5NjMwgZ8wDQYJKoZIhvcNAQEBBQADgY0AMIGJAoGBAKmTIxhgKzH8XeBUKcnKp6gdBVbyo3O9nebJGYBENWBXyIw4d68iK+RPAY89zbipmftHq1eiThmtOwrllAktJD9uxDeyFFk6cv0B/TVAY6cWrs+HLiA+Iz9sqGP82sBRnosf6oyvzgng7RuDcF3cKkRVG+caUVds1rbI3N1xVl2dAgMBAAGjgYowgYcwHwYDVR0jBBgwFoAU0dvpiILl3RqPTKoAjL588qsb9tkwOAYDVR0fBDEwLzAtoCugKYYnaHR0cDovL2NybC5jZmNhLmNvbS5jbi9SU0EvY3JsMTE2OTAuY3JsMAsGA1UdDwQEAwIGwDAdBgNVHQ4EFgQU5vYlbttojl/qEk5nWjlN7Ehd/9UwDQYJKoZIhvcNAQEFBQADggEBAGVAqnO3RW2Oo+HUZPCt8NZnYbv5/+MCx3RXvyBeAn0vr8bgtjCCWhpiB2gucpo+Gw6O93VVvDy0Hw+F0CZ70KLE5Z6SPD6bFW6j3Ia+nQGUF8S+L0MqS4/tNPhLTpz1pGpBqxn1yxx4DIQam8i4kMmsq2geVQixtWB1Y4qDVzj2+PK80rxWWI+H3aCGVBFJAkoHfJXZKqa3kxYHeyHSNgykZMWiYgM1kUVl9FYG8Qm5hMBYnPN0mj0x0l0LlSSJr58IBAaqOukFq6mDa8yJBb+hVKqNWZBKYF8qJt4KY66aFeXFAZqDKn+ibUHABnPt+TJJ0BpGNwOAaoIPlkOnaAYxgc8wgcwCAQEwKjAhMQswCQYDVQQGEwJDTjESMBAGA1UEChMJQ0ZDQSBPQ0ExAgUQEQOUMDAJBgUrDgMCGgUAMA0GCSqGSIb3DQEBAQUABIGAGZSpevR+DDgMCh/VVx3XwNnAvcXRyJC+18saiE9avAbgy+73guYph99568R1M02lLhZkteGND9Msc+XbKMfLCf901d0twQ0IQxTSmZ7QLDTvKMn7JS7sb3sXUK6YnQxOijSzJ5wQxMosicNO4Kw2D6Wml2tnqnj3ouhs7F0mdso="},"payType":"0"}

			 */

			/*businessResult===={"result":{"custTranId":"2013112900111726", "tranType":"01", 

			 * "merchantNo":"104320148141015",

			 *  "orderNo":"2013112900111726",

			 *   "curCode":"001",

			 *   "orderAmount":"10", 

			 *   "orderTime":"20130321132413", 

			 *   "orderNote":"广电项目缴费", 

			 *   "orderUrl":"http://122.96.58.36:7002/merchant/person/pc/app/toBocMessage?bankDepositId=8662",  

			 *   "mNormalData":"{c=123, d=456}",

			 *    "signature":"MIIE7gYJKoZIhvcNAQcCoIIE3zCCBNsCAQExCzAJBgUrDgMCGgUAMAsGCSqGSIb3DQEHAaCCA58w

				ggObMIICg6ADAgECAhAtnGBp8aVxrpzhRo5pddgSMA0GCSqGSIb3DQEBBQUAMF0xCzAJBgNVBAYT

				AmNuMRYwFAYDVQQKEw1iYW5rIG9mIGNoaW5hMRAwDgYDVQQIEwdiZWlqaW5nMRAwDgYDVQQHEwdi

				ZWlqaW5nMRIwEAYDVQQDEwlib2NuZXQgY2EwHhcNMTMwNjA2MDkxODQ2WhcNMTgwNjA1MDkxODQ2

				WjCBuTELMAkGA1UEBhMCQ04xFjAUBgNVBAoTDUJBTksgT0YgQ0hJTkExgZEwgY4GA1UEAx6Bhmxf

				gs93AV5/dTVnCX6/T+Fgb39RftyAoU79ZwmWUFFsU/gAXwAzADMAMQBhADkAYwA5ADAAOAAxAGMA

				ZgA0ADUAMwAzAGMANQA3ADgANgAzAGEANwA0ADEAOABlADkANQA3AGMAMABiADEANAAyAGEANwBj

				AF+WSGimWh8AXwAzADIAMgA4MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDpzF6lljY9t6PS

				FoHGRuYui7rZhXtqISh2afmIaHgBg2SRgmndPJ3dj8+yUoAvi44RvbZJ7fACJz3U8DzPUL6kIDt9

				ew5LLoWv55JBp217LFnNwPJ0EHJQduPsCFbtFj2nmNUyJRwYM/L9Ea0At3vjaunQ6R0+Txf5ITuX

				zhTXNwIDAQABo34wfDAfBgNVHSMEGDAWgBQmar81qyOZaxvwUDeNenNg2gFSUDAtBgNVHR8EJjAk

				MCKgIKAehhxodHRwOi8vMjYuMTIwLjM2LjMyL2NybDEuY3JsMAsGA1UdDwQEAwIE8DAdBgNVHQ4E

				FgQUXRi254Jv/unha5aBKfEWdvPBGdkwDQYJKoZIhvcNAQEFBQADggEBAAHhqm9JH4Fftijf6M+2

				gopHrEN2JytnrZKi2Ka25kdVRreJw3YIwTbPd84W0r9HoaTYx6VEFkBIAIogdpD3nAYSYHV3a/yD

				ljsd+3rxMSC71C5/A/hFDiteN9w1Qs5jkV1jlLGCdzKo0apGQqIqfismOWc6ZHuokLGYgNxmbUyO

				OBU6gXUvmDz+HDOs2GXX8r2+RYWttfSeP9tTdZBrFyi1cfBpKmc3cDx+z/bDVw6DL6GTs2gXJ4Rg

				IL9jJPk1GW1ZRIa65qxeQB6sIwQz3I3RifWcm3Cxgti4GqszqCddIJajSs9bU0Am07fFaF2HeFkZ

				4QTmISmLARfWIieaVugxggEXMIIBEwIBATBxMF0xCzAJBgNVBAYTAmNuMRYwFAYDVQQKEw1iYW5r

				IG9mIGNoaW5hMRAwDgYDVQQIEwdiZWlqaW5nMRAwDgYDVQQHEwdiZWlqaW5nMRIwEAYDVQQDEwli

				b2NuZXQgY2ECEC2cYGnxpXGunOFGjml12BIwCQYFKw4DAhoFADANBgkqhkiG9w0BAQEFAASBgA9q

				PdVpUnuYTSouuUeKTVoRDbED0NRjfLouU3up5T/XoIQxpPcL0U0rENO8xPdGRNnTaFSYdVPiplzF

				dbQ4/1tz8MPq7FE4UCPsxRdDgo1e/hFmPP5GAqw/x5NWo0MonC27j/DTAp8F2j1GTXxRjEvMavrw

				5IJ9QwZCNUip8ffJ"},"payType"="0"}

*/			

			/*{"result":{"custTranId":"2013112900111726", 

				"tranType":"01", 

				"merchantNo":"104320148141015", 

				"orderNo":"2013112900111726", 

				"curCode":"001","orderAmount":"10", 

				"orderTime":"20130321132413",

				"orderNote":"广电项目缴费", 

				"orderUrl":"http://122.96.58.36:7002/merchant/person/pc/app/toBocMessage?bankDepositId=8662",

				"signature":"MIIE7gYJKoZIhvcNAQcCoIIE3zCCBNsCAQExCzAJBgUrDgMCGgUAMAsGCSqGSIb3DQEHAaCCA58w

				ggObMIICg6ADAgECAhAtnGBp8aVxrpzhRo5pddgSMA0GCSqGSIb3DQEBBQUAMF0xCzAJBgNVBAYT

				AmNuMRYwFAYDVQQKEw1iYW5rIG9mIGNoaW5hMRAwDgYDVQQIEwdiZWlqaW5nMRAwDgYDVQQHEwdi

				ZWlqaW5nMRIwEAYDVQQDEwlib2NuZXQgY2EwHhcNMTMwNjA2MDkxODQ2WhcNMTgwNjA1MDkxODQ2

				WjCBuTELMAkGA1UEBhMCQ04xFjAUBgNVBAoTDUJBTksgT0YgQ0hJTkExgZEwgY4GA1UEAx6Bhmxf

				gs93AV5/dTVnCX6/T+Fgb39RftyAoU79ZwmWUFFsU/gAXwAzADMAMQBhADkAYwA5ADAAOAAxAGMA

				ZgA0ADUAMwAzAGMANQA3ADgANgAzAGEANwA0ADEAOABlADkANQA3AGMAMABiADEANAAyAGEANwBj

				AF+WSGimWh8AXwAzADIAMgA4MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDpzF6lljY9t6PS

				FoHGRuYui7rZhXtqISh2afmIaHgBg2SRgmndPJ3dj8+yUoAvi44RvbZJ7fACJz3U8DzPUL6kIDt9

				ew5LLoWv55JBp217LFnNwPJ0EHJQduPsCFbtFj2nmNUyJRwYM/L9Ea0At3vjaunQ6R0+Txf5ITuX

				zhTXNwIDAQABo34wfDAfBgNVHSMEGDAWgBQmar81qyOZaxvwUDeNenNg2gFSUDAtBgNVHR8EJjAk

				MCKgIKAehhxodHRwOi8vMjYuMTIwLjM2LjMyL2NybDEuY3JsMAsGA1UdDwQEAwIE8DAdBgNVHQ4E

				FgQUXRi254Jv/unha5aBKfEWdvPBGdkwDQYJKoZIhvcNAQEFBQADggEBAAHhqm9JH4Fftijf6M+2

				gopHrEN2JytnrZKi2Ka25kdVRreJw3YIwTbPd84W0r9HoaTYx6VEFkBIAIogdpD3nAYSYHV3a/yD

				ljsd+3rxMSC71C5/A/hFDiteN9w1Qs5jkV1jlLGCdzKo0apGQqIqfismOWc6ZHuokLGYgNxmbUyO

				OBU6gXUvmDz+HDOs2GXX8r2+RYWttfSeP9tTdZBrFyi1cfBpKmc3cDx+z/bDVw6DL6GTs2gXJ4Rg

				IL9jJPk1GW1ZRIa65qxeQB6sIwQz3I3RifWcm3Cxgti4GqszqCddIJajSs9bU0Am07fFaF2HeFkZ

				4QTmISmLARfWIieaVugxggEXMIIBEwIBATBxMF0xCzAJBgNVBAYTAmNuMRYwFAYDVQQKEw1iYW5r

				IG9mIGNoaW5hMRAwDgYDVQQIEwdiZWlqaW5nMRAwDgYDVQQHEwdiZWlqaW5nMRIwEAYDVQQDEwli

				b2NuZXQgY2ECEC2cYGnxpXGunOFGjml12BIwCQYFKw4DAhoFADANBgkqhkiG9w0BAQEFAASBgA9q

				PdVpUnuYTSouuUeKTVoRDbED0NRjfLouU3up5T/XoIQxpPcL0U0rENO8xPdGRNnTaFSYdVPiplzF

				dbQ4/1tz8MPq7FE4UCPsxRdDgo1e/hFmPP5GAqw/x5NWo0MonC27j/DTAp8F2j1GTXxRjEvMavrw

				5IJ9QwZCNUip8ffJ"}

			,"payType":"0"}*/
			final String businessResult = "{\"result\":{\"custTranId\":\""

					+ "2013112900111726"

					+ "\", \"tranType\":\""

					+ "01"

					+ "\", \"merchantNo\":\""

					+ "104320148141015"

					+ "\", \"orderNo\":\""

					+ AliPayUtil.getOutTradeNo()

					+ "\", \"curCode\":\""

					+ "001"

					+ "\",\"orderAmount\":\""

					+ amount

					+ "\", \"orderTime\":\""+UtilDate.getOrderNum()+"\", \"orderNote\":\""

					+ "海事大学基金会捐赠"

					+ "\", \"orderUrl\":\"http://122.96.58.36:7002/merchant/person/pc/app/toBocMessage?bankDepositId=8662\",  \"mNormalData\":\"{c=123, d=456}\", \"signature\":\"MIIE7gYJKoZIhvcNAQcCoIIE3zCCBNsCAQExCzAJBgUrDgMCGgUAMAsGCSqGSIb3DQEHAaCCA58wggObMIICg6ADAgECAhAtnGBp8aVxrpzhRo5pddgSMA0GCSqGSIb3DQEBBQUAMF0xCzAJBgNVBAYTAmNuMRYwFAYDVQQKEw1iYW5rIG9mIGNoaW5hMRAwDgYDVQQIEwdiZWlqaW5nMRAwDgYDVQQHEwdiZWlqaW5nMRIwEAYDVQQDEwlib2NuZXQgY2EwHhcNMTMwNjA2MDkxODQ2WhcNMTgwNjA1MDkxODQ2WjCBuTELMAkGA1UEBhMCQ04xFjAUBgNVBAoTDUJBTksgT0YgQ0hJTkExgZEwgY4GA1UEAx6Bhmxfgs93AV5/dTVnCX6/T+Fgb39RftyAoU79ZwmWUFFsU/gAXwAzADMAMQBhADkAYwA5ADAAOAAxAGMAZgA0ADUAMwAzAGMANQA3ADgANgAzAGEANwA0ADEAOABlADkANQA3AGMAMABiADEANAAyAGEANwBjAF+WSGimWh8AXwAzADIAMgA4MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDpzF6lljY9t6PSFoHGRuYui7rZhXtqISh2afmIaHgBg2SRgmndPJ3dj8+yUoAvi44RvbZJ7fACJz3U8DzPUL6kIDt9ew5LLoWv55JBp217LFnNwPJ0EHJQduPsCFbtFj2nmNUyJRwYM/L9Ea0At3vjaunQ6R0+Txf5ITuXzhTXNwIDAQABo34wfDAfBgNVHSMEGDAWgBQmar81qyOZaxvwUDeNenNg2gFSUDAtBgNVHR8EJjAkMCKgIKAehhxodHRwOi8vMjYuMTIwLjM2LjMyL2NybDEuY3JsMAsGA1UdDwQEAwIE8DAdBgNVHQ4EFgQUXRi254Jv/unha5aBKfEWdvPBGdkwDQYJKoZIhvcNAQEFBQADggEBAAHhqm9JH4Fftijf6M+2gopHrEN2JytnrZKi2Ka25kdVRreJw3YIwTbPd84W0r9HoaTYx6VEFkBIAIogdpD3nAYSYHV3a/yDljsd+3rxMSC71C5/A/hFDiteN9w1Qs5jkV1jlLGCdzKo0apGQqIqfismOWc6ZHuokLGYgNxmbUyOOBU6gXUvmDz+HDOs2GXX8r2+RYWttfSeP9tTdZBrFyi1cfBpKmc3cDx+z/bDVw6DL6GTs2gXJ4RgIL9jJPk1GW1ZRIa65qxeQB6sIwQz3I3RifWcm3Cxgti4GqszqCddIJajSs9bU0Am07fFaF2HeFkZ4QTmISmLARfWIieaVugxggEXMIIBEwIBATBxMF0xCzAJBgNVBAYTAmNuMRYwFAYDVQQKEw1iYW5rIG9mIGNoaW5hMRAwDgYDVQQIEwdiZWlqaW5nMRAwDgYDVQQHEwdiZWlqaW5nMRIwEAYDVQQDEwlib2NuZXQgY2ECEC2cYGnxpXGunOFGjml12BIwCQYFKw4DAhoFADANBgkqhkiG9w0BAQEFAASBgA9qPdVpUnuYTSouuUeKTVoRDbED0NRjfLouU3up5T/XoIQxpPcL0U0rENO8xPdGRNnTaFSYdVPiplzFdbQ4/1tz8MPq7FE4UCPsxRdDgo1e/hFmPP5GAqw/x5NWo0MonC27j/DTAp8F2j1GTXxRjEvMavrw5IJ9QwZCNUip8ffJ\"},\"payType\"=\"0\"}";

			// 调起移动支付

			BOCPAYUtil.bocPay(mIRemoteService, payHandle, businessResult);

		} catch (Exception e) {

			Toast.makeText(this, "未安装应用程序", Toast.LENGTH_LONG).show();

		}

	}
}
