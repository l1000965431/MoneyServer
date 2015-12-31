package com.money.Service.Wallet;

import com.google.gson.reflect.TypeToken;
import com.money.MoneyServerMQ.MoneyServerMQManager;
import com.money.MoneyServerMQ.MoneyServerMessage;
import com.money.Service.ServiceBase;
import com.money.Service.ServiceInterface;
import com.money.Service.alipay.PayService;
import com.money.config.Config;
import com.money.config.MoneyServerMQ_Topic;
import com.money.dao.TransactionSessionCallback;
import com.money.dao.alitarnsferDAO.TransferDAO;
import com.money.dao.userDAO.UserDAO;
import com.money.memcach.MemCachService;
import com.money.model.*;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import until.GsonUntil;
import until.MoneyServerDate;
import until.MoneyServerOrderID;
import until.MoneySeverRandom;
import until.UmengPush.UmengSendParameter;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * 钱包服务
 * <p>User: liumin
 * <p>Date: 15-7-23
 * <p>Version: 1.0
 */

@Service("WalletService")
public class WalletService extends ServiceBase implements ServiceInterface {

    @Autowired
    UserDAO generaDAO;

    @Autowired
    TransferDAO transferDAO;

    CountDownLatch countdown;

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(WalletService.class);

    /**
     * 获取用户钱包剩余金额
     *
     * @param UserID
     * @return
     */
    public int getWalletLines(String UserID) {
        WalletModel walletModel = (WalletModel) generaDAO.load(WalletModel.class, UserID);

        if (walletModel == null) {
            return 0;
        }

        return walletModel.getWalletLines();
    }

    /**
     * 充值钱包
     *
     * @param UserID 用户ID
     * @param Lines  充值金额
     * @return
     */
    public int RechargeWallet(String UserID, int Lines) throws Exception {
        WalletModel walletModel = (WalletModel) generaDAO.loadNoTransaction(WalletModel.class, UserID);

        if (walletModel == null) {
            return 0;
        }
        if (WalletAdd(UserID, Lines) == 0) {
            return 0;
        }

        return 1;
    }

    /**
     * ping++的支付服务回掉
     *
     * @param body
     * @return
     */
    public int RechargeWalletService(String body) throws Exception {
        String UserID;

        final Map<String, Object> map = GsonUntil.jsonToJavaClass(body, new TypeToken<Map<String, Object>>() {
        }.getType());

        if (map == null) {
            return Config.SENDCODE_FAILED;
        }

        final Map<String, Object> mapdata = (Map) map.get("data");
        Map<String, Object> mapobject = (Map) mapdata.get("object");
        Map<String, Object> mapMetadata = (Map) mapobject.get("metadata");

        if (mapMetadata == null) {
            return Config.SENDCODE_FAILED;
        }

        UserID = mapMetadata.get("UserID").toString();
        Double nLinse = (Double) mapobject.get("amount");
        final int Lines = (nLinse.intValue() / 100);
        final String OrderID = mapobject.get("order_no").toString();
        final String ChannelID = mapobject.get("channel").toString();

        final String finalUserID = UserID;
        if (generaDAO.excuteTransactionByCallback(new TransactionSessionCallback() {
            public boolean callback(Session session) throws Exception {
                if (RechargeWallet(finalUserID, Lines) == 0) {
                    LOGGER.error("充值失败RechargeWallet", mapdata);
                    return false;
                }
                InsertWalletOrder(OrderID,finalUserID,Lines, ChannelID);
                return true;
            }
        }) != Config.SERVICE_SUCCESS) {
            if (UserID != null || UserID.length() != 0) {
                UmengSendParameter umengSendParameter = new UmengSendParameter(UserID, "微距竞投", "充值失败", "你的充值遇到了问题请重新操作", "充值失败");
                String Json = GsonUntil.JavaClassToJson(umengSendParameter);
                MoneyServerMQManager.SendMessage(new MoneyServerMessage(MoneyServerMQ_Topic.MONEYSERVERMQ_PUSH_TOPIC,
                        MoneyServerMQ_Topic.MONEYSERVERMQ_PUSH_TAG, Json, "充值失败"));
                return Config.SENDCODE_FAILED;
            }
        }

        UmengSendParameter umengSendParameter = new UmengSendParameter(UserID, "微距竞投", "充值成功", "充值成功,成功充入" + Integer.toString(Lines) + "元", "充值成功");
        String Json = GsonUntil.JavaClassToJson(umengSendParameter);
        MoneyServerMQManager.SendMessage(new MoneyServerMessage(MoneyServerMQ_Topic.MONEYSERVERMQ_PUSH_TOPIC,
                MoneyServerMQ_Topic.MONEYSERVERMQ_PUSH_TAG, Json, "充值成功"));


        return Config.SENDCODE_SUCESS;


    }

