package com.byd.utils.serviceauth.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import com.byd.utils.serviceauth.filter.SecurityAuthFilter;
import com.byd.utils.serviceauth.interceptor.FeignSecurityAuthRequestInterceptor;

public class EnableUserInfoTransmitterAutoConfiguration {
	private Logger logger = LoggerFactory.getLogger(getClass());
	public EnableUserInfoTransmitterAutoConfiguration() {
    }

    @Bean
    public FeignSecurityAuthRequestInterceptor transmitUserInfo2FeighHttpHeader(){
    	logger.info("初始化请求拦截器-----FeignSecurityAuthRequestInterceptor");
        return new FeignSecurityAuthRequestInterceptor();
    }

    @Bean
    public SecurityAuthFilter transmitUserInfoFromHttpHeader(){
    	logger.info("初始化服务调用过滤器-----SecurityAuthFilter");
        return new SecurityAuthFilter();
    }
}
