package com.money.model;

import javax.persistence.*;

/**
 * 群主列表
 */

@Entity(name = "Haremmaster")
@Table
public class HaremmasterModel extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int Id;

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

    /**
     * 当月每天的充值 每月28日0点清空 复制到MonthRecharge项中
     */
    int MonthDayRecharge;

    /**
     * 总得邀请人数
     */
    int TotalInvitePeopleNum;

    boolean IsShielding;

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

    public int getMonthDayRecharge() {
        return MonthDayRecharge;
    }

    public void setMonthDayRecharge(int monthDayRecharge) {
        MonthDayRecharge = monthDayRecharge;
    }

    public int getTotalInvitePeopleNum() {
        return TotalInvitePeopleNum;
    }

    public void setTotalInvitePeopleNum(int totalInvitePeopleNum) {
        TotalInvitePeopleNum = totalInvitePeopleNum;
    }

    public boolean isShielding() {
        return IsShielding;
    }

    public void setShielding(boolean shielding) {
        IsShielding = shielding;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }
}
