package com.money.Service.Wxservice;

import com.google.gson.reflect.TypeToken;
import com.money.config.Config;
import org.apache.http.HttpException;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import until.GsonUntil;
import until.httpClient.HttpResultType;
import until.httpClient.MoneyHttpProtocolHandler;
import until.httpClient.MoneyHttpRequest;
import until.httpClient.MoneyHttpResponse;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import until.memcach.MemCachService;
/**
 * Created by liumin on 16/2/18.
 * 微信js-sdk用到的服务
 */

@Service("Wxservice")
public class Wxservice {

    @Qualifier("moneyHttpProtocolHandler")
    @Autowired
    private MoneyHttpProtocolHandler client;

    /**
     * 获得WXAPItoken 有效期只有7200S
     *
     * @return
     */
     String getWxToken() throws IOException, HttpException {

        String token = MemCachService.MemCachgGet("WXJSSDK::Token");

        if(token !=null && !token.equals("")){
            return token;
        }

        String requestUrl = "https://api.weixin.qq.com/cgi-bin/token";

        MoneyHttpRequest moneyHttpRequest = new MoneyHttpRequest(HttpResultType.STRING );
        moneyHttpRequest.setUrl(requestUrl);
        List<NameValuePair> parmer = new ArrayList();
        parmer.add( new BasicNameValuePair( "grant_type","client_credential" ));
        parmer.add( new BasicNameValuePair( "appid", Config.WXAPPID));
        parmer.add( new BasicNameValuePair( "secret",Config.WXAPPSECRET ));
        moneyHttpRequest.setParameters( parmer );
        MoneyHttpResponse moneyHttpResponse = client.execute(moneyHttpRequest, "", "");
        String result = moneyHttpResponse.getStringResult();

        Map<String,String> map = GsonUntil.jsonToJavaClass( result,new TypeToken<Map<String,String>>(){}.getType());
        if( map == null ){
            return null;
        }

        MemCachService.InsertValueWithTime( "WXJSSDK::Token",(int)(3600*1.5),map.get("access_token") );

        return map.get("access_token");
    }

    /**
     * 获得WXTicket 并缓存Ticket 有效期只有7200S
     *
     * @return
     */
    public String getWxTicket() throws IOException, HttpException {

        String ticket = MemCachService.MemCachgGet("WXJSSDK::Ticket");

        if(ticket !=null && !ticket.equals("")){
            return ticket;
        }

        String requestUrl = "https://api.weixin.qq.com/cgi-bin/ticket/getticket";

        MoneyHttpRequest moneyHttpRequest = new MoneyHttpRequest(HttpResultType.STRING );
        moneyHttpRequest.setUrl(requestUrl);
        List<NameValuePair> parmer = new ArrayList();
        parmer.add( new BasicNameValuePair( "access_token",getWxToken() ));
        parmer.add( new BasicNameValuePair( "type","jsapi" ));
        moneyHttpRequest.setParameters( parmer );
        MoneyHttpResponse moneyHttpResponse = client.execute(moneyHttpRequest, "", "");
        String result = moneyHttpResponse.getStringResult();

        Map<String,String> map = GsonUntil.jsonToJavaClass( result,new TypeToken<Map<String,String>>(){}.getType());
        if( map == null ){
            return null;
        }

        MemCachService.InsertValueWithTime( "WXJSSDK::Ticket",(int)(3600*1.5),map.get("ticket") );

        return map.get("ticket");
    }

    /**
     * 签名
     */
    public String sign(String jsapi_ticket,String nonce_str,String timestamp, String url) {

        String string1;
        String signature;

        //注意这里参数名必须全部小写，且必须有序
        string1 = "jsapi_ticket=" + jsapi_ticket +
                "&noncestr=" + nonce_str +
                "&timestamp=" + timestamp +
                "&url=" + url;
        try
        {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(string1.getBytes("UTF-8"));
            signature = byteToHex(crypt.digest());
        }
        catch (NoSuchAlgorithmException e)
        {
            return "";
        }
        catch (UnsupportedEncodingException e)
        {
            return "";
        }

        return signature;
    }

    private String byteToHex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash)
        {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }

    public String create_nonce_str() {
        return UUID.randomUUID().toString();
    }

    public String create_timestamp() {
        return Long.toString(System.currentTimeMillis() / 1000);
    }

}
