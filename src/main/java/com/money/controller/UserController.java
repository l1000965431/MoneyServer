package com.money.controller;

import com.money.Service.user.UserService;
import com.money.config.Config;
import com.money.config.ServerReturnValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import until.GsonUntil;
import until.WxBinding;
import until.WxOauth2Token;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by fisher on 2015/7/13.
 */

@Controller
@RequestMapping("/User")
public class UserController extends ControllerBase implements IController
{
    @Autowired
    UserService userService;

    @Autowired
    WxBinding wxBinding;

    @RequestMapping("/passWordLogin")
    @ResponseBody
    public String Login( HttpServletRequest request,
                              HttpServletResponse response )
    {
        String UserName = request.getParameter( "userId" );
        String PassWord = request.getParameter( "password" );

        String LoginResult = userService.userLand(UserName,PassWord);

        Map<String,Object> map = new HashMap<String, Object>();
        map.put( "token",LoginResult );

        if( LoginResult.length() >= 8 ) {
            map.put( "LoginResult",ServerReturnValue.LANDSUCCESS );
            map.put( "UserResponse",userService.getUserInfo(UserName) );
        }else{
            map.put( "LoginResult",LoginResult );
            map.put( "UserResponse","");
        }

        return GsonUntil.JavaClassToJson( map );
    }

    @RequestMapping("/tokenLogin")
    @ResponseBody
    public int tokenLogin( HttpServletRequest request,
                              HttpServletResponse response )
    {
        String token = request.getParameter( "token" );
        String userId = request.getParameter( "userId" );
        return userService.tokenLand(userId,token);
    }

    @RequestMapping("/perfectInfo")
    @ResponseBody
    public int perfectInfo( HttpServletRequest request,
                                HttpServletResponse response )
    {
        String userID = request.getParameter( "userID" );
        String token = request.getParameter( "token" );
        String info = request.getParameter( "info" );
        return userService.perfectInfo(userID,token, info);
    }

    @RequestMapping("/changeInfo")
    @ResponseBody
    public int changeInfo( HttpServletRequest request,
                            HttpServletResponse response )
    {
        String token = request.getParameter( "token" );
        String info = request.getParameter( "info" );
        String userType=request.getParameter("userType");
        return userService.changeInfo(token, info, userType);
    }

    @RequestMapping("/quitLogin")
    @ResponseBody
    public boolean quitLogin( HttpServletRequest request,
                                HttpServletResponse response )
    {
        String userID = request.getParameter( "userId" );
        return userService.quitLand(userID);
    }

    @RequestMapping("/register")
    @ResponseBody
    public  int register(HttpServletRequest request,
                             HttpServletResponse response )
    {
        String userName = request.getParameter( "userId" );
        //String code = request.getParameter( "code" );
        String password = request.getParameter( "password" );
        int userType = Integer.valueOf(request.getParameter("userType"));
        return  userService.userRegister( userName, "",password,userType );
    }

    @RequestMapping("/submitTeleNum")
    @ResponseBody
    public  int submitTeleNum(HttpServletRequest request,
                             HttpServletResponse response )
    {
        String userName = request.getParameter( "userId" );
        return  userService.submitTeleNum(userName,"");
    }

    @RequestMapping("/sendPasswordCode")
    @ResponseBody
    public int sendPasswordCode(HttpServletRequest request,
                                  HttpServletResponse response )
    {
        String userName = request.getParameter( "userId" );
        String password = request.getParameter( "password" );
        return  userService.sendPasswordCode(userName, password,"");
    }

    @RequestMapping("/changPassword")
    @ResponseBody
    public  int sendPasswochangPasswordrdCode(HttpServletRequest request,
                                     HttpServletResponse response )
    {
        String userName = request.getParameter( "userId" );
        //String code = request.getParameter( "code" );
        String newPassword = request.getParameter( "newPassword" );
        String oldPassword = request.getParameter( "oldPassword" );
        return  userService.changPassword(userName,"",newPassword,oldPassword);
    }

    @RequestMapping("/RetrievePassword")
    @ResponseBody
    public  int RetrievePassword(HttpServletRequest request,
                                 HttpServletResponse response ){

        String userName = request.getParameter( "userId" );
        //String code = request.getParameter( "code" );
        String newPassword = request.getParameter( "newPassword" );
        return  userService.RetrievePassword(userName, newPassword);
    }

    @RequestMapping("/SendUserCode")
    @ResponseBody
    public int SendUserCode( HttpServletRequest request,
                              HttpServletResponse response ){
        String userID = request.getParameter( "userId" );
        return userService.SendCode( userID );
    }

    @RequestMapping("/ChangeUserHeadPortrait")
    @ResponseBody
    public int ChangeUserHeadPortrait( HttpServletRequest request,
                                   HttpServletResponse response ){
        String userID = request.getParameter( "userId" );
        String Url = request.getParameter( "Url" );

        if( userID == null || userID.length() == 0 ){
            return ServerReturnValue.SERVERRETURNERROR;
        }

        if( Url == null || Url.length() == 0 ){
            return ServerReturnValue.SERVERRETURNERROR;
        }

        return userService.ChangeUserHeadPortrait( userID,Url );
    }

    /**
     * 获得微信openId
     * @param request
     * @param response
     */
    @RequestMapping("/getWxOpenId")
    @ResponseBody
    public void getWxOpenId( HttpServletRequest request,
                           HttpServletResponse response ) throws ServletException, IOException {
/*        String code = request.getParameter("code");
        if( code == null ){
            return;
        }

        WxOauth2Token wxOauth2Token;
        try {
            wxOauth2Token = wxBinding.getOauth2AccessToken( "wx287d8a1f932dc864","5e39c31e9e69105b90184db19c05b6e4",code );
        } catch (IOException e) {
            return;
        }

        if( wxOauth2Token == null ){
            return;
        }*/

        /*request.setAttribute("openId", wxOauth2Token.getOpenId());*/
        request.setAttribute("openId", "hhyuise-7768");
        request.getRequestDispatcher("../project/WxBinding.jsp").forward(request, response);
        return;
    }

    @RequestMapping("/BindingWxOpenId")
    @ResponseBody
    public void BindingWxOpenId( HttpServletRequest request,
                             HttpServletResponse response ) throws ServletException, IOException {

        String userId = request.getParameter("userId");
        String password = request.getParameter( "password" );
        String openId = request.getParameter( "openId" );

        if( userId == null || password == null || openId == null ){
           if( userService.BinddingUserId( openId,userId,password )){
               request.getRequestDispatcher("index.jsp");
           }else{
               request.getRequestDispatcher("index.jsp");
           }
        }

    }

}
