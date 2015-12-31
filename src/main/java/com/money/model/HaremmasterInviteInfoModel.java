package com.money.model;

import javax.persistence.*;
import java.util.Date;

/**
 * 群主邀请情况表
 */
@Entity(name = "HaremmasterInviteInfo")
@Table
public class HaremmasterInviteInfoModel extends BaseModel {

    /**
     * 被邀请人的用户ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    String InvitedUserId;

    /**
     * 邀请码填写时间
     */
    Date InvitedDate;

    /**
     * 群主ID
     */
    String HaremmasterUserId;

    public String getInvitedUserId() {
        return InvitedUserId;
    }

    public void setInvitedUserId(String invitedUserId) {
        InvitedUserId = invitedUserId;
    }

    public Date getInvitedDate() {
        return InvitedDate;
    }

    public void setInvitedDate(Date invitedDate) {
        InvitedDate = invitedDate;
    }

    public String getHaremmasterUserId() {
        return HaremmasterUserId;
    }

    public void setHaremmasterUserId(String haremmasterUserId) {
        HaremmasterUserId = haremmasterUserId;
    }
}
