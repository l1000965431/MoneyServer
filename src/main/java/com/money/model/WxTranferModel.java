/**
 * Created by liumin on 15/11/16.
 */

package com.money.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Table;
import java.util.Date;

@Entity(name = "wxtransfer")
@Table
public class WxTranferModel {
    @javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int Id;

    /**
     * 用户ID
     */
    String UserId = "";

    /**
     * 批量付款是否失败
     */
    boolean IsFaliled = false;

    /**
     * 付款金额
     */
    int TransferLines = 0;

    /**
     * 提现手续费
     */
    int poundageResult = 0;

    /**
     * 付款最后时间
     */
    Date WxtransferDate;

    /**
     * 真实姓名
     */
    String RealName;

    /**
     * 微信提现Id
     */
    String OpenId;

    /**
     * 是否锁定
     */
    int IsLock = 0;

    /**
     * 错误信息
     */
    String ErrorInfo = "";

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public boolean isFaliled() {
        return IsFaliled;
    }

    public void setIsFaliled(boolean isFaliled) {
        IsFaliled = isFaliled;
    }

    public int getLines() {
        return TransferLines;
    }

    public void setLines(int lines) {
        TransferLines = lines;
    }


    public Date getWxtransferDate() {
        return WxtransferDate;
    }

    public void setWxtransferDate(Date wxtransferDate) {
        WxtransferDate = wxtransferDate;
    }

    public String getOpenId() {
        return OpenId;
    }

    public void setOpenId(String openId) {
        OpenId = openId;
    }

    public void setFaliled(boolean faliled) {
        IsFaliled = faliled;
    }

    public int getTransferLines() {
        return TransferLines;
    }

    public void setTransferLines(int transferLines) {
        TransferLines = transferLines;
    }

    public String getRealName() {
        return RealName;
    }

    public void setRealName(String realName) {
        RealName = realName;
    }

    public String getErrorInfo() {
        return ErrorInfo;
    }

    public void setErrorInfo(String errorInfo) {
        ErrorInfo = errorInfo;
    }

    public void setPoundageResult(int poundageResult) {
        this.poundageResult = poundageResult;
    }

    public int getPoundageResult() {
        return poundageResult;
    }
}
