package com.money.model;

import javax.persistence.*;
import java.util.Date;

/**
 * 批量提现申请表
 */

@Entity(name = "BatchTransfer" )
@Table
public class BatchTransferModel extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int Id;

    /**
     * 提现批次ID
     */
    String BatchId;

    //提现实际金额
    int TransferLines;

    /**
     * 提现日期
     */
    Date TransferDate;

    /**
     * 提现渠道
     */
    String Transferchannel;


    public String getBatchId() {
        return BatchId;
    }

    public void setBatchId(String batchId) {
        BatchId = batchId;
    }

    public int getTransferLines() {
        return TransferLines;
    }

    public void setTransferLines(int transferLines) {
        TransferLines = transferLines;
    }

    public Date getTransferDate() {
        return TransferDate;
    }

    public void setTransferDate(Date transferDate) {
        TransferDate = transferDate;
    }

    public String getTransferchannel() {
        return Transferchannel;
    }

    public void setTransferchannel(String transferchannel) {
        Transferchannel = transferchannel;
    }
}