    public int TestRechargeWallet(final String UserID, final int Lines) throws Exception {

        generaDAO.excuteTransactionByCallback(new TransactionSessionCallback() {
            public boolean callback(Session session) throws Exception {

                WalletModel walletModel = (WalletModel) generaDAO.loadNoTransaction(WalletModel.class, UserID);

                if (walletModel == null) {
                    return false;
                }

                return WalletAdd(UserID, Lines) != 0;

            }
        });

        return 1;
    }

    /**
     * 花费
     *
     * @param CostLines
     * @return
     */
    public boolean CostLines(String UserID, int CostLines) {
        WalletModel walletModel = (WalletModel) generaDAO.loadNoTransaction(WalletModel.class, UserID);

        if (walletModel == null) {
            return false;
        }

        if (!walletModel.IsLinesEnough(CostLines)) {
            return false;
        }

        return WalletCost(UserID, CostLines) != 0;

    }

    /**
     * ping++的提现服务
     *
     * @param body
     * @return
     */
    public int TranferLinesService(String body) throws Exception {
        Map<String, Object> map = GsonUntil.jsonToJavaClass(body, new TypeToken<Map<String, Object>>() {
        }.getType());

        if (map == null) {
            countdown.countDown();
            return Config.SENDCODE_FAILED;
        }

        Map<String, Object> mapdata = (Map) map.get("data");
        Map<String, Object> mapobject = (Map) mapdata.get("object");

        String status = mapobject.get("status").toString();
        double ammont = Double.valueOf(mapobject.get("amount").toString()) / 100.0;
        String openId = mapobject.get("recipient").toString();
        String orderId = mapobject.get("transaction_no").toString();
        String description = mapobject.get("description").toString();
        if (status.equals("paid")) {
            TransferLines(orderId, openId, (int) ammont, status, description);
        } else {
            String temp[] = description.split("_");
            String BatchId = temp[0];
            String Id = temp[1];
            String key = "wxTransferFailList" + BatchId;

            List<String> errorlist = new ArrayList<>();
            errorlist.add( Id );
            errorlist.add( "支付回掉失败" );
            String json = GsonUntil.JavaClassToJson( errorlist );
            MemCachService.lpush(key.getBytes(), json.getBytes());
        }

        countdown.countDown();

        return Config.SENDCODE_SUCESS;
    }

    public boolean TransferLines(final String OrderId, final String OpenId, final int Lines, final String status, String description) {

        //修改为更改内存内容 最后在一个job里完成所有的插入操作和修改操作
/*        if (Objects.equals(generaDAO.excuteTransactionByCallback(new TransactionSessionCallback() {
            public boolean callback(Session session) throws Exception {
                UserModel userModel = generaDAO.getUSerModelByOpenIdNoTransaction(OpenId);
                if (userModel == null) {
                    return false;
                }

                InsertTransferOrder(userModel, OrderId, OpenId, Lines, status);

                WalletModel walletModel = (WalletModel) generaDAO.loadNoTransaction(WalletModel.class, userModel.getUserId());

                if (walletModel == null) {
                    return false;
                }

                if (!walletModel.IsLinesEnough(Lines)) {
                    return false;
                }

                if (WalletCost(userModel.getUserId(), Lines) == 0) {
                    return false;
                }

                return true;
            }
        }), Config.SERVICE_SUCCESS)) ;*/
        List<String> list = new ArrayList();
        String temp[] = description.split("_");
        String BatchId = temp[0];
        String Id = temp[1];
        String key = "wxtransferWinList::" + BatchId;
        list.add(Id);
        list.add(Integer.toString(Lines));
        String json = GsonUntil.JavaClassToJson(list);
        MemCachService.lpush(key.getBytes(), json.getBytes());
        return false;
    }


    /**
     * 插入充值订单
     *
     * @param OrderID
     * @param Lines
     * @param ChannelID
     */
    public void InsertWalletOrder(String OrderID,String UserId ,int Lines, String ChannelID) throws Exception {

        WalletOrderModel walletOrderModel = new WalletOrderModel();
        walletOrderModel.setOrderID(OrderID);
        walletOrderModel.setWalletLines(Lines);
        walletOrderModel.setWalletChannel(ChannelID);
        walletOrderModel.setOrderDate(MoneyServerDate.getDateCurDate());
        walletOrderModel.setUserId( UserId );
        generaDAO.saveNoTransaction(walletOrderModel);

    }

