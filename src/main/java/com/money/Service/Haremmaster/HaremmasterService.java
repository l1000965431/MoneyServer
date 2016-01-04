package com.money.Service.Haremmaster;

import com.money.Service.ServiceBase;
import com.money.Service.ServiceInterface;
import com.money.config.Config;
import com.money.dao.BaseDao;
import com.money.dao.GeneraDAO;
import com.money.dao.TransactionCallback;
import com.money.dao.TransactionSessionCallback;
import com.money.dao.userDAO.UserDAO;
import com.money.model.AlitransferModel;
import com.money.model.HaremmasterModel;
import com.money.model.UserModel;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import until.GsonUntil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 群主模块服务
 */

@Service("HaremmasterService")
public class HaremmasterService extends ServiceBase implements ServiceInterface {

    private static final Logger LOGGER = LoggerFactory.getLogger(HaremmasterService.class);

    @Autowired
    GeneraDAO generaDAO;

    @Autowired
    UserDAO userDAO;

    /**
     * 设置一个帐号成为群主
     *
     * @param userId
     * @return
     */
    public int SetUserHaremmaster(final String userId) {
        if (generaDAO.excuteTransactionByCallback(new TransactionSessionCallback() {
            @Override
            public boolean callback(Session session) throws Exception {
                UserModel userModel = userDAO.getUSerModelNoTransaction(userId);
                if (userModel == null) {
                    return false;
                }
                userModel.setHaremmaster(true);
                session.update(userModel);

                HaremmasterModel haremmasterModel = new HaremmasterModel();
                haremmasterModel.setMonthPushMoney(0);
                haremmasterModel.setMonthRecharge(0);
                haremmasterModel.setTotalRecharge(0);
                haremmasterModel.setProportion(Config.Haremmaster_Proportion);
                haremmasterModel.setUserId(userModel.getUserId());
                session.save(haremmasterModel);
                return true;
            }
        }).equals(Config.SERVICE_FAILED)) {
            return 0;
        } else {
            LOGGER.info(userId + "设置群主");
            return 1;
        }
    }

    /**
     * 根据总的充值金额计算提成比例
     *
     * @param TotalRecharge
     * @return
     */
    float getProportion(int TotalRecharge) {
        return Config.Haremmaster_Proportion;
    }

    /**
     * 刷新月每天充值金额
     *
     * @param MoneyDayRecharge
     * @param userId
     * @return
     */
    int updateMonthDayRecharge(int MoneyDayRecharge, String userId) {
        String sql = "update haremmaster set MonthDayRecharge=MonthDayRecharge+? where userId=? ";
        Session session = generaDAO.getNewSession();
        return session.createSQLQuery(sql)
                .setParameter(0, MoneyDayRecharge)
                .setParameter(1, userId)
                .executeUpdate();
    }

    /**
     * 计算充值总额
     *
     * @param MoneyDayRecharge
     * @param userId
     * @return
     */
    int updateTotalRecharge(int MoneyDayRecharge, String userId) {
        String sql = "update haremmaster set TotalRecharge=MonthDayRecharge+? where userId=? ";
        Session session = generaDAO.getNewSession();
        return session.createSQLQuery(sql)
                .setParameter(0, MoneyDayRecharge)
                .setParameter(1, userId)
                .executeUpdate();
    }

    /**
     * 每月结算
     *
     * @return
     */
    int updateMonthRecharge() {
        String sql = "update haremmaster set MonthRecharge=MonthDayRecharge,MonthDayRecharge = 0,MonthPushMoney=MonthRecharge*Proportion where 1=1 ";
        Session session = generaDAO.getNewSession();
        return session.createSQLQuery(sql)
                .executeUpdate();
    }

    /**
     * 刷新提成比例
     *
     * @param TotalRecharge
     * @param userId
     */
    void updateProportion(int TotalRecharge, String userId) {
        float proportion = getProportion(TotalRecharge);
        String sql = "update haremmaster set Proportion = ? where userId=?";
        Session session = generaDAO.getNewSession();
        session.createSQLQuery(sql)
                .setParameter(0, proportion)
                .setParameter(1, userId)
                .executeUpdate();
    }

    /**
     * 刷新总的邀请人数量
     *
     * @param userId
     */
    void updateTotalInviteNum(String userId) {
        String sql = "update haremmaster set TotalInvitePeopleNum=(select count(InvitedUserId) from " +
                "haremmasterinviteinfo where haremmasterinviteinfo.HaremmasterUserId = ?) where userId=?";
        Session session = generaDAO.getNewSession();
        session.createSQLQuery(sql)
                .setParameter(0, userId)
                .setParameter(1, userId)
                .executeUpdate();
    }


