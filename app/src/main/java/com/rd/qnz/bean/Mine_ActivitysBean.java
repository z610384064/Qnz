package com.rd.qnz.bean;

import java.util.List;

/**
 * Created by Administrator on 2017/4/21 0021.
 */

public class Mine_ActivitysBean {

    private List<ActivityListBean> activityList;
    /**
     * addTime : 1492513903000
     * color :
     * duce : “天上”掉下iPhone 7？Plus？！128G？！！还有海淘断货爆款等着你~
     * fileName : 海淘季，夏日专场！
     * fileTypeName : 手机活动图
     * franchiseeId : 1
     * fullRedirectUrl : http://testqnz.qianneizhu.com/activity/monthActivity/queensDay.html?returnUrl=20170419m&code=20170419
     * fullShowUrl : http://testqnz.qianneizhu.com/data/images/banner/1492513840717.png
     * id : 116
     * intro : 海淘季，夏日专场！
     * locationUrl : /activity/monthActivity/queensDay.html?returnUrl=20170419m&code=20170419
     * subType :
     * url : /data/images/banner/1492513840717.png
     */
    public static class ActivityListBean {
        private long addTime;
        private String color;
        private String duce;
        private String fileName;
        private String fileTypeName;
        private int franchiseeId;
        private String fullRedirectUrl;
        private String fullShowUrl;
        private int id;
        private String intro;
        private String locationUrl;
        private String subType;
        private String url;

        public long getAddTime() {
            return addTime;
        }

        public void setAddTime(long addTime) {
            this.addTime = addTime;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public String getDuce() {
            return duce;
        }

        public void setDuce(String duce) {
            this.duce = duce;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getFileTypeName() {
            return fileTypeName;
        }

        public void setFileTypeName(String fileTypeName) {
            this.fileTypeName = fileTypeName;
        }

        public int getFranchiseeId() {
            return franchiseeId;
        }

        public void setFranchiseeId(int franchiseeId) {
            this.franchiseeId = franchiseeId;
        }

        public String getFullRedirectUrl() {
            return fullRedirectUrl;
        }

        public void setFullRedirectUrl(String fullRedirectUrl) {
            this.fullRedirectUrl = fullRedirectUrl;
        }

        public String getFullShowUrl() {
            return fullShowUrl;
        }

        public void setFullShowUrl(String fullShowUrl) {
            this.fullShowUrl = fullShowUrl;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getIntro() {
            return intro;
        }

        public void setIntro(String intro) {
            this.intro = intro;
        }

        public String getLocationUrl() {
            return locationUrl;
        }

        public void setLocationUrl(String locationUrl) {
            this.locationUrl = locationUrl;
        }

        public String getSubType() {
            return subType;
        }

        public void setSubType(String subType) {
            this.subType = subType;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public List<ActivityListBean> getActivityList() {
        return activityList;
    }

    public void setActivityList(List<ActivityListBean> activityList) {
        this.activityList = activityList;
    }
}
