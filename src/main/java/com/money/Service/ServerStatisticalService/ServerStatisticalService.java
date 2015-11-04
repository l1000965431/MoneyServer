package com.money.Service.ServerStatisticalService;

import com.money.Service.ServiceBase;
import com.money.Service.ServiceInterface;
import com.money.dao.GeneraDAO;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transaction;
import java.math.BigInteger;
import java.util.List;

/**
 * Created by seele on 2015/11/2.
 */

@Service("ServerStatisticalService")
public class ServerStatisticalService extends ServiceBase implements ServiceInterface {

    @Autowired
    GeneraDAO generaDAO;

    /**
     * 每日投资人总收益
     * @param startDate
     * @return
     */
    public int getTotlaLotterySum(String startDate,String endDate) {
        String sql = "select sum(earningsrecord.TotalPrize) as LotteryLinesSum from earningsrecord where earningsrecord.EndDate BETWEEN 'startDate' AND 'endDate';";
        sql = sql.replace("startDate", startDate).replace("endDate",endDate);

        Session session = generaDAO.getNewSession();
        org.hibernate.Transaction t = session.beginTransaction();
        List<BigInteger> list =  session.createSQLQuery( sql ).list();
        t.commit();
        return list.get(0).intValue();
    }

    /**
     * 每日用户投资次数
     * @param startDate
     * @param endDate
     * @return
     */
    public int getTotalBuySum( String startDate,String endDate ){
        String sql = "SELECT count(*) FROM moneyserver.activityorder where OrderDate BETWEEN 'startDate' and 'endDate';";
        sql = sql.replace("startDate", startDate).replace("endDate",endDate);

        Session session = generaDAO.getNewSession();
        org.hibernate.Transaction t = session.beginTransaction();
        List<BigInteger> list =  session.createSQLQuery( sql ).list();
        t.commit();
        return list.get(0).intValue();
    }


    /**
     * 每日用户投资额度
     * @param startDate
     * @param endDate
     * @return
     */
    public int getTotalBuyLines( String startDate,String endDate ){
        String sql = "SELECT sum(orderLines) FROM moneyserver.activityorder where OrderDate BETWEEN 'startDate' and 'endDate';";
        sql = sql.replace("startDate", startDate).replace("endDate",endDate);

        Session session = generaDAO.getNewSession();
        org.hibernate.Transaction t = session.beginTransaction();
        List<BigInteger> list =  session.createSQLQuery( sql ).list();
        t.commit();
        return list.get(0).intValue();
    }

    /**
     * 总发布项目数量
     * @return
     */
    public int getTotlaVerifyActivity(){
        String sql = "SELECT count(*) FROM moneyserver.activityverify;";

        Session session = generaDAO.getNewSession();
        org.hibernate.Transaction t = session.beginTransaction();
        List<BigInteger> list =  session.createSQLQuery( sql ).list();
        t.commit();
        return list.get(0).intValue();
    }

    /**
     * 一共累计总投资额度
     * @return
     */
    public int getTotalInvestment(){
        String sql = "select sum(userearnings.UserEarningLines)as UserLotterySum from userearnings;";
        Session session = generaDAO.getNewSession();
        org.hibernate.Transaction t = session.beginTransaction();
        List<BigInteger> list =  session.createSQLQuery( sql ).list();
        t.commit();
        return list.get(0).intValue();
    }

    /**
     * 一共累计投资次数
     * @return
     */
    public int getTotalInvestmentNum(){
        String sql = "select COUNT(*) as BuyNum FROM activityorder;";
        Session session = generaDAO.getNewSession();
        org.hibernate.Transaction t = session.beginTransaction();
        List<Integer> list =  session.createSQLQuery( sql ).list();
        t.commit();
        return list.get(0);
    }

    /**
     * 一共给投资人带来多少收益
     * @return
     */
    public int getTotalLottery(){
        String sql = "select sum(earningsrecord.TotalPrize) as LotteryLinesSum from earningsrecord;";
        Session session = generaDAO.getNewSession();
        org.hibernate.Transaction t = session.beginTransaction();
        List<Integer> list =  session.createSQLQuery( sql ).list();
        t.commit();
        return list.get(0);
    }


    /**
     * 平均每人充值多少钱
     * @return
     */
    public float getAverageWallet(){
        String sql = "select (sum(walletorder.WalletLines)/ (SELECT COUNT(id) from user )) as AverageWallet from walletorder;";
        Session session = generaDAO.getNewSession();
        org.hibernate.Transaction t = session.beginTransaction();
        List<Float> list =  session.createSQLQuery( sql ).list();
        t.commit();
        return list.get(0).floatValue();
    }

    /**
     * 每日公司营收金额
     * @return
     */
    public int getRevenueWallet( String startDate,String endDate ){
        String sql = "select ( SUM(earningsrecord.TotalFund)- SUM(earningsrecord.TotalPrize) )" +
                " from earningsrecord where earningsrecord.EndDate BETWEEN 'startDate' AND 'endDate' ;";
        sql = sql.replace( "startDate",startDate ).replace("endDate",endDate);
        Session session = generaDAO.getNewSession();
        org.hibernate.Transaction t = session.beginTransaction();
        List<Integer> list =  session.createSQLQuery( sql ).list();
        t.commit();
        return list.get(0).intValue();
    }



    /**
     * 每日提交的项目数
     * @return
     */
    public int getActivityVerify( String startDate,String endDate ){
        String sql = "SELECT count(*) as VerifyNum from activityverify where activityverify.createDate BETWEEN 'startDate' AND 'endDate';";
        sql = sql.replace( "startDate",startDate ).replace("endDate",endDate);
        Session session = generaDAO.getNewSession();
        org.hibernate.Transaction t = session.beginTransaction();
        List<BigInteger> list =  session.createSQLQuery( sql ).list();
        t.commit();
        return list.get(0).intValue();
    }

}
