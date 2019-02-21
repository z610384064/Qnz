package com.rd.qnz.bean;

/**
 * 产品详情页bean2017/9/30 0030.
 */

public class ProductContentBean {


        /**
         * act_title : 投资三重奏赢红包
         * act_url : http://test.qianneizhu.com/forward.html?returnUrl=investAward&type=2
         * counttender : 1
         * firstRedPacket : 8
         * highestRedPacket : 58
         * interestDay : 59
         * largestTenderSum : 500.00
         * largestTenderUser : 15168234307
         * lastRedPacket : 38
         * product : {"account":"1000","accountYes":"500","addtime":1506650741000,"apr":"12.3","bmApieceDayIn":0,"bmApieceTotal":0,"bmLimitDes":" ","bmPerDayOutTotal":0,"borrowId":539,"borrowType":1,"brType":"2","city":"重庆市","company":"杭州方捷投资管理有限公司","content":"aa","countDownTime":0,"countdownFlag":0,"debtorInfo":"","extraApr":0,"extraAwardApr":1.5,"flowCount":0,"flowMoney":0,"flowYesCount":0,"franchiseeId":3,"franchiseeName":"方捷投资","franchiseeTime":1410364800000,"fundUsage":"","hasRepaidPeriod":0,"interestEndTime":"期满当日","interestStartTime":"募集完成次日","interestType":1,"isAdvanceRepay":0,"isExperience":0,"isNewHand":0,"isday":1,"lastRepayTime":1511884799000,"logo":"","lowestAccount":100,"mostAccount":0,"name":"车商宝 No.00204","normalApr":10.8,"productStatus":1,"productType":1,"repaymentSource":"","riskEvaluation":"a","secVerifyTime":1506650836000,"status":1,"style":2,"tenderTimes":1,"timeLimit":0,"timeLimitDay":60,"totalPeriod":0,"type":112,"userId":841461,"userShowName":"13555888789","usetype":11001,"validTime":"3"}
         * sendFlag : 0
         * showStatus : 1
         */

        private String act_title;
        private String act_url;
        private int counttender;
        private int firstRedPacket;
        private int highestRedPacket;
        private String interestDay;
        private String largestTenderSum;
        private String largestTenderUser;
        private int lastRedPacket;
        private ProductBean product;
        private int sendFlag;
        private int showStatus;

        public String getAct_title() {
            return act_title;
        }

        public void setAct_title(String act_title) {
            this.act_title = act_title;
        }

        public String getAct_url() {
            return act_url;
        }

        public void setAct_url(String act_url) {
            this.act_url = act_url;
        }

        public int getCounttender() {
            return counttender;
        }

        public void setCounttender(int counttender) {
            this.counttender = counttender;
        }

        public int getFirstRedPacket() {
            return firstRedPacket;
        }

        public void setFirstRedPacket(int firstRedPacket) {
            this.firstRedPacket = firstRedPacket;
        }

        public int getHighestRedPacket() {
            return highestRedPacket;
        }

        public void setHighestRedPacket(int highestRedPacket) {
            this.highestRedPacket = highestRedPacket;
        }

        public String getInterestDay() {
            return interestDay;
        }

        public void setInterestDay(String interestDay) {
            this.interestDay = interestDay;
        }

        public String getLargestTenderSum() {
            return largestTenderSum;
        }

        public void setLargestTenderSum(String largestTenderSum) {
            this.largestTenderSum = largestTenderSum;
        }

        public String getLargestTenderUser() {
            return largestTenderUser;
        }

        public void setLargestTenderUser(String largestTenderUser) {
            this.largestTenderUser = largestTenderUser;
        }

        public int getLastRedPacket() {
            return lastRedPacket;
        }

        public void setLastRedPacket(int lastRedPacket) {
            this.lastRedPacket = lastRedPacket;
        }

        public ProductBean getProduct() {
            return product;
        }

        public void setProduct(ProductBean product) {
            this.product = product;
        }

