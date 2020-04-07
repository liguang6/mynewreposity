package com.byd.web.wms.in.service;

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
public interface StoSendCreateRemote {

	@RequestMapping(value = "/wms-service/sto/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R list(@RequestParam(value = "params") Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/sto/create", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R create(@RequestParam(value = "params") Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/sto/querySto", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R querySto(@RequestParam(value = "params") Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/sto/queryCustomer", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R queryCustomer(@RequestParam(value = "params")  Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/sto/queryWMSNo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R queryWMSNo(@RequestParam(value = "params")  Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/sto/queryContact", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R queryContact(@RequestParam(value = "params")  Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/sto/queryLiktx", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R queryLiktx(@RequestParam(value = "params") Map<String, Object>  params);
	
	@RequestMapping(value = "/wms-service/sto/checkAddr", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R checkAddr(@RequestParam(value = "params") Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/sto/checkExist", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R checkExist(@RequestParam(value = "params") Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/sto/queryBydeliveryNo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R queryBydeliveryNo(@RequestBody List<Map<String, Object>> params);
	
}
