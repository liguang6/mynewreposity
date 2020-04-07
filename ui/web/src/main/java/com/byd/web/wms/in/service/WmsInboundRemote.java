package com.byd.web.wms.in.service;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.byd.utils.R;

/** 
 * @author 作者 E-mail: 
 * @version 创建时间：2019年1月17日 下午1:56:49 
 * 类说明 
 */
@FeignClient(name = "WMS-SERVICE")
public interface WmsInboundRemote {

	@RequestMapping(value = "/wms-service/in/wmsinbound/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R list(@RequestParam(value = "params") Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/in/wmsinbound/getInboundTasks", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R getInboundTasks(@RequestParam(value = "params") Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/in/wmsinbound/relatedAreaNamelist", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R RelatedAreaNamelist(@RequestParam Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/in/wmsinbound/lgortlist", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R lgortlist(@RequestParam Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/in/wmsinbound/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R saveInbound(@RequestParam Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/in/wmsinbound/Receiptlist", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R Receiptlist(@RequestParam(value = "params") Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/in/wmsinbound/sapComponentlist", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R sapComponentlist(@RequestParam(value = "params") Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/in/wmsinbound/getDeptNameByWerk", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	List<Map<String,Object>> getDeptNameByWerk(@RequestParam(value = "deptMap") Map<String, Object> deptMap);
	
}
