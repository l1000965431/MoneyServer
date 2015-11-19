package com.money.Service.ServerStatisticalService;

import com.money.Service.ServiceBase;
import com.money.Service.ServiceInterface;
import com.money.dao.GeneraDAO;
import com.money.model.BatchTransferModel;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import until.GsonUntil;

import javax.transaction.Transaction;
import java.math.BigDecimal;
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
        String sql = "select sum(earningsrecord.TotalPrize) as LotteryLinesSum from earningsrecord " +
                "where date_format(earningsrecord.EndDate,'%Y-%m-%d') BETWEEN 'startDate' AND 'endDate';";
        sql = sql.replace("startDate", startDate).replace("endDate",endDate);

        Session session = generaDAO.getNewSession();
        org.hibernate.Transaction t = session.beginTransaction();
        try{
            BigDecimal result =  (BigDecimal)session.createSQLQuery( sql ).uniqueResult();
            t.commit();
            return result.intValue();
        }catch ( Exception e){
            t.rollback();
            return -1;
        }

    }

    /**
     * 每日用户投资次数
     * @param startDate
     * @param endDate
     * @return
     */
    public int getTotalBuySum( String startDate,String endDate ){
        String sql = "SELECT count(*) FROM moneyserver.activityorder where date_format(OrderDate,'%Y-%m-%d') BETWEEN 'startDate' and 'endDate';";
        sql = sql.replace("startDate", startDate).replace("endDate",endDate);

        Session session = generaDAO.getNewSession();
        org.hibernate.Transaction t = session.beginTransaction();
        try{
            BigInteger result =  (BigInteger)session.createSQLQuery( sql ).uniqueResult();
            t.commit();
            return result.intValue();
        }catch ( Exception e ){
            t.rollback();
            return -1;
        }

    }


    /**
     * 每日用户投资额度
     * @param startDate
     * @param endDate
     * @return
     */
    public int getTotalBuyLines( String startDate,String endDate ){
        String sql = "SELECT sum(orderLines) FROM moneyserver.activityorder where date_format(OrderDate,'%Y-%m-%d') BETWEEN 'startDate' and 'endDate';";
        sql = sql.replace("startDate", startDate).replace("endDate",endDate);

        Session session = generaDAO.getNewSession();
        org.hibernate.Transaction t = session.beginTransaction();
        try{
            BigDecimal result =  (BigDecimal)session.createSQLQuery( sql ).uniqueResult();
            t.commit();
            return result.intValue();
        }catch ( Exception e ){
            t.rollback();
            return -1;
        }

    }

    /**
     * 总发布项目数量
     * @return
     */
    public int getTotlaVerifyActivity(){
        String sql = "SELECT count(*) FROM moneyserver.activityverify;";

        Session session = generaDAO.getNewSession();
        org.hibernate.Transaction t = session.beginTransaction();
        try{
            BigInteger result =  (BigInteger)session.createSQLQuery( sql ).uniqueResult();
            t.commit();
            return result.intValue();
        }catch ( Exception e ){
            t.rollback();
            return -1;
        }

    }

    /**
     * 一共累计总投资额度
     * @return
     */
    public int getTotalInvestment(){
        String sql = "select sum(userearnings.UserEarningLines)as UserLotterySum from userearnings;";
        Session session = generaDAO.getNewSession();
        org.hibernate.Transaction t = session.beginTransaction();
        try{
            BigDecimal result =  (BigDecimal)session.createSQLQuery( sql ).uniqueResult();
            t.commit();
            return result.intValue();
        }catch ( Exception e ){
            t.rollback();
            return -1;
        }

    }

    /**
     * 一共累计投资次数
     * @return
     */
    public int getTotalInvestmentNum(){
        String sql = "select COUNT(*) as BuyNum FROM activityorder;";
        Session session = generaDAO.getNewSession();
        org.hibernate.Transaction t = session.beginTransaction();
        try{
            BigInteger result =  (BigInteger)session.createSQLQuery( sql ).uniqueResult();
            t.commit();
            return result.intValue();
        }catch ( Exception e ){
            t.rollback();
            return -1;
        }

    }

    /**
     * 一共给投资人带来多少收益
     * @return
     */
    public int getTotalLottery(){
        String sql = "select sum(earningsrecord.TotalPrize) as LotteryLinesSum from earningsrecord;";
        Session session = generaDAO.getNewSession();
        org.hibernate.Transaction t = session.beginTransaction();
        try{
            BigDecimal result =  (BigDecimal)session.createSQLQuery( sql ).uniqueResult();
            t.commit();
            return result.intValue();
        }catch ( Exception e ){
            t.rollback();
            return -1;
        }

    }


    /**
     * 平均每人充值多少钱
     * @return
     */
    public float getAverageWallet(){
        String sql = "select (sum(walletorder.WalletLines)/ (SELECT COUNT(id) from user )) as AverageWallet from walletorder;";
        Session session = generaDAO.getNewSession();
        org.hibernate.Transaction t = session.beginTransaction();
        try{
            BigDecimal result =  (BigDecimal)session.createSQLQuery( sql ).uniqueResult();
            t.commit();
            return result.floatValue();
        }catch ( Exception e ){
            t.rollback();
            return -1.0f;
        }

    }

    /**
     * 每日公司营收金额
     * @return
     */
    public int getRevenueWallet( String startDate,String endDate ){
        String sql = "select ( SUM(earningsrecord.TotalFund)- SUM(earningsrecord.TotalPrize) )" +
                " from earningsrecord where date_format(earningsrecord.EndDate,'%Y-%m-%d') BETWEEN 'startDate' AND 'endDate' ;";
        sql = sql.replace( "startDate",startDate ).replace("endDate",endDate);
        Session session = generaDAO.getNewSession();
        org.hibernate.Transaction t = session.beginTransaction();
        try{
            BigDecimal result =  (BigDecimal)session.createSQLQuery( sql ).uniqueResult();
            t.commit();
            return result.intValue();
        }catch ( Exception e ){
            t.rollback();
            return -1;
        }

    }



    /**
     * 每日提交的项目数
     * @return
     */
    public int getActivityVerify( String startDate,String endDate ){
        String sql = "SELECT count(*) as VerifyNum from activityverify " +
                "where date_format(activityverify.createDate,'%Y-%m-%d') BETWEEN 'startDate' AND 'endDate';";
        sql = sql.replace( "startDate",startDate ).replace("endDate",endDate);
        Session session = generaDAO.getNewSession();
        org.hibernate.Transaction t = session.beginTransaction();
        try{
            BigInteger result =  (BigInteger)session.createSQLQuery( sql ).uniqueResult();
            t.commit();
            return result.intValue();
        }catch ( Exception e ){
            t.rollback();
            return -1;
        }

    }

    /**
     * 获取批量提现的列表
     * @param startDate
     * @param endDate
     * @return
     */
    public String getBatchTransferList( String startDate,String endDate ){
        String sql = "SELECT * as from batchtransfer " +
                "where date_format(batchtransfer.TransferDate,'%Y-%m-%d') BETWEEN 'startDate' AND 'endDate';";
        sql = sql.replace( "startDate",startDate ).replace("endDate",endDate);
        Session session = generaDAO.getNewSession();
        org.hibernate.Transaction t = session.beginTransaction();
        try{
            List<BatchTransferModel> result = session.createSQLQuery( sql ).list();
            t.commit();
            return GsonUntil.JavaClassToJson( result );
        }catch ( Exception e ){
            t.rollback();
            return "";
        }
    }


}
