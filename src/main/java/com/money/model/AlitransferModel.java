package com.money.model;

import javax.persistence.*;
import java.text.DecimalFormat;
import java.util.Date;

/**
 * 支付宝提交提现申请表
 */

@Entity(name = "alitransfer")
@Table
public class AlitransferModel extends BaseModel {
    @Id
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
    Date AlitransferDate;

    /**
     * 真实姓名
     */
    String RealName;

    /**
     * 支付宝帐号
     */
    String AliEmail;

    /**
     * 是否锁定
     */
    int IsLock = 0;

    /**
     * 错误信息
     */
    String ErrorInfo = "";

    /**
     * 扩展参数
     */
    String extension = "";

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

    public Date getAlitransferDate() {
        return AlitransferDate;
    }

    public void setAlitransferDate(Date alitransferDate) {
        AlitransferDate = alitransferDate;
    }

    public String getRealName() {
        return RealName;
    }

    public void setRealName(String realName) {
        RealName = realName;
    }

    public String getAliEmail() {
        return AliEmail;
    }

    public void setAliEmail(String aliEmail) {
        AliEmail = aliEmail;
    }

    public String toAlipayTransFormat() {
        DecimalFormat df = new DecimalFormat("#####0.00");
        return Integer.toString(Id) + "^" + AliEmail + "^" + RealName + "^"
                + df.format(TransferLines) + "^" + extension;
    }

    public String getErrorInfo() {
        return ErrorInfo;
    }

    public void setErrorInfo(String errorInfo) {
        ErrorInfo = errorInfo;
    }

    public int getPoundageResult() {
        return poundageResult;
    }

    public void setPoundageResult(int poundageResult) {
        this.poundageResult = poundageResult;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }
}
