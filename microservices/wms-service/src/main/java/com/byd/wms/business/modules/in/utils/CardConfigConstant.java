package com.byd.wms.business.modules.in.utils;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/** 
 * @author 作者 E-mail: peng.tao1@byd.com
 * @version 创建时间：2018年8月24日 下午1:44:14 
 * 类说明 
 */
@Component
@ConfigurationProperties(prefix = "card")
public class CardConfigConstant {
	private String cardurl;
	private String cardusername;
	private String cardpassword;
	public String getCardurl() {
		return cardurl;
	}
	public void setCardurl(String cardurl) {
		this.cardurl = cardurl;
	}
	public String getCardusername() {
		return cardusername;
	}
	public void setCardusername(String cardusername) {
		this.cardusername = cardusername;
	}
	public String getCardpassword() {
		return cardpassword;
	}
	public void setCardpassword(String cardpassword) {
		this.cardpassword = cardpassword;
	}
	
	
}
