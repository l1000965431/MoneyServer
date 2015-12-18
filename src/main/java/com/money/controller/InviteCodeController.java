package com.money.controller;

import com.money.Service.InviteCodeService.InviteCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;

/**
 * Created by liumin on 15/10/4.
 */

@Controller
@RequestMapping("/InviteCodeController")
public class InviteCodeController {

    @Autowired
    InviteCodeService inviteCodeService;

    @RequestMapping("/useInviteCode")
    @ResponseBody
    public int useInviteCode(HttpServletRequest request) throws ParseException {
        String userId = request.getParameter("userId");
        String inviteCode = request.getParameter("inviteCode");

        if (userId == null || inviteCode == null) {
            return 0;
        }

        return inviteCodeService.useInviteCode(userId, inviteCode);
    }

    @RequestMapping("/InsertInviteCode")
    @ResponseBody
    public String InsertInviteCode(HttpServletRequest request) throws ParseException {
        int inviteCode = Integer.valueOf(request.getParameter("num"));
        return inviteCodeService.AddInviteCode(inviteCode);
    }

    @RequestMapping("/getInviteCode")
    @ResponseBody
    public String getInviteCode(HttpServletRequest request) {
        int num = Integer.valueOf(request.getParameter("num"));
        return inviteCodeService.getInviteCode( num );
    }

    @RequestMapping("/TestInviteCode")
    @ResponseBody
    public int TestInviteCode() throws ParseException {
        return inviteCodeService.CountInviteCode();

    }

}
