package com.rd.qnz.entity;

import android.os.Parcel;
import android.os.Parcelable;
/*
首页对应的产品
 */
public class ProductItemBean implements Parcelable {
    private String account;//标总额
    private String accountYes;//已投金额
    private String addtime;//添加时间
    private String apr;//年化收益率
    private String rateapr;//年化收益率
    private String normalApr; //基本利率


    private String interestDay;//天数
    private String extraAwardApr;//年化收益率
    private String borrowId;//标id
    private String flowCount;//份数
    private String flowMoney;//金额
    private String flowYesCount;//已投份数
    private String franchiseeId;//钱行id
    private String franchiseeName;//钱行名称
    private String hasRepaidPeriod;//已还期数
    private String isExperience;//是否体验馆
    private String isday;//是否是天标

    public static final String IS_DAY_YES = "1";
    public static final String IS_DAY_NO = "0";
    private String name;
    private String productType;//1：债券类，2：借款类
    private String status;//status=1代表全部审核通过，用户可以投资，10 预告产品 ，代表钱内助还没有审核通过，进入倒计时。
    private String style;//还款方式（1;等额本息，2;一次性到期还款，3;每月还息到期还本）
    private String timeLimit;//借款期限（月）
    private String timeLimitDay;//借款期限（天标）
    private String totalPeriod;//期数
    private String type;//类型（102,103，110,112）其中（102,103借款类，110，112债券类，其中110是按照份数）
    private String borrowType;//1：债券类，2：借款类
    private String userId;//借款人的userId
    private String userShowName;//借款人的用户名
    private String usetype;//借款用途（11001：经营,11002：购车,11003：购房,11004：装修,11005：应急,11006：助学，11007：其他）
    private String lastRepayTime;//最后待收时间
    private String lowestAccount;//最小投资金额
    private String mostAccount;//最大投资金额（其中0代表没有限制）
    private String productStatus;//产品状态：0:预售，1：申购，2:已售完，3:还款中,4:已完结
    public static final String PRODUCT_STATUS_PRESELL = "0";//预售
    public static final String PRODUCT_STATUS_BUYING = "1";//热卖中，申购中
    public static final String PRODUCT_STATUS_BUY_OVER = "2";//卖完了，还没交易结束，可能会有冻结的释放出来
    public static final String PRODUCT_STATUS_REPAYMENT = "3";//售罄了，还款中
    public static final String PRODUCT_STATUS_OVER = "4";//已完结
    private String countdownFlag;//是否预售倒计时
    //	private String preSaleTime;//预售时间（什么时候开始开售）
    private String countDownTime;//发标倒计时时间（时间差）
    private String isNewHand;//新手专享字段1代表新手专享
    private String isAdvanceRepay;//是否支持提前还款  1支持 0不支持
    public static final String REPAY_YES = "1";
    public static final String REPAY_NO = "0";
    private String brType;//brType 1新手标 2 普通标 3 钱贝勒
    public static final String BR_TYPE_NEW = "1";//新手标
    public static final String BR_TYPE_NORMAL = "2";//普通
    private String preSaleTimeDes;//带时间格式的预售时间  例如  明天 10:00
    private String tenderTimes;//已投资人数

    public String getTenderTimes() {
        return tenderTimes;
    }

    public void setTenderTimes(String tenderTimes) {
        this.tenderTimes = tenderTimes;
    }

    public ProductItemBean(Parcel source) {
        //先读取mId，再读取mDate
        account = source.readString();
        accountYes = source.readString();
        addtime = source.readString();
        apr = source.readString();
        rateapr = source.readString();
        normalApr=source.readString();
        interestDay = source.readString();
        extraAwardApr=source.readString();
        borrowId = source.readString();
        flowCount = source.readString();
        flowMoney = source.readString();
        flowYesCount = source.readString();
        franchiseeId = source.readString();
        franchiseeName = source.readString();
        hasRepaidPeriod = source.readString();
        isExperience = source.readString();
        isday = source.readString();
        name = source.readString();
        productType = source.readString();
        status = source.readString();
        style = source.readString();
        timeLimit = source.readString();
        timeLimitDay = source.readString();
        totalPeriod = source.readString();
        type = source.readString();
        borrowType = source.readString();
        userId = source.readString();
        userShowName = source.readString();
        usetype = source.readString();
        lastRepayTime = source.readString();
        lowestAccount = source.readString();
        mostAccount = source.readString();
        productStatus = source.readString();
        countdownFlag = source.readString();
        countDownTime = source.readString();
        isNewHand = source.readString();
        isAdvanceRepay = source.readString();
        brType = source.readString();
        preSaleTimeDes = source.readString();
        tenderTimes = source.readString();
    }



    //实现Parcelable的方法writeToParcel，将ParcelableDate序列化为一个Parcel对象
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        //先写入mId，再写入mDate
        dest.writeString(account);
        dest.writeString(accountYes);
        dest.writeString(addtime);
        dest.writeString(apr);
        dest.writeString(rateapr);
        dest.writeString(normalApr);
        dest.writeString(interestDay);
        dest.writeString(extraAwardApr);
        dest.writeString(borrowId);
        dest.writeString(flowCount);
        dest.writeString(flowMoney);
        dest.writeString(flowYesCount);
        dest.writeString(franchiseeId);

