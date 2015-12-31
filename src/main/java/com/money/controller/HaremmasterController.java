package com.money.controller;

import com.money.Service.Haremmaster.HaremmasterService;
import com.money.Service.Wallet.WalletService;
import com.money.Service.alipay.PayService;
import com.money.Service.alipay.util.AlipayNotify;
import com.money.config.Config;
import com.money.model.HaremmasterModel;
import org.apache.http.HttpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import until.GsonUntil;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 群主模块访问
 */

@Controller
@RequestMapping("/Haremmaster")
public class HaremmasterController {

    @Autowired
    HaremmasterService haremmasterService;

    @Autowired
    WalletService walletService;

    @Autowired
    PayService payService;

    @RequestMapping("/SetUserIsHaremmaster")
    @ResponseBody
    public int SetUserIsHaremmaster(HttpServletRequest httpServletRequest) {
        String userId = httpServletRequest.getParameter("userId");

        if (userId == null) {
            return 0;
        }

        return haremmasterService.SetUserHaremmaster(userId);
    }

    @RequestMapping("/ShieldingHaremmaster")
    @ResponseBody
    public int ShieldingHaremmaster(HttpServletRequest request) {
        String userId = request.getParameter("userId");

        if (userId == null) {
            return 0;
        }

        return haremmasterService.SetShieldingHaremmaster( userId );
    }


    @RequestMapping("/CanelShieldingHaremmaster")
    @ResponseBody
    public int CanelShieldingHaremmaster(HttpServletRequest request) {
        String userId = request.getParameter("userId");

        if (userId == null) {
            return 0;
        }

        return haremmasterService.CanelShieldingHaremmaster( userId );
    }


    @RequestMapping("/DeleteHaremmaster")
    @ResponseBody
    public int DeleteHaremmaster(HttpServletRequest request) {
        String userId = request.getParameter("userId");

        if (userId == null) {
            return 0;
        }

        return haremmasterService.DeleteHaremmaster(userId);
    }

    @RequestMapping("/GetaremmasterInfo")
    @ResponseBody
    public String GetaremmasterInfo(HttpServletRequest request) {
        int page = Integer.valueOf(request.getParameter("page"));
        int pagenum = Integer.valueOf(request.getParameter("pagenum"));
        List list = haremmasterService.GetHaremmasterList(page, pagenum);
        return GsonUntil.JavaClassToJson(list);
    }

    @RequestMapping("/GetaremmasterInfoByUserId")
    @ResponseBody
    public String GetaremmasterInfoByUserId(HttpServletRequest request) {
        String userId = request.getParameter("userId");

        return haremmasterService.GetHaremmaster(userId);
    }

    @RequestMapping("/HaremmasterTransaction")
    @ResponseBody
    public String HaremmasterTransaction(HttpServletRequest request) throws IOException, HttpException {
        int page = Integer.valueOf(request.getParameter("page"));
        int pagenum = Integer.valueOf(request.getParameter("pagenum"));
        List dataList = haremmasterService.GetHaremmasterTransfer(page, pagenum);
        return payService.requestTransaction(dataList, "http://115.29.111.0/Longyan/Haremmaster/HaremmasterTransactionResult");
    }


    /**
     * 群主发钱结果
     *
     * @param request
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping("/HaremmasterTransactionResult")
    @ResponseBody
    public String HaremmasterTransactionResult(HttpServletRequest request) throws UnsupportedEncodingException {
        Map<String, String> map = AlipayNotify.getalipayInfo(request);
        if (!AlipayNotify.verify(map)) {
            return "fail";
        }

        if (Objects.equals(walletService.HaremmasterTransferNotify(map), Config.MESSAGE_SEND_SUCCESS)) {
            return "success";
        } else {
            return "fail";
        }
    }

}
