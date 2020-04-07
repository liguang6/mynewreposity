package com.byd.web.wms.out.service;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.byd.utils.R;


@FeignClient(name = "WMS-SERVICE")
public interface DispatchingDeliveryServiceRemote {


	@RequestMapping(value = "/wms-service/out/delivery/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R list(@RequestParam(value = "params") Map<String, Object> params);
	
	

	@RequestMapping(value = "/wms-service/out/delivery/delivery", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R delivery(@RequestBody List<Map<String, Object>> params);
	
}
