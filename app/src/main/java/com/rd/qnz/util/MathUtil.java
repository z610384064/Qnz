package com.rd.qnz.util;

import java.math.BigDecimal;

/**
 * 数学运算工具类
 * Created by yinqm on 2017/10/11.
 */
public class MathUtil {
    /**
     * 格式化年化收益率（根据精度处理）
     * @param apr
     * @return
     */
    public static String formatRate(String apr){
        BigDecimal percent = new BigDecimal(apr);
        if (percent.scale() >= 2){
            percent = percent.setScale(2,BigDecimal.ROUND_HALF_UP);
        }else{
            percent = percent.setScale(1,BigDecimal.ROUND_HALF_UP);
        }
        return percent.toString();
    }
}
