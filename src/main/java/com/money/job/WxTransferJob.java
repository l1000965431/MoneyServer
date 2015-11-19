package com.money.job;

import com.money.dao.BaseDao;
import com.money.dao.GeneraDAO;
import com.money.memcach.MemCachService;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import until.ScheduleJob;

import java.util.List;

/**
 * Created by liumin on 15/11/17.
 */
public class WxTransferJob implements Job {

    @Autowired
    GeneraDAO generaDAO;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        ScheduleJob scheduleJob = (ScheduleJob) jobExecutionContext.getMergedJobDataMap().get("scheduleJob");
        String BatchId = scheduleJob.getDesc();

        //成功的订单处理
        wxTransferWinList( BatchId );

        //失败提现的处理
        FailTransfer(BatchId);

        //清理键值
        String passKey = "wxTransferPass::" + BatchId;
        MemCachService.unLockRedisKey(passKey);
        MemCachService.RemoveValue(passKey.getBytes());
    }


    void wxTransferWinList( String BatchId ){
        String winKey = "wxtransferWinList::" + BatchId;
        int winLen = (int) MemCachService.getLen(winKey.getBytes());
        List<byte[]> winList = MemCachService.lrang(winKey.getBytes(), 0, winLen - 1);

        StringBuffer sqlWin = new StringBuffer("delete from wxtransfer where Id in (WinId)");
        StringBuffer FailId = new StringBuffer();
        int WinIndex = 0;
        int sqlNum = 0;
        for (byte[] temp : winList) {
            String id = new String(temp);
            WinIndex++;
            FailId.append(id);
            FailId.append(",");
            if (WinIndex == 100 || winList.size() == sqlNum) {
                WinIndex = 0;
                int last = FailId.lastIndexOf(",");
                FailId.replace(last, FailId.length() - 1, "");
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
                FailId.replace(0, FailId.length() - 1, "");
            }

        }
        MemCachService.RemoveValue(winKey.getBytes());
    }

    void FailTransfer(String BatchId) {
        String failKey = "wxTransferFailList::" + BatchId;
        int failLen = (int) MemCachService.getLen(failKey.getBytes());
        List<byte[]> failList = MemCachService.lrang(failKey.getBytes(), 0, failLen - 1);


        StringBuffer sqlFail = new StringBuffer("update wxtransfer set IsFaliled=TRUE where Id in (FailedId)");
        StringBuffer FailId = new StringBuffer();
        int FaliIndex = 0;
        int sqlNum = 0;
        for (byte[] temp : failList) {
            String id = new String(temp);
            FaliIndex++;
            sqlNum++;
            FailId.append(id);
            FailId.append(",");
            if (FaliIndex == 100 || failList.size() == sqlNum ) {
                FaliIndex = 0;
                int last = FailId.lastIndexOf(",");
                FailId.replace(last, FailId.length() - 1, "");
                String sql = sqlFail.toString().replace("FailedId", FailId);

                Session session = generaDAO.getNewSession();
                Transaction t = session.beginTransaction();
                try {
                    session.createSQLQuery(sql).executeUpdate();
                    t.commit();
                } catch (Exception e) {
                    t.rollback();
                    break;
                }
                FailId.replace(0, FailId.length() - 1, "");
            }

        }


        MemCachService.RemoveValue(failKey.getBytes());
    }


}
