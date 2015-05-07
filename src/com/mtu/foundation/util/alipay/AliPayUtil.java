package com.mtu.foundation.util.alipay;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.alipay.sdk.app.PayTask;
import com.mtu.foundation.util.Constants;

/**
 * Created by jov on 2015/2/14.
 */
public class AliPayUtil {
    /**
     * call alipay sdk pay. 调用SDK支付
     */
    private Context context;
    private Handler mHandler;

    public AliPayUtil(Context context, Handler handler) {
        this.context = context;
        this.mHandler = handler;
    }

    public void pay(String goodsName, String goodsDesc, String goodsPrice, String refno) {
        // 订单
        String orderInfo = getOrderInfo(goodsName, goodsDesc, goodsPrice, refno);

        // 对订单做RSA 签名
        String sign = sign(orderInfo);
        try {
            // 仅需对sign 做URL编码
            sign = URLEncoder.encode(sign, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // 完整的符合支付宝参数规范的订单信息
        final String payInfo = orderInfo + "&sign=\"" + sign + "\"&"
                + getSignType();

        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask((Activity) context);
                // 调用支付接口，获取支付结果
                String result = alipay.pay(payInfo);

                Message msg = new Message();
                msg.what = Constants.ALI_SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    /**
     * sign the order info. 对订单信息进行签名
     *
     * @param content 待签名订单信息
     */
    public static String sign(String content) {
        return RSA.sign(content, RSA_PRIVATE, "UTF-8");
    }

    /**
     * create the order info. 创建订单信息
     */
    public String getOrderInfo(String subject, String body, String price, String refno) {
        // 签约合作者身份ID
        String orderInfo = "partner=" + "\"" + PARTNER + "\"";

        // 签约卖家支付宝账号
        orderInfo += "&seller_id=" + "\"" + SELLER + "\"";

        // 商户网站唯一订单号
        orderInfo += "&out_trade_no=" + "\"" + refno + "\"";

        // 商品名称
        orderInfo += "&subject=" + "\"" + subject + "\"";

        // 商品详情
        orderInfo += "&body=" + "\"" + body + "\"";

        // 商品金额
        orderInfo += "&total_fee=" + "\"" + price + "\"";

        // 服务器异步通知页面路径
        orderInfo += "&notify_url=" + "\"" + "http://notify.msp.hk/notify.htm"
                + "\"";

        // 服务接口名称， 固定值
        orderInfo += "&service=\"mobile.securitypay.pay\"";

        // 支付类型， 固定值
        orderInfo += "&payment_type=\"1\"";

        // 参数编码， 固定值
        orderInfo += "&_input_charset=\"utf-8\"";

        // 设置未付款交易的超时时间
        // 默认30分钟，一旦超时，该笔交易就会自动被关闭。
        // 取值范围：1m～15d。
        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
        // 该参数数值不接受小数点，如1.5h，可转换为90m。
        orderInfo += "&it_b_pay=\"30m\"";

        // extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
        // orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

        // 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
        //orderInfo += "&return_url=\"m.alipay.com\"";

        // 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
        // orderInfo += "&paymethod=\"expressGateway\"";

        return orderInfo;
    }

    /**
     * get the out_trade_no for an order. 生成商户订单号，该值在商户端应保持唯一（可自定义格式规范）
     */
    public static String getOutTradeNo() {
        SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss",
                Locale.getDefault());
        Date date = new Date();
        String key = format.format(date);

        Random r = new Random();
        key = key + r.nextInt();
        key = key.substring(0, 15);
        return key;
    }

    /**
     * get the sign type we use. 获取签名方式
     */
    public String getSignType() {
        return "sign_type=\"RSA\"";
    }

    //商户PID
    public static final String PARTNER = "2088711150346785";
    //商户收款账号
    public static final String SELLER = "wenjuan.guan@supwisdom.com";
    //商户私钥，pkcs8格式
    public static final String RSA_PRIVATE = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAOAP0mEvYCibofJx" +
            "HTdPUK0kPXdqY0tTvfIMt9lSaU7nIPTAhSAsw20OaGiDCosp3I0RObv2sroJ4LLQ" +
            "ozLKzxIhN6Q4wur3a+jAU3HXsedYmPksMQRpP/JA010T0qMmsvvOPqfAswcueXzw" +
            "wAsNinIpqVx+QXYvjdbg2WOnPbgvAgMBAAECgYEAnglq2ROCXoIPgyJXehiejdD+" +
            "7ciDSrFBS4W+8zEWiAmsDBXykBQirzw3Vmf/OtMG9hUNRM+nlmK7M0THp163F/8b" +
            "VYJg7Hcl8gTSq0RtzYl7TgrhJ5XIu/xbBoGCKALhovuCKdreuHEnbBVqgVbjZrBS" +
            "d+Tu4wHeOz+Ou/s7+jECQQD9zlSJxm9AbIVSOLBoZtiHRGiK/DtAAJAA+GrUOzAS" +
            "kK7Lea+qRGkNVQXsb39Fk3QtB/t8RN1veXvHRnh/mtJJAkEA4f+rBRt4TvkAg838" +
            "TfQJlo+CiL5BlSDqDg/XesvLBLTrAd2qyUb5Wq3GpRxmfZ9WRCHeLDuWcqN3S7Gu" +
            "DYY2twJAODla9OJmhskDh8FTIu2VjfGTjyZtIbJ+NBjT4YvzDEnMzvp39aoN84wg" +
            "Mc5JTWpq1AbuqQrAWw94Yh60VuA/MQJBAJGN2ba7X3v1cThylTobn4VBvn+VkaWb" +
            "gkM2PsDOul24q9cSzik+NeEKJPM4XGYyFhxhd7cjVZ1V3MJ6mDALrfcCQQDAUCy0" +
            "ZqZRit9jk6+8eQnXnNI5ffMWUJAR2umJ/7Uy7aCUZRUizWSUPqa+aSQemBlHbW+F" +
            "X9eIeO4TcX+jjiaA";
    //支付宝公钥
    public static final String RSA_PUBLIC = "";
    public static final String CHARSET = "UTF-8";
    public static final String SIGN_TYPE = "0001";
    public static final String ALIPAY_WAP_URL = "http://wappaygw.alipay.com/service/rest.htm";

    public static String buildRequestMysign(Map<String, String> sPara) {
        String prestr = AlipayCore.createLinkString(sPara); //把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
        String mysign = "";
        mysign = RSA.sign(prestr, RSA_PRIVATE, CHARSET);
        return mysign;
    }

    public static String getAliPayWAPOrderBodyInfor(String subject, String price, String refNo, String SUCCESS_URL, String NOTIFY_URL, String userid) {
        String orderInfor = "<direct_trade_create_req><su" +
                "bject>" + subject +
                "</subject><out_trade_no>" + "1282889689801" + "</out_trade_no><total_fee>" + price + "</total_fee><seller_account_name>" + AliPayUtil.SELLER
                + "</seller_account_name><call_back_url>" + SUCCESS_URL + "</call_back_url><notify_url>"
                + NOTIFY_URL + "</notify_url><out_user>" + userid + "</out_user><pay_expire>3600</pay_expire></direct_trade_create_req>";
        return orderInfor;
    }

    public static Map<String, String> getAccessTokenParamsMap() {
        Map<String, String> sParaTemp = new HashMap<String, String>();
        sParaTemp.put("service", "alipay.wap.trade.create.direct");
        sParaTemp.put("partner", AliPayUtil.PARTNER);
        sParaTemp.put("sec_id", AliPayUtil.SIGN_TYPE);
        sParaTemp.put("format", "xml");
        sParaTemp.put("v", "2.0");
        sParaTemp.put("req_id","1");
        return sParaTemp;
    }

    public static Map<String, String> getBillParamsMap() {
        Map<String, String> sParaTemp = new HashMap<String, String>();
        sParaTemp.put("service", "alipay.wap.auth.authAndExecute");
        sParaTemp.put("partner", AliPayUtil.PARTNER);
        sParaTemp.put("sec_id", AliPayUtil.SIGN_TYPE);
        sParaTemp.put("format", "xml");
        sParaTemp.put("v", "2.0");
        return sParaTemp;
    }
    
}
