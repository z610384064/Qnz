package com.rd.qnz.entity;

import android.text.TextUtils;

import com.rd.qnz.tools.Utils;

public class WebViewToAppBean {
	public static String ACTION_TO_SHARE = "getShare";//打开分享页面
	public static String ACTION_TO_NEW_WEBVIEW = "getUrl";//打开一个新的wap内嵌网页
	public static String ACTION_TO_PROJECTDETAIL = "projectDetail.html";//申购结果返回
	public static String ACTION_TO_MY_BANK = "myBank.html";//我的银行卡
	public static String ACTION_TO_GET_BANK = "getBank";//返回
	public static String ACTION_TO_HISTORY = "history";//回到上一页  即关闭当前页
	public static String ACTION_TO_OPENNATIVEPAGE = "openNativePage";//跳转到原生界面
	private boolean isQianToAppView;//是否是 跳转到 原生响应
	private String aciton;
	private String url;
	private String title;
	private String link;
	private String desc;
	private String imageUrl;
 	private String type; //跳原生界面
	
	
	public boolean isQianToAppView() {
		return isQianToAppView;
	}
	public void setQianToAppView(boolean isQianToAppView) {
		this.isQianToAppView = isQianToAppView;
	}
	public String getAciton() {
		return aciton;
	}
	public void setAciton(String aciton) {
		this.aciton = aciton;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	
	/**
	 * 解析 本地处理的weburl
	 * @param webUrl  qian://getUrl?url=reward.html&title=123&abc=567
	 *                http://testqnz.qianneizhu.com/activity.html?returnUrl=20170228m
	 * @return
	 */
	public static WebViewToAppBean decordUrlToBean(String webUrl){  //    webUrl=qian://history?go=-2
		WebViewToAppBean bean = new WebViewToAppBean();	
		if(webUrl.contains("qian://")){
			bean.isQianToAppView = true;
		}else{
			bean.isQianToAppView = false;  //不跳到原生响应
			return bean;
		}
		int indexFirst = webUrl.indexOf("//");
		int indexSecond = webUrl.indexOf("?");	
		bean.aciton = webUrl.substring(indexFirst+2, indexSecond);  //   取//和?之间的数
		String subStringResult = "";
		subStringResult = webUrl.substring(indexSecond+1);
		String[] strArray = subStringResult.split("&");
		for(int i = 0;i< strArray.length;i++){
			String oneItemStr = strArray[i];
			int index = oneItemStr.indexOf("=");
			if(index < 0){
				return bean;
			}
			String key = oneItemStr.substring(0, index);
			String value = oneItemStr.substring(index+1);
			value = Utils.getURLDecoder(value);			
			if(TextUtils.equals(key, "url")){
				bean.url = value;
			}else if(TextUtils.equals(key, "title")){
				bean.title = value;
			}else if(TextUtils.equals(key, "link")){
				bean.link = value;
			}else if(TextUtils.equals(key, "desc")){
				bean.desc = value;
			}else if(TextUtils.equals(key, "type")){
				bean.type = value;
			}else if(TextUtils.equals(key, "imgUrl")){
				bean.imageUrl = value;
			}
		}		
//		Log.e("text url = ", "解析结果 = "+ bean.aciton+"---"+ bean.url+"---"+ bean.title+"---"+bean.desc+"---"+bean.link+"---"+bean.imageUrl);
		return bean;
	}

}
