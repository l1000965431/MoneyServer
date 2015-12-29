package com.money.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 群主列表
 */

@Entity(name = "Haremmaster")
@Table
public class HaremmasterModel {
    @Id
    String userId;

    /**
     * 提成比例
     */
    float Proportion;

    /**
     * 总充值
     */
    int TotalRecharge;

    /**
     * 当月充值
     */
    int MonthRecharge;

    /**
     * 当月提成
     */
    int MonthPushMoney;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public float getProportion() {
        return Proportion;
    }

    public void setProportion(float proportion) {
        Proportion = proportion;
    }

    public int getTotalRecharge() {
        return TotalRecharge;
    }

    public void setTotalRecharge(int totalRecharge) {
        TotalRecharge = totalRecharge;
    }

    public int getMonthRecharge() {
        return MonthRecharge;
    }

    public void setMonthRecharge(int monthRecharge) {
        MonthRecharge = monthRecharge;
    }

    public int getMonthPushMoney() {
        return MonthPushMoney;
    }

    public void setMonthPushMoney(int monthPushMoney) {
        MonthPushMoney = monthPushMoney;
    }
}
