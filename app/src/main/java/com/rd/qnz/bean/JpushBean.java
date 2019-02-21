package com.rd.qnz.bean;

/**
 * 极光推送附加字段对应的bean 2017/5/25 0025.
 */

public class JpushBean {
    private String locationUrl;
    private String intro;
    private String duce;
    private String url;  //图片对应的布局

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLocationUrl() {
        return locationUrl;
    }

    public void setLocationUrl(String locationUrl) {
        this.locationUrl = locationUrl;
    }

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
}
