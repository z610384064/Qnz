package com.rd.qnz.entity;

import java.util.List;

/**
 * Created by Evonne on 2016/12/16.
 */
public class TimeBean {

    /**
     * resultCode : 1
     * resultData : {"timeList":["1481126400000","1480608000000","1482076800000","1482422400000","1481731200000"]}
     */

    private int resultCode;
    private ResultDataBean resultData;

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public ResultDataBean getResultData() {
        return resultData;
    }

    public void setResultData(ResultDataBean resultData) {
        this.resultData = resultData;
    }

    public static class ResultDataBean {
        private List<String> timeList;

        public List<String> getTimeList() {
            return timeList;
        }

        public void setTimeList(List<String> timeList) {
            this.timeList = timeList;
        }
    }
}
