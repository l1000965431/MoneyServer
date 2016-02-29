package com.money.dao.userDAO;

import com.google.gson.reflect.TypeToken;
import com.money.Service.user.Token;
import com.money.config.Config;
import com.money.config.ServerReturnValue;
import com.money.dao.BaseDao;
import com.money.dao.TransactionCallback;
import until.memcach.MemCachService;
import com.money.model.InviteCodeModel;
import com.money.model.UserModel;
import com.money.model.WalletModel;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import until.*;

import javax.servlet.http.HttpSession;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by fisher on 2015/7/9.
 */

@Repository
public class UserDAO extends BaseDao {

    private static Logger logger = LoggerFactory.getLogger(UserDAO.class);


    //0未登录；1，修改信息成功；2，信息不合法;3，token不一致;4,userType有问题 5:身份证号重复 6:邮箱重复
    public int modifyInvestorInfo(String userId, String info) {
        //将信息转换为map形式
        Map<String, String> map = GsonUntil.jsonToJavaClass(info, new TypeToken<Map<String, String>>() {
        }.getType());
        //有空信息标志位
        boolean infoFlag = true;
        //获取MAP的第一个值，开始遍历，判断信息是否为空
        Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            String value = entry.getValue();
            if (value == null)
                infoFlag = false;
        }
        //查看用户昵称是否合法
        boolean userIsRight = userIsRight(userId);

        //查找身份证或者邮箱是否有重复
        UserModel userModel = getUserByMailOrIdCard(map.get("mail"), map.get("identity"));
        if (userModel != null) {
            if (userModel.getIdentityId().equals(map.get("identity"))) {
                return 5;
            }
            if (userModel.getMail().equals(map.get("mail"))) {
                return 6;
            }
        }

