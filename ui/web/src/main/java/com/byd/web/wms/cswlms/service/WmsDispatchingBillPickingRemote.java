package com.byd.web.wms.cswlms.service;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.byd.utils.R;

/** 
 * @author 作者 E-mail: 
 * @version 创建时间：2019年1月19日 上午10:26:19 
 * 类说明 
 */
@FeignClient(name = "WMS-SERVICE")
public interface WmsDispatchingBillPickingRemote {
	@RequestMapping(value = "/wms-service/cswlms/dispatchingBillPicking/listJIS", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R listJIS(@RequestParam(value = "params") Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/cswlms/dispatchingBillPicking/listJISDetail", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R listJISDetail(@RequestParam(value = "params") Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/cswlms/dispatchingBillPicking/listAssembly", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R listAssembly(@RequestParam(value = "params") Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/cswlms/dispatchingBillPicking/picking", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R picking(@RequestParam(value = "params") Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/cswlms/dispatchingBillPicking/listFeiJIS", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R listFeiJIS(@RequestParam(value = "params") Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/cswlms/dispatchingBillPicking/feijispicking", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R feijispicking(@RequestParam(value = "params") Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/cswlms/dispatchingBillPicking/printjis", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R printjis(@RequestParam(value = "params") Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/cswlms/dispatchingBillPicking/printfeijis", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R printfeijis(@RequestParam(value = "params") Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/cswlms/dispatchingBillPicking/checkJISDetailStatus", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R checkJISDetailStatus(@RequestParam(value = "params") Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/cswlms/dispatchingBillPicking/listQueRen", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R listQueRen(@RequestParam(value = "params") Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/cswlms/dispatchingBillPicking/checkQueRen", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R checkQueRen(@RequestParam(value = "params") Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/cswlms/dispatchingBillPicking/updateQueRen", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R updateQueRen(@RequestParam(value = "params") Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/cswlms/dispatchingBillPicking/listjiaojie", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R listjiaojie(@RequestParam(value = "params") Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/cswlms/dispatchingBillPicking/dispatchingHandover", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R dispatchingHandover(@RequestParam(value = "params") Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/cswlms/dispatchingBillPicking/listfabu", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R listfabu(@RequestParam(value = "params") Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/cswlms/dispatchingBillPicking/updatefabu", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R updatefabu(@RequestParam(value = "params") Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/cswlms/dispatchingBillPicking/dispatchingchaif", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R dispatchingchaif(@RequestParam(value = "params") Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/cswlms/dispatchingBillPicking/listwmlsException", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R listwmlsException(@RequestParam(value = "params") Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/cswlms/dispatchingBillPicking/listPickRecordNoCount", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R listPickRecordNoCount(@RequestParam(value = "params") Map<String, Object> params);
}