    public void InsertTransferOrder(UserModel userModel, String OrderId, String OpenId, int Lines,int poundage, String status) throws ParseException {
        TransferModel transferModel = new TransferModel();
        transferModel.setOrderId(OrderId);
        transferModel.setOpenId(OpenId);
        transferModel.setTransferLines(Lines);
        transferModel.setTransferDate(MoneyServerDate.getDateCurDate());
        transferModel.setUserId(userModel.getUserId());
        transferModel.setStatus(status);
        transferModel.setTransferLinesPoundage( poundage );
        generaDAO.saveNoTransaction(transferModel);
    }


    public boolean IsWalletEnough(String UserID, int Lines) {
        WalletModel walletModel = (WalletModel) generaDAO.loadNoTransaction(WalletModel.class, UserID);

        if (walletModel == null) {
            return false;
        }

        return walletModel.IsLinesEnough(Lines);
    }


    public boolean IsWalletEnoughTransaction(String UserID, int Lines) {
        WalletModel walletModel = (WalletModel) generaDAO.load(WalletModel.class, UserID);

        if (walletModel == null) {
            return false;
        }

        return walletModel.IsLinesEnough(Lines);
    }

    public boolean IsvirtualSecuritiesEnough(String UserID, int Lines) {
        WalletModel walletModel = (WalletModel) generaDAO.loadNoTransaction(WalletModel.class, UserID);

        if (walletModel == null) {
            return false;
        }

        return walletModel.IsvirtualSecuritiesEnough(Lines);
    }

    /**
     * 金额增加
     *
     * @param UserId
     * @param Lines
     * @return
     */
    private int WalletAdd(String UserId, int Lines) {
        String sql = "update wallet set WalletLines = WalletLines+? where UserID = ? ";
        Session session = generaDAO.getNewSession();
        SQLQuery query = session.createSQLQuery(sql);
        query.setParameter(0, Lines);
        query.setParameter(1, UserId);
        return query.executeUpdate();
    }

    /**
     * 金额花费
     *
     * @param UserId
     * @param Lines
     * @return
     */
    private int WalletCost(String UserId, int Lines) {
        String sql = "update wallet set WalletLines = WalletLines-? where UserID = ? and WalletLines-? >= 0";
        Session session = generaDAO.getNewSession();
        SQLQuery query = session.createSQLQuery(sql);
        query.setParameter(0, Lines);
        query.setParameter(1, UserId);
        query.setParameter(2, Lines);
        return query.executeUpdate();
    }

    /**
     * 微劵花费
     *
     * @param UserId
     * @param Lines
     * @return
     */
    public int virtualSecuritiesCost(String UserId, int Lines) {
        if (Lines <= 0) {
            return 0;
        }

        String sql = "update wallet set virtualSecurities = virtualSecurities-? where UserID = ? and virtualSecurities-? >= 0";
        Session session = generaDAO.getNewSession();
        SQLQuery query = session.createSQLQuery(sql);
        query.setParameter(0, Lines);
        query.setParameter(1, UserId);
        query.setParameter(2, Lines);
        return query.executeUpdate();
    }

    /**
     * 微劵充值
     *
     * @param UserId
     * @param Lines
     * @return
     */
    public int virtualSecuritiesAdd(String UserId, int Lines) {
        String sql = "update wallet set virtualSecurities = virtualSecurities+? where UserID = ? and virtualSecurities+?<=?";
        Session session = generaDAO.getNewSession();
        SQLQuery query = session.createSQLQuery(sql);
        query.setParameter(0, Lines);
        query.setParameter(1, UserId);
        query.setParameter(2, Lines);
        query.setParameter(3, Config.MaxVirtualSecurities);
        return query.executeUpdate();
    }

    /**
     * 获得支付宝提现申请清单
     *
     * @return
     */
    public List GetAliTranserOrder() {

        final List[] list = new List[1];
        transferDAO.excuteTransactionByCallback(new TransactionSessionCallback() {
            public boolean callback(Session session) throws Exception {
                list[0] = transferDAO.GetAliTransferOdrer();
                return true;
            }
        });


        return list[0];
    }

