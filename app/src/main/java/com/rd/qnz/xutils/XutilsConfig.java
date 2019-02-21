package com.rd.qnz.xutils;

import com.rd.qnz.custom.MyApplication;
import com.rd.qnz.tools.Utils;

import java.io.File;

/**
 * xutils 的配置文件
 *
 * @author win7-64
 */
public class XutilsConfig {
    public static String pakageFilePath = File.separator + "Android" + File.separator + "data" + File.separator + MyApplication.getInstance().getApplicationContext().getPackageName();
    public static String PATH_BASE = Utils.getVolumePath(MyApplication.getInstance().getApplicationContext())
            + pakageFilePath
            + File.separator + "xutils" + File.separator; //在Android/data/com.rd.qnz/xutils缓存图片在里面
    public static String PATH_CACHE = PATH_BASE + "cache" + File.separator;//文件缓存路径
    public static String PATH_START_PAGTE = PATH_BASE + "start" + File.separator;//启动页存储路径
    public static String FILE_START_PAGE_NAME = "startPage.jpg";//启动页名称
}
