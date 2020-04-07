package com.byd.wms.business.modules.common.service;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "SAP-SERVICE")
public interface HelloRemote {
	@RequestMapping(value = "/sap-service/hello/test", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    String hello(@RequestParam Map<String, Object> map);
	
	@RequestMapping(value = "/sap-service/SapJob01", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	Map<String,Object> getSapBapiGoodsmvtDetail(@RequestParam(value = "order_id") String order_id);
} 