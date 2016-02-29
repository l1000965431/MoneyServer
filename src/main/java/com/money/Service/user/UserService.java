package com.money.Service.user;

import com.money.MoneyServerMQ.MoneyServerMQManager;
import com.money.MoneyServerMQ.MoneyServerMessage;
import com.money.Service.ServiceBase;
import com.money.Service.ServiceInterface;
import com.money.Service.Wallet.WalletService;
import com.money.config.Config;
import com.money.config.MoneyServerMQ_Topic;
import com.money.config.ServerReturnValue;
import com.money.dao.TransactionSessionCallback;
import com.money.dao.userDAO.UserDAO;
import com.money.model.HaremmasterInviteInfoModel;
import com.money.model.UserModel;
import com.money.model.WalletModel;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import until.GsonUntil;
import until.MoneyServerDate;
import until.UmengPush.UMengMessage;
import until.UmengPush.UmengSendParameter;

import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * Created by fisher on 2015/7/6.
 */

@Service("UserService")
public class UserService extends ServiceBase implements ServiceInterface {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    @Autowired
    UserDAO userDAO;

    @Autowired
    WalletService walletService;

    /**
     * //用户注册，判断验证码是否正确，正确则完成用户注册
     *
     * @param username
     * @param code
     * @param password
     * @param userType
     * @param inviteCode
     * @param openid     微信的opneid
     * @return
     */
    public int userRegister(final String username, final String code,
                            final String password, final int userType, final String inviteCode, final String openid) {
        //用户名 密码合法性

        if (!userDAO.userIsRight(username) || !userDAO.passwordIsRight(password)) {
            return ServerReturnValue.REQISTEREDUSERNAMEERROR;
        }

        final int[] state = new int[1];
        if (userDAO.excuteTransactionByCallback(new TransactionSessionCallback() {
            public boolean callback(Session session) throws Exception {
                if (userDAO.checkUserName(username)) {
                    state[0] = ServerReturnValue.REQISTEREDUSERNAMEREPEAT;

                    return false;
                }

                state[0] = userDAO.registeredHaremmaster(username, password, userType, inviteCode);

                //如果有openid 则绑定微信帐号
                if (openid != null || !openid.equals("")) {
                    BindingUserId(openid, username, password);
                }

                return true;
            }
        }) != Config.SERVICE_SUCCESS) {
            return state[0];
        }

        PushRegisteredMessage(username);
        return state[0];
    }


    public int userRegisterHaremmaster(final String username, final String code,
                                       final String password, final int userType, final String inviteCode) {
        //用户名 密码合法性

        if (!userDAO.userIsRight(username) || !userDAO.passwordIsRight(password)) {
            return ServerReturnValue.REQISTEREDUSERNAMEERROR;
        }

        if (code == null || code.length() == 0) {
            return ServerReturnValue.REQISTEREDFAILED;
        }

        final int[] state = new int[1];
        if (userDAO.excuteTransactionByCallback(new TransactionSessionCallback() {
            public boolean callback(Session session) throws Exception {
                if (userDAO.checkUserName(username)) {
                    state[0] = ServerReturnValue.REQISTEREDUSERNAMEREPEAT;
                    return false;
                }

                state[0] = userDAO.registeredHaremmaster(username, password, userType, inviteCode);
                AddHaremmasterInveite(username, code);
                return true;
            }
        }) != Config.SERVICE_SUCCESS) {
            return state[0];
        }

        PushRegisteredMessage(username);
        return state[0];
    }


    //用户注册-提交手机号，验证是否已注册，发送短信验证码
    //已注册返回2,发送验证码成功返回1,失败返回0,密码不合法返回3
    public int submitTeleNum(String username, String password) {
        //验证用户名是否已注册
        if (userDAO.checkUserName(username))
            return Config.USER_IS_REGISTER;
        else {
            //验证密码是否合法
            boolean passwordIsRight = userDAO.passwordIsRight(password);
            if (passwordIsRight) {
                //发送手机验证码，并验证是否发送成功
                return userDAO.teleCodeIsSend(username);
            } else
                return Config.PASSWORD_ILLEGAL;

        }
    }