        public int getSendFlag() {
            return sendFlag;
        }

        public void setSendFlag(int sendFlag) {
            this.sendFlag = sendFlag;
        }

        public int getShowStatus() {
            return showStatus;
        }

        public void setShowStatus(int showStatus) {
            this.showStatus = showStatus;
        }

        public static class ProductBean {
            /**
             * account : 1000
             * accountYes : 500
             * addtime : 1506650741000
             * apr : 12.3
             * bmApieceDayIn : 0
             * bmApieceTotal : 0
             * bmLimitDes :
             * bmPerDayOutTotal : 0
             * borrowId : 539
             * borrowType : 1
             * brType : 2
             * city : 重庆市
             * company : 杭州方捷投资管理有限公司
             * content : aa
             * countDownTime : 0
             * countdownFlag : 0
             * debtorInfo :
             * extraApr : 0
             * extraAwardApr : 1.5
             * flowCount : 0
             * flowMoney : 0
             * flowYesCount : 0
             * franchiseeId : 3
             * franchiseeName : 方捷投资
             * franchiseeTime : 1410364800000
             * fundUsage :
             * hasRepaidPeriod : 0
             * interestEndTime : 期满当日
             * interestStartTime : 募集完成次日
             * interestType : 1
             * isAdvanceRepay : 0
             * isExperience : 0
             * isNewHand : 0
             * isday : 1
             * lastRepayTime : 1511884799000
             * logo :
             * lowestAccount : 100
             * mostAccount : 0
             * name : 车商宝 No.00204
             * normalApr : 10.8
             * productStatus : 1
             * productType : 1
             * repaymentSource :
             * riskEvaluation : a
             * secVerifyTime : 1506650836000
             * status : 1
             * style : 2
             * tenderTimes : 1
             * timeLimit : 0
             * timeLimitDay : 60
             * totalPeriod : 0
             * type : 112
             * userId : 841461
             * userShowName : 13555888789
             * usetype : 11001
             * validTime : 3
             */

            private String account;
            private String accountYes;
            private long addtime;
            private String apr;
            private int bmApieceDayIn;
            private int bmApieceTotal;
            private String bmLimitDes;
            private int bmPerDayOutTotal;
            private int borrowId;
            private String borrowType;
            private String brType;
            private String city;
            private String company;
            private String content;
            private String countDownTime;
            private int countdownFlag;
            private String debtorInfo;
            private int extraApr;
            private String extraAwardApr;
            private String flowCount;
            private String flowMoney;
            private int flowYesCount;
            private int franchiseeId;
            private String franchiseeName;
            private long franchiseeTime;
            private String fundUsage;
            private int hasRepaidPeriod;
            private String interestEndTime;
            private String interestStartTime;
            private int interestType;
            private String isAdvanceRepay;
            private int isExperience;
            private int isNewHand;
            private String isday;
            private String lastRepayTime;
            private String logo;
            private String lowestAccount;
            private int mostAccount;
            private String name;
            private String normalApr;
            private String productStatus;
            private String productType;
            private String repaymentSource;
            private String riskEvaluation;
            private String secVerifyTime;
            private int status;
            private String style;
            private int tenderTimes;
            private int timeLimit;
            private int timeLimitDay;
            private int totalPeriod;
            private String type;
            private int userId;
            private String userShowName;
            private int usetype;
            private String validTime;

            public String getAccount() {
                return account;
            }

            public void setAccount(String account) {
                this.account = account;
            }

            public String getAccountYes() {
                return accountYes;
            }

            public void setAccountYes(String accountYes) {
                this.accountYes = accountYes;
            }

            public long getAddtime() {
                return addtime;
            }

            public void setAddtime(long addtime) {
                this.addtime = addtime;
            }

            public String getApr() {
                return apr;
            }

            public void setApr(String apr) {
                this.apr = apr;
            }

            public int getBmApieceDayIn() {
                return bmApieceDayIn;
            }