        if ((userIsRight) && (infoFlag)) {
            //写数据库信息
            writeInfo(userId, map);
            return Config.MODIFYINFO_SUCCESS;
        } else
            return Config.MODIFYINFO_FAILED;

    }

    //投资者修改个人信息
    public int changeInvestorInfo(String userName, String info) {
        //将信息转换为map形式
        Map<String, String> map = new HashMap<String, String>();
        map = GsonUntil.jsonToJavaClass(info, map.getClass());
        //有空信息标志位
        boolean infoFlag = true;
        //获取MAP的第一个值，开始遍历，判断信息是否为空
        Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            String value = entry.getValue();
            if (value == null)
                infoFlag = false;

        }
        //查看用户昵称是否合法
        String user = map.get("user");
        boolean userIsRight = userIsRight(user);

        if ((userIsRight) && (infoFlag)) {
            //写数据库信息
            writeInfo(userName, map);
            return Config.MODIFYINFO_SUCCESS;
        } else
            return Config.MODIFYINFO_FAILED;
    }

    //修改密码
    public boolean changePassword(String userID, String newPassWord, String oldPassWord,HttpSession session) {
        UserModel userModel = this.getUSerModel(userID);

        if (userModel == null) {
            return false;
        }

        String CurPassWord = userModel.getPassword();

        String decodePassword = new String(Base32.decode(CurPassWord));

        if (!oldPassWord.equals(decodePassword)) {
            return false;
        }

        userModel.setPassword(newPassWord);
        this.update(userModel);
        return true;
    }

    /**
     * 密码找回
     *
     * @param userID
     * @param newPassWord
     * @return
     */
    public boolean RetrievePassword(String userID, String newPassWord) {
        UserModel userModel = this.getUSerModel(userID);

        if (userModel == null) {
            return false;
        }

        if (!passwordIsRight(newPassWord)) {
            return false;
        }

        userModel.setPassword(newPassWord);
        this.update(userModel);
        return true;
    }


    //注册
    public int registered(final String userID, final String passWord, final int userType, final String inviteCode) {

        UserModel userModel = new UserModel();
        //用户注册，存入数据库
        userModel.setUserId(userID);
        userModel.setPassword(passWord);
        userModel.setUserType(userType);
        userModel.setWxOpenId(userID);
        userModel.setAlipayId("0");
        userModel.setUserInvitecode(ShareCodeUtil.toSerialCode(ShareCodeUtil.codeToId(userID)));
        userModel.setCreateTime(MoneyServerDate.getDateCurDate());
        saveNoTransaction(userModel);

        if (userType == Config.INVESTOR) {
            WalletModel walletModel = new WalletModel();
            walletModel.setUserID(userID);
            saveNoTransaction(walletModel);
        } else if (userType == Config.BORROWER) {
            try {
                if (userInviteCode(userID, inviteCode) == 0) {
                    return ServerReturnValue.REQISTEREDCODEERROR;
                }
            } catch (ParseException e) {
                return ServerReturnValue.REQISTEREDCODEERROR;
            }
        }
        return ServerReturnValue.REQISTEREDSUCCESS;
    }

    //群主注册
    public int registeredHaremmaster(final String userID,
                                     final String passWord, final int userType, final String inviteCode) {

        UserModel userModel = new UserModel();
        //用户注册，存入数据库
        userModel.setUserId(userID);
        userModel.setPassword(passWord);
        userModel.setUserType(userType);
        userModel.setWxOpenId(userID);
        userModel.setAlipayId("0");
        userModel.setUserInvitecode(ShareCodeUtil.toSerialCode(ShareCodeUtil.codeToId(userID)));
        userModel.setCreateTime(MoneyServerDate.getDateCurDate());
        saveNoTransaction(userModel);

        if (userType == Config.INVESTOR) {
            WalletModel walletModel = new WalletModel();
            walletModel.setUserID(userID);
            saveNoTransaction(walletModel);

        } else if (userType == Config.BORROWER) {
            try {
                if (userInviteCode(userID, inviteCode) == 0) {
                    return ServerReturnValue.REQISTEREDCODEERROR;
                }
            } catch (ParseException e) {
                return ServerReturnValue.REQISTEREDCODEERROR;
            }
        }
        return ServerReturnValue.REQISTEREDSUCCESS;
    }



    //验证用户名是否已注册
    public boolean checkUserName(String userName) {
        return userIsExist(userName);

    }

    //验证短信验证码是否正确
    public boolean checkTeleCode(String userName, String code) {
        //根据userName,寻找缓存中的code，并判断是否相等
        return true;
        /*String UserCodeName = Config.CODE + userName;
        if (code == MemCachService.MemCachgGet(UserCodeName))
            return true;
        else
            return true;*/
    }

    //发送手机验证码，并验证手机短信是否发送成功 1为成功，0为失败............验证码内容待改，输出待改
    public int teleCodeIsSend(String userName) {
        HashMap<String, Object> result = null;

        //获取一个随机数
        int random = MoneySeverRandom.getRandomNum(1, 10000);
        String code = String.valueOf(random);
        //将发送的验证码存入缓存
        String UserCodeName = Config.CODE + userName;
        MemCachService.InsertValueWithTime(UserCodeName, Config.USERCODETIME, code);
        result = PRestSmsSDKUntil.getRestAPI().sendTemplateSMS(userName, "1", new String[]{code, "5"});
        if ("000000".equals(result.get("statusCode"))) {
            //正常返回输出data包体信息（map）
            HashMap<String, Object> data = (HashMap<String, Object>) result.get("data");
            Set<String> keySet = data.keySet();
            for (String key : keySet) {
                Object object = data.get(key);
                System.out.println(key + " = " + object);
            }
            return Config.SENDCODE_SUCESS;

        } else {
            //异常返回输出错误码和错误信息
            System.out.println("错误码=" + result.get("statusCode") + " 错误信息= " + result.get("statusMsg"));
            return Config.SENDCODE_FAILED;
        }
    }

    //登录，查询DB
    public String landing(String userName, String openid,HttpSession session ) {

        String tokenData = Token.create(userName);
        Map<String,String> map = new HashMap<>();
        map.put("token",tokenData);
        map.put("userid",userName);

        if(openid != null){
            map.put("openid",openid);
        }

        session.setAttribute(Config.SessionUserKey,map);

        /*Long orderTime = System.currentTimeMillis();
        String time = Long.toString(orderTime);
        Map<String, String> map = new HashMap<String, String>();
        map.put("token", tokenData);
        map.put("time", time);
        //存入缓存
        MemCachService.MemCachSetMap(Config.UserLoginToken + userName, Config.FAILUER_TIME, map);*/
        return tokenData;

    }

    //查询用户名是否存在
    public boolean userIsExist(String userID) {
        UserModel userModel = getUSerModelNoTransaction(userID);
        if (userModel != null)
            return true;
        else
            return false;
    }

    //查询数据库，比对用户密码是否正确
    public boolean checkPassWord(String userID, String passWord) {
        UserModel userModel = this.getUSerModel(userID);

        if (userModel == null) {
            return false;
        }

        String passWordSql = userModel.getPassword();

        String decodePassWord = new String(Base32.decode(passWordSql));

        if (passWord.equals(decodePassWord))
            return true;
        else
            return false;
    }

    //登录，2成功，0失败
    public int tokenLand(String userName, String time) {
        MemCachService.SetMemCachMapByMapKey(userName, "time", time);
        String tokenTime = MemCachService.GetMemCachMapByMapKey(userName, "time");
        if (tokenTime != time) {
            return Config.TOKENLAND_SUCESS;
        } else {
            return Config.TOKENLAND_FAILED;
        }
    }

    //根据userName查找缓存中上次token更新时间,判断是否为登录状态
    public boolean tokenTime(String userName, Long time) {
        String tokenTime = MemCachService.GetMemCachMapByMapKey(Config.UserLoginToken + userName, "time");
        Long tokenUpdTime = Long.parseLong(tokenTime);
        //在登录状态
        if ((time - tokenUpdTime) / 1000 < 3600)
            return true;
        else
            return false;

    }

    //查询缓存中是否有token字符串,并验证token字符串是否与客户端传来的相等
    public boolean isTokenExist(String userID, String token,HttpSession session) {

        Map<String,String> map = (Map<String,String>)session.getAttribute(Config.SessionUserKey);


        boolean tokenIsExist = map==null?false:true;

        //若存在
        if (tokenIsExist) {
            //如果不传token则只检测session是否存在
            if("".equals(token)){
                return true;
            }

            String memToken = map.get("token");
            if (token.equals(memToken))
                return true;
            else
                return false;
        } else
            return false;
    }

    //退出登录
    public boolean quitTokenLand(String userId,HttpSession session) {
        //清楚缓存中token
        session.removeAttribute(Config.SessionUserKey);
        return true;
    }

    //获取用户类型
    public int getUserType(String userName) {
        UserModel userModel = this.getUSerModel(userName);
        int userType = userModel.getUserType();
        return userType;

    }

    //信息完善，写数据库信息
    private void writeInfo(String userName, Map<String, String> map) {

        UserModel userModel = this.getUSerModel(userName);

        String user = map.get("userName");
        String mail = map.get("mail");
        int sex = Integer.valueOf(map.get("sex"));
        String location = map.get("location");
        String education = map.get("education");
        String identity = map.get("identity");
        String personalProfile = map.get("personalProfile");
        String selfIntroduce = map.get("selfintroduce");
        String goodAtField = map.get("goodAtField");
        String RealName = map.get("realName");
        userModel.setUserName(user);
        userModel.setMail(mail);
        userModel.setSex(sex);
        userModel.setLocation(location);
        userModel.setRealName(RealName);
        userModel.setEduInfo(education);
        userModel.setIdentityId(identity);
        userModel.setCareer(personalProfile);
        userModel.setIntroduction(selfIntroduce);
        userModel.setExpertise(goodAtField);
        userModel.setIsPerfect(true);
        this.update(userModel);
    }


    public UserModel getUserByMailOrIdCard(String Mail, String IdCard) {
        if (Mail == null || IdCard == null) {
            return null;
        }
        Session session = getNewSession();
        Transaction t = session.beginTransaction();
        try {

            Object o = session.createCriteria(UserModel.class)
                    .add(Restrictions.or(Restrictions.eq("mail", Mail), Restrictions.eq("identityId", IdCard)))
                    .uniqueResult();

            t.commit();
            UserModel userModel = null;
            if (o != null) {
                userModel = (UserModel) o;
            }

            return userModel;
        } catch (Exception e) {
            t.rollback();
            return null;
        }
    }

    //查看用户昵称是否合法
    public boolean userIsRight(String user) {
        if (user == null) {
            return false;
        }

        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Matcher m = p.matcher(user);
        return m.find();
    }

    //检查登录密码是否合法
    public boolean passwordIsRight(String password) {
        if (password == null) {
            return false;
        }

        Pattern p = Pattern.compile("^[0-9a-zA-Z]{6,16}$");
        Matcher m = p.matcher(password);
        return m.find();
    }

    public UserModel getUSerModel(final String UserID) {
        final UserModel[] userModel = {null};

        this.excuteTransactionByCallback(new TransactionCallback() {
            public void callback(BaseDao basedao) throws Exception {
                userModel[0] = (UserModel) basedao.getNewSession().createCriteria(UserModel.class)
                        .setMaxResults(1)
                        .add(Restrictions.eq("userId", UserID))
                        .uniqueResult();
            }
        });

        return userModel[0];
    }

    public UserModel getUSerModelNoTransaction(final String UserID) {
        final UserModel userModel;

        userModel = (UserModel) getNewSession().createCriteria(UserModel.class)
                .setMaxResults(1)
                .add(Restrictions.eq("userId", UserID))
                .uniqueResult();

        return userModel;
    }

    public UserModel getUSerModelByOpenId(final String openId) {
        final UserModel[] userModel = {null};

        this.excuteTransactionByCallback(new TransactionCallback() {
            public void callback(BaseDao basedao) throws Exception {
                userModel[0] = (UserModel) basedao.getNewSession().createCriteria(UserModel.class)
                        .setMaxResults(1)
                        .add(Restrictions.eq("wxOpenId", openId))
                        .uniqueResult();
            }
        });

        return userModel[0];
    }

    public UserModel getUSerModelByalipayId(final String alipayId) {
        final UserModel[] userModel = {null};

        this.excuteTransactionByCallback(new TransactionCallback() {
            public void callback(BaseDao basedao) throws Exception {
                userModel[0] = (UserModel) basedao.getNewSession().createCriteria(UserModel.class)
                        .setMaxResults(1)
                        .add(Restrictions.eq("alipayId", alipayId))
                        .uniqueResult();
            }
        });

        return userModel[0];
    }

    public UserModel getUSerModelByOpenIdNoTransaction(final String openId) {
        final UserModel userModel;

        userModel = (UserModel) getNewSession().createCriteria(UserModel.class)
                .setMaxResults(1)
                .add(Restrictions.eq("wxOpenId", openId))
                .uniqueResult();

        return userModel;
    }

    public UserModel getUSerModelByalipayIdNoTransaction(final String alipayId) {
        final UserModel userModel;

        userModel = (UserModel) getNewSession().createCriteria(UserModel.class)
                .setMaxResults(1)
                .add(Restrictions.eq("alipayId", alipayId))
                .uniqueResult();

        return userModel;
    }


    public UserModel getUSerModelByInviteCode(final String userInvitecode) {
        final UserModel[] userModel = {null};

        this.excuteTransactionByCallback(new TransactionCallback() {
            public void callback(BaseDao basedao) throws Exception {
                userModel[0] = (UserModel) basedao.getNewSession().createCriteria(UserModel.class)
                        .setMaxResults(1)
                        .add(Restrictions.eq("userInvitecode", userInvitecode))
                        .uniqueResult();
            }
        });

        return userModel[0];
    }

    public UserModel getUSerModelByInviteCodeNoTransaction(final String userInvitecode) {
        final UserModel userModel;

        userModel = (UserModel) getNewSession().createCriteria(UserModel.class)
                .setMaxResults(1)
                .add(Restrictions.eq("userInvitecode", userInvitecode))
                .uniqueResult();

        return userModel;
    }

    //1:绑定成功 2:绑定失败 3:已经绑定
    public int BindingOpenId(String openId, String UserId) {
        UserModel userModel = getUSerModel(UserId);
        if (userModel == null) {
            return 2;
        }

        if (!userModel.getWxOpenId().equals(userModel.getUserId())) {
            return 3;
        }

        userModel.setWxOpenId(openId);
        if (this.update(userModel)) {
            return 1;
        } else {
            return 2;
        }
    }

    //1:绑定成功 2:绑定失败 3:已经绑定
    public int BindingOpenId(String openId, UserModel userModel) {
        if (userModel == null) {
            return 2;
        }

        if (!userModel.getWxOpenId().equals(userModel.getUserId())) {
            return 3;
        }

        userModel.setWxOpenId(openId);
        if (this.update(userModel)) {
            return 1;
        } else {
            return 2;
        }
    }

    /**
     * 获得用户token
     *
     * @param UserId
     * @return
     */
    public String getUserToken(String UserId,HttpSession session) {
        Map<String,String> map = (Map<String,String>)session.getAttribute(Config.SessionUserKey);
        return map.get("token");
    }

    /**
     * 使用邀请码
     *
     * @param userId
     * @throws ParseException
     */
    public int userInviteCode(String userId, String InviteCode) throws ParseException {

        if (userId == null || InviteCode == null || InviteCode.length() == 0) {
            return 0;
        }

        String Sql = "select * from invitecode where userId='0' and inviteCode=?;";
        Session session = this.getNewSession();

        SQLQuery sqlQuery = session.createSQLQuery(Sql).addEntity(InviteCodeModel.class);
        sqlQuery.setParameter(0, InviteCode);
        InviteCodeModel inviteCodeModel = (InviteCodeModel) sqlQuery.uniqueResult();

        if (inviteCodeModel != null) {
            inviteCodeModel.setUseDate(MoneyServerDate.getDateCurDate());
            inviteCodeModel.setUserId(userId);
            this.updateNoTransaction(inviteCodeModel);
            return 1;
        } else {
            return 0;
        }

    }


    public int AddUserExpByUserId(String userId, int AddExp) {
        String sql = "update User set userExp=userExp+? where userId = ?";
        Session session = this.getNewSession();
        SQLQuery sqlQuery = session.createSQLQuery(sql);
        sqlQuery.setParameter(0, AddExp);
        sqlQuery.setParameter(1, userId);


        return sqlQuery.executeUpdate();
    }


    public int AddUserExpByInviteCode(String inviteCode, int AddExp) {
        String sql = "update User set userExp=userExp+? where userInvitecode = ?";
        Session session = this.getNewSession();
        SQLQuery sqlQuery = session.createSQLQuery(sql);
        sqlQuery.setParameter(0, AddExp);
        sqlQuery.setParameter(1, inviteCode);


        return sqlQuery.executeUpdate();
    }

    public int UpdateUserInvited(String userId) {
        String sql = "update User set IsInvited=true where userId = ?";
        Session session = this.getNewSession();
        SQLQuery sqlQuery = session.createSQLQuery(sql);
        sqlQuery.setParameter(0, userId);

        return sqlQuery.executeUpdate();
    }
}
