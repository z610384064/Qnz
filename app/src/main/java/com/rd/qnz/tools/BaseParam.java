package com.rd.qnz.tools;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class BaseParam {

    /**
     * 公共参
     */
//    public static String URL_QIAN = "http://www.qianneizhu.com/";//
//      public static String URL_QIAN = "https://www.qianneizhu.com/";//正式库

    public static String URL_QIAN = "http://test.qianneizhu.com/";//测试环境
//      public static String URL_QIAN = "http://121.40.133.235/";//临时测\试环境
//    public static String URL_QIAN = "http://192.168.0.55:8087/";//修改内;网测试环境
//    public static String URL_QIAN = "http://192.1Z68.1.102:8080/";
//    public static String URL_QIAN = "http://qnz-server:8080/";//修改内网测试环境

    public static final String QIAN_CATALOGUE = "/qian/qnz/cache/icon/";
    public static String QIAN = "qian";
    public static String DEVICE_ID = "";//手机meid
    public static String IDFA = "idfa";

    /* 分享title */
    //public static String QIAN_SHARE = "推荐一个高收益、安全又放心的理财app，精明理财人的首选！钱内助+下载客户端app链接 http://www.qianneizhu.com/forward.html?returnUrl=about101";
    public static String QIAN_SHARE = "献给最不平凡的你，即使没有好几个零的存款，不懂复杂的数学游戏，你也值得拥有更好的人生。钱内助+下载客户端app链接 http://www.qianneizhu.com/forward.html?returnUrl=about101";
    //public static String QIAN_PRODUCT_SHARE = "我在钱内助发现了一个很棒的投资项目，快来看看吧！钱内助+下载客户端app链接 http://www.qianneizhu.com/forward.html?returnUrl=about101";
    public static String QIAN_PRODUCT_SHARE = "献给最不平凡的你，即使没有好几个零的存款，不懂复杂的数学游戏，你也值得拥有更好的人生。钱内助+下载客户端app链接 http://www.qianneizhu.com/forward.html?returnUrl=about101";
    public static String QIAN_IMEI = "imei";
    public static String QIAN_WEB_DOWNLOAD = "http://www.qianneizhu.com/forward.html?returnUrl=about101";//下载地址
    public static String QIAN_BANNER_INTRO = "活动火爆进行中，钱内助邀您一起参加！";//首页banner分享描述
    //    public static String QIAN_BANNER_INTRO = "我在钱内助发现了一个很棒的投资项目，快来看看吧！";//首页banner分享描述
    public static String QIAN_ICON_WEB_PATH = "https://www.qianneizhu.com/data/images/app/icon.png";//icon 的网络地址
//    public static String QIAN_ICON_WEB_PATH = "http://testqnz.qianneizhu.com/data/images/app/icon.png";//icon 的网络地址

    // 钱内助 login info
    public static String QIAN_LOGIN_PASSWORD = "password";
    public static String QIAN_LOGIN_USERNAME = "userName";
    public static String QIAN_LOGIN_PHONE = "phone";
    public static String QIAN_LOGIN_PHONECODE = "phoneCode";

    public static String QIAN_LOGIN_REALNAME = "realName";
    public static String QIAN_LOGIN_CARDID = "cardId";

    public static String QIAN_REQUEAT_LOGIN = "login";
    public static String QIAN_REQUEAT_REG = "reg";
    public static String QIAN_REQUEAT_REG_VERIFY = "regVerify";
    public static String QIAN_REQUEAT_RETRIEVE_PHONE_MIBAO = "phone_mibao";

    public static String QIAN_REQUEAT_PRODUCT_CONTENT = "product_content";
    public static String QIAN_REQUEAT_PRODUCT_MORE_LIST = "product_list";
    public static String QIAN_REQUEAT_MY = "my";
    public static String QIAN_REQUEAT_RETURN_VERIFY = "return";
    public static String QIAN_REQUEAT_MY_TENDERRECORD = "myTenderRecord";
    public static String QIAN_REQUEAT_RESETING = "reseting";
    public static String QIAN_REQUEAT_RETURN_FORGETVERIFY = "forget_verify";
    public static String QIAN_REQUEAT_RETURN_FORGETPASSWORD = "forget_password";

    // 实名认证
    public static String URL_REQUEAT_MY_OPENYJFACCOUNT = "openYjfAccount";
    public static String URL_REQUEAT_MY_MODIFYPWD = "modifyPwd";
    public static String URL_REQUEAT_MY_REDPACKETS = "redPackets";
    public static String URL_REQUEAT_MY_HOMEPAGE = "homepage";
    public static String URL_REQUEAT_MY_GETLATESTSTATUS = "getLatestStatus";
    public static String URL_REQUEAT_MY_AVAILABLEREDPACKET = "availableRedPacket";
    public static String URL_REQUEAT_MORE_FEEDBACK = "feedback";
    public static String URL_REQUEAT_CHECKLOGINPWD = "checkLoginPwd";
    public static String URL_REQUEAT_MORE_NOTICE = "notice";
    // 我的银行卡
    public static String URL_REQUEAT_MY_BANK = "myBank";

    public static String QIAN_SHAREDPREFERENCES_USER = "sp_user";
    public static String QIAN_SHAREDPREFERENCES_USER_EXPIRES = "expires";
    public static String QIAN_SHAREDPREFERENCES_USER_OAUTHTOKEN = "oauthToken";
    public static String QIAN_SHAREDPREFERENCES_USER_REFRESHTOKEN = "refreshToken";

    public static String QIAN_SHAREDPREFERENCES_USER_REALSTATUS = "realStatus";
    public static String QIAN_SHAREDPREFERENCES_USER_PHONESTATUS = "phoneStatus";
    public static String QIAN_SHAREDPREFERENCES_USER_EMAILSTATUS = "emailStatus";
    public static String QIAN_SHAREDPREFERENCES_USER_BANKSTATUS = "bankStatus";
    public static String QIAN_SHAREDPREFERENCES_USER_PAYPWDFLAG = "payPwdFlag";
    public static String QIAN_SHAREDPREFERENCES_USER_USERID = "userId";
    public static String QIAN_SHAREDPREFERENCES_USER_REALNAME = "realName";

    public static String QIAN_SHAREDPREFERENCES_USER_LATESTDATE_XIAOXI = "latestDate_xiaoxi";// 消息公告
    public static String QIAN_SHAREDPREFERENCES_USER_GETSTARTPAGE = "startPageUrl";// 启动页图片

    public static String QIAN_SHAREDPREFERENCES_USER_LATESTDATE = "latestDate";
    public static String QIAN_SHAREDPREFERENCES_USER_ISRED = "isred";// 1 表示没读 0
    // 已读
    public static String QIAN_SHAREDPREFERENCES_USER_JYCOUNT = "jy_count";
    public static String QIAN_SHAREDPREFERENCES_USER_JYTIME = "jy_time";
    public static String QIAN_SHAREDPREFERENCES_USER_USERNAME = "userName";
    public static String QIAN_SHAREDPREFERENCES_USER_PHONE = "phone";
    public static String QIAN_SHAREDPREFERENCES_USER_SHARE = "share";//分享码

    public static String QIAN_REDPACKETAMOUNT = "redPacketAmount";// 红包金额
    public static String QIAN_REDPACKETOPEN = "redPacketOpen";// 红包是否显示
    public static String QIAN_REDPACKETTYPE = "redPacketType";// 红包金额

    // 手势密码
    public static String QIAN_SHAREDPREFERENCES_LOCK = "lock";
    public static String QIAN_SHAREDPREFERENCES_LOCKKEY = "lock_key";
    // error code 警告信息
    public static String ERRORCODE_CHECKNET = "请检查网络连接是否正常";
    public static String ERRORCODE_CHECKFWQ = "服务器连接异常";
    // 公共参数
    public static String URL_QIAN_API_APPID = "appId";
    public static String URL_QIAN_API_SERVICE = "service";
    public static String URL_QIAN_API_SIGN = "sign";
    public static String URL_QIAN_API_SIGNTYPE = "signType";

    // 刷新加载
    public static String QIAN_PERNUM = "pernum";
    public static String QIAN_CURRENTPAGE = "currentPage";
    public static String QIAN_PRODUCTTYPE = "productType";// 1是零钱通 2是钱贝勒

    // 产品列表
    public static String QIAN_PRODUCT_ACCOUNT = "account";//标的总额
    public static String QIAN_PRODUCT_ACCOUNTYES = "accountYes";//已投金额
    public static String QIAN_PRODUCT_ADDTIME = "addtime";//添加时间
    public static String QIAN_PRODUCT_APR = "normalApr";//利率
    public static String QIAN_PRODUCT_BORROWID = "borrowId";//产品ID
    public static String QIAN_PRODUCT_FLOWCOUNT = "flowCount";//份数
    public static String QIAN_PRODUCT_FLOWMONEY = "flowMoney";//每份金额
    public static String QIAN_PRODUCT_FLOWYESCOUNT = "flowYesCount";//已投分数
    public static String QIAN_PRODUCT_FRANCHISEEID = "franchiseeId";//钱行ID
    public static String QIAN_PRODUCT_FRANCHISEENAME = "franchiseeName";//钱行名称
    public static String QIAN_PRODUCT_HASREPAIDPERIOD = "hasRepaidPeriod";//已还期数
    public static String QIAN_PRODUCT_ISEXPERIENCE = "isExperience";//是否体验馆(0不是 1是)
    public static String QIAN_PRODUCT_ISDAY = "isday";//是否是天标(1天标 0月标)
    public static String QIAN_PRODUCT_NAME = "name";//标名
    public static String QIAN_PRODUCT_PRODUCT = "productType";
    public static String QIAN_PRODUCT_STATUS = "status";// 1表示可投  10预售标(倒计时)
    public static String QIAN_PRODUCT_STYLE = "style";//还款方式  1等额本息 2一次性到期还款  3每月还息到期还本
    public static String QIAN_PRODUCT_TIMELIMIT = "timeLimit";//借款期限(月)
    public static String QIAN_PRODUCT_TIMELIMITDAY = "timeLimitDay";//借款期限(天标)
    public static String QIAN_PRODUCT_TOTALPERIOD = "totalPeriod";//
    public static String QIAN_PRODUCT_TYPE = "type";//type 类型（102,103，110,112）其中（102,103借款类，110，112债券类，其中110是按照份数）
    public static String QIAN_PRODUCT_LOWESTACCOUNT = "lowestAccount";//lowestAccount最低投资金额
    public static String QIAN_PRODUCT_INTEREST = "interest";
    public static String QIAN_PRODUCT_TENDERTIMES = "tenderTimes";//已投资人数

    public static String QIAN_PRODUCT_USERID = "userId";
    public static String QIAN_PRODUCT_USERSHOWNAME = "userShowName";
    public static String QIAN_PRODUCT_USETYPE = "usetype";
    public static String QIAN_PRODUCT_PRODUCTSTATUS = "productStatus";// 产品状态：0：预售 ，1：申购，2:已售完，3:还款中,4:已完结
    public static String QIAN_PRODUCT_PRESALETIME = "preSaleTime";
    public static String QIAN_PRODUCT_LASTREPAYTIME = "lastRepayTime";//最后待收时间
    public static String QIAN_PRODUCT_ISNEWHAND = "isNewHand";// 1 为新手专享
    public static String QIAN_PRODUCT_EXTRAAWARDAPR = "extraAwardApr";
    public static String QIAN_PRODUCT_ACT_TITLE = "act_title";//新手投就送红包  投资有奖
    public static String QIAN_PRODUCT_ACT_URL = "act_url";
    public static String QIAN_PRODUCT_BR_TYPE = "brType";//区分 1新手标 2 普通标 3 钱贝勒
    public static String QIAN_PRODUCT_PRE_SALE_TIMEDES = "preSaleTimeDes";//预发布标 时间描述
    public static String QIAN_PRODUCT_IS_ADVANCEREPAY = "isAdvanceRepay";//是否支持提前还款 1 支持  0 不支持

    public static String QIAN_PRODUCT_CITY = "city";
    public static String QIAN_PRODUCT_COMPANY = "company";
    public static String QIAN_PRODUCT_CONTENT = "content";
    public static String QIAN_PRODUCT_DEBTORINFO = "debtorInfo";
    public static String QIAN_PRODUCT_FRANCHISEETIME = "franchiseeTime";
    public static String QIAN_PRODUCT_FUNDUSAGE = "fundUsage";
    public static String QIAN_PRODUCT_LOGO = "logo";
    public static String QIAN_PRODUCT_REPAYMENTSOURCE = "repaymentSource";
    public static String QIAN_PRODUCT_RISKEVALUATION = "riskEvaluation";

    public static String QIAN_PRODUCT_BORROWNAME = "borrowName";
    public static String QIAN_PRODUCT_BORROWSTATUS = "borrowStatus";
    public static String QIAN_PRODUCT_BORROWTYPE = "borrowType";//1债券类 2借款类
    public static String QIAN_PRODUCT_BORROWUSERID = "borrowUserId";
    public static String QIAN_PRODUCT_HASCOLLECTEDPERIOD = "hasCollectedPeriod";
    public static String QIAN_PRODUCT_TENDERID = "tenderId";
    public static String QIAN_PRODUCT_TENDERSTATUS = "tenderStatus";
    public static String QIAN_PRODUCT_TENDERTYPE = "tenderType";
    public static String QIAN_PRODUCT_MONEY = "money";

    //余额明细 数据
    public static String QIAN_BALANCE_RECORD_DATA = "date";//操作时间
    public static String QIAN_BALANCE_RECORD_LATEST_BALANCE = "latestBalance";//余额
    public static String QIAN_BALANCE_RECORD_MONEY = "money";//操作金额
    public static String QIAN_BALANCE_RECORD_TITLE = "title";//名称标题
    public static String QIAN_BALANCE_RECORD_TYPE = "type";//type 1支出 2收入
    //提现记录
    public static String QIAN_CASH_RECORD_ADD_TIME = "addtime";//提现时间
    public static String QIAN_CASH_RECORD_BANK_NAME = "bankName";//银行名称
    public static String QIAN_CASH_RECORD_BANK_NO = "bankNo";//提现卡号
    public static String QIAN_CASH_RECORD_ID = "id";
    public static String QIAN_CASH_RECORD_MONEY = "money";//提现金额
    public static String QIAN_CASH_RECORD_STATUS_DESC = "statusDesc";//提现状态
    public static String QIAN_CASH_RECORD_STATUS = "status";//提现状态  status 1 提现成功  2提现处理中 3提现失败 4 用户提现取消

    // 我的钱内助
    public static String QIAN_MY_ACCUMULATEDINCOME = "accumulatedIncome";
    public static String QIAN_MY_REPAYACCOUNT = "repayAccount";
    public static String QIAN_MY_REPAYTIME = "repayTime";
    public static String QIAN_MY_TENDERACCOUNT = "tenderAccount";
    public static String QIAN_MY_TENDERTIME = "tenderTime";
    public static String QIAN_MY_USERMONEY = "useMoney";
    public static String QIAN_MY_USERNAME = "userName";
    public static String QIAN_MY_PHONE = "phone";
    public static String QIAN_MY_REDPACKETCOUNT = "redPacketCount";
    public static String QIAN_MY_AVAYARURL = "avatarUrl";//头像url

    public static String QIAN_MY_OLDPASSWORD = "oldPassword";
    public static String QIAN_MY_NEWPASSWORD = "newPassword";
    public static String QIAN_MY_CONFIRMPASSWORD = "confirmPassword";

    public static String QIAN_MY_REDPACKETS_OVERDUEFLAG = "overdueFlag";
    public static String QIAN_MY_REDPACKETS_USEFLAG = "useFlag";
    //消息公告
    public static String QIAN_MORE_NOTICE_ADDTIME = "addTime";
    public static String QIAN_MORE_NOTICE_ID = "id";
    public static String QIAN_MORE_NOTICE_NAME = "name";
    public static String QIAN_MORE_NOTICE_CONTENT = "content";

    //账户余额
    public static String QIAN_ACCOUNT_BALANCE_KEY_BALANCE = "balance";
    public static String QIAN_ACCOUNT_BALANCE_KEY_DISPOSECASH = "disposeCash";
    public static String QIAN_ACCOUNT_BALANCE_KEY_CASHFEE = "cashFee";
    public static String QIAN_ACCOUNT_BALANCE_KEY_CURRENMONTHHASCASH = "currentMonthHasCash";
    public static String QIAN_ACCOUNT_BALANCE_KEY_FREECASHCOUNT = "freeCashCount";
    //充值140
    public static String QIAN_RECHARGE_BALANCE_PAYMENTINFO = "paymentInfo";
    public static String QIAN_RECHARGE_BALANCE_NO_ORDER = "no_order";//订单号
    //充值结果查询
    public static String QIAN_RECHARGE_RESULT_STATUS = "status";
    //申购
    public static String QIAN_TENDER_1XV4_ORDER_NO = "orderNo";
    //投资准备金申购结果轮询
    public static String QIAN_TENDER_RESULT_STATUS = "status";

    //获取用户状态信息
    public static String QIAN_USER_STATUS_INFO_REAL_NAME = "realNameStatus";//是否实名认证 0未实名  1实名
    public static String QIAN_USER_STATUS_INFO_PAY_PWD = "payPwdStatus";//是否设置交易密码 1设置了 0未设置
    public static String QIAN_USER_STATUS_INFO_PAY_PWD_STATUS_YES = "1";//设置过交易密码
    public static String QIAN_USER_STATUS_INFO_BANK_CARD = "bankCardStatus";//是否绑定银行卡
    public static String QIAN_USER_STATUS_INFO_NEED_POP = "needPopStatus";//是否弹出单卡设置页 1需要 0不需要
    public static String QIAN_USER_STATUS_INFO_NEWHAND_STATUS = "newHandStatus";//是否是新手（有没有投资过）1是新手 0为非新手

    public static String QIAN_LOGIN_PHONEEXIST = "phoneExist";
    public static String QIAN_LOGIN_CARDEXIST = "cardExist";

    //余额提现页 数据获取
    public static String QIAN_BALANCE_WITHDROW_SHOW_KEY_BALANCE = "balance";//余额
    public static String QIAN_BALANCE_WITHDROW_SHOW_KEY_TOK = "tok";//余额
    public static String QIAN_BALANCE_WITHDROW_SHOW_KEY_IS_NEED_POPSETCARD = "isNeedPopSetCard";//是否需要设置单卡
    public static String QIAN_BALANCE_WITHDROW_SHOW_KEY_BANKCARD = "bankcard";//银行卡
    public static String QIAN_BALANCE_WITHDROW_SHOW_KEY_BANK_SHORT_NAME = "bankShortName";//银行卡简称
    public static String QIAN_BALANCE_WITHDROW_SHOW_KEY_HIDDEN_CARD_NO = "hiddenCardNo";//银行卡后四位
    public static String QIAN_BALANCE_WITHDROW_SHOW_KEY_ID = "id";//银行卡id
    public static String QIAN_BALANCE_WITHDROW_SHOW_IS_NEED = "isneed";//是否需要完善银行卡信息 1需要0不需要

    //余额提现页 提交
    public static String QIAN_BALANCE_WITHDROW_COMMIT_STATUS = "status";//提现结果 1 成功，0失败

    // 我的钱内助
    public static String QIAN_MY_COLLECTION = "collection";// 当前持有资产
    public static String QIAN_MY_EARNEDTHISMOUTH = "earnedThisMonth";// 本月收益
    public static String QIAN_MY_TENDER_YES_INTEREST = "tenderYesInterest";//定期理财昨日收益
    public static String QIAN_MY_INVESTING_WAIT_INTEREST = "investingWaitInterest";//定期理财待到帐收益
    public static String QIAN_MY_INVESTING_CAPITAL = "investingCapital";//定期理财的在投资金

    public static String QIAN_MY_ACTIVITY = "activityPic";
    public static String QIAN_MY_ACTIVITY_LOCATION_URL = "locationUrl";
    public static String QIAN_MY_ACTIVITY_URL = "url";

    public static String QIAN_MY_BALANCE = "balance";
    public static String QIAN_MY_INVESTINGWAITINTEREST = "investingWaitInterest";
    public static String QIAN_MY_INVESTINGCAPITAL = "investingCapital";

    // 发布公告给手机客户端
    public static String QIAN_MY_NOTICEID = "noticeid";
    public static String QIAN_MY_CONTENT = "content";
    public static String qIAN_MY_SHOWADDR = "showaddr";
    public static String QIAN_MY_TITLE = "title";
    public static String QIAN_MY_TYPE = "type";
    public static String QIAN_MY_URL = "url";
    // 投资中资产
    public static String QIAN_USEMONEY_BORROWID = "borrowId";
    public static String QIAN_USEMONEY_BORROWNAME = "borrowName";
    public static String QIAN_USEMONEY_INTEREST = "interest";
    public static String QIAN_USEMONEY_TENDERID = "tenderId";
    public static String QIAN_USEMONEY_BACKPLACE = "backPlace";

    // 投资概况
    public static String QIAN_USEMONEY_ACCOUNT = "account";
    public static String QIAN_USEMONEY_ADDTIME = "addtime";
    public static String QIAN_USEMONEY_APR = "apr";
    public static String QIAN_USEMONEY_BORROWSTATUS = "borrowStatus";
    public static String QIAN_USEMONEY_REPAYMENTTIME = "repaymentTime";
    public static String QIAN_USEMONEY_ISBIN = "isbin";
    public static String QIAN_USEMONEY_ISNEED = "isneed";
    public static String QIAN_USEMONEY_BANKNAME = "bankName";
    public static String QIAN_USEMONEY_HIDDENCARDNO = "hiddenCardNo";
    public static String QIAN_USEMONEY_EXTRAAWARD = "extraAward";  //额外利息收益()
    public static String QIAN_USEMONEY_DEFAULTBANKNAME = "defaultBankName";
    public static String QIAN_USEMONEY_DEFAULTBANKCARD = "defaultBankCard";

    // 已回款 待回款
    public static String QIAN_MY_REPAY_BORROWID = "borrowId";
    public static String QIAN_MY_REPAY_BORROWNAME = "borrowName";
    public static String QIAN_MY_REPAY_CAPTIAL = "captial";
    public static String QIAN_MY_REPAY_INTEREST = "interest";
    public static String QIAN_MY_REPAY_REPAYMENTIME = "repaymenTime";
    public static String QIAN_MY_REPAY_REPAYMENTACCOUNT = "repaymentAccount";
    public static String QIAN_MY_REPAY_TENDERID = "tenderId";
    public static String QIAN_MY_REPAY_TENDERTIME = "tenderTime";
    public static String QIAN_MY_REPAY_ISBIN = "isbin";
    public static String QIAN_MY_REPAY_BANKNAME = "bankName";
    public static String QIAN_MY_REPAY_HIDDENCARDNO = "hiddenCardNo";
    public static String QIAN_MY_REPAY_BACKPLACE = "backPlace";

    // 我的银行卡
    public static String QIAN_MY_BANK_ADDTIME = "addTime";
    public static String QIAN_MY_BANK_BANKCODE = "bankCode";
    public static String QIAN_MY_BANK_BANKSHORTNAME = "bankShortName";
    public static String QIAN_MY_BANK_HIDDENCARDNO = "hiddenCardNo";//尾号
    public static String QIAN_MY_BANK_ID = "id";
    public static String QIAN_MY_BANK_PERDAYLIMIT = "perDayLimit";
    public static String QIAN_MY_BANK_PERDEALLIMIT = "perDealLimit";
    public static String QIAN_MY_BANK_STATUS = "status";
    public static String QIAN_MY_BANK_UNIQNO = "uniqNo";

    public static String QIAN_MY_BANK_ISDEFAULT = "isDefault";

    // 所有的银行卡
    public static String QIAN_ALL_BANK_BANKCODE = "bankCode";
    public static String QIAN_ALL_BANK_BANKNAME = "bankName";
    public static String QIAN_ALL_BANK_BANKSHORTNAME = "bankShortName";
    public static String QIAN_ALL_BANK_CHANNELCODE = "channelCode";
    public static String QIAN_ALL_BANK_ID = "id";
    public static String QIAN_ALL_BANK_LOGOCSS = "logoCss";
    public static String QIAN_ALL_BANK_PERDAYLIMIT = "perDayLimit";
    public static String QIAN_ALL_BANK_PERDEALLIMIT = "perDealLimit";
    public static String QIAN_ALL_BANK_STATUS = "status";

    // 银行卡
    public static String QIAN_BANK_ID = "id";
    public static String QIAN_BANK_ADDTIME = "addTime";
    public static String QIAN_BANK_BANKCODE = "bankCode";
    public static String QIAN_BANK_BANKSHORTNAME = "bankShortName";
    public static String QIAN_BANK_HIDDENCARDNO = "hiddenCardNo";
    public static String QIAN_BANK_PERDAYLIMIT = "perDayLimit";
    public static String QIAN_BANK_PERDEALLIMIT = "perDealLimit";
    public static String QIAN_BANK_STATUS = "status";
    public static String QIAN_BANK_UNIQNO = "uniqNo";
    public static String QIAN_BANK_REAL_USER_NAEM = "realName";
    public static String QIAN_BANK_IS_NEED_ADD_INFORMATION = "isneed";//是否需要完善银行卡信息1 需要，0 不需要

    public static String QIAN_REQUEAT_USEMONEYDETAIL = "useMoneyDetial";
    public static String QIAN_REQUEAT_PUBLISHUSERNOTICE = "publishUserNotice";
    public static String QIAN_REQUEAT_MY_RETURNRECORD = "myReturnRecord";
    public static String QIAN_REQUEAT_REALINFOBACK = "realInfoBack";
    public static String QIAN_REQUEAT_ADDBANKCARD = "addBankCard";

    public static String QIAN_REQUEAT_SETDEFAULTCARD = "setDefaultCard";
    public static String QIAN_REQUEAT_CHANGTENDERSTATUS = "changTenderStatus";

    /* 启动页图片 */
    public static String QIAN_REQUEAT_GETSTARTPAGE = "getStartPage";
    /* 一分钱邦卡 */
    public static String QIAN_REQUEAT_ONEMONEYBINCARD = "oneMoneyBinCard";

    // 活动中心
    public static String QIAN_MORE_ACTIVE_ADDTIME = "addTime";
    public static String QIAN_MORE_ACTIVE_COLOR = "color";
    public static String QIAN_MORE_ACTIVE_FILENAME = "fileName";
    public static String QIAN_MORE_ACTIVE_FILETYPENAME = "fileTypeName";
    public static String QIAN_MORE_ACTIVE_FRANCHISEEID = "franchiseeId";
    public static String QIAN_MORE_ACTIVE_ID = "id";
    public static String QIAN_MORE_ACTIVE_INTRO = "intro";
    public static String QIAN_MORE_ACTIVE_LOCATIONURL = "locationUrl";
    public static String QIAN_MORE_ACTIVE_URL = "url";
    public static String QIAN_REQUEAT_MOREACTIVITY = "activity";
    public static String QIAN_REQUEAT_ENDFLG = "endFlg";//活动是否结束标记
    public static String DUCE = "duce";


    public static String getChannelCode(Context context) {
        String code = getMetaData(context, "UMENG_CHANNEL");
        if (code != null) {
            return code;
        }
        return "qian360";
    }

    private static String getMetaData(Context context, String key) {
        try {
            ApplicationInfo ai = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            Object value = ai.metaData.get(key);
            if (value != null) {
                return value.toString();
            }
        } catch (Exception e) {

        }
        return null;
    }

    /**
     * 版本信息
     */
    public static String systemType = "24";

    public static String getVersionName(Activity activity) {
        String version = "";
        try {
            // 获取packagemanager的实例
            PackageManager packageManager = activity.getPackageManager();
            // getPackageName()是你当前类的包名，0代表是获取版本信息
            PackageInfo packInfo = packageManager.getPackageInfo(activity.getPackageName(), 0);
            version = "" + packInfo.versionName;
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return version;
    }

    /* 悬浮窗 */
    public static String URL_CLICKACTIVITY = URL_QIAN + "/api/clickActivity.html";
    /* 登陆 */
    public static String URL_QIAN_LOGIN = URL_QIAN + "/api/user/login.html";
    /* 注册-手机验证码regPhoneCode */
    public static String URL_QIAN_REG_VERIFY = URL_QIAN + "/api/user/regPhoneCode.html";
    /* 手机密保找回 重置密码 */
    public static String URL_QIAN_RETRIEVE_RESETING = URL_QIAN + "/api/user/resetLoginPassword.html";
    /* 产品列表 */
    public static String URL_QIAN_PRODUCT_LIST = URL_QIAN + "/api/trade/productList1xV4.html";
    /* 产品详情 */
    public static String URL_QIAN_GET_PRODUCT_CONTENT = URL_QIAN + "/api/trade/getProductDetail.html";
    /* 产品详情web页的url */
    public static String URL_QIAN_PRODUCT_WEB_VIEW = URL_QIAN + "/api/trade/getProductDetail2.html";
    /* 项目投资记录 */
    public static String URL_QIAN_PRODUCT_MORE_JL = URL_QIAN + "/api/trade/tenderRecord.html";
    /* 我的钱内助 */
    public static String URL_QIAN_MY = URL_QIAN + "/api/member/myAccount.html";
    /* 找回登录密码界面 获取验证码 */
    public static String URL_QIAN_RETURN_VERIFY = URL_QIAN + "/api/user/resetPwdPhoneCode.html";
    /* 修改登录密码 */
    public static String URL_QIAN_MY_LOGINPASSWORD = URL_QIAN + "/api/member/modifyLoginPwd.html";
    /* 修改交易密码 */
    public static String URL_QIAN_MY_JYPASSWORD = URL_QIAN + "/api/member/modifyPayPwd.html";
    /* 项目详情（传给网页端） */
    public static String URL_QIAN_PROJECTDETAIL = URL_QIAN + "/api/trade/projectDetail.html";
    /* 账户管理 -刷新安全中心 */
    public static String URL_QIAN_SALE = URL_QIAN + "/api/member/getLatestStatus.html";
    /* 意见反馈 */
    public static String URL_QIAN_FEEDBACK = URL_QIAN + "/api/user/feedback.html";
    /* 账户余额 */
    public static String URL_QIAN_ACCOUNT_BALANCE = URL_QIAN + "/api/member/accountBalance.html";
    /* 余额明细 */
    public static String URL_QIAN_BALANCE_RECORD = URL_QIAN + "/api/member/balanceRecord.html";
    /* 提现记录 */
    public static String URL_QIAN_CASH_RECORD = URL_QIAN + "/api/member/withdrawRecord.html";
    /* 充值 */
    public static String URL_QIAN_RECHARGE_BALANCE = URL_QIAN + "/api/account/rechargeBalance.html";
    /* 充值结果查询 */
    public static String URL_QIAN_RECHARGE_RESULT = URL_QIAN + "/api/account/rechargeResult.html";
    /* 申购 */
    public static String URL_QIAN_TENDER_1XV4 = URL_QIAN + "/api/trade/tender1xV4.html";
    /* 投资准备金申购结果轮询 */
    public static String URL_QIAN_TENDER_RESULT_1XV4 = URL_QIAN + "/api/trade/tenderResult1xV4.html";
    /* 获取用户状态信息 */
    public static String URL_QIAN_USER_STATUS_INFORMATION = URL_QIAN + "/api/member/getUserInfoStatus.html";
    /* 余额提现页 数据获取 */
    public static String URL_QIAN_BALANCE_WITHDROW_SHOW = URL_QIAN + "/api/account/goWithdraw.html";
    /* 余额提现页 提交 */
    public static String URL_QIAN_BALANCE_WITHDROW_COMMIT = URL_QIAN + "/api/account/withdrawBalance.html";
    /* 设定单张银行卡 */
    public static String URL_QIAN_SETTING_ONE_BANK_CARD = URL_QIAN + "/api/member/setSingleBankCard.html";
    /* 设置交易密码新 */
    public static String URL_QIAN_SETTING_PAY_PASSWORD = URL_QIAN + "/api/member/certification1xV4.html";
    /* 我是新手 */
    public static String URL_QIAN_NEWS = URL_QIAN + "/forward.html?returnUrl=aboutus";
    /* 提前还款介绍页 */
    public static String URL_QIAN_REPAYMENT_EXPLAIN = URL_QIAN + "/api/trade/repaymentExplain.html";
    /* 忘记交易密码的验证码 */
    public static String URL_QIAN_RETURN_FORGETVERIFY = URL_QIAN + "/api/member/findPayPwdPhoneCode.html";
    /* 刷新token */
    public static String URL_QIAN_REFRESHTOKEN = URL_QIAN + "/api/user/refreshToken.html";
    /* 公告列表 */
    public static String URL_QIAN_NOTICE = URL_QIAN + "/api/user/notice.html";
    /* 公告详情 */
    public static String URL_QIAN_NOTICEDETAIL = URL_QIAN + "/api/user/noticeDetail.html";
    /* 推广 */
    public static String URL_QIAN_PROMOTIONSTAT = URL_QIAN + "/api/user/promotionStat.html";
    /* 我的加息劵 h5页面*/
    public static String URL_QIAN_MY_H5_JIAXI = URL_QIAN + "/api/member/couponH5.html";
    /* 我的红包 h5页面*/
    public static String URL_QIAN_MY_H5_READPACKAGE = URL_QIAN + "/api/member/redPacketH5.html";
    /* 我的奖励 */
    public static String URL_QIAN_MY_AWARDRECORD = URL_QIAN + "/api/member/awardRecord.html";
    /* 首页 */
    public static String URL_QIAN_HOMEPAGEGAI = URL_QIAN + "/api/home1xV4.html";
    /* 登录-手机存在校验 */
    public static String URL_QIAN_PHONECHECK = URL_QIAN + "/api/user/phoneCheck.html";
    /* 找回登录密码校验 */
    public static String URL_QIAN_CHECKRESETLOGIN = URL_QIAN + "/api/user/checkResetLogin.html";

    /* 发布公告给手机客户端-（针对已登录用户） */
    public static String URL_QIAN_PUBLISHUSERNOTICE = URL_QIAN + "/api/common/publishUserNotice.html";

    /* 用户点击改变为已读状态-（针对已登录用户） */
    public static String URL_QIAN_UPDATEUSERNOTICEREAD = URL_QIAN + "/api/common/updateUserNoticeRead.html";
    /* 投资概况 */
    public static String URL_QIAN_MYTENDERDETIAL = URL_QIAN + "/api/member/myTenderRecordDetail.html";
    /* 我的红包 */
    public static String URL_QIAN_MY_REDPACKETSGAI = URL_QIAN + "/api/member/redPacket.html";
    /* 我的奖励 */
    public static String URL_QIAN_MY_AWARD = URL_QIAN + "/api/member/award.html";
    /* 申购-可使用红包 和银行卡 */
    public static String URL_QIAN_GETPAYINFO = URL_QIAN + "/api/trade/getPayInfo.html";
    /* 账户管理-添加银行卡addBank */
    public static String URL_QIAN_REALINFOBACK = URL_QIAN + "/api/account/realInfoBack.html";
    /* 注册 */
    public static String URL_QIAN_NEWREG = URL_QIAN + "/api/user/newRegister.html";
    /* 账申购前添加银行卡 */
    public static String URL_QIAN_ADDBANKCARD = URL_QIAN + "/api/account/addBankCard.html";
    /* 账申取消订 */
    public static String URL_QIAN_CHANGTENDERSTATUS = URL_QIAN + "//api/trade/changTenderStatus.html";
    /* 账户管理-我的银行卡myBank */
    public static String URL_QIAN_MYBANKCARD = URL_QIAN + "/api/account/myBankCard.html";
    /* 我的投资记录 */
    public static String URL_QIAN_MY_GETHISTORYTENDER = URL_QIAN + "/api/member/getHistoryTender.html";
    /* 一分钱绑卡 */
    public static String URL_QIAN_ONEMONEYBINCARD = URL_QIAN + "/api/account/oneMoneyBinCard.html";
    /* 活动中心 */
    public static String URL_QIAN_MORE_ACTIVITY = URL_QIAN + "/api/common/activity.html";
    /* 账户管理-提现删除银行卡 */
    public static String URL_QIAN_DEFAULTBANK = URL_QIAN + "/api/account/setDefaultCard.html";
    /* 回款记录-已回款和待回款 */
    public static String URL_QIAN_MY_RETURNRECORD2 = URL_QIAN + "/api/member/returnRecord2.html";
    /* 找回交易密码 */
    public static String URL_QIAN_FINDPAYPWD = URL_QIAN + "/api/member/findPayPwd.html";
    /* 获取启动页 */
    public static String URL_QIAN_GETSTARTPAGE = URL_QIAN + "/api/common/getStartPage.html";
    /* 协议查看 */
    public static String URL_QIAN_PROTOCLFORHTML = URL_QIAN + "/api/common/protoclforHtml.html";
    /* 完善银行卡信息 */
    public static String URL_QIAN_EDITBANKINFO = URL_QIAN + "/api/common/editBankInfo.html";
    /* 我的优惠使用规则 */
    public static String URL_QIAN_FORWARD = URL_QIAN + "/forward.html?returnUrl=redpacketHelpCenter";

    /* 上传头像 */
    public static String URL_QIAN_AVAYARURL = URL_QIAN + "/api/member/uploadAvatar.html";
    /* 收货地址 */
    public static String URL_QIAN_ADDRESS = URL_QIAN + "/api/user/receiptAddress.html";
    /* 充值记录 */
    public static String URL_QIAN_RECHARGERECORD = URL_QIAN + "/api/member/rechargeRecord.html";
    /* 回款日 */
    public static String URL_QIAN_PAYMENTDATE = URL_QIAN + "/api/member/returnRecordTime.html";
    /* app更新 */
    public static String URL_QIAN_UPDATA = URL_QIAN + "/api/version.html";
    /* 理财产品顶部图片下载地址 */

    /**
     * 1.5.0新增
     */
    public static String URL_QIAN_DOWNLOADIMAGE = URL_QIAN + "data/activity/hot.png";
    /*自动投标 */
    public static String URL_QIAN_AUTOINTEREST = URL_QIAN + "/api/common/autoInvest.html";
    /*自动投标 */
    public static String URL_QIAN_AUTOINTERESTIMAGE = URL_QIAN + "/data/activity/auto.png";
    /* 首页banner 邀请好友 */
    public static String URL_QIAN_BANNER_YAOQING="http://www.qianneizhu.com/invite/inviteIndex.html?share";

    /* 自动投资使用规则 */
    public static String URL_QIAN_AUTOFORWARD = URL_QIAN + "/forward.html?returnUrl=autoRule";

    /* 额外奖励图片 */
    public static String URL_FIRST_NO ="https://www.qianneizhu.com/data/images/borrow/sthb1.png";//未领取
    public static String URL_FIRST_YES ="https://www.qianneizhu.com/data/images/borrow/sthb0.png";//已领取
    public static String URL_MAX_YES = "https://www.qianneizhu.com/data/images/borrow/zgehb0.png";
    public static String URL_MAX_NO = "https://www.qianneizhu.com/data/images/borrow/zgehb1.png";
    public static String URL_FINALLY_YES = "https://www.qianneizhu.com/data/images/borrow/wthb0.png";
    public static String URL_FINALLY_NO = "https://www.qianneizhu.com/data/images/borrow/wthb1.png";
    /**
     * 1.5.1
     */
    public static String URL_WLJD =URL_QIAN + "/forward.html?returnUrl=tip1";
    //借款协议
    public static String URL_ZJLY =URL_QIAN + "/forward.html?returnUrl=tip2";
    public static  String URL_SEND_SCORE=URL_QIAN+"/api/member/riskResult.html";
    /**
     * 1.6.0
     */
     /* 首页,安全保障*/
    public static String URL_QIAN_HOME_SECURITY=URL_QIAN+"/api/common/securityDetail.html";
    /* 首页,运营数据*/
    public static String URL_QIAN_HOME_DATA=URL_QIAN+"/activity.html?returnUrl=app-augreport";
    /* 首页,新手福利*/
    public static String URL_QIAN_HOME_NEW=URL_QIAN+"/activity/monthActivity/newhand.html?returnUrl=Mo";
    /* 售罄产品统计*/
    public static String URL_QIAN_SHOUQIN=URL_QIAN+"/api/trade/countSoleProduct1xV6.html";
    /* 售罄产品统计*/
    public static String URL_QIAN_MYTENDERDOING=URL_QIAN+"/api/member/myTenderDoing.html";
}
