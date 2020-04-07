package com.byd.web.wms.in.service;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.byd.utils.R;

/**
 * 
 * @author ren.wei3
 *
 */
@FeignClient(name = "WMS-SERVICE")
public interface WmsInAutoPutawayRemote {

	@RequestMapping(value = "/wms-service/in/autoPutaway/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R list(@RequestParam(value = "params") Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/in/autoPutaway/createPO", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R createPO(@RequestParam(value = "params") Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/in/autoPutaway/createDN", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R createDN(@RequestParam(value = "params") Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/in/autoPutaway/postDN", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R postDN(@RequestParam(value = "params") Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/in/autoPutaway/loglist", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R loglist(@RequestParam(value = "params") Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/in/autoPutaway/autoPutawayProcess", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R autoPutawayProcess(@RequestParam(value = "params") Map<String, Object> params);
}