    /**
     * 获得支付宝提现申请详情信息
     *
     * @return
     */
    public List<AlitransferModel> GetAliTranserInfo(final int page) {
        final List[] list = new List[1];
        transferDAO.excuteTransactionByCallback(new TransactionSessionCallback() {
            public boolean callback(Session session) throws Exception {
                list[0] = transferDAO.GetAliTransferInfo(page);
                return true;
            }
        });


        return list[0];
    }


    /**
     * 获得微信提现详细信息
     *
     * @param page
     * @return
     */
    public List<WxTranferModel> GetWxTranserInfo(final int page, StringBuffer Out_BatchId) {
        final List<WxTranferModel>[] list = new List[1];
        transferDAO.excuteTransactionByCallback(new TransactionSessionCallback() {
            public boolean callback(Session session) throws Exception {
                list[0] = transferDAO.GetWxTransferInfo(page);
                return true;
            }
        });

        //缓存提现列表
        Out_BatchId.append(MoneyServerOrderID.GetOrderID(Integer.toString(MoneySeverRandom.getRandomNum(0, 100))));
        String key = "wxTransferPass" + "::" + Out_BatchId;
        for (WxTranferModel wxTranferModel : list[0]) {
            List listTemp = new ArrayList();
            listTemp.add(wxTranferModel.getId());
            listTemp.add(wxTranferModel.getLines());
            listTemp.add(wxTranferModel.getOpenId());
            listTemp.add(wxTranferModel.getUserId());
            listTemp.add(MoneyServerOrderID.GetOrderID(wxTranferModel.getUserId()) + wxTranferModel.getId());
            String json = GsonUntil.JavaClassToJson(listTemp);

            MemCachService.lpush(key.getBytes(), json.getBytes());
        }

        MemCachService.SetTimeOfKey(key.getBytes(), 1800);
        return list[0];
    }

    /**
     * 获得微信提现申请数量
     *
     * @return
     */
    public int GetWxTranserNum() {
        final int[] Num = {0};
        transferDAO.excuteTransactionByCallback(new TransactionSessionCallback() {
            public boolean callback(Session session) throws Exception {
                Num[0] = transferDAO.GetWxTransferNum();
                return true;
            }
        });

        return Num[0];
    }


    /**
     * 获得微信提现申请失败订单数量
     *
     * @return
     */
    public int GetWxFailTranserNum() {
        final int[] Num = {0};
        transferDAO.excuteTransactionByCallback(new TransactionSessionCallback() {
            public boolean callback(Session session) throws Exception {
                Num[0] = transferDAO.GetWxFailTransferNum();
                return true;
            }
        });

        return Num[0];
    }


    /**
     * 获得支付宝提现申请数量
     *
     * @return
     */
    public int GetAliTranserNum() {
        final int[] Num = {0};
        transferDAO.excuteTransactionByCallback(new TransactionSessionCallback() {
            public boolean callback(Session session) throws Exception {
                Num[0] = transferDAO.GetAliTransferNum();
                return true;
            }
        });

        return Num[0];
    }


    /**
     * 获得支付宝提现申请失败订单数量
     *
     * @return
     */
    public int GetAliFailTranserNum() {
        final int[] Num = {0};
        transferDAO.excuteTransactionByCallback(new TransactionSessionCallback() {
            public boolean callback(Session session) throws Exception {
                Num[0] = transferDAO.GetAliFailTransferNum();
                return true;
            }
        });

        return Num[0];
    }

    public String BindingalipayId(final String UserId, final String AlipayId, final String RealName) {
        return transferDAO.excuteTransactionByCallback(new TransactionSessionCallback() {
            public boolean callback(Session session) throws Exception {
                UserModel userModel = generaDAO.getUSerModelNoTransaction(UserId);
                if (userModel == null) {
                    return false;
                }

                if (!userModel.getAlipayId().equals("0")) {
                    return false;
                }

                userModel.setAlipayRealName(RealName);
                userModel.setAlipayId(AlipayId);
                generaDAO.updateNoTransaction(userModel);
                return true;
            }
        });
    }

    /**
     * 清空支付宝绑定
     *
     * @param UserId
     * @return
     */
    public String ClearalipayId(final String UserId) {

        return transferDAO.excuteTransactionByCallback(new TransactionSessionCallback() {
            public boolean callback(Session session) throws Exception {
                UserModel userModel = generaDAO.getUSerModelNoTransaction(UserId);

                if (userModel == null) {
                    return false;
                }

                userModel.setAlipayRealName("");
                userModel.setAlipayId("0");
                generaDAO.updateNoTransaction(userModel);
                return true;
            }
        });
    }

