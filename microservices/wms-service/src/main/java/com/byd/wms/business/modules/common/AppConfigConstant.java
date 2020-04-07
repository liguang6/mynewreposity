package com.byd.wms.business.modules.common;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/** 
 * @author 作者 E-mail: peng.tao1@byd.com
 * @version 创建时间：2018年8月23日 下午2:56:26 
 * 类说明 
 */
@Component
@ConfigurationProperties(prefix = "sysconfig")
public class AppConfigConstant {
	private String sys;

	public String getSys() {
		return sys;
	}

	public void setSys(String sys) {
		this.sys = sys;
	}
	
}
