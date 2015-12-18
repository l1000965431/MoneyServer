package com.money.model;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by liumin on 15/12/17.
 */

@Entity(name = "backTicket")
@Table
public class BackTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int Id;

    String InstallmentActivityID;

    String UserId;

    int PurchaseType;

    Date PurchaseDate;

    int AdvanceType;

    String TickID;

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getInstallmentActivityID() {
        return InstallmentActivityID;
    }

    public void setInstallmentActivityID(String installmentActivityID) {
        InstallmentActivityID = installmentActivityID;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public int getPurchaseType() {
        return PurchaseType;
    }

    public void setPurchaseType(int purchaseType) {
        PurchaseType = purchaseType;
    }

    public Date getPurchaseDate() {
        return PurchaseDate;
    }

    public void setPurchaseDate(Date purchaseDate) {
        PurchaseDate = purchaseDate;
    }

    public int getAdvanceType() {
        return AdvanceType;
    }

    public void setAdvanceType(int advanceType) {
        AdvanceType = advanceType;
    }

    public String getTickID() {
        return TickID;
    }

    public void setTickID(String ticketID) {
        TickID = ticketID;
    }
}