    /**
     * 支付宝提现
     *
     * @param UserId
     * @param Lines
     * @return
     */
    //0:失败 1:提交成功 2:余额不足
    public int alipayTransfer(final String UserId, final int Lines) {
        //计算支付宝的手续费
        final int poundageResult = (int)getPoundage( Lines,0.005,1.0,25.0 );

        final int costLines = Lines + poundageResult;

        final int[] state = {1};
        transferDAO.excuteTransactionByCallback(new TransactionSessionCallback() {
            public boolean callback(Session session) throws Exception {

                UserModel userModel = generaDAO.getUSerModelNoTransaction(UserId);

                if (userModel == null) {
                    state[0] = 0;
                    return false;
                }

                if (!CostLines(UserId, costLines)) {
                    state[0] = 2;
                    return false;
                }

                if (transferDAO.Submitalitansfer(userModel.getUserId(), Lines,poundageResult,userModel.getAlipayRealName(), userModel.getAlipayId()) == 0) {
                    state[0] = -1;
                    Object[] objects = new Object[3];
                    objects[0] = userModel;
                    objects[1] = Lines;
                    objects[2] = MoneyServerDate.getDateCurDate();
                    LOGGER.error("提交提现申请失败", objects);
                    return false;
                }


                InsertTransferOrder(userModel, MoneyServerOrderID.GetOrderID(UserId), userModel.getAlipayId(), costLines,poundageResult, "alipay");
                return true;
            }
        });


        return state[0];
    }


    /**
     * 微信提现
     *
     * @param UserId
     * @param Lines
     * @return
     */
    //0:失败 1:提交成功 2:余额不足
    public int WxpayTransfer(final String UserId, int Lines) {

        //计算支付宝的手续费
        final int costLines = Lines;

        final int[] state = {1};
        transferDAO.excuteTransactionByCallback(new TransactionSessionCallback() {
            public boolean callback(Session session) throws Exception {

                UserModel userModel = generaDAO.getUSerModelNoTransaction(UserId);

                if (userModel == null) {
                    state[0] = 0;
                    return false;
                }

                if (!CostLines(UserId, costLines)) {
                    state[0] = 2;
                    return false;
                }

                if (transferDAO.SubmitaliWxtansfer(userModel.getUserId(), costLines,0, userModel.getRealName(), userModel.getWxOpenId()) == 0) {
                    state[0] = -1;
                    Object[] objects = new Object[3];
                    objects[0] = userModel;
                    objects[1] = costLines;
                    objects[2] = MoneyServerDate.getDateCurDate();
                    LOGGER.error("提交提现申请失败", objects);
                    return false;
                }

                InsertTransferOrder(userModel, MoneyServerOrderID.GetOrderID(UserId), userModel.getWxOpenId(), costLines,0, "wxpay");
                return true;
            }
        });


        return state[0];
    }

