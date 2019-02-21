package com.rd.qnz.tools;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.rd.qnz.custom.MineShow;
import com.rd.qnz.custom.MyApplication;
import com.rd.qnz.login.Login;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.regex.Pattern;

public class Check {
    public static String NORMAL_ERROR_RETURN_MESSAGE = "网络繁忙请稍后";

    public static String checkReturn(String errorCode) {
        if (("ROLE_NOT_START").equals(errorCode)) {
            return "签约账号对应角色的服务未启用";
        }
        if (("ROLE_SERVICE_NOT_START").equals(errorCode)) {
            return "签约账号对应角色未启用";
        }
        if (("TRADE_TENDER_HAS_FINISHED").equals(errorCode)) {
            return " 产品已被抢光,下次下手要快哦";
        }
        if (("TRADE_TENDER_MORETHAN_REMAIN").equals(errorCode)) {
            return "产品不足，钱已进入您余额";
        }
        if (("TRADE_TENDER_REDPACKET_IN_USE").equals(errorCode)) {
            return "此红包正在使用";
        }
        if (("FEEDBACK_BLANK").equals(errorCode)) {
            return "反馈不能为空";
        }
        if (("USER_LOGIN_TYPE_ERROR").equals(errorCode)) {
            return "请您填写正确的手机号码";
        }
        if (("USER_FINDPASS_CARD_ERROR").equals(errorCode)) {
            return "身份验证不正确";
        }
        if (("USER_PHONE_NOT_EXIST").equals(errorCode)) {
            return "手机号码不存在";
        }
        if (("USER_ONLINE_INFO_ERROR").equals(errorCode)) {
            return "用户未登陆";
        }
        if (("NOTFIND_USER_IN_TRADEORDER").equals(errorCode)) {
            return "查无该用户";
        }
        if (("TRADE_TENDER_MONEY_INVALID").equals(errorCode)) {
            return "无效金额";
        }
        if (("ORDER_NO_NOT_NULL").equals(errorCode)) {
            return "订单号不能为空";
        }
        if (("ORDER_NO_IS_INVALID").equals(errorCode)) {
            return "无效订单号";
        }
        if (("TRADE_ORDER_NOT_EXISTS").equals(errorCode)) {
            return "无此订单号";
        }
        if (("TRADE_ORDER_NOTHAVE_AUTH").equals(errorCode)) {
            return "对不起,您无权操作该笔订单";
        }
        if (("TRADE_ORDER_HAS_EXISTS").equals(errorCode)) {
            return "订单号已经存在";
        }
        if (("TRADE_TENDER_FAILURE").equals(errorCode)) {
            return "申购失败";
        }
        if (("APP_VERSION_BEHIND_CURRENT").equals(errorCode)) {
            return "您当前版本过低，请更新到最新版本";
        }
        if (("SERVER_VERSION_OVER_CURRENT").equals(errorCode)) {
            return "当前服务端版本还未最新，请关注我们的更新";
        }
        if (("TRADE_TENDER_MORETHAN_PER_DEAL_LIMIT").equals(errorCode)) {
            return "申购金额超过单笔限制最大金额";
        }
        if (("TRADE_TENDER_OVER_UNHANDLE_LIMIT").equals(errorCode)) {
            return "您有多笔申购业务正在处理，请五分钟后重试";
        }
        if (("USER_NAME_DIF_CARD").equals(errorCode)) {
            return "查询的身份证号码与姓名不对应";
        }
        if (("USER_NAME_CARD_NOTEXISTS").equals(errorCode)) {
            return "库中无此号，请到户籍所在地进行核实！";
        }
        if (("USER_IDENTIFY_REAlNAME_AUTH_UNPASS").equals(errorCode)) {
            return "您尚未通过实名认证";
        }
        if (("CARD_ID_FORMAT_ERROR").equals(errorCode)) {
            return "身份证号码格式不正确";
        }
        if (("REAL_NAME_FORMAT_ERROR").equals(errorCode)) {
            return "姓名格式不正确";
        }

        if (("BANK_FIND_LIST_FAILURE").equals(errorCode)) {
            return "加载银行卡列表失败";
        }
        if (("ACCOUNT_BANK_VALID_DEPOSITCARD").equals(errorCode)) {
            return "请使用储蓄卡";
        }
        if (("BANK_BIN_TENDER_FAILURE").equals(errorCode)) {
            return "回款绑定操作失败";
        }
        if (("BANK_CARD_HAS_USE").equals(errorCode)) {
            return "对不起，该卡已被使用";
        }
        if (("TRADE_TENDER_REDPACKET_OVER").equals(errorCode)) {
            return "红包额度已超过每次投资使用限制";
        }
        if (("TRADE_TENDER_NO_BANK_CARD").equals(errorCode)) {
            return "请选择银行卡";
        }
        if (("TRADE_TENDER_BANK_CARD_NOT_BELONGTO_USER").equals(errorCode)) {
            return "该银行卡已被使用，不属于您";
        }
        if (("TRADE_TENDER_BANK_CARD_NOT_EXIST").equals(errorCode)) {
            return "查无该银行卡";
        }
        if (("SET_DEFAULT_BANKCARD_FAILURE").equals(errorCode)) {
            return "设置默认银行卡失败！";
        }
        if (("REALNAME_TIMES_OVER_LIMIT").equals(errorCode)) {
            return "您的实名次数已达到上限，请联系客服进行实名！";
        }
        if (("BANK_CARD_HAS_CHECK").equals(errorCode)) {
            return "该卡已通过支付验证，请进入个人中心进行默认设置";
        }

        if (("TRADE_TENDER_OVER_UNHANDLE_LIMIT").equals(errorCode)) {
            return "您有多笔申购业务正在处理，请五分钟后重试";
        }
        if (("TRADE_TENDER_MORETHAN_PER_DEAL_LIMIT").equals(errorCode)) {
            return "申购金额超过单笔限制最大金额";
        }
        if (("BANK_CARD_HAS_CHECK").equals(errorCode)) {
            return "该卡已通过支付验证，请进入个人中心进行默认设置";
        }
        if (("APP_VERSION_BEHIND_CURRENT").equals(errorCode)) {
            return "您当前版本过低，请更新到最新版本";
        }
        if (("SERVER_VERSION_OVER_CURRENT").equals(errorCode)) {
            return "当前服务端版本还未最新，请关注我们的更新";
        }
        if (("USER_FINDPASS_CARD_ERROR").equals(errorCode)) {
            return "身份验证不正确";
        }
        if (("ACCOUNT_BANK_CHECK_FAILURE").equals(errorCode)) {
            return "银行卡校验未通过";
        }
        if (("ACCOUNT_BANK_IS_BIND").equals(errorCode)) {
            return "银行卡已签约";
        }
        if (("ACCOUNT_BANK_PACTAPPLY_FAILURE").equals(errorCode)) {
            return "银行卡申请签约失败";
        }
        if (("ACCOUNT_BANK_PACTSIGN_FAILURE").equals(errorCode)) {
            return "银行卡签约认证失败";
        }
        if (("BANK_FIND_LIST_FAILURE").equals(errorCode)) {
            return "加载银行卡列表失败";
        }
        if (("ACCOUNT_BANK_VALID_DEPOSITCARD").equals(errorCode)) {
            return "请使用储蓄卡";
        }
        if (("BANK_BIN_TENDER_FAILURE").equals(errorCode)) {
            return "回款绑定操作失败";
        }
        if (("BANK_CARD_HAS_USE").equals(errorCode)) {
            return "对不起，该卡已被使用";
        }
        if (("TRADE_TENDER_REDPACKET_OVER").equals(errorCode)) {
            return "红包额度已超过每次投资使用限制！";
        }
        if (("TRADE_TENDER_NO_BANK_CARD").equals(errorCode)) {
            return "请选择银行卡";
        }
        if (("TRADE_TENDER_BANK_CARD_NOT_BELONGTO_USER").equals(errorCode)) {
            return "该银行卡已被使用，不属于您";
        }
        if (("TRADE_TENDER_BANK_CARD_NOT_EXIST").equals(errorCode)) {
            return "查无该银行卡";
        }

        if (("SIGN_CHECK_FAILURE").equals(errorCode)) {
            return "签名校验失败";
        }
        if (("USER_LOGIN_TYPE_ERROR").equals(errorCode)) {
            return "手机号码错误";
        }
        if (("USER_IDENTIFY_REAlNAME_AUTH_UNPASS").equals(errorCode)) {
            return "未通过实名认证";
        }
        if (("USER_REGISTER_PHONE_FORMAT_ERROR").equals(errorCode)) {
            return "手机格式不对";
        }
        if (("TRADE_TENDER_NOT_NEW_HAND").equals(errorCode)) {
            return "亲，你已经不是新手了哦，还抢啊";
        }
        if (("ACCOUNT_BANK_IS_BIND").equals(errorCode)) {
            return "银行卡已签约";
        }
        if (("ACCOUNT_BANK_PACTAPPLY_FAILURE").equals(errorCode)) {
            return "申请签约错误";
        }
        if (("ACCOUNT_BANK_PACTSIGN_FAILURE").equals(errorCode)) {
            return "签约认证失败";
        }
        if (("ACCOUNT_BANK_INVALID").equals(errorCode)) {
            return "无效银行卡";
        }
        if (("ACCOUNT_BANK_BANDING_FAILURE").equals(errorCode)) {
            return "调用易极付解绑银行卡失败";
        }
        if (("ACCOUNT_BANK_VALID_CARDID").equals(errorCode)) {
            return "请输入有效银行卡号";
        }
        if (("ACCOUNT_BANK_IS_BINDING").equals(errorCode)) {
            return "银行卡已被绑定，请使用其它银行卡";
        }
        if (("ACCOUNT_BANK_NOT_SUPPORT").equals(errorCode)) {
            return "尚不支持该银行";
        }

        if (("ACCOUNT_BANK_SELECT_AREA").equals(errorCode)) {
            return "请选择银行卡办卡地省市信息";
        }
        if (("ACCOUNT_BANK_ADD_FAILURE").equals(errorCode)) {
            return "添加银行卡失败";
        }
        if (("ACCOUNT_BANK_PASSWORD_WRONG").equals(errorCode)) {
            return "登录密码不正确";
        }
        if (("DEDUCT_RECHARGE_LOCAL_FAILURE").equals(errorCode)) {
            return "代扣充值本地处理失败";
        }
        if (("DEDUCT_RECHARGE_BANKID_BLANK").equals(errorCode)) {
            return "请选择代扣银行卡";
        }
        if (("DEDUCT_RECHARGE_BANKID_NOT_EXIST").equals(errorCode)) {
            return "银行卡不存在，请联系客服";
        }
        if (("DEDUCT_RECHARGE_MONEY_NOT_ZERO").equals(errorCode)) {
            return "充值金额不能为零";
        }
        if (("DEDUCT_RECHARGE_EMALL_AUTH_FIRST").equals(errorCode)) {
            return "尊敬的用户，为了保障您的账户安全，请先通过邮箱认证";
        }
        if (("DEDUCT_RECHARGE_PHONE_AUTH_FIRST").equals(errorCode)) {
            return "尊敬的用户，为了保障您的账户安全，请先通过手机认证！";
        }

        if (("DEDUCT_RECHARGE_REAL_AUTH_FIRST").equals(errorCode)) {
            return "尊敬的用户，为了保障您的账户安全，请先通过实名认证";
        }

        if (("DEDUCT_RECHARGE_MONEY_HIGHEST").equals(errorCode)) {
            return "充值金额不能超过一千万";
        }

        if (("WITHDRAW_BANK_CARD_BLANK").equals(errorCode)) {
            return "请选择一张提现银行卡";
        }

        if (("WITHDRAW_MONEY_LESS_THAN_100").equals(errorCode)) {
            return "提现金额不能小于100元";
        }

        if (("WITHDRAW_USEMONEY_NOT_ENOUGH").equals(errorCode)) {
            return "可用金额不足";
        }

        if (("WITHDRAW_MONEY_MORETHAN_USEMONEY").equals(errorCode)) {
            return "输入的提现金额大于可提现总额，请核对后再提现";
        }

        if (("WITHDRAW_FAILURE").equals(errorCode)) {
            return "提现失败，请联系客服";
        }

        if (("DEDUCT_RECHARGE_BANKID_OVERLIMIT").equals(errorCode)) {
            return "已超过单笔充值额度";
        }
        if (("DEDUCT_RECHARGE_BANKID_NO_SUPPORT").equals(errorCode)) {
            return "此银行不支持代扣充值";
        }
        if (("DEDUCT_RECHARGE_FAILURE").equals(errorCode)) {
            return "代扣充值处理失败";
        }
        if (("DEDUCT_RECHARGE_PROCESSING").equals(errorCode)) {
            return "代扣充值处理中";
        }
        if (("ACCOUNT_BANK_CHECK_FAILURE").equals(errorCode)) {
            return "银行卡校验未通过";
        }

        if (("USER_FINDPASS_USERNAMEORVALIDCODE_WRONG").equals(errorCode)) {
            return "登录名不能为空";
        }
        if (("USER_FINDPASS_USER_NOT_EXIST").equals(errorCode)) {
            return "用户不存在";
        }

        if (("USER_FINDPASS_EMAIL_NOT_AUTH").equals(errorCode)) {
            return "请先通过邮箱认证";
        }
        if (("USER_FINDPASS_PHONE_NOT_AUTH").equals(errorCode)) {
            return "请先通过手机认证";
        }
        if (("USER_FINDPASS_USERNAME_TYPE_WRONG").equals(errorCode)) {
            return "登录名类型错误";
        }
        if (("USER_FINDPASS_VALIDCODE_NOT_BLANK").equals(errorCode)) {
            return "验证码不能为空";
        }
        if (("USER_FINDPASS_VALIDCODE_WRONG").equals(errorCode)) {
            return "验证码不正确";
        }
        if (("USER_FINDPASS_QUESTION_INVALID").equals(errorCode)) {
            return "密保问题已失效，请联系管理员";
        }

        if (("USER_FINDPASS_ANSWER_NOT_BLANK").equals(errorCode)) {
            return "答案不能为空";
        }
        if (("USER_FINDPASS_ANSWER_WRONG").equals(errorCode)) {
            return "密保答案不正确";
        }
        if (("USER_FINDPASS_EMAIL_BLANK").equals(errorCode)) {
            return "邮箱不能为空";
        }

        if (("USER_FINDPASS_USER_NO_EXIST").equals(errorCode)) {
            return "用户不存在";
        }
        if (("USER_FINDPASS_EMAIL_NO_AUTH").equals(errorCode)) {
            return "请先通过邮箱认证";
        }

        if (("USER_RESET_PASSWORD_NOT_BLANK").equals(errorCode)) {
            return "密码不能为空";
        }
        if (("USER_RESET_REPASSWORD_NOT_BLANK").equals(errorCode)) {
            return "确认密码不能为空";
        }
        if (("USER_RESET_PASSWORD_NOT_EQUAL").equals(errorCode)) {
            return "两次密码不一致";
        }

        if (("USER_RESET_PASSWORD_LENGTH_ERROR").equals(errorCode)) {
            return "密码长度只能在8-20字符之间";
        }

        if (("REQUEST_TIMEOUT").equals(errorCode)) {
            return "请求超时";
        }

        if (("TOKEN_REFRESH_ERROR").equals(errorCode)) {
            return "重新授权失败，请选择重新登录";
        }

        if (("SYSTEM_EXCEPTION").equals(errorCode)) {
            return "系统异常";
        }
        if (("USER_FINDPASS_VALIDCODE_WRONGOREXPIRE").equals(errorCode)) {
            return "验证码不正确或已经过期";
        }
        if (("USER_FINDPASS_USER_NOT_EXIST").equals(errorCode)) {
            return "用户不存在";
        }
        if (("USER_FINDPASS_EMAIL_NOT_AUTH").equals(errorCode)) {
            return "请先通过邮箱认证";
        }

        if (("USER_FINDPASS_PHONE_NOT_AUTH").equals(errorCode)) {
            return "请先通过手机认证";
        }
        if (("USER_FINDPASS_USERNAME_TYPE_WRONG").equals(errorCode)) {
            return "登录名类型错误";
        }
        if (("USER_FINDPASS_VALIDCODE_NOT_BLANK").equals(errorCode)) {
            return "验证码不能为空";
        }
        if (("USER_FINDPASS_VALIDCODE_WRONG").equals(errorCode)) {
            return "验证码不正确";
        }

        if (("USER_FINDPASS_QUESTION_INVALID").equals(errorCode)) {
            return "密保问题已失效，请联系管理员";
        }
        if (("USER_FINDPASS_ANSWER_WRONG").equals(errorCode)) {
            return "密保答案不正确";
        }

        if (("USER_FINDPASS_EMAIL_BLANK").equals(errorCode)) {
            return "邮箱不能为空";
        }
        if (("USER_FINDPASS_USER_NO_EXIST").equals(errorCode)) {
            return "用户不存在";
        }
        if (("USER_FINDPASS_EMAIL_NO_AUTH").equals(errorCode)) {
            return "用户不存在";
        }
        if (("USER_RESET_PASSWORD_LENGTH_ERROR").equals(errorCode)) {
            return "请先通过邮箱认证-20字符之间";
        }

        if (("USER_LOGIN_INFO_ERROR").equals(errorCode)) {
            return "登录信息错误";
        }
        if (("USER_LOGIN_USERNAME_NOT_EXIST").equals(errorCode)) {
            return "用户名不存在";
        }
        if (("USER_LOGIN_USERNAME_LOCKED").equals(errorCode)) {
            return "用户已被锁定";
        }
        if (("USER_LOGIN_WRONG_MORETHAN_5").equals(errorCode)) {
            return "登录错误超过5次";
        }
        if (("USER_LOGIN_USERNAMEPASSWORD_WRONG").equals(errorCode)) {
            return "账号或密码错误";
        }
        if (("USER_LOGIN_EMAIL_NOT_ACTIVATE").equals(errorCode)) {
            return "邮箱未激活";
        }
        if (("USER_LOGIN_PHONE_NOT_ACTIVATE").equals(errorCode)) {
            return "手机未激活";
        }
        if (("USER_REGISTER_TYPE_ERROR").equals(errorCode)) {
            return "注册方式有误，请使用手机或邮箱！";
        }
        if (("USER_REGISTER_MSG_VERIFICATION_CODE_ERROR").equals(errorCode)) {
            return "短信验证码不正确或已过期";
        }
        if (("USER_REGISTER_INVALID_INFO").equals(errorCode)) {
            return "请您填写有效的注册信息";
        }
        if (("USER_REGISTER_TYPE_NOT_OPEN").equals(errorCode)) {
            return "注册类型还未开通";
        }

        if (("USER_REGISTER_USERNAME_FORMAT_ERROR").equals(errorCode)) {
            return "用户名格式不正确";
        }
        if (("USER_REGISTER_USERNAME_LENGTH_ERROR").equals(errorCode)) {
            return "用户名长度只能在6-12字符之间";
        }
        if (("USER_REGISTER_USERNAME_EXIST").equals(errorCode)) {
            return "用户名已被使用";
        }
        if (("USER_REGISTER_PASSWORD_LENGTH_ERROR").equals(errorCode)) {
            return "密码长度只能在8-20字符之间";
        }
        if (("USER_REGISTER_PHONE_BLANK").equals(errorCode)) {
            return "手机不能为空";
        }
        if (("USER_REGISTER_PHONE_EXIST").equals(errorCode)) {
            return "手机号码已被使用";
        }
        if (("USER_REGISTER_PHONE_YJF_EXIST").equals(errorCode)) {
            return "手机号码已被其它易极付账号占用";
        }
        if (("USER_REGISTER_EMAIL_EXIST").equals(errorCode)) {
            return "邮箱已被使用";
        }
        if (("USER_REGISTER_PHONE_EXIST").equals(errorCode)) {
            return "手机号码已被使用";
        }
        if (("USER_REGISTER_EMAIL_YJF_EXIST").equals(errorCode)) {
            return "邮箱已被其它易极付账号占用";
        }
        if (("USER_IDENTIFY_PHONE_FORMAT_WRONG").equals(errorCode)) {
            return "手机格式不正确";
        }
        if (("USER_IDENTIFY_PHONE_BLANK").equals(errorCode)) {
            return "手机号码不能为空";
        }
        if (("USER_IDENTIFY_VALIDCODE_BLANK").equals(errorCode)) {
            return "验证码不能为空";
        }
        if (("USER_IDENTIFY_PHONE_AUTH").equals(errorCode)) {
            return "手机认证已通过！请勿重复提交！";
        }
        if (("USER_IDENTIFY_PHONE_EXIST").equals(errorCode)) {
            return "该手机号已被人使用";
        }
        if (("USER_IDENTIFY_PHONECODE_WRONG").equals(errorCode)) {
            return "手机验证码不正确";
        }

        if (("USER_IDENTIFY_PASSWORD_WRONG").equals(errorCode)) {
            return "登录密码不正确";
        }
        if (("USER_IDENTIFY_EMAIL_BLANK").equals(errorCode)) {
            return "邮箱不能为空";
        }
        if (("USER_IDENTIFY_EMAIL_FORMAT_WRONG").equals(errorCode)) {
            return "邮箱格式不正确";
        }
        if (("USER_IDENTIFY_EMAIL_AUTH").equals(errorCode)) {
            return "邮箱认证已通过！请勿重复提交";
        }
        if (("USER_IDENTIFY_EMAIL_EXIST").equals(errorCode)) {
            return "该邮箱已被人使用，请重新输入邮箱";
        }
        if (("USER_IDENTIFY_NOT_LOGIN").equals(errorCode)) {
            return "该您尚未登录，请登录";
        }
        if (("USER_IDENTIFY_YJF_BINDING").equals(errorCode)) {
            return "您的易极付账户已绑定，不能重复绑定";
        }
        if (("USER_IDENTIFY_PHONE_AUTH_FIRST").equals(errorCode)) {
            return "您需要先进行手机认证";
        }

        if (("USER_IDENTIFY_EMAIL_AUTH_FIRST").equals(errorCode)) {
            return "您需要先进行邮箱认证";
        }
        if (("USER_IDENTIFY_CARDID_EXIST").equals(errorCode)) {
            return "您此实名信息已存在";
        }
        if (("USER_IDENTIFY_YJF_AUTH_EXIST").equals(errorCode)) {
            return "您在易极付已通过认证，请到PC端关联实名";
        }
        if (("USER_IDENTIFY_AUTH_FAILURE").equals(errorCode)) {
            return "实名认证失败，请稍后再试";
        }
        if (("USER_PASSWORD_INFO_BLANK").equals(errorCode)) {
            return "相关信息为空！请输入完整信息！";
        }
        if (("USER_PASSWORD_LENGTH_WRONG").equals(errorCode)) {
            return "密码长度在8到20之间！";
        }
        if (("USER_PASSWORD_NOT_EQUAL").equals(errorCode)) {
            return "两次密码不一致！请重新输入！";
        }
        if (("USER_PASSWORD_OLD_WRONG").equals(errorCode)) {
            return "修改失败，原密码错误！";
        }

        if (("TRADE_PRODUCT_DETAIL_ERROR").equals(errorCode)) {
            return "读取产品详细信息出错";
        }
        if (("TRADE_TENDER_ACCOUNT_LOCKED").equals(errorCode)) {
            return "您账号已经被锁定，不能进行投标，请跟管理员联系";
        }
        if (("TRADE_TENDER_SET_PAYPASSWORD").equals(errorCode)) {
            return "请先设置交易密码";
        }
        if (("TRADE_TENDER_PAYPASSWORD_BLANK").equals(errorCode)) {
            return "交易密码不能为空";
        }
        if (("TRADE_TENDER_PAYPASSWORD_WRONG").equals(errorCode)) {
            return "交易密码不正确";
        }
        if (("PAYPASSWORD_WRONG").equals(errorCode)) {
            return "交易密码错误";
        }
        if (("TRADE_TENDER_SYSTEM_BUSY").equals(errorCode)) {
            return "系统繁忙，投标失败,请稍后再试！";
        }
        if (("TRADE_TENDER_NOT_IN_INVEST").equals(errorCode)) {
            return "产品抢光，钱已进入您余额";
        }
        if (("TRADE_TENDER_PWD_BLANK").equals(errorCode)) {
            return "定向密码不能为空";
        }
        if (("TRADE_TENDER_PWD_WRONG").equals(errorCode)) {
            return "定向密码不正确";
        }
        if (("TRADE_TENDER_NOT_SUBSCRIBE_SELF_PRODUCT").equals(errorCode)) {
            return "自己不能申购自己发布的理财产品";
        }
        if (("TRADE_TENDER_FRANCHISEE_NOT_SUBSCRIBE_SELF_PRODUCT")
                .equals(errorCode)) {
            return "钱行不能申购本身代理的理财产品";
        }
        if (("TRADE_TENDER_PRODUCT_TYPE_WRONG").equals(errorCode)) {
            return "不正确的产品类型";
        }
        if (("TRADE_TENDER_SUBSCRIBE_LESS_ZERO").equals(errorCode)) {
            return "申购金额不能小于0";
        }
        if (("TRADE_TENDER_LESS_THAN_LIMLIT").equals(errorCode)) {
            return "申购金额不能小于最小限制金额额度";
        }
        if (("TRADE_TENDER_LESS_THAN_ONE").equals(errorCode)) {
            return "申购份数不能少于1份";
        }
        if (("TRADE_TENDER_MORETHAN_LIMIT").equals(errorCode)) {
            return "该理财产品已售完或您个人申购金额已达到最大申购额";
        }
        if (("TRADE_TENDER_MORETHAN_USEMONEY").equals(errorCode)) {
            return "申购金额大于您的可用金额";
        }
        if (("TRADE_TENDER_SEARCH_ERROR").equals(errorCode)) {
            return "查询处理信息错误";
        }
        if (("TRADE_PRODUCT_DETAIL_ERROR").equals(errorCode)) {
            return "读取产品详细信息出错";
        }
        if (("TRADE_PRODUCT_DETAIL_ERROR").equals(errorCode)) {
            return "读取产品详细信息出错";
        }

        if (("TRADE_TENDER_REDPACKET_NOT_YOURS").equals(errorCode)) {
            return "此红包不属于您";
        }
        if (("TRADE_TENDER_REDPACKET_EXCHANGED").equals(errorCode)) {
            return "此红包已兑换";
        }
        if (("TRADE_TENDER_REDPACKET_OVERDUE").equals(errorCode)) {
            return "此红包已过期";
        }
        if (TextUtils.equals("BM_IN_FAILURE", errorCode)) {
            return "钱贝勒转入失败！";
        }
        if (TextUtils.equals("BM_OUT_FAILURE", errorCode)) {
            return "钱贝勒转出失败！";
        }
        if (TextUtils.equals("BM_OUT_MONEY_INVALID", errorCode)) {
            return "转出金额不能小于0！";
        }
        if (TextUtils.equals("ONLY_ONE_BANK_CARD", errorCode)) {
            return "只能设定一张银行卡";
        }
        if (TextUtils.equals("TRADE_PAYPASSWORD_LENGTH_WRONG", errorCode)) {
            return "交易密码长度不正确";
        }
        if (TextUtils.equals("TOKEN_NOT_EXIST", errorCode)) {
            return "账号过期,请重新登录";
        }
        if (TextUtils.equals("PAYPWD_NOT_EQUALS_LOGINPWD", errorCode)) {
            return "交易密码不能和登录密码相同";
        }
        if (TextUtils.equals("CARD_ID_HAS_USED", errorCode)) {
            return "身份证已被使用！";
        }
        if (TextUtils.equals("REALNAME_TIMES_OVER_LIMIT", errorCode)) {
            return "您的实名次数已达到上限，请联系客服进行实名！";
        }
        if (TextUtils.equals("PAYPWD_NOT_EQUALS_LOGINPWD", errorCode)) {
            return "交易密码不能和登录密码相同";
        }
        if (TextUtils.equals("USER_PASSWORD_NOT_EQUAL_PAYPASSWORD", errorCode)) {
            return "交易密码不能和登录密码相同";
        }
        return "服务器异常";
    }