    //退出登录
    public boolean quitLand(String userId,HttpSession session) {
        return userDAO.quitTokenLand(userId,session);
    }

    //使用用户名密码登录
    public String userLand(String username, String password, HttpSession session) {
        boolean userIsExist = userDAO.checkPassWord(username, password);
        if (userIsExist) {
            String tokenData = userDAO.landing(username, password, session);
            if (tokenData == null) {
                return ServerReturnValue.LANDFAILED;
            } else {
                return tokenData;
            }
        } else
            return ServerReturnValue.LANDUSERERROR;
    }

    /**
     * 微信登录
     * @param openid
     * @param session
     * @return
     */
    public String userLandByOpenId(String openid,HttpSession session){
        UserModel userModel = userDAO.getUSerModelByOpenId(openid);

        //跳转注册
        if(userModel == null){
            return "http://www.360.com";
        }
        String UserId = userModel.getUserId();
        String re = userDAO.landing(UserId, openid, session);

        if(re.length() > 8){
            //跳转登录成功 如果没有绑定帐号 自动绑定
            if(!userModel.getWxOpenId().equals(UserId)){
                userDAO.BindingOpenId(openid,userModel);
            }


            return "http://www.baidu.com";
        }else{
            //跳转登录失败
            return "http://www.weijujingtou.com";
        }
    }

    //用户token登陆,0登录失败，1已登录，2登录成功,3使用用户名密码登录或token不正确
    public int tokenLand(String userID, String token, HttpSession session) {

        //查看缓存中是否含有token,且客户端参数是否与token一样
        boolean tokenExist = userDAO.isTokenExist(userID, token, session);
        //若存在，查询用户登录状态，否则,应该使用用户名密码登录，返回3
        if (tokenExist) {
            return Config.ALREADLAND;
            //比对缓存token上次更新时间，判断用户是否已登录
            /*Long orderTime = System.currentTimeMillis();
            String time = Long.toString(orderTime);
            Long timeLong = Long.parseLong(time);
            boolean landFlag = userDAO.tokenTime(userID, timeLong);
            if (landFlag) {
                return Config.ALREADLAND;
            } else {
                return Config.USEPASSWORD;//userDAO.tokenLand(userID, time);
            }*/
        } else {
            return Config.USEPASSWORD;
        }
    }

    //openid登录,0登录失败，1已登录，2登录成功,3使用用户名密码登录或token不正确
    public int openidLand(HttpSession session){
        boolean tokenExist = userDAO.isTokenExist("", "", session);
        //若存在，查询用户登录状态，否则,应该使用用户名密码登录，返回3
        if (tokenExist) {
            return Config.ALREADLAND;
        } else {
            return Config.USEPASSWORD;
        }
    }

    //完善信息 0未登录；1，修改信息成功；2，信息不合法;3，token不一致;4,userType有问题 5:身份证号重复 6:邮箱重复
    public int perfectInfo(String username, String token, String info, HttpSession session) {
        //查看缓存中是否含有token,且客户端参数是否与token一样
        int flag = tokenLand(username, token, session);

        if (flag == 1) {
            //比对缓存token上次更新时间，判断用户是否已登录
           /* Long orderTime = System.currentTimeMillis();
            String time = Long.toString(orderTime);
            Long timeLong = Long.parseLong(time);
            boolean landFlag = userDAO.tokenTime(username, timeLong);
            if (landFlag) {*/
            //根据username,查找用户类型
            return userDAO.modifyInvestorInfo(username, info);
            /*} else
                return 0;*/
        } else
            return 3;
    }

