package com.byd.web.wms.common.service;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.byd.utils.R;

@FeignClient(name = "WMS-SERVICE")
public interface WmsCDocNoRemote {
	
	@RequestMapping(value = "/wms-service/common/docNo/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R list(@RequestParam(value = "params") Map<String, Object> params);
    
	@RequestMapping(value = "/wms-service/common/docNo/listEntity", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R queryEntity(@RequestParam(value = "params") Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/common/docNo/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R add(@RequestParam(value = "params") Map<String, Object> params);
	
    @RequestMapping(value = "/wms-service/common/docNo/info/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R info(@PathVariable("id") Long id);
    
    @RequestMapping(value = "/wms-service/common/docNo/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R update(@RequestParam(value = "params") Map<String, Object> params);
    
    @RequestMapping(value = "/wms-service/common/docNo/del", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R delete(@RequestParam(value = "id") Long id);
    
	@RequestMapping(value = "/wms-service/common/docNo/dels", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R deletes(@RequestParam(value = "ids") String ids);
	
	@RequestMapping(value = "/wms-service/common/docNo/getdocno", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R getdocno(@RequestParam(value = "params") Map<String, Object> params);
}
