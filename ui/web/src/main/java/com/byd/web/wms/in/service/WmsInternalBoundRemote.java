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
 * @version 创建时间：2019年1月17日 下午3:37:41 
 * 类说明 
 */
@FeignClient(name = "WMS-SERVICE")
public interface WmsInternalBoundRemote {

	@RequestMapping(value = "/wms-service/in/wmsinternalbound/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R list(@RequestParam(value = "params") Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/in/wmsinternalbound/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R saveInbound(@RequestParam(value = "params") Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/in/wmsinternalbound/querymaterial", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R querymaterial(@RequestParam(value = "params") Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/in/wmsinternalbound/queryfullbox", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R queryfullbox(@RequestParam(value = "params") Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/in/wmsinternalbound/querybincode", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R querybincode(@RequestParam(value = "params") Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/in/wmsinternalbound/querywhmanager", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R querywhmanager(@RequestParam(value = "params") Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/in/wmsinternalbound/save501", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R saveInbound501(@RequestBody Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/in/wmsinternalbound/save903", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R saveInbound903(@RequestParam(value = "params") Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/in/wmsinternalbound/save511", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R saveInbound511(@RequestBody Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/in/wmsinternalbound/preview511", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R previewExcel(@RequestParam(value = "params") Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/in/wmsinternalbound/listMo101", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R listMo101(@RequestBody Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/in/wmsinternalbound/listMo531", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R listMo531(@RequestBody Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/in/wmsinternalbound/save521", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R saveInbound521(@RequestParam(value = "params") Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/in/wmsinternalbound/save202", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R saveInbound202(@RequestParam(value = "params") Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/in/wmsinternalbound/save222", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R saveInbound222(@RequestParam(value = "params") Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/in/wmsinternalbound/save262", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R saveInbound262(@RequestParam(value = "params") Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/in/wmsinternalbound/saveCo101", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R saveInboundCo101(@RequestParam(value = "params") Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/in/wmsinternalbound/listCo101", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R listCo101(@RequestParam(value = "params") Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/in/wmsinternalbound/listCostCenter202", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R listCostCenter202(@RequestParam(value = "params") Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/in/wmsinternalbound/listWBS222", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R listWBS222(@RequestParam(value = "params") Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/in/wmsinternalbound/listyf262", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R listyf262(@RequestParam(value = "params") Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/in/wmsinternalbound/listA101", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R listA101(@RequestParam(value = "params") Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/in/wmsinternalbound/listA531", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R listA531(@RequestParam(value = "params") Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/in/wmsinternalbound/listpo101", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R listpo101(@RequestBody Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/in/wmsinternalbound/listMo262", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R listMo262(@RequestBody Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/in/wmsinternalbound/saveMo262", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R saveInboundMo262(@RequestParam(value = "params") Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/in/wmsinternalbound/save262import", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R saveInbound262import(@RequestParam(value = "params") Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/in/wmsinternalbound/saveMo262import", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R saveInboundMo262import(@RequestParam(value = "params") Map<String, Object> params);
	
	
}