    /**
     * 支付宝批量付款通知
     */
    public String alipayTransferNotify(Map<String, String> NotifyInfo) {

        final String Batchno = NotifyInfo.get("batch_no");
        final String Payuserid = NotifyInfo.get("pay_user_id");
        final String Payusername = NotifyInfo.get("pay_user_name");
        final String Notifytime = NotifyInfo.get("notify_time");
        final String Faildetails = NotifyInfo.get("fail_details");
        final String Successdetails = NotifyInfo.get("success_details");

        return transferDAO.excuteTransactionByCallback(new TransactionSessionCallback() {
            public boolean callback(Session session) throws Exception {
                //同一写入到批量打款列表中
                /*                AliTransferNotifyModel aliTransferNotifyModel = new AliTransferNotifyModel();
                aliTransferNotifyModel.setBatchno(Batchno);
                aliTransferNotifyModel.setPayuserid(Payuserid);
                aliTransferNotifyModel.setPayusername(Payusername);
                aliTransferNotifyModel.setPayStates(1);
                aliTransferNotifyModel.setPayDate(MoneyServerDate.StrToDate(Notifytime));
                session.save(aliTransferNotifyModel);*/

                List<List<String>> FaildetailsList;
                List<List<String>> SuccessdetailsList;
                if (Faildetails != null) {
                    FaildetailsList = PayService.ParsingNotifyParam(Faildetails);

                    if (FaildetailsList == null) {
                        LOGGER.error("FaildetailsList == null");
                        return false;
                    }

                    int TotalIndex = 0;

                    for (List<String> aFaildetailsList : FaildetailsList) {
                        TotalIndex++;
                        AlitransferModel alitransferModel = (AlitransferModel) transferDAO.loadNoTransaction(AlitransferModel.class, Integer.valueOf(aFaildetailsList.get(0)));
                        if (alitransferModel == null) {
                            continue;
                        }
                        double linestemp = Double.valueOf(aFaildetailsList.get(3));
                        if (alitransferModel.getAliEmail().equals(aFaildetailsList.get(1)) &&
                                alitransferModel.getRealName().equals(aFaildetailsList.get(2)) &&
                                alitransferModel.getLines() == (int) linestemp) {
                            alitransferModel.setIsFaliled(true);
                            alitransferModel.setErrorInfo( aFaildetailsList.get(5) );
                            session.update(alitransferModel);

                            //将钱款打回到用户账户中
                            RechargeWallet( alitransferModel.getUserId(),
                                    alitransferModel.getLines()+alitransferModel.getPoundageResult() );

                            //发送失败通知
                            UmengSendParameter umengSendParameter = new UmengSendParameter( alitransferModel.getUserId(),"微距竞投","支付宝提现失败","您的支付宝提现请求被驳回,请检查绑定的支付宝帐号是否正确." +
                                    "点击解绑支付宝帐号,重新进行绑定","支付宝提现失败" );
                            String Json = GsonUntil.JavaClassToJson( umengSendParameter );
                            MoneyServerMQManager.SendMessage(new MoneyServerMessage(MoneyServerMQ_Topic.MONEYSERVERMQ_PUSH_TOPIC,
                                    MoneyServerMQ_Topic.MONEYSERVERMQ_PUSH_TAG, Json, "购买成功"));

                        }

                        if( TotalIndex == 200 ){
                            TotalIndex = 0;
                            session.flush();
                        }

                    }
                }

                if (Successdetails != null) {
                    SuccessdetailsList = PayService.ParsingNotifyParam(Successdetails);

                    if (SuccessdetailsList == null) {
                        LOGGER.error("SuccessdetailsList == null");
                        return false;
                    }

                    int TotalLines = 0;
                    int TotalIndex = 0;
                    for (List<String> aSuccessdetailsList : SuccessdetailsList) {
                        TotalIndex++;
                        AlitransferModel alitransferModel = (AlitransferModel) transferDAO.loadNoTransaction(AlitransferModel.class, Integer.valueOf(aSuccessdetailsList.get(0)));
                        if (alitransferModel == null) {
                            continue;
                        }

                        double linestemp = Double.valueOf(aSuccessdetailsList.get(3));
                        if (alitransferModel.getAliEmail().equals(aSuccessdetailsList.get(1)) &&
                                alitransferModel.getRealName().equals(aSuccessdetailsList.get(2)) &&
                                alitransferModel.getLines() == (int) linestemp) {
                            session.delete(alitransferModel);
                        }

                        if( TotalIndex == 200 ){
                            TotalIndex = 0;
                            session.flush();
                        }


                        //计算该批次的提现总金额
                        TotalLines += linestemp;
                    }

                    //写入提现批次记录表
                    BatchTransferModel batchTransferModel = new BatchTransferModel();
                    batchTransferModel.setBatchId(Batchno);
                    batchTransferModel.setTransferchannel("支付宝");
                    batchTransferModel.setTransferDate(MoneyServerDate.getDateCurDate());
                    batchTransferModel.setTransferLines(TotalLines);
                    generaDAO.saveNoTransaction(batchTransferModel);
                }

                return true;
            }
        });
    }

