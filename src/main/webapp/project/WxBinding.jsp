<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<!--
Author: W3layouts
Author URL: http://w3layouts.com
License: Creative Commons Attribution 3.0 Unported
License URL: http://creativecommons.org/licenses/by/3.0/
-->
<!DOCTYPE html>
<html>
<head>
    <title>微聚竞投绑定帐号</title>
    <link href="res/style.css" rel='stylesheet' type='text/css'/>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta name="keywords"
          content="Nature Sign In Form,Login Forms,Sign up Forms,Registration Forms,News latter Forms,Elements" ./>
    <script type="application/x-javascript"> addEventListener("load", function () {
        setTimeout(hideURLbar, 0);
    }, false);
    function hideURLbar() {
        window.scrollTo(0, 1);
    } </script>

    <script type="text/javascript">
        <%!
    private String openId;
/*    String userId;
    String userPassword;*/
    %>

        <%
            String TempOpenId = request.getParameter("openId");
            if (openId == null || (TempOpenId != null && !TempOpenId.equals( openId )) ) {
                openId = request.getParameter("openId");
            }
        %>

    </script>
    <style type="text/css">
        label{display:block;position:relative;margin:20px 0;}
        span{position:absolute;float:left;line-height:40px;left:65px;top:10px;cursor:text;}
    </style>


    <!--webfonts-->
    <link href='http://fonts.googleapis.com/css?family=Open+Sans:300italic,400italic,600italic,700italic,400,300,600,700,800'
          rel='stylesheet' type='text/css'>
    <!--//webfonts-->
</head>
<body>
<h1>绑定微信</h1>

<div class="app-nature">
    <div class="nature"><img src="http://7xo7qm.com1.z0.glb.clouddn.com/timer.png" class="img-responsive" alt=""/></div>
    <form action="./js/WxBindingFunction.jsp" method="get">
       <%-- <label><span>请输入微距竞投帐号</span>--%><input type="text" class="text" value=''
                                            onfocus="if(this.value==''){$(this).siblings('span').hide();};"
               onblur="if (this.value == '') {this.siblings('spanusername')[0].show();}" id="username" name="username"><%--</label>--%>
        <%--<label><span id="spanuserapssword">请输入密码</span>--%><input type="password" value=""
                                                              onfocus="if(this.value = ''){this.siblings('spanuserapssword')[0].hide();};"
               onblur="if (this.value == '') {this.siblings('spanuserapssword')[0].show();}" id="userpassword" name="userpassword"><%--</label>--%>
        <input type="text" class="text" value="<%=openId%>" id="openId" name="openId" hidden="hidden">
        <div class="submit"><input type="submit" onsubmit="if( document.getElementsByName( 'username')[0].value == '' || document.getElementsByName( 'username')[0].value =='输入登陆的手机号码'){ return alert( '请输入用户名' ); }
               if( document.getElementsByName( 'userpassword' )[0].value == '' || document.getElementsByName( 'userpassword' )[0].value == '输入密码输入密码' ){ return alert( '请输入密码' ); } return on_binding();"
                                   value="绑定"></div>
        <div class="clear"></div>
    </form>
</div>
<!--start-copyright-->
<div class="copy-right">
    <p>Copyright &copy; 2015 All rights Reserved | &nbsp;<a href="http://www.longan.com">Longan</a></p>
</div>
<!--//end-copyright-->
</body>
</html>