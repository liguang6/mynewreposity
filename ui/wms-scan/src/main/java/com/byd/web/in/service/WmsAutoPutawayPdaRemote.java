package com.byd.web.in.service;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.byd.utils.R;

/**
 * PDA一步联动收货
 * @author ren.wei3
 *
 */

@FeignClient(name = "WMS-SERVICE")
public interface WmsAutoPutawayPdaRemote {

	@RequestMapping(value = "/wms-service/in/autoPutaway/scan", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R scan(@RequestBody Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/in/autoPutaway/docItem", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R docItem(@RequestBody Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/in/autoPutaway/labelItem", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R labelItem(@RequestBody Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/in/autoPutaway/deleteLabel", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R deleteLabel(@RequestBody Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/in/autoPutaway/confirm", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R confirm(@RequestBody Map<String, Object> params);
}
