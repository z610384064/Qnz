package com.rd.qnz.entity;

/**
 *  首页弹窗广告对应的bean
 */
public class FloatingWindowBean {
    private String fullShowUrl;  //弹窗对应的图片
    private String fullRedirectUrl;
    private String locationUrl;  //点击弹窗要跳转的网页
    private String fileName; //弹窗的标题
    // TODO: 2017/3/10 0010
    private String intro; //标题
    private String duce; //描述信息

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    private int id;

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getLocationUrl() {
        return locationUrl;
    }

    public void setLocationUrl(String locationUrl) {
        this.locationUrl = locationUrl;
    }


}
