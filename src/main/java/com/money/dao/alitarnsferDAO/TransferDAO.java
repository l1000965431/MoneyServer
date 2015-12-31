package com.money.dao.alitarnsferDAO;

import com.money.dao.BaseDao;
import com.money.model.AliTransferInfo;
import com.money.model.AlitransferModel;
import com.money.model.WxTranferModel;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;
import until.MoneyServerDate;

import java.math.BigInteger;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liumin on 15/9/25.
 */

@Repository
public class TransferDAO extends BaseDao {

    /**
     * 提交提现申请
     *
     * @param UserId   用户ID
     * @param Lines    提款额度
     * @param RealName 真实姓名
     * @param AliName  支付宝帐号
     * @return 0失败 >0成功
     */
    public int Submitalitansfer(String UserId, int Lines,int Poundage, String RealName, String AliName) {

        AlitransferModel alitransferModel = GetAliTransfer(UserId);

        if (alitransferModel == null) {
            AlitransferModel NewalitransferModel = new AlitransferModel();
            NewalitransferModel.setUserId(UserId);
            NewalitransferModel.setAliEmail(AliName);
            NewalitransferModel.setRealName(RealName);
            NewalitransferModel.setLines(Lines);
            NewalitransferModel.setPoundageResult( Poundage );
            NewalitransferModel.setAlitransferDate( MoneyServerDate.getDateCurDate() );
            NewalitransferModel.setExtension( "微聚竞投提现打款" );
            this.saveNoTransaction(NewalitransferModel);
            return 1;
        } else {
            String sql = "update alitransfer set TransferLines = TransferLines+?,poundageResult = poundageResult+?, AlitransferDate=? where UserId = ?";
            Session session = this.getNewSession();
            SQLQuery query = session.createSQLQuery(sql);
            query.setParameter(0, Lines);
            query.setParameter(1, Poundage);
            query.setParameter(2, MoneyServerDate.getStringCurDate());
            query.setParameter(3, UserId);
            return query.executeUpdate();
        }
    }

    public AlitransferModel GetAliTransfer(String UserId) {

        final AlitransferModel alitransferModel;

        alitransferModel = (AlitransferModel) getNewSession().createCriteria(AlitransferModel.class)
                .setMaxResults(1)
                .add(Restrictions.eq("UserId", UserId))
                .add(Restrictions.eq("IsLock", 0))
                .uniqueResult();

        return alitransferModel;

    }

    public int SubmitaliWxtansfer( String UserId, int Lines,int Poundage, String RealName, String AliName ){
        WxTranferModel wxTranferModel = GetWxTransfer(UserId);

        if (wxTranferModel == null) {

            WxTranferModel NewwxTranferModel = new WxTranferModel();
            NewwxTranferModel.setUserId(UserId);
            NewwxTranferModel.setOpenId(AliName);
            NewwxTranferModel.setRealName(RealName);
            NewwxTranferModel.setLines(Lines);
            NewwxTranferModel.setPoundageResult( Poundage );
            NewwxTranferModel.setWxtransferDate( MoneyServerDate.getDateCurDate() );
            this.saveNoTransaction(NewwxTranferModel);
            return 1;
        } else {
            String sql = "update wxtransfer set TransferLines = TransferLines+?,PoundageResult = PoundageResult+?, WxtransferDate=? where UserId = ?";
            Session session = this.getNewSession();
            SQLQuery query = session.createSQLQuery(sql);
            query.setParameter(0, Lines);
            query.setParameter(1, Poundage);
            query.setParameter(2, MoneyServerDate.getStringCurDate());
            query.setParameter(3, UserId);
            return query.executeUpdate();
        }
    }

    public WxTranferModel GetWxTransfer(String UserId) {

        final WxTranferModel wxTranferModel;

        wxTranferModel = (WxTranferModel) getNewSession().createCriteria(WxTranferModel.class)
                .setMaxResults(1)
                .add(Restrictions.eq("UserId", UserId))
                .add(Restrictions.eq("IsLock", 0))
                .uniqueResult();

        return wxTranferModel;

    }

    /**
     * 提现成功清零
     *
     * @param UserId 用户ID
     * @return 0:错误 >0:成功
     */
    public int Clearalitansfer(String UserId) throws ParseException {
        String sql = "update alitransfer set Lines = 0 , AlitransferDate=? where UserId = ?";
        Session session = this.getNewSession();
        SQLQuery query = session.createSQLQuery(sql);
        query.setParameter(0, UserId);
        query.setDate(1, MoneyServerDate.getDateCurDate());
        return query.executeUpdate();
    }

    /**
     * 设置支付失败
     *
     * @param UserId 用户ID
     */
    public void SetalitransferFailed(String UserId) {

        AlitransferModel alitransferModel = GetAliTransfer(UserId);

        if (alitransferModel == null) {
            return;
        }
        alitransferModel.setIsFaliled(true);

        this.updateNoTransaction(alitransferModel);
    }


