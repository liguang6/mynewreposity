package com.byd.wms.webservice.common.fallback;

import com.byd.utils.R;
import com.byd.wms.webservice.common.remote.BusinessRemote;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class BusinessRemoteFallBackFactory implements BusinessRemote{


	@Override
	public R handover(Map<String, Object> params) {
		System.err.println("WMS-BUSINESS-SERVICE服务异常！WMS-BUSINESS-SERVICE服务异常！");
		return R.error(500, "调用WMS-BUSINESS-SERVICE服务异常");
	}
	
	@Override
	public R list(Map<String, Object> params) {
		System.err.println("WMS-BUSINESS-SERVICE服务异常！WMS-BUSINESS-SERVICE服务异常！");
		return R.error(500, "调用WMS-BUSINESS-SERVICE服务异常");
	}

	@Override
	public String saveWMSDoc(Map<String, Object> params) {
		System.err.println("WMS-BUSINESS-SERVICE服务异常！WMS-BUSINESS-SERVICE服务异常！");
		return "500 ,调用WMS-BUSINESS-SERVICE服务异常";
	}

	@Override
	public String doSapPost(Map<String, Object> params) {
		System.err.println("WMS-BUSINESS-SERVICE服务异常！WMS-BUSINESS-SERVICE服务异常！");
		return  "500,调用WMS-BUSINESS-SERVICE服务异常";
	}
}