    /**
     * 群主打款通知
     * @param NotifyInfo
     * @return
     */
    public String HaremmasterTransferNotify(Map<String, String> NotifyInfo){
        final String Batchno = NotifyInfo.get("batch_no");
        final String Payuserid = NotifyInfo.get("pay_user_id");
        final String Payusername = NotifyInfo.get("pay_user_name");
        final String Notifytime = NotifyInfo.get("notify_time");
        final String Faildetails = NotifyInfo.get("fail_details");
        final String Successdetails = NotifyInfo.get("success_details");

        return transferDAO.excuteTransactionByCallback(new TransactionSessionCallback() {
            @Override
            public boolean callback(Session session) throws Exception {

                List<List<String>> FaildetailsList;
                List<List<String>> SuccessdetailsList;

                if (Faildetails != null) {
                    FaildetailsList = PayService.ParsingNotifyParam(Faildetails);

                    if (FaildetailsList == null) {
                        LOGGER.error("FaildetailsList == null");
                        return false;
                    }




                }

                if (Successdetails != null) {
                    SuccessdetailsList = PayService.ParsingNotifyParam(Successdetails);

                    if (SuccessdetailsList == null) {
                        LOGGER.error("SuccessdetailsList == null");
                        return false;
                    }

                    int TotalIndex = 0;
                    for (List<String> aSuccessdetailsList : SuccessdetailsList) {
                        TotalIndex++;
                        HaremmasterModel haremmasterModel =
                                (HaremmasterModel) transferDAO.loadNoTransaction(HaremmasterModel.class, aSuccessdetailsList.get(0).toString());
                        if (haremmasterModel == null) {
                            continue;
                        }

                        //
                        haremmasterModel.setMonthPushMoney(0);
                        session.update( haremmasterModel );

                        double linestemp = Double.valueOf(aSuccessdetailsList.get(3));
                        HaremmasterTransferModel haremmasterTransferModel
                                = new HaremmasterTransferModel();
                        haremmasterTransferModel.setPushMoney( (int)linestemp );
                        haremmasterTransferModel.setPushMoneyDate( MoneyServerDate.getDateCurDate() );
                        session.save( linestemp );

                        if( TotalIndex == 200 ){
                            TotalIndex = 0;
                            session.flush();
                        }
                    }


                }


                return true;
            }
        });
    }



    /**
     * 计算跟投花费
     *
     * @param Lines             实际金额
     * @param AdvanceNum        购买期数
     * @param VirtualSecurities 微卷使用
     * @return
     */
    public int getCostLines(int Lines, int AdvanceNum, int VirtualSecurities) {
        if (AdvanceNum > 1) {
            return Lines * AdvanceNum;
        } else {
            return Lines - VirtualSecurities > 0 ? Lines - VirtualSecurities : 0;
        }
    }

    /**
     * 微信开始打款
     *
     * @param BatchId 打款流水号
     * @return
     */
    public String WxStartTransfer(final String BatchId) throws InterruptedException {
        String key = "wxTransferPass::" + BatchId;

        if (!MemCachService.isExistUpdate(key, "10000")) {
            return "重复提交提现列表";
        }

        int len = (int) MemCachService.getLen(key.getBytes());
        if (len == 0) {
            return "提现列表不存在";
        }

        //线程数量
        int threadNum = len % 200 == 0 ? len / 200 : len / 200 + 1;
        int sleepTime = MoneySeverRandom.getRandomNum(0, 10);
        countdown = new CountDownLatch(len);
        List<WxTransfer> listThread = new ArrayList<>();
        for (int i = 0; i < threadNum; ++i) {
            WxTransfer wxTransfer = new WxTransfer(sleepTime * 100, BatchId, i, countdown);
            listThread.add(wxTransfer);
            wxTransfer.start();
        }

        countdown.await();

        String state;

        wxTransferWinList(BatchId);
        if (FailTransfer(BatchId)) {
            state = "有失败订单,请通知管理员检查";
        } else {
            state = "提现成功";
        }

        //清理键值
        String passKey = "wxTransferPass::" + BatchId;
        MemCachService.unLockRedisKey(passKey);
        MemCachService.RemoveValue(passKey.getBytes());


        return state;
    }

    public void wxTransferWinList(String BatchId) {
        String winKey = "wxtransferWinList::" + BatchId;
        int winLen = (int) MemCachService.getLen(winKey.getBytes());
        List<byte[]> winList = MemCachService.lrang(winKey.getBytes(), 0, winLen - 1);

        StringBuffer sqlWin = new StringBuffer("delete from wxtransfer where Id in (WinId)");
        StringBuffer FailId = new StringBuffer();
        int WinIndex = 0;
        int sqlNum = 0;
        int TotalLines = 0;
        for (byte[] temp : winList) {
            String json = new String(temp);
            List<String> List = GsonUntil.jsonListToJavaClass(json, new TypeToken<List<String>>() {
            }.getType());


            WinIndex++;
            sqlNum++;
            FailId.append(List.get(0));
            FailId.append(",");

            //计算该批次总得提现金额
            TotalLines += Integer.valueOf(List.get(1).replace(".00", "").toString());

            if (WinIndex == 100 || winList.size() == sqlNum) {
                WinIndex = 0;
                FailId.deleteCharAt(FailId.length() - 1);
                String sql = sqlWin.toString().replace("WinId", FailId);

                Session session = generaDAO.getNewSession();
                Transaction t = session.beginTransaction();
                try {
                    session.createSQLQuery(sql).executeUpdate();
                    t.commit();
                } catch (Exception e) {
                    t.rollback();
                    break;
                }
                FailId.setLength(0);
            }

        }

        //计算该批次的总提现金额并写入该批次的总提现金额
        if (TotalLines != 0) {
            BatchTransferModel batchTransferModel = new BatchTransferModel();
            batchTransferModel.setBatchId(BatchId);
            batchTransferModel.setTransferchannel("微信");
            batchTransferModel.setTransferDate(MoneyServerDate.getDateCurDate());
            batchTransferModel.setTransferLines(TotalLines);
            generaDAO.save(batchTransferModel);
        }


        MemCachService.RemoveValue(winKey.getBytes());
    }

