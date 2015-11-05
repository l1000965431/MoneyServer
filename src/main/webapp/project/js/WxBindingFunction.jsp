<%@ page import="com.money.Service.user.UserService" %>
<%@ page import="com.money.Service.ServiceFactory" %>
<%--
  Created by IntelliJ IDEA.
  User: liumin
  Date: 15/11/4
  Time: 下午10:41
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<script type="text/javascript">
  function on_binding() {
    <%
                                    String userId = request.getParameter("username");
                                    String userPassword = request.getParameter("userpassword");
                                    String openId = request.getParameter("openId");
                                    UserService userService = ServiceFactory.getService("UserService");
                                    if( userService != null ){
                                    int result = userService.BindingUserId( openId,userId,userPassword );
                                    if( result == 1 ){
                                    response.sendRedirect("../BindingResult.jsp?result=1");
                                    }else if( result == 3 ){
                                    response.sendRedirect("../BindingResult.jsp?result=3");
                                    }else if( result == -1 ){

                                    }else{
                                    response.sendRedirect("../BindingResult.jsp?result=2");
                                    }

                                    }
                                    %>
    return;
  }
</script>