            public void setBmApieceDayIn(int bmApieceDayIn) {
                this.bmApieceDayIn = bmApieceDayIn;
            }

            public int getBmApieceTotal() {
                return bmApieceTotal;
            }

            public void setBmApieceTotal(int bmApieceTotal) {
                this.bmApieceTotal = bmApieceTotal;
            }

            public String getBmLimitDes() {
                return bmLimitDes;
            }

            public void setBmLimitDes(String bmLimitDes) {
                this.bmLimitDes = bmLimitDes;
            }

            public int getBmPerDayOutTotal() {
                return bmPerDayOutTotal;
            }

            public void setBmPerDayOutTotal(int bmPerDayOutTotal) {
                this.bmPerDayOutTotal = bmPerDayOutTotal;
            }

            public int getBorrowId() {
                return borrowId;
            }

            public void setBorrowId(int borrowId) {
                this.borrowId = borrowId;
            }

            public String getBorrowType() {
                return borrowType;
            }

            public void setBorrowType(String borrowType) {
                this.borrowType = borrowType;
            }

            public String getBrType() {
                return brType;
            }

            public void setBrType(String brType) {
                this.brType = brType;
            }

            public String getCity() {
                return city;
            }

            public void setCity(String city) {
                this.city = city;
            }

            public String getCompany() {
                return company;
            }

            public void setCompany(String company) {
                this.company = company;
            }

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public String getCountDownTime() {
                return countDownTime;
            }

            public void setCountDownTime(String countDownTime) {
                this.countDownTime = countDownTime;
            }

            public int getCountdownFlag() {
                return countdownFlag;
            }

            public void setCountdownFlag(int countdownFlag) {
                this.countdownFlag = countdownFlag;
            }

            public String getDebtorInfo() {
                return debtorInfo;
            }

            public void setDebtorInfo(String debtorInfo) {
                this.debtorInfo = debtorInfo;
            }

            public int getExtraApr() {
                return extraApr;
            }

            public void setExtraApr(int extraApr) {
                this.extraApr = extraApr;
            }

            public String getExtraAwardApr() {
                return extraAwardApr;
            }

            public void setExtraAwardApr(String extraAwardApr) {
                this.extraAwardApr = extraAwardApr;
            }

            public String getFlowCount() {
                return flowCount;
            }

            public void setFlowCount(String flowCount) {
                this.flowCount = flowCount;
            }

            public String getFlowMoney() {
                return flowMoney;
            }

            public void setFlowMoney(String flowMoney) {
                this.flowMoney = flowMoney;
            }

            public int getFlowYesCount() {
                return flowYesCount;
            }

            public void setFlowYesCount(int flowYesCount) {
                this.flowYesCount = flowYesCount;
            }

            public int getFranchiseeId() {
                return franchiseeId;
            }

            public void setFranchiseeId(int franchiseeId) {
                this.franchiseeId = franchiseeId;
            }

            public String getFranchiseeName() {
                return franchiseeName;
            }

            public void setFranchiseeName(String franchiseeName) {
                this.franchiseeName = franchiseeName;
            }

            public long getFranchiseeTime() {
                return franchiseeTime;
            }

            public void setFranchiseeTime(long franchiseeTime) {
                this.franchiseeTime = franchiseeTime;
            }

            public String getFundUsage() {
                return fundUsage;
            }

            public void setFundUsage(String fundUsage) {
                this.fundUsage = fundUsage;
            }

            public int getHasRepaidPeriod() {
                return hasRepaidPeriod;
            }

            public void setHasRepaidPeriod(int hasRepaidPeriod) {
                this.hasRepaidPeriod = hasRepaidPeriod;
            }

            public String getInterestEndTime() {
                return interestEndTime;
            }

            public void setInterestEndTime(String interestEndTime) {
                this.interestEndTime = interestEndTime;
            }

            public String getInterestStartTime() {
                return interestStartTime;
            }