        dest.writeString(franchiseeName);
        dest.writeString(hasRepaidPeriod);
        dest.writeString(isExperience);
        dest.writeString(isday);
        dest.writeString(name);
        dest.writeString(productType);
        dest.writeString(status);
        dest.writeString(style);
        dest.writeString(timeLimit);
        dest.writeString(timeLimitDay);
        dest.writeString(totalPeriod);
        dest.writeString(type);
        dest.writeString(borrowType);
        dest.writeString(userId);
        dest.writeString(userShowName);
        dest.writeString(usetype);
        dest.writeString(lastRepayTime);
        dest.writeString(lowestAccount);
        dest.writeString(mostAccount);
        dest.writeString(productStatus);
        dest.writeString(countdownFlag);
        dest.writeString(countDownTime);
        dest.writeString(isNewHand);
        dest.writeString(isAdvanceRepay);
        dest.writeString(brType);
        dest.writeString(preSaleTimeDes);
        dest.writeString(tenderTimes);
    }

    public String getNormalApr() {
        return normalApr;
    }

    public void setNormalApr(String normalApr) {
        this.normalApr = normalApr;
    }

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

    public String getAddtime() {
        return addtime;
    }

    public void setAddtime(String addtime) {
        this.addtime = addtime;
    }

    public String getApr() {
        return apr;
    }

    public String getRateapr() {
        return rateapr;
    }

    public void setRateapr(String rateapr) {
        this.rateapr = rateapr;
    }

    public String getInterestDay() {
        return interestDay;
    }

    public void setInterestDay(String interestDay) {
        this.interestDay = interestDay;
    }

    public void setApr(String apr) {
        this.apr = apr;
    }

    public String getExtraAwardApr() {
        return extraAwardApr;
    }

    public void setExtraAwardApr(String extraAwardApr) {
        this.extraAwardApr = extraAwardApr;
    }

    public String getBorrowId() {
        return borrowId;
    }

    public void setBorrowId(String borrowId) {
        this.borrowId = borrowId;
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

    public String getFlowYesCount() {
        return flowYesCount;
    }

    public void setFlowYesCount(String flowYesCount) {
        this.flowYesCount = flowYesCount;
    }

    public String getFranchiseeId() {
        return franchiseeId;
    }

    public void setFranchiseeId(String franchiseeId) {
        this.franchiseeId = franchiseeId;
    }

    public String getFranchiseeName() {
        return franchiseeName;
    }

    public void setFranchiseeName(String franchiseeName) {
        this.franchiseeName = franchiseeName;
    }

    public String getHasRepaidPeriod() {
        return hasRepaidPeriod;
    }

    public void setHasRepaidPeriod(String hasRepaidPeriod) {
        this.hasRepaidPeriod = hasRepaidPeriod;
    }

    public String getIsExperience() {
        return isExperience;
    }

    public void setIsExperience(String isExperience) {
        this.isExperience = isExperience;
    }

    public String getIsday() {
        return isday;
    }

    public void setIsday(String isday) {
        this.isday = isday;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(String timeLimit) {
        this.timeLimit = timeLimit;
    }

    public String getTimeLimitDay() {
        return timeLimitDay;
    }

    public void setTimeLimitDay(String timeLimitDay) {
        this.timeLimitDay = timeLimitDay;
    }

    public String getTotalPeriod() {
        return totalPeriod;
    }

    public void setTotalPeriod(String totalPeriod) {
        this.totalPeriod = totalPeriod;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBorrowType() {
        return borrowType;
    }

    public void setBorrowType(String borrowType) {
        this.borrowType = borrowType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserShowName() {
        return userShowName;
    }

    public void setUserShowName(String userShowName) {
        this.userShowName = userShowName;
    }

    public String getUsetype() {
        return usetype;
    }

    public void setUsetype(String usetype) {
        this.usetype = usetype;
    }

    public String getLastRepayTime() {
        return lastRepayTime;
    }

    public void setLastRepayTime(String lastRepayTime) {
        this.lastRepayTime = lastRepayTime;
    }

    public String getLowestAccount() {
        return lowestAccount;
    }

    public void setLowestAccount(String lowestAccount) {
        this.lowestAccount = lowestAccount;
    }

    public String getMostAccount() {
        return mostAccount;
    }

    public void setMostAccount(String mostAccount) {
        this.mostAccount = mostAccount;
    }

    public String getProductStatus() {
        return productStatus;
    }

    public void setProductStatus(String productStatus) {
        this.productStatus = productStatus;
    }

    public String getCountdownFlag() {
        return countdownFlag;
    }

    public void setCountdownFlag(String countdownFlag) {
        this.countdownFlag = countdownFlag;
    }

    public String getCountDownTime() {
        return countDownTime;
    }

    public void setCountDownTime(String countDownTime) {
        this.countDownTime = countDownTime;
    }

    public String getIsNewHand() {
        return isNewHand;
    }

    public void setIsNewHand(String isNewHand) {
        this.isNewHand = isNewHand;
    }

    public String getIsAdvanceRepay() {
        return isAdvanceRepay;
    }

    public void setIsAdvanceRepay(String isAdvanceRepay) {
        this.isAdvanceRepay = isAdvanceRepay;
    }

    public String getBrType() {
        return brType;
    }

    public void setBrType(String brType) {
        this.brType = brType;
    }

    public String getPreSaleTimeDes() {
        return preSaleTimeDes;
    }

    public void setPreSaleTimeDes(String preSaleTimeDes) {
        this.preSaleTimeDes = preSaleTimeDes;
    }


    //实例化静态内部对象CREATOR实现接口Parcelable.Creator  
    public static final Parcelable.Creator<ProductItemBean> CREATOR = new Creator<ProductItemBean>() {

        @Override
        public ProductItemBean[] newArray(int size) {
            return new ProductItemBean[size];
        }

        //将Parcel对象反序列化为ParcelableDate  
        @Override
        public ProductItemBean createFromParcel(Parcel source) {
            return new ProductItemBean(source);
        }
    };

    @Override
    public int describeContents() {
        // TODO 自动生成的方法存根
        return 0;
    }

}