    //修改信息,0未登录；1，修改信息成功；2，信息不合法;3,tooken不一致;4,userType有问题
    public int changeInfo(String userName, String token, String info, HttpSession session) {
        //查看缓存中是否含有token,且客户端参数是否与token一样
        boolean tokenExist = userDAO.isTokenExist(userName, token, session);

        if (tokenExist) {
            //比对缓存token上次更新时间，判断用户是否已登录
/*            Long orderTime = System.currentTimeMillis();
            String time = Long.toString(orderTime);
            Long timeLong = Long.parseLong(time);
            boolean landFlag = userDAO.tokenTime(userName, timeLong);
            if (landFlag) {*/
            //根据username,查找用户类型
            return userDAO.changeInvestorInfo(userName, info);
/*            } else
                return Config.NOT_LAND;*/
        } else
            return Config.NOT_LAND;

    }

    //修改密码发送验证码 3,密码不正确;2,新密码不合法；0短信未发送成功；1成功
    public int sendPasswordCode(String userName, String password, String newPassword) {
        //检查旧密码是否正确
        if (userDAO.checkPassWord(userName, password)) {
            //检查新密码是否合法
            boolean passwordIsRight = userDAO.passwordIsRight(newPassword);
            //若发送验证码成功
            int sendSuccess = userDAO.teleCodeIsSend(userName);
            if (passwordIsRight) {
                return sendSuccess;
            } else {
                return Config.NEWPASSWORD_FAILED;
            }

        } else
            return Config.PASSWORD_NOTRIGHT;
    }

    //比对验证码，修改密码
    public int changPassword(String userName, String code, String newPassWord, String oldPassWord, HttpSession session) {
        if (userDAO.checkTeleCode(userName, code)) {
            if (userDAO.changePassword(userName, newPassWord, oldPassWord, session)) {
                return 1;
            } else {
                return 0;
            }
        } else
            return 3;
    }

    public boolean checkPassWord(String userId, String passWord) {
        return userDAO.checkPassWord(userId, passWord);
    }

    /**
     * 密码找回
     *
     * @param userID
     * @param newPassWord
     * @return
     */
    public int RetrievePassword(String userID, String newPassWord) {
        if (userDAO.RetrievePassword(userID, newPassWord)) {
            return 1;
        } else {
            return 0;
        }
    }


    /**
     * 用户是否完善过信息
     *
     * @param userID 用户ID
     * @return
     */

    public boolean IsPerfectInfo(String userID) {
        UserModel userModel = userDAO.getUSerModel(userID);
        return userModel.isPerfect();
    }

    /**
     * 获得用户信息
     *
     * @param UserID
     * @return
     */
    public UserModel getUserInfo(String UserID) {
        return userDAO.getUSerModel(UserID);
    }

    public UserModel getUserInfoByOpenId(String openid){
        return userDAO.getUSerModelByOpenId(openid);
    }

    public UserModel getUserInfoNoTransaction(String UserID) {
        return userDAO.getUSerModelNoTransaction(UserID);
    }

    public UserModel getUserInfoTest(String UserID) {
        Session session = userDAO.getNewSession();
        Transaction t = session.beginTransaction();
        try {
            UserModel userModel = userDAO.getUSerModelNoTransaction(UserID);
            t.commit();
            return userModel;
        } catch (Exception e) {
            t.rollback();
            return null;
        }
    }

    /**
     * 发送手机验证码
     *
     * @param UserID
     */
    public int SendCode(String UserID) {
        //return userDAO.teleCodeIsSend(UserID);
        return 0;
    }

    /**
     * 更改用户头像
     *
     * @param UserID
     * @param Url
     * @return
     */
    public int ChangeUserHeadPortrait(String UserID, String Url) {
        UserModel userModel = userDAO.getUSerModel(UserID);

        if (userModel == null) {
            return ServerReturnValue.SERVERRETURNERROR;
        }

        userModel.setUserHeadPortrait(Url);
        userDAO.update(userModel);
        return ServerReturnValue.SERVERRETURNCOMPELETE;
    }


    //-1:参数错误 1:绑定成功 2:绑定失败 3:已经绑定
    public int BindingUserId(String OpenId, String UserId, String passWord) {
        if (OpenId == null || UserId == null || passWord == null) {
            return -1;
        }

        boolean userIsExist = userDAO.checkPassWord(UserId, passWord);

        if (!userIsExist) {
            return 2;
        }

        if (userDAO.getUSerModelByOpenId(OpenId) != null) {
            return 2;
        }

        return userDAO.BindingOpenId(OpenId, UserId);

    }

