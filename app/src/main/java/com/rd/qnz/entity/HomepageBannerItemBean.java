package com.rd.qnz.entity;

/**
 * viewPager里面的广告对应的bean
 */
public class HomepageBannerItemBean {
    private String addTime;
    private String fileName;
    private String id;
    private String fullShowUrl;
    private String locationUrl;  //图片跳转地址url(点击了viewpager里面的图片对应跳转到的地址)
    private String url;//banner的图片资源地址(展示在viewpager里面的图片对应的地址)
    private String fileTypeName;



    // TODO: 2017/3/9 0009 周文才
    private String intro;
    private String duce;


    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getDuce() {
        return duce;
    }

    public void setDuce(String duce) {
        this.duce = duce;
    }


    public String getFullShowUrl() {
        return fullShowUrl;
    }

    public void setFullShowUrl(String fullShowUrl) {
        this.fullShowUrl = fullShowUrl;
    }

    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLocationUrl() {
        return locationUrl;
    }

    public void setLocationUrl(String locationUrl) {
        this.locationUrl = locationUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFileTypeName() {
        return fileTypeName;
    }

    public void setFileTypeName(String fileTypeName) {
        this.fileTypeName = fileTypeName;
    }
}
