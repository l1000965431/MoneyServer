package com.money.model;

import javax.persistence.*;
import java.util.Date;

/**
 * 群主打款记录列表
 */

@Entity(name = "HaremmasterTransfer")
@Table
public class HaremmasterTransferModel extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int Id;

    String userId;

    /**
     * 打款金额
     */
    int PushMoney;

    /**
     * 打款时间
     */
    Date PushMoneyDate;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getPushMoney() {
        return PushMoney;
    }

    public void setPushMoney(int pushMoney) {
        PushMoney = pushMoney;
    }

    public Date getPushMoneyDate() {
        return PushMoneyDate;
    }

    public void setPushMoneyDate(Date pushMoneyDate) {
        PushMoneyDate = pushMoneyDate;
    }
}
