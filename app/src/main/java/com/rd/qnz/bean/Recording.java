package com.rd.qnz.bean;

/**
 *投资中 2017/9/15 0015.
 */

public class Recording {
    private String account;
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
    private String money; //投资金额
    private String addtime;  //投资日期
    private String repayTime; //标到期回款日

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getAddtime() {
        return addtime;
    }

    public void setAddtime(String addtime) {
        this.addtime = addtime;
    }

    public String getRepayTime() {
        return repayTime;
    }

    public void setRepayTime(String repayTime) {
        this.repayTime = repayTime;
    }

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
