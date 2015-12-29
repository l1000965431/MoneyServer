package com.money.model;

import java.io.Serializable;

/**
 * 特惠项目中奖信息
 */
public class PreferentiaLotteryModel implements Serializable {

    public PreferentiaLotteryModel( long id,SREarningModel srEarningModel ){
        this.Id = id;
        this.Lines = srEarningModel.getEarningPrice();
    }

    long Id;

    int Lines;

    String UserId = "";

    public long getId() {
        return Id;
    }

    public void setId(long id) {
        Id = id;
    }

    public int getLines() {
        return Lines;
    }

    public void setLines(int lines) {
        Lines = lines;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }
}
