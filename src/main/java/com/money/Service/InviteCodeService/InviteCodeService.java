package com.money.Service.InviteCodeService;

import com.google.gson.Gson;
import com.money.MoneyServerMQ.MoneyServerMQManager;
import com.money.MoneyServerMQ.MoneyServerMessage;
import com.money.Service.ServiceBase;
import com.money.Service.ServiceInterface;
import com.money.config.MoneyServerMQ_Topic;
import com.money.dao.InviteCodeDAO.InviteCodeDAO;
import com.money.dao.TransactionSessionCallback;
import com.money.model.InviteCodeModel;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import until.GsonUntil;
import until.MoneyServerDate;

import java.text.ParseException;
import java.util.List;

/**
 * Created by liumin on 15/10/4.
 */

@Service("InviteCodeService")
public class InviteCodeService extends ServiceBase implements ServiceInterface {

    @Autowired
    InviteCodeDAO inviteCodeDAO;

    public String AddInviteCode(final int num) {

        final String[] re = new String[1];
        inviteCodeDAO.excuteTransactionByCallback(new TransactionSessionCallback() {
            public boolean callback(Session session) throws Exception {
                re[0] = inviteCodeDAO.InsertnviteCode(num);
                return true;
            }
        });
        return re[0];
    }

    public int CountInviteCode() {
        final int[] num = new int[1];
        inviteCodeDAO.excuteTransactionByCallback(new TransactionSessionCallback() {
            public boolean callback(Session session) throws Exception {
                num[0] = inviteCodeDAO.countInviteCodeNum();
                return true;
            }
        });
        return num[0];
    }

    public int useInviteCode(final String userID, final String inviteCode) throws ParseException {
        final int[] state = new int[1];
        inviteCodeDAO.excuteTransactionByCallback(new TransactionSessionCallback() {
            public boolean callback(Session session) throws Exception {
                state[0] = inviteCodeDAO.userInviteCode(userID, inviteCode);
                return true;
            }
        });


        if( inviteCodeDAO.countInviteCodeNum() < 200 ){
            //插入邀请码消息
            MoneyServerMQManager.SendMessage(new MoneyServerMessage(MoneyServerMQ_Topic.MONEYSERVERMQ_INSERTINVITECODE_TOPIC,
                    MoneyServerMQ_Topic.MONEYSERVERMQ_INSERTINVITECODE_TAG, "", "项目这邀请码插入"+ MoneyServerDate.getStringCurDate()));
        }

        return state[0];
    }

    public String getInviteCode(final int num ){

        final int[] State = {0};
        final List[] list = new List[1];
        inviteCodeDAO.excuteTransactionByCallback(new TransactionSessionCallback() {
            @Override
            public boolean callback(Session session) throws Exception {
                if( num > inviteCodeDAO.getTotalCount( InviteCodeModel.class ) ){
                    State[0] = 1;
                    return false;
                }


                list[0] = inviteCodeDAO.getInviteCode( num );

                return true;
            }
        });

        if( State[0] == 1 ){
            return "邀请码数量不足,请插入邀请码.";
        }else if( State[0] == 0 ){
            return GsonUntil.JavaClassToJson( list );
        }

        return "查询邀请码错误";
    }

}
