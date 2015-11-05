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
    <title>微聚竞投下载</title>
    <script src="./js/zepto.min.js"></script>
    <meta name="viewport" content="width=device-width,minimum-scale=1.0,maximum-scale=1.0,user-scalable=no,initial-scale=1.0" />
    <meta content="yes" name="apple-mobile-web-app-capable" />
    <meta content="black" name="apple-mobile-web-app-status-bar-style" />
    <meta content="telephone=no" name="format-detection" />
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <link href="res/styledownload.css" rel='stylesheet' type='text/css'/>
    <script type="application/x-javascript"> addEventListener("load", function () {
        setTimeout(hideURLbar, 0);
    }, false);
    function hideURLbar() {
        window.scrollTo(0, 1);
    } </script>

    <!--webfonts-->
    <link href='http://fonts.googleapis.com/css?family=Open+Sans:300italic,400italic,600italic,700italic,400,300,600,700,800'
          rel='stylesheet' type='text/css'>
    <!--//webfonts-->
    <style>
        .zhezhao {
            width: 100%;
            height: 100%;
            position: fixed;
            top: 0;
            bottom: 0;
            left: 0;
            background: #000;
            opacity: 0.4;
            display: none;
        }

        .pic {
            right: 10px;
            position: absolute;
            display: none;
            top: 0;
        }

        .pic img {
            width: 235px;
        }
    </style>
</head>

<body>
<h1>下载“微聚竞投”APP</h1>

<div class="zhezhao" id="zhezhao"></div>
<div class="pic" id="zhezhao_od">
    <img src="res/dashed.png" alt="">
</div>
<div class="app-nature">
    <div class="nature" id="nature"><img src="res/timer.png" class="img-responsive" alt=""/></div>
    <div class="submit" id="android"><input type="button" value="安卓版下载"></div>
    <div class="submit"><input type="button" onclick="myFunction()" value="iOS版下载(敬请期待)" id="IOS"></div>
    <div class="clear"></div>
</div>

<!--start-copyright-->
<div class="copy-right">
    <p>Copyright &copy; 2015 All rights Reserved | &nbsp;<a href="http://www.longan.com">Longan</a></p>
</div>
<!--//end-copyright-->
</body>
<script type="text/javascript">
    function is_weixn() {
        var ua = navigator.userAgent.toLowerCase();
        if (ua.match(/MicroMessenger/i) == "micromessenger") {
            return true;
        } else {
            return false;
        }
    }

    $('#android').on('click',function (){
        var url = "http://7xjewm.com1.z0.glb.clouddn.com/app-release.apk";
        var isweixin = is_weixn();
        if (isweixin) {
            $('#zhezhao').show();
            $('#zhezhao_od').show();
        }else{
            window.location.href = url;
        }
    });

    $('#zhezhao').on('click',function(){
        $('#zhezhao').hide();
        $('#zhezhao_od').hide();
    })
</script>
</html>