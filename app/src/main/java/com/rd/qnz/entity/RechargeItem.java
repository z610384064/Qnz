package com.rd.qnz.entity;

/**
 * 充值记录Item
 * Created by Evonne on 2016/12/8.
 * <p/>
 * <p/>
 * 充值时间"add_time" ,
 * 银行名 "bank_name"，
 * 卡号后四位 "card_no",
 * 充值名"goods_name"（app充值，一毛钱银行卡校验，账户余额充值，线下充值）
 * 充值状态 "status"（200成功，其他未完成）
 * 充值金额"trade_money"
 * 充值类型  "trade_type"（app充值115，一毛钱银行卡校验105，账户余额充值108，线下充值219）
 * app充值和一毛钱银行卡校验有银行卡，账户余额充值和线下充值无
 */
public class RechargeItem {

    private String addTime;
    private String bankName;
    private String cardNo;
    private String goodsName;
    private String status;
    private String tradeMoney;
    private String tradeType;


    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTradeMoney() {
        return tradeMoney;
    }

    public void setTradeMoney(String tradeMoney) {
        this.tradeMoney = tradeMoney;
    }

    public String getTradeType() {
        return tradeType;
    }

    public void setTradeType(String tradeType) {
        this.tradeType = tradeType;
    }
}
