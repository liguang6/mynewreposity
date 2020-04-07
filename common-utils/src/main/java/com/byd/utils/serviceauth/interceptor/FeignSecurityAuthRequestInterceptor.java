package com.byd.utils.serviceauth.interceptor;

import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.byd.utils.RedisKeys;
import com.byd.utils.RedisUtils;
import com.byd.utils.SpringContextUtils;
import com.byd.utils.UserUtils;

import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 * 拦截所有Feign请求，添加认证token信息(从HttpServletRequest请求里获取所有请求头信息，写入Feign请求（RequestTemplate）的Header里)
 *
 * @author develop01
 * @date 16:04 2019/01/19
 */
@Configuration
public class FeignSecurityAuthRequestInterceptor implements RequestInterceptor {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Override
	public void apply(RequestTemplate template) {
		HttpServletRequest request = getHttpServletRequest();
		if(request != null) {
			Enumeration<String> enumeration = request.getHeaderNames();
			while (enumeration.hasMoreElements()) {
				String key = enumeration.nextElement();
				String value = request.getHeader(key);
				template.header(key, value);
			}
		}

	}

	private HttpServletRequest getHttpServletRequest() {
		try {
			ServletRequestAttributes servletRequestAttributes = RequestContextHolder.getRequestAttributes()==null?null:(ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
			if(servletRequestAttributes!=null) {
				return servletRequestAttributes.getRequest();
			}else {
				logger.info("请求拦截器中获取getHttpServletRequest失败！");
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
