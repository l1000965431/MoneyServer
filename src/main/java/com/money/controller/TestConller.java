package com.money.controller;

import com.google.gson.reflect.TypeToken;
import com.money.Service.PurchaseInAdvance.PurchaseInAdvance;
import com.money.Service.ServiceFactory;
import com.money.Service.Wallet.WalletService;
import com.money.Service.activity.ActivityService;
import com.money.Service.activityPreferential.ActivityPreferentialService;
import com.money.Service.user.Token;
import com.money.Service.user.UserService;
import com.money.config.Config;
import com.money.config.ServerReturnValue;
import com.money.dao.userDAO.UserDAO;
import com.money.job.TestJob;
import com.money.memcach.MemCachService;
import com.money.model.PreferentiaLotteryModel;
import com.money.model.SREarningModel;
import com.money.model.UserModel;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.quartz.DateBuilder;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import until.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by liumin on 15/7/6.
 */

@Controller
@RequestMapping("/Test")
public class TestConller extends ControllerBase implements IController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestConller.class);

    @Autowired
    UserDAO userDAO;

    @RequestMapping("/TestHead")
    @ResponseBody
    String TestHead(HttpServletRequest request) throws Exception {

        PurchaseInAdvance purchaseInAdvance = ServiceFactory.getService("PurchaseInAdvance");
        //activityService.ActivityCompleteStart("4");
        //activityService.InstallmentActivityIDStart( "4",1 );
        purchaseInAdvance.PurchaseActivityFromPurchaseInAdvance("4", "4_1");
        return "1";
    }

    @RequestMapping("/TestActivityStart")
    @ResponseBody
    String TestActivityStart(HttpServletRequest request) throws Exception {

        ActivityService activityService = ServiceFactory.getService("ActivityService");
        activityService.ActivityCompleteStart("4");
        return "1";
    }

    @RequestMapping("/TestDBTest")
    @ResponseBody
    String TestDBTest(HttpServletRequest request) throws Exception {
        UserService userService = ServiceFactory.getService("UserService");
        return userService.getUserInfoTest("18511583205").toString();
    }

    @RequestMapping("/TestDB")
    @ResponseBody
    String TestDB(HttpServletRequest request) throws Exception {
        UserService userService = ServiceFactory.getService("UserService");
        return userService.getUserInfo("18511583205").toString();
    }

    @RequestMapping("/TestRedis")
    @ResponseBody
    String TestRedis() throws Exception {
        String a = MemCachService.MemCachgGet("db1ws");
        return a;
    }

    @RequestMapping("/TestPing")
    @ResponseBody
    String TestPing(HttpServletRequest request) throws Exception {

        PingPlus.Test();
        //MemCachService.MemCachgGet("foo");
        return "1";
    }

    @RequestMapping("/Testwebhooks")
    @ResponseBody
    String Testwebhooks(HttpServletRequest request) throws Exception {

        //PingPlus.Test();
        //MemCachService.MemCachgGet("foo");
        return MemCachService.MemCachgGet("foo");
    }

    @RequestMapping("/TestcallCharg")
    @ResponseBody
    String TestcallCharg(HttpServletRequest request) throws Exception {
        String a = request.getParameter("a");
        Pattern p = Pattern.compile("^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$");
        Matcher m = p.matcher(a);
        return GsonUntil.JavaClassToJson(m.find());
    }

    @RequestMapping("/TestcallDBCach")
    @ResponseBody
    String TestcallDBCach(HttpServletRequest request) throws Exception {
        List<SREarningModel> LinePeoplesSREarningList = new ArrayList<SREarningModel>();
        SREarningModel srEarningModel1 = new SREarningModel();
        srEarningModel1.setEarningType(1);
        srEarningModel1.setEarningPrice(0);
        srEarningModel1.setNum(1);
        LinePeoplesSREarningList.add(srEarningModel1);

        srEarningModel1 = new SREarningModel();
        srEarningModel1.setEarningType(1);
        srEarningModel1.setEarningPrice(54);
        srEarningModel1.setNum(1);
        LinePeoplesSREarningList.add(srEarningModel1);


        String json = GsonUntil.JavaClassToJson(LinePeoplesSREarningList);

        ActivityService activityService = ServiceFactory.getService("ActivityService");
        activityService.Test("18", json);

        return json;
    }

    @RequestMapping("/TestWX")
    @ResponseBody
    String TestWX(HttpServletRequest request) throws IOException {

/*        String result = null;
        String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";

        url = url.replace("APPID", Config.WXAPPID).replace("APPSECRET", Config.WXAPPSECRET);

        HttpGet post = new HttpGet(url);
        CloseableHttpResponse response2 = null;
        try {
            response2 = (CloseableHttpResponse) client.execute(post);
            HttpEntity entity = response2.getEntity();
            if (entity != null) {
                result = EntityUtils.toString(entity, "UTF-8");
            }

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return e.toString();
        }finally {
            response2.close();
        }*/

        return null;
    }


    @RequestMapping("/Testalipay")
    @ResponseBody
    String Testalipay(HttpServletRequest request) throws IOException {
        String a = "0315006^xinjie_xj@163.com^星辰公司^20.00^F^TXN _RESULT_TRANSFER_OUT_CAN_NOT_EQUAL_IN^20081024" +
                "8427065^20081024143651|" +
                "0315006^xinjie_xj@163.com^星辰公司^20.00^F^TXN _RESULT_TRANSFER_OUT_CAN_NOT_EQUAL_IN^200810248427065^20081024143651";

        List<List<String>> c = new ArrayList<List<String>>();
        String[] b = a.split("\\|");
        for (int i = 0; i < b.length; i++) {
            List<String> d = Arrays.asList(b[i].split("\\^"));
            c.add(d);
        }

        String json = GsonUntil.JavaClassToJson(c);

        return json;
    }

    @RequestMapping("/TestDownLoad")
    @ResponseBody
    void TestDownLoad(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String a = request.getSession().getId();

        response.sendRedirect("http://p.gdown.baidu.com/58ebaf2831bb441e9ee3605aa1a809f7b36dee8a7618473419a33a0354cafd0a2a10c55cbf934846e0ec064e283e33be852c01ee7d062f92d6ff5aee1e8081b1514725476657f847544a3dcd927f5535141cf0cf4368df612cd2816ac355708eb471ee58b44e3273333aa69a8d7ab90e05df928e9af10fea108c3adda329dc23315a713107726a14561e14ea0fee26c9d5e17eb1cc279a8051e2142f2121b6689303a8383beba488");
    }

    @RequestMapping("/TestBeanToMap")
    @ResponseBody
    String TestBean22Map() {
        UserService userService = ServiceFactory.getService("UserService");
        UserModel userModel = userService.getUserInfo("18511583205");
        Map map = BeanTransfersUntil.TransBean2Map(userModel);
        return GsonUntil.JavaClassToJson(map);
    }

    @RequestMapping("/TestQrtzJob")
    @ResponseBody
    void TestQrtzJob() {
        ScheduleJob job = new ScheduleJob();
        job.setJobGroup("TestQrtzJob");
        job.setCronExpression("2 * * * * ?");
        job.setJobId("1");
        job.setJobName("TestQrtzJob1");
        job.setJobStatus("1");

        try {
            QuartzUntil.getQuartzUntil().AddTick(job, TestJob.class, DateBuilder.nextGivenSecondDate(null, 0));
        } catch (SchedulerException e) {
            return;
        }
    }

    @RequestMapping("/TestActivityPreferential")
    @ResponseBody
    void TestActivityPreferential(HttpServletRequest request) throws ParseException {

        String ActivityID = request.getParameter("activityId");
        ActivityPreferentialService activityPreferentialService = ServiceFactory.getService("ActivityPreferentialService");

        if (activityPreferentialService == null) {
            return;
        }

        String a = "[\n" +
                "    {\n" +
                "        \"earningPrice\": 6,\n" +
                "        \"earningType\": 2,\n" +
                "        \"num\": 1\n" +
                "    },\n" +
                "    {\n" +
                "        \"earningPrice\": 4,\n" +
                "        \"earningType\": 2,\n" +
                "        \"num\": 1\n" +
                "    },\n" +
                "    {\n" +
                "        \"earningPrice\": 3,\n" +
                "        \"earningType\": 2,\n" +
                "        \"num\": 1\n" +
                "    },\n" +
                "    {\n" +
                "        \"earningPrice\": 2,\n" +
                "        \"earningType\": 2,\n" +
                "        \"num\": 1\n" +
                "    },\n" +
                "    {\n" +
                "        \"earningPrice\": 1,\n" +
                "        \"earningType\": 2,\n" +
                "        \"num\": 1\n" +
                "    }\n" +
                "]\n";

        activityPreferentialService.InsertActivityPreferential(100, MoneyServerDate.getDateCurDate(), ActivityID, a, 50, 5);
    }

    @RequestMapping("/TestActivityPreferentialStart")
    @ResponseBody
    String TestActivityPreferentialStart(HttpServletRequest request) throws Exception {

        String ActivityID = request.getParameter("activityId");
        String ActivityInfoKey = Config.PREFERENTIUNBLLLED + ActivityID;
        MemCachService.RemoveValue(ActivityInfoKey);


        ActivityPreferentialService activityPreferentialService = ServiceFactory.getService("ActivityPreferentialService");

        activityPreferentialService.StartActivityPreferential(Integer.valueOf(ActivityID));


        List<byte[]> list = MemCachService.getRedisList(ActivityInfoKey.getBytes());

        List<PreferentiaLotteryModel> re = new ArrayList<>();

        for (byte[] a : list) {
            re.add((PreferentiaLotteryModel) GsonUntil.jsonToJavaClass(new String(a), new TypeToken<PreferentiaLotteryModel>() {
            }.getType()));
        }

        return GsonUntil.JavaClassToJson(re);
    }

    @RequestMapping("/TestActivityPreferentialInfo")
    @ResponseBody
    String TestActivityPreferentialInfo(HttpServletRequest request) throws Exception {

        String ActivityID = request.getParameter("activityId");
        String ActivityInfoKey = Config.PREFERENTIINFO + ActivityID;

        return new String(MemCachService.MemCachgGet(ActivityInfoKey.getBytes()));
    }

    @RequestMapping("/TestActivityPreferentialBilled")
    @ResponseBody
    String TestActivityPreferentialBilled(HttpServletRequest request) throws Exception {

        String ActivityID = request.getParameter("activityId");
        String ActivityInfoKey = Config.PREFERENTIBLLLED + ActivityID;

        List<byte[]> list = MemCachService.getRedisList(ActivityInfoKey.getBytes());

        List<PreferentiaLotteryModel> re = new ArrayList<>();

        for (byte[] a : list) {
            re.add((PreferentiaLotteryModel) BeanTransfersUntil.bytesToObject(a));
        }

        return GsonUntil.JavaClassToJson(re);
    }

    @RequestMapping("/TestActivityPreferentialUnBilled")
    @ResponseBody
    String TestActivityPreferentialUnBilled(HttpServletRequest request) throws Exception {

        String ActivityID = request.getParameter("activityId");
        String ActivityInfoKey = Config.PREFERENTIUNBLLLED + ActivityID;

        List<byte[]> list = MemCachService.getRedisList(ActivityInfoKey.getBytes());

        List<PreferentiaLotteryModel> re = new ArrayList<>();

        for (byte[] a : list) {
            re.add((PreferentiaLotteryModel) BeanTransfersUntil.bytesToObject(a));
        }

        return GsonUntil.JavaClassToJson(re);
    }

    @RequestMapping("/TestActivityPreferentialJoin")
    @ResponseBody
    String TestActivityPreferentialJoin(HttpServletRequest request) throws Exception {

        String ActivityID = request.getParameter("activityId");
        String UserId = request.getParameter("userId");
        ActivityPreferentialService activityPreferentialService = ServiceFactory.getService("ActivityPreferentialService");

        return activityPreferentialService.JoinActivityPreferential(Integer.valueOf(ActivityID), UserId, 0);
    }

    @RequestMapping("/TestGlobalConfig")
    @ResponseBody
    String TestGlobalConfig() throws Exception {
        Map<String, Integer> map = new HashMap<>();
        map.put("AddExpInvite", Config.AddExpInvite);
        map.put("AddVirtualSecuritiesInvite", Config.AddVirtualSecuritiesInvite);
        map.put("AddExpPurchase", Config.AddExpPurchase);
        map.put("AddVirtualSecuritiesSelf", Config.AddVirtualSecuritiesSelf);
        map.put("MaxVirtualSecurities", Config.MaxVirtualSecurities);
        map.put("MaxVirtualSecuritiesBuy", Config.MaxVirtualSecuritiesBuy);

        return GsonUntil.JavaClassToJson(map);
    }

    @RequestMapping("/TestGetUserModelByIdCard")
    @ResponseBody
    String TestGetUserModelByIdCard( HttpServletRequest request ) throws Exception {
        String mail = request.getParameter( "mail" );
        String idcard = request.getParameter( "idcard" );
        UserModel userModel = userDAO.getUserByMailOrIdCard( mail,idcard );
        return userModel.toString();
    }

    @RequestMapping("/TestUserLogin")
    @ResponseBody
    void TestUserLogin( HttpServletRequest request ) throws Exception {
        Map<String, String> mapData = DecryptionDataToMapByUserId(request.getParameter("data"),
                this.initDesKey(request.getHeader("userId")));

        if (mapData == null) {
            return;
        }


        String UserName = mapData.get("userId");
        String PassWord = mapData.get("password");

        userService.userLand(UserName, PassWord);
        Long orderTime = System.currentTimeMillis();
        String time = Long.toString(orderTime);
        Map<String, String> map = new HashMap<>();
        map.put("token", UserName);
        map.put("time", time);
        //存入缓存
        MemCachService.MemCachSetMap(UserName, Config.FAILUER_TIME, map);

    }

    @RequestMapping("/TestRedisList")
    @ResponseBody
    int TestRedisList( HttpServletRequest request ){
        int ThreadId = Integer.valueOf(request.getParameter( "threadId" ));
        int len = (int) MemCachService.getLen("wxTransferPass2015111705373972".getBytes());
        int enIndex = len<200 ? len-1 : ThreadId * 200+200-1;
        List<byte[]> list = MemCachService.lrang("wxTransferPass2015111705373972".getBytes(), ThreadId*200, 100);

        return 1;
    }

    @RequestMapping("/TestPasswordLogin")
    @ResponseBody
    String TestPasswordLogin( HttpServletRequest request ){
        String UserName = request.getParameter("userId");
        String PassWord = request.getParameter("password");

        String LoginResult = userService.userLand(UserName, PassWord);

        String tokenData = Token.create(UserName);
        Long orderTime = System.currentTimeMillis();
        String time = Long.toString(orderTime);
        Map<String, String> mapToken = new HashMap<>();
        mapToken.put("token", UserName);
        mapToken.put("time", time);
        //存入缓存
        MemCachService.MemCachSetMap( Config.UserLoginToken+UserName, Config.FAILUER_TIME, mapToken);

        Map<String, Object> map = new HashMap();
        map.put("token", LoginResult);

        if (LoginResult.length() >= 8) {
            map.put("LoginResult", ServerReturnValue.LANDSUCCESS);
            map.put("UserResponse", userService.getUserInfo(UserName));
        } else {
            map.put("LoginResult", LoginResult);
            map.put("UserResponse", "");
        }

        return GsonUntil.JavaClassToJson(map);
    }

    @RequestMapping("/TestRewallet")
    @ResponseBody
    String TestRewallet( HttpServletRequest request ){
        String UserID = request.getParameter("userId");
        int Lines = Integer.valueOf(request.getParameter("lines"));
        String ChannelID = request.getParameter("channelid");

        return PingPlus.CreateChargeParams(UserID,Lines, ChannelID, "", "充值", "null", Long.toString( System.currentTimeMillis()));
    }

    @RequestMapping("/TestPrefactInfo")
    @ResponseBody
    int TestPrefactInfo( HttpServletRequest request ){
        String userID = request.getParameter("userID");
        String token = request.getParameter("token");
        String info = request.getParameter("info");
        return userService.perfectInfo(userID, token, info);
    }

    @RequestMapping("/TestRig")
    @ResponseBody
    int TestRig( HttpServletRequest request ){
        String userName = request.getParameter("userId");
        //String code = request.getParameter( "code" );
        String password = request.getParameter("password");
        int userType = Integer.valueOf(request.getParameter("userType"));
        String inviteCode = request.getParameter("inviteCode");

        return userService.userRegister(userName, "", password, userType, inviteCode);
    }

    @RequestMapping("/TestInsertWxTransferInfo")
    @ResponseBody
    int TestInsertWxTransferInfo( HttpServletRequest request ){
        int insertNum = Integer.valueOf(request.getParameter("insertNum"));

        StringBuffer stringBuffer =  new StringBuffer("(FALSE,'1','刘旻',2,'15810356658','2015-11-23 16:03:08',1),");

        for( int i = 0; i < insertNum-1;++i ){
            stringBuffer.append( "(FALSE,'1','刘旻',2,'15810356658','2015-11-23 16:03:08',1)," );
        }
        stringBuffer.deleteCharAt( stringBuffer.length()-1 );
        Session session = userDAO.getNewSession();
        String sql = "insert into wxtransfer (IsFaliled,OpenId,RealName,TransferLines,UserId,WxtransferDate,IsLock)" +
                "values" + stringBuffer.toString();
        Transaction t = session.beginTransaction();
        session.createSQLQuery( sql ).executeUpdate();
        t.commit();

        return 1;
    }

    @RequestMapping("/TestInsertAliTransferInfo")
    @ResponseBody
    int TestInsertAliTransferInfo( HttpServletRequest request ){
        int insertNum = Integer.valueOf(request.getParameter("insertNum"));

        StringBuffer stringBuffer =  new StringBuffer("(FALSE,'1','刘旻',2,'15810356658','2015-11-23 16:03:08',1),");

        for( int i = 0; i < insertNum-1;++i ){
            stringBuffer.append( "(FALSE,'1','刘旻',2,'15810356658','2015-11-23 16:03:08',1)," );
        }
        stringBuffer.deleteCharAt( stringBuffer.length()-1 );
        Session session = userDAO.getNewSession();
        String sql = "insert into alitransfer (IsFaliled,AliEmail,RealName,TransferLines,UserId,AlitransferDate,IsLock)" +
                "values" + stringBuffer.toString();
        Transaction t = session.beginTransaction();
        session.createSQLQuery( sql ).executeUpdate();
        t.commit();

        return 1;
    }


    @RequestMapping("/TestCreateWxWinTransferList")
    @ResponseBody
    int TestCreateWxWinTransferList( HttpServletRequest request ){
        String BatchId = request.getParameter( "BatchId" );
        String key = "wxtransferWinList::" + BatchId;
        String key1 = "wxTransferPass::" + BatchId;
        int len = (int) MemCachService.getLen(key1.getBytes());
        List<byte[]> list = MemCachService.lrang(key1.getBytes(), 0, len-1);

        for (byte[] tempbyte : list) {
            String json = new String(tempbyte);
            List<String> transferInfo = GsonUntil.jsonListToJavaClass(json, new TypeToken<List<String>>() {
            }.getType());


            int lines = Integer.valueOf(transferInfo.get(1));
            List<String> list1 = new ArrayList<>();
            list1.add(transferInfo.get(0));
            list1.add(Integer.toString(lines));
            String json1 = GsonUntil.JavaClassToJson(list1);
            MemCachService.lpush(key.getBytes(), json1.getBytes());
        }

        return 1;
    }


    @RequestMapping("/TestBatchWinList")
    @ResponseBody
    int TestBatchWinList( HttpServletRequest request ){
        String BachList = request.getParameter("BatchWinList");
        //String code = request.getParameter( "code" );
        WalletService walletService = ServiceFactory.getService( "WalletService" );
        walletService.wxTransferWinList( BachList );
        return 1;
    }

    @RequestMapping("/TestBatchFailList")
    @ResponseBody
    int TestBatchFailList( HttpServletRequest request ){
        String BachList = request.getParameter("BatchWinList");
        //String code = request.getParameter( "code" );
        WalletService walletService = ServiceFactory.getService( "WalletService" );
        walletService.FailTransfer( BachList );
        return 1;
    }

    @RequestMapping("/TestLogs")
    @ResponseBody
    int TestLogs(){
        LOGGER.error( "测试log error" );
        LOGGER.info( "测试log info" );

        List<Integer> listInt = new ArrayList<>();
        listInt.add( 1 );
        listInt.add( 2 );
        listInt.add( 3 );
        listInt.add( 4 );

        List<String> listString = new ArrayList<>();
        listString.add( "10" );
        listString.add( "20" );
        listString.add( "30" );
        listString.add( "40" );

        Map<Integer,String> mapIS = new HashMap<>();
        mapIS.put( 1,"1" );
        mapIS.put( 2,"2" );
        mapIS.put( 3,"3" );
        mapIS.put( 4,"4" );

        Map<String,String> mapSS = new HashMap<>();
        mapSS.put( "10","1" );
        mapSS.put( "20","2" );
        mapSS.put( "30","3" );
        mapSS.put( "40","4" );

        Map<String,List<String>> mapSL = new HashMap<>();
        mapSL.put( "19",listString );
        mapSL.put( "29",listString );
        mapSL.put( "39",listString );
        mapSL.put( "49",listString );

        LOGGER.error( "测试log error List{}",listInt );
        LOGGER.info( "测试log info List{}",listString );

        LOGGER.error( "测试log error map{}",mapIS );
        LOGGER.info( "测试log info map{}",mapSS );

        LOGGER.error( "测试log error mapO{}",mapSL );
        LOGGER.info( "测试log info mapO{}",mapSL );

        return 1;
    }
}

