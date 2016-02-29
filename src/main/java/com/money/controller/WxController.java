package com.money.controller;

import com.money.Service.Wxservice.Wxservice;
import org.apache.http.HttpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import until.Coder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by happysky on 15-9-17.
 * 微信公众号接口
 */

@Controller
@RequestMapping("/Wx")
public class WxController {

    @Autowired
    Wxservice wxservice;

    @RequestMapping("/FocusonWx")
    @ResponseBody
    String TestFocusonWx( HttpServletRequest request ) throws Exception {
        String signature = request.getParameter( "signature" );
        String echostr = request.getParameter( "echostr" );
        String timestamp = request.getParameter( "timestamp" );
        String nonce = request.getParameter( "nonce" );

        String token = "longyan";

        String[] trmp = new String[3];
        trmp[0] = token;
        trmp[1] = timestamp;
        trmp[2] = nonce;
        Arrays.sort(trmp);

        String a = "";
        for( int i = 0; i < trmp.length; i++ ){
             a += trmp[i];
        }

        String b = new String(Coder.encryptSHA1(a.getBytes()));

        if( b.equals( signature ) ){
            return echostr;
        }

        return echostr;

    }

    /**
     * 微信SDK签名
     */
    @RequestMapping(value = "/WxSign",params = {"timestamp","nonceStr","url"})
    @ResponseBody
    String WxSign(HttpServletRequest request) throws IOException, HttpException {
        String time = request.getParameter("timestamp");
        String nonceStr = request.getParameter("nonceStr");
        String Url = request.getParameter("url");
        String Ticket = wxservice.getWxTicket();

        return wxservice.sign(Ticket,nonceStr,time,Url);
    }

    @RequestMapping(value = "/WxNonceStr")
    @ResponseBody
    String WxNonceStr() throws IOException, HttpException {
        return wxservice.create_nonce_str();
    }

}
