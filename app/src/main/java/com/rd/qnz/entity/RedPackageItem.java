package com.rd.qnz.entity;

/**
 * 我的红包Item
 * Created by Evonne on 2016/12/7.
 */
public class RedPackageItem {

    private String status;
    private String createDate;
    private String redPacketAmount;
    private String redPacketType;
    private String redpacketId;
    private String validDate;
    private String timeLimit;
    private String aprLimit;
    private String dayLimit;


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getRedPacketAmount() {
        return redPacketAmount;
    }

    public void setRedPacketAmount(String redPacketAmount) {
        this.redPacketAmount = redPacketAmount;
    }

    public String getRedPacketType() {
        return redPacketType;
    }

    public void setRedPacketType(String redPacketType) {
        this.redPacketType = redPacketType;
    }

    public String getRedpacketId() {
        return redpacketId;
    }

    public void setRedpacketId(String redpacketId) {
        this.redpacketId = redpacketId;
    }

    public String getValidDate() {
        return validDate;
    }

    public void setValidDate(String validDate) {
        this.validDate = validDate;
    }

    public String getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(String timeLimit) {
        this.timeLimit = timeLimit;
    }

    public String getAprLimit() {
        return aprLimit;
    }

    public void setAprLimit(String aprLimit) {
        this.aprLimit = aprLimit;
    }

    public String getDayLimit() {
        return dayLimit;
    }

    public void setDayLimit(String dayLimit) {
        this.dayLimit = dayLimit;
    }
}
