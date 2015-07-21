package com.money.model;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by fisher on 2015/7/18.
 */
@Entity
@Table(name = "User")
public class UserModel implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //�������ֻ���
    private String userId;//service���username
    //��¼����
    private String password;
    //�û�����
    private String userType;
    //�û���
    private String userName;
    //����
    private String mail;
    //�Ա�
    private String sex;
    //���ڵ�
    private String location;
    //��ʵ����
    private String realName;





    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }


}