    /**
     * 获得群主ID列表
     *
     * @return
     */
    List<String> GetHaremmasterIdList(int page, int pagenum) {
        String sql = "select userId from haremmaster limit ?,?";
        Session session = generaDAO.getNewSession();
        return session.createSQLQuery(sql)
                .setParameter(0, page*pagenum)
                .setParameter(1, pagenum)
                .list();
    }

    /**
     * 获得群主邀请的玩家ID列表
     *
     * @param HaremmasterId
     * @return
     */
    List GetHaremmasterInvitedUserIDList(String HaremmasterId, int page, int pagenum) {
        String sql = "select InvitedUserId,InvitedDate from haremmasterinviteinfo where HaremmasterUserId=? limit ?,?";
        Session session = generaDAO.getNewSession();
        return session.createSQLQuery(sql)
                .setParameter(0, HaremmasterId)
                .setParameter(1, page*pagenum)
                .setParameter(2, pagenum)
                .list();
    }

    int GetHaremmasterTotalRecharge(String HaremmasterId) {
        String sql = "select TotalRecharge from haremmaster where userId=?";
        Session session = generaDAO.getNewSession();
        Integer re = (Integer) session.createSQLQuery(sql)
                .setParameter(0, HaremmasterId)
                .uniqueResult();
        if (re == null) {
            return 0;
        } else {
            return re;
        }
    }


    /**
     * 计算每天的充值金额
     *
     * @param userId
     * @return
     */
    int CalculateMonthDayRecharge(String userId, Date InviteDate, Date MonthDate) {
        /*String sql = "select sum(WalletLines) from walletorder where userId = ? and ? < OrderDate and date_format(OrderDate,'%Y-%m-%d') = date_format(?,'%Y-%m-%d')";*/
        String sql = "select sum(WalletLines) from walletorder where userId = ? ";
        Session session = generaDAO.getNewSession();
        BigDecimal re = (BigDecimal) session.createSQLQuery(sql)
                .setParameter(0, userId)
                /*.setParameter(1, InviteDate)
                .setParameter(2, MonthDate)*/
                .uniqueResult();
        if (re == null) {
            return 0;
        } else {
            return re.intValue();
        }
    }

    /**
     * 当月每天结算
     *
     * @param MonthDate 当前日期
     */
    public void SettlementMonthDay(final Date MonthDate) {
        if (MonthDate == null) {
            return;
        }

        LOGGER.info("群主每月每日结算");

        generaDAO.excuteTransactionByCallback(new TransactionSessionCallback() {
            @Override
            public boolean callback(Session session) throws Exception {
                int HaremmasterIdpage = 0;
                int HaremmasterIdpagenum = 30;
                List<String> HaremmasterIdList = GetHaremmasterIdList(HaremmasterIdpage, HaremmasterIdpagenum);
                while (HaremmasterIdList != null &&
                        HaremmasterIdList.size() != 0) {
                    for (String HaremmasterId : HaremmasterIdList) {
                        int HaremmasterInvitedUserIDPage = 0;
                        int HaremmasterInvitedUserIDPagenum = 30;
                        List HaremmasterInvitedUserIDList =
                                GetHaremmasterInvitedUserIDList(HaremmasterId, HaremmasterInvitedUserIDPage, HaremmasterInvitedUserIDPagenum);

                        while (HaremmasterInvitedUserIDList != null
                                && HaremmasterInvitedUserIDList.size() != 0) {
                            int MonthDayRecharge = 0;
                            for (int i = 0; i < HaremmasterInvitedUserIDList.size(); ++i) {
                                Object[] o = (Object[]) HaremmasterInvitedUserIDList.get(i);

                                String userId = o[0].toString();
                                Date date = (Date) o[1];
                                MonthDayRecharge += CalculateMonthDayRecharge(userId, date, MonthDate);
                            }

                            updateTotalRecharge(MonthDayRecharge, HaremmasterId);
                            updateMonthDayRecharge(MonthDayRecharge, HaremmasterId);
                            int TotalRecharge = GetHaremmasterTotalRecharge(HaremmasterId);
                            updateProportion(TotalRecharge, HaremmasterId);
                            updateTotalInviteNum(HaremmasterId);

                            HaremmasterInvitedUserIDPage ++;
                            HaremmasterInvitedUserIDList =
                                    GetHaremmasterInvitedUserIDList(HaremmasterId, HaremmasterInvitedUserIDPage, HaremmasterInvitedUserIDPagenum);
                        }
                    }

                    HaremmasterIdpage ++;
                    HaremmasterIdList = GetHaremmasterIdList(HaremmasterIdpage, HaremmasterIdpagenum);
                }

                return true;
            }
        });
    }