    public static void checkMsg(Map<String, String> map, Activity activity) {
        if (null != map.get("errorCode")) {
            if (map.get("errorCode").equals("TRADE_TENDER_MORETHAN_REMAIN")) {
                MineShow.toastShow(checkReturn(map.get("errorCode")), activity);
                activity.finish();
            } else if (map.get("errorCode").equals("TOKEN_NOT_EXIST")) {
                activity.startActivity(new Intent(activity, Login.class));
            } else if (map.get("errorCode").equals("TOKEN_EXPIRED")) {
                activity.startActivity(new Intent(activity, Login.class));
            } else {
                if (checkReturn(map.get("errorCode")).equals("服务器异常")) {
                    MineShow.toastShow(map.get("resultMsg"), activity);
                } else {
                    MineShow.toastShow(Check.checkReturn(map.get("errorCode")), activity);
                }
            }

        } else {
            if (null != map.get("resultMsg")) {
                MineShow.toastShow(map.get("resultMsg"), activity);
            }

        }

    }


    public static boolean checkEmail(String paramString) {
        return Pattern.compile("^[\\w-]+(\\.[\\w-]+)*@[\\w-]+(\\.[\\w-]+)+$")
                .matcher(paramString).matches();
    }

    public static boolean chekPhone(String paramString) {
        return Pattern
                .compile(
                        "((\\d{11})|^((\\d{7,8})|(\\d{4}|\\d{3})-(\\d{7,8})|(\\d{4}|\\d{3})-(\\d{7,8})-(\\d{4}|\\d{3}|\\d{2}|\\d{1})|(\\d{7,8})-(\\d{4}|\\d{3}|\\d{2}|\\d{1}))$)")
                .matcher(paramString).matches();
    }

