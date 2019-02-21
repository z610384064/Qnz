package com.rd.qnz.bean;

/**
 *启动页接口 on 2017/9/21 0021.
 */

public class StartPageBean {
       private String countdownTime;
       private String locationUrl;
         private String  startPageUrl;

    public String getCountdownTime() {
        return countdownTime;
    }

    public void setCountdownTime(String countdownTime) {
        this.countdownTime = countdownTime;
    }

    public String getLocationUrl() {
        return locationUrl;
    }

    public void setLocationUrl(String locationUrl) {
        this.locationUrl = locationUrl;
    }

    public String getStartPageUrl() {
        return startPageUrl;
    }

    public void setStartPageUrl(String startPageUrl) {
        this.startPageUrl = startPageUrl;
    }
}