    /**
     * 每月结算
     */
    public void SettlementMonth() {
        LOGGER.info("群主每月结算");
        generaDAO.excuteTransactionByCallback(new TransactionCallback() {
            @Override
            public void callback(BaseDao basedao) throws Exception {
                updateMonthRecharge();
            }
        });
    }

    /**
     * 查找所有群主信息
     *
     * @param page
     * @param pagenum
     * @return
     */
    public List<HaremmasterModel> GetHaremmasterList(int page, int pagenum) {
        Session session = generaDAO.getNewSession();
        Transaction t = session.beginTransaction();
        try {
            List<HaremmasterModel> list = session.createCriteria(HaremmasterModel.class)
                    .setFirstResult(page * pagenum)
                    .setMaxResults(pagenum)
                    .list();
            t.commit();
            return list;
        } catch (Exception e) {
            t.rollback();
            return null;
        }

    }

    HaremmasterModel GetHaremmasterByuserId(String userId) {
        Session session = generaDAO.getNewSession();
        HaremmasterModel haremmasterModel = (HaremmasterModel) session.createCriteria(HaremmasterModel.class)
                .setMaxResults(1)
                .add(Restrictions.eq("userId", userId))
                .uniqueResult();
        return haremmasterModel;

    }

    public String GetHaremmaster(String userId) {
        Session session = generaDAO.getNewSession();
        Transaction t = session.beginTransaction();
        try {
            HaremmasterModel haremmasterModel = GetHaremmasterByuserId(userId);
            t.commit();
            return GsonUntil.JavaClassToJson(haremmasterModel);
        } catch (Exception e) {
            t.rollback();
            return null;
        }
    }

    /**
     * 获得群主打款的信息
     *
     * @param page
     * @return
     */
    public List<AlitransferModel> GetHaremmasterTransfer(int page, int pagenum) {
        List<AlitransferModel> AlitransferList = new ArrayList<>();

        List<HaremmasterModel> HaremmasterList = GetHaremmasterList(page, pagenum);
        Session session = generaDAO.getNewSession();
        Transaction t = session.beginTransaction();
        try {
            for (HaremmasterModel it : HaremmasterList) {
                if (it.isShielding()) {
                    continue;
                }

                UserModel userModel = userDAO.getUSerModelNoTransaction(it.getUserId());
                AlitransferModel alitransferModel = new AlitransferModel();
                alitransferModel.setId(Integer.valueOf(it.getId()));
                alitransferModel.setLines(it.getMonthPushMoney());
                alitransferModel.setAliEmail(userModel.getAlipayId());
                alitransferModel.setRealName(userModel.getAlipayRealName());
                alitransferModel.setExtension("本月邀请提成打款 总邀请数量:" + it.getTotalInvitePeopleNum() + " 本月邀请人总充值:" + it.getMonthRecharge());
                AlitransferList.add(alitransferModel);
            }

            t.commit();

            return AlitransferList;
        } catch (Exception e) {
            t.rollback();
            return null;
        }
    }


    int SetShieldingHaremmaster(String userId, Boolean Shielding) {
        Session session = generaDAO.getNewSession();
        Transaction t = session.beginTransaction();
        try {
            HaremmasterModel haremmasterModel = GetHaremmasterByuserId(userId);
            haremmasterModel.setShielding(Shielding);
            session.update(haremmasterModel);
            t.commit();
            return 1;
        } catch (Exception e) {
            t.rollback();
            return 0;
        }
    }

    /**
     * 取消屏蔽群主
     *
     * @param userId
     * @return
     */
    public int CanelShieldingHaremmaster(String userId) {
        LOGGER.info(userId + "取消屏蔽群主");
        return SetShieldingHaremmaster(userId, false);
    }

    /**
     * 屏蔽群主
     *
     * @param userId
     * @return
     */
    public int SetShieldingHaremmaster(String userId) {
        LOGGER.info(userId + "屏蔽群主");
        return SetShieldingHaremmaster(userId, true);
    }

    /**
     * 删除群主
     *
     * @param userId
     * @return
     */
    public int DeleteHaremmaster(String userId) {
        Session session = generaDAO.getNewSession();
        Transaction t = session.beginTransaction();
        String sql = "delete from Haremmaster where  userId = ?";
        String sql1 = "delete from haremmasterinviteinfo where HaremmasterUserId = ?";
        try {
            UserModel userModel = userDAO.getUSerModelNoTransaction(userId);
            userModel.setHaremmaster(false);
            session.update(userModel);
            session.createSQLQuery(sql)
                    .setParameter(0, userId)
                    .executeUpdate();
            session.createSQLQuery(sql1)
                    .setParameter(0, userId)
                    .executeUpdate();
            t.commit();
            LOGGER.info("删除群主:" + userId);
            return 1;
        } catch (Exception e) {
            t.rollback();
            return 0;
        }
    }

}