    /**
     * 是否绑定
     *
     * @param UserId
     */
    public boolean IsBinding(String UserId) {
        UserModel userModel = getUserInfo(UserId);

        if (null == userModel) {
            return false;
        }

        return !userModel.getWxOpenId().equals(UserId);
    }


    /**
     * 是否绑定支付宝帐号
     *
     * @param UserId
     */
    public boolean IsBindingalipayID(String UserId) {
        UserModel userModel = getUserInfo(UserId);

        if (userModel == null) {
            return false;
        }

        return !userModel.getAlipayId().equals("0");
    }

    public String getBindingOpenId(String UserId) {
        UserModel userModel = getUserInfo(UserId);

        if (userModel == null) {
            return null;
        }

        return userModel.getWxOpenId();
    }

    /**
     * 微信关注取消
     *
     * @param openId
     */
    public void ClearBinding(String openId) {
        UserModel userModel = userDAO.getUSerModel(openId);

        if (userModel == null) {
            return;
        }

        userModel.setWxOpenId(userModel.getUserId());
        userDAO.update(userModel);
    }


    /**
     * 获得用户token
     *
     * @param userId
     * @return
     */
    public String getUserToken(String userId,HttpSession session) {
        return userDAO.getUserToken(userId,session);
    }


    public void addUserExpByUserId(final String userId, final int AddExp) {
        userDAO.excuteTransactionByCallback(new TransactionSessionCallback() {
            @Override
            public boolean callback(Session session) throws Exception {
                userDAO.AddUserExpByUserId(userId, AddExp);
                return true;
            }
        });

    }

    public void addUserExpByInviteCode(final String InviteCode, final int AddExp) {
        userDAO.excuteTransactionByCallback(new TransactionSessionCallback() {
            @Override
            public boolean callback(Session session) throws Exception {
                userDAO.AddUserExpByInviteCode(InviteCode, AddExp);
                return true;
            }
        });
    }

    /**
     * 邀请活动
     *
     * @param userId
     * @param userAddExp   被邀请人的增加的经验
     * @param InviteCode
     * @param InviteAddExp 邀请人增加的经验
     */
    public List<Integer> addUserExp(final String userId, final int userAddExp, final String InviteCode, final int InviteAddExp) {
        final String[] InvitedUserID = {""};
        final String[] InviteUserName = {""};
        if (Objects.equals(userDAO.excuteTransactionByCallback(new TransactionSessionCallback() {
            @Override
            public boolean callback(Session session) throws Exception {

                UserModel inviteUserModel = userDAO.getUSerModelByInviteCodeNoTransaction(InviteCode);
                UserModel userModel = userDAO.getUSerModelNoTransaction(userId);

                if (inviteUserModel == null) {
                    return false;
                }

                if (userModel.isInvited()) {
                    return false;
                }

                if (inviteUserModel.getUserId().equals(userId)) {
                    return false;
                }

                InvitedUserID[0] = inviteUserModel.getUserId();
                InviteUserName[0] = userModel.getUserName();

                if (userDAO.AddUserExpByInviteCode(InviteCode, InviteAddExp) == 0 ||
                        userDAO.AddUserExpByUserId(userId, userAddExp) == 0 ||
                        userDAO.UpdateUserInvited(userId) == 0) {
                    return false;
                }

                walletService.virtualSecuritiesAdd(userId, Config.AddVirtualSecuritiesSelf);
                walletService.virtualSecuritiesAdd(inviteUserModel.getUserId(), Config.AddVirtualSecuritiesInvite);

                return true;
            }
        }), Config.SERVICE_SUCCESS)) {
            //给对方发送消息发送消息
            UmengSendParameter umengSendParameter = new UmengSendParameter(InvitedUserID[0], "微距竞投", "推荐人奖励",
                    InviteUserName + "用户将您填为推荐人,您获得了经验:" + Integer.toString(InviteAddExp), "填写推荐ID");
            String Json = GsonUntil.JavaClassToJson(umengSendParameter);
            MoneyServerMQManager.SendMessage(new MoneyServerMessage(MoneyServerMQ_Topic.MONEYSERVERMQ_PUSH_TOPIC,
                    MoneyServerMQ_Topic.MONEYSERVERMQ_PUSH_TAG, Json, "填写推荐ID"));
            List<Integer> list = new ArrayList<>();
            list.add(userAddExp);
            list.add(Config.AddVirtualSecuritiesSelf);
            return list;
        } else {
            return null;
        }


    }