    public static boolean checkMoney(String paramString) {
        return Pattern.compile("-?[0-9]*$?").matcher(paramString).matches();
    }

    public static boolean checkInteger(String paramString) {
        try {
            Integer.valueOf(paramString);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean checkPoint(String paramString) {
        if (paramString.indexOf(".") > 0) {
            if (paramString.substring(paramString.indexOf(".")).length() > 3) {
                return false;
            }
        }
        return true;
    }

    public static boolean isSdPresent() {
        return Environment.getExternalStorageState().equals("mounted");
    }

    /**
     * 判断网络连接状态,true为可用,flase为当前没有网络
     * @param paramActivity
     * @return
     */
    public static boolean hasInternet(Activity paramActivity) {
        /* 判断网络链接状态 */
        ConnectivityManager localConnectivityManager = (ConnectivityManager) MyApplication.getInstance().getSystemService("connectivity");
        boolean ret = false;
        if (localConnectivityManager == null) {
            return ret;
        }
        NetworkInfo[] arrayOfNetworkInfo = localConnectivityManager.getAllNetworkInfo();
        if (arrayOfNetworkInfo != null)
            ;
        for (int j = 0; j < arrayOfNetworkInfo.length; j++) {
            if (arrayOfNetworkInfo[j].getState() == NetworkInfo.State.CONNECTED) {
                ret = true;
                break;
            }
        }
        return ret;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo[] allNetworkInfo = connectivityManager.getAllNetworkInfo();
            if (allNetworkInfo != null) {
                for (NetworkInfo networkInfo : allNetworkInfo) {
                    if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static String bankCardList(JSONObject json, String param) {
        String vaule = null;
        try {
            vaule = json.getString(param);
        } catch (JSONException e) {
            Log.d("jsonGetStringAnalysis", "参数" + param + "不存在");
        }
        return vaule;
    }

    /**
     * 返回json对象里面的param字段
     * @param json
     * @param param
     * @return
     */
    public static String jsonGetStringAnalysis(JSONObject json, String param) {
        String vaule = null;
        try {
            vaule = json.getString(param);
        } catch (Exception e) {
            Log.d("jsonGetStringAnalysis", "参数" + param + "不存在");
        }
        return vaule;
    }

    public static int jsonGetintAnalysis(JSONObject json, String param) {
        int vaule = 0;
        try {
            vaule = json.getInt(param);
        } catch (Exception e) {
            Log.d("jsonGetStringAnalysis", "参数" + param + "不存在");
        }
        return vaule;
    }

    public static JSONArray jsonGetJSONArrayAnalysis(JSONObject json, String param) {
        JSONArray vaule = null;
        try {
            vaule = json.getJSONArray(param);
        } catch (JSONException e) {
            Log.d("jsonGetJSONArrayAnalysis", "参数" + param + "不存在");
        }
        return vaule;
    }

    public static float StringToFloat(String value) {
        float result = 0;
        try {
            result = Float.valueOf(value);
        } catch (Exception e) {
            result = 0;
        }
        return result;
    }

}
