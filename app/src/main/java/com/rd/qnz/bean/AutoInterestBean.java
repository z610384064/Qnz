package com.rd.qnz.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/4/12 0012.
 */

public class AutoInterestBean implements Serializable {


        /**
         * autoData : {"aprEnd":0,"aprStart":0,"balance":35373.06,"isAdvance":0,"isOpen":0,"realstatus":1,"tenderAccount":0,"tenderWay":0,"timeEnd":0,"timeStart":0,"useMoney":0,"userName":"q13867415694"}
         */

        private AutoDataBean autoData;

        public AutoDataBean getAutoData() {
            return autoData;
        }

        public void setAutoData(AutoDataBean autoData) {
            this.autoData = autoData;
        }

        public static class AutoDataBean implements Serializable{
            /**
             * aprEnd : 0     最高利率
             * aprStart : 0   最低利率
             * balance : 35373.06  用户余额
             * isAdvance : 0   没用
             * isOpen : 0  是否开启自动投资
             * realstatus : 1  是否实名
             * tenderAccount : 0   当前设置的投资金额
             * tenderWay : 0   在开启自动投资的时候传1
             * timeEnd : 0     期限结束时间
             * timeStart : 0   期限开始时间
             * useMoney : 0.0
             * userName : q13867415694
             */

            private String aprEnd;
            private String aprStart;
            private double balance;
            private String isAdvance;
            private String isOpen;
            private String realstatus;
            private String tenderAccount;
            private String tenderWay;
            private String timeEnd;
            private String timeStart;
            private double useMoney;
            private String userName;

            @Override
            public String toString() {
                return "AutoDataBean{" +
                        "aprEnd='" + aprEnd + '\'' +
                        ", aprStart='" + aprStart + '\'' +
                        ", balance=" + balance +
                        ", isAdvance='" + isAdvance + '\'' +
                        ", isOpen='" + isOpen + '\'' +
                        ", realstatus='" + realstatus + '\'' +
                        ", tenderAccount='" + tenderAccount + '\'' +
                        ", tenderWay='" + tenderWay + '\'' +
                        ", timeEnd='" + timeEnd + '\'' +
                        ", timeStart='" + timeStart + '\'' +
                        ", useMoney=" + useMoney +
                        ", userName='" + userName + '\'' +
                        '}';
            }

            public String getAprEnd() {
                return aprEnd;
            }

            public void setAprEnd(String aprEnd) {
                this.aprEnd = aprEnd;
            }

            public String getTimeStart() {
                return timeStart;
            }

            public void setTimeStart(String timeStart) {
                this.timeStart = timeStart;
            }

            public String getAprStart() {
                return aprStart;
            }

            public void setAprStart(String aprStart) {
                this.aprStart = aprStart;
            }

            public String getIsAdvance() {
                return isAdvance;
            }

            public void setIsAdvance(String isAdvance) {
                this.isAdvance = isAdvance;
            }

            public String getIsOpen() {
                return isOpen;
            }

            public void setIsOpen(String isOpen) {
                this.isOpen = isOpen;
            }

            public String getRealstatus() {
                return realstatus;
            }

            public void setRealstatus(String realstatus) {
                this.realstatus = realstatus;
            }

            public String getTenderAccount() {
                return tenderAccount;
            }

            public void setTenderAccount(String tenderAccount) {
                this.tenderAccount = tenderAccount;
            }

            public String getTenderWay() {
                return tenderWay;
            }

            public void setTenderWay(String tenderWay) {
                this.tenderWay = tenderWay;
            }

            public String getTimeEnd() {
                return timeEnd;
            }

            public void setTimeEnd(String timeEnd) {
                this.timeEnd = timeEnd;
            }

            public double getBalance() {
                return balance;
            }

            public void setBalance(double balance) {
                this.balance = balance;
            }



            public double getUseMoney() {
                return useMoney;
            }

            public void setUseMoney(double useMoney) {
                this.useMoney = useMoney;
            }

            public String getUserName() {
                return userName;
            }

            public void setUserName(String userName) {
                this.userName = userName;
            }
        }

}
