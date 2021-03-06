package com.money.Service.alipay;

import com.money.Service.ServiceBase;
import com.money.Service.ServiceInterface;
import com.money.Service.alipay.config.AlipayConfig;
import com.money.Service.alipay.util.AlipayCore;
import com.money.Service.alipay.util.AlipaySubmit;
import com.money.Service.alipay.util.UtilDate;
import com.money.model.AlitransferModel;
import org.apache.http.HttpException;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by happysky on 15-9-23.
 */
@Service("PayService")
public class PayService extends ServiceBase implements ServiceInterface {

    /**
     * 支付宝提供给商户的服务接入网关URL(新)
     */
    private static final String ALIPAY_GATEWAY_NEW = "https://mapi.alipay.com/gateway.do?";

/*    @Autowired
    private HttpProtocolHandler client;*/

    public String requestTransaction(List<AlitransferModel> dataList,String notify_url) throws IOException, HttpException {
        if (dataList == null || dataList.size() == 0) {
            return "无支付宝提现记录";
        }

        //待请求参数数组
        Map<String, String> paramsMap = buildParam(dataList,notify_url);
        return buildForm(paramsMap);
    }


    public Map<String, String> buildParam(List<AlitransferModel> dataList,String notify_url) {
        HashMap<String, String> map = new HashMap<>();

        // j接口名称
        map.put("service", "batch_trans_notify");

        // 合作身份者
        map.put("partner", AlipayConfig.partner);

        // 参数编码字符集
        map.put("_input_charset", AlipayConfig.input_charset);

        // 服务器异步通知页面
        map.put("notify_url", notify_url);

        // 付款账号名
        map.put("account_name", AlipayConfig.account_name);

        // 付款详细数据
        map.put("detail_data", buildDetailData(dataList));

        // 批量付款批次号
        map.put("batch_no", UtilDate.getOrderNum());

        // 付款总笔数
        map.put("batch_num", String.valueOf(dataList.size()));

        // 付款总金额
        DecimalFormat df = new DecimalFormat("#####0.00");
        String sumPriceString = df.format(calcSumPrice(dataList));
        map.put("batch_fee", sumPriceString);

        // 付款账号
        map.put("email", AlipayConfig.account_email);

        // 支付日期
        map.put("pay_date", UtilDate.getDate());

        // 付款账号别名
        map.put("buyer_account_name", AlipayConfig.account_email);

        // 业务扩展参数
        map.put("extend_ param", "");

        return map;
    }

    private String buildForm(Map<String, String> map) {
        return AlipaySubmit.buildRequest(map, "post", "haha");
    }

    private String buildPostString(Map<String, String> map) {
        //除去数组中的空值和签名参数
        Map<String, String> sPara = AlipayCore.paraFilter(map);
        //生成签名结果
        String mysign = AlipaySubmit.buildRequestMysign(sPara);

        //签名结果与签名方式加入请求提交参数组中
        sPara.put("sign", mysign);
        sPara.put("sign_type", AlipayConfig.sign_type);

        return AlipayCore.createLinkString(sPara);
    }

    public void foo2(Map<String, String> map) {
        map.get("");
        map.get("");
        map.get("");
        map.get("");
        map.get("");
        map.get("");
        map.get("");
        map.get("");
        map.get("");
        map.get("");
        map.get("");
        map.get("");
    }

    public String buildDetailData(List<AlitransferModel> dataList) {
        StringBuffer dataString = new StringBuffer();
        for (int i = 0; i < dataList.size() - 1; i++) {
            dataString.append(dataList.get(i).toAlipayTransFormat() + "|");
        }
        dataString.append(dataList.get(dataList.size() - 1).toAlipayTransFormat());
        return dataString.toString();
    }

    public double calcSumPrice(List<AlitransferModel> dataList) {
        double value = 0.0f;
        for (AlitransferModel data : dataList) {
            value += data.getLines();
        }
        return value;
    }

    /**
     * MAP类型数组转换成NameValuePair类型
     *
     * @param map MAP类型数组
     * @return NameValuePair类型数组
     */
    private static List<NameValuePair> generateNameValuePair(Map<String, String> map, String charset) throws Exception {
        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
        int i = 0;
        for (Map.Entry<String, String> entry : map.entrySet()) {
            nameValuePair.add(new BasicNameValuePair(entry.getKey(), URLEncoder.encode(entry.getValue(), charset)));
        }
        return nameValuePair;
    }

    /**
     * 解析支付宝通知的付款结果信息
     *
     * @param Param
     * @return
     */
    public static List<List<String>> ParsingNotifyParam(String Param) {
        List<List<String>> result = new ArrayList<List<String>>();
        String[] parpaminfo = Param.split("\\|");
        for (int i = 0; i < parpaminfo.length; i++) {
            List<String> childParam = Arrays.asList(parpaminfo[i].split("\\^"));
            result.add(childParam);
        }

        return result;
    }
}
