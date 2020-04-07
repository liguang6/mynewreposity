package com.byd.wms.business.modules.common.service;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.byd.utils.R;

@FeignClient(name = "WMS-WEBSERVICE")
public interface WmsCloudPlatformRemote {
	@RequestMapping(value = "/wms-webservice/webservice/getDeliveryDetail", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R getDeliveryDetail(@RequestBody Map<String,Object> paramMap);
	@RequestMapping(value = "/wms-webservice/webservice/getDeliveryDetailByBarcode", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	Map<String,Object> getDeliveryDetailByBarcode(@RequestBody Map<String,Object> paramMap);
	
	@RequestMapping(value = "/wms-webservice/webservice/sendDeliveryData", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R sendDeliveryData(@RequestBody Map<String,Object> paramMap);
}
