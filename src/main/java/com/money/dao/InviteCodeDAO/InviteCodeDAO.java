package com.money.dao.InviteCodeDAO;

import com.money.dao.BaseDao;
import com.money.model.InviteCodeModel;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import until.GsonUntil;
import until.MoneyServerDate;
import until.MoneySeverRandom;
import until.ShareCodeUtil;

import java.math.BigInteger;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liumin on 15/10/4.
 */

@Repository
public class InviteCodeDAO extends BaseDao {


    /**
     * 插入邀请码
     *
     * @param num 插入的数量
     * @return
     */
    public String InsertnviteCode(int num) {
        int numTemp = num;
        List<String> list = new ArrayList<>();

        while (true) {
            if (numTemp <= 1000) {
                List list1 = insert(numTemp);
                list.addAll( list1 );
                return GsonUntil.JavaClassToJson( list );
            } else {
                List list1 = insert(1000);
                list.addAll( list1 );
                numTemp -= 1000;
            }
        }
    }


    private List insert(int num) {
        String Sql = "insert into invitecode( inviteCode,userId,CreateDate ) values ";
        String Vaules = "";
        List<String> list = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            String a = ShareCodeUtil.toSerialCode(MoneySeverRandom.getRandomNum( 10000000,99999999 ));
            Vaules += "('" + a + "','0','"+MoneyServerDate.getStringCurDate()+"'),";

            list.add( a );
        }
        Vaules = Vaules.substring(0,Vaules.length()-1);
        Sql += Vaules;
        SQLQuery sqlQuery = this.getNewSession().createSQLQuery( Sql );
        sqlQuery.executeUpdate();
        //this.getSession().flush();
        return list;
    }

    /**
     * 使用邀请码
     *
     * @param userId
     * @throws ParseException
     */
    public int userInviteCode(String userId,String InviteCode) throws ParseException {

        String Sql = "select * from invitecode where userId='0' and inviteCode=? ;";
        Session session = this.getNewSession();

        SQLQuery sqlQuery = session.createSQLQuery(Sql).addEntity(InviteCodeModel.class);
        sqlQuery.setParameter( 0,InviteCode );
        InviteCodeModel inviteCodeModel = (InviteCodeModel) sqlQuery.uniqueResult();

        if (inviteCodeModel != null) {
            inviteCodeModel.setUseDate(MoneyServerDate.getDateCurDate());
            inviteCodeModel.setUserId(userId);
            this.updateNoTransaction(inviteCodeModel);
            return 1;
        }else{
            return 0;
        }

    }

    /**
     * 统计没使用的邀请码还有多少个
     */
    public int countInviteCodeNum() {
        String Sql = "select count(Id) from invitecode where userId='0';";
        Session session = this.getNewSession();
        BigInteger re = (BigInteger)session.createSQLQuery(Sql).uniqueResult();
        return re.intValue();

    }

    public List getInviteCode(int Num ){
        String Sql = "select invitecode from invitecode where userId='0' limit ? ";
        Session session = this.getNewSession();
        return session.createSQLQuery( Sql ).setParameter( 0,Num ).list();
    }


}
