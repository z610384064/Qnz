package com.rd.qnz.bean;

/**
 * Created by Administrator on 2017/2/27 0027.
 */

public class UpdateBean {

    public static class AppVersionBean {
        /**
         * id : 1
         * content : 1.新年全新logo设计；
         2.SSL认证协议,更安全;
         3.资金图标, 日历回款;
         * action : 1代表强制更新  2(代表不强制)
         * createTime : 1482135977000
         * url : http://a.b.c/apk/钱内助1.29-qian360.apk
         * deviceType : 3
         * version : 1.2.9
         */

        private int id;    //1
        private String content;
        private int action;
        private long createTime;
        private String url;
        private int deviceType;
        private String version;  //1.2.9

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public int getAction() {
            return action;
        }

        public void setAction(int action) {
            this.action = action;
        }

        public long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public int getDeviceType() {
            return deviceType;
        }

        public void setDeviceType(int deviceType) {
            this.deviceType = deviceType;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }
    }
}
