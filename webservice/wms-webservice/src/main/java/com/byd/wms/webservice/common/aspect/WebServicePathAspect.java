/**
 * Copyright 2018 cscc
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.byd.wms.webservice.common.aspect;

import com.byd.utils.StringUtils;
import com.byd.wms.webservice.common.Constant.WebserviceInfo;
import com.byd.wms.webservice.common.Constant.WmsWebserviceConfigConstant;
import com.byd.wms.webservice.common.annotation.WebServicePath;
import com.byd.wms.webservice.common.util.WebServiceClientUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * WebServicePath切面处理类
 *
 */
@Aspect
@Component
public class WebServicePathAspect {

	@Autowired
	private WebServiceClientUtil webServiceClientUtil;
	@Autowired
	private WmsWebserviceConfigConstant wmsWebserviceConfigConstant;

	@Pointcut("@annotation(com.byd.wms.webservice.common.annotation.WebServicePath)")
	public void webServicePathPointCut() {
		
	}

	@Before("webServicePathPointCut()")
	public void Before(JoinPoint jpoint) throws Throwable {
		System.err.println("进入WebServicePathAspectWebServicePathAspectWebServicePathAspect");
		Object params = jpoint.getArgs()[0];
		Map map = null;
		if(params != null && params instanceof Map){
			map = (Map) params;
		}
		MethodSignature signature = (MethodSignature) jpoint.getSignature();
		WebServicePath permissionDataFilter= signature.getMethod().getAnnotation(WebServicePath.class);
		String path = permissionDataFilter.path();
		String methodName = permissionDataFilter.methodName();
		
		if(path.equals(WebserviceInfo.WEBCLOUD_PATH)) {
			path = wmsWebserviceConfigConstant.getWebCloudPath();
		}		
		if(methodName.equals(WebserviceInfo.WEBCLOUD_METHOD_DELIVERYDATA)) {
			methodName = wmsWebserviceConfigConstant.getWebCloudMethod().getDeliveryData();
		}
		if(methodName.equals(WebserviceInfo.WEBCLOUD_METHOD_DELIVERYDATA)) {
			methodName = wmsWebserviceConfigConstant.getWebCloudMethod().getDeliveryData();
		}
		if(methodName.equals(WebserviceInfo.WEBCLOUD_METHOD_DELIVERYDATA_BYBARCODE)) {
			methodName = wmsWebserviceConfigConstant.getWebCloudMethod().getDeliveryByBarcode();
		}
		if(methodName.equals(WebserviceInfo.WEBCLOUD_METHOD_DELIVERYDETAILDATA)) {
			methodName = wmsWebserviceConfigConstant.getWebCloudMethod().getDeliveryDetailData();
		}
		if(path.equals(WebserviceInfo.LIKU_PATH)) {
			path = wmsWebserviceConfigConstant.getLiKUPath();
		}
		if(methodName.equals(WebserviceInfo.LIKU_METHOD_OUT_INSTRUCTION)) {
			methodName = wmsWebserviceConfigConstant.getLiKuMethod().getLiKuOutInstruction();
		}
		String param ="";
		if(!StringUtils.isBlank((String) map.get("param"))){
			param = map.get("param").toString();
		}
		System.err.println("path: "+path);
		System.err.println("methodName: "+methodName);
		System.err.println("param: "+param);
		Object[] objects = webServiceClientUtil.invoke(path,
				methodName,param);
		System.err.println(objects[0].toString());
		map.put("webServiceData",objects[0]);

	}


}
