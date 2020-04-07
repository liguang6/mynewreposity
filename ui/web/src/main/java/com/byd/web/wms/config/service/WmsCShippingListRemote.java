package com.byd.web.wms.config.service;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.byd.utils.R;


@FeignClient(name = "WMS-SERVICE")
public interface WmsCShippingListRemote {
	
	/**
     * 分页查询
     */
	@RequestMapping(value = "/wms-service/config/shipping/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R list(@RequestParam Map<String, Object> params) ;
    /**
     * 单条记录查询By ID
     */
	@RequestMapping(value = "/wms-service/config/shipping/info/{id}/{itemNo}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R info(@PathVariable("id") String id,@PathVariable("itemNo") String itemNo) ;
	/**
	 * 
	 * 保存
	 */
	@RequestMapping(value = "/wms-service/config/shipping/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R save(@RequestParam Map<String, Object> params) ;
	
	/**
	 * 
	 * 修改
	 */
	@RequestMapping(value = "/wms-service/config/shipping/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R update(@RequestParam Map<String, Object> params) ;
    
}
