package com.rd.qnz.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/2/27 0027.
 *  刷新token
 */

public class RefreshBean implements Serializable{

    private static final long serialVersionUID = -1709688913712690346L;
    /**
     * bankStatus : 0
     * emailStatus : 0
     * expires : 2592000000
     * oauthToken : 8a7b74e95a6e18ae015a7e500143000e
     * payPwdFlag : 1
     * phone : 13867415694
     * phoneStatus : 1
     * realName : 周文财
     * realStatus : 1
     * refreshToken : 8a7b74e95a6e18ae015a7e500143000f
     * share : jQRw4wkfB8u9jJS3yF4QCQ==
     * useMoney : 0
     * userId : 424
     * userName : q13867415694
     *
     */

    private int bankStatus;
    private int emailStatus;
    private String expires;
    private String oauthToken;
    private int payPwdFlag;
    private String phone;
    private int phoneStatus;
    private String realName;
    private int realStatus;
    private String refreshToken;
    private String share;
    private int useMoney;
    private int userId;
    private String userName;
    private String userIcon;

    public String getUserIcon() {
        return userIcon;
    }

    public void setUserIcon(String userIcon) {
        this.userIcon = userIcon;
    }

    public int getBankStatus() {
        return bankStatus;
    }

    public void setBankStatus(int bankStatus) {
        this.bankStatus = bankStatus;
    }

    public int getEmailStatus() {
        return emailStatus;
    }

    public void setEmailStatus(int emailStatus) {
        this.emailStatus = emailStatus;
    }

    public String getExpires() {
        return expires;
    }

    public void setExpires(String expires) {
        this.expires = expires;
    }

    public String getOauthToken() {
        return oauthToken;
    }

    public void setOauthToken(String oauthToken) {
        this.oauthToken = oauthToken;
    }

    public int getPayPwdFlag() {
        return payPwdFlag;
    }

    public void setPayPwdFlag(int payPwdFlag) {
        this.payPwdFlag = payPwdFlag;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getPhoneStatus() {
        return phoneStatus;
    }

    public void setPhoneStatus(int phoneStatus) {
        this.phoneStatus = phoneStatus;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public int getRealStatus() {
        return realStatus;
    }

    public void setRealStatus(int realStatus) {
        this.realStatus = realStatus;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getShare() {
        return share;
    }

    public void setShare(String share) {
        this.share = share;
    }

    public int getUseMoney() {
        return useMoney;
    }

    public void setUseMoney(int useMoney) {
        this.useMoney = useMoney;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
