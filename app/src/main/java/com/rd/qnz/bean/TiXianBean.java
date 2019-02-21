package com.rd.qnz.bean;

/**
 * Created by Administrator on 2017/3/29 0029.
 *   提现页面返回的数据
 */

public class TiXianBean {


        /**
         * balance : 100.28
         * bankcard : {"addTime":1487317038000,"amountOrCardNo":"尾号6196","bankCode":"01050000","bankShortName":"建设银行","cardNo":"6210811592010376196","hiddenCardNo":"6196","id":110550,"isDefault":0,"isneed":"0","perDayLimit":200000,"perDealLimit":100000,"realName":"周文财","status":1,"uniqNo":"BCN1702171003568500"}
         * isNeedPopSetCard : false
         * ishaveBank : true
         * tok : N2N10BD1J0HQXZOY13MMLW7L3OM6ZZ5I
         */

        private String balance;
        private BankcardBean bankcard;
        private boolean isNeedPopSetCard;
        private boolean ishaveBank;
        private String tok;

        public String getBalance() {
            return balance;
        }

        public void setBalance(String balance) {
            this.balance = balance;
        }

        public BankcardBean getBankcard() {
            return bankcard;
        }

        public void setBankcard(BankcardBean bankcard) {
            this.bankcard = bankcard;
        }

        public boolean isIsNeedPopSetCard() {
            return isNeedPopSetCard;
        }

        public void setIsNeedPopSetCard(boolean isNeedPopSetCard) {
            this.isNeedPopSetCard = isNeedPopSetCard;
        }

        public boolean isIshaveBank() {
            return ishaveBank;
        }

        public void setIshaveBank(boolean ishaveBank) {
            this.ishaveBank = ishaveBank;
        }

        public String getTok() {
            return tok;
        }

        public void setTok(String tok) {
            this.tok = tok;
        }

        public static class BankcardBean {
            /**
             * addTime : 1487317038000
             * amountOrCardNo : 尾号6196
             * bankCode : 01050000
             * bankShortName : 建设银行
             * cardNo : 6210811592010376196
             * hiddenCardNo : 6196
             * id : 110550
             * isDefault : 0
             * isneed : 0
             * perDayLimit : 200000
             * perDealLimit : 100000
             * realName : 周文财
             * status : 1
             * uniqNo : BCN1702171003568500
             */

            private long addTime;
            private String amountOrCardNo;
            private String bankCode;
            private String bankShortName;
            private String cardNo;
            private String hiddenCardNo;
            private int id;
            private int isDefault;
            private String isneed;
            private int perDayLimit;
            private int perDealLimit;
            private String realName;
            private int status;
            private String uniqNo;

            public long getAddTime() {
                return addTime;
            }

            public void setAddTime(long addTime) {
                this.addTime = addTime;
            }

            public String getAmountOrCardNo() {
                return amountOrCardNo;
            }

            public void setAmountOrCardNo(String amountOrCardNo) {
                this.amountOrCardNo = amountOrCardNo;
            }

            public String getBankCode() {
                return bankCode;
            }

            public void setBankCode(String bankCode) {
                this.bankCode = bankCode;
            }

            public String getBankShortName() {
                return bankShortName;
            }

            public void setBankShortName(String bankShortName) {
                this.bankShortName = bankShortName;
            }

            public String getCardNo() {
                return cardNo;
            }

            public void setCardNo(String cardNo) {
                this.cardNo = cardNo;
            }

            public String getHiddenCardNo() {
                return hiddenCardNo;
            }

            public void setHiddenCardNo(String hiddenCardNo) {
                this.hiddenCardNo = hiddenCardNo;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public int getIsDefault() {
                return isDefault;
            }

            public void setIsDefault(int isDefault) {
                this.isDefault = isDefault;
            }

            public String getIsneed() {
                return isneed;
            }

            public void setIsneed(String isneed) {
                this.isneed = isneed;
            }

            public int getPerDayLimit() {
                return perDayLimit;
            }

            public void setPerDayLimit(int perDayLimit) {
                this.perDayLimit = perDayLimit;
            }

            public int getPerDealLimit() {
                return perDealLimit;
            }

            public void setPerDealLimit(int perDealLimit) {
                this.perDealLimit = perDealLimit;
            }

            public String getRealName() {
                return realName;
            }

            public void setRealName(String realName) {
                this.realName = realName;
            }

            public int getStatus() {
                return status;
            }

            public void setStatus(int status) {
                this.status = status;
            }

            public String getUniqNo() {
                return uniqNo;
            }

            public void setUniqNo(String uniqNo) {
                this.uniqNo = uniqNo;
            }
        }

}
