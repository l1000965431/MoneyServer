package com.money.model;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;

/**
 * 支付宝提交提现申请信息
 */
public class AliTransferInfo {

    BigDecimal TransferLines;

    BigInteger CountTransfer;

    public BigDecimal getTransferLines() {
        return TransferLines;
    }

    public void setTransferLines(BigDecimal transferLines) {
        TransferLines = transferLines;
    }

    public BigInteger getCountTransfer() {
        return CountTransfer;
    }

    public void setCountTransfer(BigInteger countTransfer) {
        CountTransfer = countTransfer;
    }

}