    public boolean FailTransfer(String BatchId) {
        String failKey = "wxTransferFailList::" + BatchId;
        int failLen = (int) MemCachService.getLen(failKey.getBytes());
        List<byte[]> failList = MemCachService.lrang(failKey.getBytes(), 0, failLen - 1);

        if (failList == null || failList.size() == 0) {
            return false;
        }

        StringBuffer sqlFail = new StringBuffer("update wxtransfer set IsFaliled=TRUE,ErrorInfo=case Id WhenCase end where Id in (FailedId)");
        StringBuffer FailId = new StringBuffer();
        StringBuffer WhenCase = new StringBuffer();
        int FaliIndex = 0;
        int sqlNum = 0;
        for (byte[] temp : failList) {
            String Json = new String(temp);

            List<String> jsonlist = GsonUntil.jsonListToJavaClass( Json,new TypeToken<List<String>>(){}.getType() );

            FaliIndex++;
            sqlNum++;
            FailId.append(jsonlist.get(0));
            FailId.append(",");

            WhenCase.append("when "+jsonlist.get(0)+" then "+"'"+jsonlist.get(1)+"'"+" ");

            if (FaliIndex == 100 || failList.size() == sqlNum) {
                FaliIndex = 0;
                FailId.deleteCharAt(FailId.length() - 1);

                String sql = sqlFail.toString().replace("FailedId", FailId).replace( "WhenCase",WhenCase );

                Session session = generaDAO.getNewSession();
                Transaction t = session.beginTransaction();
                try {
                    session.createSQLQuery(sql).executeUpdate();
                    t.commit();
                } catch (Exception e) {
                    t.rollback();
                    break;
                }
                FailId.setLength(0);
                WhenCase.setLength( 0 );
            }

            //微信提现失败 打款到原账户
            Session session1 = generaDAO.getNewSession();
            Transaction t1 = session1.beginTransaction();

            WxTranferModel wxTranferModel = (WxTranferModel)generaDAO.loadNoTransaction( WxTranferModel.class,Integer.valueOf( jsonlist.get(0) ) );
            try {
                RechargeWallet( wxTranferModel.getUserId(),wxTranferModel.getLines()+wxTranferModel.getPoundageResult() );
                t1.commit();
            } catch (Exception e) {
                LOGGER.error( "WX提现打回失败{}",wxTranferModel );
                t1.rollback();
            }
            //发送失败通知
            UmengSendParameter umengSendParameter = new UmengSendParameter( wxTranferModel.getUserId(),"微距竞投","微信提现失败","您的微信提现请求被驳回,请重新提交"
                    ,"支付宝提现失败" );
            String Json1 = GsonUntil.JavaClassToJson( umengSendParameter );
            MoneyServerMQManager.SendMessage(new MoneyServerMessage(MoneyServerMQ_Topic.MONEYSERVERMQ_PUSH_TOPIC,
                    MoneyServerMQ_Topic.MONEYSERVERMQ_PUSH_TAG, Json1, "购买成功"));
        }
        MemCachService.RemoveValue(failKey.getBytes());
        return true;
    }

    /**
     * 计算提现手续费
     * @param rate 利率
     * @return
     */
    double getPoundage( int Lines,double rate,double MinPoundage,double MaxPoundage ){
        //计算支付宝的手续费
        double poundage = Lines * rate;
        double poundageResult = Math.ceil(poundage);

        if (poundageResult < MinPoundage) {
            poundageResult = MinPoundage;
        } else if (poundageResult > MaxPoundage) {
            poundageResult = MaxPoundage;
        }

        return poundageResult;
    }

}
