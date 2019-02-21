package com.rd.qnz.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/2/27 0027.
 */

public class BaseBean<T> implements Serializable {

    private static final long serialVersionUID = 6000670123125326831L;
    public String resultCode;
    public String errorCode; //错误的代码
    public String resultMsg;
    public T resultData;
}
