package com.rd.qnz.bean;

/**
 * 下一页2017/5/27 0027.
 */

public class Next implements EvenInter{
    private int fenshu;
    private Boolean issend;
    public int getFenshu() {
        return fenshu;
    }

    public void setFenshu(int fenshu) {
        this.fenshu = fenshu;
    }

    public Boolean getIssend() {
        return issend;
    }

    public void setIssend(Boolean issend) {
        this.issend = issend;
    }

    public Next(int fenshu){
        this.fenshu=fenshu;
    }
    public Next(int fenshu,boolean issend){
        this.fenshu=fenshu;
        this.issend=issend;
    }
}