    public int GetCountGrouping() {
        String sql = "select count(Id) from alitransfer where Faliled != false";
        Session session = this.getNewSession();
        SQLQuery query = session.createSQLQuery(sql);
        return Integer.valueOf(query.uniqueResult().toString());
    }

    /**
     * 获得提现申请订单 每3000个位一组
     *
     * @return null:失败
     */
    public List GetAliTransferOdrer() {

        int page = 0;
        List list = new ArrayList();
        Session session = this.getNewSession();
        String sql = "SELECT sum(TransferLines) as TransferLines,count(id) as CountTransfer " +
                "FROM (select TransferLines,Id from alitransfer where TransferLines != 0 and IsFaliled != true limit ? ,3000) as TransferTemp;";
        Query query = session.createSQLQuery(sql).setResultTransformer(Transformers.aliasToBean(AliTransferInfo.class));

        //锁定列表
        String sql1 = "update alitransfer set alitransfer.IsLock=1;";
        Query query1 = session.createSQLQuery(sql1);
        query1.executeUpdate();

        while (true) {
            query.setParameter(0, page);
            List<AliTransferInfo> Sqllist = (List<AliTransferInfo>)query.list();

            if (Sqllist == null || Sqllist.size() == 0 || Sqllist.get(0).getCountTransfer().equals( BigInteger.ZERO) ) {
                return list;
            }

            list.add( Sqllist );
            page += 3000;
        }
    }

    /**
     * 获得对应的批次信息
     *
     * @param page 获取的页数
     * @return 此次批次的列表
     */
    public List<AlitransferModel> GetAliTransferInfo(int page) {
        Session session = this.getNewSession();
        String sql = "select * from alitransfer where TransferLines != 0 and IsFaliled != true limit ? ,3000;";
        Query query = session.createSQLQuery(sql).addEntity(AlitransferModel.class);

        query.setParameter(0, page);
        List<AlitransferModel> Sqllist = (List<AlitransferModel>)query.list();

        //锁定列表
        String sql1 = "update alitransfer INNER JOIN"
                +"(select * from alitransfer where TransferLines != 0 and IsFaliled != true limit ? ,3000)as ali set alitransfer.IsLock=1;";
        query = session.createSQLQuery(sql1);
        query.setParameter(0, page*3000);
        query.executeUpdate();

        return Sqllist;
    }

    /**
     * 获得对应的批次信息
     *
     * @param page 获取的页数
     * @return 此次批次的列表
     */
    public List<WxTranferModel> GetWxTransferInfo(int page) {
        Session session = this.getNewSession();
        String sql = "select * from wxtransfer where TransferLines != 0 and IsFaliled != true limit ? ,1000;";
        Query query = session.createSQLQuery(sql).addEntity(WxTranferModel.class);

        query.setParameter(0, page*1000);
        List<WxTranferModel> Sqllist = (List<WxTranferModel>)query.list();

        //锁定列表
        String sql1 = "update wxtransfer INNER JOIN " +
                "(select * from wxtransfer where TransferLines != 0 and IsFaliled != true limit ? ,1000)as wx set wxtransfer.IsLock=1;";
        query = session.createSQLQuery(sql1);
        query.setParameter(0, page*1000);
        query.executeUpdate();
        return Sqllist;
    }

    /**
     * 获取微信提现申请人数
     * @return
     */
    public int GetWxTransferNum(){
        Session session = this.getNewSession();
        String sql = "select count(Id) from wxtransfer where IsFaliled=FALSE ";
        Query query = session.createSQLQuery(sql);
        BigInteger re = (BigInteger) query.uniqueResult();
        return re.intValue();
    }

    /**
     * 获取微信提现申请失败订单数
     * @return
     */
    public int GetWxFailTransferNum(){
        Session session = this.getNewSession();
        String sql = "select count(Id) from wxtransfer where IsFaliled=TRUE ";
        Query query = session.createSQLQuery(sql);
        BigInteger re = (BigInteger) query.uniqueResult();
        return re.intValue();
    }

    /**
     * 获取支付宝提现申请人数
     * @return
     */
    public int GetAliTransferNum(){
        Session session = this.getNewSession();
        String sql = "select count(Id) from alitransfer where IsFaliled=FALSE ";
        Query query = session.createSQLQuery(sql);
        BigInteger re = (BigInteger) query.uniqueResult();
        return re.intValue();
    }

    /**
     * 获取支付宝提现申请失败订单数
     * @return
     */
    public int GetAliFailTransferNum(){
        Session session = this.getNewSession();
        String sql = "select count(Id) from alitransfer where IsFaliled=TRUE ";
        Query query = session.createSQLQuery(sql);
        BigInteger re = (BigInteger) query.uniqueResult();
        return re.intValue();
    }
}
