package com.rd.qnz.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/2/27 0027.
 */

public class LoginBean implements Serializable {
    private static final long serialVersionUID = 6718299996243948081L;

    /**
     * cardExist : 1
     * phoneExist : 1
     */

    private String cardExist;   //是否绑定银行卡(1:绑定)
    private String phoneExist;  //手机号是否已经注册(1:注册)

    public String getCardExist() {
        return cardExist;
    }

    public void setCardExist(String cardExist) {
        this.cardExist = cardExist;
    }

    public String getPhoneExist() {
        return phoneExist;
    }

    public void setPhoneExist(String phoneExist) {
        this.phoneExist = phoneExist;
    }
}
