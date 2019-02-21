package com.yintong.secure.demo.env;

public class EnvConstants {
	private EnvConstants() {
	}
	/**
     * TODO 商户号，商户MD5 key 配置。本测试Demo里的“PARTNER”；强烈建议将私钥配置到服务器上，以免泄露。“MD5_KEY”字段均为测试字段。正式接入需要填写商户自己的字段
     */

    public static final String PARTNER_PREAUTH = "201504071000272504"; // 短信

    public static final String MD5_KEY_PREAUTH = "201504071000272504_test_20150417";
    public static final String PARTNER = "000000000"; 
    //TODO 修改MD5KEY
    public static final String MD5_KEY = "00000000000000000000000000000";
  
    //=======================采用RSA签名的，请修"RSA_PRIVATE"字段============================//
    
    // 商户（RSA）私钥  TODO 强烈建议将私钥配置到服务器上，以免泄露。
    public static final String RSA_PRIVATE =
            "000000000000000000000000000000000000000000000000000000000000000000ATU=";
    
    // 银通支付（RSA）公钥，这个是银通的公钥，不需要修改，如果是RSA签名的，可以用来本地验签，MD5不需要关注
    public static final String RSA_YT_PUBLIC =
            "00000000000000000000000000000000000";

}
