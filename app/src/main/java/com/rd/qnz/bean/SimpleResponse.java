package com.rd.qnz.bean;

/**
 * Created by Administrator on 2017/2/27 0027.
 */

import java.io.Serializable;

/**
 * 当数据没有具体的data数据时返回的bean
 */
public class SimpleResponse implements Serializable {

    private static final long serialVersionUID = -1477609349345966116L;

    public String resultCode;
    public String resultMsg;

    public BaseBean toBaseBean() {
        BaseBean baseBean = new BaseBean();
        baseBean.resultCode = resultCode;
//        baseBean.resultMsg = resultMsg;
        return baseBean;
    }
}
