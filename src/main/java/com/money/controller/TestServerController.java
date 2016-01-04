package com.money.controller;

import com.money.Service.ServiceFactory;
import com.money.Service.user.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by liumin on 16/1/4.
 */

@Controller
@RequestMapping("/Test")
public class TestServerController extends ControllerBase implements IController {
    @RequestMapping("/TestDB")
    @ResponseBody
    String TestDB() throws Exception {
        UserService userService = ServiceFactory.getService("UserService");
        return userService.getUserInfo("18511583205").toString();
    }
}
