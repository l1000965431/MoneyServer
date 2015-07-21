package com.money.model;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by fisher on 2015/7/18.
 */
@Entity
@Table(name = "UserBorrow")
public class UserBorrowModel implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //�������ֻ���
    private String userId;//service���username
    //���֤
    private String identity;
    //һ�仰�����Լ�
    private String selfIntroduce;
    //�ó�����
    private String goodAtField;
    //��������
    private String education;
    //���˽���
    private String personalProfile;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getSelfIntroduce() {
        return selfIntroduce;
    }

    public void setSelfIntroduce(String selfIntroduce) {
        this.selfIntroduce = selfIntroduce;
    }

    public String getGoodAtField() {
        return goodAtField;
    }

    public void setGoodAtField(String goodAtField) {
        this.goodAtField = goodAtField;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getPersonalProfile() {
        return personalProfile;
    }

    public void setPersonalProfile(String personalProfile) {
        this.personalProfile = personalProfile;
    }


}
