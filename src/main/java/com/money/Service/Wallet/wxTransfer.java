package com.money.Service.Wallet;

import com.google.gson.reflect.TypeToken;
import com.money.memcach.MemCachService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import until.GsonUntil;
import until.PingPlus;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by liumin on 15/11/17.
 */
public class WxTransfer extends Thread {

    private static final Logger LOGGER = LoggerFactory.getLogger(WxTransfer.class);

    //失败队列台头
    final String wxTransferFailList = "wxTransferFailList::";

    String BatchId;
    int ThreadId;
    String key;
    String FailList;
    int msSleep;
    CountDownLatch countDownLatch;

    StringBuffer re;

    public WxTransfer(int msSleep, String BatchId, int ThreadId,CountDownLatch countDownLatch ) {
        this.key = "wxTransferPass::" + BatchId;
        this.BatchId = BatchId;
        this.ThreadId = ThreadId;
        this.msSleep = msSleep;
        this.countDownLatch = countDownLatch;
        this.re = new StringBuffer("提交失败,请注意检查日志");
        FailList = wxTransferFailList+BatchId;
    }

    @Override
    public void run() {
        try {
            this.sleep(msSleep);
        } catch (InterruptedException e) {
            countDownLatch.countDown();
            return;
        }

        transfer();
    }

    void transfer() {
        int len = (int) MemCachService.getLen(key.getBytes());
        int enIndex = len<200 ? len-1 : ThreadId * 200+200-1;
        List<byte[]> list = MemCachService.lrang(key.getBytes(), ThreadId*200, enIndex);

        for (byte[] tempbyte : list) {
            String json = new String(tempbyte);
            List<String> transferInfo = GsonUntil.jsonListToJavaClass(json, new TypeToken<List<String>>() {
            }.getType());

            if( transferInfo == null ){
                return;
            }

            try {
                int lines = Integer.valueOf(transferInfo.get(1));
                String OpenId = transferInfo.get(2);
                String UserId = transferInfo.get(3);
                String OrderId = transferInfo.get(4);
                PingPlus.CreateTransferMap( lines,OpenId,UserId,OrderId,BatchId+"_"+ transferInfo.get( 0 )+"_"+UserId );
                //每次访问后线程睡眠500MS避免访问太过集中
                this.sleep( 500 );
            } catch (Exception e) {
                //将失败的订单移动到失败列表里 只存储ID即可
                LOGGER.error( "微信提现错误:",e );
                re.setLength( 0 );
                re.append( e.getMessage() );
                String id = transferInfo.get( 0 );
                if( id != null ){
                 MemCachService.lpush(FailList.getBytes(),id.getBytes() );
                }
                countDownLatch.countDown();
                continue;
            }
        }
    }

    public StringBuffer getRe() {
        return re;
    }
}
