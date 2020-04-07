package com.byd.web.wms.in.service;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.byd.utils.R;

/** 
 * @author 作者 E-mail: 
 * @version 创建时间：2019年1月18日 下午2:32:32 
 * 类说明 
 */
@FeignClient(name = "WMS-SERVICE")
public interface WmsInHandoverRemote {
	@RequestMapping(value = "/wms-service/in/wmsinhandoverbound/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R list(@RequestBody Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/in/wmsinhandoverbound/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R handover(@RequestBody Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/in/wmsinhandoverbound/labellist", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R labelList(@RequestBody Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/in/wmsinhandoverbound/ValidlabelQyt", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R ValidlabelQyt(@RequestBody Map<String, Object> params); 
	
	@RequestMapping(value = "/wms-service/in/wmsinhandoverbound/inPoCptConsumelist", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R inPoCptConsumelist(@RequestBody Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/in/wmsinhandoverbound/ValidlabelHandover", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R ValidlabelHandover(@RequestBody Map<String, Object> params);
}
