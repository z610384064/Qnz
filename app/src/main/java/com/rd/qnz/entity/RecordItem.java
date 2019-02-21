package com.rd.qnz.entity;

/**
 * 待回款已回款item
 */
/* {
 "borrowId";
 "borrowName";
 "captial";
 "interest";
 "repaymenTime";
 "repaymentAccount";
 "tenderId";
 "tenderTime";
 "isbin";
 "bankName";
 "hiddenCardNo";
 "backPlace";
} */

public class RecordItem {

    private String borrowId;
    private String borrowName;
    private String captial;
    private String interest;
    private String repaymenTime;
    private String repaymentAccount;
    private String tenderId;
    private String tenderTime;
    private String isbin;
    private String bankName;
    private String hiddenCardNo;
    private String backPlace;

    public String getBorrowId() {
        return borrowId;
    }

    public void setBorrowId(String borrowId) {
        this.borrowId = borrowId;
    }

    public String getBorrowName() {
        return borrowName;
    }

    public void setBorrowName(String borrowName) {
        this.borrowName = borrowName;
    }

    public String getCaptial() {
        return captial;
    }

    public void setCaptial(String captial) {
        this.captial = captial;
    }

    public String getInterest() {
        return interest;
    }

    public void setInterest(String interest) {
        this.interest = interest;
    }

    public String getRepaymenTime() {
        return repaymenTime;
    }

    public void setRepaymenTime(String repaymenTime) {
        this.repaymenTime = repaymenTime;
    }

    public String getRepaymentAccount() {
        return repaymentAccount;
    }

    public void setRepaymentAccount(String repaymentAccount) {
        this.repaymentAccount = repaymentAccount;
    }

    public String getTenderId() {
        return tenderId;
    }

    public void setTenderId(String tenderId) {
        this.tenderId = tenderId;
    }

    public String getTenderTime() {
        return tenderTime;
    }

    public void setTenderTime(String tenderTime) {
        this.tenderTime = tenderTime;
    }

    public String getIsbin() {
        return isbin;
    }

    public void setIsbin(String isbin) {
        this.isbin = isbin;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getHiddenCardNo() {
        return hiddenCardNo;
    }

    public void setHiddenCardNo(String hiddenCardNo) {
        this.hiddenCardNo = hiddenCardNo;
    }

    public String getBackPlace() {
        return backPlace;
    }

    public void setBackPlace(String backPlace) {
        this.backPlace = backPlace;
    }
}
