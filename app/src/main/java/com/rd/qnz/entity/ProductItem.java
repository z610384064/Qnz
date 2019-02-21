package com.rd.qnz.entity;

/**
 * 标列表item
 */

/* {
productList[
{
timeLimitDay=1,
flowMoney=0,
isday=1,
isExperience=0,
type=112,
lowestAccount=100,
productType=1,
flowYesCount=0,
accountYes=4723,
addtime=1436953588000,
tenderTimes=8,
extraAwardApr=1,
style=2,
productStatus=0,
borrowId=19,
userId=17,
name=日益宝 No.00007,
hasRepaidPeriod=0,
borrowType=1,
totalPeriod=0,
isNewHand=1,
timeLimit=0,
normalApr=12,
status=8,
flowCount=0,
preSaleTimeDes=null,
brType=1,
franchiseeName=方捷投资,
account=5000,
franchiseeId=3,
userShowName=18458108159,
isAdvanceRepay=0}

} */

public class ProductItem {

    private String timeLimitDay;
    private String flowMoney;
    private String isday;
    private String isExperience;
    private String type;
    private String lowestAccount;
    private String productType;
    private String flowYesCount;
    private String accountYes;  //已购买金额
    private String addtime;
    private String tenderTimes;
    private String extraAwardApr;  //额外加息
    private String style;
    private String productStatus;
    private String borrowId;  //产品id
    private String userId;
    private String name;   //标明
    private String hasRepaidPeriod;
    private String borrowType;
    private String totalPeriod;
    private String isNewHand;   //是否是新手
    private String timeLimit;
    private String normalApr;
    private String status;
    private String flowCount;
    private String preSaleTimeDes;
    private String brType;
    private String franchiseeName;
    private String account;   //总金额
    private String franchiseeId;
    private String userShowName;
    private String isAdvanceRepay; //是否能提前还款   1支持  0不支持

    public String getTimeLimitDay() {
        return timeLimitDay;
    }

    public void setTimeLimitDay(String timeLimitDay) {
        this.timeLimitDay = timeLimitDay;
    }

    public String getFlowMoney() {
        return flowMoney;
    }

    public void setFlowMoney(String flowMoney) {
        this.flowMoney = flowMoney;
    }

    public String getIsday() {
        return isday;
    }

    public void setIsday(String isday) {
        this.isday = isday;
    }

    public String getIsExperience() {
        return isExperience;
    }

    public void setIsExperience(String isExperience) {
        this.isExperience = isExperience;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLowestAccount() {
        return lowestAccount;
    }

    public void setLowestAccount(String lowestAccount) {
        this.lowestAccount = lowestAccount;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getFlowYesCount() {
        return flowYesCount;
    }

    public void setFlowYesCount(String flowYesCount) {
        this.flowYesCount = flowYesCount;
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

    public String getTenderTimes() {
        return tenderTimes;
    }

    public void setTenderTimes(String tenderTimes) {
        this.tenderTimes = tenderTimes;
    }

    public String getExtraAwardApr() {
        return extraAwardApr;
    }

    public void setExtraAwardApr(String extraAwardApr) {
        this.extraAwardApr = extraAwardApr;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getProductStatus() {
        return productStatus;
    }

    public void setProductStatus(String productStatus) {
        this.productStatus = productStatus;
    }

    public String getBorrowId() {
        return borrowId;
    }

    public void setBorrowId(String borrowId) {
        this.borrowId = borrowId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHasRepaidPeriod() {
        return hasRepaidPeriod;
    }

    public void setHasRepaidPeriod(String hasRepaidPeriod) {
        this.hasRepaidPeriod = hasRepaidPeriod;
    }

    public String getBorrowType() {
        return borrowType;
    }

    public void setBorrowType(String borrowType) {
        this.borrowType = borrowType;
    }

    public String getTotalPeriod() {
        return totalPeriod;
    }

    public void setTotalPeriod(String totalPeriod) {
        this.totalPeriod = totalPeriod;
    }

    public String getIsNewHand() {
        return isNewHand;
    }

    public void setIsNewHand(String isNewHand) {
        this.isNewHand = isNewHand;
    }

    public String getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(String timeLimit) {
        this.timeLimit = timeLimit;
    }

    public String getNormalApr() {
        return normalApr;
    }

    public void setNormalApr(String normalApr) {
        this.normalApr = normalApr;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFlowCount() {
        return flowCount;
    }

    public void setFlowCount(String flowCount) {
        this.flowCount = flowCount;
    }

    public String getPreSaleTimeDes() {
        return preSaleTimeDes;
    }

    public void setPreSaleTimeDes(String preSaleTimeDes) {
        this.preSaleTimeDes = preSaleTimeDes;
    }

    public String getBrType() {
        return brType;
    }

    public void setBrType(String brType) {
        this.brType = brType;
    }

    public String getFranchiseeName() {
        return franchiseeName;
    }

    public void setFranchiseeName(String franchiseeName) {
        this.franchiseeName = franchiseeName;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getFranchiseeId() {
        return franchiseeId;
    }

    public void setFranchiseeId(String franchiseeId) {
        this.franchiseeId = franchiseeId;
    }

    public String getUserShowName() {
        return userShowName;
    }

    public void setUserShowName(String userShowName) {
        this.userShowName = userShowName;
    }

    public String getIsAdvanceRepay() {
        return isAdvanceRepay;
    }

    public void setIsAdvanceRepay(String isAdvanceRepay) {
        this.isAdvanceRepay = isAdvanceRepay;
    }
}
