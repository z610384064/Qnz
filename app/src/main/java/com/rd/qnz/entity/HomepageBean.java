package com.rd.qnz.entity;

import com.rd.qnz.bean.HomepageButtomItemBean;

import java.util.ArrayList;

/**
 * 首页对应的bean
 */
public class HomepageBean {
    private HomepageBannerItemBean horBanner;//banner广告
    private ArrayList<HomepageBannerItemBean> bannerList;//bannerlist viewpager里面的广告
    private ArrayList<HomepageButtomItemBean> bottomGG;
    private String counttender;//已投资人数
    private String latestDate;//"latestDate"  用于判断时候有新公告的时间字段
    private ProductItemBean product;   //首页的推荐产品
    private String loginstatus;//loginstatus  1 已经登录  0 未登录或者是新手
    public static final String LOGIN_STATUS_YES = "1";//已登录
    public static final String LOGIN_STATUS_NO = "0";//未登陆或者为新手
    private String tenderTotal = "";//投资总金额
    private String memberNum = "";//投资总人数

    public ArrayList<HomepageButtomItemBean> getBottomGG() {
        return bottomGG;
    }

    public void setBottomGG(ArrayList<HomepageButtomItemBean> bottomGG) {
        this.bottomGG = bottomGG;
    }

    public String getTenderTotal() {
        return tenderTotal;
    }

    public void setTenderTotal(String tenderTotal) {
        this.tenderTotal = tenderTotal;
    }

    public String getMemberNum() {
        return memberNum;
    }

    public void setMemberNum(String memberNum) {
        this.memberNum = memberNum;
    }

    public HomepageBannerItemBean getHorBanner() {
        return horBanner;
    }

    public void setHorBanner(HomepageBannerItemBean horBanner) {
        this.horBanner = horBanner;
    }

    public ArrayList<HomepageBannerItemBean> getBannerList() {
        return bannerList;
    }

    public void setBannerList(ArrayList<HomepageBannerItemBean> bannerList) {
        this.bannerList = bannerList;
    }

    public String getCounttender() {
        return counttender;
    }

    public void setCounttender(String counttender) {
        this.counttender = counttender;
    }

    public String getLatestDate() {
        return latestDate;
    }

    public void setLatestDate(String latestDate) {
        this.latestDate = latestDate;
    }

    public ProductItemBean getProduct() {
        return product;
    }

    public void setProduct(ProductItemBean product) {
        this.product = product;
    }

    public String getLoginstatus() {
        return loginstatus;
    }

    public void setLoginstatus(String loginstatus) {
        this.loginstatus = loginstatus;
    }

}