    /**
     * 获取用户设置信息
     *
     * @param userId
     * @return
     */
    public String getUserSetInfo(final String userId) {

        final int[] Exp = {0};
        final int[] wallet = new int[1];
        final int[] virtualSecurities = new int[1];
        final int[] ledSecurities = new int[1];
        final boolean[] IsInvited = new boolean[1];
        userDAO.excuteTransactionByCallback(new TransactionSessionCallback() {
            @Override
            public boolean callback(Session session) throws Exception {
                UserModel userModel = userDAO.getUSerModelNoTransaction(userId);

                if (userModel == null) {
                    return false;
                }

                WalletModel walletModel = (WalletModel) userDAO.loadNoTransaction(WalletModel.class, userId);

                if (walletModel == null) {
                    return false;
                }

                Exp[0] = userModel.getUserExp();
                wallet[0] = walletModel.getWalletLines();
                virtualSecurities[0] = walletModel.getVirtualSecurities();
                ledSecurities[0] = walletModel.getLedSecurities();
                IsInvited[0] = userModel.isInvited();
                return true;
            }
        });

        Map<String, String> map = new HashMap<>();

        map.put("Exp", Integer.toString(Exp[0]));
        map.put("wallet", Integer.toString(wallet[0]));
        map.put("virtualSecurities", Integer.toString(virtualSecurities[0]));
        map.put("ledSecurities", Integer.toString(ledSecurities[0]));
        map.put("IsInvited", Boolean.toString(IsInvited[0]));
        return GsonUntil.JavaClassToJson(map);
    }

    /**
     * 注册成功发送消息
     *
     * @param userId
     */
    String PushRegisteredMessage(String userId) {
        ArrayList<Map<String, String>> MessageBody = new ArrayList<>();

        Map<String, String> map = new HashMap<>();
        map.put("imgUrl", Config.MessageUrl);
        map.put("link", "");
        map.put("messageContent", "欢迎加入微聚竞投!如有任何疑问请加官方QQ群:421814586");
        MessageBody.add(map);
        String json = GsonUntil.JavaClassToJson(MessageBody);

        UmengSendParameter umengSendParameterRed = new UmengSendParameter(new UMengMessage(userId, "messagebox", json, "注册成功发送消息"));
        String JsonRed = GsonUntil.JavaClassToJson(umengSendParameterRed);
        MoneyServerMQManager.SendMessage(new MoneyServerMessage(MoneyServerMQ_Topic.MONEYSERVERMQ_PUSH_TOPIC,
                MoneyServerMQ_Topic.MONEYSERVERMQ_PUSH_TAG, JsonRed, "注册成功发送消息"));
        return json;
    }

    /**
     * 增加群主邀请
     *
     * @param userId
     * @param code
     */
    void AddHaremmasterInveite(String userId, String code) {
        UserModel inviteUserModel = userDAO.getUSerModelByInviteCodeNoTransaction(code);

        /**
         * 如果是群主 添加到群主邀请信息里
         */
        if (inviteUserModel.isHaremmaster()) {
            HaremmasterInviteInfoModel haremmasterInviteInfoModel
                    = new HaremmasterInviteInfoModel();

            haremmasterInviteInfoModel.setHaremmasterUserId(inviteUserModel.getUserId());
            haremmasterInviteInfoModel.setInvitedDate(MoneyServerDate.getDateCurDate());
            haremmasterInviteInfoModel.setInvitedUserId(userId);
            userDAO.saveNoTransaction(haremmasterInviteInfoModel);
        }
    }
}
