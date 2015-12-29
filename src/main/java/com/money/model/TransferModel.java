package com.money.model;

import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Date;

/**
 * 整体提现信息表
 */

@Entity(name = "Transfer")
@Table
@DynamicUpdate(true)
public class TransferModel extends BaseModel {

    @Id
    String orderId;

    Date transferDate;

    int transferLines;

    int transferLinesPoundage;

    String userId;

    String openId;

    String status;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Date getTransferDate() {
        return transferDate;
    }

    public void setTransferDate(Date transferDate) {
        this.transferDate = transferDate;
    }

    public int getTransferLines() {
        return transferLines;
    }

    public void setTransferLines(int transferLines) {
        this.transferLines = transferLines;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getTransferLinesPoundage() {
        return transferLinesPoundage;
    }

    public void setTransferLinesPoundage(int transferLinesPoundage) {
        this.transferLinesPoundage = transferLinesPoundage;
    }
}
