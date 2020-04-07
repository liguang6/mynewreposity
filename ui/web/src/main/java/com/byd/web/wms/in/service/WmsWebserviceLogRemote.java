package com.byd.web.wms.in.service;

import com.byd.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * @author rain
 * @version 创建时间：2019年11月27日16:24:29
 * @description 接口日志remote
 */
@FeignClient(name = "WMS-SERVICE")
public interface WmsWebserviceLogRemote {
	
    @RequestMapping(value = "/wms-service/in/webservicelog/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R list(@RequestParam(value = "params") Map<String, Object> params);

	@RequestMapping(value = "/wms-service/in/webservicelog/retrigger", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R retrigger(@RequestParam(value="pkLog") Long pkLog) ;

    @RequestMapping(value = "/wms-service/in/webservicelog/retriggerLogs", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R retriggerLogs(@RequestParam(value = "params") Map<String, Object> params);
}
