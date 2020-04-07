package com.byd.web.wms.config.service;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.byd.utils.R;
import com.byd.web.wms.config.entity.WmsCAssemblySortTypeEntity;

/** 
 * @author 作者 E-mail: 
 * @version 创建时间：2019年8月2日 下午4:30:01 
 * 类说明 
 */
@FeignClient(name = "WMS-SERVICE")
public interface WmsCAssemblySortTypeRemote {

	/**
     * 分页查询
     */
	@RequestMapping(value = "/wms-service/config/assemblySortType/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R list(@RequestParam(value = "params") Map<String, Object> params);
	
	/**
     * 软删BY ID
     */
	@RequestMapping(value = "/wms-service/config/assemblySortType/delById", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R delById(@RequestBody WmsCAssemblySortTypeEntity wmsCAssemblySortType);
	
	/**
     * 单条记录查询By ID
     */
	@RequestMapping(value = "/wms-service/config/assemblySortType/info/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R info(@PathVariable("id") Long id);
	
	@RequestMapping(value = "/wms-service/config/assemblySortType/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R save(@RequestBody WmsCAssemblySortTypeEntity wmsCAssemblySortType);

	@RequestMapping(value = "/wms-service/config/assemblySortType/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R update(@RequestBody WmsCAssemblySortTypeEntity wmsCAssemblySortType);
	
}