            public void setInterestStartTime(String interestStartTime) {
                this.interestStartTime = interestStartTime;
            }

            public int getInterestType() {
                return interestType;
            }

            public void setInterestType(int interestType) {
                this.interestType = interestType;
            }

            public String getIsAdvanceRepay() {
                return isAdvanceRepay;
            }

            public void setIsAdvanceRepay(String isAdvanceRepay) {
                this.isAdvanceRepay = isAdvanceRepay;
            }

            public int getIsExperience() {
                return isExperience;
            }

            public void setIsExperience(int isExperience) {
                this.isExperience = isExperience;
            }

            public int getIsNewHand() {
                return isNewHand;
            }

            public void setIsNewHand(int isNewHand) {
                this.isNewHand = isNewHand;
            }

            public String getIsday() {
                return isday;
            }

            public void setIsday(String isday) {
                this.isday = isday;
            }

            public String getLastRepayTime() {
                return lastRepayTime;
            }

            public void setLastRepayTime(String lastRepayTime) {
                this.lastRepayTime = lastRepayTime;
            }

            public String getLogo() {
                return logo;
            }

            public void setLogo(String logo) {
                this.logo = logo;
            }

            public String getLowestAccount() {
                return lowestAccount;
            }

            public void setLowestAccount(String lowestAccount) {
                this.lowestAccount = lowestAccount;
            }

            public int getMostAccount() {
                return mostAccount;
            }

            public void setMostAccount(int mostAccount) {
                this.mostAccount = mostAccount;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getNormalApr() {
                return normalApr;
            }

            public void setNormalApr(String normalApr) {
                this.normalApr = normalApr;
            }

            public String getProductStatus() {
                return productStatus;
            }

            public void setProductStatus(String productStatus) {
                this.productStatus = productStatus;
            }

            public String getProductType() {
                return productType;
            }

            public void setProductType(String productType) {
                this.productType = productType;
            }

            public String getRepaymentSource() {
                return repaymentSource;
            }

            public void setRepaymentSource(String repaymentSource) {
                this.repaymentSource = repaymentSource;
            }

            public String getRiskEvaluation() {
                return riskEvaluation;
            }

            public void setRiskEvaluation(String riskEvaluation) {
                this.riskEvaluation = riskEvaluation;
            }

            public String getSecVerifyTime() {
                return secVerifyTime;
            }

            public void setSecVerifyTime(String secVerifyTime) {
                this.secVerifyTime = secVerifyTime;
            }

            public int getStatus() {
                return status;
            }

            public void setStatus(int status) {
                this.status = status;
            }

            public String getStyle() {
                return style;
            }

            public void setStyle(String style) {
                this.style = style;
            }

            public int getTenderTimes() {
                return tenderTimes;
            }

            public void setTenderTimes(int tenderTimes) {
                this.tenderTimes = tenderTimes;
            }

            public int getTimeLimit() {
                return timeLimit;
            }

            public void setTimeLimit(int timeLimit) {
                this.timeLimit = timeLimit;
            }

            public int getTimeLimitDay() {
                return timeLimitDay;
            }

            public void setTimeLimitDay(int timeLimitDay) {
                this.timeLimitDay = timeLimitDay;
            }

            public int getTotalPeriod() {
                return totalPeriod;
            }

            public void setTotalPeriod(int totalPeriod) {
                this.totalPeriod = totalPeriod;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public int getUserId() {
                return userId;
            }

            public void setUserId(int userId) {
                this.userId = userId;
            }

            public String getUserShowName() {
                return userShowName;
            }

            public void setUserShowName(String userShowName) {
                this.userShowName = userShowName;
            }

            public int getUsetype() {
                return usetype;
            }

            public void setUsetype(int usetype) {
                this.usetype = usetype;
            }

            public String getValidTime() {
                return validTime;
            }

            public void setValidTime(String validTime) {
                this.validTime = validTime;
            }
        }
}
