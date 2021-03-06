package until;

import com.money.Service.Wallet.WalletService;
import com.money.config.Config;
import com.pingplusplus.Pingpp;
import com.pingplusplus.exception.APIConnectionException;
import com.pingplusplus.exception.APIException;
import com.pingplusplus.exception.AuthenticationException;
import com.pingplusplus.exception.InvalidRequestException;
import com.pingplusplus.model.Charge;
import com.pingplusplus.model.Event;
import com.pingplusplus.model.Transfer;
import com.pingplusplus.model.Webhooks;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * ping++ 支付服务
 * <p>User: 刘旻
 * <p>Date: 15-7-13
 * <p>Version: 1.0
 */

@Component
public class PingPlus {

    static PingPlus pingPlus;

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(PingPlus.class);

    PingPlus() {
        pingPlus = this;
    }

    @Autowired
    WalletService walletService;

    public static String CreateChargeParams(String UserID, int amount,
                                            String channel, String client_ip,
                                            String subject, String body,
                                            String order_no,String openid,int PayType) {
        Pingpp.apiKey = Config.PINGPLUSLIVEID;
        Map<String, Object> chargeParams = new HashMap();
        chargeParams.put("order_no", order_no);
        chargeParams.put("amount", amount);
        Map<String, String> app = new HashMap();
        app.put("id", Config.PINGPLUSLAPPID);
        chargeParams.put("app", app);
        chargeParams.put("channel", channel);
        chargeParams.put("currency", "cny");
        chargeParams.put("client_ip", "192.168.0.161");
        chargeParams.put("subject", subject);
        chargeParams.put("body", body);
        Map<String, String> initialMetadata = new HashMap();
        initialMetadata.put("UserID", UserID);
        initialMetadata.put("PayType",Integer.toString(PayType));
        chargeParams.put("metadata", initialMetadata);
        Map<String, String> ExtraMetadata = new HashMap();
        switch (channel) {
            case "alipay_wap": {
                ExtraMetadata.put("success_url", "http://www.baidu.com");
                ExtraMetadata.put("cancel_url", "http://www.baidu.com");
            }
            break;

            case "wx_pub": {
                ExtraMetadata.put("limit_pay", "no_credit");
                ExtraMetadata.put("open_id", openid);
            }
            break;
        }

        chargeParams.put("extra", ExtraMetadata);

        Charge charge;
        try {
            charge = Charge.create(chargeParams);
        } catch (AuthenticationException e) {
            LOGGER.error("ping++创建参数错误:", e);
            return null;
        } catch (InvalidRequestException e) {
            LOGGER.error("ping++创建参数错误:", e);
            return null;
        } catch (APIConnectionException e) {
            LOGGER.error("ping++创建参数错误:", e);
            return null;
        } catch (APIException e) {
            LOGGER.error("ping++创建参数错误:", e);
            return null;
        }

        String a = charge.toString();

        return charge.toString();
    }

    public static String CreateTransferMap(int amount, String opneId, String userId, String Orderno, String BatchId) throws UnsupportedEncodingException, InvalidRequestException, APIException, APIConnectionException, AuthenticationException {
        Pingpp.apiKey = Config.PINGPLUSLIVEID;
        Map<String, Object> transferMap = new HashMap();
        transferMap.put("amount", amount * 100);
        transferMap.put("currency", "cny");
        transferMap.put("type", "b2c");
        transferMap.put("order_no", Orderno);
        transferMap.put("channel", "wx_pub");
        transferMap.put("recipient", opneId);
        transferMap.put("description", BatchId);
        Map<String, String> app = new HashMap();
        app.put("id", Config.PINGPLUSLAPPID);
        transferMap.put("app", app);

        Transfer transfer = Transfer.create(transferMap);

        if (transfer != null) {
            return transfer.toString();
        } else {
            LOGGER.error("ping++_CreateTransferMap transfer is null");
            return null;
        }

    }


    public static void Webhooks(HttpServletRequest request,
                                HttpServletResponse response) throws Exception {
        // 获得 http body 内容
        BufferedReader reader = request.getReader();
        StringBuffer buffer = new StringBuffer();
        String string;
        while ((string = reader.readLine()) != null) {
            buffer.append(string);
        }
        reader.close();
        // 解析异步通知数据
        Event event = Webhooks.eventParse(buffer.toString());
        if ("charge.succeeded".equals(event.getType())) {
            String body = event.toString();
            if (pingPlus.walletService.RechargeWalletService(body) == Config.SENDCODE_SUCESS) {
                response.setStatus(200);
            } else {
                LOGGER.error("Webhooks_charge.succeeded_error");
                response.setStatus(500);
            }
        } else if ("transfer.succeeded".equals(event.getType())) {
            String body = event.toString();
            if (pingPlus.walletService.TranferLinesService(body) == Config.SENDCODE_SUCESS) {
                response.setStatus(200);
            } else {
                LOGGER.error("Webhooks_transfer.succeeded_error");
                response.setStatus(500);
            }
        } else {
            response.setStatus(500);
        }

    }


    public static void Test() {
        Pingpp.apiKey = "sk_test_aD4qnHSWTCmPvX1un5rXfjPK";

        Map<String, Object> chargeParams = new HashMap();
        chargeParams.put("order_no", "123456789");
        chargeParams.put("amount", 100);
        Map<String, String> app = new HashMap();
        app.put("id", "app_jvXfzPe9e90GeLWz");
        chargeParams.put("app", app);
        chargeParams.put("channel", "upacp");
        chargeParams.put("currency", "cny");
        chargeParams.put("client_ip", "127.0.0.1");
        chargeParams.put("subject", "LongyanTest");
        chargeParams.put("body", "MoneyServerTest");

        try {
            Charge.create(chargeParams);
        } catch (AuthenticationException | InvalidRequestException | APIConnectionException | APIException e) {
            e.printStackTrace();
        }
    }

    private static String urlEncode(String str) throws UnsupportedEncodingException {
        return str == null ? null : URLEncoder.encode(str, "UTF-8");
    }
}
